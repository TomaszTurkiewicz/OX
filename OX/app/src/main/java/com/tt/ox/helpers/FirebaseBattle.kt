package com.tt.ox.helpers

private const val NONE = "NONE"
class FirebaseBattle() {
    var battleId:String = ""
    var timestamp:Long = 0
    var turn:String = ""
    var play:Boolean = true
    var win:String = NONE
    val field:OnlineField = OnlineField()
}