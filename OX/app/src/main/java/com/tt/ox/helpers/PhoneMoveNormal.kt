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
import kotlin.random.Random

class PhoneMoveNormal(private val board:Board,private val mMark:Int,private val oMark:Int):MakeMove {

    private val secondMarkBoard = arrayListOf<Int>()

    private val firstMarkCorners = arrayListOf<Int>()

    private val emptyRowBoard = arrayListOf<Int>()

    private val horizontalTopCounters = LineMarkCounters()
    private val horizontalMidCounters = LineMarkCounters()
    private val horizontalBottomCounters = LineMarkCounters()
    private val verticalLeftCounters = LineMarkCounters()
    private val verticalMidCounters = LineMarkCounters()
    private val verticalRightCounters = LineMarkCounters()
    private val angleUpCounters = LineMarkCounters()
    private val angleDownCounters = LineMarkCounters()
    override fun makeMove(): Int {
        secondMarkBoard.clear()
        firstMarkCorners.clear()
        emptyRowBoard.clear()

        setCounters()


        // make winning move
        if(horizontalTopCounters.getWinningCounter()){
            return getWinningField(board.getTopLeft(), board.getTopMid(), board.getTopRight(), TOP_LEFT, TOP_MID, TOP_RIGHT)
        }
        if(horizontalMidCounters.getWinningCounter()){
            return getWinningField(board.getMidLeft(),board.getMidMid(),board.getMidRight(), MID_LEFT, MID_MID, MID_RIGHT)
        }
        if(horizontalBottomCounters.getWinningCounter()){
            return getWinningField(board.getBottomLeft(),board.getBottomMid(),board.getBottomRight(), BOTTOM_LEFT, BOTTOM_MID, BOTTOM_RIGHT)
        }
        if(verticalLeftCounters.getWinningCounter()){
            return getWinningField(board.getTopLeft(),board.getMidLeft(),board.getBottomLeft(), TOP_LEFT, MID_LEFT, BOTTOM_LEFT)
        }
        if(verticalMidCounters.getWinningCounter()){
            return getWinningField(board.getTopMid(), board.getMidMid(),board.getBottomMid(), TOP_MID, MID_MID, BOTTOM_MID)
        }
        if(verticalRightCounters.getWinningCounter()){
            return getWinningField(board.getTopRight(), board.getMidRight(), board.getBottomRight(), TOP_RIGHT, MID_RIGHT, BOTTOM_RIGHT)
        }
        if(angleUpCounters.getWinningCounter()){
            return getWinningField(board.getBottomLeft(),board.getMidMid(),board.getTopRight(), BOTTOM_LEFT, MID_MID, TOP_RIGHT)
        }
        if(angleDownCounters.getWinningCounter()){
            return getWinningField(board.getTopLeft(),board.getMidMid(),board.getBottomRight(), TOP_LEFT, MID_MID, BOTTOM_RIGHT)
        }


        // try to put second mark the best way
        if(horizontalTopCounters.getSecondMark()){
            addSecondMark(board.getTopLeft(), board.getTopMid(), board.getTopRight(), TOP_LEFT, TOP_MID, TOP_RIGHT)
        }
        if(horizontalMidCounters.getSecondMark()){
            addSecondMark(board.getMidLeft(),board.getMidMid(),board.getMidRight(), MID_LEFT, MID_MID, MID_RIGHT)
        }
        if(horizontalBottomCounters.getSecondMark()){
            addSecondMark(board.getBottomLeft(),board.getBottomMid(),board.getBottomRight(), BOTTOM_LEFT, BOTTOM_MID, BOTTOM_RIGHT)
        }
        if(verticalLeftCounters.getSecondMark()){
            addSecondMark(board.getTopLeft(),board.getMidLeft(),board.getBottomLeft(), TOP_LEFT, MID_LEFT, BOTTOM_LEFT)
        }
        if(verticalMidCounters.getSecondMark()){
            addSecondMark(board.getTopMid(), board.getMidMid(),board.getBottomMid(), TOP_MID, MID_MID, BOTTOM_MID)
        }
        if(verticalRightCounters.getSecondMark()){
            addSecondMark(board.getTopRight(), board.getMidRight(), board.getBottomRight(), TOP_RIGHT, MID_RIGHT, BOTTOM_RIGHT)
        }
        if(angleUpCounters.getSecondMark()){
            addSecondMark(board.getBottomLeft(),board.getMidMid(),board.getTopRight(), BOTTOM_LEFT, MID_MID, TOP_RIGHT)
        }
        if(angleDownCounters.getSecondMark()){
            addSecondMark(board.getTopLeft(),board.getMidMid(),board.getBottomRight(), TOP_LEFT, MID_MID, BOTTOM_RIGHT)
        }

        if(secondMarkBoard.size > 0){
            if(secondMarkBoard.contains(MID_MID)){
                return MID_MID
            }

            if(secondMarkBoard.contains(TOP_LEFT)){
                emptyRowBoard.add(TOP_LEFT)
            }
            if(secondMarkBoard.contains(TOP_RIGHT)){
                emptyRowBoard.add(TOP_RIGHT)
            }
            if(secondMarkBoard.contains(BOTTOM_LEFT)){
                emptyRowBoard.add(BOTTOM_LEFT)
            }
            if(secondMarkBoard.contains(BOTTOM_RIGHT)){
                emptyRowBoard.add(BOTTOM_RIGHT)
            }

            if(emptyRowBoard.size > 0){
                val size = emptyRowBoard.size
                val random = Random.nextInt(size)
                return emptyRowBoard[random]
            }

            val secondSize = secondMarkBoard.size
            val random = Random.nextInt(secondSize)
            return secondMarkBoard[random]
        }

//        if(secondMarkBoard.size > 0){
//            //add mid-mid
//            if(horizontalMidCounters.getEmptyRow() or (
//                verticalMidCounters.getEmptyRow() or(
//                    angleDownCounters.getEmptyRow() or(
//                        angleUpCounters.getEmptyRow()
//                    )
//                )
//            )){
//                emptyRowBoard.add(MID_MID)
//            }
//
//            //add corners to empty board
//            if(horizontalTopCounters.getEmptyRow() or(
//                verticalLeftCounters.getEmptyRow() or(
//                    angleDownCounters.getEmptyRow()
//                )
//            )){
//                emptyRowBoard.add(TOP_LEFT)
//            }
//            if(horizontalTopCounters.getEmptyRow() or(
//                        verticalRightCounters.getEmptyRow() or(
//                                angleUpCounters.getEmptyRow()
//                                )
//                        )){
//                emptyRowBoard.add(TOP_RIGHT)
//            }
//            if(horizontalBottomCounters.getEmptyRow() or(
//                        verticalLeftCounters.getEmptyRow() or(
//                                angleUpCounters.getEmptyRow()
//                                )
//                        )){
//                emptyRowBoard.add(BOTTOM_LEFT)
//            }
//            if(horizontalBottomCounters.getEmptyRow() or(
//                        verticalRightCounters.getEmptyRow() or(
//                                angleDownCounters.getEmptyRow()
//                                )
//                        )){
//                emptyRowBoard.add(BOTTOM_RIGHT)
//            }
//            val a = 100
//            if(!secondMarkBoard.contains(MID_MID)){
//                emptyRowBoard.remove(MID_MID)
//            }else{
//                return MID_MID
//            }
//            if(!secondMarkBoard.contains(TOP_LEFT)){
//                emptyRowBoard.remove(TOP_LEFT)
//            }
//            if(!secondMarkBoard.contains(TOP_RIGHT)){
//                emptyRowBoard.remove(TOP_RIGHT)
//            }
//            if(!secondMarkBoard.contains(BOTTOM_LEFT)){
//                emptyRowBoard.remove(BOTTOM_LEFT)
//            }
//            if(!secondMarkBoard.contains(BOTTOM_RIGHT)){
//                emptyRowBoard.remove(BOTTOM_RIGHT)
//            }
//
//            val size = emptyRowBoard.size
//            if(size>0){
//                val random = Random.nextInt(size)
//                return emptyRowBoard[random]
//            }
//        }

        // put first mark the best way
        if(board.getMidMid().value!! == NOTHING){
            return MID_MID
        }

        if(board.getTopLeft().value!! == NOTHING){
            firstMarkCorners.add(TOP_LEFT)
        }
        if(board.getTopRight().value!! == NOTHING){
            firstMarkCorners.add(TOP_RIGHT)
        }
        if(board.getBottomLeft().value!! == NOTHING){
            firstMarkCorners.add(BOTTOM_LEFT)
        }
        if(board.getBottomRight().value!! == NOTHING){
            firstMarkCorners.add(BOTTOM_RIGHT)
        }

        if(firstMarkCorners.size>0){
            val size = firstMarkCorners.size
            val random = Random.nextInt(size)

            return firstMarkCorners[random]
        }

        return PhoneMoveEasy(board).makeMove()
    }

    private fun addSecondMark(mark1:MutableLiveData<Int>,mark2:MutableLiveData<Int>, mark3:MutableLiveData<Int>, field1:Int, field2:Int, field3:Int){
        if(mark1.value!! == NOTHING){
            secondMarkBoard.add(field1)
        }
        if(mark2.value!! == NOTHING){
            secondMarkBoard.add(field2)
        }
        if(mark3.value!! == NOTHING){
            secondMarkBoard.add(field3)
        }
    }

    private fun getWinningField(mark1:MutableLiveData<Int>,mark2:MutableLiveData<Int>, mark3:MutableLiveData<Int>, field1:Int, field2:Int, field3:Int):Int{
        var field = 0
        if(mark1.value!! == NOTHING){
            field = field1
        }
        if(mark2.value!! == NOTHING){
            field = field2
        }
        if(mark3.value!! == NOTHING){
            field = field3
        }
        return field
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