package com.example.nichenhl

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(
    val id: String,
    val startTime: String,
    val homeTeam: String,
    val awayTeam: String,
    val awayScore: Int = 0,
    val homeScore: Int = 0
): Parcelable

data class Team(
    val name: String,
    var logoResId: Int
)
