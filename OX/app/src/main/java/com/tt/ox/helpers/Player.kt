package com.tt.ox.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tt.ox.X

class Player(private var myTurn:Boolean, private val name:String? = "Tom") {

    private var _mark = MutableLiveData<Int>()
    val mark:LiveData<Int> = _mark

    private var _turn = MutableLiveData<Boolean>()
    val turn:LiveData<Boolean> = _turn

    init {
        _turn.value = myTurn
    }

    fun getTurn():Boolean{
        return this._turn.value!!
    }
    fun changeTurn(){
        val boolean = !_turn.value!!
        _turn.value = boolean
    }

    fun getName():String{
        return name ?: ""
    }

    fun setMark(mark:Int){
        this._mark.value = mark
    }


}