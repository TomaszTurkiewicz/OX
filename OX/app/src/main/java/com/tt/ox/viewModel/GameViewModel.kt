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
import com.tt.ox.helpers.Board
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

//    private val _horizontalTop = MutableLiveData<Boolean>()
//    private val _horizontalMid = MutableLiveData<Boolean>()
//    private val _horizontalBottom = MutableLiveData<Boolean>()
//    private val _verticalLeft = MutableLiveData<Boolean>()
//    private val _verticalMid = MutableLiveData<Boolean>()
//    private val _verticalRight = MutableLiveData<Boolean>()
//    private val _angleDown = MutableLiveData<Boolean>()
//    private val _angleUp = MutableLiveData<Boolean>()

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
    fun switchMarks(){
        game.value!!.switchMarks()
    }

    fun initialize(firstGame:Boolean){
//        _topLeft.value = NOTHING
//        _topMid.value = NOTHING
//        _topRight.value = NOTHING
//
//        _midLeft.value = NOTHING
//        _midMid.value = NOTHING
//        _midRight.value = NOTHING
//
//        _bottomLeft.value = NOTHING
//        _bottomMid.value = NOTHING
//        _bottomRight.value = NOTHING

//        _horizontalTop.value = false
//        _horizontalMid.value = false
//        _horizontalBottom.value = false
//        _verticalLeft.value = false
//        _verticalMid.value = false
//        _verticalRight.value = false
//        _angleUp.value = false
//        _angleDown.value = false

        _win.value = false

        if(_moves.value!!>0) {
            _play.value = true
        }

        board.initialize()


        setStartingTurn(firstGame)

        _buttonSwitch.value = board.setSwitchButtonEnable()
//        setButtonEnable()

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

    fun setBottomRight(context: Context){
        setField(context,board.getBottomRight())
    }
    fun setBottomMid(context: Context){
        setField(context,board.getBottomMid())
    }
    fun setBottomLeft(context: Context){
        setField(context,board.getBottomLeft())
    }
    fun setMidRight(context: Context){
        setField(context,board.getMidRight())
    }
    fun setMidMid(context: Context){
        setField(context,board.getMidMid())
    }
    fun setMidLeft(context: Context){
        setField(context,board.getMidLeft())
    }
    fun setTopRight(context: Context){
        setField(context,board.getTopRight())
    }
    fun setTopMid(context: Context){
        setField(context,board.getTopMid())
    }
    fun setTopLeft(context: Context){
        setField(context,board.getTopLeft())
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
            0 -> setFieldPhone(context,board.getTopLeft())
            1 -> setFieldPhone(context,board.getTopMid())
            2 -> setFieldPhone(context,board.getTopRight())
            3 -> setFieldPhone(context,board.getMidLeft())
            4 -> setFieldPhone(context,board.getMidMid())
            5 -> setFieldPhone(context,board.getMidRight())
            6 -> setFieldPhone(context,board.getBottomLeft())
            7 -> setFieldPhone(context,board.getBottomMid())
            8 -> setFieldPhone(context,board.getBottomRight())
            else -> phoneMakeMove(context)

        }
    }

    private fun setField(context: Context,field: MutableLiveData<Int>){
        if(play.value==true){
            val marked = board.setField(
                if(turn.value!!) _game.value!!.getMainPlayerMark() else _game.value!!.getOpponentMark(),
                field
            )
            if(marked){
                decreaseMoves(context)
                val endGame = checkLines()
                if (endGame) {
                    winingMark = board.getWinningMark()
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
                _buttonSwitch.value = board.setSwitchButtonEnable()
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

//todo better phone moves