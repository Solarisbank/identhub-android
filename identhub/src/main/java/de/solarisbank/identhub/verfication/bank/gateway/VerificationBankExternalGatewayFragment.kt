package de.solarisbank.identhub.verfication.bank.gateway

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.webkit.WebMessageCompat
import androidx.webkit.WebMessagePortCompat
import androidx.webkit.WebMessagePortCompat.*
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewCompat.createWebMessageChannel
import androidx.webkit.WebViewCompat.postWebMessage
import androidx.webkit.WebViewFeature
import de.solarisbank.identhub.R
import de.solarisbank.identhub.base.IdentHubFragment
import de.solarisbank.identhub.di.FragmentComponent
import de.solarisbank.identhub.verfication.bank.VerificationBankViewModel
import de.solarisbank.sdk.core.di.internal.Preconditions
import de.solarisbank.sdk.core.result.Result
import timber.log.Timber

class VerificationBankExternalGatewayFragment : IdentHubFragment() {
    private var sharedViewModel: VerificationBankViewModel? = null
    private var verificationBankExternalGateViewModel: VerificationBankExternalGateViewModel? = null
    private var webView: WebView? = null
    override fun inject(component: FragmentComponent) {
        component.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =
            inflater.inflate(R.layout.fragment_verification_bank_external_gateway, container, false)
        webView = root.findViewById(R.id.webView)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWebView()
        observeVerificationStatus()
    }

    override fun initViewModel() {
        super.initViewModel()
        sharedViewModel = ViewModelProvider(requireActivity(), viewModelFactory)
            .get(VerificationBankViewModel::class.java)
        verificationBankExternalGateViewModel =
            ViewModelProvider(requireActivity(), viewModelFactory)
                .get(VerificationBankExternalGateViewModel::class.java)
    }

    private fun initWebView() {
        val bankVerificationUrlEvent =
            verificationBankExternalGateViewModel!!.verificationBankUrl.value!!
        val verificationBankUrl = bankVerificationUrlEvent.content
        Preconditions.checkNotNull(verificationBankUrl)
        webView!!.settings.javaScriptEnabled = true
        enableWebViewDarkModeSupport()
        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val messagePorts = createWebMessageChannel(view!!)
                val nativePort = messagePorts[0]
                nativePort.setWebMessageCallback(object : WebMessageCallbackCompat() {
                    override fun onMessage(port: WebMessagePortCompat, message: WebMessageCompat?) {
                        super.onMessage(port, message)
                        Timber.d("Message from JavaScript received: %s", message)
                        AlertDialog.Builder(requireActivity()).apply {
                            setTitle(de.solarisbank.sdk.core.R.string.identity_dialog_quit_process_title)
                            setMessage(de.solarisbank.sdk.core.R.string.identity_dialog_quit_process_message)
                            setPositiveButton(de.solarisbank.sdk.core.R.string.identity_dialog_quit_process_positive_button) { _, _ ->
                                Timber.d("Quit IdentHub SDK after back button pressed")
                                sharedViewModel?.cancelIdentification()
                            }
                            setNegativeButton(de.solarisbank.sdk.core.R.string.identity_dialog_quit_process_negative_button) { _, _ -> }
                        }.show()
                    }
                })
                val script = """
                    console.log('Init Solarisbank message channel');
                    var solarisbankPort = null;
                    window.addEventListener('message', function(message) {
                        console.log('Received message: ' + message.data);
                        if (message.data != 'capturePort') {
                            var _a, _b, _c, _d, _e, _f;
                            if ((_b = (_a = message === null || message === void 0 ? void 0 : message.data) === null || _a === void 0 ? void 0 : _a.query) === null || _b === void 0 ? void 0 : _b.state) {
                                var state = message.data.query.state;
                                solarisbankPort.postMessage(state);
                            }
                        } else if (message.data == 'capturePort') {
                            if (message.ports[0] != null) {
                                solarisbankPort = message.ports[0];
                            }
                        }
                    }, false);
                """.trimIndent()
                view.evaluateJavascript(script) {
                    Timber.d("Finished init Solarisbank message channel")
                }
                val jsPort = arrayOf(messagePorts[1])
                postWebMessage(view, WebMessageCompat("capturePort", jsPort), Uri.EMPTY)
            }
        }
        webView!!.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                super.onConsoleMessage(consoleMessage)
                Timber.d("JavaScript: %s", consoleMessage?.message())
                return true
            }
        }
        webView!!.loadUrl(verificationBankUrl!!)
    }

    private fun enableWebViewDarkModeSupport() {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(
                        webView!!.settings,
                        WebSettingsCompat.FORCE_DARK_ON
                    )
                }
                WebSettingsCompat.setForceDarkStrategy(
                    webView!!.settings,
                    WebSettingsCompat.DARK_STRATEGY_WEB_THEME_DARKENING_ONLY
                )
            }
        }
    }

    private fun observeVerificationStatus() {
        verificationBankExternalGateViewModel!!.getVerificationResultLiveData().observe(
            viewLifecycleOwner, { result: Result<Any> -> onVerificationStatusChanged(result) })
    }

    private fun onVerificationStatusChanged(result: Result<Any>) {
        if (result is Result.Success<*>) {
            val nextStep = (result as Result.Success<Any?>).nextStep
            sharedViewModel!!.navigateToProcessingVerification(nextStep)
        } else if (result is Result.Error) {
            Timber.d("Could not find verification result")
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return VerificationBankExternalGatewayFragment()
        }
    }
}
