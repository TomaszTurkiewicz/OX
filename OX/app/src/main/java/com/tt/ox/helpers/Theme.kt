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

    fun getControlsDisableColor() = theme.getControlsDisableColor()
    fun getGrayColor() = theme.getGrayColor()
    fun getBackgroundColor() = theme.getBackgroundColor()
    fun getAccentColor() = theme.getAccentColor()
    fun getGreenColor() = theme.getGreenColor()
    fun getRedColor() = theme.getReadColor()
    fun getBlueColor() = theme.getBlueColor()

    fun getAlertDialogBackgroundColor() = theme.getAlertDialogBackgroundColor()

    private open class LightMode{
        open val background = R.color.white
        open val accent = R.color.black
        open val green = R.color.green
        open val red = R.color.red
        open val blue = R.color.blue
        open val gray = R.color.gray
        open val alertDialogBackground = R.color.white
        open val controlsDisable = R.color.gray_light

        open fun getControlsDisableColor():Int{
            return controlsDisable
        }

        open fun getAlertDialogBackgroundColor():Int{
            return alertDialogBackground
        }

        open fun getGrayColor():Int{
            return gray
        }
        open fun getBlueColor():Int{
            return blue
        }
        open fun getReadColor():Int{
            return red
        }
        open fun getGreenColor():Int{
            return green
        }
        open fun getBackgroundColor():Int{
            return background
        }

        open fun getAccentColor():Int{
            return accent
        }

    }

    private class DarkMode : LightMode(){
        override val background = R.color.black
        override val accent = R.color.gray_dark
        override val green = R.color.green_dark
        override val red = R.color.red_dark
        override val blue = R.color.blue_dark
//        override val gray = R.color.gray_dark
        override val gray = R.color.gray
        override val alertDialogBackground = R.color.black_light
        override val controlsDisable = R.color.black_light

        override fun getControlsDisableColor():Int{
            return controlsDisable
        }
        override fun getAlertDialogBackgroundColor(): Int {
            return alertDialogBackground
        }
        override fun getGrayColor():Int{
            return gray
        }
        override fun getBlueColor(): Int {
            return blue
        }

        override fun getReadColor(): Int {
            return red
        }

        override fun getGreenColor(): Int {
            return green
        }

        override fun getBackgroundColor(): Int {
            return background
        }

        override fun getAccentColor(): Int {
            return accent
        }
    }
}