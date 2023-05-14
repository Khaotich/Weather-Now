package com.khaotic.weather_now.ui.today

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khaotic.weather_now.R
import com.khaotic.weather_now.databinding.FragmentTodayBinding
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*


class TodayFragment : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SdCardPath", "Recycle", "DiscouragedApi", "SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(TodayViewModel::class.java)
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView = binding.textView
        val imageButton = binding.imageButton
        val imageView = binding.imageView

        //widgets for info weather
        val imageClouds = binding.imageClouds
        val weatherText = binding.textWeather
        val currentTempText = binding.textCurrentTemperature
        val textPerceptibleTemperature = binding.textPerceptibleTemperature
        val humidityText = binding.textHumidity
        val pressureText = binding.textPressure
        val speeedText = binding.speedText
        val degText = binding.degText
        val visibilityText = binding.textVisibility
        val cloudsText = binding.textClouds
        val sunriseText = binding.textSunrise
        val sunsetText = binding.textSunset

        val db: SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/data/data/com.khaotic." +
                "weather_now/databases/cities.db",null)
        val sharedPref = root.context.getSharedPreferences("data", MODE_PRIVATE)

        var fav = 0
        val city = sharedPref.getString("city", "")
        var lon = 0.0
        var lat = 0.0

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        if (city != "")
        {
            textView.text = city
            val query = db.rawQuery("SELECT name, country, fav, lon, lat FROM cities WHERE name='${city}'"
                , null)
            query.moveToFirst()
            fav = query.getString(2).toInt()
            lon = query.getString(3).toDouble()
            lat = query.getString(4).toDouble()

            if (fav == 0)
                imageButton.setImageResource(R.drawable.favorite)
            else
                imageButton.setImageResource(R.drawable.favorite_)

            val id = root.resources.getIdentifier(query.getString(1).lowercase(Locale.ROOT),
                "drawable", root.context.packageName)
            imageView.setImageResource(id)
        }

        imageButton.setOnClickListener {
            if (fav == 0)
            {
                imageButton.setImageResource(R.drawable.favorite_)
                db.execSQL("UPDATE cities SET fav=1 WHERE name='${city}'")
            }
            else
            {
                imageButton.setImageResource(R.drawable.favorite)
                db.execSQL("UPDATE cities SET fav=0 WHERE name='${city}'")
            }
        }

        GlobalScope.launch(Dispatchers.Main)
        {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(PlaceholderApi::class.java)
            val data = withContext(Dispatchers.IO)
            {
                val response = async {
                        api.weather(lon.toString(), lat.toString())
                }
                return@withContext response.await()
            }

            val call = data.await()

            val con: Context = imageClouds.context
            val id = con.resources.getIdentifier("_${call.weather[0].icon}",
                "drawable", con.packageName)
            imageClouds.setImageResource(id)

            weatherText.text = "Weather ${call.weather[0].description}"
            currentTempText.text = "Current Temperature ${call.main.temp} ℃"
            textPerceptibleTemperature.text = "Perceptible Temperature ${call.main.feels_like} ℃"
            humidityText.text = "Humidity ${call.main.humidity} %"
            pressureText.text = "Pressure ${call.main.pressure} hPa"
            speeedText.text = "Speed ${call.wind.speed} meter/sec"
            degText.text = "Degs ${call.wind.deg} °"
            visibilityText.text = "Visibility ${call.visibility} meters"
            cloudsText.text = "Cloudiness ${call.clouds.all} %"

            var dd = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(call.sys.sunrise.toLong()))
            dd = dd.substring(11, 16)
            sunriseText.text = "Sun Rise $dd"

            dd = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(call.sys.sunset.toLong()))
            dd = dd.substring(11, 16)
            sunsetText.text = "Sun Set $dd"
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}