package com.tt.ox.helpers

import java.util.Calendar

const val YEAR_MULTIPLAYER = 10000
const val MONTH_MULTIPLAYER = 100
class DateUtils {


    fun getCurrentDate():Int{
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        return year* YEAR_MULTIPLAYER+month* MONTH_MULTIPLAYER+day
    }
}