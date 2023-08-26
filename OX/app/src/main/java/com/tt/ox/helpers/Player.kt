package com.tt.ox.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tt.ox.X

class Player() {

    private var _name = MutableLiveData<String>()
    val name:LiveData<String> = _name

    private var _mark = MutableLiveData<Int>()
    val mark:LiveData<Int> = _mark

    private var _wins = MutableLiveData<Int>()
    val wins:LiveData<Int> = _wins

    private var _loses = MutableLiveData<Int>()
    val loses:LiveData<Int> = _loses

    init {
        _mark.value = X
        _wins.value = 0
        _loses.value = 0
    }

    fun setName(name:String){
        this._name.value = name
    }

    fun setMark(mark:Int){
        this._mark.value = mark
    }

    fun setWins(wins:Int){
        this._wins.value = wins
    }

    fun setLoses(loses:Int){
        this._loses.value = loses
    }

}