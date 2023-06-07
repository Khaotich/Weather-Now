package com.khaotic.weather_now.ui.today

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.khaotic.weather_now.R
import com.khaotic.weather_now.databinding.FragmentTodayBinding
import com.khaotic.weather_now.weather_api.PlaceholderApi
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
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @OptIn(DelicateCoroutinesApi::class)
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SdCardPath", "Recycle", "DiscouragedApi", "SetTextI18n", "MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[TodayViewModel::class.java]
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        val root: View = binding.root
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

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

        //widgets for pollution
        val co = binding.textViewCo
        val no = binding.textViewNo
        val no2 = binding.textViewNo2
        val o3 = binding.textViewO3
        val so2 = binding.textViewSo2
        val pm25 = binding.textViewPm25
        val pm10 = binding.textViewPm10
        val nh3 = binding.textViewNh3

        var lon = 0.0
        var lat = 0.0
        fun co()
        {
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

                val sharedPref = root.context.getSharedPreferences("data", Context.MODE_PRIVATE)
                val edit = sharedPref.edit()
                edit.apply {
                    putString("city", call.name)
                    apply()
                }

                textView.text = call.name
                weatherText.text = "Weather ${call.weather[0].description}"
                currentTempText.text = "Current Temperature ${call.main.temp} ℃"
                textPerceptibleTemperature.text = "Perceptible Temperature ${call.main.feels_like} ℃"
                humidityText.text = "Humidity ${call.main.humidity} %"
                pressureText.text = "Pressure ${call.main.pressure} hPa"
                speeedText.text = "Speed ${call.wind.speed} meter/sec"
                degText.text = "Degs ${call.wind.deg} °"
                visibilityText.text = "Visibility ${call.visibility} meters"
                cloudsText.text = "Cloudiness ${call.clouds.all} %"

                var dd =
                    DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(call.sys.sunrise.toLong()))
                dd = dd.substring(11, 16)
                sunriseText.text = "Sun Rise $dd"

                dd =
                    DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochSecond(call.sys.sunset.toLong()))
                dd = dd.substring(11, 16)
                sunsetText.text = "Sun Set $dd"
            }
        }

        fun get_pollution()
        {
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
                        api.pollution(lon.toString(), lat.toString())
                    }
                    return@withContext response.await()
                }

                val call = data.await()
                val elements = call.list[0].components

                co.text = elements.co.toString()
                no.text = elements.no.toString()
                no2.text = elements.no2.toString()
                o3.text = elements.o3.toString()
                so2.text = elements.so2.toString()
                pm25.text = elements.pm2_5.toString()
                pm10.text = elements.pm10.toString()
                nh3.text = elements.nh3.toString()
            }
        }

        val db: SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(
            "/data/data/com.khaotic." +
                    "weather_now/databases/cities.db", null
        )

        var fav = 0
        var city = arguments?.getString("city")

        if (!city.isNullOrEmpty())
        {
            textView.text = city
            val query = db.rawQuery(
                "SELECT name, country, fav, lon, lat FROM cities WHERE name='${city}'", null
            )
            query.moveToFirst()
            fav = query.getString(2).toInt()
            lon = query.getString(3).toDouble()
            lat = query.getString(4).toDouble()

            if (fav == 0)
                imageButton.setImageResource(R.drawable.favorite)
            else
                imageButton.setImageResource(R.drawable.favorite_)

            val id = root.resources.getIdentifier(
                query.getString(1).lowercase(Locale.ROOT),
                "drawable", root.context.packageName
            )
            imageView.setImageResource(id)
            co()
            get_pollution()
        }
        else
        {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            fusedLocationClient.getCurrentLocation(100, object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
                override fun isCancellationRequested() = false
            })
                .addOnSuccessListener { location: Location? ->
                    if (location == null)
                        Toast.makeText(root.context, "Cannot get location.", Toast.LENGTH_SHORT).show()
                    else
                    {
                        lat = location.latitude
                        lon = location.longitude

                        co()
                        get_pollution()

                        val sharedPref = root.context.getSharedPreferences("data", Context.MODE_PRIVATE)
                        city = sharedPref.getString("city", "")
                        val query = db.rawQuery("SELECT name, country, fav, lon, lat FROM cities WHERE name='${city}'"
                                            , null)
                        query.moveToFirst()
                        fav = query.getString(2).toInt()

                        if (fav == 0)
                            imageButton.setImageResource(R.drawable.favorite)
                        else
                            imageButton.setImageResource(R.drawable.favorite_)

                        val id = root.resources.getIdentifier(query.getString(1).lowercase(Locale.ROOT),
                            "drawable", root.context.packageName)
                        imageView.setImageResource(id)
                    }
                }
        }

        imageButton.setOnClickListener {
            fav = if (fav == 0) {
                imageButton.setImageResource(R.drawable.favorite_)
                db.execSQL("UPDATE cities SET fav=1 WHERE name='${city}'")
                1
            } else {
                imageButton.setImageResource(R.drawable.favorite)
                db.execSQL("UPDATE cities SET fav=0 WHERE name='${city}'")
                0
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}