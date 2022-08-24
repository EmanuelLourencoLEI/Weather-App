package com.emanuel.lourenco.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emanuel.lourenco.weatherapp.databinding.FragmentCityListBinding

/**
 * Fragment for the display of the cities. Displays a [RecyclerView] of cities.
 */
class CityListFragment : Fragment() {
    // Binding object instance corresponding to the fragment_city_list.xml layout
    private var _binding: FragmentCityListBinding? = null
    private val binding get() = _binding!!

    //List of cities used to check weather information
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            //Adds the last city location of the user to the city list
            cityList.add(it.getString("last_location").toString())
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

    /**
     * Called when the view is created.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Sets the action title
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.city_list)

        //Sets the fragment to show the custom action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        translateCities()

        // Sets the LayoutManager of the recyclerview
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        // Sets the Adapter of the recyclerview
        binding.recyclerView.adapter = CityListAdapter(cityList)
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Function that translates the names of the cities depending on the language selected.
     */
    private fun translateCities() {
        cityList[0] = getString(R.string.lisbon)
        cityList[3] = getString(R.string.berlin)
        cityList[4] = getString(R.string.copenhagen)
        cityList[5] = getString(R.string.rome)
        cityList[6] = getString(R.string.london)
        cityList[8] = getString(R.string.prague)
        cityList[9] = getString(R.string.vienna)
    }
}