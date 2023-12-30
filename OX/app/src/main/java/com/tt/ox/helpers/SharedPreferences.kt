package com.tt.ox.helpers

import android.content.Context
import com.tt.ox.DARK_MODE_AUTO
import com.tt.ox.MOVES
import com.tt.ox.O
import com.tt.ox.X

class SharedPreferences {
    companion object {
        fun saveMainPlayer(context:Context?, name:String){
            context?.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("player_name_set_up",true)
                editor.putString("name",name)
                editor.apply()
            }
        }

        fun checkIfPlayerNameSetUp(context:Context?):Boolean{
            var boolean = false
            context?.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                boolean = sharedPreferences.getBoolean("player_name_set_up",false)
            }
            return boolean
        }

        fun readPlayerName(context:Context?):String{
            var name = ""
            context?.let {
                val sharedPreferences = it.getSharedPreferences("Player_Name", Context.MODE_PRIVATE)
                name = sharedPreferences.getString("name","").toString()
            }
            return name
        }

        fun saveMoves(context: Context?,moves:Int){
            context?.let {
                val sharedPreferences = it.getSharedPreferences("Moves",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putInt("moves",moves)
                editor.apply()
            }
        }

        fun readMoves(context: Context?):Int{
            var moves: Int = MOVES
            context?.let {
                val sharedPreferences = it.getSharedPreferences("Moves",Context.MODE_PRIVATE)
                moves = sharedPreferences.getInt("moves", MOVES)
            }
            return moves
        }

        fun saveOnlineMoves(context: Context?,moves:Int){
            context?.let {
                val sp = it.getSharedPreferences("OnlineMoves",Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putInt("online_moves",moves)
                editor.apply()
            }
        }

        fun readOnlineMoves(context: Context?):Int{
            var moves: Int = MOVES
            context?.let {
                val sharedPreferences = it.getSharedPreferences("OnlineMoves",Context.MODE_PRIVATE)
                moves = sharedPreferences.getInt("online_moves", MOVES)
            }
            return moves
        }

        fun readMarks(context: Context?):Marks{
            val marks = Marks()
            context?.let {
                val sharedPreferences = it.getSharedPreferences("Marks",Context.MODE_PRIVATE)
                marks.playerMark = sharedPreferences.getInt("playerMark", X)
                marks.playerColor = sharedPreferences.getInt("playerColor", COLOR_BLUE)
                marks.opponentMark = sharedPreferences.getInt("opponentMark", O)
                marks.opponentColor = sharedPreferences.getInt("opponentColor", COLOR_RED)
            }
            return marks
        }

        fun saveMarks(context: Context?, marks:Marks){
            context?.let {
                val sp = it.getSharedPreferences("Marks",Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putInt("playerMark", marks.playerMark)
                editor.putInt("playerColor", marks.playerColor)
                editor.putInt("opponentMark", marks.opponentMark)
                editor.putInt("opponentColor", marks.opponentColor)
                editor.apply()
            }
        }

        fun readRandomMarks(context: Context?):Boolean{
            var random = true
            context?.let {
                val sp = it.getSharedPreferences("Random",Context.MODE_PRIVATE)
                random = sp.getBoolean("random",true)
            }
            return random
        }

        fun saveRandomMarks(context: Context?, random:Boolean){
            context?.let {
                val sp = it.getSharedPreferences("Random",Context.MODE_PRIVATE)
                val editor = sp.edit()
                editor.putBoolean("random",random)
                editor.apply()
            }
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
                darkMode = sharedPreferences.getInt("dark_mode", DARK_MODE_AUTO)
            }
            return darkMode
        }

       fun readButtonSound(context: Context?):Boolean{
           var sound = true
           context?.let {
               val sp = it.getSharedPreferences("SOUNDS",Context.MODE_PRIVATE)
               sound = sp.getBoolean("button_sound",true)
           }
           return sound
       }

        fun readEffectsSound(context: Context?):Boolean{
            var sound = true
            context?.let {
                val sp = it.getSharedPreferences("SOUNDS",Context.MODE_PRIVATE)
                sound = sp.getBoolean("effects_sound",true)
            }
            return sound
        }

        fun saveButtonSound(context: Context?,sound:Boolean){
            context?.let {
                val sharedPreferences = it.getSharedPreferences("SOUNDS",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("button_sound",sound)
                editor.apply()
            }
        }
        fun saveEffectsSound(context: Context?,sound:Boolean){
            context?.let {
                val sharedPreferences = it.getSharedPreferences("SOUNDS",Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putBoolean("effects_sound",sound)
                editor.apply()
            }
        }


        fun readNumberOfAppsFromMemory(context:Context?):Int{
            var numberOfApps = 0
            context?.let {
                val sp = context.getSharedPreferences("APPS_IN_MEMORY",Context.MODE_PRIVATE)
                numberOfApps = sp.getInt("apps_in_memory",0)
            }
            return numberOfApps
        }

        fun saveNumberOfAppsFromMemory(context:Context?, numberOfApps:Int){
            context?.let {
                val sp = context.getSharedPreferences("APPS_IN_MEMORY",Context.MODE_PRIVATE)
                val edit = sp.edit()
                edit.putInt("apps_in_memory",numberOfApps)
                edit.apply()
            }
        }

        fun saveNewAppAvailable(context:Context?,boolean: Boolean){
            context?.let {
                val sp = context.getSharedPreferences("NEW_APP",Context.MODE_PRIVATE)
                val edit = sp.edit()
                edit.putBoolean("new_app",boolean)
                edit.apply()
            }
        }

        fun readNewAppAvailable(context:Context?):Boolean{
            var boolean = false
            context?.let {
                val sp = context.getSharedPreferences("NEW_APP",Context.MODE_PRIVATE)
                boolean = sp.getBoolean("new_app",false)
            }
            return boolean
        }
    }
}