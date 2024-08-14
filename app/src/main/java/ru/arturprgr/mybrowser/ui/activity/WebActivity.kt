package ru.arturprgr.mybrowser.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.data.FirebaseHelper
import ru.arturprgr.mybrowser.data.Preferences
import ru.arturprgr.mybrowser.databinding.ActivityWebBinding
import ru.arturprgr.mybrowser.makeMessage
import ru.arturprgr.mybrowser.webClient.CustomWebChromeClient
import ru.arturprgr.mybrowser.webClient.CustomWebViewClient

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    private lateinit var webViewClient: CustomWebViewClient
    private lateinit var webChromeClient: CustomWebChromeClient
    private lateinit var preferences: Preferences
    private lateinit var dirsAdapter: ArrayAdapter<CharSequence>
    private lateinit var collectionsAdapter: ArrayAdapter<String>
    private lateinit var collectionsArrayList: ArrayList<String>

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        webViewClient = CustomWebViewClient(this@WebActivity, binding)
        webChromeClient = CustomWebChromeClient(binding)
        preferences = Preferences(this@WebActivity)
        collectionsArrayList = arrayListOf()
        dirsAdapter = ArrayAdapter.createFromResource(
            this@WebActivity, R.array.dirs, android.R.layout.simple_spinner_item
        )
        dirsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        for (index in 0..preferences.getQuantityCollections()) if (preferences.getQuantityCollections() == 0) collectionsAdapter =
            ArrayAdapter<String>(
                this@WebActivity,
                android.R.layout.simple_spinner_dropdown_item,
                arrayListOf("*Нет коллекций*")
            )
        else FirebaseHelper("${preferences.getAccount()}/collections/$index/usage").getValue { usage ->
            if (usage.toBoolean()) FirebaseHelper("${preferences.getAccount()}/collections/$index/name").getValue { name ->
                collectionsArrayList.add(name)
                collectionsAdapter = ArrayAdapter<String>(
                    this@WebActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    collectionsArrayList
                )
            }
        }

        setContentView(binding.root)

        binding.apply {
            webView.settings.javaScriptEnabled = true
            webView.settings.defaultTextEncodingName = "utf-8"
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
                        val reference = "${preferences.getAccount()}/downloads"
                        webViewClient
                            .downloadFile(
                                name[name.size - 1],
                                url.toString(),
                                spinnerDir.selectedItem.toString()
                            )
                        preferences.setQuantityDownloads(preferences.getQuantityDownloads() + 1)
                        FirebaseHelper("$reference/quantity")
                            .setValue(preferences.getQuantityDownloads())
                        FirebaseHelper(
                            "$reference/${
                                preferences
                                    .getQuantityDownloads()
                            }/name"
                        ).setValue(name[name.size - 1])
                        FirebaseHelper(
                            "$reference/${
                                preferences
                                    .getQuantityDownloads()
                            }/path"
                        ).setValue("Скачано в папку ${spinnerDir.selectedItem}")
                        FirebaseHelper(
                            "$reference/${
                                preferences
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

            buttonMain.setOnClickListener {
                startActivity(Intent(this@WebActivity, MainActivity::class.java))
            }

            buttonGoMain.setOnClickListener {
                startActivity(Intent(this@WebActivity, MainActivity::class.java))
            }

            buttonReload.setOnClickListener {
                webView.reload()
                layoutWebView.visibility = View.VISIBLE
                layoutError.visibility = View.GONE
            }

            buttonAddInCollection.setOnClickListener {
                val linearLayout = LinearLayout(this@WebActivity)
                val spinnerCollections = Spinner(this@WebActivity)
                val editName = EditText(this@WebActivity)
                spinnerCollections.adapter = collectionsAdapter
                editName.hint = "Название элемента"
                editName.setText("${webView.title}")
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.addView(spinnerCollections)
                linearLayout.addView(editName)

                AlertDialog.Builder(this@WebActivity)
                    .setTitle("Добавление элемента в коллекцию")
                    .setMessage("Выберите коллекцию и по желанию измените название элемента")
                    .setView(linearLayout)
                    .setPositiveButton("Добавить") { _, _ ->
                        val reference =
                            "${preferences.getAccount()}/collections/${spinnerCollections.selectedItemPosition + 1}"
                        val quantity = 1
                        preferences.setPreference("quantity$spinnerCollections", quantity)
                        FirebaseHelper("$reference/$quantity/title")
                            .setValue(editName)
                        FirebaseHelper("$reference/$quantity/url")
                            .setValue("${webView.url}")
                        FirebaseHelper("$reference/$quantity/usage")
                            .setValue(true)
                        FirebaseHelper("$reference/quantity")
                            .setValue(quantity)
                    }
                    .create()
                    .show()

                linearLayout.updateLayoutParams<FrameLayout.LayoutParams> {
                    this.topMargin = 16
                    this.leftMargin = 48
                    this.rightMargin = 48
                    this.bottomMargin = 16
                }
            }

            buttonAddBookmark.setOnClickListener {
                val editName = EditText(this@WebActivity)
                editName.hint = "Название закладки"
                editName.setText("${webView.title}")

                AlertDialog.Builder(this@WebActivity).setTitle("Добавить закладку")
                    .setMessage("Введите имя закладки и URL, затем нажмите добавить")
                    .setView(editName).setPositiveButton("Добавить") { _: DialogInterface, _: Int ->
                        preferences.setQuantityBookmarks(preferences.getQuantityBookmarks() + 1)
                        val name = editName.text.toString()
                        val url = webView.url.toString()
                        val reference =
                            "${preferences.getAccount()}/bookmarks/${preferences.getQuantityBookmarks()}"
                        FirebaseHelper("$reference/usage").setValue(true)
                        FirebaseHelper("$reference/name").setValue(name)
                        FirebaseHelper("$reference/url").setValue(url)
                        FirebaseHelper("${preferences.getAccount()}/bookmarks/quantity").setValue(
                            preferences.getQuantityBookmarks()
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
    }

    @Suppress("DEPRECATION")
    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        val webView = binding.webView
        if (webView.canGoBack()) webView.goBack() else super.onBackPressed()
    }
}