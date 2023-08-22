package com.tt.ox.helpers

import android.content.Context

class SharedPreferences {
    companion object {
        fun saveMainPlayerName(context:Context,name:String){
            context.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("player_name",name)
                editor.putBoolean("player_name_set_up",true)
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

        fun readPlayerName(context: Context):String {
            var name:String = "_"
            context.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                name = sharedPreferences.getString("player_name","_").toString()
            }
            return name
        }
    }
}