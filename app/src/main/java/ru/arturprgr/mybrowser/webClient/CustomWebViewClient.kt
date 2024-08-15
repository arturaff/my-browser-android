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
import ru.arturprgr.mybrowser.data.Preferences
import ru.arturprgr.mybrowser.databinding.ActivityWebBinding


class CustomWebViewClient(val context: Context, val binding: ActivityWebBinding) : WebViewClient() {
    val preferences = Preferences(context)

    override fun onPageCommitVisible(view: WebView?, url: String?) {
        super.onPageCommitVisible(view, url)
        Log.d("Attempt", url!!)
        view!!.visibility = View.VISIBLE
        Log.d("Attempt", "canGoBack - ${view.canGoBack()}, canGoForward - ${view.canGoForward()}")
        if ((!view.canGoBack() || !view.canGoForward()) && (view.canGoBack() || !view.canGoForward())) {
            val reference = "${preferences.getAccount()}/history"
            val title = view.title.toString()
            preferences.setQuantityHistory(preferences.getQuantityHistory() + 1)
            FirebaseHelper("$reference/quantity").setValue(preferences.getQuantityHistory())
            FirebaseHelper("$reference/${preferences.getQuantityHistory()}/name").setValue(title)
            FirebaseHelper("$reference/${preferences.getQuantityHistory()}/url").setValue(url)
            FirebaseHelper("$reference/${preferences.getQuantityHistory()}/usage").setValue(true)
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

            ERROR_TIMEOUT -> viewErrorPage(
                R.drawable.ic_timeout, "Время подключения истекло", "Загрузка страницы окончено"
            ) { button ->
                button.text = "Попробовать ещё"
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

            ERROR_FAILED_SSL_HANDSHAKE -> viewErrorPage(
                R.drawable.ic_safety,
                "Не удалось выполнить SSL-квитирование.",
                "Вы не как не сможете исправить эту ошибку. Ждите, когда разработчики сайта его продлят"
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
                "Сайт перегружен! Подождите немного, пока запросов станет поменьше."
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
                "Выйдите на главную и напишите его верно"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_UNSUPPORTED_SCHEME -> viewErrorPage(
                R.drawable.ic_error,
                "Неподдерживаемой схема URL",
                "Выйдите на главную и напишите его верно"
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

            ERROR_UNSUPPORTED_AUTH_SCHEME -> viewErrorPage(
                R.drawable.ic_error,
                "Неподдерживаемая схемы аутентификации",
                "Неподдерживаемая схема аутентификации (не базовая и не дайджест)"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_AUTHENTICATION -> viewErrorPage(
                R.drawable.ic_error,
                "Ошибка аутентификации",
                "Ошибка аутентификации пользователя на сервере"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_PROXY_AUTHENTICATION -> viewErrorPage(
                R.drawable.ic_error,
                "Ошибка прокси-аутентификации",
                "Ошибка аутентификации пользователя на прокси-сервере"
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
    }
}