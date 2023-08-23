package com.tt.ox.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tt.ox.X

class Player() {

    private var _name = MutableLiveData<String>()
    val name:LiveData<String> = _name

    private var _mark = MutableLiveData<Int>()
    val mark:LiveData<Int> = _mark

    fun getName():String{
        return this._name.value!!
    }

    fun setName(name:String){
        this._name.value = name
    }

    fun setMark(mark:Int){
        this._mark.value = mark
    }

}