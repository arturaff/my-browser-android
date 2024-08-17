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
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.arturprgr.mybrowser.databinding.FragmentMainBinding
import ru.arturprgr.mybrowser.getDefaultBoolean
import ru.arturprgr.mybrowser.getDefaultString
import ru.arturprgr.mybrowser.makeMessage
import ru.arturprgr.mybrowser.ui.activity.SettingsActivity
import ru.arturprgr.mybrowser.ui.activity.WeatherActivity
import ru.arturprgr.mybrowser.ui.activity.WebActivity
import ru.arturprgr.mybrowser.weather

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var sSystem: String
    private lateinit var doc: Document
    private lateinit var msg: Message
    private var bundle = Bundle()
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

        binding.apply {
            val sSystem = getDefaultString(requireContext(), "def_search_system")

            if (sSystem != "") spinnerSystem.visibility = View.GONE
            else editQuery.updateLayoutParams<ConstraintLayout.LayoutParams> {
                this.topMargin = 0
                this.bottomMargin = 0
                this.rightMargin = 0
                this.leftMargin = 0
            }

            if (weather.size == 0) {
                if (!getDefaultBoolean(requireContext(), "disabled_weather")) {
                    val handler = @SuppressLint("HandlerLeak") object : Handler() {
                        override fun handleMessage(msg: Message) {
                            super.handleMessage(msg)
                            if (!msg.data.getBoolean("error")) {
                                weather.add(msg.data.getString("degrees").toString())
                                weather.add(msg.data.getString("max").toString())
                                weather.add(msg.data.getString("min").toString())
                                weather.add(msg.data.getString("state").toString())
                                weather.add(msg.data.getString("location").toString())
                                weather.add(msg.data.getString("precipitation").toString())
                                weather.add(msg.data.getString("humidity").toString())
                                weather.add(msg.data.getString("wind").toString())
                                weather.add(msg.data.getString("drawable").toString())
                                textWeather.text = "${weather[0]}, ${weather[3]}"
                                textLocation.text = weather[4]
                                Log.d("Attempt", weather.toString())
                            } else makeMessage(
                                requireContext(),
                                "Не удалось получить информацию  о погоде"
                            )
                        }
                    }
                    Thread {
                        try {
                            doc = if (!getDefaultBoolean(requireContext(), "auto_location")) {
                                Jsoup.connect("https://www.google.com/search?q=погода+сегодня")
                                    .userAgent("Chrome/4.0.249.0 Safari/532.5").get()
                            } else {
                                Jsoup.connect(
                                    "https://www.google.com/search?q=погода+в+${
                                        getDefaultString(
                                            requireContext(), "def_location"
                                        )
                                    }+сегодня"
                                ).userAgent("Chrome/4.0.249.0 Safari/532.5").get()
                            }
                            Log.d("Attempt", doc.toString())
                            msg = handler.obtainMessage()
                            bundle.also {
                                val wobT = doc.getElementsByClass("wob_t")
                                it.putString(
                                    "degrees", "${doc.getElementById("wob_tm")?.text()}°C"
                                )
                                Log.d("Attempt", "${doc.getElementById("wob_tm")?.text()}°C")
                                it.putString(
                                    "max", "${wobT[0].text()}°C"
                                )
                                it.putString(
                                    "min", "${wobT[10].text()}°C"
                                )
                                it.putString(
                                    "state", doc.getElementById("wob_dc")!!.text()
                                )
                                it.putString(
                                    "location", doc.getElementsByClass("BBwThe")[2].text()
                                )
                                it.putString(
                                    "precipitation",
                                    "Вероятность осадков: ${doc.getElementById("wob_pp")!!.text()}"
                                )
                                it.putString(
                                    "humidity",
                                    "Влажность: ${doc.getElementById("wob_hm")!!.text()}"
                                )
                                it.putString(
                                    "wind", "Ветер: ${wobT[6].text()}"
                                )
                                it.putString(
                                    "drawable", "https:${
                                        doc.getElementById("wob_tci")?.attribute("src")!!.value
                                    }"
                                )
                            }
                            bundle.putBoolean("error", false)
                            msg.data = bundle
                            handler.sendMessage(msg)
                        } catch (e: HttpStatusException) {
                            bundle.putBoolean("error", true)
                            msg = handler.obtainMessage()
                            msg.data = bundle
                            handler.sendMessage(msg)
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }.start()
                } else layoutWeather.visibility = View.GONE
            } else {
                textWeather.text = "${weather[0]}, ${weather[3]}"
                textLocation.text = weather[4]
            }

            layoutWeather.setOnClickListener {
                try {
                    weather[8]
                    startActivity(
                        Intent(requireContext(), WeatherActivity::class.java).putExtra(
                            "info", weather
                        )
                    )
                } catch (_: IndexOutOfBoundsException) {
                    makeMessage(requireContext(), "Подождите, пока загрузятся данные")
                }
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
        }

        return binding.root
    }

    private fun search() = with(binding) {
        query = editQuery.text.toString()
        if (query != "") if (spinnerSystem.visibility == View.VISIBLE) {
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