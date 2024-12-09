package com.example.nichenhl

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nichenhl.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val gamesFragment: Fragment = GamesFragment()
    private val standingsFragment: Fragment = StandingsFragment()
    private val settingsFragment: Fragment = SettingsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // Initialize the BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_games -> gamesFragment
                R.id.nav_standings -> standingsFragment
                R.id.nav_settings -> settingsFragment
                else -> gamesFragment
            }
            replaceFragment(fragment)
            true
        }

        // Restore the previous fragment if there's a saved instance state
        if (savedInstanceState != null) {
            val selectedFragmentId = savedInstanceState.getInt("selectedFragmentId", R.id.nav_games)
            binding.bottomNavigationView.selectedItemId = selectedFragmentId
        } else {
            // Set the default fragment if no state is saved
            binding.bottomNavigationView.selectedItemId = R.id.nav_games
            replaceFragment(gamesFragment)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedFragmentId", binding.bottomNavigationView.selectedItemId)
    }

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.pwhl_frame_layout)

        // Box score goes back to games fragment
        if (fragment is BoxScoreFragment) {
            supportFragmentManager.popBackStack()
            binding.bottomNavigationView.selectedItemId = R.id.nav_games
        } else {
            finish()
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pwhl_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
