package ru.arturprgr.mybrowser.webClient

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
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

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view!!.visibility = View.VISIBLE
        val reference = "${preferences.getAccount()}/history"
        val title = view.title.toString()
        preferences.setQuantityHistory(preferences.getQuantityHistory() + 1)
        FirebaseHelper("$reference/${preferences.getQuantityHistory()}/name").setValue(title)
        FirebaseHelper("$reference/${preferences.getQuantityHistory()}/url").setValue(url!!)
        FirebaseHelper("$reference/${preferences.getQuantityHistory()}/usage").setValue(true)
        FirebaseHelper("$reference/quantity").setValue(preferences.getQuantityHistory())
    }

    @SuppressLint("NewApi")
    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?,
    ) {
        super.onReceivedError(view, request, error)
        when (error!!.errorCode) {
            ERROR_HOST_LOOKUP -> viewErrorPage(
                R.drawable.ic_no_internet,
                "Не получается загрузить страницу",
                "Проблема с подключением к интернету"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_UNSAFE_RESOURCE -> viewErrorPage(
                R.drawable.ic_safety,
                "Страница небезопасна!",
                "Не советуем переходить по этой ссылке! Она может быть небезопасна."
            ) { button ->
                button.text = "Все равно перейти"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_TIMEOUT -> viewErrorPage(
                R.drawable.ic_timeout,
                "Загрузка страницы окончено",
                "Время на загрузку страницы вышло. Возможно, что он заблокирован"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_UNKNOWN -> {
                viewErrorPage(
                    R.drawable.ic_error,
                    "Неизвестная ошибка",
                    "Попробуйте перезагрузить или выйдите на главную"
                ) { button ->
                    button.text = "Попробовать ещё"
                    button.setOnClickListener {
                        view!!.reload()
                        binding.layoutWebView.visibility = View.VISIBLE
                        binding.layoutError.visibility = View.GONE
                    }
                }
            }

            ERROR_FILE_NOT_FOUND -> viewErrorPage(
                R.drawable.ic_error,
                "Файл не найден",
                "Не удалось найти файл"
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
                "У страницы истек срок SSL-сертификата",
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
                "У сайта слишком много запросов",
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
                "Ошибка цикла перенаправления",
                "Попробуйте перезагрузить или выйдите на главную"
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
                "Ошибка неподдерживаемой схемы",
                "Попробуйте перезагрузить или выйдите на главную"
            ) { button ->
                button.text = "Попробовать ещё"
                button.setOnClickListener {
                    view!!.reload()
                    binding.layoutWebView.visibility = View.VISIBLE
                    binding.layoutError.visibility = View.GONE
                }
            }

            ERROR_BAD_URL -> viewErrorPage(
                R.drawable.ic_error,
                "Введен неверный URL!",
                "Выйдите на главную и напишите его верно"
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
                "Ошибка неподдерживаемой схемы авторизации",
                "Попробуйте перезагрузить или выйдите на главную"
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
                "Попробуйте перезагрузить или выйдите на главную"
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
                "Попробуйте перезагрузить или выйдите на главную"
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
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
                Environment.DIRECTORY_MUSIC,
                name
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