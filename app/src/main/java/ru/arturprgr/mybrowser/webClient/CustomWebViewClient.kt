package ru.arturprgr.mybrowser.webClient

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.SavesHelper
import ru.arturprgr.mybrowser.data.Singleton
import ru.arturprgr.mybrowser.databinding.ActivityWebBinding
import ru.arturprgr.mybrowser.model.Card
import ru.arturprgr.mybrowser.ui.fragment.main.DownloadsFragment
import ru.arturprgr.mybrowser.ui.fragment.main.HistoryFragment


class CustomWebViewClient(val context: Context, val binding: ActivityWebBinding) : WebViewClient() {
    private val savesHelper = SavesHelper(context)

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
        view!!.visibility = View.VISIBLE
        Log.d("Attempt", "canGoBack - ${view.canGoBack()}, canGoForward - ${view.canGoForward()}")
        if ((!view.canGoBack() || !view.canGoForward()) && (view.canGoBack() || !view.canGoForward())) {
            val reference = "${savesHelper.getAccount()}/history"
            savesHelper.setQuantityHistory(savesHelper.getQuantityHistory() + 1)
            FirebaseHelper("$reference/quantity").setValue(savesHelper.getQuantityHistory())
            FirebaseHelper("$reference/${savesHelper.getQuantityHistory()}/name").setValue(view.title)
            FirebaseHelper("$reference/${savesHelper.getQuantityHistory()}/url").setValue(url)
            FirebaseHelper("$reference/${savesHelper.getQuantityHistory()}/usage").setValue(true)
            Singleton.historyAdapter.addQuery(Card(HistoryFragment.context, "${view.title}", "$url", Singleton.historyAdapter.getSize() + 1))
        }
    }


    @SuppressLint("NewApi")
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?,
    ) {
        super.onReceivedError(view, request, error)

        try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            @Suppress("DEPRECATION")
            cm.activeNetworkInfo!!.isConnected
        } catch (_: NullPointerException) {
            viewErrorPage(
                R.drawable.ic_connect,
                "Нет подключения к Интернету",
                "Проверьте его и попробуйте перезагрузить или выйдите на главную"
            ) { button ->
                button.text = "Перезагрузить"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }
        }

        when (error!!.errorCode) {
            ERROR_IO -> viewErrorPage(
                R.drawable.ic_no_internet,
                "Не удалось прочитать или записать на сервер",
                "Попробуйте перезагрузить или выйдите на главную"
            ) { button ->
                button.text = "Перезагрузить"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_UNSAFE_RESOURCE -> viewErrorPage(
                R.drawable.ic_safety,
                "Загрузка ресурса была отменена безопасным просмотром",
                " Страница небезопасна! Лучше всего ее не посещать"
            ) { button ->
                button.text = "Все равно перейти"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_FILE_NOT_FOUND -> viewErrorPage(
                R.drawable.ic_error,
                "Файл не найден",
                "Попробуйте перезагрузить или выйдите на главную"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_TOO_MANY_REQUESTS -> viewErrorPage(
                R.drawable.ic_error,
                "Слишком много запросов во время этой загрузки",
                "Сайт перегружен запросами. Подождите пока они спадут"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_REDIRECT_LOOP -> viewErrorPage(
                R.drawable.ic_error,
                "Слишком много перенаправлений",
                "Сайты перенаправляют вас с одного на другой"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_BAD_URL -> viewErrorPage(
                R.drawable.ic_error, "Неверный URL-адрес", "Выйдите на главную и напишите его верно"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }
        }
    }

    private fun viewErrorPage(
        image: Int,
        title: String,
        cause: String,
        onClick: (button: Button) -> Unit,
    ) = with(binding) {
        layoutWebView.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        icon.setImageResource(image)
        textTitle.text = title
        textCause.text = cause
        onClick(buttonActions)
    }

    fun downloadFile(
        name: String,
        url: String,
        path: String,
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle(name)
        request.setDescription(name)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager.enqueue(request)
        when (path) {
            "Download" -> request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS, name
            )

            "Documents" -> request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOCUMENTS, name
            )

            "Music" -> request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MUSIC, name
            )

            "Pictures" -> request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_PICTURES, name
            )

            "Movies" -> request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MOVIES, name
            )
        }
        Singleton.downloadsAdapter.addDownload(Card(DownloadsFragment.context, name, path, Singleton.downloadsAdapter.getSize() + 1))
    }
}