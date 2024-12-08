package com.example.nichenhl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nichenhl.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val gamesFragment: Fragment = GamesFragment()
        val standingsFragment: Fragment = StandingsFragment()
        val settingsFragment: Fragment = SettingsFragment()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Handle navigation selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            val fragment = when (item.itemId) {
                R.id.nav_home -> gamesFragment
                R.id.nav_standings -> standingsFragment
                R.id.nav_settings -> settingsFragment
                else -> gamesFragment
            }
            replaceFragment(fragment)

            true
        }

        // Check if there's a saved instance state to restore the previous fragment
        if (savedInstanceState != null) {
            val selectedFragmentId = savedInstanceState.getInt("selectedFragmentId", R.id.nav_home)
            binding.bottomNavigationView.selectedItemId = selectedFragmentId
        } else {
            // Set the default fragment if no state is saved
            binding.bottomNavigationView.selectedItemId = R.id.nav_home
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedFragmentId", binding.bottomNavigationView.selectedItemId)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.pwhl_frame_layout, fragment)
        fragmentTransaction.commit()
    }
}


