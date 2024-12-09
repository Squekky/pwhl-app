package com.example.nichenhl

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.material.divider.MaterialDivider
import okhttp3.Headers
import org.json.JSONException

private const val TAG = "StandingsFragment"
private const val API_KEY = BuildConfig.API_KEY
private const val STANDINGS_SEARCH = "https://api.sportradar.com/icehockey/trial/v2/en/seasons/sr%3Aseason%3A122567/standings.json?api_key=${API_KEY}"
private val client = AsyncHttpClient()

class StandingsFragment : Fragment() {
    private lateinit var tableLayout: TableLayout
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_standings, container, false)

        tableLayout = view.findViewById(R.id.standingsTable)
        progressBar = view.findViewById(R.id.progressBar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tableLayout.visibility = View.GONE
        fetchHeaderRow()
        fetchStandings()
        tableLayout.visibility = View.VISIBLE
    }

    private fun fetchStandings() {
        progressBar.visibility = View.VISIBLE

        client.get(STANDINGS_SEARCH, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                if (!isAdded) return
                progressBar.visibility = View.GONE
                Log.e(TAG, "Failed to fetch standings: $statusCode")
                Toast.makeText(context, "Failed to fetch standings. Please try again.", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                if (!isAdded) return
                try {
                    val allStandings = json?.jsonObject?.getJSONArray("standings")
                    val totalStandings = allStandings?.getJSONObject(0)
                    val groups = totalStandings?.getJSONArray("groups")?.getJSONObject(0)
                    val standings = groups?.getJSONArray("standings")

                    if (standings != null) {
                        for (j in 0 until standings.length()) {
                            val standing = standings.getJSONObject(j)
                            val rank = standing.getInt("rank")
                            val teamName = standing.getJSONObject("competitor").getString("name")

                            val gamesPlayed = standing.getInt("played")
                            val points = standing.getInt("points")
                            val wins = standing.getInt("win_normaltime")
                            val overtimeWins = standing.getInt("win_overtime") + standing.getInt("win_shootout")
                            val overtimeLosses = standing.getInt("loss_overtime") + standing.getInt("loss_shootout")
                            val losses = standing.getInt("loss_normaltime")

                            val tableRow = TableRow(requireContext())
                            val rowData = listOf(
                                rank.toString(),
                                teamName,
                                points.toString(),
                                wins.toString(),
                                overtimeWins.toString(),
                                overtimeLosses.toString(),
                                losses.toString(),
                                gamesPlayed.toString()
                            )

                            for (cell in rowData) {
                                if (cell == teamName) {
                                    val linearLayout = LinearLayout(requireContext())

                                    val teamNameTextView = TextView(requireContext())
                                    teamNameTextView.text = teamName

                                    val logoImageView = ImageView(requireContext())
                                    val logoLayoutParams = LinearLayout.LayoutParams(48, 48)
                                    logoImageView.setImageResource(getTeamLogoResource(teamName))
                                    logoLayoutParams.gravity = Gravity.CENTER
                                    logoLayoutParams.marginEnd = 10
                                    logoImageView.layoutParams = logoLayoutParams

                                    linearLayout.addView(logoImageView)
                                    linearLayout.addView(teamNameTextView)
                                    linearLayout.gravity = Gravity.START
                                    linearLayout.setPadding(16, 16, 16, 16)

                                    tableRow.addView(linearLayout)
                                } else {
                                    val cellTextView = TextView(requireContext())
                                    cellTextView.text = cell
                                    cellTextView.setPadding(16, 16, 16, 16)
                                    cellTextView.gravity = Gravity.CENTER

                                    tableRow.addView(cellTextView)
                                }
                            }
                            tableLayout.addView(tableRow)
                            val divider = MaterialDivider(requireContext())
                            tableLayout.addView(divider)
                        }
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Failed to parse standings: ${e.localizedMessage}")
                }
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun fetchHeaderRow() {
        val headerRow = TableRow(requireContext())
        val headers = listOf("", "Team", "PTS", "W", "OTW", "OTL", "L", "GP")

        for (header in headers) {
            val headerTextView = TextView(requireContext())
            headerTextView.text = header
            headerTextView.setPadding(16, 16, 16, 16)

            if (header == "Team") {
                headerTextView.gravity = Gravity.START
            } else {
                headerTextView.gravity = Gravity.CENTER
            }

            headerTextView.setTypeface(null, Typeface.BOLD)
            headerTextView.setBackgroundColor(getResources().getColor(R.color.headers))
            
            headerRow.addView(headerTextView)
        }

        tableLayout.addView(headerRow)
        val divider = MaterialDivider(requireContext())
        tableLayout.addView(divider)
    }

    private fun getTeamLogoResource(teamName: String): Int {
        if (!isAdded) return R.drawable.baseline_sports_hockey_24
        return when (teamName) {
            "Boston Fleet" -> R.drawable.bostonfleet
            "Minnesota Frost" -> R.drawable.minnesotafrost
            "Montreal Victoire" -> R.drawable.montrealvictoire
            "New York Sirens" -> R.drawable.newyorksirens
            "Ottawa Charge" -> R.drawable.ottawacharge
            "Toronto Sceptres" -> R.drawable.torontosceptres
            else -> R.drawable.baseline_sports_hockey_24
        }
    }
}
