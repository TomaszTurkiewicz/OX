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


    private val _horizontalTop = MutableLiveData<Boolean>()
    private val _horizontalMid = MutableLiveData<Boolean>()
    private val _horizontalBottom = MutableLiveData<Boolean>()
    private val _verticalLeft = MutableLiveData<Boolean>()
    private val _verticalMid = MutableLiveData<Boolean>()
    private val _verticalRight = MutableLiveData<Boolean>()
    private val _angleDown = MutableLiveData<Boolean>()
    private val _angleUp = MutableLiveData<Boolean>()

    private val _play = MutableLiveData<Boolean>()
    val play :LiveData<Boolean> = _play



    private val _mark = MutableLiveData<Int>()
    private val mark:LiveData<Int> = _mark
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
        setField(_bottomRight)
    }
    fun setBottomMid(){
        setField(_bottomMid)
    }
    fun setBottomLeft(){
        setField(_bottomLeft)
    }
    fun setMidRight(){
        setField(_midRight)
    }
    fun setMidMid(){
        setField(_midMid)
    }
    fun setMidLeft(){
        setField(_midLeft)
    }
    fun setTopRight(){
        setField(_topRight)
    }
    fun setTopMid(){
        setField(_topMid)
    }
    fun setTopLeft(){
        setField(_topLeft)
    }
    fun getHorizontalTop():Boolean{
        return this._horizontalTop.value!!
    }
    fun getHorizontalMid():Boolean{
        return this._horizontalMid.value!!
    }
    fun getHorizontalBottom():Boolean{
        return this._horizontalBottom.value!!
    }
    fun getVerticalLeft():Boolean{
        return this._verticalLeft.value!!
    }
    fun getVerticalMid():Boolean{
        return this._verticalMid.value!!
    }
    fun getVerticalRight():Boolean{
        return this._verticalRight.value!!
    }
    fun getAngleUp():Boolean{
        return this._angleUp.value!!
    }
    fun getAngleDown():Boolean{
        return this._angleDown.value!!
    }
    private fun setField(field: MutableLiveData<Int>){
        if(play.value==true){
        if(field.value == NOTHING) {
            field.value = mark.value
            val endGame = checkLines()
            if (endGame) {
                _play.value = false
            } else {
                changeMark()
            }
        }
        }
    }

    fun initialize(){
        _topLeft.value = NOTHING
        _topMid.value = NOTHING
        _topRight.value = NOTHING

        _midLeft.value = NOTHING
        _midMid.value = NOTHING
        _midRight.value = NOTHING

        _bottomLeft.value = NOTHING
        _bottomMid.value = NOTHING
        _bottomRight.value = NOTHING

        _play.value = true
    }

    private fun checkLines():Boolean{
        _horizontalTop.value = checkLine(_topLeft,_topMid,_topRight)
        _horizontalMid.value = checkLine(_midLeft,_midMid,_midRight)
        _horizontalBottom.value = checkLine(_bottomLeft,_bottomMid,_bottomRight)
        _verticalLeft.value = checkLine(_topLeft,_midLeft,_bottomLeft)
        _verticalMid.value = checkLine(_topMid,_midMid,_bottomMid)
        _verticalRight.value = checkLine(_topRight,_midRight,_bottomRight)
        _angleDown.value = checkLine(_topLeft,_midMid,_bottomRight)
        _angleUp.value = checkLine(_bottomLeft,_midMid,_topRight)
        return checkWin()
    }

    private fun checkWin():Boolean{
        return getHorizontalTop() or (
            getHorizontalMid() or (
                getHorizontalBottom() or (
                    getVerticalLeft() or (
                        getVerticalMid() or (
                            getVerticalRight() or (
                                getAngleUp() or (
                                    getAngleDown() or (
                                        getNoMovesAvailable()
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    private fun getNoMovesAvailable(): Boolean {
        var endGame = false
        if(_topLeft.value!= NOTHING){
            if(_topMid.value!= NOTHING){
                if(_topRight.value!= NOTHING){
                    if(_midLeft.value!= NOTHING){
                        if(_midMid.value!= NOTHING){
                            if(_midRight.value!= NOTHING){
                                if(_bottomLeft.value!= NOTHING){
                                    if(_bottomMid.value!= NOTHING){
                                        if(_bottomRight.value!= NOTHING){
                                            endGame = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return endGame
    }


    private fun checkLine(fieldFirst:MutableLiveData<Int>,fieldSecond:MutableLiveData<Int>,fieldThird:MutableLiveData<Int>):Boolean{
        var line = false
        if(fieldFirst.value!= NOTHING){
            if(fieldSecond.value!= NOTHING) {
                if (fieldThird.value!= NOTHING){
                    if(fieldFirst.value == fieldSecond.value){
                        if(fieldFirst.value == fieldThird.value){
                            line = true
                        }
                    }
                }
            }
        }
        return line
    }

}

// todo change mark to person live data
// todo mark assigned to person

class GameViewModelFactory : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return GameViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}