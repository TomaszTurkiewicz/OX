package com.tt.ox.helpers

const val COLOR_RED = 1
const val COLOR_GREEN = 2
const val COLOR_BLUE = 3
const val COLOR_BLACK = 4

class MarkColors(private val color:Int) {
    private var pointer:Int = 0

    private val colors = arrayListOf(
        COLOR_RED,
        COLOR_GREEN,
        COLOR_BLUE,
        COLOR_BLACK
    )

    init {
         for (i in 0 until colors.size){
            if(colors[i]==color){
                pointer = i
            }
        }

    }


    fun getColor():Int{
        return this.colors[pointer]
    }

    fun getRightColor():Int{
        val newPointer = (pointer+1).mod(colors.size)
        return this.colors[newPointer]
    }

    fun getLeftColor():Int{
        val newPointer = if(pointer==0) colors.size-1 else pointer-1
        return this.colors[newPointer]
    }

    fun increasePointer(){
        this.pointer = (pointer+1).mod(colors.size)
    }

    fun decreasePointer(){
        if(pointer==0){
            this.pointer = colors.size-1
        }else{
            this.pointer -=1
        }
    }


}