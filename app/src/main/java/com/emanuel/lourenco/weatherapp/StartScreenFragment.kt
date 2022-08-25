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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import java.util.*
import kotlin.system.exitProcess

/**
 * Entry fragment for the app. Displays a Launcher Screen and asks location permissions to the user.
 */
class StartScreenFragment : Fragment() {
    //Fused Location Provider Client variable
    // used to get the last location of the user
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //Geocode variable used to get the address
    // from the latitude and longitude of the last user location
    private lateinit var geoCoder: Geocoder

    //Mutable address list used to contain the last user location
    private lateinit var addressList: MutableList<Address>

    //Variable that contains the full address of the last user location
    private var completeCityId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialization of the Fused Location Provider Client variable
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        //Initialization of the Geocode variable
        geoCoder = Geocoder(requireActivity(), Locale.getDefault())

        //Checks if the user has internet enabled
        //If the user has internet enabled it will request location permissions
        //If the user has internet disabled it will show a dialog that opens the Wi-Fi settings and closes the app
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Sets the fragment to hide the custom action bar
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
    }

    /**
     * Called before fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        completeCityId = ""
    }

    /**
     * Function used to get the last user location
     */
    @Suppress("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            //Variable that contains the location result from the last location function
            val location: Location? = task.result

            //If the location obtained is null
            //Sets the interval for location updates to 100 milliseconds
            //Sets the fastest interval for location updates to 50 milliseconds
            //Sets the priority to high accuracy
            //Sets the maximum wait time for location updates to 100 milliseconds
            if (location == null) {
                val locationRequest = LocationRequest.create().apply {
                    interval = 100
                    fastestInterval = 50
                    priority = Priority.PRIORITY_HIGH_ACCURACY
                    maxWaitTime = 100
                }

                //Gets the Fused Location Provider Client
                fusedLocationClient =
                    LocationServices.getFusedLocationProviderClient(requireActivity())

                //Request location updates
                fusedLocationClient.requestLocationUpdates(
                    locationRequest, locationCallback,
                    Looper.myLooper()
                )
            } else {
                //If the location is not null
                //Get the locality and country of the last user location with the Geocoder variable
                addressList = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                completeCityId = addressList[0].locality + "," + addressList[0].countryName

                //Navigates to the City List Fragment
                findNavController().navigate(
                    StartScreenFragmentDirections.actionLaunchScreenFragmentToCityListFragment(
                        completeCityId
                    )
                )
            }
        }
    }

    //Location Callback variable to get the last user location
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //Location obtained from the last location function
            val lastLocation: Location? = locationResult.lastLocation

            //If the last location is not null get the locality and country of the last user location
            //with the Geocoder variable
            if (lastLocation != null) {
                addressList =
                    geoCoder.getFromLocation(lastLocation.latitude, lastLocation.longitude, 1)
            }

            //Concatenate the locality and country to get the complete last user location address
            completeCityId = addressList[0].locality + "," + addressList[0].countryName
        }
    }

    //Activity Result Launcher variable that register a request to start an activity
    //for result and verifies if the permissions are granted or not
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            // Handle Permission granted/rejected
            var isGranted = false

            //Verifies if the permissions are granted or not
            permissions.entries.forEach {
                isGranted = it.value
            }

            //If the permissions are granted use the getLastLocation Function
            //If the permissions are not granted use the showDenyPermissionsDialog
            if (isGranted) getLastLocation() else showDenyPermissionsDialog()
        }


    /**
     * Function used to request location permissions to the user
     */
    private fun requestPermissions() {
        //Launches the location permissions request
        activityResultLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    /**
     * Function used to show the dialog to inform the user
     * that the application needs permissions to be used
     */
    private fun showDenyPermissionsDialog() {
        //Alert dialog used to show the the dialog to inform the user
        // that the application needs permissions to be used
        val denyDialog = AlertDialog.Builder(requireActivity())

        //Sets the title of the alert dialog
        denyDialog.setTitle("Permission Needed!")

        //Sets the message of the alert dialog
        denyDialog.setMessage("You can´t use the app without permissions!")

        //If the user choose the OK button it will request permissions again
        denyDialog.setPositiveButton("OK") { _, _ -> requestPermissions() }

        //If the user choose the Exit App button the application will close
        denyDialog.setNegativeButton("Exit App") { _, _ -> exitProcess(0) }

        //Shows the dialog
        denyDialog.show()
    }

    /**
     * Function used to show the dialog to inform that the user
     * that the application needs Wi-Fi enabled
     */
    private fun showNoInternetDialog() {
        //Alert dialog used to show the the dialog to inform the user
        // that the application needs Wi-Fi enabled
        val denyDialog = AlertDialog.Builder(requireActivity())

        //Sets the title of the alert dialog
        denyDialog.setTitle("Permission Needed!")

        //Sets the message of the alert dialog
        denyDialog.setMessage("You can´t use the app without Wi-Fi!\nTurn on the Wi-Fi and start the app again!")

        //If the user choose the OK button it will open the Wi-Fi settings and close the app
        denyDialog.setPositiveButton("OK") { _, _ ->
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
            exitProcess(0)
        }

        //If the user choose the Exit App button the application will close
        denyDialog.setNegativeButton("Exit App") { _, _ -> exitProcess(0) }

        //Shows the dialog
        denyDialog.show()
    }

    /**
     * Function used to check if the user has the Wi-Fi enabled
     */
    private fun checkForInternet(context: Context): Boolean {
        //Register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // If the android version is equal to M
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
            // If the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}

