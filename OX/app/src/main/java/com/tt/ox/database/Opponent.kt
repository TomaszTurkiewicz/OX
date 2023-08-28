package com.tt.ox.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tt.ox.O
import com.tt.ox.X
import com.tt.ox.helpers.COLOR_BLACK

@Entity
data class Opponent(
    @PrimaryKey(autoGenerate = true)
    private var id: Int = 0,
    private var name:String = "",
    private var wins:Int = 0,
    private var loses:Int = 0,
    private var mainPlayerMark:Int = X,
    private var mainPlayerMarkColor:Int = COLOR_BLACK,
    private var opponentMark:Int = O,
    private var opponentMarkColor:Int = COLOR_BLACK,
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

    fun getMainPlayerMarkColor():Int{
        return this.mainPlayerMarkColor
    }

    fun getOpponentMarkColor():Int{
        return this.opponentMarkColor
    }

    fun setPlayerColor(color:Int){
        this.mainPlayerMarkColor = color
    }

    fun setOpponentColor(color:Int){
        this.opponentMarkColor = color
    }
}