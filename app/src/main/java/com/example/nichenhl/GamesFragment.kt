package com.example.nichenhl

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.fragment.app.activityViewModels

private const val TAG = "GamesFragment"
private const val API_KEY = BuildConfig.API_KEY
private const val GAMES_SEARCH = "https://api.sportradar.com/icehockey/trial/v2/en/seasons/sr%3Aseason%3A122567/summaries.json?api_key=${API_KEY}"
private val client = AsyncHttpClient()

class GamesViewModel : ViewModel() {
    var currentDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
}

class GamesFragment : Fragment() {
    private lateinit var adapter: GameAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var dateTextView: TextView
    private lateinit var database: AppDatabase
    private var games: MutableList<Game> = mutableListOf()
    private val viewModel: GamesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_games, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.gamesRecyclerView)
        recyclerView.visibility = View.GONE
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = GameAdapter(games, requireActivity().supportFragmentManager)
        recyclerView.adapter = adapter

        database = AppDatabase.getInstance(requireContext())

        // Use the currentDate from ViewModel
        dateTextView = view.findViewById(R.id.dateTextView)
        dateTextView.text = viewModel.currentDate

        fetchGamesFromApi()

        recyclerView.visibility = View.VISIBLE
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageView: ImageView = view.findViewById(R.id.titleImage)

        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode) {
            imageView.setImageResource(R.drawable.pwhl_logo_transparent_white)
        } else {
            imageView.setImageResource(R.drawable.pwhl_logo_transparent_black)
        }

        val nextButton: Button = view.findViewById(R.id.nextDayButton)
        nextButton.setOnClickListener {
            viewModel.currentDate = getNextDate(viewModel.currentDate)
            dateTextView.text = viewModel.currentDate
            updateGamesList(getGamesForDate(games, viewModel.currentDate))
        }

        val prevButton: Button = view.findViewById(R.id.prevDayButton)
        prevButton.setOnClickListener {
            viewModel.currentDate = getPreviousDate(viewModel.currentDate)
            dateTextView.text = viewModel.currentDate
            updateGamesList(getGamesForDate(games, viewModel.currentDate))
        }
    }

    private fun fetchGamesFromApi() {
        progressBar.visibility = View.VISIBLE

        client.get(GAMES_SEARCH, object : JsonHttpResponseHandler() {
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
                    val summaries = json?.jsonObject?.getJSONArray("summaries")

                    if (summaries != null) {
                        for (i in 0 until summaries.length()) {
                            val summary = summaries.getJSONObject(i)
                            val sportEvent = summary.getJSONObject("sport_event")
                            val id = sportEvent.getString("id")
                            val startTime = sportEvent.getString("start_time")
                            val competitors = sportEvent.getJSONArray("competitors")
                            var homeTeam = ""
                            var awayTeam = ""
                            var homeScore = 0
                            var awayScore = 0

                            for (j in 0 until competitors.length()) {
                                val competitor = competitors.getJSONObject(j)
                                val teamName = competitor.getString("name")
                                val qualifier = competitor.getString("qualifier")
                                if (qualifier == "home") {
                                    homeTeam = teamName
                                } else if (qualifier == "away") {
                                    awayTeam = teamName
                                }
                            }

                            try {
                                // Parse the start time with timezone offset
                                val format = SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ssXXX",
                                    Locale.getDefault()
                                )
                                format.timeZone = TimeZone.getTimeZone("UTC")
                                val parsedStartTime = format.parse(startTime)

                                if (parsedStartTime != null) {
                                    // Convert time to EST
                                    val calendar = Calendar.getInstance()
                                    calendar.time = parsedStartTime
                                    calendar.add(Calendar.HOUR, -5)

                                    // Format the adjusted time back to string
                                    val adjustedStartTime = SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss'Z'",
                                        Locale.getDefault()
                                    )
                                    adjustedStartTime.timeZone = TimeZone.getTimeZone("UTC")
                                    val adjustedStartTimeStr =
                                        adjustedStartTime.format(calendar.time)

                                    val sportEventStatus =
                                        summary.getJSONObject("sport_event_status")
                                    val matchStatus = sportEventStatus.getString("match_status")

                                    if (matchStatus == "ended") {
                                        homeScore = sportEventStatus.getInt("home_score")
                                        awayScore = sportEventStatus.getInt("away_score")
                                    }

                                    val game = Game(
                                        id = id,
                                        startTime = adjustedStartTimeStr,
                                        homeTeam = homeTeam,
                                        awayTeam = awayTeam,
                                        awayScore = awayScore,
                                        homeScore = homeScore
                                    )

                                    games.add(game)
                                    updateGamesList(games)
                                    lifecycleScope.launch(Dispatchers.IO) {
                                        val existingGame = database.gameDao().getGameById(id)
                                        if (existingGame == null) {
                                            // If the game does not exist, create and add to the list
                                            val gameEntity = GameEntity(
                                                id = id,
                                                homeTeam = homeTeam,
                                                awayTeam = awayTeam,
                                                startTime = startTime
                                            )

                                            database.gameDao().insert(gameEntity)
                                            Log.d(TAG, "Inserting: ${gameEntity}")
                                        }
                                    }
                                } else {
                                    println("Invalid start time format for game ID $id: $startTime")
                                }
                            } catch (e: ParseException) {
                                println("Error parsing start time for game ID $id: $startTime")
                            }
                        }
                    }
                    updateGamesList(getGamesForDate(this@GamesFragment.games, viewModel.currentDate))
                    Log.d(TAG, "Fetched games: ${games.size}")
                } catch (e: JSONException) {
                    Log.e(TAG, "Failed to parse games: ${e.localizedMessage}")
                }
                progressBar.visibility = View.GONE
            }
        })
    }

    override fun onResume() {
        super.onResume()
        games.clear()
        updateGamesList(getGamesForDate(this@GamesFragment.games, viewModel.currentDate))
    }

    private fun getGamesForDate(games: List<Game>, selectedDate: String): List<Game> {
        return games.filter { game ->
            val gameDate = game.startTime.substring(0, 10)
            gameDate == selectedDate
        }
    }

    private fun getNextDate(currentDate: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(currentDate) ?: Date()
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        return dateFormat.format(calendar.time)
    }

    private fun getPreviousDate(currentDate: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.time = dateFormat.parse(currentDate) ?: Date()
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        return dateFormat.format(calendar.time)
    }

    private fun updateGamesList(games: List<Game>) {
        adapter.updateGames(games)
    }
}
