package com.example.nichenhl

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import androidx.appcompat.widget.SwitchCompat
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "SettingsFragment"
private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

class SettingsFragment : Fragment() {
    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var notificationsScheduled = false
    private val teams = listOf(
        Team("None", R.drawable.baseline_sports_hockey_24),
        Team("Boston Fleet", R.drawable.bostonfleet),
        Team("Minnesota Frost", R.drawable.minnesotafrost),
        Team("Montreal Victoire", R.drawable.montrealvictoire),
        Team("New York Sirens", R.drawable.newyorksirens),
        Team("Ottawa Charge", R.drawable.ottawacharge),
        Team("Toronto Sceptres", R.drawable.torontosceptres)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = AppDatabase.getInstance(requireContext())

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Spinner
        val teamSpinner: Spinner = view.findViewById(R.id.selectTeamSpinner)
        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        // Get the saved team name or default to the first team (None)
        val savedTeamName = sharedPreferences.getString("selectedTeam", teams[0].name)

        // Set up adapter
        val adapter = TeamSpinnerAdapter(requireContext(), teams)
        teamSpinner.adapter = adapter

        // Set the spinner's selected value to the saved team
        val position = teams.indexOfFirst { it.name == savedTeamName }
        if (position >= 0) {
            teamSpinner.setSelection(position)
        }

        // Save the selection when a new team is chosen
        teamSpinner.post {
            teamSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    Log.d(TAG, "on item selected")
                    val selectedTeamItem = teams[position]
                    with(sharedPreferences.edit()) {
                        putString("selectedTeam", selectedTeamItem.name)
                        apply()
                    }

                    // Cancel scheduled notifications and reschedule for the new selected team
                    cancelAllScheduledAlarms()
                    if (selectedTeamItem.name != "None") {
                        notificationsScheduled = false
                        if (notificationSwitch.isChecked) {
                            checkAndNotifyForFavoriteTeam(selectedTeamItem.name)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        // Notification switch
        notificationSwitch = view.findViewById(R.id.enableNotificationsSwitch)
        notificationSwitch.isChecked = sharedPreferences.getBoolean("notificationsEnabled", true)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, "notif switched changed")
            // Save the state of the switch
            sharedPreferences.edit().putBoolean("notificationsEnabled", isChecked).apply()
            if (isChecked) {
                // If notifications are enabled, reschedule notifications
                val selectedTeam = sharedPreferences.getString("selectedTeam", "None") ?: "None"
                if (selectedTeam != "None") {
                    checkAndNotifyForFavoriteTeam(selectedTeam)
                }
            } else {
                // If notifications are disabled, cancel all alarms and reset the flag
                cancelAllScheduledAlarms()
                notificationsScheduled = false // Reset the flag to avoid skipping re-scheduling
            }
        }
        return view
    }

    private fun cancelAllScheduledAlarms() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val sharedPreferences = requireContext().getSharedPreferences("scheduled_alarms", Context.MODE_PRIVATE)
        val allPrefs = sharedPreferences.all

        for (entry in allPrefs) {
            if (entry.key.startsWith("team_")) {
                val gameHashCode = entry.key.substringAfter("team_").toIntOrNull()
                gameHashCode?.let {
                    val intent = Intent(requireContext(), NotificationReceiver::class.java)
                    val pendingIntent = PendingIntent.getBroadcast(
                        requireContext(), gameHashCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    // Cancel the alarm
                    alarmManager.cancel(pendingIntent)
                    Log.d(TAG, "Cancelled alarm for game with hash code: $gameHashCode")

                    // Remove the alarm from shared preferences after cancellation
                    sharedPreferences.edit().remove(entry.key).apply()
                }
            }
        }
    }

    private fun checkAndNotifyForFavoriteTeam(favoriteTeam: String) {
        // Prevent re-scheduling if notifications are already scheduled
        if (notificationsScheduled) {
            Log.d(TAG, "Notifications are already scheduled, skipping re-scheduling.")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val gamesForTeam = database.gameDao().getGamesForTeam(favoriteTeam)
            Log.d(TAG, "gamesForTeam: $gamesForTeam")

            if (gamesForTeam.isNotEmpty()) {
                gamesForTeam.forEach { game ->
                    Log.d(TAG, "Scheduling notification for game: ${game.homeTeam} vs ${game.awayTeam} at ${game.startTime}")
                    scheduleNotificationForGame(game)
                }
                notificationsScheduled = true // Mark notifications as scheduled
            } else {
                Log.d(TAG, "No upcoming games for team: $favoriteTeam")
            }
        }
    }

    private fun scheduleNotificationForGame(game: GameEntity) {
        // Check if notifications are enabled and if exact alarm permission is granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = requireContext().getSystemService(AlarmManager::class.java)

            // Check if the app has permission to schedule exact alarms
            if (!alarmManager.canScheduleExactAlarms()) {
                // Request the user to grant the permission
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivityForResult(intent, NOTIFICATION_PERMISSION_REQUEST_CODE)
                return
            }
        }
        proceedWithNotificationScheduling(game)
    }

    private fun proceedWithNotificationScheduling(game: GameEntity) {
        val sharedPreferences = requireContext().getSharedPreferences("scheduled_alarms", Context.MODE_PRIVATE)

        // Continue with scheduling if notifications are enabled
        val gameStartTime = game.startTime
        val calendar = Calendar.getInstance()
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

        val gameTime = format.parse(gameStartTime)
        if (gameTime != null && gameTime.after(calendar.time)) {
            // Schedule the notification 15 minutes before the game
            calendar.time = gameTime
            calendar.add(Calendar.MINUTE, -15)

            val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(requireContext(), NotificationReceiver::class.java).apply {
                putExtra("homeTeam", game.homeTeam)
                putExtra("awayTeam", game.awayTeam)
            }

            intent.action = "com.example.nichenhl.ACTION_NOTIFICATION"

            val gameHashCode = game.hashCode()
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(), gameHashCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            // Save the alarm to shared preferences
            sharedPreferences.edit().putInt("team_${gameHashCode}", gameHashCode).apply()

            Log.d(TAG, "Scheduled notification for game: ${game.homeTeam} vs ${game.awayTeam} at ${calendar.time}")
        } else {
            Log.d(TAG, "Game is in the past, ignoring: ${game.homeTeam} vs ${game.awayTeam} at ${gameStartTime}")
        }
    }
}
