package com.tt.ox.helpers

import android.content.Context
import com.tt.ox.DARK_MODE_AUTO
import com.tt.ox.MOVES
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
            var boolean: Boolean
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
                name = sharedPreferences.getString("name","").toString()
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
            var moves: Int
            context.let {
                val sharedPreferences = it.getSharedPreferences("Moves",Context.MODE_PRIVATE)
                moves = sharedPreferences.getInt("moves", MOVES)
            }
            return moves
        }

        fun saveOnlineMoves(context: Context,moves:Int){
            context.let {
                val sp = it.getSharedPreferences("OnlineMoves",Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putInt("online_moves",moves)
                editor.apply()
            }
        }

        fun readOnlineMoves(context: Context):Int{
            var moves: Int
            context.let {
                val sharedPreferences = it.getSharedPreferences("OnlineMoves",Context.MODE_PRIVATE)
                moves = sharedPreferences.getInt("online_moves", MOVES)
            }
            return moves
        }

        fun readMarks(context: Context):Marks{
            val marks = Marks()
            context.let {
                val sharedPreferences = it.getSharedPreferences("Marks",Context.MODE_PRIVATE)
                marks.playerMark = sharedPreferences.getInt("playerMark", X)
                marks.playerColor = sharedPreferences.getInt("playerColor", COLOR_BLUE)
                marks.opponentMark = sharedPreferences.getInt("opponentMark", O)
                marks.opponentColor = sharedPreferences.getInt("opponentColor", COLOR_RED)
            }
            return marks
        }

        fun readRandomMarks(context: Context):Boolean{
            var random:Boolean
            context.let {
                val sp = it.getSharedPreferences("Random",Context.MODE_PRIVATE)
                random = sp.getBoolean("random",true)
            }
            return random
        }

        fun saveDarkMode(context: Context?,darkMode:Int){
            context?.let {
                val sharedPreferences = context.getSharedPreferences("DARK_MODE",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("dark_mode",darkMode)
                editor.apply()
            }
        }

        fun readDarkMode(context: Context?):Int{
            var darkMode = 0
            context?.let {
                val sharedPreferences = context.getSharedPreferences("DARK_MODE",Context.MODE_PRIVATE)
                darkMode = sharedPreferences.getInt("dark_mode",DARK_MODE_AUTO)
            }
            return darkMode
        }

    }
}