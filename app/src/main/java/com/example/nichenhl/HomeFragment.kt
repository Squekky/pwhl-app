package com.example.nichenhl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "HomeFragment"

class HomeFragment : Fragment() {
    private lateinit var adapter: SearchAdapter
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private val stats = listOf("Hat Trick Leaders", "Blocks/60", "2 Goal Games", "3 Point Games")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.statsListRecyclerView)
        searchView = view.findViewById(R.id.searchStats)

        val adapter = SearchAdapter(stats) { selectedItem ->
            // Navigate to DetailsFragment and pass the selected item
            val bundle = Bundle()
            bundle.putString("ITEM_KEY", selectedItem)

            val statsFragment = StatsFragment()
            statsFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.nhl_frame_layout, statsFragment)
                .addToBackStack(null) // Add to back stack to enable back navigation
                .commit()
        }

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Set up SearchView
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val filteredData = stats.filter {
                    it.contains(newText ?: "", ignoreCase = true)
                }
                adapter.updateData(filteredData)
                return true
            }
        })

        return view
    }
}