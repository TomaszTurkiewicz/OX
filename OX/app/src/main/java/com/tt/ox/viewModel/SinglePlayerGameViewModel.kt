package com.tt.ox.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tt.ox.NOTHING
import com.tt.ox.NO_ONE
import com.tt.ox.O
import com.tt.ox.X
import com.tt.ox.helpers.Player
import com.tt.ox.helpers.SharedPreferences
import kotlin.random.Random

class SinglePlayerGameViewModel() : ViewModel() {

    private var mainPlayerStarted = true

    private val _moves = MutableLiveData<Int>()
    val moves:LiveData<Int> = _moves

    private var movesDecreased = false

    private val _turn = MutableLiveData<Boolean>()
    val turn:LiveData<Boolean> = _turn

    private var winingMark = NOTHING
    private var winingPerson = NO_ONE

    private val _win = MutableLiveData<Boolean>()
    val win:LiveData<Boolean> = _win

    private val _buttonSwitch = MutableLiveData<Boolean>()
    val buttonSwitch:LiveData<Boolean> = _buttonSwitch


    private val _mainPlayer = MutableLiveData<Player>()
    val mainPlayer: LiveData<Player> = _mainPlayer
    private val _opponentPlayer = MutableLiveData<Player>()
    val opponentPlayer: LiveData<Player> = _opponentPlayer
    private val _horizontalTop = MutableLiveData<Boolean>()
    private val _horizontalMid = MutableLiveData<Boolean>()
    private val _horizontalBottom = MutableLiveData<Boolean>()
    private val _verticalLeft = MutableLiveData<Boolean>()
    private val _verticalMid = MutableLiveData<Boolean>()
    private val _verticalRight = MutableLiveData<Boolean>()
    private val _angleDown = MutableLiveData<Boolean>()
    private val _angleUp = MutableLiveData<Boolean>()

    private val _play = MutableLiveData<Boolean>()
    val play :LiveData<Boolean> = _play

    private val _topLeft = MutableLiveData<Int>()
    val topLeft: LiveData<Int> = _topLeft
    private val _topMid = MutableLiveData<Int>()
    val topMid: LiveData<Int> = _topMid
    private val _topRight = MutableLiveData<Int>()
    val topRight: LiveData<Int> = _topRight

    private val _midLeft = MutableLiveData<Int>()
    val midLeft: LiveData<Int> = _midLeft
    private val _midMid = MutableLiveData<Int>()
    val midMid: LiveData<Int> = _midMid
    private val _midRight = MutableLiveData<Int>()
    val midRight: LiveData<Int> = _midRight


    private val _bottomLeft = MutableLiveData<Int>()
    val bottomLeft: LiveData<Int> = _bottomLeft
    private val _bottomMid = MutableLiveData<Int>()
    val bottomMid: LiveData<Int> = _bottomMid
    private val _bottomRight = MutableLiveData<Int>()
    val bottomRight: LiveData<Int> = _bottomRight

    fun initializeMainPlayer(context: Context) {
        _mainPlayer.value = Player()
        val player = SharedPreferences.readPlayer(context)
        _mainPlayer.value!!.setMark(player.mark.value!!)
        _mainPlayer.value!!.setName(player.name.value!!)
    }

    fun initializeOpponentPlayer() {
        val oPlayer = Player()
        oPlayer.setName("ROBOT")
        _opponentPlayer.value = oPlayer
        val mark = if(_mainPlayer.value!!.mark.value== X) O else X
        _opponentPlayer.value!!.setMark(mark)
    }

    fun getWiningPerson():Int{
        return this.winingPerson
    }

    fun getHorizontalTop():Boolean{
        return this._horizontalTop.value!!
    }
    fun getHorizontalMid():Boolean{
        return this._horizontalMid.value!!
    }
    fun getHorizontalBottom():Boolean{
        return this._horizontalBottom.value!!
    }
    fun getVerticalLeft():Boolean{
        return this._verticalLeft.value!!
    }
    fun getVerticalMid():Boolean{
        return this._verticalMid.value!!
    }
    fun getVerticalRight():Boolean{
        return this._verticalRight.value!!
    }
    fun getAngleUp():Boolean{
        return this._angleUp.value!!
    }
    fun getAngleDown():Boolean{
        return this._angleDown.value!!
    }

    fun initialize(firstGame:Boolean){
        _topLeft.value = NOTHING
        _topMid.value = NOTHING
        _topRight.value = NOTHING

        _midLeft.value = NOTHING
        _midMid.value = NOTHING
        _midRight.value = NOTHING

        _bottomLeft.value = NOTHING
        _bottomMid.value = NOTHING
        _bottomRight.value = NOTHING

        _horizontalTop.value = false
        _horizontalMid.value = false
        _horizontalBottom.value = false
        _verticalLeft.value = false
        _verticalMid.value = false
        _verticalRight.value = false
        _angleUp.value = false
        _angleDown.value = false

        if(_moves.value!!>0) {
            _play.value = true
        }
        _win.value = false

        setStartingTurn(firstGame)

        setButtonEnable()

        resetWiningPerson()

        resetMovesDecreased()
    }
    private fun resetWiningPerson(){
        this.winingPerson = NO_ONE
        resetWin()
    }
    fun resetWin(){
        _win.value = false
    }

    private fun resetMovesDecreased() {
        this.movesDecreased = false
    }

    private fun setButtonEnable(){
        _buttonSwitch.value = false
        if(_topLeft.value == NOTHING){
            if(_topMid.value == NOTHING){
                if(_topRight.value == NOTHING){
                    if(_midLeft.value == NOTHING){
                        if(_midMid.value == NOTHING){
                            if(_midRight.value == NOTHING){
                                if(_bottomLeft.value == NOTHING){
                                    if(_bottomMid.value == NOTHING){
                                        if(_bottomRight.value == NOTHING){
                                            _buttonSwitch.value = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setStartingTurn(firstGame:Boolean){
        if(firstGame){
            setFirstStartingTurn()
        }else{
            setAnotherStartingTurn()
        }
//        _turn.value = true
    }

    private fun setFirstStartingTurn(){
        val random = Random.nextBoolean()
        mainPlayerStarted = random
        _turn.value = random
    }

    private fun setAnotherStartingTurn(){
        mainPlayerStarted = !mainPlayerStarted
        _turn.value = mainPlayerStarted
    }
    fun initializeMoves(context: Context) {
        _moves.value = SharedPreferences.readMoves(context)
    }
}

class SinglePlayerGameViewModelFactory() : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SinglePlayerGameViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return SinglePlayerGameViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}