package de.solarisbank.identhub.session.utils

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber
import java.net.URI

fun provideFourthlineActivityIntent(context: Context): Intent {

    val isFourthlineModuleAvailable = context.packageManager.queryIntentActivities(
            Intent(FOURTHLINE_FLOW_ACTIVITY_ACTION, null).apply { addCategory(Intent.CATEGORY_DEFAULT) }, 0
    )
            .last()
            .activityInfo.name == FOURTHLINE_ACTIVITY_REFERENCE_CLASS
    if (isFourthlineModuleAvailable) {
        return  Intent(context, Class.forName(FOURTHLINE_ACTIVITY_REFERENCE_CLASS))
    } else {
        throw IllegalStateException("Fourthline identification is impossible, module is absent")
    }
}

fun isServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
    for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
        if (service.service.className.toString().endsWith("KycUploadingService")) {
            Timber.d("isServiceRunning() true")
            return true
        }
    }
    Timber.d("isServiceRunning() false")
    return false
}

fun buildApiUrl(url: String): String {
    val uri = URI.create(url)
    if (uri.authority.contains("-api")) {
        return uri.authority
    } else {
        val domain = uri.authority.replaceFirst(".", "-api.")
        val apiUri = Uri.Builder().authority(domain)
                .scheme(uri.scheme)
                .appendEncodedPath("person_onboarding")
                .appendEncodedPath(uri.path.substring(1))
                .build()

        var apiStringUrl = apiUri.toString()
        if (apiStringUrl.last() != '/') {
            apiStringUrl = "$apiStringUrl/"
        }

        return apiStringUrl
    }
}

const val SHOW_UPLOADING_SCREEN = "SHOW_UPLOADING_SCREEN"
const val FOURTHLINE_FLOW_ACTIVITY_ACTION = "de.solarisbank.identhub.FOURTHLINE_FLOW"
const val FOURTHLINE_ACTIVITY_REFERENCE_CLASS = "de.solarisbank.sdk.fourthline.feature.ui.FourthlineActivity"