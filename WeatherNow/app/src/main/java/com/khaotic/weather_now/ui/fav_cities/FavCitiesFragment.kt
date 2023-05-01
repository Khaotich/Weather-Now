package com.khaotic.weather_now.ui.fav_cities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khaotic.weather_now.databinding.FragmentFavCitiesBinding

class FavCitiesFragment : Fragment() {

    private var _binding: FragmentFavCitiesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(FavCitiesViewModel::class.java)

        _binding = FragmentFavCitiesBinding.inflate(inflater, container, false)
        val root: View = binding.root

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
}