package ru.arturprgr.mybrowser.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import ru.arturprgr.mybrowser.databinding.ActivityWeatherBinding
import ru.arturprgr.mybrowser.weather

class WeatherActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)

        binding.apply {
            textDegrees.text = weather[0]
            textMax.text = weather[1]
            textMin.text = weather[2]
            textState.text = weather[3]
            textLocation.text = weather[4]
            textPrecipitation.text = weather[5]
            textHumidity.text = weather[6]
            textWind.text = weather[7]
            Picasso.get().load(weather[8]).into(imageState)

            buttonBack.setOnClickListener {
                startActivity(Intent(this@WeatherActivity, MainActivity::class.java))
            }
        }

        setContentView(binding.root)
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        @Suppress("DEPRECATION") super.onBackPressed()
        startActivity(Intent(this@WeatherActivity, MainActivity::class.java))
    }
}