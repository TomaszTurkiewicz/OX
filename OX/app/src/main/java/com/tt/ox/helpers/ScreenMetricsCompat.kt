package com.tt.ox.helpers

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowManager
import android.view.WindowMetrics
import androidx.annotation.RequiresApi

class ScreenMetricsCompat {
    private val api: Api = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) ApiLevel30() else Api()

    fun getUnit(context:Context):Int{
        val width = getScreenSize(context).width/10
        val height = getScreenSize(context).height/20

        return if(width>height)height else width
    }

    fun getWindowHeight(context: Context):Int{
        return getScreenSize(context).height
    }

    fun getWindowWidth(context: Context):Int{
        return getScreenSize(context).width
    }


    private fun getScreenSize(context: Context): Size = api.getScreenSize(context)

    @Suppress("DEPRECATION")
    private open class Api{
        open fun getScreenSize(context: Context) : Size {
            val display = context.getSystemService(WindowManager::class.java).defaultDisplay
            val metrics = if(display!=null){
                DisplayMetrics().also { display.getRealMetrics(it) }
            } else {
                Resources.getSystem().displayMetrics
            }
            return Size(metrics.widthPixels, metrics.heightPixels)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private class ApiLevel30 : Api() {
        override fun getScreenSize(context: Context): Size {
            val metrics: WindowMetrics = context.getSystemService(WindowManager::class.java).currentWindowMetrics
            return Size(metrics.bounds.width(), metrics.bounds.height())
        }
    }
}