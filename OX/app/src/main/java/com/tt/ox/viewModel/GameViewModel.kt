package com.tt.ox.viewModel

import android.content.Context
import android.graphics.Path.Op
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tt.ox.NOTHING
import com.tt.ox.O
import com.tt.ox.X
import com.tt.ox.database.Opponent
import com.tt.ox.database.OpponentDao
import com.tt.ox.helpers.Player
import com.tt.ox.helpers.SharedPreferences
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class GameViewModel(private val opponentDao: OpponentDao) : ViewModel(){

    private val _buttonEnable = MutableLiveData<Boolean>()
    val buttonEnable:LiveData<Boolean> = _buttonEnable


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

    val listOfOpponents:LiveData<List<Opponent>> = opponentDao.getOpponents().asLiveData()

    fun getOpponent(id:Int): LiveData<Opponent>{
        return opponentDao.getOpponent(id).asLiveData()
    }
    fun addNewOpponent(name:String){
        val opponent = getNewOpponentEntity(name)
        insertNewOpponent(opponent)
    }
    private fun getNewOpponentEntity(name:String):Opponent{
        return Opponent(
            opponentName = name,
            opponentWin = 0
        )
    }

    private fun insertNewOpponent(opponent: Opponent){
        viewModelScope.launch {
            opponentDao.insert(opponent)
        }
    }

    private fun getMainPlayer():Player{
        return this._mainPlayer.value!!
    }

    private fun getOpponentPlayer():Player{
        return this._opponentPlayer.value!!
    }

    fun getMainPlayerName():String{
        return this.getMainPlayer().getName()
    }
    fun getOpponentPlayerName():String{
        return this.getOpponentPlayer().getName()
    }

    private fun changePerson(){
        _mainPlayer.value!!.changeTurn()
        _opponentPlayer.value!!.changeTurn()
    }

    fun setBottomRight(){
        setField(_bottomRight)
    }
    fun setBottomMid(){
        setField(_bottomMid)
    }
    fun setBottomLeft(){
        setField(_bottomLeft)
    }
    fun setMidRight(){
        setField(_midRight)
    }
    fun setMidMid(){
        setField(_midMid)
    }
    fun setMidLeft(){
        setField(_midLeft)
    }
    fun setTopRight(){
        setField(_topRight)
    }
    fun setTopMid(){
        setField(_topMid)
    }
    fun setTopLeft(){
        setField(_topLeft)
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

    private fun setButtonEnable(){
        _buttonEnable.value = false
        if(_topLeft.value == NOTHING){
            if(_topMid.value == NOTHING){
                if(_topRight.value == NOTHING){
                    if(_midLeft.value == NOTHING){
                        if(_midMid.value == NOTHING){
                            if(_midRight.value == NOTHING){
                                if(_bottomLeft.value == NOTHING){
                                    if(_bottomMid.value == NOTHING){
                                        if(_bottomRight.value == NOTHING){
                                           _buttonEnable.value = true
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
    private fun setField(field: MutableLiveData<Int>){
        if(play.value==true){
        if(field.value == NOTHING) {
            field.value = if(_mainPlayer.value!!.getTurn()) _mainPlayer.value!!.mark.value else _opponentPlayer.value!!.mark.value
            val endGame = checkLines()
            if (endGame) {
                _play.value = false
            } else {
                changePerson()
            }
            setButtonEnable()
        }
        }
    }

    fun switchMarks(){
        val opponentPlayerMark = _opponentPlayer.value!!.mark.value!!
        val mainPlayerMark = _mainPlayer.value!!.mark.value!!

        _mainPlayer.value!!.setMark(opponentPlayerMark)
        _opponentPlayer.value!!.setMark(mainPlayerMark)
    }



    fun initialize(){
        _topLeft.value = NOTHING
        _topMid.value = NOTHING
        _topRight.value = NOTHING

        _midLeft.value = NOTHING
        _midMid.value = NOTHING
        _midRight.value = NOTHING

        _bottomLeft.value = NOTHING
        _bottomMid.value = NOTHING
        _bottomRight.value = NOTHING

        _play.value = true

        setButtonEnable()

    }

    private fun checkLines():Boolean{
        _horizontalTop.value = checkLine(_topLeft,_topMid,_topRight)
        _horizontalMid.value = checkLine(_midLeft,_midMid,_midRight)
        _horizontalBottom.value = checkLine(_bottomLeft,_bottomMid,_bottomRight)
        _verticalLeft.value = checkLine(_topLeft,_midLeft,_bottomLeft)
        _verticalMid.value = checkLine(_topMid,_midMid,_bottomMid)
        _verticalRight.value = checkLine(_topRight,_midRight,_bottomRight)
        _angleDown.value = checkLine(_topLeft,_midMid,_bottomRight)
        _angleUp.value = checkLine(_bottomLeft,_midMid,_topRight)
        return checkWin()
    }

    private fun checkWin():Boolean{
        return getHorizontalTop() or (
            getHorizontalMid() or (
                getHorizontalBottom() or (
                    getVerticalLeft() or (
                        getVerticalMid() or (
                            getVerticalRight() or (
                                getAngleUp() or (
                                    getAngleDown() or (
                                        getNoMovesAvailable()
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    private fun getNoMovesAvailable(): Boolean {
        var endGame = false
        if(_topLeft.value!= NOTHING){
            if(_topMid.value!= NOTHING){
                if(_topRight.value!= NOTHING){
                    if(_midLeft.value!= NOTHING){
                        if(_midMid.value!= NOTHING){
                            if(_midRight.value!= NOTHING){
                                if(_bottomLeft.value!= NOTHING){
                                    if(_bottomMid.value!= NOTHING){
                                        if(_bottomRight.value!= NOTHING){
                                            endGame = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return endGame
    }


    private fun checkLine(fieldFirst:MutableLiveData<Int>,fieldSecond:MutableLiveData<Int>,fieldThird:MutableLiveData<Int>):Boolean{
        var line = false
        if(fieldFirst.value!= NOTHING){
            if(fieldSecond.value!= NOTHING) {
                if (fieldThird.value!= NOTHING){
                    if(fieldFirst.value == fieldSecond.value){
                        if(fieldFirst.value == fieldThird.value){
                            line = true
                        }
                    }
                }
            }
        }
        return line
    }

    fun initializeMainPlayer(context: Context) {
        _mainPlayer.value = Player(true)
        _mainPlayer.value!!.setMark(X)
        _mainPlayer.value!!.setName(SharedPreferences.readPlayerName(context))
    }
    fun initializeOpponentPlayer(name:String) {
        val oPlayer = Player(false)
        oPlayer.setName(name)
        _opponentPlayer.value = oPlayer
        val mark = if(_mainPlayer.value!!.mark.value==X) O else X
        _opponentPlayer.value!!.setMark(mark)
    }

}
class GameViewModelFactory(private val opponentDao: OpponentDao) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(GameViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(opponentDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}