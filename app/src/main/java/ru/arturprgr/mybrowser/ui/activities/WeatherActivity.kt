package ru.arturprgr.mybrowser.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.squareup.picasso.Picasso
import org.jsoup.Jsoup
import ru.arturprgr.mybrowser.R
import ru.arturprgr.mybrowser.databinding.ActivityWeatherBinding
import java.io.IOException

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)

        loadWeather("сегодня")

        binding.apply {
            buttonBack.setOnClickListener {
                startActivity(Intent(this@WeatherActivity, MainActivity::class.java))
            }

            tabLayout.addTab(
                tabLayout.newTab().setText(resources.getText(R.string.today).toString())
            )
            tabLayout.addTab(
                tabLayout.newTab().setText(resources.getText(R.string.tomorrow).toString())
            )
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    when (tab?.position) {
                        0 -> loadWeather("сегодня")
                        1 -> loadWeather("завтра")
                    }
                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {}

                override fun onTabReselected(p0: TabLayout.Tab?) {}
            })
        }

        setContentView(binding.root)
    }

    private fun loadWeather(string: String) = with(binding) {
        @Suppress("DEPRECATION") val handler = @SuppressLint("HandlerLeak") object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                val message = msg.data
                textDegrees.text = message.getString("degrees")!!.toString()
                textState.text = message.getString("state")!!.toString()
                textLocation.text = message.getString("location")!!.toString()
                textPrecipitation.text = message.getString("precipitation")!!.toString()
                textHumidity.text = message.getString("humidity")!!.toString()
                textWind.text = message.getString("wind")!!.toString()
                Picasso.get().load(message.getString("drawable")).into(image)
            }
        }
        Thread {
            try {
                val doc = Jsoup.connect("https://www.google.com/search?q=погода+$string").get()
                val msg = handler.obtainMessage()
                val bundle = Bundle().also {
                    it.putString(
                        "degrees", "${doc.getElementById("wob_tm")?.text()}°C"
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
                        "humidity", "Влажность: ${doc.getElementById("wob_hm")!!.text()}"
                    )
                    it.putString(
                        "wind", "Ветер: ${doc.getElementsByClass("wob_t")[6].text()}"
                    )
                    it.putString(
                        "drawable",
                        "https:${doc.getElementById("wob_tci")?.attribute("src")!!.value}"
                    )
                }
                msg.data = bundle
                handler.sendMessage(msg)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
}