package de.solarisbank.sdk.fourthline.feature.ui.webview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import androidx.fragment.app.Fragment
import de.solarisbank.sdk.fourthline.R

class WebViewFragment : Fragment() {

    private var webView: WebView? = null
    private var closeButton: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false).also {
            webView = it.findViewById(R.id.webView)
            webView!!.setWebViewClient(object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    return false
                }
            })

            closeButton = it.findViewById(R.id.closeButton)
            closeButton!!.setOnClickListener { requireActivity().onBackPressed() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(WEB_VIEW_URL_KEY)?.let {
            webView!!.loadUrl(it)
        }
    }

    override fun onDestroy() {
        webView = null
        closeButton = null
        super.onDestroy()
    }

    companion object {
        val WEB_VIEW_URL_KEY = "WEB_VIEW_URL_KEY"
    }
}