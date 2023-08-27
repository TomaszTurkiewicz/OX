package com.tt.ox.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tt.ox.MAIN_PLAYER
import com.tt.ox.NOTHING
import com.tt.ox.NO_ONE
import com.tt.ox.OPPONENT
import com.tt.ox.database.Opponent
import com.tt.ox.database.OpponentDao
import com.tt.ox.helpers.Game
import com.tt.ox.helpers.SharedPreferences
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(private val opponentDao: OpponentDao) : ViewModel() {

    private var mainPlayerStarted = true

    private val _moves = MutableLiveData<Int>()
    val moves:LiveData<Int> = _moves

    private var number = 0

    private var movesDecreased = false

    private val _turn = MutableLiveData<Boolean>()
    val turn:LiveData<Boolean> = _turn

    private var winingMark = NOTHING
    private var winingPerson = NO_ONE

    private val _win = MutableLiveData<Boolean>()
    val win:LiveData<Boolean> = _win

    private val _buttonSwitch = MutableLiveData<Boolean>()
    val buttonSwitch:LiveData<Boolean> = _buttonSwitch

    private val _game = MutableLiveData<Game>()
    val game:LiveData<Game> = _game

//    private val _mainPlayer = MutableLiveData<Player>()
//    val mainPlayer: LiveData<Player> = _mainPlayer
//    private val _opponentPlayer = MutableLiveData<Player>()
//    val opponentPlayer: LiveData<Player> = _opponentPlayer

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


    fun initializeGame(context: Context, opponent: Opponent){
        _game.value = Game(context,opponent)
    }

//    fun initializeMainPlayerDatabase(name:String) {
//        val mPlayer = Player()
//        mPlayer.setName(name)
//        _mainPlayer.value = mPlayer
//        _mainPlayer.value!!.setMark(mPlayer.mark.value!!)
//    }

//    fun initializeOpponentPlayerSinglePlayer() {
//        val oPlayer = Player()
//        oPlayer.setName("ROBOT")
//        _opponentPlayer.value = oPlayer
//        val mark = if(_mainPlayer.value!!.mark.value== X) O else X
//        _opponentPlayer.value!!.setMark(mark)
//    }

//    fun initializeOpponentPlayerMultiPlayer(name:String) {
//        val oPlayer = Player()
//        oPlayer.setName(name)
//        _opponentPlayer.value = oPlayer
//        val mark = if(_mainPlayer.value!!.mark.value==X) O else X
//        _opponentPlayer.value!!.setMark(mark)
//    }

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
    fun switchMarks(){
        game.value!!.switchMarks()
//        val opponentPlayerMark = _opponentPlayer.value!!.mark.value!!
//        val mainPlayerMark = _mainPlayer.value!!.mark.value!!
//
//        _mainPlayer.value!!.setMark(opponentPlayerMark)
//        _opponentPlayer.value!!.setMark(mainPlayerMark)
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

        _win.value = false

        if(_moves.value!!>0) {
            _play.value = true
        }


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

    private fun decreaseMoves(context: Context){
        if(!movesDecreased){
            movesDecreased = true
            if(_moves.value!!>0){
                _moves.value = _moves.value!!-1
                saveMovesToSharedPreferences(context)
            }
        }

    }

    private fun saveMovesToSharedPreferences(context: Context){
        SharedPreferences.saveMoves(context,_moves.value!!)
    }

    private fun checkLines(): Boolean {
        _horizontalTop.value = checkLine(_topLeft, _topMid, _topRight)
        _horizontalMid.value = checkLine(_midLeft, _midMid, _midRight)
        _horizontalBottom.value = checkLine(_bottomLeft, _bottomMid, _bottomRight)
        _verticalLeft.value = checkLine(_topLeft, _midLeft, _bottomLeft)
        _verticalMid.value = checkLine(_topMid, _midMid, _bottomMid)
        _verticalRight.value = checkLine(_topRight, _midRight, _bottomRight)
        _angleDown.value = checkLine(_topLeft, _midMid, _bottomRight)
        _angleUp.value = checkLine(_bottomLeft, _midMid, _topRight)
        return checkWin()
    }

    fun setBottomRight(context: Context){
        setField(context,_bottomRight)
    }
    fun setBottomMid(context: Context){
        setField(context,_bottomMid)
    }
    fun setBottomLeft(context: Context){
        setField(context,_bottomLeft)
    }
    fun setMidRight(context: Context){
        setField(context,_midRight)
    }
    fun setMidMid(context: Context){
        setField(context,_midMid)
    }
    fun setMidLeft(context: Context){
        setField(context,_midLeft)
    }
    fun setTopRight(context: Context){
        setField(context,_topRight)
    }
    fun setTopMid(context: Context){
        setField(context,_topMid)
    }
    fun setTopLeft(context: Context){
        setField(context,_topLeft)
    }

    private fun checkLine(fieldFirst:MutableLiveData<Int>,fieldSecond:MutableLiveData<Int>,fieldThird:MutableLiveData<Int>):Boolean{
        var line = false
        if(fieldFirst.value!= NOTHING){
            if(fieldSecond.value!= NOTHING) {
                if (fieldThird.value!= NOTHING){
                    if(fieldFirst.value == fieldSecond.value){
                        if(fieldFirst.value == fieldThird.value){
                            line = true
                            winingMark = fieldFirst.value!!

                        }
                    }
                }
            }
        }
        return line
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

    fun playPhone(context: Context){
            phoneMakeMove(context)
    }

    private fun setFieldPhone(context: Context, field:MutableLiveData<Int>){
        if(field.value!! == NOTHING){
            setField(context,field)
        }else{
            number+=1
            number %= 9
            phoneMakeMove(context)
        }
    }

    fun addMoves(context: Context){
        resetMoves(context)
    }

    private fun resetMoves(context: Context){
        _moves.value = 10
        saveMovesToSharedPreferences(context)
    }

    private fun phoneMakeMove(context:Context) {
        when(number){
            0 -> setFieldPhone(context,_topLeft)
            1 -> setFieldPhone(context,_topMid)
            2 -> setFieldPhone(context,_topRight)
            3 -> setFieldPhone(context,_midLeft)
            4 -> setFieldPhone(context,_midMid)
            5 -> setFieldPhone(context,_midRight)
            6 -> setFieldPhone(context,_bottomLeft)
            7 -> setFieldPhone(context,_bottomMid)
            8 -> setFieldPhone(context,_bottomRight)
            else -> phoneMakeMove(context)

        }
    }

    private fun setField(context: Context,field: MutableLiveData<Int>){
        if(play.value==true){
            if(field.value == NOTHING) {
                decreaseMoves(context)
                field.value = if(turn.value!!) _game.value!!.getMainPlayerMark() else _game.value!!.getOpponentMark()
                val endGame = checkLines()

                if (endGame) {
                    _play.value = false
                    when(winingMark){
                        _game.value!!.getMainPlayerMark() -> addWinToMainPlayer()
                        _game.value!!.getOpponentMark() -> addWinToOpponent()
                        else -> clearWinningMark()
                    }
                    _win.value = true
                } else {
                    changePerson()
                }
                setButtonEnable()
            }
        }
    }

    private fun changePerson(){
        _turn.value = !_turn.value!!
    }

    private fun clearWinningMark() {
        winingMark = NOTHING
    }

    private fun addWinToMainPlayer() {
        winingPerson = MAIN_PLAYER
        winingMark = NOTHING
    }

    private fun addWinToOpponent() {
        winingPerson = OPPONENT
        winingMark = NOTHING
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

    fun getOpponentMultiPlayer(id:Int): LiveData<Opponent>{
        return opponentDao.getOpponent(id).asLiveData()
    }
    fun addNewOpponent(name:String){
        val opponent = getNewOpponentEntity(name)
        insertNewOpponent(opponent)
    }

    private fun getNewOpponentEntity(name:String):Opponent{
        return Opponent(
            name = name
        )
    }

    fun updateOpponent(opponent: Opponent){
        viewModelScope.launch {
            opponentDao.update(opponent)
        }
    }

    fun deleteOpponentMultiPlayer(opponent: Opponent){
        viewModelScope.launch {
            opponentDao.delete(opponent)
        }
    }

    private fun insertNewOpponent(opponent: Opponent){
        viewModelScope.launch {
            opponentDao.insert(opponent)
        }
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

//todo better phone moves