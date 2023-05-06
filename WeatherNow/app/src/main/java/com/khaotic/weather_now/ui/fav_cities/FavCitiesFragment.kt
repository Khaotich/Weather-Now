package com.khaotic.weather_now.ui.fav_cities

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
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
import com.khaotic.weather_now.databinding.FragmentFavCitiesBinding
import com.khaotic.weather_now.ui.cities.City
import com.khaotic.weather_now.ui.cities.LanguageAdapter
import java.util.ArrayList
import java.util.Locale

class FavCitiesFragment : Fragment() {

    private var _binding: FragmentFavCitiesBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<City>()
    private lateinit var adapter: LanguageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(FavCitiesViewModel::class.java)
        _binding = FragmentFavCitiesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navController = findNavController()

        recyclerView = binding.recyclerView1
        searchView = binding.searchView1
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        addDataToList()
        adapter = LanguageAdapter(mList)
        recyclerView.adapter = adapter

        adapter.onItemClick = {
            val bundle = Bundle()
            bundle.putString("city", it.title)
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

//        val textView: TextView = binding.
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
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

        val db: SQLiteDatabase = SQLiteDatabase.openOrCreateDatabase("/data/data/com.khaotic." +
                "weather_now/databases/cities.db",null)
        val query = db.rawQuery("SELECT name, country FROM cities WHERE fav=1", null)
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