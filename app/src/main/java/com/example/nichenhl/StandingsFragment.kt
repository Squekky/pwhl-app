package com.example.nichenhl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StandingsFragment : Fragment() {

    // Placeholder
    private val standingsList = listOf(
        "Team A - 1st",
        "Team B - 2nd",
        "Team C - 3rd",
        "Team D - 4th"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_standings, container, false)

        // Set up RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.standingsRecyclerView)
        val adapter = StandingsAdapter(standingsList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        return view
    }
}

class StandingsAdapter(private val standings: List<String>) : RecyclerView.Adapter<StandingsAdapter.StandingsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StandingsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return StandingsViewHolder(view)
    }

    override fun onBindViewHolder(holder: StandingsViewHolder, position: Int) {
        holder.bind(standings[position])
    }

    override fun getItemCount(): Int {
        return standings.size
    }

    class StandingsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(standing: String) {
            (itemView as android.widget.TextView).text = standing
        }
    }
}
