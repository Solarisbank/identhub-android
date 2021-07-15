package de.solarisbank.sdk.fourthline.feature.ui.scan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
    private var resultBlock: ViewGroup? = null
    private var punchhole: PunchholeView? = null
    private var retakeButton: TextView? = null
    private var confirmButton: TextView? = null
    private var progressBar: ProgressBar? = null
    private var resultImageView: AppCompatImageView? = null

    internal lateinit var assistedViewModelFactory: AssistedViewModelFactory
    internal lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var currentDocumentType: DocumentType

    private var cleanupJob: Job? = null

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
        documentMask = null
        takeSnapshot = null
        scanPreview = null
        stepLabel = null
        warningsLabel = null
        resultBlock = null
        punchhole = null
        retakeButton = null
        confirmButton = null
        progressBar = null
        resultImageView = null
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
                .also {
                    documentMask = it.findViewById(R.id.documentMask)
                    when (currentDocumentType) {
                        DocumentType.PASSPORT -> documentMask!!.setImageResource(R.drawable.ic_passport_front_success_frame)
                        DocumentType.ID_CARD -> documentMask!!.setImageResource(R.drawable.ic_idcard_front_success_frame)
                    }
                    takeSnapshot = it.findViewById(R.id.takeSnapshot)
                    takeSnapshot!!.setOnClickListener { takeSnapshot() }
                    scanPreview = it.findViewById(R.id.scanPreview)
                    stepLabel = it.findViewById(R.id.stepName)
                    warningsLabel = it.findViewById(R.id.warningsLabel)
                    resultBlock = it.findViewById(R.id.resultBlock)
                    punchhole = it.findViewById(R.id.punchhole)
                    retakeButton = it.findViewById(R.id.retakeButton)
                    retakeButton!!.setOnClickListener { resetCurrentStep() }
                    confirmButton = it.findViewById(R.id.confirmButton)
                    confirmButton!!.setOnClickListener { moveToNextStep() }
                    progressBar = it.findViewById(R.id.progressBar)
                    resultImageView = it.findViewById(R.id.resultImageView)
                    it.onLayoutMeasuredOnce {
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
                documentMask!!.right,
                documentMask!!.bottom
        )
    }

    override fun onStepUpdate(step: DocumentScannerStep) {
        Timber.d("onStepUpdate: ${step.prettify()}")
        step.fileSide.ordinal
        requireActivity().runOnUiThread(Runnable {
            stepLabel!!.text = step.asString(requireContext())
            documentMask!!.setImageDrawable(step.findMaskDrawable(requireContext()))
            punchhole!!.punchholeRect = getDocumentDetectionArea()
            punchhole!!.postInvalidate()
            syncUi(UiState.SCANNING)
            if (step.isAutoDetectAvailable && currentDocumentType != DocumentType.ID_CARD) {
                takeSnapshot!!.visibility = View.GONE
                lifecycleScope.launch(Dispatchers.Main) {
                    delay(5000)
                    takeSnapshot!!.visibility = View.VISIBLE
                }
            } else {
                takeSnapshot!!.visibility = View.VISIBLE
            }
        })
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
                result.metadata.fileSide.name
            }
        } else {
            moveToNextStep()
        }
    }

    override fun onFail(error: DocumentScannerError) {
        Timber.d("onFail")
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), error.asString(requireContext()), Toast.LENGTH_LONG).show()
            requireActivity().onBackPressed()
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
        syncUi(UiState.INTERMEDIATE_RESULT)
    }

    private fun syncUi(newState: UiState) {
        Timber.d("syncUi")
        when (newState) {
            UiState.SCANNING -> {
                stepLabel!!.show()
                takeSnapshot!!.show()
                scanPreview!!.hide()
                resultBlock!!.hide()
                progressBar!!.show()
                resultImageView!!.hide()
            }
            UiState.INTERMEDIATE_RESULT -> {
                stepLabel!!.text = ""
                warningsLabel!!.text = getString(R.string.document_scanner_scan_success)
                stepLabel!!.hide()
                takeSnapshot!!.hide()
                scanPreview!!.show()
                resultBlock!!.show()
                progressBar!!.hide()
                resultImageView!!.show()
            }
        }
    }

    companion object {
        const val DOC_TYPE_KEY = "DOC_TYPE_KEY"
        const val TYPE_PASSPORT = 1
        const val TYPE_ID = 2

        private val SHOW_INTERMEDIATR_RESULTS = true


    }

}