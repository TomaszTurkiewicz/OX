package com.tt.ox.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tt.ox.O
import com.tt.ox.X

@Entity
data class Opponent(
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0,
    private var name:String = "",
    private var wins:Int = 0,
    private var loses:Int = 0,
    private var mainPlayerMark:Int = X,
    private var opponentMark:Int = O
){
    fun getId():Int{
        return this.id
    }

    fun getName():String{
        return this.name
    }

    fun getWins():Int{
        return this.wins
    }

    fun getLoses():Int{
        return this.loses
    }

    fun getMainPlayerMark():Int{
        return this.mainPlayerMark
    }

    fun getOpponentMark():Int{
        return this.opponentMark
    }
}