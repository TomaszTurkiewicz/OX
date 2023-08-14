package com.tt.ox.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tt.ox.NOTHING
import com.tt.ox.O
import com.tt.ox.X
import java.lang.IllegalArgumentException

class GameViewModel : ViewModel(){


    private val _mark = MutableLiveData<Int>()
    val mark:LiveData<Int> = _mark
    private val _topLeft = MutableLiveData<Int>()
    val topLeft: LiveData<Int> = _topLeft
    private val _topMid = MutableLiveData<Int>()
    val topMid: LiveData<Int> = _topMid
    private val _topRight = MutableLiveData<Int>()
    val topRight: LiveData<Int> = _topRight

    private val _midLeft = MutableLiveData<Int>()
    val midLeft: LiveData<Int> = _midLeft
    private val _midMid = MutableLiveData<Int>()
    val midMid: LiveData<Int> = _midMid
    private val _midRight = MutableLiveData<Int>()
    val midRight: LiveData<Int> = _midRight


    private val _bottomLeft = MutableLiveData<Int>()
    val bottomLeft: LiveData<Int> = _bottomLeft
    private val _bottomMid = MutableLiveData<Int>()
    val bottomMid: LiveData<Int> = _bottomMid
    private val _bottomRight = MutableLiveData<Int>()
    val bottomRight: LiveData<Int> = _bottomRight

    fun setMark(mark:Int){
        this._mark.value = mark
    }

    private fun changeMark(){
        if(this._mark.value==X){
            setMark(O)
        }else{
            setMark(X)
        }
    }

    fun setBottomRight(){
        this._bottomRight.value = mark.value
        changeMark()
    }
    fun getBottomRight():Int{
        return this.bottomRight.value!!
    }
    fun setBottomMid(){
        this._bottomMid.value = mark.value
        changeMark()
    }
    fun getBottomMid():Int{
        return this.bottomMid.value!!
    }
    fun setBottomLeft(){
        this._bottomLeft.value = mark.value
        changeMark()
    }

    fun getBottomLeft():Int{
        return this.bottomLeft.value!!
    }


    fun setMidRight(){
        this._midRight.value = mark.value
        changeMark()
    }
    fun getMidRight():Int{
        return this.midRight.value!!
    }
    fun setMidMid(){
        this._midMid.value = mark.value
        changeMark()
    }
    fun getMidMid():Int{
        return this.midMid.value!!
    }
    fun setMidLeft(){
        this._midLeft.value = mark.value
        changeMark()
    }

    fun getMidLeft():Int{
        return this.midLeft.value!!
    }

    fun setTopRight(){
        this._topRight.value = mark.value
        changeMark()
    }
    fun getTopRight():Int{
        return this.topRight.value!!
    }
    fun setTopMid(){
        this._topMid.value = mark.value
        changeMark()
    }
    fun getTopMid():Int{
        return this.topMid.value!!
    }
    fun setTopLeft(){
            this._topLeft.value = mark.value
            changeMark()
    }

    fun getTopLeft():Int{
        return this.topLeft.value!!
    }

}

class GameViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return GameViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}