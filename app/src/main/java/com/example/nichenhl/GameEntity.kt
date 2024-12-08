package com.example.nichenhl

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: String,
    val homeTeam: String,
    val awayTeam: String,
    val startTime: String
)
