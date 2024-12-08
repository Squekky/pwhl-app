package com.example.nichenhl

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class GameAdapter(private var games: List<Game>) : RecyclerView.Adapter<GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game)
    }

    fun updateGames(newGames: List<Game>) {
        this.games = newGames
        notifyDataSetChanged()
    }


    override fun getItemCount(): Int = games.size
}

class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val awayTeamLogoImage: ImageView = itemView.findViewById(R.id.awayTeamLogo)
    private val awayTeamTextView: TextView = itemView.findViewById(R.id.awayTeamName)
    private val homeTeamLogoImage: ImageView = itemView.findViewById(R.id.homeTeamLogo)
    private val homeTeamTextView: TextView = itemView.findViewById(R.id.homeTeamName)
    private val gameStartTimeTextView: TextView = itemView.findViewById(R.id.startTime)
    private val awayScoreTextView: TextView = itemView.findViewById(R.id.awayScore)
    private val homeScoreTextView: TextView = itemView.findViewById(R.id.homeScore)

    fun bind(game: Game) {
        awayTeamTextView.text = game.awayTeam
        homeTeamTextView.text = game.homeTeam

        Glide.with(itemView.context)
            .load(getTeamLogoResource(game.awayTeam))
            .into(awayTeamLogoImage)

        Glide.with(itemView.context)
            .load(getTeamLogoResource(game.homeTeam))
            .into(homeTeamLogoImage)

        // Current time in milliseconds
        val currentTime = System.currentTimeMillis()
        Log.d("GameAdapter", "Current time: $currentTime")

        // Parse the game start time, handle timezone if necessary
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val gameStartTimeMillis = try {
            format.parse(game.startTime)?.time ?: 0L
        } catch (e: Exception) {
            Log.e("GameAdapter", "Error parsing game start time: ${game.startTime}")
            0L
        }

        Log.d("GameAdapter", "Game start time (in millis): $gameStartTimeMillis")

        if (gameStartTimeMillis > currentTime) {
            // Game is in the future - show the start time
            gameStartTimeTextView.text = formatTime(game.startTime)
            awayScoreTextView.visibility = View.GONE
            homeScoreTextView.visibility = View.GONE
            gameStartTimeTextView.visibility = View.VISIBLE
            Log.d("GameAdapter", "Game is in the future, showing start time: ${game.startTime}")
        } else {
            // Game is over - show the scores
            gameStartTimeTextView.text = "Game Over"
            awayScoreTextView.visibility = View.VISIBLE
            homeScoreTextView.visibility = View.VISIBLE

            if (game.awayScore > game.homeScore) {
                awayScoreTextView.text = Html.fromHtml("<b>${game.awayScore}</b>")
                homeScoreTextView.text = "${game.homeScore}"
            } else {
                awayScoreTextView.text = "${game.awayScore}"
                homeScoreTextView.text = Html.fromHtml("<b>${game.homeScore}</b>")
            }
            gameStartTimeTextView.visibility = View.GONE // Hide the start time when the game is over
            Log.d("GameAdapter", "Game is over, showing scores: Away: ${game.awayScore}, Home: ${game.homeScore}")
        }
    }

    private fun getTeamLogoResource(teamName: String): Int {
        return when (teamName) {
            "Boston Fleet" -> R.drawable.bostonfleet
            "Minnesota Frost" -> R.drawable.minnesotafrost
            "Montreal Victoire" -> R.drawable.montrealvictoire
            "New York Sirens" -> R.drawable.newyorksirens
            "Ottawa Charge" -> R.drawable.ottawacharge
            "Toronto Sceptres" -> R.drawable.torontosceptres
            else -> R.drawable.baseline_sports_hockey_24 // Default logo if no match is found
        }
    }

    private fun formatTime(startTime: String): String {
        // Parse and format the start time to display as needed
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = format.parse(startTime)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault()) // For 12-hour format
        val formattedTime = timeFormat.format(date)
        Log.d("GameAdapter", "Formatted start time: $formattedTime")
        return formattedTime
    }
}
