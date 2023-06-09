package com.khaotic.weather_now.ui.cities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.database.sqlite.SQLiteDatabase as sql
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.khaotic.weather_now.R
import com.khaotic.weather_now.databinding.FragmentCitiesBinding
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class CitiesFragment : Fragment() {

    private var _binding: FragmentCitiesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<City>()
    private lateinit var adapter: LanguageAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[CitiesViewModel::class.java]
        _binding = FragmentCitiesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navController = findNavController()

        recyclerView = binding.recyclerView
        searchView = binding.searchView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        addDataToList()
        adapter = LanguageAdapter(mList)
        recyclerView.adapter = adapter

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putString("city", it.title)

            val sharedPref = root.context.getSharedPreferences("data", MODE_PRIVATE)
            val edit = sharedPref.edit()
            edit.apply {
                putString("city", it.title)
                apply()
            }

            navController.navigate(R.id.navigation_today, bundle)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }
        })

        return root
    }

    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }

    private fun filterList(query: String?) {

        if (query != null) {
            val filteredList = ArrayList<City>()
            for (i in mList) {
                if (i.title.lowercase(Locale.ROOT).contains(query)) {
                    filteredList.add(i)
                }
            }

            if (filteredList.isEmpty()) {
                Toast.makeText(this.context, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                adapter.setFilteredList(filteredList)
            }
        }
    }

    @SuppressLint("Recycle", "SdCardPath", "DiscouragedApi")
    private fun addDataToList() {
        val con = context
        val dictionary = mutableMapOf<String, Int>()
            for (i in 65..90)
                for (j in 65..90)
                {
                    val key = (i.toChar().toString() + j.toChar().toString()).lowercase(Locale.ROOT)
                    val id = con!!.resources.getIdentifier(key, "drawable", con.packageName)
                    dictionary[key] = id
                }

        val db: sql = sql.openOrCreateDatabase("/data/data/com.khaotic." +
                "weather_now/databases/cities.db",null)
        val query = db.rawQuery("SELECT name, country FROM cities", null)
        val range = query.count

        query.moveToFirst()
        for (i in 0 until range)
        {
            val city = query.getString(0).toString()
            val country = query.getString(1).toString().lowercase(Locale.ROOT)
            val id = dictionary.getOrDefault(country, 0)

            if(id != 0)
                mList.add(City(city, id))
            else
                mList.add(City(city, R.drawable.ic_city))

            query.moveToNext()
        }
    }
}