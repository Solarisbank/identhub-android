package de.solarisbank.sdk.fourthline

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import com.fourthline.core.DocumentFileSide
import com.fourthline.core.DocumentType
import com.fourthline.vision.document.DocumentScannerError
import com.fourthline.vision.document.DocumentScannerStep
import com.fourthline.vision.document.DocumentScannerStepWarning
import com.fourthline.vision.selfie.SelfieScannerError
import com.fourthline.vision.selfie.SelfieScannerStep
import com.fourthline.vision.selfie.SelfieScannerWarning
import de.solarisbank.identhub.fourthline.R
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

fun View.onLayoutMeasuredOnce(action: (View) -> Unit) = viewTreeObserver.addOnGlobalLayoutListener(
        object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                viewTreeObserver.removeOnGlobalLayoutListener(this)

                action(this@onLayoutMeasuredOnce)
            }
        })


fun SelfieScannerStep.asString(context: Context) =  when (this) {
    SelfieScannerStep.SELFIE -> context.resources.getString(R.string.identhub_fourthline_selfie_scan_title)
    SelfieScannerStep.TURN_HEAD_LEFT -> context.resources.getString(R.string.identhub_fourthline_selfie_scan_step_left)
    SelfieScannerStep.TURN_HEAD_RIGHT -> context.resources.getString(R.string.identhub_fourthline_selfie_scan_step_right)
}

fun SelfieScannerWarning.asString(context: Context) = when (this) {
    SelfieScannerWarning.FACE_NOT_DETECTED -> context.resources.getString(R.string.identhub_fourthline_selfie_warning_not_detected)
    SelfieScannerWarning.FACE_NOT_IN_FRAME -> context.resources.getString(R.string.identhub_fourthline_selfie_warning_face_in_frame)
    SelfieScannerWarning.FACE_TOO_CLOSE -> context.resources.getString(R.string.identhub_fourthline_selfie_warning_phone_too_close)
    SelfieScannerWarning.FACE_TOO_FAR -> context.resources.getString(R.string.identhub_fourthline_selfie_warning_phone_too_far)
    SelfieScannerWarning.FACE_YAW_TOO_BIG -> context.resources.getString(R.string.identhub_selfie_warning_yaw_too_big)
    SelfieScannerWarning.EYES_CLOSED -> context.resources.getString(R.string.identhub_fourthline_selfie_warning_eyes)
    SelfieScannerWarning.DEVICE_NOT_STEADY -> context.resources.getString(R.string.identhub_fourthline_selfie_warning_device_steadiness)
}

fun SelfieScannerError.asString(context: Context) = when (this) {
    SelfieScannerError.TIMEOUT -> context.resources.getString(R.string.identhub_scanner_error_timeout)
    SelfieScannerError.FACE_DISAPPEARED -> context.resources.getString(R.string.identhub_scanner_error_face_disappeared)
    SelfieScannerError.CAMERA_PERMISSION_NOT_GRANTED -> context.resources.getString(R.string.identhub_scanner_error_camera_permission)
    SelfieScannerError.RECORDING_FAILED -> context.resources.getString(R.string.identhub_scanner_error_recording_failed)
    SelfieScannerError.RECORD_AUDIO_PERMISSION_NOT_GRANTED -> context.resources.getString(R.string.identhub_scanner_error_unknown)
    SelfieScannerError.SCANNER_INTERRUPTED -> context.resources.getString(R.string.identhub_scanner_error_interrupted)
    SelfieScannerError.RECOGNITION_MODELS_NOT_DOWNLOADED -> context.resources.getString(R.string.identhub_scanner_error_models_not_downloaded)
    SelfieScannerError.UNKNOWN -> context.resources.getString(R.string.identhub_scanner_error_unknown)
    SelfieScannerError.CAMERA_NOT_AVAILABLE -> context.resources.getString(R.string.identhub_scanner_error_unknown)
    SelfieScannerError.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE -> context.resources.getString(R.string.identhub_scanner_error_unknown)
    SelfieScannerError.MULTIPLE_FACES_DETECTED -> context.resources.getString(R.string.identhub_scanner_error_multiple_faces_detected)
}

fun DocumentScannerStep.prettify() =
        "Step: fileSide = $fileSide, isAngled = $isAngled, isAutoDetectAvailable = $isAutoDetectAvailable"

fun List<DocumentScannerStepWarning>.asString(context: Context) = when (first()) {
    DocumentScannerStepWarning.DOCUMENT_TOO_DARK -> context.resources.getString(R.string.identhub_document_scanner_warning_too_dark)
    DocumentScannerStepWarning.DEVICE_NOT_STEADY -> context.resources.getString(R.string.identhub_document_scanner_warning_device_not_steady)
    DocumentScannerStepWarning.RECOGNITION_MODELS_NOT_DOWNLOADED ->
        context.resources.getString(R.string.identhub_document_scanner_warning_recognition_models_not_downloaded)
    DocumentScannerStepWarning.GOOGLE_PLAY_SERVICES_NOT_AVAILABLE -> context.resources.getString(R.string.identhub_scanner_error_unknown)
}

fun DocumentScannerError.asString(context: Context) = when (this) {
    DocumentScannerError.CAMERA_PERMISSION_NOT_GRANTED -> context.resources.getString(R.string.identhub_scanner_error_camera_permission)
    DocumentScannerError.RECORDING_FAILED -> context.resources.getString(R.string.identhub_scanner_error_recording_failed)
    DocumentScannerError.RECORD_AUDIO_PERMISSION_NOT_GRANTED -> context.resources.getString(R.string.identhub_scanner_error_unknown)
    DocumentScannerError.SCANNER_INTERRUPTED -> context.resources.getString(R.string.identhub_scanner_error_interrupted)
    DocumentScannerError.UNKNOWN -> context.resources.getString(R.string.identhub_scanner_error_unknown)
    DocumentScannerError.TIMEOUT -> context.resources.getString(R.string.identhub_scanner_error_timeout)
    DocumentScannerError.CAMERA_NOT_AVAILABLE -> context.resources.getString(R.string.identhub_scanner_error_unknown)
}

fun DocumentScannerStep.asString(context: Context): CharSequence {
    val resource = when(fileSide) {
        DocumentFileSide.FRONT -> {
            if (isAngled) R.string.identhub_fourthline_doc_scan_angled
            else R.string.identhub_fourthline_doc_scan_front
        }
        DocumentFileSide.BACK -> {
            if (isAngled) R.string.identhub_fourthline_doc_scan_back_angled
            else R.string.identhub_fourthline_doc_scan_back
        }
        DocumentFileSide.INSIDE_LEFT -> {
            if (isAngled) R.string.identhub_document_scanner_side_inside_left_angled_title
            else R.string.identhub_document_scanner_side_inside_left_title
        }
        DocumentFileSide.INSIDE_RIGHT -> {
            if (isAngled) R.string.identhub_document_scanner_side_inside_right_angled_title
            else R.string.identhub_document_scanner_side_inside_right_title
        }
        else -> R.string.identhub_document_scanner_side_undefined_title
    }

    return context.getString(resource)
}

fun DocumentScannerStep.isScan(docType: DocumentType): Boolean {
    if (isAngled || !isAutoDetectAvailable) {
        return false
    }

    return if (docType == DocumentType.PASSPORT) {
        fileSide == DocumentFileSide.FRONT
    } else {
        fileSide == DocumentFileSide.BACK
    }
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
            date = SimpleDateFormat(format, Locale.US)
                .apply { timeZone = TimeZone.getTimeZone("UTC") }
                .parse(this)
            break
        } catch (pE: ParseException){
            Timber.d("getDateFromMRZ() string value: $this, format: $format")
        }
    }
    Timber.d("String.getDateFromMRZ() date value: $date")
    return date
}

fun String.streetNumber(): Int? {
    var answer: Int? = null
    try {
        val m: Matcher = Pattern.compile("-?\\d+").matcher(this)
        if (m.find()) {
            answer = m.group().toIntOrNull()
        }
    } catch (e: Exception) {
        Timber.e(e, "String.streetNumber()")
    }
    return answer
}

fun String.streetSuffix(): String? {
    var answer: String? = null
    try {
        val m: Matcher = Pattern.compile("-?[a-zA-Z]+").matcher(this)
        if (m.find()) {
            answer = m.group()
        }
    } catch (e: Exception) {
        Timber.e(e, "String.streetSuffix()")
    }
    return answer
}