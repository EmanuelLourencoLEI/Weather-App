package com.emanuel.lourenco.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.emanuel.lourenco.weatherapp.databinding.FragmentCityListBinding

class CityListFragment : Fragment() {
    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    private val cityList = mutableListOf(
        "Lisbon",
        "Madrid",
        "Paris",
        "Berlin",
        "Copenhagen",
        "Rome",
        "London",
        "Dublin",
        "Prague",
        "Vienna",
    )

    companion object {
        const val LAST_LOCATION = "last_location"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            cityList.add(it.getString(LAST_LOCATION).toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Retrieve and inflate the layout for this fragment
        _binding = FragmentCityListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.city_list)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = CityListAdapter(cityList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}