package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fourthline.core.DocumentFileSide
import com.fourthline.core.DocumentType
import com.fourthline.vision.document.*
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.core.viewmodel.AssistedViewModelFactory
import de.solarisbank.sdk.fourthline.*
import de.solarisbank.sdk.fourthline.data.entity.AppliedDocument
import de.solarisbank.sdk.fourthline.data.entity.toDocumentType
import de.solarisbank.sdk.fourthline.di.FourthlineFragmentComponent
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.FOURTHLINE_SCAN_FAILED
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.KEY_CODE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity.Companion.KEY_MESSAGE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.custom.PunchholeView
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class DocScanFragment : DocumentScannerFragment() {

    private val activityViewModel: FourthlineViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as BaseActivity).viewModelFactory)
                .get(FourthlineViewModel::class.java)
    }

    private val kycSharedViewModel: KycSharedViewModel by lazy<KycSharedViewModel> {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)[KycSharedViewModel::class.java]
    }

    private var documentMask: AppCompatImageView? = null
    private var takeSnapshot: View? = null
    private var scanPreview: AppCompatImageView? = null
    private var stepLabel: AppCompatTextView? = null
    private var warningsLabel: AppCompatTextView? = null
    private var resultButtons: ViewGroup? = null
    private var punchhole: PunchholeView? = null
    private var retakeButton: Button? = null
    private var confirmButton: Button? = null
    private var noticeRoot: ViewGroup? = null
    private var bottomInfoText: TextView? = null
    private var progressRoot: LinearLayout? = null
    private var resultRoot: LinearLayout? = null

    internal lateinit var assistedViewModelFactory: AssistedViewModelFactory
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var currentDocumentType: DocumentType

    private var cleanupJob: Job? = null
    private var showSnapshotJob: Job? = null

    private enum class UiState {
        SCANNING, INTERMEDIATE_RESULT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        val activityComponent = (requireActivity() as FourthlineActivity).activitySubcomponent
        inject(activityComponent.fragmentComponent().create())
        (arguments?.getSerializable(DOC_TYPE_KEY) as? AppliedDocument)?.let {
            currentDocumentType = it.toDocumentType()
        }
        super.onCreate(savedInstanceState)
        Timber.d("onCreate end")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
    }

    override fun onDestroyView() {
        cleanupJob?.cancel()
        showSnapshotJob?.cancel()
        documentMask = null
        takeSnapshot = null
        scanPreview = null
        stepLabel = null
        warningsLabel = null
        resultButtons = null
        punchhole = null
        retakeButton = null
        confirmButton = null
        noticeRoot = null
        bottomInfoText = null
        progressRoot = null
        resultRoot = null
        super.onDestroyView()
    }

    fun inject(component: FourthlineFragmentComponent) {
        component.inject(this)
    }

    private fun initViewModel() {
        viewModelFactory = assistedViewModelFactory.create(this, arguments)
    }

    override fun getConfig(): DocumentScannerConfig {
        Timber.d("getConfig()")
        return DocumentScannerConfig(currentDocumentType, true, false, true, MrzValidationPolicy.STRONG)
    }

    override fun getOverlayView(): View? {
        Timber.d("getOverlayView()")
        return LayoutInflater
                .from(requireContext())
                .inflate(R.layout.fragment_doc_scan, requireActivity().findViewById(R.id.content), false)
                .also(::initView)
    }

    private fun initView(view: View) {
        with(view) {
            documentMask = findViewById(R.id.documentMask)
            when (currentDocumentType) {
                DocumentType.PASSPORT -> documentMask!!.setImageResource(R.drawable.ic_passport_front_success_frame)
                DocumentType.ID_CARD -> documentMask!!.setImageResource(R.drawable.ic_idcard_front_success_frame)
            }
            takeSnapshot = findViewById(R.id.takeSnapshot)
            takeSnapshot?.setOnClickListener { takeSnapshot() }
            scanPreview = findViewById(R.id.scanPreview)
            stepLabel = findViewById(R.id.stepName)
            warningsLabel = findViewById(R.id.warningsLabel)
            resultButtons = findViewById(R.id.resultButtonsRoot)
            punchhole = findViewById(R.id.punchhole)
            retakeButton = findViewById(R.id.retakeButton)
            retakeButton?.setOnClickListener { resetCurrentStep() }
            confirmButton = findViewById(R.id.confirmButton)
            confirmButton?.setOnClickListener { moveToNextStep() }
            noticeRoot = findViewById(R.id.noticeRoot)
            bottomInfoText = findViewById(R.id.bottomInfoText)
            resultRoot = findViewById(R.id.resultRoot)
            progressRoot = findViewById(R.id.progressRoot)
            handleShortScreen()
            onLayoutMeasuredOnce {
                punchhole!!.punchholeRect = getDocumentDetectionArea()
                punchhole!!.postInvalidate()
            }
        }
    }

    override fun getDocumentDetectionArea(): Rect {
        Timber.d("getDocumentDetectionArea() ${documentMask}")
        return Rect(
                documentMask!!.left,
                documentMask!!.top,
                documentMask!!.right - 1,
                documentMask!!.bottom - 1
        )
    }

    override fun onStepUpdate(step: DocumentScannerStep) {
        Timber.d("onStepUpdate: ${step.prettify()}")

        activity?.runOnUiThread {
            updateUiForScanning(
                isScan = step.isScan(currentDocumentType),
                tilted = step.isAngled,
                maskDrawable = step.findMaskDrawable(requireContext()),
                stepText = step.asString(currentDocumentType, requireContext())
            )
            punchhole?.punchholeRect = getDocumentDetectionArea()
            punchhole?.postInvalidate()
        }
    }

    private fun DocumentScannerStep.findMaskDrawable(context: Context): Drawable? {
        Timber.d("findMaskDrawable, fileSide: $fileSide")
        val frameResource: Int

        when (fileSide) {
            DocumentFileSide.FRONT ->
                frameResource = when (currentDocumentType) {
                    DocumentType.ID_CARD ->
                        if (isAngled) R.drawable.ic_idcard_front_tilted_success_frame
                        else R.drawable.ic_idcard_front_success_frame
                    DocumentType.PASSPORT ->
                        if (isAngled) R.drawable.ic_passport_angled_success_frame
                        else R.drawable.ic_passport_front_success_frame
                    else -> R.drawable.ic_idcard_front_success_frame
                }

            DocumentFileSide.BACK ->
                frameResource = when (currentDocumentType) {
                    DocumentType.ID_CARD ->
                        if (isAngled) R.drawable.ic_idcard_back_tilted_success_frame
                        else R.drawable.ic_idcard_back_success_frame
                    else -> R.drawable.ic_idcard_back_success_frame
                }
            else -> throw RuntimeException("ID cards do not have document side: $fileSide")
        }


        return ContextCompat.getDrawable(context, frameResource)
    }

    override fun onWarnings(warnings: List<DocumentScannerStepWarning>) {
        Timber.d("onWarnings")
        cleanupJob?.cancel()
        cleanupJob = lifecycleScope.launch(Dispatchers.Main) {
            warningsLabel!!.text = warnings.asString(requireContext())
            delay(500)
            warningsLabel!!.text = getString(R.string.selfie_step_scanning)
        }
    }

    override fun onStepFail(error: DocumentScannerStepError) {
        Timber.d("Document scanner misuse, reason $error")
        throw RuntimeException("Business logic violation, should never happen")
    }

    override fun onStepSuccess(result: DocumentScannerStepResult) {
        Timber.d("onStepSuccess: ${step?.fileSide?.name}")
        cleanupJob?.cancel()
        if(SHOW_INTERMEDIATR_RESULTS) {
            lifecycleScope.launch(Dispatchers.Main) {
                showIntermediateResult(result.image.cropped)
                kycSharedViewModel.updateKycInfoWithDocumentScannerStepResult(currentDocumentType, result)
            }
        } else {
            moveToNextStep()
        }
    }

    override fun onFail(error: DocumentScannerError) {
        Timber.d("onFail")
        lifecycleScope.launch(Dispatchers.Main) {
            val args = Bundle().apply {
                putString(KEY_CODE, FOURTHLINE_SCAN_FAILED)
                putString(KEY_MESSAGE, error.asString(requireContext()))
            }
            activityViewModel.navigateBackToDocTypeSelectionFragment(args)
        }
    }

    override fun onSuccess(result: DocumentScannerResult) {
        Timber.d("onSuccess")
        lifecycleScope.launch(Dispatchers.Main) {
            kycSharedViewModel.updateKycInfoWithDocumentScannerResult(currentDocumentType, result)
            activityViewModel.navigateToDocScanResultFragment()
        }
    }

    private fun showIntermediateResult(image: Bitmap) {
        Timber.d("showIntermediateResult")
        scanPreview!!.setImageBitmap(image)
        updateUiForResult()
    }

    private fun updateUiForScanning(isScan: Boolean,
                                    tilted: Boolean,
                                    maskDrawable: Drawable?,
                                    stepText: String)
    {
        documentMask?.setImageDrawable(maskDrawable)
        resultRoot?.hide()
        stepLabel?.text = stepText
        stepLabel?.show()
        scanPreview?.hide()
        resultButtons?.hide()
        if (isScan) {
            progressRoot?.show()
            noticeRoot?.show()
            warningsLabel?.text = ""
            takeSnapshot?.hide()
            showSnapshotJob?.cancel()
            showSnapshotJob = lifecycleScope.launch(Dispatchers.Main) {
                delay(5000)
                takeSnapshot?.show()
            }
        } else {
            takeSnapshot?.show()
            progressRoot?.hide()
            noticeRoot?.hide()
        }
        if (tilted) {
            bottomInfoText?.text = getString(R.string.document_scanner_tilted_notice)
            bottomInfoText?.show()
        } else {
            bottomInfoText?.hide()
        }
    }

    private fun updateUiForResult() {
        showSnapshotJob?.cancel()
        stepLabel?.hide()
        takeSnapshot?.hide()
        scanPreview?.show()
        resultButtons?.show()
        resultRoot?.show()
        progressRoot?.hide()
        noticeRoot?.hide()
        bottomInfoText?.text = getString(R.string.document_scanner_clear_picture_title)
        bottomInfoText?.show()
    }

    private fun handleShortScreen() {
        if (isScreenShort(requireContext())) {
            resultRoot?.orientation = LinearLayout.HORIZONTAL
            progressRoot?.orientation = LinearLayout.HORIZONTAL
            if (currentDocumentType == DocumentType.PASSPORT) {
                noticeRoot?.findViewById<View>(R.id.noticeImage)?.visibility = View.INVISIBLE
                noticeRoot?.findViewById<View>(R.id.noticeTitle)?.visibility = View.INVISIBLE
                bottomInfoText?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            }
        }
    }

    private fun isScreenShort(context: Context): Boolean {
        val dm = context.resources.displayMetrics
        return dm.heightPixels.toFloat() / dm.density < 700.0
    }

    companion object {
        const val DOC_TYPE_KEY = "DOC_TYPE_KEY"
        const val TYPE_PASSPORT = 1
        const val TYPE_ID = 2

        private val SHOW_INTERMEDIATR_RESULTS = true
    }

}