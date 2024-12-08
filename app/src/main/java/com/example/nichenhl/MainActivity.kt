package com.example.nichenhl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nichenhl.databinding.ActivityMainBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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
            replaceFragment(fragment, item.itemId)
            true
        }

        // Check if there's a saved instance state to restore the previous fragment
        if (savedInstanceState != null) {
            val selectedFragmentId = savedInstanceState.getInt("selectedFragmentId", R.id.nav_home)
            binding.bottomNavigationView.selectedItemId = selectedFragmentId
        } else {
            // Default to Home Fragment
            binding.bottomNavigationView.selectedItemId = R.id.nav_home
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.nhl_frame_layout)
            if (fragment is GamesFragment) {
                binding.bottomNavigationView.selectedItemId = R.id.nav_home
            } else if (fragment is SettingsFragment) {
                binding.bottomNavigationView.selectedItemId = R.id.nav_settings
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("selectedFragmentId", binding.bottomNavigationView.selectedItemId)
    }

    private fun replaceFragment(fragment: Fragment, selectedItemId: Int) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        // If Home Fragment, clear the back stack before replacing
        if (selectedItemId == R.id.nav_home) {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        // Check if the fragment is already in the fragment manager
        val fragmentTag = fragment.javaClass.simpleName
        val existingFragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        if (existingFragment == null) {
            // Replace fragments not already in fragment manager
            fragmentTransaction.replace(R.id.nhl_frame_layout, fragment, fragmentTag)

            // Only add non-home fragments to back stack
            if (selectedItemId != R.id.nav_home) {
                fragmentTransaction.addToBackStack(fragmentTag)
            }

            fragmentTransaction.commit()
        }
    }
}