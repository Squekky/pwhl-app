package com.example.nichenhl

import android.app.Activity
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
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private const val TAG = "SettingsFragment"

class SettingsFragment : Fragment() {
    private lateinit var notificationSwitch: SwitchCompat
    private lateinit var database: AppDatabase
    private lateinit var sharedPreferences: SharedPreferences
    private var notificationsScheduled = false
    private var teams = mutableListOf(
        Team("None", R.drawable.baseline_sports_hockey_24),
        Team("Boston Fleet", R.drawable.bostonfleet),
        Team("Minnesota Frost", R.drawable.minnesotafrost),
        Team("Montreal Victoire", R.drawable.montrealvictoire),
        Team("New York Sirens", R.drawable.newyorksirens),
        Team("Ottawa Charge", R.drawable.ottawacharge),
        Team("Toronto Sceptres", R.drawable.torontosceptres)
    )

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Notification permission granted.")
            checkAndRequestExactAlarmPermission(false) // Request alarm permission right after notification permission is granted
        } else {
            Log.d(TAG, "Notification permission denied.")
            Toast.makeText(context, "Notification permission denied. Check your device settings.", Toast.LENGTH_SHORT).show()
            notificationSwitch.isChecked = false // Reset the switch if denied
        }
    }

    private val requestExactAlarmPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        notificationSwitch.isChecked = false
        if (result.resultCode == Activity.RESULT_CANCELED) {
            // Check if they enabled alarms again
            checkAndRequestExactAlarmPermission(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = AppDatabase.getInstance(requireContext())

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Spinner
        val teamSpinner: Spinner = view.findViewById(R.id.selectTeamSpinner)
        sharedPreferences =
            requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        // Get the saved team name or default to the first team (None)
        val savedTeamName = sharedPreferences.getString("selectedTeam", teams[0].name)

        // Set up adapter
        val adapter = TeamSpinnerAdapter(requireContext(), teams)
        teamSpinner.adapter = adapter

        // Adjust "None" placeholder for theme
        val isDarkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        if (isDarkMode) {
            teams[0].logoResId = R.drawable.pwhl_logo_transparent_white
        } else {
            teams[0].logoResId = R.drawable.pwhl_logo_transparent_black
        }

        // Set the spinner's selected value to the saved team
        val position = teams.indexOfFirst { it.name == savedTeamName }
        if (position >= 0) {
            teamSpinner.setSelection(position)
        }

        // Save the selection when a new team is chosen
        teamSpinner.post {
            teamSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedTeamItem = teams[position]
                    with(sharedPreferences.edit()) {
                        putString("selectedTeam", selectedTeamItem.name)
                        apply()
                    }

                    // Cancel scheduled notifications and reschedule for new team
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
        notificationSwitch.isChecked = sharedPreferences.getBoolean("notificationsEnabled", false)
        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("notificationsEnabled", isChecked).apply()
            if (isChecked) {
                checkAndRequestPermissions()
            } else {  // Cancel alarms when notifications are disabled
                cancelAllScheduledAlarms()
                notificationsScheduled = false
                sharedPreferences.edit().putBoolean("notificationsEnabled", false).apply()
            }
        }
        return view
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission = "android.permission.POST_NOTIFICATIONS"
            if (ContextCompat.checkSelfPermission(requireContext(), notificationPermission) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Requesting notification permissions")
                requestPermissionLauncher.launch(notificationPermission)
                return
            }
        }
        Log.d(TAG, "Notification permission granted or not required")
        checkAndRequestExactAlarmPermission(false)
    }

    private fun checkAndRequestExactAlarmPermission(alreadyChecked: Boolean) {
        val alarmManager = requireContext().getSystemService(AlarmManager::class.java)
        if (!alarmManager.canScheduleExactAlarms()) {
            if (alreadyChecked) return // Prevent users from an infinite loop
            Log.d(TAG, "Requesting exact alarm permissions")
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            requestExactAlarmPermissionLauncher.launch(intent)
            return
        }

        Log.d(TAG, "Exact alarm permission granted or not required")
        val selectedTeam = sharedPreferences.getString("selectedTeam", "None") ?: "None"
        notificationSwitch.isChecked = true
        if (selectedTeam != "None") {
            checkAndNotifyForFavoriteTeam(selectedTeam)
        }
        notificationsScheduled = true
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
                    sharedPreferences.edit().remove(entry.key).apply()
                    Log.d(TAG, "Cancelled alarm for game with hash code: $gameHashCode")
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

            if (gamesForTeam.isNotEmpty()) {
                gamesForTeam.forEach { game ->
                    scheduleNotification(game)
                }
                notificationsScheduled = true
            } else {
                Log.d(TAG, "No upcoming games for team: $favoriteTeam")
            }
        }
    }

    private fun scheduleNotification(game: GameEntity) {
        val sharedPreferences = requireContext().getSharedPreferences("scheduled_alarms", Context.MODE_PRIVATE)

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