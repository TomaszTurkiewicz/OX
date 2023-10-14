package com.tt.ox.helpers


const val AVAILABLE = 0
const val SEND = 1
const val RECEIVED = 2
const val REJECTED = 3
const val ACCEPTED = 4
const val PLAY = 5
class FirebaseRequests() {
    var status:Int = AVAILABLE
    var opponentId:String? = ""
    var timestamp:Long = 0
    var battle:String? = ""
}