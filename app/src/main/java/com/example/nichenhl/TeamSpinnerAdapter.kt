package com.example.nichenhl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class TeamSpinnerAdapter(private val context: Context, private val teams: List<Team>) : BaseAdapter() {

    override fun getCount(): Int = teams.size

    override fun getItem(position: Int): Any = teams[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent, false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent, true)
    }

    private fun createViewFromResource(
        position: Int,
        convertView: View?,
        parent: ViewGroup?,
        isDropdown: Boolean
    ): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(if (isDropdown) R.layout.spinner_item else R.layout.spinner_item, parent, false)

        val team = teams[position]

        val teamLogo: ImageView = view.findViewById(R.id.teamLogo)
        val teamName: TextView = view.findViewById(R.id.teamName)

        teamLogo.setImageResource(team.logoResId)
        teamName.text = team.name

        return view
    }
}