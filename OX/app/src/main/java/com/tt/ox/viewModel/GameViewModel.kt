package com.tt.ox.viewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tt.ox.BOTTOM_LEFT
import com.tt.ox.BOTTOM_MID
import com.tt.ox.BOTTOM_RIGHT
import com.tt.ox.EASY_GAME
import com.tt.ox.HARD_GAME
import com.tt.ox.MAIN_PLAYER
import com.tt.ox.MID_LEFT
import com.tt.ox.MID_MID
import com.tt.ox.MID_RIGHT
import com.tt.ox.MOVES
import com.tt.ox.NORMAL_GAME
import com.tt.ox.NOTHING
import com.tt.ox.NO_ONE
import com.tt.ox.OPPONENT
import com.tt.ox.TOP_LEFT
import com.tt.ox.TOP_MID
import com.tt.ox.TOP_RIGHT
import com.tt.ox.database.Opponent
import com.tt.ox.database.OpponentDao
import com.tt.ox.helpers.Board
import com.tt.ox.helpers.Game
import com.tt.ox.helpers.Marks
import com.tt.ox.helpers.PhoneMoveEasy
import com.tt.ox.helpers.PhoneMoveHard
import com.tt.ox.helpers.PhoneMoveNormal
import com.tt.ox.helpers.SharedPreferences
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(private val opponentDao: OpponentDao) : ViewModel() {

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

//    private val _buttonSwitch = MutableLiveData<Boolean>()
//    val buttonSwitch:LiveData<Boolean> = _buttonSwitch

    private val _game = MutableLiveData<Game>()
    val game:LiveData<Game> = _game

    private val _play = MutableLiveData<Boolean>()
    val play :LiveData<Boolean> = _play


    val board = Board()

    val listOfOpponents:LiveData<List<Opponent>> = opponentDao.getOpponents().asLiveData()


    fun initializeGame(context: Context, opponent: Opponent){
        _game.value = Game(context,opponent)


    }

    fun getWiningPerson():Int{
        return this.winingPerson
    }

    fun getHorizontalTop():Boolean{
        return this.board.getHorizontalTop()
    }
    fun getHorizontalMid():Boolean{
        return this.board.getHorizontalMid()
    }
    fun getHorizontalBottom():Boolean{
        return this.board.getHorizontalBottom()
    }
    fun getVerticalLeft():Boolean{
        return this.board.getVerticalLeft()
    }
    fun getVerticalMid():Boolean{
        return this.board.getVerticalMid()
    }
    fun getVerticalRight():Boolean{
        return this.board.getVerticalRight()
    }
    fun getAngleUp():Boolean{
        return this.board.getAngleUp()
    }
    fun getAngleDown():Boolean{
        return this.board.getAngleDown()
    }
//    fun switchMarks(){
//        game.value!!.switchMarks()
//    }

//    fun setOpponentMarkColor(color:Int){
//        game.value!!.setOpponentMarkColor(color)
//    }

//    fun setMainPlayerMarkColor(color:Int){
//        game.value!!.setPlayerMarkColor(color)
//    }

    fun initialize(firstGame:Boolean){
        _win.value = false
        _play.value = _moves.value!!>0
        board.initialize()
        setStartingTurn(firstGame)

//        _buttonSwitch.value = board.setSwitchButtonEnable()
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
        board.checkWin()
        val win = board.getWin()
        val noMoves = board.checkMovesNotAvailable()
//        board.set_horizontalTop = checkLine(board.getTopLeft(), board.getTopMid(), board.getTopRight())
//        board.set_horizontalMid = checkLine(board.getMidLeft(), board.getMidMid(), board.getMidRight())
//        board.set_horizontalBottom = checkLine(board.getBottomLeft(), board.getBottomMid(), board.getBottomRight())
//        board.set_verticalLeft = checkLine(board.getTopLeft(), board.getMidLeft(), board.getBottomLeft())
//        board.set_verticalMid = checkLine(board.getTopMid(), board.getMidMid(), board.getBottomMid())
//        board.set_verticalRight = checkLine(board.getTopRight(), board.getMidRight(), board.getBottomRight())
//        board.set_angleDown = checkLine(board.getTopLeft(), board.getMidMid(), board.getBottomRight())
//        board.set_angleUp = checkLine(board.getBottomLeft(), board.getMidMid(), board.getTopRight())
        return win or (noMoves)
    }

    fun setBottomRight(context: Context,marks:Marks){
        setField(context,board.getBottomRight(),marks)
    }
    fun setBottomMid(context: Context,marks:Marks){
        setField(context,board.getBottomMid(),marks)
    }
    fun setBottomLeft(context: Context,marks:Marks){
        setField(context,board.getBottomLeft(),marks)
    }
    fun setMidRight(context: Context,marks:Marks){
        setField(context,board.getMidRight(),marks)
    }
    fun setMidMid(context: Context,marks:Marks){
        setField(context,board.getMidMid(),marks)
    }
    fun setMidLeft(context: Context,marks:Marks){
        setField(context,board.getMidLeft(),marks)
    }
    fun setTopRight(context: Context,marks:Marks){
        setField(context,board.getTopRight(),marks)
    }
    fun setTopMid(context: Context,marks:Marks){
        setField(context,board.getTopMid(),marks)
    }
    fun setTopLeft(context: Context,marks:Marks){
        setField(context,board.getTopLeft(),marks)
    }


    fun playPhone(context: Context, mode:Int, marks:Marks){
            phoneMakeMove(context,mode,marks)
    }

    private fun setFieldPhone(context: Context, field:MutableLiveData<Int>,mode:Int, marks:Marks){
        if(field.value!! == NOTHING){
            setField(context,field,marks)
        }else{
            phoneMakeMove(context,mode,marks)
        }
    }

    fun addMoves(context: Context){
        resetMoves(context)
    }

    private fun resetMoves(context: Context){
        _moves.value = MOVES
        saveMovesToSharedPreferences(context)
    }

    private fun phoneMakeMove(context:Context, mode:Int, marks: Marks) {

        when (mode){
            EASY_GAME -> move(context,PhoneMoveEasy(board).makeMove(),mode,marks)
            NORMAL_GAME -> move(context, PhoneMoveNormal(board,marks.playerMark,marks.opponentMark).makeMove(),mode,marks)
            HARD_GAME -> move(context, PhoneMoveHard(board,marks.playerMark,marks.opponentMark).makeMove(),mode,marks)
        }

    }

    private fun move(context: Context, move:Int,mode:Int, marks:Marks){
        when(move){
            TOP_LEFT -> setFieldPhone(context,board.getTopLeft(),mode,marks)
            TOP_MID -> setFieldPhone(context,board.getTopMid(),mode,marks)
            TOP_RIGHT -> setFieldPhone(context,board.getTopRight(),mode,marks)
            MID_LEFT -> setFieldPhone(context,board.getMidLeft(),mode,marks)
            MID_MID -> setFieldPhone(context,board.getMidMid(),mode,marks)
            MID_RIGHT -> setFieldPhone(context,board.getMidRight(),mode,marks)
            BOTTOM_LEFT -> setFieldPhone(context,board.getBottomLeft(),mode,marks)
            BOTTOM_MID -> setFieldPhone(context,board.getBottomMid(),mode,marks)
            BOTTOM_RIGHT -> setFieldPhone(context,board.getBottomRight(),mode,marks)
            else -> phoneMakeMove(context,mode,marks)
        }
    }

    private fun setField(context: Context,field: MutableLiveData<Int>,marks:Marks){
        if(play.value==true){
            val marked = board.setField(
                if(turn.value!!) marks.playerMark else marks.opponentMark,
                field
            )
            if(marked){
                decreaseMoves(context)
                val endGame = checkLines()
                if (endGame) {
                    winingMark = board.getWinningMark()
                    _play.value = false
                    when(winingMark){
                        marks.playerMark -> addWinToMainPlayer()
                        marks.opponentMark -> addWinToOpponent()
                        else -> clearWinningMark()
                    }
                    _win.value = true
                } else {
                    changePerson()
                }
//                _buttonSwitch.value = board.setSwitchButtonEnable()
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

    private fun setStartingTurn(firstGame:Boolean){
        if(firstGame){
            setFirstStartingTurn()
        }else{
            setAnotherStartingTurn()
        }
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