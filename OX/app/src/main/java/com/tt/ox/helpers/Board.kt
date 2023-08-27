package com.tt.ox.helpers

import com.tt.ox.NOTHING

class Board {
    private var horizontalTop:Boolean = false
    private var horizontalMid:Boolean = false
    private var horizontalBottom:Boolean = false
    private var verticalLeft:Boolean = false
    private var verticalMid:Boolean = false
    private var verticalRight:Boolean = false
    private var angleDown:Boolean = false
    private var angleUp:Boolean = false


    private var topLeft: Int = NOTHING
    private var topMid: Int = NOTHING
    private var topRight: Int = NOTHING

    private var midLeft: Int = NOTHING
    private var midMid: Int = NOTHING
    private var midRight: Int = NOTHING

    private var bottomLeft: Int = NOTHING
    private var bottomMid: Int = NOTHING
    private var bottomRight: Int = NOTHING


    fun initialize(){
        this.horizontalTop = false
        this.horizontalMid = false
        this.horizontalBottom = false
        this.verticalLeft = false
        this.verticalMid = false
        this.verticalRight = false
        this.angleDown = false
        this.angleUp = false
        this.topLeft = NOTHING
        this.topMid = NOTHING
        this.topRight = NOTHING
        this.midLeft = NOTHING
        this.midMid = NOTHING
        this.midRight = NOTHING
        this.bottomLeft = NOTHING
        this.bottomMid = NOTHING
        this.bottomRight = NOTHING
    }

}