package com.tt.ox.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Opponent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val opponentName:String,
    val mainPlayerWin:Int,
    val opponentWin:Int
)