package de.solarisbank.identhub.session.feature.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import timber.log.Timber
import java.net.URI


fun isFourthlineModuleAvailable(context: Context): Boolean {
    return context
            .packageManager
            .getPackageInfo(
                    context.applicationContext.packageName,
                    PackageManager.GET_ACTIVITIES
            )
            .activities
            .toList()
            .any { it.name == FOURTHLINE_ACTIVITY_REFERENCE_CLASS }
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
const val VERIFICATION_BANK_ACTIVITY_REFERENCE_CLASS = "de.solarisbank.identhub.verfication.bank.VerificationBankActivity"
const val CONTRACT_ACTIVITY_REFERENCE_CLASS = "de.solarisbank.identhub.session.main.MainActivity"
const val MAIN_ACTIVITY_REFERENCE_CLASS = "de.solarisbank.identhub.session.main.MainActivity"