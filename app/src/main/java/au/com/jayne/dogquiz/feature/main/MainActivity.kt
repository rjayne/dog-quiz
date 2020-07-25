package au.com.jayne.dogquiz.feature.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import au.com.jayne.dogquiz.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.main_activity.*


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
            R.id.navigation_instructions,
            R.id.navigation_settings
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            currentDestination = destination
            if(destination.id == R.id.game_fragment) {
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
                animateBottomNavViewToGone()
            } else {
                nav_view.alpha = 1f
                nav_view.translationY = 0f
                nav_view.visibility = View.VISIBLE
            }
        }
    }

    private fun animateBottomNavViewToGone() {
        nav_view.animate()
            .translationY(nav_view.getHeight().toFloat())
            .alpha(0f)
            .setDuration(ONE_HUNDRED_MS)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    nav_view.visibility = View.GONE
                }
            })
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

    companion object {
        private const val ONE_HUNDRED_MS = 100L
    }
}