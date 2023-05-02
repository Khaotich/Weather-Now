package com.khaotic.weather_now.ui.cities

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
import com.khaotic.weather_now.databinding.FragmentCitiesBinding
import java.util.*

class CitiesFragment : Fragment() {

    private var _binding: FragmentCitiesBinding? = null
    private val binding get() = _binding!!

    //test
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private var mList = ArrayList<City>()
    private lateinit var adapter: LanguageAdapter

//    private val navController = findNavController()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(CitiesViewModel::class.java)
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

//        val textView: TextView = binding.homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

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

    private fun addDataToList() {
        mList.add(City("Java", R.drawable.ic_city))
        mList.add(City("Kotlin", R.drawable.ic_city))
        mList.add(City("C++", R.drawable.ic_city))
        mList.add(City("Python", R.drawable.ic_city))
        mList.add(City("HTML", R.drawable.ic_city))
        mList.add(City("Swift", R.drawable.ic_city))
        mList.add(City("C#", R.drawable.ic_city))
        mList.add(City("JavaScript", R.drawable.ic_city))
    }
}