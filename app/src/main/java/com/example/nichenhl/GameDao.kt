package com.example.nichenhl

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GameDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(games: List<GameEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert (game: GameEntity)

    @Query("SELECT * FROM games WHERE homeTeam = :team OR awayTeam = :team")
    fun getGamesForTeam(team: String): List<GameEntity>

    @Query("SELECT * FROM games")
    fun getAllGames(): List<GameEntity>

    @Query("DELETE FROM games")
    fun deleteAllGames()

    @Query("SELECT * FROM games WHERE id = :id LIMIT 1")
    fun getGameById(id: String): GameEntity?
}
