package com.emanuel.lourenco.weatherapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.emanuel.lourenco.weatherapp.databinding.FragmentCityDetailBinding
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for the display of the weather information of the specified city.
 */
class CityDetailFragment : Fragment() {
    // Binding object instance corresponding to the fragment_city_detail.xml layout
    private var _binding: FragmentCityDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView lifecycle callbacks,
    // when the view hierarchy is attached to the fragment
    private val binding get() = _binding!!

    //Variable that contains the specified city name
    private lateinit var cityId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            //Gets the specified city name to use to obtain the weather information from the openweathermap API
            cityId = it.getString("city_name").toString()
        }

        completeCityId()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCityDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Sets the action bar title
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            getString(R.string.city_details)

        //Sets the fragment to show the custom action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.show()

        getJsonData()
    }

    /**
     * Frees the binding object when the Fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Function used to to add the country that is associated with the city
     * since there are cities with the same name but from different countries
     */
    private fun completeCityId() {
        when (cityId) {
            "Lisbon" -> cityId = "Lisbon,PT"
            "Madrid" -> cityId = "Madrid,ES"
            "Paris" -> cityId = "Paris,FR"
            "Berlin" -> cityId = "Berlin,DE"
            "Copenhagen" -> cityId = "Copenhagen,DK"
            "Rome" -> cityId = "Rome,IT"
            "London" -> cityId = "London,GB"
            "Dublin" -> cityId = "Dublin,IE"
            "Prague" -> cityId = "Prague,CZ"
            "Vienna" -> cityId = "Vienna,AT"
        }
    }

    /**
     * Function used to get the json data from the openweathermap API
     * with the help of the HTTP library Volley
     */
    private fun getJsonData() {
        hideContainer()

        //Creates a new Request Queue instance
        val queue = Volley.newRequestQueue(this.context)

        //URL that contains the city name and the API KEY used to get the json data from the openweathermap API
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$cityId&units=metric&appid=${BuildConfig.API_KEY}"

        //Request made to the openweathermap API to get the json data
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                bindCityDetails(response)
            },
            {
                showErrorMessage()
            })

        //Add the json request to the queue
        queue.add(jsonRequest)
    }

    /**
     * Function used to bind the json data obtained from the openweathermap API
     */
    private fun bindCityDetails(response: JSONObject) {
        //JSON Objects that contain the various types of json data
        val main = response.getJSONObject("main")
        val sys = response.getJSONObject("sys")
        val wind = response.getJSONObject("wind")
        val weather = response.getJSONArray("weather").getJSONObject(0)

        //Binding the json data to the corresponding text view
        binding.location.text =
            getString(R.string.location, response.getString("name"), sys.getString("country"))
        binding.wind.text = getString(R.string.wind_speed, wind.getString("speed"))
        binding.updateDate.text = getString(
            R.string.updated_at,
            SimpleDateFormat(
                "dd/MM/yyyy hh:mm a",
                Locale.ENGLISH
            ).format(Date(response.getLong("dt") * 1000))
        )
        binding.currentTemperature.text =
            getString(R.string.current_temperature, main.getString("temp"))
        binding.minimumTemperature.text =
            getString(R.string.minimum_temperature, main.getString("temp_min"))
        binding.maximumTemperature.text =
            getString(R.string.maximum_temperature, main.getString("temp_max"))
        binding.sunrise.text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sys.getLong("sunrise") * 1000))
        binding.sunset.text =
            SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sys.getLong("sunset") * 1000))
        binding.weatherStatus.text = weather.getString("description")
        binding.humidity.text = getString(R.string.humidity_value, main.getString("humidity") + "%")

        binding.pressure.text = getString(R.string.pressure_value, main.getString("pressure"))

        showContainer()
    }

    /**
     * Function used to show the loader and hide the main container while the json request is processed
     */
    private fun hideContainer() {
        binding.loader.visibility = View.VISIBLE
        binding.mainContainer.visibility = View.GONE
    }

    /**
     * Function used to show the main container and hide the loader when the json request is finished
     */
    private fun showContainer() {
        binding.loader.visibility = View.GONE
        binding.mainContainer.visibility = View.VISIBLE
    }

    /**
     * Function used to show error message in case of API request failure
     */
    private fun showErrorMessage() {
        binding.loader.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = getString(R.string.error_message)
    }
}