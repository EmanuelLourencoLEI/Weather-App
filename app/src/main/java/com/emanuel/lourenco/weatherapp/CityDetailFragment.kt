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


class CityDetailFragment : Fragment() {
    companion object {
        const val CITY_NAME = "city_name"
    }

    private var _binding: FragmentCityDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var cityId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            cityId = it.getString(CITY_NAME).toString()
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
        (requireActivity() as AppCompatActivity).supportActionBar?.title = getString(R.string.city_details)
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

    private fun getJsonData() {
        hideContainer()

        val queue = Volley.newRequestQueue(this.context)
        val url =
            "https://api.openweathermap.org/data/2.5/weather?q=$cityId&units=metric&appid=${BuildConfig.API_KEY}"
        val jsonRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                bindCityDetails(response)
            },
            {
                showErrorMessage()
            })

        queue.add(jsonRequest)
    }

    private fun bindCityDetails(response: JSONObject) {
        val main = response.getJSONObject("main")
        val sys = response.getJSONObject("sys")
        val wind = response.getJSONObject("wind")
        val weather = response.getJSONArray("weather").getJSONObject(0)

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

    private fun hideContainer() {
        binding.loader.visibility = View.VISIBLE
        binding.mainContainer.visibility = View.GONE
    }

    private fun showContainer() {
        binding.loader.visibility = View.GONE
        binding.mainContainer.visibility = View.VISIBLE
    }

    private fun showErrorMessage() {
        binding.loader.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.errorText.text = getString(R.string.error_message)
    }
}