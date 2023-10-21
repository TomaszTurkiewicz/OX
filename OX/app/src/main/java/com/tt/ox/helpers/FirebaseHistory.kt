package com.tt.ox.helpers

class FirebaseHistory() {
    var wins:Int = 0
    var loses:Int = 0

    fun addWin(){
        this.wins+=1
    }

    fun addLose(){
        this.loses+=1
    }
}