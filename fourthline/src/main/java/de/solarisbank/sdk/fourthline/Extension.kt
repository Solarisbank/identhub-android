package de.solarisbank.sdk.fourthline

import android.view.View
import android.view.ViewTreeObserver
import com.fourthline.core.DocumentFileSide
import com.fourthline.vision.document.DocumentScannerError
import com.fourthline.vision.document.DocumentScannerStep
import com.fourthline.vision.document.DocumentScannerStepWarning
import com.fourthline.vision.selfie.SelfieScannerError
import com.fourthline.vision.selfie.SelfieScannerStep
import com.fourthline.vision.selfie.SelfieScannerWarning
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun View.onLayoutMeasuredOnce(action: (View) -> Unit) = viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                action(this@onLayoutMeasuredOnce)
            }
        })


fun SelfieScannerStep.asString() = when (this) {
    SelfieScannerStep.SELFIE -> "Look into camera"
    SelfieScannerStep.MANUAL_SELFIE -> "Take a selfie"
    SelfieScannerStep.TURN_HEAD_LEFT -> "Turn your head to the left"
    SelfieScannerStep.TURN_HEAD_RIGHT -> "Turn your head to the right"
}

fun SelfieScannerWarning.asString() = when (this) {
    SelfieScannerWarning.FACE_NOT_DETECTED -> "Face not detected"
    SelfieScannerWarning.MULTIPLE_FACES_DETECTED -> "Multiple faces detected"
    SelfieScannerWarning.FACE_NOT_IN_FRAME -> "Fit face in frame"
    SelfieScannerWarning.FACE_TOO_CLOSE -> "Move the phone farther"
    SelfieScannerWarning.FACE_TOO_FAR -> "Move the phone closer"
    SelfieScannerWarning.FACE_YAW_TOO_BIG -> "Face the camera directly"
    SelfieScannerWarning.EYES_CLOSED -> "Open both eyes"
    SelfieScannerWarning.DEVICE_NOT_STEADY -> "Device not steady"
}

fun SelfieScannerError.asString() = when (this) {
    SelfieScannerError.TIMEOUT -> "Selfie scanner timed out"
    SelfieScannerError.FACE_DISAPPEARED -> "Face disappeared during liveness check"
    SelfieScannerError.CAMERA_PERMISSION_NOT_GRANTED -> "Please grant camera access"
    SelfieScannerError.MANUAL_SELFIE_NOT_ALLOWED -> "Not allowed while not in manual selfie mode"
    SelfieScannerError.RECORDING_FAILED -> "Video recording failed"
    SelfieScannerError.SCANNER_INTERRUPTED -> "Selfie scanner was interrupted"
    SelfieScannerError.RECOGNITION_MODELS_NOT_DOWNLOADED -> "Check internet connection and/or free space available"
    SelfieScannerError.UNKNOWN -> "Unexpected error occurred"
}

private var cleanupJob: Job? = null

fun CoroutineScope.scheduleCleanup(block: () -> Unit) {
    cleanupJob?.cancel()
    cleanupJob = launch(Dispatchers.Main) {
        delay(500)
        block()
    }
}

fun DocumentScannerStep.prettify() =
        "Step: fileSide = $fileSide, isAngled = $isAngled, isAutoDetectAvailable = $isAutoDetectAvailable"

fun List<DocumentScannerStepWarning>.asString() = when (first()) {
    DocumentScannerStepWarning.DOCUMENT_TOO_DARK -> "Document too dark"
    DocumentScannerStepWarning.DEVICE_NOT_STEADY -> "Device not steady"
    DocumentScannerStepWarning.RECOGNITION_MODELS_NOT_DOWNLOADED ->
        "Check internet connection and/or free space available"
}

fun DocumentScannerError.asString() = when (this) {
    DocumentScannerError.CAMERA_PERMISSION_NOT_GRANTED -> "Please allow camera usage"
    DocumentScannerError.RECORDING_FAILED -> "Image recording failed"
    DocumentScannerError.SCANNER_INTERRUPTED -> "Document scanner was interrupted"
    DocumentScannerError.UNKNOWN -> "Unexpected scanner error occurred"
    DocumentScannerError.TIMEOUT -> "Document scanner timed out"
}

fun DocumentScannerStep.asString(): String {
    val side = when (fileSide) {
        DocumentFileSide.FRONT -> "front"
        DocumentFileSide.BACK -> "back"
        DocumentFileSide.INSIDE_LEFT -> "inside left"
        DocumentFileSide.INSIDE_RIGHT -> "inside right"
    }

    return "Scan $side${if (isAngled) " tilted" else ""} side of id card"
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun String.getDateFromMRZ(): Date? {

    val formats = listOf("yyMMdd", "MMM dd, yyyy")
    var date: Date? = null

    for (format in formats) {
        try {
            date = SimpleDateFormat(format).parse(this)
            break
        } catch (pE: ParseException){
            Timber.d("getDateFromMRZ() string value: $this, format: $format")
        }
    }
    Timber.d("String.getDateFromMRZ() date value: $this")
    return date
}