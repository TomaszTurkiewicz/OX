package com.tt.ox.helpers

import android.content.Context
import com.tt.ox.O
import com.tt.ox.X

class SharedPreferences {
    companion object {
        fun saveMainPlayer(context:Context, name:String){
            context.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("player_name_set_up",true)
                editor.putString("name",name)
                editor.apply()
            }
        }

        fun checkIfPlayerNameSetUp(context:Context):Boolean{
            var boolean = false
            context.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                boolean = sharedPreferences.getBoolean("player_name_set_up",false)
            }
            return boolean
        }

        fun readPlayerName(context:Context):String{
            var name:String
            context.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                name = sharedPreferences.getString("name","_").toString()
            }
            return name
        }

        fun saveMoves(context: Context,moves:Int){
            context.let {
                val sharedPreferences = it.getSharedPreferences("Moves",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("moves",moves)
                editor.apply()
            }
        }

        fun readMoves(context: Context):Int{
            var moves = 0
            context.let {
                val sharedPreferences = it.getSharedPreferences("Moves",Context.MODE_PRIVATE)
                moves = sharedPreferences.getInt("moves",0)
            }
            return moves
        }

        fun readOnlineMarks(context: Context):OnlineMarks{
            var onlineMarks = OnlineMarks()
            context.let {
                val sharedPreferences = it.getSharedPreferences("OnlineMarks",Context.MODE_PRIVATE)
                onlineMarks.playerMark = sharedPreferences.getInt("playerMark", X)
                onlineMarks.playerColor = sharedPreferences.getInt("playerColor", COLOR_BLACK)
                onlineMarks.opponentMark = sharedPreferences.getInt("opponentMark", O)
                onlineMarks.opponentColor = sharedPreferences.getInt("opponentColor", COLOR_BLACK)
            }
            return onlineMarks
        }
    }
}