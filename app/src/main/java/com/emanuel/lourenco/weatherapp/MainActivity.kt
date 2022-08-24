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


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadLocale()
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the navigation host fragment from this Activity
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // Instantiate the navController using the NavHostFragment
        navController = navHostFragment.navController

        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    /**
     * Enables back button support. Simply navigates one element up on the stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.choose_language -> setLanguage()
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLanguage() {
        val languageList =
            arrayOf(getString(R.string.en_en), getString(R.string.pt_pt), getString(R.string.fr_fr))

        val languageSelector = AlertDialog.Builder(this)

        languageSelector.setTitle(getString(R.string.select_language))

        languageSelector.setSingleChoiceItems(languageList, -1) { dialog, selection ->
            when (selection) {
                0 -> {
                    setLocale("en")
                }

                1 -> {
                    setLocale("pt")
                }

                2 -> {
                    setLocale("fr")
                }

            }
            recreate()
            dialog.dismiss()
        }
        languageSelector.create().show()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setLocale(localeToSet: String) {
        val localeListToSet = LocaleList(Locale(localeToSet))

        LocaleList.setDefault(localeListToSet)

        resources.configuration.setLocales(localeListToSet)
        resources.updateConfiguration(resources.configuration, resources.displayMetrics)

        val sharedPref = getSharedPreferences("Language", Context.MODE_PRIVATE).edit()
        sharedPref.putString("locale", localeToSet)
        sharedPref.apply()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun loadLocale() {
        val sharedPref = getSharedPreferences("Language", Context.MODE_PRIVATE)
        setLocale(sharedPref.getString("locale", "")!!)
    }
}