package ru.arturprgr.mybrowser.ui.fragment.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.arturprgr.mybrowser.databinding.FragmentMainBinding
import ru.arturprgr.mybrowser.getDefaultBoolean
import ru.arturprgr.mybrowser.getDefaultString
import ru.arturprgr.mybrowser.ui.activity.SettingsActivity
import ru.arturprgr.mybrowser.ui.activity.WeatherActivity
import ru.arturprgr.mybrowser.ui.activity.WebActivity
import java.io.IOException

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var sSystem: String
    private lateinit var doc: Document
    private var query: String = ""

    @RequiresApi(Build.VERSION_CODES.Q)
    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        sSystem = getDefaultString(requireContext(), "def_search_system")

        Log.d("Attempt", getDefaultBoolean(requireContext(), "bottom_panel_tools").toString())

        binding.apply {
            val sSystem = getDefaultString(requireContext(), "def_search_system")
            if (sSystem != "") spinnerSystem.visibility = View.GONE
            else editQuery.updateLayoutParams<ConstraintLayout.LayoutParams> {
                this.topMargin = 0
                this.bottomMargin = 0
                this.rightMargin = 0
                this.leftMargin = 0
            }


            layoutWeather.setOnClickListener {
                startActivity(Intent(requireContext(), WeatherActivity::class.java))
            }

            buttonSettings.setOnClickListener {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }

            editQuery.setOnEditorActionListener { _, actionId, event ->
                if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH) search()
                true
            }

            buttonQuery.setOnClickListener {
                search()
            }

            if (!getDefaultBoolean(requireContext(), "disabled_weather")) {
                val handler = @SuppressLint("HandlerLeak") object : Handler() {
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        textWeather.text = msg.data.getString("weather").toString()
                        textLocation.text = msg.data.getString("location").toString()
                    }
                }
                Thread {
                    try {
                        doc = if (!getDefaultBoolean(requireContext(), "auto_location")) {
                            Jsoup.connect("https://www.google.com/search?q=погода+сегодня").get()
                        } else {
                            Jsoup.connect(
                                "https://www.google.com/search?q=погода+в+${
                                    getDefaultString(
                                        requireContext(),
                                        "def_location"
                                    )
                                }+сегодня"
                            ).get()
                        }

                        val msg = handler.obtainMessage()
                        val bundle = Bundle()
                        bundle.putString(
                            "weather", "${
                                doc.getElementById("wob_tm")?.text()
                            }°C, ${doc.getElementById("wob_dc")?.text()}"
                        )
                        bundle.putString("location", doc.getElementsByClass("BBwThe")[2].text())
                        msg.data = bundle
                        handler.sendMessage(msg)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }.start()
            } else layoutWeather.visibility = View.GONE
        }

        return binding.root
    }

    private fun search() = with(binding) {
        query = editQuery.text.toString()
        if (query != "")
            if (spinnerSystem.visibility == View.VISIBLE) {
                if (query != "") when ("${spinnerSystem.selectedItem}") {
                    "Google" -> searchFromSearchDomain("https://www.google.com/search?q=")
                    "Яндекс" -> searchFromSearchDomain("https://ya.ru/search/?text=")
                    "DuckDuckGo" -> searchFromSearchDomain("https://duckduckgo.com/q=")
                    "Bing" -> searchFromSearchDomain("https://www.bing.com/search?q=")
                    "Yahoo!" -> searchFromSearchDomain("https://search.yahoo.com/search?p=")
                }
            } else searchFromSearchDomain(sSystem)
    }

    private fun searchFromSearchDomain(searchSystem: String) {
        requireContext().startActivity(
            Intent(requireContext(), WebActivity::class.java).putExtra("url", "$searchSystem$query")
                .putExtra("name", query)
        )
        binding.editQuery.setText("")
    }


}