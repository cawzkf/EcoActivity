package com.ecoactivity.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.ecoactivity.app.R

class EducacionalFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_educacional, container, false)

        val webViewNoticias: WebView = view.findViewById(R.id.webViewNoticias)


        val url = "https://news.google.com/search?q=consumo+residencial+eficiencia+energetica&hl=pt-BR"



        webViewNoticias.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportZoom(false)
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            webViewClient = WebViewClient()
            loadUrl(url)
        }

        return view
    }
}
