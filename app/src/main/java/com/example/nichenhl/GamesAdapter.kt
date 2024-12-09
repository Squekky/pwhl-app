package com.example.nichenhl

import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class GameAdapter(private var games: List<Game>, private val fragmentManager: FragmentManager) : RecyclerView.Adapter<GameViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_game, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game, fragmentManager)
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

    fun bind(game: Game, fragmentManager: FragmentManager) {
        awayTeamTextView.text = game.awayTeam
        homeTeamTextView.text = game.homeTeam

        awayTeamLogoImage.setImageResource(getTeamLogoResource(game.awayTeam))
        homeTeamLogoImage.setImageResource(getTeamLogoResource(game.homeTeam))

        val currentTime = System.currentTimeMillis()

        // Parse the game start time
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val gameStartTimeMillis = try {
            format.parse(game.startTime)?.time ?: 0L
        } catch (e: Exception) {
            Log.e("GameAdapter", "Error parsing game start time: ${game.startTime}")
            0L
        }

        if (gameStartTimeMillis > currentTime) {
            // Show the start time if the game is in the future, and disable box scores
            gameStartTimeTextView.text = formatTime(game.startTime)
            awayScoreTextView.visibility = View.GONE
            homeScoreTextView.visibility = View.GONE
            gameStartTimeTextView.visibility = View.VISIBLE
            itemView.setOnClickListener(null)
        } else {
            // Show scores if the game is over, and enable box scores
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
            itemView.setOnClickListener {
                openBoxScoreFragment(game, fragmentManager)
            }
        }
    }

    private fun openBoxScoreFragment(game: Game, fragmentManager: FragmentManager) {
        val boxScoreFragment = BoxScoreFragment.newInstance(game.id)
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.pwhl_frame_layout, boxScoreFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun getTeamLogoResource(teamName: String): Int {
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

    private fun formatTime(startTime: String): String {
        // Parse and format the start time to display as needed
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = format.parse(startTime)
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(date)
        return formattedTime
    }
}
