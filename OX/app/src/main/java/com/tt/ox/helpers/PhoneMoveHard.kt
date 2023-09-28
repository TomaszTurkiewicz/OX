package com.tt.ox.helpers

import androidx.lifecycle.MutableLiveData
import com.tt.ox.BOTTOM_LEFT
import com.tt.ox.BOTTOM_MID
import com.tt.ox.BOTTOM_RIGHT
import com.tt.ox.MID_LEFT
import com.tt.ox.MID_MID
import com.tt.ox.MID_RIGHT
import com.tt.ox.NOTHING
import com.tt.ox.TOP_LEFT
import com.tt.ox.TOP_MID
import com.tt.ox.TOP_RIGHT

class PhoneMoveHard(private val board:Board,private val mMark:Int,private val oMark:Int):MakeMove {


    private val horizontalTopCounters = LineMarkCounters()
    private val horizontalMidCounters = LineMarkCounters()
    private val horizontalBottomCounters = LineMarkCounters()
    private val verticalLeftCounters = LineMarkCounters()
    private val verticalMidCounters = LineMarkCounters()
    private val verticalRightCounters = LineMarkCounters()
    private val angleUpCounters = LineMarkCounters()
    private val angleDownCounters = LineMarkCounters()
    override fun makeMove(): Int {

        setCounters()

        if(horizontalTopCounters.getWinningCounter() or (
            horizontalMidCounters.getWinningCounter() or (
                horizontalBottomCounters.getWinningCounter() or (
                    verticalLeftCounters.getWinningCounter() or (
                        verticalMidCounters.getWinningCounter() or (
                            verticalRightCounters.getWinningCounter() or (
                                angleUpCounters.getWinningCounter() or (
                                    angleDownCounters.getWinningCounter()
                                )
                            )
                        )
                    )
                )
            )
        )){
          return  PhoneMoveNormal(board,mMark,oMark).makeMove()
        }


        if(horizontalTopCounters.getLosingCounter()){

            return getEmptyField(board.getTopLeft(), TOP_LEFT,board.getTopMid(), TOP_MID,board.getTopRight(),
                TOP_RIGHT)
        }
        if(horizontalMidCounters.getLosingCounter()){

            return getEmptyField(board.getMidLeft(), MID_LEFT, board.getMidMid(), MID_MID,board.getMidRight(),
                MID_RIGHT)
        }
        if(horizontalBottomCounters.getLosingCounter()){

            return getEmptyField(board.getBottomLeft(), BOTTOM_LEFT,board.getBottomMid(), BOTTOM_MID,board.getBottomRight(),
                BOTTOM_RIGHT)
        }
        if(verticalLeftCounters.getLosingCounter()){
            return getEmptyField(board.getTopLeft(), TOP_LEFT,board.getMidLeft(), MID_LEFT,board.getBottomLeft(),
                BOTTOM_LEFT)
        }
        if(verticalMidCounters.getLosingCounter()){
            return getEmptyField(board.getTopMid(), TOP_MID,board.getMidMid(), MID_MID,board.getBottomMid(),
                BOTTOM_MID)
        }
        if(verticalRightCounters.getLosingCounter()){
            return getEmptyField(board.getTopRight(), TOP_RIGHT,board.getMidRight(), MID_RIGHT,board.getBottomRight(),
                BOTTOM_RIGHT)
        }
        if(angleUpCounters.getLosingCounter()){
            return getEmptyField(board.getBottomLeft(), BOTTOM_LEFT,board.getMidMid(), MID_MID,board.getTopRight(),
                TOP_RIGHT)
        }
        if(angleDownCounters.getLosingCounter()){
            return getEmptyField(board.getTopLeft(), TOP_LEFT,board.getMidMid(), MID_MID,board.getBottomRight(),
                BOTTOM_RIGHT)
        }

        return PhoneMoveNormal(board,mMark,oMark).makeMove()
    }

    private fun getEmptyField(field1:MutableLiveData<Int>,value1:Int,field2:MutableLiveData<Int>,value2:Int,field3:MutableLiveData<Int>,value3:Int):Int{
        var empty = 0
        if(field1.value!! == NOTHING){
            empty = value1
        }
        if(field2.value!! == NOTHING){
            empty = value2
        }
        if(field3.value!! == NOTHING){
            empty = value3
        }
        return empty
    }

    private fun setCounters(){
        addCounters(board.getTopLeft().value!!,horizontalTopCounters,verticalLeftCounters,angleDownCounters)
        addCounters(board.getTopMid().value!!,horizontalTopCounters,verticalMidCounters)
        addCounters(board.getTopRight().value!!,horizontalTopCounters,verticalRightCounters,angleUpCounters)
        addCounters(board.getMidLeft().value!!,horizontalMidCounters,verticalLeftCounters)
        addCounters(board.getMidMid().value!!,horizontalMidCounters,verticalMidCounters,angleUpCounters,angleDownCounters)
        addCounters(board.getMidRight().value!!,horizontalMidCounters,verticalRightCounters)
        addCounters(board.getBottomLeft().value!!,horizontalBottomCounters,verticalLeftCounters,angleUpCounters)
        addCounters(board.getBottomMid().value!!,horizontalBottomCounters,verticalMidCounters)
        addCounters(board.getBottomRight().value!!,horizontalBottomCounters,verticalRightCounters,angleDownCounters)


    }

    private fun addCounters(mark:Int,vararg counters: LineMarkCounters){
        when(mark){
            NOTHING -> {
                for (c in counters){
                    c.addNothing()
                }
            }
            mMark -> {
                for (c in counters){
                    c.addMainMark()
                }
            }
            oMark -> {
                for (c in counters){
                    c.addOpponentMark()
                }
            }
        }
    }
}