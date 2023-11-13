package com.tt.ox.helpers

import kotlin.random.Random

const val COLOR_RED = 1
const val COLOR_GREEN = 2
const val COLOR_BLUE = 3
const val COLOR_BLACK = 4

class MarkColors() {
    private val colors = arrayListOf(
        COLOR_RED,
        COLOR_GREEN,
        COLOR_BLUE,
        COLOR_BLACK
    )

    fun getHighestColor():Int{
        return colors.size
    }


    fun getRandomColor():Int {
        val random = Random.nextInt(colors.size)
        return colors[random]
    }
}