package com.emanuel.lourenco.weatherapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * [ListAdapter] implementation for the recyclerview in CityListFragment.
 */

class CityListAdapter(private val cityList: List<String>) :
    RecyclerView.Adapter<CityListAdapter.CityListViewHolder>() {

    /**
     * Provides a reference for the views needed to display items in your list.
     */
    class CityListViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.button_item)
    }

    /**
     * Returns the size of the list associated with the adapter
     */
    override fun getItemCount(): Int {
        return cityList.size
    }

    /**
     * Creates new views with R.layout.city_view as its template
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityListViewHolder {
        val layout = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.city_view, parent, false)

        return CityListViewHolder(layout)
    }

    /**
     * Replaces the content of an existing view with new data
     */
    override fun onBindViewHolder(holder: CityListViewHolder, position: Int) {
        //Variable that holds a city of the city list
        val item = cityList[position]
        //creates a new view with the current city of the variable "item"
        holder.button.text = item

        // Assigns a [OnClickListener] to the button contained in the [ViewHolder]
        holder.button.setOnClickListener {
            //Action used to navigate from city list fragment to city detail fragment
            val action =
                CityListFragmentDirections.actionCityListFragmentToCityDetailFragment(cityName = holder.button.text.toString())
            //View controller used to navigate through fragments
            holder.view.findNavController().navigate(action)
        }
    }

}