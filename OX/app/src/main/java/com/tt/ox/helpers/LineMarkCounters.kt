package com.tt.ox.helpers

class LineMarkCounters {
    private var nothingCounter = 0
    private var mainMarkCounter = 0
    private var opponentMarkCounter = 0

    fun addNothing(){
        this.nothingCounter +=1
    }
    fun addMainMark(){
        this.mainMarkCounter +=1
    }
    fun addOpponentMark(){
        this.opponentMarkCounter +=1
    }

    fun getWinningCounter():Boolean{
        var boolean = false
        if(opponentMarkCounter == 2){
            if(nothingCounter == 1){
                boolean = true
            }
        }
        return boolean
    }
}