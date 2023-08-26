package com.tt.ox.helpers

import android.content.Context

class SharedPreferences {
    companion object {
        fun saveMainPlayer(context:Context,player: Player){
            context.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("player_name",player.name.value!!)
                editor.putBoolean("player_name_set_up",true)
                editor.putInt("wins",player.wins.value!!)
                editor.putInt("loses",player.loses.value!!)
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

        fun readPlayer(context: Context):Player {
            val player = Player()
            context.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                val name = sharedPreferences.getString("player_name","_").toString()
                val wins = sharedPreferences.getInt("wins",0)
                val loses = sharedPreferences.getInt("loses",0)

                player.setName(name)
                player.setWins(wins)
                player.setLoses(loses)
            }
            return player
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
    }
}