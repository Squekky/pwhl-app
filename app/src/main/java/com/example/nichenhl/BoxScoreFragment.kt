package com.example.nichenhl

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException
import java.text.SimpleDateFormat
import java.util.Locale

private const val TAG = "BoxScoreFragment"
private const val API_KEY = BuildConfig.API_KEY

private val client = AsyncHttpClient()
class BoxScoreFragment : Fragment(R.layout.fragment_box_score) {
    private lateinit var progressBar: ProgressBar
    private lateinit var finalScoreTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var periodOneScoreTextView: TextView
    private lateinit var periodTwoScoreTextView: TextView
    private lateinit var periodThreeScoreTextView: TextView
    private lateinit var overtimeScoreTextView: TextView
    private lateinit var shootoutScoreTextView: TextView
    private lateinit var awayTeamLogo: ImageView
    private lateinit var homeTeamLogo: ImageView
    private lateinit var gameId: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve the game ID passed via arguments
        gameId = arguments?.getString("GAME_ID") ?: ""

        gameId = gameId.replace(":", "%3A")
        val BOXSCORE_SEARCH = "https://api.sportradar.com/icehockey/trial/v2/en/sport_events/${gameId}/summary.json?api_key=${API_KEY}"

        Log.d(TAG, "url = ${BOXSCORE_SEARCH}")
        progressBar = view.findViewById(R.id.progressBar)
        dateTextView = view.findViewById(R.id.dateTextView)
        finalScoreTextView = view.findViewById(R.id.boxScoreScore)
        periodOneScoreTextView = view.findViewById(R.id.periodOneScore)
        periodTwoScoreTextView = view.findViewById(R.id.periodTwoScore)
        periodThreeScoreTextView = view.findViewById(R.id.periodThreeScore)
        overtimeScoreTextView = view.findViewById(R.id.overtimeScore)
        shootoutScoreTextView = view.findViewById(R.id.shootoutScore)
        awayTeamLogo = view.findViewById(R.id.awayTeamLogo)
        homeTeamLogo = view.findViewById(R.id.homeTeamLogo)

        progressBar.visibility = View.VISIBLE
        Log.d(TAG, "fetchGamesFromApi called")

        client.get(BOXSCORE_SEARCH, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                progressBar.visibility = View.GONE
                Log.e(TAG, "Failed to fetch games: $statusCode")
                Toast.makeText(context, "Failed to fetch games. Please try again.", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                try {
                    if (json != null) {
                        Log.d(TAG, "json: $json")
                        val jsonObject = json.jsonObject
                        Log.d(TAG, "jsonObject: $jsonObject")

                        val sportEvent = jsonObject.getJSONObject("sport_event")
                        val sportEventStatus = jsonObject.getJSONObject("sport_event_status")

                        // Extract scores
                        val homeScore = sportEventStatus.getInt("home_score")
                        val awayScore = sportEventStatus.getInt("away_score")
                        requireActivity().runOnUiThread {
                            finalScoreTextView.text = "$awayScore - $homeScore"
                        }

                        // Extract period scores
                        val periodScores = sportEventStatus.getJSONArray("period_scores")
                        for (i in 0 until periodScores.length()) {
                            val period = periodScores.getJSONObject(i)
                            when (i) {
                                0 -> requireActivity().runOnUiThread {
                                    val periodOneText = SpannableString("${period.getInt("away_score")}   P1   ${period.getInt("home_score")}")
                                    periodOneText.setSpan(RelativeSizeSpan(0.7f), periodOneText.indexOf("P1"), periodOneText.indexOf("P1") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    periodOneScoreTextView.text = periodOneText
                                }
                                1 -> requireActivity().runOnUiThread {
                                    val periodTwoText = SpannableString("${period.getInt("away_score")}   P2   ${period.getInt("home_score")}")
                                    periodTwoText.setSpan(RelativeSizeSpan(0.7f), periodTwoText.indexOf("P2"), periodTwoText.indexOf("P2") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    periodTwoScoreTextView.text = periodTwoText
                                }
                                2 -> requireActivity().runOnUiThread {
                                    val periodThreeText = SpannableString("${period.getInt("away_score")}   P3   ${period.getInt("home_score")}")
                                    periodThreeText.setSpan(RelativeSizeSpan(0.7f), periodThreeText.indexOf("P3"), periodThreeText.indexOf("P3") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    periodThreeScoreTextView.text = periodThreeText
                                }
                                3 -> requireActivity().runOnUiThread {
                                    val overtimeText = SpannableString("${period.getInt("away_score")}   OT   ${period.getInt("home_score")}")
                                    overtimeText.setSpan(RelativeSizeSpan(0.7f), overtimeText.indexOf("OT"), overtimeText.indexOf("OT") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    overtimeScoreTextView.text = overtimeText
                                    overtimeScoreTextView.visibility = View.VISIBLE
                                }
                                4 -> requireActivity().runOnUiThread {
                                    val shootoutText = SpannableString("${period.getInt("away_score")}   SO   ${period.getInt("home_score")}")
                                    shootoutText.setSpan(RelativeSizeSpan(0.7f), shootoutText.indexOf("SO"), shootoutText.indexOf("SO") + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                    shootoutScoreTextView.text = shootoutText
                                    shootoutScoreTextView.visibility = View.VISIBLE
                                }
                            }
                        }

                        // Extract and format the date
                        val startTime = sportEvent.getString("start_time")
                        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
                        val date = formatter.parse(startTime)
                        val dateFormatter = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
                        requireActivity().runOnUiThread {
                            dateTextView.text = date?.let { dateFormatter.format(it) }
                        }

                        // Extract teams' names and logos
                        val competitors = sportEvent.getJSONArray("competitors")
                        var homeTeamName = ""
                        var awayTeamName = ""
                        for (i in 0 until competitors.length()) {
                            val competitor = competitors.getJSONObject(i)
                            val teamName = competitor.getString("name")
                            val qualifier = competitor.getString("qualifier")
                            if (qualifier == "home") {
                                homeTeamName = teamName
                            } else if (qualifier == "away") {
                                awayTeamName = teamName
                            }
                        }

                        awayTeamLogo.setImageResource(getTeamLogoResource(awayTeamName))
                        homeTeamLogo.setImageResource(getTeamLogoResource(homeTeamName))
                    }
                } catch (e: JSONException) {
                    Log.e(TAG, "Error parsing game data: $e")
                } catch (e: Exception) {
                    Log.e(TAG, "Unexpected error: $e")
                }
                progressBar.visibility = View.GONE
                view.visibility = View.VISIBLE
            }
        })
    }

    fun getTeamLogoResource(teamName: String): Int {
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

    companion object {
        fun newInstance(gameId: String): BoxScoreFragment {
            val fragment = BoxScoreFragment()
            val args = Bundle()
            args.putString("GAME_ID", gameId)
            fragment.arguments = args
            return fragment
        }
    }
}