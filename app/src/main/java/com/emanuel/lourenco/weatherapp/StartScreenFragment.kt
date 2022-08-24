package com.emanuel.lourenco.weatherapp

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import java.util.*
import kotlin.system.exitProcess


class StartScreenFragment : Fragment() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geoCoder: Geocoder
    private lateinit var addressList: MutableList<Address>
    private var completeCityId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        geoCoder = Geocoder(requireActivity(), Locale.getDefault())

        if (checkForInternet(requireActivity())) requestPermissions()
        else {
            showNoInternetDialog()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launch_screen, container, false)
    }

    @Suppress("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            val location: Location? = task.result
            if (location == null) {
                val locationRequest = LocationRequest.create().apply {
                    interval = 100
                    fastestInterval = 50
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                    maxWaitTime = 100
                }

                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback,
                    Looper.myLooper()
                )
            } else {
                addressList = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                completeCityId = addressList[0].locality + "," + addressList[0].countryName

                findNavController().navigate(
                    StartScreenFragmentDirections.actionLaunchScreenFragmentToCityListFragment(
                        completeCityId
                    )
                )
            }
        }
    }


    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location? = locationResult.lastLocation
            if (lastLocation != null) {
                addressList =
                    geoCoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
            }
            completeCityId = addressList[0].locality + "," + addressList[0].countryName
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var isGranted = false

            permissions.entries.forEach {
                isGranted = it.value
            }

            if (isGranted) getLastLocation() else showDenyPermissionsDialog()
        }


    private fun requestPermissions() {
        activityResultLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun showDenyPermissionsDialog() {
        val denyDialog = AlertDialog.Builder(requireActivity())

        denyDialog.setTitle("Permission Needed!")
        denyDialog.setMessage("You can´t use the app without permissions!")

        denyDialog.setPositiveButton("OK") { _, _ -> requestPermissions() }
        denyDialog.setNegativeButton("Exit App") { _, _ -> exitProcess(0) }
        denyDialog.show()
    }

    private fun showNoInternetDialog() {
        val denyDialog = AlertDialog.Builder(requireActivity())

        denyDialog.setTitle("Permission Needed!")
        denyDialog.setMessage("You can´t use the app without Wi-Fi!\n Turn on the Wi-Fi and start the app again!")

        denyDialog.setPositiveButton("OK") { _, _ ->
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            exitProcess(0)
        }
        denyDialog.setNegativeButton("Exit App") { _, _ -> exitProcess(0) }
        denyDialog.show()
    }

    private fun checkForInternet(context: Context): Boolean {
        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false
            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}

