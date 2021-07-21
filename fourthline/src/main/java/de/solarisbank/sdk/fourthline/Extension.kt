package de.solarisbank.sdk.fourthline

import android.content.Context
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


fun SelfieScannerStep.asString(context: Context) =  when (this) {
    SelfieScannerStep.SELFIE -> context.resources.getString(R.string.selfie_intro)
    SelfieScannerStep.MANUAL_SELFIE -> context.resources.getString(R.string.selfie_manual)
    SelfieScannerStep.TURN_HEAD_LEFT -> context.resources.getString(R.string.selfie_turn_left)
    SelfieScannerStep.TURN_HEAD_RIGHT -> context.resources.getString(R.string.selfie_turn_right)
}

fun SelfieScannerWarning.asString(context: Context) = when (this) {
    SelfieScannerWarning.FACE_NOT_DETECTED -> context.resources.getString(R.string.selfie_warning_not_detected)
    SelfieScannerWarning.MULTIPLE_FACES_DETECTED -> context.resources.getString(R.string.selfie_warning_multiple_detected)
    SelfieScannerWarning.FACE_NOT_IN_FRAME -> context.resources.getString(R.string.selfie_warning_not_in_frame)
    SelfieScannerWarning.FACE_TOO_CLOSE -> context.resources.getString(R.string.selfie_warning_too_close)
    SelfieScannerWarning.FACE_TOO_FAR -> context.resources.getString(R.string.selfie_warning_too_far)
    SelfieScannerWarning.FACE_YAW_TOO_BIG -> context.resources.getString(R.string.selfie_warning_yaw_too_big)
    SelfieScannerWarning.EYES_CLOSED -> context.resources.getString(R.string.selfie_warning_eyes_closed)
    SelfieScannerWarning.DEVICE_NOT_STEADY -> context.resources.getString(R.string.selfie_warning_device_not_steady)
}

fun SelfieScannerError.asString(context: Context) = when (this) {
    SelfieScannerError.TIMEOUT -> context.resources.getString(R.string.selfie_error_timeout)
    SelfieScannerError.FACE_DISAPPEARED -> context.resources.getString(R.string.selfie_error_face_disappeared)
    SelfieScannerError.CAMERA_PERMISSION_NOT_GRANTED -> context.resources.getString(R.string.selfie_error_camera_permission)
    SelfieScannerError.MANUAL_SELFIE_NOT_ALLOWED -> context.resources.getString(R.string.selfie_error_manual_selfie_not_allowed)
    SelfieScannerError.RECORDING_FAILED -> context.resources.getString(R.string.selfie_error_recording_failed)
    SelfieScannerError.SCANNER_INTERRUPTED -> context.resources.getString(R.string.selfie_error_scanner_interrupted)
    SelfieScannerError.RECOGNITION_MODELS_NOT_DOWNLOADED -> context.resources.getString(R.string.selfie_error_recognition_models_not_downloaded)
    SelfieScannerError.UNKNOWN -> context.resources.getString(R.string.selfie_error_unknown)
    SelfieScannerError.CAMERA_NOT_AVAILABLE -> context.resources.getString(R.string.selfie_error_unknown)
    SelfieScannerError.MULTIPLE_FACES_DETECTED -> context.resources.getString(R.string.selfie_warning_multiple_detected)
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

fun List<DocumentScannerStepWarning>.asString(context: Context) = when (first()) {
    DocumentScannerStepWarning.DOCUMENT_TOO_DARK -> context.resources.getString(R.string.document_scanner_warning_too_dark)
    DocumentScannerStepWarning.DEVICE_NOT_STEADY -> context.resources.getString(R.string.document_scanner_warning_device_not_steady)
    DocumentScannerStepWarning.RECOGNITION_MODELS_NOT_DOWNLOADED ->
        context.resources.getString(R.string.document_scanner_warning_recognition_models_not_downloaded)
}

fun DocumentScannerError.asString(context: Context) = when (this) {
    DocumentScannerError.CAMERA_PERMISSION_NOT_GRANTED -> context.resources.getString(R.string.document_scanner_error_no_permission)
    DocumentScannerError.RECORDING_FAILED -> context.resources.getString(R.string.document_scanner_error_recording_failed)
    DocumentScannerError.SCANNER_INTERRUPTED -> context.resources.getString(R.string.document_scanner_error_scanner_interrupted)
    DocumentScannerError.UNKNOWN -> context.resources.getString(R.string.document_scanner_error_unknown)
    DocumentScannerError.TIMEOUT -> context.resources.getString(R.string.document_scanner_error_timeout)
    DocumentScannerError.CAMERA_NOT_AVAILABLE -> context.resources.getString(R.string.document_scanner_error_unknown)
}

fun DocumentScannerStep.asString(context: Context): String {
    val side = when (fileSide) {
        DocumentFileSide.FRONT -> context.resources.getString(R.string.document_scanner_file_side_front)
        DocumentFileSide.BACK -> context.resources.getString(R.string.document_scanner_file_side_back)
        DocumentFileSide.INSIDE_LEFT -> context.resources.getString(R.string.document_scanner_file_side_inside_left)
        DocumentFileSide.INSIDE_RIGHT -> context.resources.getString(R.string.document_scanner_file_side_inside_right)
    }

    val tilted = if (isAngled) context.resources.getString(R.string.document_scanner_file_tilted) else ""
    return String.format(context.resources.getString(R.string.document_scanner_file_side), side, tilted)
}

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun String.parseDateFromString(): Date? {

    val formats = listOf("MMM dd, yyyy", "yyyy-MM-dd", "dd.MM.yyyy")
    var date: Date? = null

    for (format in formats) {
        try {
            date = SimpleDateFormat(format).parse(this)
            break
        } catch (pE: ParseException){
            Timber.d("getDateFromMRZ() string value: $this, format: $format")
        }
    }
    Timber.d("String.getDateFromMRZ() date value: $date")
    return date
}

fun String.parseDateFromMrtd(): Date? {

    val formats = listOf("yyMMdd")
    var date: Date? = null

    for (format in formats) {
        try {
            date = SimpleDateFormat(format).parse(this)
            break
        } catch (pE: ParseException){
            Timber.d("getDateFromMRZ() string value: $this, format: $format")
        }
    }
    Timber.d("String.getDateFromMRZ() date value: $date")
    return date
}