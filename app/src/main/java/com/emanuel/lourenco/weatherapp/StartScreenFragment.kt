package com.emanuel.lourenco.weatherapp

import android.Manifest
import android.app.AlertDialog
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
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
    private var locality = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        geoCoder = Geocoder(requireActivity(), Locale.getDefault())

        requestPermissions()
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
                locality = addressList[0].locality

                findNavController().navigate(
                    StartScreenFragmentDirections.actionLaunchScreenFragmentToCityListFragment(
                        locality
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
            locality = addressList[0].locality
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            var isGranted = false
            // Handle Permission granted/rejected
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

}

