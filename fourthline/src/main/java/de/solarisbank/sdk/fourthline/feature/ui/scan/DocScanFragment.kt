package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.animation.AnimatorInflater
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.fourthline.core.DocumentFileSide
import com.fourthline.core.DocumentType
import com.fourthline.vision.document.*
import de.solarisbank.sdk.core.BaseActivity
import de.solarisbank.sdk.core.view.BulletListLayout
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

    private val kycSharedViewModel: KycSharedViewModel by lazy {
        ViewModelProvider(requireActivity(), (requireActivity() as FourthlineActivity).viewModelFactory)
            .get(KycSharedViewModel::class.java)
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
    private var progressRoot: LinearLayout? = null
    private var resultRoot: LinearLayout? = null
    private var tiltingCard: ImageView? = null
    private var bulletList: BulletListLayout? = null

    internal lateinit var assistedViewModelFactory: AssistedViewModelFactory
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var currentDocumentType: DocumentType

    private var cleanupJob: Job? = null
    private var showSnapshotJob: Job? = null

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
        progressRoot = null
        resultRoot = null
        tiltingCard = null
        bulletList = null
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
        return DocumentScannerConfig(
            type = currentDocumentType,
            includeAngledSteps = true,
            debugModeEnabled = false,
            shouldRecordVideo = true,
            mrzValidationPolicy = MrzValidationPolicy.STRONG
        )
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
                DocumentType.PASSPORT -> documentMask?.setImageResource(R.drawable.ic_passport_front_success_frame)
                DocumentType.ID_CARD -> documentMask?.setImageResource(R.drawable.ic_idcard_front_success_frame)
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
            resultRoot = findViewById(R.id.resultRoot)
            progressRoot = findViewById(R.id.progressRoot)
            tiltingCard = findViewById(R.id.tiltingCard)
            bulletList = findViewById(R.id.bulletList)
            handleShortScreen()
            onLayoutMeasuredOnce {
                punchhole?.punchholeRect = getDocumentDetectionArea()
                punchhole?.postInvalidate()
            }
        }
    }

    override fun getDocumentDetectionArea(): Rect {
        Timber.d("getDocumentDetectionArea() $documentMask")
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
                stepText = step.asString(currentDocumentType, requireContext()),
                tiltingResource = step.getTiltingResource(currentDocumentType)
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

    private fun DocumentScannerStep.getTiltingResource(docType: DocumentType): Int {
        return when(docType) {
            DocumentType.PASSPORT -> R.drawable.ic_tilting_passport
            else -> {
                when (fileSide) {
                    DocumentFileSide.FRONT -> R.drawable.ic_tilting_card_front
                    DocumentFileSide.BACK -> R.drawable.ic_tilting_card_back
                    else -> R.drawable.ic_tilting_card_front
                }
            }
        }
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
        lifecycleScope.launch(Dispatchers.Main) {
            showIntermediateResult(result.image.cropped)
            kycSharedViewModel.updateKycInfoWithDocumentScannerStepResult(currentDocumentType, result)
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
                                    stepText: String,
                                    tiltingResource: Int)
    {
        documentMask?.setImageDrawable(maskDrawable)
        resultRoot?.hide()
        stepLabel?.text = stepText
        stepLabel?.show()
        scanPreview?.hide()
        resultButtons?.hide()
        if (isScan) {
            progressRoot?.show()
            warningsLabel?.text = ""
            takeSnapshot?.hide()
            showSnapshotJob?.cancel()
            showSnapshotJob = lifecycleScope.launch(Dispatchers.Main) {
                delay(5000)
                takeSnapshot?.show()
            }
            bulletList?.updateItems(
                title = getString(R.string.document_scanner_auto_scan_list_title),
                items = listOf(
                    getString(R.string.document_scanner_auto_scan_list_item1),
                    getString(R.string.document_scanner_auto_scan_list_item2)
                )
            )
            bulletList?.show()
        } else {
            takeSnapshot?.show()
            progressRoot?.hide()
            bulletList?.hide()
        }
        if (tilted) {
            tiltingCard?.setImageResource(tiltingResource)
            toggleTiltingCard(true)
        } else {
            toggleTiltingCard(false)
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
        toggleTiltingCard(false)
        bulletList?.updateItems(
            title = getString(R.string.document_scanner_clear_picture_list_title),
            items = listOf(
                getString(R.string.document_scanner_clear_picture_list_item1),
                getString(R.string.document_scanner_clear_picture_list_item2),
                getString(R.string.document_scanner_clear_picture_list_item3)
            )
        )
        bulletList?.show()
    }

    private fun toggleTiltingCard(show: Boolean) {
        if (show) {
            tiltingCard?.rotationX = 0f
            tiltingCard?.show()
            (AnimatorInflater.loadAnimator(requireContext(), R.animator.card_tilt) as ObjectAnimator)
                .apply {
                    target = tiltingCard
                    start()
                }
        } else {
            tiltingCard?.hide()
            tiltingCard?.clearAnimation()
        }
    }

    private fun handleShortScreen() {
        if (isScreenShort(requireContext())) {
            resultRoot?.orientation = LinearLayout.HORIZONTAL
            progressRoot?.orientation = LinearLayout.HORIZONTAL
        }
    }

    private fun isScreenShort(context: Context): Boolean {
        val dm = context.resources.displayMetrics
        return dm.heightPixels.toFloat() / dm.density < 700.0
    }

    companion object {
        const val DOC_TYPE_KEY = "DOC_TYPE_KEY"
    }

}