package com.emanuel.lourenco.weatherapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.emanuel.lourenco.weatherapp.databinding.ActivityMainBinding
import java.util.*

/**
 * Main Activity and entry point for the app.
 */

class MainActivity : AppCompatActivity() {
    //NavController variable used in navigation
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()

        //Enables binding on the application
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the navigation host fragment from this Activity
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Instantiate the navController using the NavHostFragment
        navController = navHostFragment.navController

        //Set up the action bar for use with the NavController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    /**
     * Enables back button support. Simply navigates one element up on the stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Creates the custom action bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /**
     * Makes the language option selectable to change the language
     */
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            //When the selected item is the language selection button
            R.id.choose_language -> setLanguage()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Function used to set the language through an alert dialog
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLanguage() {
        //List of languages used in alert dialog
        val languageList =
            arrayOf(getString(R.string.en_en), getString(R.string.pt_pt), getString(R.string.fr_fr))

        //Alert dialog used to choose the language
        val languageSelector = AlertDialog.Builder(this)

        //Sets the title of the alert dialog
        languageSelector.setTitle(getString(R.string.select_language))

        //Sets the dialog to a single choice
        languageSelector.setSingleChoiceItems(languageList, -1) { dialog, selection ->
            when (selection) {
                0 -> {
                    //Sets the locale to england
                    setLocale("en")
                }

                1 -> {
                    //Sets the locale to portugal
                    setLocale("pt")
                }

                2 -> {
                    //Sets the locale to france
                    setLocale("fr")
                }

            }
            //Recreate the application to load the new locale
            recreate()
            dialog.dismiss()
        }
        languageSelector.create().show()
    }

    /**
     * Function used to set the locale through the locale received by argument
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocale(localeToSet: String) {
        //String that contains the new locale
        val localeListToSet = LocaleList(Locale(localeToSet))

        //Set the default locale
        LocaleList.setDefault(localeListToSet)

        //Resources used to update the locale configuration
        resources.configuration.setLocales(localeListToSet)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)

        //Shared preferences used to save the current locale to the next application start
        val sharedPref = getSharedPreferences("Language", Context.MODE_PRIVATE).edit()
        sharedPref.putString("locale", localeToSet)
        sharedPref.apply()
    }

    /**
     * Function used to load the locale saved in the shared preferences in setLocale function
     */
    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadLocale() {
        //Shared preferences used to load the current locale
        val sharedPref = getSharedPreferences("Language", Context.MODE_PRIVATE)
        setLocale(sharedPref.getString("locale", "")!!)
    }
}