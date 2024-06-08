package ru.arturprgr.mybrowser.ui.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import org.jsoup.Jsoup
import ru.arturprgr.mybrowser.SettingsActivity
import ru.arturprgr.mybrowser.WebActivity
import ru.arturprgr.mybrowser.databinding.FragmentMainBinding
import java.io.IOException

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private var query: String = ""

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.apply {
            buttonSettings.setOnClickListener {
                startActivity(Intent(requireContext(), SettingsActivity::class.java))
            }

            buttonQuery.setOnClickListener {
                query = editQuery.text.toString()

                if (query != "") {
                    when ("${spinnerSystem.selectedItem}") {
                        "Google" -> {
                            put("https://www.google.com/search?q=")
                        }

                        "Яндекс" -> {
                            put("https://ya.ru/search/?text=")
                        }

                        "DuckDuckGo" -> {
                            put("https://duckduckgo.com/?t=h_&q=")
                        }

                        "Bing" -> {
                            put("https://www.bing.com/search?q=")
                        }

                        "Yahoo!" -> {
                            put("https://search.yahoo.com/search?p=")
                        }
                    }
                }
            }

            if (Firebase.auth.currentUser != null) {
                val handler = @SuppressLint("HandlerLeak")
                object : Handler() {
                    override fun handleMessage(msg: Message) {
                        super.handleMessage(msg)
                        textWeather.text = msg.data.getString("weather").toString()
                        textLocation.text = msg.data.getString("location").toString()
                    }
                }
                Thread {
                    try {
                        val doc =
                            Jsoup.connect("https://www.google.com/search?q=погода+сейчас").get()
                        val msg = handler.obtainMessage()
                        val bundle = Bundle()
                        bundle.putString(
                            "weather",
                            "${
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
            }
        }

        return binding.root
    }

    private fun put(searchSystem: String) {
        requireContext().startActivity(
            Intent(
                requireContext(),
                WebActivity::class.java
            ).putExtra("url", "$searchSystem$query").putExtra("name", query)
        )
        binding.editQuery.setText("")
    }
}