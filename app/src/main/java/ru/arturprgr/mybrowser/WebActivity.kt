package ru.arturprgr.mybrowser

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.google.firebase.Firebase
import com.google.firebase.database.database
import ru.arturprgr.mybrowser.databinding.ActivityWebBinding
import ru.arturprgr.mybrowser.model.setValue
import ru.arturprgr.mybrowser.model.viewToast

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    private lateinit var webClient: WebClient

    @SuppressLint("SetJavaScriptEnabled")
    @Suppress("NAME_SHADOWING")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webClient = WebClient(this@WebActivity)
        val preferences = Preferences(this@WebActivity)

        binding.apply {
            val url = intent!!.getStringExtra("url").toString()
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.defaultTextEncodingName = "utf-8"
            webView.setWebViewClient(webClient)
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    textQuery.text = view!!.title
                    progressBar.progress = newProgress
                    if (progressBar.progress == 100) progressBar.visibility = View.GONE
                    else progressBar.visibility = View.VISIBLE
                    super.onProgressChanged(view, newProgress)
                }
            }
            webView.loadUrl(url)

            webView.setDownloadListener { url, _, _, _, _ ->
                val spinnerDir = Spinner(this@WebActivity)
                val adapter = ArrayAdapter.createFromResource(
                    this@WebActivity, R.array.dirs, android.R.layout.simple_spinner_item
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDir.adapter = adapter
                AlertDialog.Builder(this@WebActivity).setTitle("Загрузка файла")
                    .setMessage("Выбирите место куда сохранить файл").setView(spinnerDir)
                    .setPositiveButton("Скачать") { _, _ ->
                        val name = url.split("/")
                        val reference = "${preferences.getAccount()}/downloads"
                        webClient.downloadFile(
                            name[name.size - 1],
                            url.toString(),
                            spinnerDir.selectedItem.toString()
                        )
                        preferences.setQuantityDownloads(preferences.getQuantityDownloads() + 1)
                        setValue(
                            "$reference/quantity",
                            preferences.getQuantityDownloads()
                        )
                        setValue(
                            "$reference/${preferences.getQuantityDownloads()}/name",
                            name[name.size - 1]
                        )
                        setValue(
                            "$reference/${preferences.getQuantityDownloads()}/path",
                            "Скачано в папку ${spinnerDir.selectedItem}"
                        )
                        setValue(
                            "$reference/${preferences.getQuantityDownloads()}/usage",
                            true
                        )

                        viewToast(this@WebActivity, "Начало загрузки")
                    }.create().show()

                spinnerDir.updateLayoutParams<FrameLayout.LayoutParams> {
                    this.leftMargin = 48
                    this.rightMargin = 48
                    this.topMargin = 16
                    this.bottomMargin = 16
                }
            }

            buttonMain.setOnClickListener {
                startActivity(Intent(this@WebActivity, MainActivity::class.java))
            }

            buttonAddBookmark.setOnClickListener {
                val editName = EditText(this@WebActivity)
                editName.hint = "Название закладки"
                editName.setText("${webView.title}")

                AlertDialog.Builder(this@WebActivity).setTitle("Добавить закладку")
                    .setMessage("Введите имя закладки и URL, затем нажмите добавить")
                    .setView(editName)
                    .setPositiveButton("Добавить") { _: DialogInterface, _: Int ->
                        preferences.setQuantityBookmarks(preferences.getQuantityBookmarks() + 1)
                        val name = editName.text.toString()
                        val url = webView.url.toString()
                        val reference = "${preferences.getAccount()}/bookmarks"
                        Firebase.database.getReference("$reference/${preferences.getQuantityBookmarks()}/usage")
                            .setValue(true)
                        Firebase.database.getReference("$reference/${preferences.getQuantityBookmarks()}/name")
                            .setValue(name)
                        Firebase.database.getReference("$reference/${preferences.getQuantityBookmarks()}/url")
                            .setValue(url)
                        Firebase.database.getReference("$reference/quantity")
                            .setValue(preferences.getQuantityBookmarks())
                    }.create().show()

                editName.updateLayoutParams<FrameLayout.LayoutParams> {
                    this.leftMargin = 48
                    this.rightMargin = 48
                    this.topMargin = 16
                    this.bottomMargin = 16
                }
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
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}