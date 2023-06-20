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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.fourthline.core.DocumentFileSide
import com.fourthline.core.DocumentType
import com.fourthline.vision.RecordingType
import com.fourthline.vision.document.*
import de.solarisbank.sdk.data.customization.CustomizationRepository
import de.solarisbank.sdk.data.di.koin.IdenthubKoinComponent
import de.solarisbank.sdk.data.dto.Customization
import de.solarisbank.sdk.feature.customization.ButtonStyle
import de.solarisbank.sdk.feature.customization.ImageViewTint
import de.solarisbank.sdk.feature.customization.customize
import de.solarisbank.sdk.feature.view.BulletListLayout
import de.solarisbank.sdk.fourthline.*
import de.solarisbank.sdk.fourthline.data.dto.toDocumentType
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.FOURTHLINE_SCAN_FAILED
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.KEY_CODE
import de.solarisbank.sdk.fourthline.feature.ui.FourthlineViewModel.Companion.KEY_MESSAGE
import de.solarisbank.sdk.fourthline.feature.ui.custom.PunchholeView
import de.solarisbank.sdk.fourthline.feature.ui.kyc.info.KycSharedViewModel
import de.solarisbank.sdk.logger.IdLogger
import de.solarisbank.sdk.logger.IdLogger.Category.Fourthline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.core.component.inject
import timber.log.Timber

class DocScanFragment : DocumentScannerFragment(), IdenthubKoinComponent {

    private val activityViewModel: FourthlineViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)
    private val kycSharedViewModel: KycSharedViewModel by koinNavGraphViewModel(FourthlineFlow.navigationId)

    private var documentMask: AppCompatImageView? = null
    private var takeSnapshot: Button? = null
    private var scanPreview: AppCompatImageView? = null
    private var stepLabel: AppCompatTextView? = null
    private var resultButtons: ViewGroup? = null
    private var punchhole: PunchholeView? = null
    private var retakeButton: Button? = null
    private var confirmButton: Button? = null
    private var resultRoot: LinearLayout? = null
    private var resultImageView: ImageView? = null
    private var tiltingCard: ImageView? = null
    private var docImageView: ImageView? = null
    private var bulletList: BulletListLayout? = null

    private val customizationRepository: CustomizationRepository by inject()
    private val docScanArgs: DocScanFragmentArgs by navArgs()
    private lateinit var currentDocumentType: DocumentType
    private val customization: Customization by lazy { customizationRepository.get() }

    private var cleanupJob: Job? = null
    private var showSnapshotJob: Job? = null
    private var animator: ObjectAnimator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        currentDocumentType = docScanArgs.docType.toDocumentType()

        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        cleanupJob?.cancel()
        showSnapshotJob?.cancel()
        documentMask = null
        takeSnapshot = null
        scanPreview = null
        stepLabel = null
        resultButtons = null
        punchhole = null
        retakeButton = null
        confirmButton = null
        resultRoot = null
        resultImageView = null
        tiltingCard = null
        bulletList = null
        docImageView = null
        super.onDestroyView()
    }

    override val shouldStartAutomatically: Boolean = true

    override fun getConfig(): DocumentScannerConfig {
        Timber.d("getConfig()")
        return DocumentScannerConfig(
            type = currentDocumentType,
            includeAngledSteps = true,
            debugModeEnabled = false,
            recordingType = RecordingType.VIDEO_ONLY,
            mrzValidationPolicy = MrzValidationPolicy.STRONG
        )
    }

    override fun getOverlayView(): View? {
        Timber.d("getOverlayView()")
        return LayoutInflater
                .from(requireContext())
                .inflate(R.layout.identhub_fragment_doc_scan, requireActivity().findViewById(R.id.content), false)
                .also(::initView)
    }

    private fun initView(view: View) {
        with(view) {
            documentMask = findViewById(R.id.documentMask)
            val maskResource = when (currentDocumentType) {
                DocumentType.PASSPORT -> R.drawable.identhub_ic_passport_front_success_frame
                DocumentType.ID_CARD -> R.drawable.identhub_ic_idcard_front_success_frame
                DocumentType.PAPER_ID -> R.drawable.identhub_ic_paperid_inside_left_frame
                else -> R.drawable.identhub_ic_idcard_front_success_frame
            }
            documentMask?.setImageResource(maskResource)
            takeSnapshot = findViewById(R.id.takeSnapshot)
            takeSnapshot?.setOnClickListener { takeSnapshot() }
            scanPreview = findViewById(R.id.scanPreview)
            stepLabel = findViewById(R.id.stepName)
            resultButtons = findViewById(R.id.resultButtonsRoot)
            punchhole = findViewById(R.id.punchhole)
            retakeButton = findViewById(R.id.retakeButton)
            retakeButton?.setOnClickListener { resetCurrentStep() }
            confirmButton = findViewById(R.id.confirmButton)
            confirmButton?.setOnClickListener { moveToNextStep() }
            resultRoot = findViewById(R.id.resultRoot)
            resultImageView = findViewById(R.id.resultImageView)
            tiltingCard = findViewById(R.id.tiltingCard)
            docImageView = findViewById(R.id.docImage)
            bulletList = findViewById(R.id.bulletList)
            handleShortScreen()
            onLayoutMeasuredOnce {
                punchhole?.punchholeRect = getDocumentDetectionArea()
                punchhole?.postInvalidate()
            }
            customizeView()
        }
    }

    private fun customizeView() {
        confirmButton?.customize(customization)
        takeSnapshot?.customize(customization)
        retakeButton?.customize(customization, ButtonStyle.Alternative)
        resultImageView?.customize(customization, ImageViewTint.Secondary)
    }

    override fun getDocumentDetectionArea(): Rect {
        Timber.d("getDocumentDetectionArea() $documentMask")
        return Rect(
                documentMask!!.left,
                documentMask!!.top,
                documentMask!!.right,
                documentMask!!.bottom
        )
    }

    override fun onStepUpdate(step: DocumentScannerStep) {
        Timber.d("onStepUpdate: ${step.prettify()}")

        activity?.runOnUiThread {
            updateUiForScanning(
                isScan = step.isScan(currentDocumentType),
                tilted = step.isAngled,
                maskDrawable = step.findMaskDrawable(requireContext()),
                stepText = step.asString(requireContext()),
                imageResource = step.getImageResource(currentDocumentType)
            )
            punchhole?.punchholeRect = getDocumentDetectionArea()
            punchhole?.postInvalidate()
        }
    }

    private fun DocumentScannerStep.findMaskDrawable(context: Context): Drawable? {
        Timber.d("findMaskDrawable, fileSide: $fileSide")
        val frameResource: Int = when (fileSide) {
            DocumentFileSide.FRONT ->
                when (currentDocumentType) {
                    DocumentType.ID_CARD ->
                        if (isAngled) R.drawable.identhub_ic_idcard_front_tilted_success_frame
                        else R.drawable.identhub_ic_idcard_front_success_frame
                    DocumentType.PASSPORT ->
                        if (isAngled) R.drawable.identhub_ic_passport_angled_success_frame
                        else R.drawable.identhub_ic_passport_front_success_frame
                    DocumentType.PAPER_ID ->
                        if (isAngled) R.drawable.identhub_ic_paperid_back_tilted_frame
                        else R.drawable.identhub_ic_paperid_back_frame
                    else -> R.drawable.identhub_ic_idcard_front_success_frame
                }

            DocumentFileSide.BACK ->
                when (currentDocumentType) {
                    DocumentType.ID_CARD ->
                        if (isAngled) R.drawable.identhub_ic_idcard_back_tilted_success_frame
                        else R.drawable.identhub_ic_idcard_back_success_frame
                    DocumentType.PAPER_ID ->
                        if (isAngled) R.drawable.identhub_ic_paperid_back_tilted_frame
                        else R.drawable.identhub_ic_paperid_back_frame
                    else -> R.drawable.identhub_ic_idcard_back_success_frame
                }
            DocumentFileSide.INSIDE_LEFT ->
                if (isAngled) R.drawable.identhub_ic_paperid_inside_left_tilted_frame
                else R.drawable.identhub_ic_paperid_inside_left_frame
            DocumentFileSide.INSIDE_RIGHT ->
                if (isAngled) R.drawable.identhub_ic_paperid_inside_right_tilted_frame
                else R.drawable.identhub_ic_paperid_inside_right_frame
            else -> throw RuntimeException("ID cards do not have document side: $fileSide")
        }

        return ContextCompat.getDrawable(context, frameResource)
    }

    private fun DocumentScannerStep.getImageResource(docType: DocumentType): Int {
        return when(docType) {
            DocumentType.PASSPORT -> {
                if (isAngled) {
                    R.drawable.identhub_ic_passport_scan_angled
                } else {
                    R.drawable.identhub_ic_passport_scan
                }
            }
            else -> {
                when (fileSide) {
                    DocumentFileSide.FRONT -> {
                        if (isAngled) {
                            R.drawable.identhub_ic_card_scan_front_angled
                        } else {
                            R.drawable.identhub_ic_card_scan_front
                        }
                    }
                    DocumentFileSide.BACK -> {
                        if (isAngled) {
                            R.drawable.identhub_ic_card_scan_back_angled
                        } else {
                            R.drawable.identhub_ic_card_scan_back
                        }
                    }
                    else -> R.drawable.identhub_ic_card_scan_front
                }
            }
        }
    }

    override fun onWarnings(warnings: List<DocumentScannerStepWarning>) {
        Timber.d("onWarnings")
    }

    override fun onStepFail(error: DocumentScannerStepError) {
        Timber.d("Document scanner misuse, reason $error")
        throw RuntimeException("Business logic violation, should never happen")
    }

    override fun onStepSuccess(result: DocumentScannerStepResult) {
        IdLogger.debug(
            category = Fourthline,
            message = "Document side captured: ${step?.fileSide?.name}"
        )

        cleanupJob?.cancel()
        lifecycleScope.launch(Dispatchers.Main) {
            showIntermediateResult(result.image.cropped)
            kycSharedViewModel.updateKycInfoWithDocumentScannerStepResult(
                currentDocumentType, result, docScanArgs.isSecondaryScan
            )
        }
    }

    override fun onFail(error: DocumentScannerError) {
        lifecycleScope.launch(Dispatchers.Main) {
            IdLogger.error(category = Fourthline, message = "Doc scan error: ${error.name}")
            val args = Bundle().apply {
                putString(KEY_CODE, FOURTHLINE_SCAN_FAILED)
                putString(KEY_MESSAGE, error.asString(requireContext()))
            }
            activityViewModel.onDocScanOutcome(DocScanResult.ScanFailed(args))
        }
    }

    override fun onSuccess(result: DocumentScannerResult) {
        lifecycleScope.launch(Dispatchers.Main) {
            IdLogger.info(category = Fourthline, message = "Document Capture Successful")
            // Put it in KYCInfo when the time comes
            if (!docScanArgs.isSecondaryScan) {
                kycSharedViewModel.updateKycInfoWithDocumentScannerResult(
                    currentDocumentType,
                    result
                )
            }
            activityViewModel.onDocScanOutcome(
                DocScanResult.Success(isSecondaryScan = docScanArgs.isSecondaryScan)
            )
        }
    }

    private fun showIntermediateResult(image: Bitmap) {
        Timber.d("showIntermediateResult")
        scanPreview!!.setImageBitmap(image)
        updateUiForResult()
    }

    private fun updateUiForScanning(
        isScan: Boolean,
        tilted: Boolean,
        maskDrawable: Drawable?,
        stepText: CharSequence,
        imageResource: Int)
    {
        documentMask?.setImageDrawable(maskDrawable)
        resultRoot?.hide()
        stepLabel?.text = stepText
        stepLabel?.show()
        scanPreview?.hide()
        resultButtons?.hide()
        bulletList?.hide()
        if (isScan) {
            takeSnapshot?.hide()
            showSnapshotJob?.cancel()
            showSnapshotJob = lifecycleScope.launch(Dispatchers.Main) {
                delay(5000)
                takeSnapshot?.show()
            }
        } else {
            takeSnapshot?.show()
        }
        if (tilted) {
            tiltingCard?.setImageResource(imageResource)
            toggleTiltingCard(true)
            docImageView?.hide()
        } else {
            toggleTiltingCard(false)
            docImageView?.setImageResource(imageResource)
            if (currentDocumentType != DocumentType.PAPER_ID)
                docImageView?.show()
        }
    }

    private fun updateUiForResult() {
        showSnapshotJob?.cancel()
        stepLabel?.hide()
        takeSnapshot?.hide()
        scanPreview?.show()
        resultButtons?.show()
        resultRoot?.show()
        toggleTiltingCard(false)
        docImageView?.hide()
        bulletList?.updateItems(
            title = getString(R.string.identhub_document_scanner_clear_picture_list_title),
            items = listOf(
                getString(R.string.identhub_document_scanner_clear_picture_list_item1),
                getString(R.string.identhub_document_scanner_clear_picture_list_item2),
                getString(R.string.identhub_document_scanner_clear_picture_list_item3)
            )
        )
        bulletList?.show()
    }

    private fun toggleTiltingCard(show: Boolean) {
        animator?.cancel()
        animator = null
        tiltingCard?.rotationX = 0f

        if (show && currentDocumentType != DocumentType.PAPER_ID) {
            tiltingCard?.show()
            animator = AnimatorInflater.loadAnimator(
                requireContext(),
                R.animator.identhub_card_tilt
            ) as ObjectAnimator
            animator?.apply {
                target = tiltingCard
                start()
            }
        } else {
            tiltingCard?.hide()
        }
    }

    private fun handleShortScreen() {
        if (isScreenShort(requireContext())) {
            resultRoot?.orientation = LinearLayout.HORIZONTAL
        }
    }

    private fun isScreenShort(context: Context): Boolean {
        val dm = context.resources.displayMetrics
        return dm.heightPixels.toFloat() / dm.density < 700.0
    }
}

sealed class DocScanResult {
    data class Success(val isSecondaryScan: Boolean): DocScanResult()
    data class ScanFailed(val bundle: Bundle): DocScanResult()
}