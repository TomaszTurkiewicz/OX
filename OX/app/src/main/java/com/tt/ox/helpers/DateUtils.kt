package com.tt.ox.helpers

import java.util.Calendar

const val YEAR_MULTIPLAYER = 10000
const val MONTH_MULTIPLAYER = 100
class DateUtils {


    fun getCurrentDate():Int{
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)+1
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        return year* YEAR_MULTIPLAYER+month* MONTH_MULTIPLAYER+day
    }

    private fun getPastDay(daysAgo:Int):Int{
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)+1
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val daysDiff = day-daysAgo
        if(daysDiff>0){
            return year* YEAR_MULTIPLAYER+month* MONTH_MULTIPLAYER+daysDiff
        }else{
            val newDay = 31+daysDiff

            val newMonth: Int
            val newYear: Int
            return if(month>1){
                newMonth = month-1
                year* YEAR_MULTIPLAYER+ newMonth* MONTH_MULTIPLAYER+newDay
            }else{
                newMonth=12
                newYear = year-1
                newYear* YEAR_MULTIPLAYER+ newMonth* MONTH_MULTIPLAYER+newDay
            }
        }

    }

    private fun getFutureDay(daysAhead:Int):Int{
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)+1
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        val newDate = day+daysAhead
        return if(newDate<32){
            year* YEAR_MULTIPLAYER+month* MONTH_MULTIPLAYER+newDate
        }else{
            val newDay = newDate-31
            val newMonth = month+1
            if(newMonth<13){
                year* YEAR_MULTIPLAYER+newMonth* MONTH_MULTIPLAYER+newDay
            }else{
                val newYear = year+1
                newYear* YEAR_MULTIPLAYER+1* MONTH_MULTIPLAYER+newDay
            }
        }

    }

    fun getLastMonth(): MutableList<Int> {
        val list:MutableList<Int> = mutableListOf()
        for(i in 5 downTo 1){
            list.add(getFutureDay(i))
        }
        list.add(getCurrentDate())
        for(i in 1..31){
            list.add(getPastDay(i))
        }
        return list
    }
}