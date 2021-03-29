package de.solarisbank.identhub.session.core

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.CheckResult
import androidx.annotation.RestrictTo
import androidx.fragment.app.Fragment

@RestrictTo(RestrictTo.Scope.LIBRARY)
class InlineFragment : Fragment() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!didStart()) {
            startActivityForResult(
                    launchIntent(),
                    requestCode()
            )
            didStart(true)
        }
    }

    override fun onActivityResult(
            requestCode: Int,
            resultCode: Int,
            data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCode()) {
            ActivityResultRegistry.dispatchResult(requestCode = requestCode,
                    resultCode = resultCode,
                    data = data ?: Intent())
        }
    }

    @CheckResult
    private fun didStart(): Boolean {
        return arguments?.getBoolean(
                KEY_DID_START
        ) ?: throw IllegalStateException(
                "No arguments provided"
        )
    }

    private fun didStart(didStart: Boolean) {
        arguments?.putBoolean(
                KEY_DID_START, didStart
        ) ?: throw IllegalStateException(
                "No launch intent provided"
        )
    }

    @CheckResult
    private fun launchIntent(): Intent {
        return arguments?.getParcelable(
                KEY_LAUNCH_INTENT
        ) ?: throw IllegalStateException(
                "No launch intent provided"
        )
    }

    @CheckResult
    private fun requestCode(): Int {
        return arguments?.getInt(KEY_REQUEST_CODE, 0)
                ?.takeIf { it != 0 }
                ?: error("No non-zero request code provided")
    }

    companion object {
        @CheckResult
        fun newInstance(
                launchIntent: Intent,
                requestCode: Int
        ): InlineFragment {
            return InlineFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LAUNCH_INTENT, launchIntent)
                    putInt(KEY_REQUEST_CODE, requestCode)
                }
            }
        }

        private const val KEY_LAUNCH_INTENT = "launch_intent"
        private const val KEY_REQUEST_CODE = "request_code"
        private const val KEY_DID_START = "did_start"
    }
}