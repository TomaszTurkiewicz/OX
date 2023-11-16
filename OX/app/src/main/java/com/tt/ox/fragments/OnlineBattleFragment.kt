package com.tt.ox.fragments

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.X
import com.tt.ox.alertDialogs.AlertDialogEndGameOnlineBattle
import com.tt.ox.databinding.FragmentOnlineBattleBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.TurnDrawable
import com.tt.ox.drawables.WinLineDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.ANGLE_DOWN_LINE
import com.tt.ox.helpers.ANGLE_UP_LINE
import com.tt.ox.helpers.BOTTOM_LINE
import com.tt.ox.helpers.END_DRAW
import com.tt.ox.helpers.FirebaseBattle
import com.tt.ox.helpers.FirebaseHistory
import com.tt.ox.helpers.FirebaseRequests
import com.tt.ox.helpers.FirebaseUser
import com.tt.ox.helpers.HORIZONTAL_MID_LINE
import com.tt.ox.helpers.LEFT_LINE
import com.tt.ox.helpers.Marks
import com.tt.ox.helpers.NONE
import com.tt.ox.helpers.OUT_OF_TIME
import com.tt.ox.helpers.RIGHT_LINE
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.TOP_LINE
import com.tt.ox.helpers.Theme
import com.tt.ox.helpers.VERTICAL_MID_LINE
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.random.Random

private const val CONTINUE = 0
private const val WIN = 1
private const val DRAW = 2

class OnlineBattleFragment : FragmentCoroutine() {
    private var _binding:FragmentOnlineBattleBinding? = null
    private val binding get() = _binding!!
    private var unit =0
    private var width = 0
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var dbRefBattle:DatabaseReference? = null
    private val dbRefRequest = Firebase.database.getReference("Requests")
    private val dbRefUser = Firebase.database.getReference("Users")
    private val dbRefHistory = Firebase.database.getReference("History")
    private var battleId:String = ""
    private var opponentId:String = ""
    private var request:FirebaseRequests? = null
    private var score:FirebaseHistory? = null
    private val marks = Marks()
    private var battleListener: ValueEventListener? = null
    private lateinit var onlineBattle: FirebaseBattle
    private var myTurn = false
    private var play = false
    private var timestampStart = 0L
    private val clockHandler = Handler(Looper.getMainLooper())
    private var clockStarted = false
    private var dialog:AlertDialog? = null

    private var turnPointer = 0.5
    private var turnPointerLeft = false
    private val pointerJump = 0.05
    private val pointerLeft = 0.25
    private val pointerRight = 0.75
    private val turnHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineBattleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            Toast.makeText(requireContext(),"WAIT TO THE END OF TIME",Toast.LENGTH_LONG).show()
        }
        prepareUI()

    }

    override fun onResume() {
        super.onResume()
        val dbR = dbRefRequest.child(currentUser!!.uid)
        dbR.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    request = snapshot.getValue(FirebaseRequests::class.java)
                    battleId = request!!.battle!!
                    opponentId = request!!.opponentId!!
                    dbRefBattle = Firebase.database.getReference("Battle").child(battleId)
                    checkIfBattleExists()

                }else{
                    findNavController().navigateUp()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }



    override fun onPause() {
        super.onPause()
        clockStarted=false
        clockHandler.removeCallbacksAndMessages(null)
        dbRefBattle?.removeEventListener(battleListener!!)
    }

    private fun checkIfBattleExists() {
        dbRefBattle!!.addListenerForSingleValueEvent(object : ValueEventListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val b = snapshot.getValue(FirebaseBattle::class.java)
                    timestampStart = b!!.timestamp
                        setUpUI()
                }else{
                    val battle = FirebaseBattle()
                    battle.battleId = battleId
                    battle.timestamp = System.currentTimeMillis()
                    timestampStart = battle.timestamp
                    val random = Random.nextBoolean()
                    val startingPerson = if(random) currentUser!!.uid else opponentId
                    battle.turn = startingPerson
                    dbRefBattle!!.setValue(battle)
                    setUpUI()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun runClock():Runnable = Runnable {
        val currentTime = System.currentTimeMillis()
        val endTime = timestampStart+120000
        val remainingTime = (endTime-currentTime)/1000
        if(remainingTime>=0) {
            val minutes = remainingTime / 60
            val seconds = remainingTime % 60
            val f: NumberFormat = DecimalFormat("00")
            binding.time.text = "${f.format(minutes)}:${f.format(seconds)}"
            clockHandler.postDelayed(runClock(), 1000)
        }else{
            play = false
            clockHandler.removeCallbacksAndMessages(null)
            battleListener?.let {
                dbRefBattle?.removeEventListener(battleListener!!)
            }
            dbRefBattle!!.child("win").setValue(OUT_OF_TIME)
            val request = FirebaseRequests()
            dbRefRequest.child(currentUser!!.uid).setValue(request)
            dialog = AlertDialogEndGameOnlineBattle(requireContext(),layoutInflater,"OUT OF TIME"){
                dialog?.dismiss()
                findNavController().navigateUp()
            }.create()
            dialog?.show()
        }
    }

    private fun setUpUI() {

        val myDbRef = dbRefUser.child(currentUser!!.uid)
        myDbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshotMe: DataSnapshot) {
                val me = snapshotMe.getValue(FirebaseUser::class.java)
                binding.mainPlayerName.text = me!!.userName
                val opponentDbRef = dbRefUser.child(request!!.opponentId!!)
                opponentDbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshotOpponent: DataSnapshot) {
                        val opponent = snapshotOpponent.getValue(FirebaseUser::class.java)
                        binding.opponentPlayerName.text = opponent!!.userName
                        setHistory()
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setHistory() {
        val history = dbRefHistory.child(currentUser!!.uid).child(request!!.opponentId!!)
        history.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    score = FirebaseHistory()
                    history.setValue(score)
                    displayScore()
                }else{
                    score = snapshot.getValue(FirebaseHistory::class.java)
                    displayScore()
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private fun displayScore() {
        binding.mainPlayerWins.text = score!!.wins.toString()
        binding.opponentPlayerWins.text = score!!.loses.toString()
        displayMarks()

    }

    private fun displayMarks() {
        marks.initialize(requireContext())
        binding.mainPlayerMark.setImageDrawable(
            if(marks.playerMark == X) XDrawable(requireContext(),marks.playerColor,true) else ODrawable(requireContext(),marks.playerColor,true)
        )
        binding.opponentPlayerMark.setImageDrawable(
            if(marks.opponentMark == X) XDrawable(requireContext(),marks.opponentColor,true) else ODrawable(requireContext(),marks.opponentColor,true)
        )
        startGame()
    }

    private fun startGame() {
        setOnClickListeners()
        battleListener = dbRefBattle!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                onlineBattle = snapshot.getValue(FirebaseBattle::class.java)!!
                displayField()
                gameLogic()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun displayField() {
        binding.topLeftField.setImageDrawable(
            displayField(onlineBattle.field.topLeft)
        )
        binding.topMidField.setImageDrawable(
            displayField(onlineBattle.field.topMid)
        )
        binding.topRightField.setImageDrawable(
            displayField(onlineBattle.field.topRight)
        )

        binding.midLeftField.setImageDrawable(
            displayField(onlineBattle.field.midLeft)
        )
        binding.midMidField.setImageDrawable(
            displayField(onlineBattle.field.midMid)
        )
        binding.midRightField.setImageDrawable(
            displayField(onlineBattle.field.midRight)
        )

        binding.bottomLeftField.setImageDrawable(
            displayField(onlineBattle.field.bottomLeft)
        )
        binding.bottomMidField.setImageDrawable(
            displayField(onlineBattle.field.bottomMid)
        )
        binding.bottomRightField.setImageDrawable(
            displayField(onlineBattle.field.bottomRight)
        )
        displayWinningLine()
    }

    private fun displayWinningLine() {
        var horizontalTop = false
        var horizontalMid = false
        var horizontalBottom = false
        var verticalLeft = false
        var verticalMid = false
        var verticalRight = false
        var angleUp = false
        var angleDown = false

        when(onlineBattle.winningLine){
            TOP_LINE -> horizontalTop = true
            HORIZONTAL_MID_LINE -> horizontalMid = true
            BOTTOM_LINE -> horizontalBottom = true
            LEFT_LINE -> verticalLeft = true
            VERTICAL_MID_LINE -> verticalMid = true
            RIGHT_LINE -> verticalRight = true
            ANGLE_UP_LINE -> angleUp = true
            ANGLE_DOWN_LINE -> angleDown = true
        }

        binding.winLine.setImageDrawable(
            WinLineDrawable(
                requireContext(),
                horizontalTop, horizontalMid, horizontalBottom, verticalLeft, verticalMid, verticalRight, angleUp, angleDown
            )
        )
    }

    private fun displayField(string:String):Drawable?{
        return when(string){
            currentUser!!.uid -> markDrawable(true)
            request!!.opponentId -> markDrawable(false)
            else -> null
        }
    }

    private fun markDrawable(mainPlayer:Boolean):Drawable{
        return if(mainPlayer){
            if(marks.playerMark==X) XDrawable(requireContext(),marks.playerColor,false) else ODrawable(requireContext(),marks.playerColor,false)
        }else{
            if(marks.opponentMark==X) XDrawable(requireContext(),marks.opponentColor,false) else ODrawable(requireContext(),marks.opponentColor,false)
        }
    }

    private fun setOnClickListeners() {
        binding.topLeftField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.topLeft == ""){
                    playButtonClick()
                    onlineBattle.field.topLeft = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("topLeft").setValue(currentUser.uid)
                    }
                }
            }
        }
        binding.topMidField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.topMid == ""){
                    playButtonClick()
                    onlineBattle.field.topMid = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("topMid").setValue(currentUser.uid)
                    }
                }
                }
        }
        binding.topRightField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.topRight == ""){
                    playButtonClick()
                    onlineBattle.field.topRight = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("topRight").setValue(currentUser.uid)
                    }
                }
                }
        }

        binding.midLeftField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.midLeft == ""){
                    playButtonClick()
                    onlineBattle.field.midLeft = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("midLeft").setValue(currentUser.uid)
                    }
                }
            }
        }
        binding.midMidField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.midMid == ""){
                    playButtonClick()
                    onlineBattle.field.midMid = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("midMid").setValue(currentUser.uid)
                    }
                }
            }
        }
        binding.midRightField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.midRight == ""){
                    playButtonClick()
                    onlineBattle.field.midRight = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("midRight").setValue(currentUser.uid)
                    }
                }
            }
        }

        binding.bottomLeftField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.bottomLeft == ""){
                    playButtonClick()
                    onlineBattle.field.bottomLeft = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("bottomLeft").setValue(currentUser.uid)
                    }
                }
            }
        }
        binding.bottomMidField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.bottomMid == ""){
                    playButtonClick()
                    onlineBattle.field.bottomMid = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("bottomMid").setValue(currentUser.uid)
                    }
                }
            }
        }
        binding.bottomRightField.setOnClickListener {
            if(myTurn and (play)){
                if(onlineBattle.field.bottomRight == ""){
                    playButtonClick()
                    onlineBattle.field.bottomRight = currentUser!!.uid
                    checkWinningAndChangeTurn {
                        dbRefBattle!!.child("field").child("bottomRight").setValue(currentUser.uid)
                    }
                }
            }
        }
    }

    private fun checkWinningAndChangeTurn(
        setField: () -> Unit
    ){
        var endGame = CONTINUE

        if(onlineBattle.field.topLeft != "" &&(
                    onlineBattle.field.topMid != "" &&(
                            onlineBattle.field.topRight != "" &&(
                                    onlineBattle.field.midLeft != "" &&(
                                            onlineBattle.field.midMid != "" &&(
                                                    onlineBattle.field.midRight != "" &&(
                                                            onlineBattle.field.bottomLeft != "" &&(
                                                                    onlineBattle.field.bottomMid != "" &&(
                                                                            onlineBattle.field.bottomRight != ""
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                )
            ){
            endGame = DRAW
        }



        if(onlineBattle.field.topLeft == currentUser!!.uid && (
                onlineBattle.field.topMid == currentUser.uid && (
                        onlineBattle.field.topRight == currentUser.uid
                        )
                )
            ){
            onlineBattle.winningLine = TOP_LINE
            endGame = WIN
        }

        if(onlineBattle.field.midLeft == currentUser.uid && (
                    onlineBattle.field.midMid == currentUser.uid && (
                            onlineBattle.field.midRight == currentUser.uid
                            )
                    )
        ){
            onlineBattle.winningLine = HORIZONTAL_MID_LINE
            endGame = WIN
        }

        if(onlineBattle.field.bottomLeft == currentUser.uid && (
                    onlineBattle.field.bottomMid == currentUser.uid && (
                            onlineBattle.field.bottomRight == currentUser.uid
                            )
                    )
        ){
            onlineBattle.winningLine = BOTTOM_LINE
            endGame = WIN
        }

        if(onlineBattle.field.topLeft == currentUser.uid && (
                    onlineBattle.field.midLeft == currentUser.uid && (
                            onlineBattle.field.bottomLeft == currentUser.uid
                            )
                    )
        ){
            onlineBattle.winningLine = LEFT_LINE
            endGame = WIN
        }

        if(onlineBattle.field.topMid == currentUser.uid && (
                    onlineBattle.field.midMid == currentUser.uid && (
                            onlineBattle.field.bottomMid == currentUser.uid
                            )
                    )
        ){
            onlineBattle.winningLine = VERTICAL_MID_LINE
            endGame = WIN
        }

        if(onlineBattle.field.topRight == currentUser.uid && (
                    onlineBattle.field.midRight == currentUser.uid && (
                            onlineBattle.field.bottomRight == currentUser.uid
                            )
                    )
        ){
            onlineBattle.winningLine = RIGHT_LINE
            endGame = WIN
        }

        if(onlineBattle.field.topLeft == currentUser.uid && (
                    onlineBattle.field.midMid == currentUser.uid && (
                            onlineBattle.field.bottomRight == currentUser.uid
                            )
                    )
        ){
            onlineBattle.winningLine = ANGLE_DOWN_LINE
            endGame = WIN
        }

        if(onlineBattle.field.topRight == currentUser.uid && (
                    onlineBattle.field.midMid == currentUser.uid && (
                            onlineBattle.field.bottomLeft == currentUser.uid
                            )
                    )
        ){
            onlineBattle.winningLine = ANGLE_UP_LINE
            endGame = WIN
        }



        when(endGame){
            CONTINUE -> {
                setField()
                dbRefBattle!!.child("turn").setValue(request!!.opponentId)
            }
            WIN -> {
                playWinSound()
                clockHandler.removeCallbacksAndMessages(null)
                dbRefBattle?.removeEventListener(battleListener!!)
                play = false
                displayField()
                displayWinningLine()
                setField()
                dbRefBattle!!.child("winningLine").setValue(onlineBattle.winningLine)
                dbRefBattle!!.child("win").setValue(currentUser.uid)

                val history = dbRefHistory.child(currentUser.uid).child(request!!.opponentId!!)
                history.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val firebaseHistory = snapshot.getValue(FirebaseHistory::class.java)
                        firebaseHistory!!.addWin()
                        history.setValue(firebaseHistory)
                        val request = FirebaseRequests()
                        dbRefRequest.child(currentUser.uid).setValue(request)
                        val userDb = dbRefUser.child(currentUser.uid)
                        userDb.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    val user = snapshot.getValue(FirebaseUser::class.java)
                                    user!!.addWins()
                                    userDb.setValue(user)
                                    dialog = AlertDialogEndGameOnlineBattle(requireContext(),layoutInflater,"WIN"){
                                        playButtonClick()
                                        dialog?.dismiss()
                                        findNavController().navigateUp()
                                    }.create()
                                    dialog?.show()
                                }else{
                                    val user = FirebaseUser()
                                    user.addWins()
                                    userDb.setValue(user)
                                    dialog = AlertDialogEndGameOnlineBattle(requireContext(),layoutInflater,"WIN"){
                                        playButtonClick()
                                        dialog?.dismiss()
                                        findNavController().navigateUp()
                                    }.create()
                                    dialog?.show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })

                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            DRAW -> {
                clockHandler.removeCallbacksAndMessages(null)
                dbRefBattle?.removeEventListener(battleListener!!)
                displayField()
                setField()
                dbRefBattle!!.child("win").setValue(END_DRAW)
                val request = FirebaseRequests()
                dbRefRequest.child(currentUser.uid).setValue(request)
                dialog = AlertDialogEndGameOnlineBattle(requireContext(),layoutInflater,"DRAW"){
                    playButtonClick()
                    dialog?.dismiss()
                    findNavController().navigateUp()
                }.create()
                dialog?.show()
            }
        }
    }

    private fun gameLogic() {
        when(onlineBattle.win){
            NONE -> {
                if(!clockStarted){
                    clockStarted=true
                    runClock().run()
                }
                myTurn = onlineBattle.turn == currentUser!!.uid
                play = onlineBattle.win == NONE
                if(myTurn){
                    turnPointerLeft = true
                    turnHandler.postDelayed(movePointer(),0)
                }else{
                    turnPointerLeft = false
                    turnHandler.postDelayed(movePointer(),0)
                }
            }
            request!!.opponentId -> {
                clockHandler.removeCallbacksAndMessages(null)
                displayWinningLine()
                dbRefBattle?.removeEventListener(battleListener!!)
                val history = dbRefHistory.child(currentUser!!.uid).child(request!!.opponentId!!)
                history.addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val firebaseHistory = snapshot.getValue(FirebaseHistory::class.java)
                        firebaseHistory!!.addLose()
                        history.setValue(firebaseHistory)
                        dbRefBattle?.removeValue()
                        val request = FirebaseRequests()
                        dbRefRequest.child(currentUser.uid).setValue(request)


                        val userDb = dbRefUser.child(currentUser.uid)
                        userDb.addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if(snapshot.exists()){
                                    val user = snapshot.getValue(FirebaseUser::class.java)
                                    user!!.addLoses()
                                    userDb.setValue(user)
                                    dialog = AlertDialogEndGameOnlineBattle(requireContext(),layoutInflater,"LOSE"){
                                        playButtonClick()
                                        dialog?.dismiss()
                                        findNavController().navigateUp()
                                    }.create()
                                    dialog?.show()
                                }else{
                                    val user = FirebaseUser()
                                    user.addLoses()
                                    userDb.setValue(user)
                                    dialog = AlertDialogEndGameOnlineBattle(requireContext(),layoutInflater,"LOSE"){
                                        playButtonClick()
                                        dialog?.dismiss()
                                        findNavController().navigateUp()
                                    }.create()
                                    dialog?.show()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                            }

                        })

                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
            }
            END_DRAW -> {
                clockHandler.removeCallbacksAndMessages(null)
                dbRefBattle?.removeEventListener(battleListener!!)

                        dbRefBattle?.removeValue()
                        val request = FirebaseRequests()
                        dbRefRequest.child(currentUser!!.uid).setValue(request)
                dialog = AlertDialogEndGameOnlineBattle(requireContext(),layoutInflater,"DRAW"){
                    playButtonClick()
                    dialog?.dismiss()
                    findNavController().navigateUp()
                }.create()
                dialog?.show()
            }
            OUT_OF_TIME -> {
                dbRefBattle?.removeEventListener(battleListener!!)

                Firebase.database.getReference("Battle").child(request!!.battle!!).removeValue()
                val request = FirebaseRequests()
                dbRefRequest.child(currentUser!!.uid).setValue(request)
                dialog = AlertDialogEndGameOnlineBattle(requireContext(),layoutInflater,"OUT OF TIME"){
                    playButtonClick()
                    dialog?.dismiss()
                    findNavController().navigateUp()
                }.create()
                dialog?.show()
            }
        }


    }

    private fun movePointer():Runnable = kotlinx.coroutines.Runnable {
        val delay = 10L
        if (turnPointerLeft) {
            // move left
            if (turnPointer > pointerLeft) {
                turnPointer -= pointerJump
                displayPointer()
                turnHandler.postDelayed(movePointer(), delay)
            } else {
                turnPointer = pointerLeft
                displayPointer()
                turnHandler.removeCallbacksAndMessages(null)
            }
        } else {
            //move right
            if (turnPointer < pointerRight) {
                turnPointer += pointerJump
                displayPointer()
                turnHandler.postDelayed(movePointer(), delay)
            } else {
                turnPointer = pointerRight
                displayPointer()
                turnHandler.removeCallbacksAndMessages(null)
            }
        }
    }

    private fun displayPointer(){
        view?.let {
            binding.turn.setImageDrawable(TurnDrawable(requireContext(),turnPointer))
        }
    }


    private fun prepareUI() {
        setSizes()
        setDrawables()
        setConstraint()
    }

    private fun setConstraint() {
        val set = ConstraintSet()

        set.clone(binding.onlineBattleLayout)

        set.connect(binding.backgroundField.id,
            ConstraintSet.TOP, binding.onlineBattleLayout.id,
            ConstraintSet.TOP,3*unit)
        set.connect(binding.backgroundField.id,
            ConstraintSet.BOTTOM, binding.onlineBattleLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.LEFT, binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.RIGHT, binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.midMidField.id,
            ConstraintSet.TOP, binding.backgroundField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.BOTTOM, binding.backgroundField.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.LEFT, binding.backgroundField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.RIGHT, binding.backgroundField.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.topMidField.id,
            ConstraintSet.BOTTOM,binding.midMidField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.topMidField.id,
            ConstraintSet.LEFT,binding.midMidField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.topMidField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.topRightField.id,
            ConstraintSet.LEFT,binding.midMidField.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.topRightField.id,
            ConstraintSet.BOTTOM,binding.midMidField.id,
            ConstraintSet.TOP,0)

        set.connect(binding.midRightField.id,
            ConstraintSet.LEFT, binding.midMidField.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.midRightField.id,
            ConstraintSet.TOP, binding.midMidField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.midRightField.id,
            ConstraintSet.BOTTOM, binding.midMidField.id,
            ConstraintSet.BOTTOM,0)

        set.connect(binding.bottomRightField.id,
            ConstraintSet.LEFT,binding.midMidField.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.bottomRightField.id,
            ConstraintSet.TOP,binding.midMidField.id,
            ConstraintSet.BOTTOM,0)

        set.connect(binding.bottomMidField.id,
            ConstraintSet.TOP,binding.midMidField.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.bottomMidField.id,
            ConstraintSet.LEFT,binding.midMidField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.bottomMidField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.bottomLeftField.id,
            ConstraintSet.TOP,binding.midMidField.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.bottomLeftField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.midLeftField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.midLeftField.id,
            ConstraintSet.TOP,binding.midMidField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.midLeftField.id,
            ConstraintSet.BOTTOM,binding.midMidField.id,
            ConstraintSet.BOTTOM,0)

        set.connect(binding.topLeftField.id,
            ConstraintSet.BOTTOM,binding.midMidField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.topLeftField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.winLine.id,
            ConstraintSet.TOP, binding.backgroundField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.winLine.id,
            ConstraintSet.BOTTOM, binding.backgroundField.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.winLine.id,
            ConstraintSet.LEFT, binding.backgroundField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.winLine.id,
            ConstraintSet.RIGHT, binding.backgroundField.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.divider.id, ConstraintSet.TOP,binding.onlineBattleLayout.id, ConstraintSet.TOP,0)
        set.connect(binding.divider.id,
            ConstraintSet.LEFT,binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.divider.id,
            ConstraintSet.RIGHT,binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.time.id,ConstraintSet.TOP,binding.onlineBattleLayout.id,ConstraintSet.TOP,unit/4)
        set.connect(binding.time.id,ConstraintSet.LEFT,binding.onlineBattleLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.time.id,ConstraintSet.RIGHT,binding.onlineBattleLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.TOP,binding.time.id,
            ConstraintSet.TOP, (unit*1.5).toInt())
        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.LEFT,binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerWins.id, ConstraintSet.RIGHT,
            binding.divider.id, ConstraintSet.LEFT,0)

        set.connect(binding.mainPlayerName.id,
            ConstraintSet.TOP,binding.mainPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.LEFT,binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.RIGHT,binding.divider.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.LEFT,binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.TOP,binding.mainPlayerName.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.RIGHT,binding.divider.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.TOP,binding.time.id,
            ConstraintSet.TOP,(unit*1.5).toInt())
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.RIGHT,binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,unit)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,unit)

        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.TOP,binding.opponentPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.RIGHT,binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.RIGHT,binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.TOP,binding.opponentPlayerName.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.turn.id,ConstraintSet.TOP,binding.mainPlayerWins.id,ConstraintSet.TOP,0)
        set.connect(binding.turn.id,ConstraintSet.BOTTOM,binding.mainPlayerMark.id,ConstraintSet.BOTTOM,0)

        set.applyTo(binding.onlineBattleLayout)

    }

    private fun setDrawables() {
        binding.onlineBattleLayout.background = BackgroundColorDrawable(requireContext())

        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))
        binding.mainPlayerWins.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.opponentPlayerWins.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.mainPlayerName.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.opponentPlayerName.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.time.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))

        
    }

    private fun setSizes() {
        val fieldSize = 3*unit
        binding.backgroundField.layoutParams = ConstraintLayout.LayoutParams(3*fieldSize,3*fieldSize)
        binding.topLeftField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.topMidField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.topRightField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.midLeftField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.midMidField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.midRightField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.bottomLeftField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.bottomMidField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.bottomRightField.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.winLine.layoutParams = ConstraintLayout.LayoutParams(3*fieldSize,3*fieldSize)
        binding.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit*0.8f)

        binding.turn.layoutParams = ConstraintLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,4*unit)
        binding.mainPlayerWins.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
        binding.opponentPlayerWins.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
        binding.mainPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
        binding.opponentPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
        binding.mainPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            binding.mainPlayerName.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                binding.mainPlayerName,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            binding.opponentPlayerName.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else{
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                binding.opponentPlayerName,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            binding.mainPlayerWins.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else{
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                binding.mainPlayerWins,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            binding.opponentPlayerWins.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else{
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                binding.opponentPlayerWins,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }
    }
}