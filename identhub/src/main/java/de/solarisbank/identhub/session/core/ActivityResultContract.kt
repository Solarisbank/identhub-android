package de.solarisbank.identhub.session.core

import android.content.Context
import android.content.Intent
import androidx.annotation.RestrictTo

@RestrictTo(RestrictTo.Scope.LIBRARY)
interface ActivityResultContract<Input, Output> {

    fun createFirstStepIntent(context: Context, input: Input): Intent

    fun createNextStepIntent(context: Context, input: Input): Intent

    fun parseResult(resultCode: Int, intent: Intent?): Output
}
