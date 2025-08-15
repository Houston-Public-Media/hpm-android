package org.houstonpublicmedia.hpmandroid

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebView.setWebContentsDebuggingEnabled
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun WatchScreen() {
    // Declare a string that contains a url
    val mUrl = "https://cdn.houstonpublicmedia.org/assets/watch-live.html"
    val context = LocalContext.current
    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.userAgentString = "Jared's Super Cool Chrome Android Thingy"
            //setWebContentsDebuggingEnabled(true)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = WebViewClient() // Handle link clicks within the WebView
        }
    }
    // Adding a WebView inside AndroidView
    // with layout as full screen
    AndroidView(
        factory = { webView },
        update = { view ->
            view.loadUrl(mUrl)
        }
    )
}