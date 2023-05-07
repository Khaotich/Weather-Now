package com.khaotic.weather_now.ui.today

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khaotic.weather_now.R
import com.khaotic.weather_now.databinding.FragmentTodayBinding
import java.util.Locale


class TodayFragment : Fragment() {

    private var _binding: FragmentTodayBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SdCardPath", "Recycle", "DiscouragedApi")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(TodayViewModel::class.java)
        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView = binding.textView
        val imageButton = binding.imageButton
        val imageView = binding.imageView

        val db: SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/data/data/com.khaotic." +
                "weather_now/databases/cities.db",null)
        var fav = 0
        val city = arguments?.getString("city")


//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        if (city != null)
        {
            textView.text = city
            val query = db.rawQuery("SELECT name, country, fav FROM cities WHERE name='${city}'"
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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}