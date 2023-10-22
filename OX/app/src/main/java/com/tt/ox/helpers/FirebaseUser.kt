package com.tt.ox.helpers

class FirebaseUser(){
    var id:String? = ""
    var userName:String = "ANONYMOUS"
    var wins:Int = 0
    var loses:Int = 0
    var timestamp:Int = 0
    var unixTime:Long = 0
    var moves = 10

    fun addWins(){
        this.wins +=1
    }

    fun addLoses(){
        this.loses +=1
    }
}