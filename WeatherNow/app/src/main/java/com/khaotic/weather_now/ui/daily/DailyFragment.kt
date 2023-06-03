package com.khaotic.weather_now.ui.daily

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.khaotic.weather_now.databinding.FragmentDailyBinding
import com.khaotic.weather_now.ui.today.Forecast
import com.khaotic.weather_now.ui.today.PlaceholderApi
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.await
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date


class DailyFragment : Fragment() {

    private var _binding: FragmentDailyBinding? = null
    private val binding get() = _binding!!

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SdCardPath", "Recycle", "SimpleDateFormat", "SetTextI18n", "DiscouragedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentDailyBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val day1 = binding.day1
        val desc1 = binding.desc1
        val temp1 = binding.temp1
        val img1 = binding.img1

        val day2 = binding.day2
        val desc2 = binding.desc2
        val temp2 = binding.temp2
        val img2 = binding.img2

        val day3 = binding.day3
        val desc3 = binding.desc3
        val temp3 = binding.temp3
        val img3 = binding.img3

        val day4 = binding.day4
        val desc4 = binding.desc4
        val temp4 = binding.temp4
        val img4 = binding.img4

        val day5 = binding.day5
        val desc5 = binding.desc5
        val temp5 = binding.temp5
        val img5 = binding.img5


        val sharedPref = root.context.getSharedPreferences("data", Context.MODE_PRIVATE)
        val city = sharedPref.getString("city", "")

        val db: SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(
            "/data/data/com.khaotic.weather_now/databases/cities.db", null
        )

        val query = db.rawQuery("SELECT lon, lat FROM cities WHERE name='${city}'", null)
        query.moveToFirst()
        val lon = query.getString(0).toDouble()
        val lat = query.getString(1).toDouble()

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
                    api.forecast(lon.toString(), lat.toString())
                }
                return@withContext response.await()
            }

            val call = data.await()
            var index = 0

            //day1
            val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val outFormat = SimpleDateFormat("EEEE")
            var date: Date = inFormat.parse(call.list[index].dt_txt) as Date
            var goal: String = outFormat.format(date)

            desc1.text = call.list[index].weather[0].description
            day1.text = goal
            temp1.text = call.list[index].main.temp.toString() + "℃"

            var con: Context = img1.context
            var id = con.resources.getIdentifier("_${call.list[index].weather[0].icon}",
                "drawable", con.packageName)
            img1.setImageResource(id)

            index = getIndex(index, goal, call)

            //day2
            date = inFormat.parse(call.list[index].dt_txt) as Date
            goal = outFormat.format(date)

            desc2.text = call.list[index].weather[0].description
            day2.text = goal
            temp2.text = call.list[index].main.temp.toString() + "℃"

            con = img2.context
            id = con.resources.getIdentifier("_${call.list[index].weather[0].icon}",
                "drawable", con.packageName)
            img2.setImageResource(id)

            index = getIndex(index, goal, call)

            //day3
            date = inFormat.parse(call.list[index].dt_txt) as Date
            goal = outFormat.format(date)

            desc3.text = call.list[index].weather[0].description
            day3.text = goal
            temp3.text = call.list[index].main.temp.toString() + "℃"

            con = img3.context
            id = con.resources.getIdentifier("_${call.list[index].weather[0].icon}",
                "drawable", con.packageName)
            img3.setImageResource(id)

            index = getIndex(index, goal, call)

            //day4
            date = inFormat.parse(call.list[index].dt_txt) as Date
            goal = outFormat.format(date)

            desc4.text = call.list[index].weather[0].description
            day4.text = goal
            temp4.text = call.list[index].main.temp.toString() + "℃"

            con = img4.context
            id = con.resources.getIdentifier("_${call.list[index].weather[0].icon}",
                "drawable", con.packageName)
            img4.setImageResource(id)

            index = getIndex(index, goal, call)

            //day5
            date = inFormat.parse(call.list[index].dt_txt) as Date
            goal = outFormat.format(date)

            desc5.text = call.list[index].weather[0].description
            day5.text = goal
            temp5.text = call.list[index].main.temp.toString() + "℃"

            con = img5.context
            id = con.resources.getIdentifier("_${call.list[index].weather[0].icon}",
                "drawable", con.packageName)
            img5.setImageResource(id)
        }

        return root
    }

    @SuppressLint("SimpleDateFormat")
    fun getIndex(index: Int, day:String, list: Forecast):Int
    {
        var result = index + 1
        val inFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val outFormat = SimpleDateFormat("EEEE")
        val outFormat2 = SimpleDateFormat("HH")
        var date: Date = inFormat.parse(list.list[result].dt_txt) as Date
        var goal: String = outFormat.format(date)
        var goal2: String = outFormat2.format(date)

        while (goal == day || goal2 != "12")
        {
            result += 1
            date = inFormat.parse(list.list[result].dt_txt) as Date
            goal = outFormat.format(date)
            goal2 = outFormat2.format(date)
        }

        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}