package com.tt.ox.helpers

import android.content.Context
import android.content.res.Configuration
import com.tt.ox.DARK_MODE_OFF
import com.tt.ox.DARK_MODE_ON
import com.tt.ox.R

class Theme(val context:Context) {

    private val darkMode = SharedPreferences.readDarkMode(context)
    private val darkModeBoolean = checkDarkMode(darkMode)

    private fun checkDarkMode(darkMode: Int): Boolean {
        return when(darkMode){
            DARK_MODE_ON -> true
            DARK_MODE_OFF -> false
            else -> checkSystemDarkMode()
        }
    }

    private fun checkSystemDarkMode(): Boolean {
        val boolean = when(context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)){
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
        return boolean
    }

    private val theme = if(darkModeBoolean) DarkMode() else LightMode()

    fun getBackgroundColor() = theme.getBackgroundColor()
    private open class LightMode{
        open val background = R.color.white

        open fun getBackgroundColor():Int{
            return background
        }

    }

    private class DarkMode : LightMode(){
        override val background = R.color.black

        override fun getBackgroundColor(): Int {
            return background
        }
    }
}