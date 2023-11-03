package com.tt.ox.helpers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tt.ox.NOTHING

class Board {
    private val _topLeft = MutableLiveData<Int>()
    val topLeft:LiveData<Int> = _topLeft

    private val _topMid = MutableLiveData<Int>()
    val topMid:LiveData<Int> = _topMid

    private val _topRight = MutableLiveData<Int>()
    val topRight:LiveData<Int> = _topRight

    private val _midLeft = MutableLiveData<Int>()
    val midLeft:LiveData<Int> = _midLeft

    private val _midMid = MutableLiveData<Int>()
    val midMid:LiveData<Int> = _midMid

    private val _midRight = MutableLiveData<Int>()
    val midRight:LiveData<Int> = _midRight

    private val _bottomLeft = MutableLiveData<Int>()
    val bottomLeft:LiveData<Int> = _bottomLeft

    private val _bottomMid = MutableLiveData<Int>()
    val bottomMid:LiveData<Int> = _bottomMid

    private val _bottomRight = MutableLiveData<Int>()
    val bottomRight:LiveData<Int> = _bottomRight

    private var horizontalTop:Boolean = false
    private var horizontalMid:Boolean = false
    private var horizontalBottom:Boolean = false
    private var verticalLeft:Boolean = false
    private var verticalMid:Boolean = false
    private var verticalRight:Boolean = false
    private var angleDown:Boolean = false
    private var angleUp:Boolean = false

    private var win = false
    private var winningMark = NOTHING


    fun getHorizontalTop():Boolean{
        return this.horizontalTop
    }
    fun getHorizontalMid(): Boolean{
        return this.horizontalMid
    }
    fun getHorizontalBottom(): Boolean{
        return this.horizontalBottom
    }
    fun getVerticalLeft(): Boolean{
        return this.verticalLeft
    }
    fun getVerticalMid(): Boolean{
        return this.verticalMid
    }
    fun getVerticalRight(): Boolean{
        return this.verticalRight
    }
    fun getAngleUp(): Boolean{
        return this.angleUp
    }
    fun getAngleDown(): Boolean{
        return this.angleDown
    }
    fun initialize(){

        this.horizontalTop = false
        this.horizontalMid = false
        this.horizontalBottom = false
        this.verticalLeft = false
        this.verticalMid = false
        this.verticalRight = false
        this.angleDown = false
        this.angleUp = false
        this._topLeft.value = NOTHING
        this._topMid.value = NOTHING
        this._topRight.value = NOTHING
        this._midLeft.value = NOTHING
        this._midMid.value = NOTHING
        this._midRight.value = NOTHING
        this._bottomLeft.value = NOTHING
        this._bottomMid.value = NOTHING
        this._bottomRight.value = NOTHING
        this.win = false
        this.winningMark = NOTHING
    }

    fun getTopLeft():MutableLiveData<Int>{
        return this._topLeft
    }

    fun getTopMid():MutableLiveData<Int>{
        return this._topMid
    }

    fun getTopRight():MutableLiveData<Int>{
        return this._topRight
    }

    fun getMidLeft():MutableLiveData<Int>{
        return this._midLeft
    }

    fun getMidMid():MutableLiveData<Int>{
        return this._midMid
    }

    fun getMidRight():MutableLiveData<Int>{
        return this._midRight
    }

    fun getBottomLeft():MutableLiveData<Int>{
        return this._bottomLeft
    }

    fun getBottomMid():MutableLiveData<Int>{
        return this._bottomMid
    }

    fun getBottomRight():MutableLiveData<Int>{
        return this._bottomRight
    }

//    fun setSwitchButtonEnable():Boolean{
//        var enable = false
//        if(_topLeft.value!! == NOTHING){
//            if(_topMid.value!! == NOTHING){
//                if(_topRight.value!! == NOTHING){
//                    if(_midLeft.value!! == NOTHING){
//                        if(_midMid.value!! == NOTHING){
//                            if(_midRight.value!! == NOTHING){
//                                if(_bottomLeft.value!! == NOTHING){
//                                    if(_bottomMid.value!! == NOTHING){
//                                        if(_bottomRight.value!! == NOTHING){
//                                            enable = true
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return enable
//    }


    fun setField(mark:Int, field:MutableLiveData<Int>):Boolean{
        var marked = false
        if(field.value!! == NOTHING){
            field.value = mark
            marked = true
        }
        return marked
    }

    fun checkWin(){
        this.horizontalTop = checkLine(_topLeft,_topMid,_topRight)
        this.horizontalMid = checkLine(_midLeft,_midMid,_midRight)
        this.horizontalBottom = checkLine(_bottomLeft,_bottomMid,_bottomRight)
        this.verticalLeft = checkLine(_topLeft,_midLeft,_bottomLeft)
        this.verticalMid = checkLine(_topMid,_midMid,_bottomMid)
        this.verticalRight = checkLine(_topRight,_midRight,_bottomRight)
        this.angleUp = checkLine(_bottomLeft,_midMid,_topRight)
        this.angleDown = checkLine(_topLeft, _midMid,_bottomRight)
    }

    private fun checkLine(fieldFirst:MutableLiveData<Int>,fieldSecond:MutableLiveData<Int>,fieldThird:MutableLiveData<Int>):Boolean{
        var line = false
        if(fieldFirst.value!= NOTHING){
            if(fieldSecond.value!= NOTHING) {
                if (fieldThird.value!= NOTHING){
                    if(fieldFirst.value == fieldSecond.value){
                        if(fieldFirst.value == fieldThird.value){
                            win = true
                            line = true
                            winningMark = fieldFirst.value!!
                        }
                    }
                }
            }
        }
        return line
    }

    fun getWin():Boolean{
        return this.win
    }

    fun getWinningMark():Int{
        return this.winningMark
    }

    fun checkMovesNotAvailable():Boolean{
        var noMoves = false
        if(_topLeft.value != NOTHING){
            if(_topMid.value!= NOTHING){
                if(_topRight.value!= NOTHING){
                    if(_midLeft.value!= NOTHING){
                        if(_midMid.value!= NOTHING){
                            if(_midRight.value!= NOTHING){
                                if(_bottomLeft.value!= NOTHING){
                                    if(_bottomMid.value!= NOTHING){
                                        if(_bottomRight.value!= NOTHING){
                                            noMoves = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return noMoves
    }

}