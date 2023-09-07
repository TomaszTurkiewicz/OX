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

    fun getLosingCounter():Boolean{
        var boolean = false
        if(mainMarkCounter == 2){
            if(nothingCounter == 1){
                boolean = true
            }
        }
        return boolean

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

    fun getSecondMark():Boolean{
        var boolean = false
        if(opponentMarkCounter == 1){
            if(nothingCounter == 2){
                boolean = true
            }
        }
        return boolean
    }

    fun getEmptyRow():Boolean{
        var boolean = false
        if(nothingCounter == 3){
                boolean = true
        }
        return boolean
    }
}