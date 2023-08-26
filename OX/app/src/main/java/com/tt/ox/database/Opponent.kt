package com.tt.ox.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Opponent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name:String = "",
    val wins:Int = 0,
    val loses:Int = 0
)