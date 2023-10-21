package com.tt.ox.helpers

const val NONE = "NONE"
const val END_DRAW = "DRAW"
const val OUT_OF_TIME = "OUT_OF_TIME"

const val TOP_LINE = 1
const val HORIZONTAL_MID_LINE = 2
const val BOTTOM_LINE = 3
const val LEFT_LINE = 4
const val VERTICAL_MID_LINE = 5
const val RIGHT_LINE = 6
const val ANGLE_UP_LINE = 7
const val ANGLE_DOWN_LINE = 8
class FirebaseBattle() {
    var battleId:String = ""
    var timestamp:Long = 0
    var turn:String = ""
    var win:String = NONE
    val field:OnlineField = OnlineField()
    var winningLine = 0
}