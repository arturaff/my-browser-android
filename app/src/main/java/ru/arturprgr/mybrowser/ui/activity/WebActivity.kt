package ru.arturprgr.mybrowser.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.SavesHelper
import ru.arturprgr.mybrowser.data.Singleton
import ru.arturprgr.mybrowser.databinding.ActivityWebBinding
import ru.arturprgr.mybrowser.getDefaultBoolean
import ru.arturprgr.mybrowser.makeMessage
import ru.arturprgr.mybrowser.model.Card
import ru.arturprgr.mybrowser.ui.fragment.main.BookmarksFragment
import ru.arturprgr.mybrowser.webClient.CustomWebChromeClient
import ru.arturprgr.mybrowser.webClient.CustomWebViewClient

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    private lateinit var webViewClient: CustomWebViewClient
    private lateinit var webChromeClient: CustomWebChromeClient
    private lateinit var savesHelper: SavesHelper
    private lateinit var dirsAdapter: ArrayAdapter<CharSequence>

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        webViewClient = CustomWebViewClient(this@WebActivity, binding)
        webChromeClient = CustomWebChromeClient(binding)
        savesHelper = SavesHelper(this@WebActivity)
        dirsAdapter = ArrayAdapter.createFromResource(
            this@WebActivity, R.array.dirs, android.R.layout.simple_spinner_item
        )
        dirsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        setContentView(binding.root)

        binding.apply {
            if (getDefaultBoolean(this@WebActivity, "bottom_panel_tools")) {
                buttonMainBottom.setOnClickListener {
                    goMainActivity()
                }

                buttonReloadBottom.setOnClickListener {
                    reload()
                }

                buttonAddBookmarkBottom.setOnClickListener {
                    addBookmark()
                }

                layoutToolTop.visibility = View.GONE
                layoutWebView.updateLayoutParams<FrameLayout.LayoutParams> {
                    this.topMargin = 0
                    this.rightMargin = 0
                    this.leftMargin = 0
                    this.bottomMargin = 135
                }
            } else {
                buttonMainTop.setOnClickListener {
                    goMainActivity()
                }

                buttonReloadTop.setOnClickListener {
                    reload()
                }

                buttonAddBookmarkTop.setOnClickListener {
                    addBookmark()
                }

                layoutToolBottom.visibility = View.GONE
                layoutWebView.updateLayoutParams<FrameLayout.LayoutParams> {
                    this.topMargin = 140
                    this.rightMargin = 0
                    this.leftMargin = 0
                    this.bottomMargin = 0
                }
            }
            webView.settings.javaScriptEnabled = true
            webView.settings.defaultTextEncodingName = "utf-8"
            webView.settings.userAgentString =
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36"
            webView.setWebViewClient(webViewClient)
            webView.setWebChromeClient(webChromeClient)
            webView.loadUrl(intent!!.getStringExtra("url").toString())
            webView.setDownloadListener { url, _, _, _, _ ->
                val spinnerDir = Spinner(this@WebActivity)
                spinnerDir.adapter = dirsAdapter

                AlertDialog.Builder(this@WebActivity)
                    .setTitle("Загрузка файла")
                    .setMessage("Выбирите место куда сохранить файл").setView(spinnerDir)
                    .setView(spinnerDir)
                    .setPositiveButton("Скачать") { _, _ ->
                        val name = url.split("/")
                        val reference = "${savesHelper.getAccount()}/downloads"
                        webViewClient
                            .downloadFile(
                                name[name.size - 1],
                                url.toString(),
                                spinnerDir.selectedItem.toString()
                            )
                        savesHelper.setQuantityDownloads(savesHelper.getQuantityDownloads() + 1)
                        FirebaseHelper("$reference/quantity")
                            .setValue(savesHelper.getQuantityDownloads())
                        FirebaseHelper(
                            "$reference/${
                                savesHelper
                                    .getQuantityDownloads()
                            }/name"
                        ).setValue(name[name.size - 1])
                        FirebaseHelper(
                            "$reference/${
                                savesHelper
                                    .getQuantityDownloads()
                            }/path"
                        ).setValue("Скачано в папку ${spinnerDir.selectedItem}")
                        FirebaseHelper(
                            "$reference/${
                                savesHelper
                                    .getQuantityDownloads()
                            }/usage"
                        ).setValue(true)
                        makeMessage(this@WebActivity, "Начало загрузки")
                    }
                    .create()
                    .show()

                spinnerDir.updateLayoutParams<FrameLayout.LayoutParams> {
                    this.topMargin = 16
                    this.leftMargin = 48
                    this.rightMargin = 48
                    this.bottomMargin = 16
                }
            }

            buttonGoMain.setOnClickListener {
                if (webView.canGoBack()) webView.goBack()
                else goMainActivity()
            }
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        val webView = binding.webView
        if (webView.canGoBack()) webView.goBack() else {
            super.onBackPressed()
            startActivity(
                Intent(
                    this@WebActivity,
                    MainActivity::class.java
                )
            )
        }
    }

    private fun goMainActivity() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }

    private fun reload() = with(binding) {
        webView.reload()
        layoutWebView.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
    }

    private fun addBookmark() = with(binding) {
        val editName = EditText(this@WebActivity)
        editName.hint = "Название закладки"
        editName.setText("${webView.title}")

        AlertDialog.Builder(this@WebActivity).setTitle("Добавить закладку")
            .setMessage("Введите имя закладки и URL, затем нажмите добавить")
            .setView(editName)
            .setPositiveButton("Добавить") { _: DialogInterface, _: Int ->
                savesHelper.setQuantityBookmarks(savesHelper.getQuantityBookmarks() + 1)
                val reference =
                    "${savesHelper.getAccount()}/bookmarks/${savesHelper.getQuantityBookmarks()}"
                FirebaseHelper("${savesHelper.getAccount()}/bookmarks/quantity").setValue(
                    savesHelper.getQuantityBookmarks()
                )
                FirebaseHelper("$reference/usage").setValue(true)
                FirebaseHelper("$reference/name").setValue("${editName.text}")
                FirebaseHelper("$reference/url").setValue("${webView.url}")
                Singleton.bookmarksAdapter.addBookmark(
                    Card(
                        BookmarksFragment.context,
                        "${editName.text}",
                        "${webView.url}",
                        Singleton.bookmarksAdapter.getSize() + 1
                    )
                )
            }
            .create()
            .show()

        editName.updateLayoutParams<FrameLayout.LayoutParams> {
            this.leftMargin = 48
            this.rightMargin = 48
            this.topMargin = 16
            this.bottomMargin = 16
        }
    }
}