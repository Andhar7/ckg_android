package com.andrejkling.ckg.ui.screens.books

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.andrejkling.ckg.ui.theme.Accent
import com.andrejkling.ckg.ui.theme.Unity

private const val LIBRARY_URL = "https://www.srichinmoylibrary.com"

// Sri Chinmoy library inside the app, like the iOS LibraryBrowserView.
// Back appears only when a page can go back; Close is always on the right.
// "Create book" closes the library and opens the form.
@Composable
fun LibraryScreen(onClose: () -> Unit, onCreate: () -> Unit) {
    var web by remember { mutableStateOf<WebView?>(null) }
    var canGoBack by remember { mutableStateOf(false) }

    BackHandler {
        val w = web
        if (w != null && w.canGoBack()) w.goBack() else onClose()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Unity)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back appears only when there is a previous page; its space is kept so the title stays centred
            Text(
                text = "‹ Back",
                color = Accent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                modifier = Modifier
                    .width(80.dp)
                    .clickable(enabled = canGoBack) { web?.goBack() }
                    .alpha(if (canGoBack) 1f else 0f)
            )
            Text(
                text = "Sri Chinmoy Library",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Close",
                color = Accent,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.width(80.dp).clickable(onClick = onClose)
            )
        }

        // The page takes the space that is left; the hint and Create book stay visible below it
        LibraryWebView(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            onCreated = { web = it },
            onPageChanged = { canGoBack = it.canGoBack() }
        )

        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Select the text you like, copy it, then tap Create book.",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Create book",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color(0xFF0F2A85))
                    .clickable(onClick = onCreate)
                    .padding(vertical = 14.dp)
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun LibraryWebView(
    modifier: Modifier,
    onCreated: (WebView) -> Unit,
    onPageChanged: (WebView) -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.setSupportMultipleWindows(true)
                // Links that open a new window load here instead, so Back keeps working
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        view?.let(onPageChanged)
                    }
                }
                webChromeClient = object : android.webkit.WebChromeClient() {
                    override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message?): Boolean {
                        val transport = resultMsg?.obj as? WebView.WebViewTransport ?: return false
                        transport.webView = view
                        resultMsg.sendToTarget()
                        return true
                    }
                }
                loadUrl(LIBRARY_URL)
                onCreated(this)
            }
        }
    )
}
