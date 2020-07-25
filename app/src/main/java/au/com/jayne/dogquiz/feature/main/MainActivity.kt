package au.com.jayne.dogquiz.feature.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import au.com.jayne.dogquiz.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var currentDestination: NavDestination? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_game_selection,
            R.id.navigation_leaderboard,
            R.id.navigation_help,
            R.id.navigation_settings
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination
            if(destination.id == R.id.game_fragment) {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if(currentDestination?.id == R.id.game_fragment) {
            // pass back press functionality to fragment
            onBackPressed()
            return true
        } else {
            return findNavController(R.id.nav_host_fragment).navigateUp()
        }
    }

}