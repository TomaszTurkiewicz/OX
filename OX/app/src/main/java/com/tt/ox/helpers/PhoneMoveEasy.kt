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

class PhoneMoveEasy(private val board:Board):MakeMove {

    private val newBoard = arrayListOf<Int>()

    override fun makeMove(): Int {
        newBoard.clear()
        addToNewBoard(board.getTopLeft(), TOP_LEFT)
        addToNewBoard(board.getTopMid(), TOP_MID)
        addToNewBoard(board.getTopRight(), TOP_RIGHT)
        addToNewBoard(board.getMidLeft(), MID_LEFT)
        addToNewBoard(board.getMidMid(), MID_MID)
        addToNewBoard(board.getMidRight(), MID_RIGHT)
        addToNewBoard(board.getBottomLeft(), BOTTOM_LEFT)
        addToNewBoard(board.getBottomMid(), BOTTOM_MID)
        addToNewBoard(board.getBottomRight(), BOTTOM_RIGHT)

        val size = newBoard.size
        val random = Random.nextInt(size)

        return newBoard[random]

    }


    private fun addToNewBoard(field:MutableLiveData<Int>,fieldMarker:Int){
        if(field.value!! == NOTHING){
            newBoard.add(fieldMarker)
        }
    }
}