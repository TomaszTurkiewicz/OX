package com.tt.ox.helpers

import com.tt.ox.NOTHING

class WinAndMark {
    private var win = false
    private var mark = NOTHING

    fun setWin(){
        this.win = true
    }

    fun setMark(mark:Int){
        this.mark = mark
    }
}