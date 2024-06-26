package ru.arturprgr.mybrowser.classes

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.WebView
import android.webkit.WebViewClient

class WebClient(val context: Context) : WebViewClient() {
    val preferences = Preferences(context)

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val reference = "${preferences.getAccount()}/history"
        val title = view!!.title.toString()
        preferences.setQuantityHistory(preferences.getQuantityHistory() + 1)
        Database("$reference/${preferences.getQuantityHistory()}/name").setValue(title)
        Database("$reference/${preferences.getQuantityHistory()}/url").setValue(url!!)
        Database("$reference/${preferences.getQuantityHistory()}/usage").setValue( true)
        Database("$reference/quantity").setValue(preferences.getQuantityHistory())
    }

    fun downloadFile(
        name: String,
        url: String,
        path: String,
    ) {
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(name)
        request.setDescription(name)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
        when (path) {
            "Download" -> {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name)
            }

            "Documents" -> {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, name)
            }

            "Music" -> {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, name)
            }

            "Pictures" -> {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, name)
            }

            "Movies" -> {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, name)
            }
        }
    }
}