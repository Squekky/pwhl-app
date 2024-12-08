package com.example.nichenhl

data class Game(
    val id: String,
    val startTime: String,
    val homeTeam: String,
    val awayTeam: String,
    val awayScore: Int = 0,
    val homeScore: Int = 0
)

