package com.tt.ox.fragments

import android.app.ActionBar
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.R
import com.tt.ox.X
import com.tt.ox.databinding.FragmentOnlineBattleBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.PointerUpperDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.FirebaseBattle
import com.tt.ox.helpers.FirebaseHistory
import com.tt.ox.helpers.FirebaseRequests
import com.tt.ox.helpers.FirebaseUser
import com.tt.ox.helpers.OnlineMarks
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences
import kotlin.random.Random

class OnlineBattleFragment : Fragment() {
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
    private lateinit var marks: OnlineMarks
    private var battleListener: ValueEventListener? = null
    private lateinit var onlineBattle: FirebaseBattle
    private var myTurn = false

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
        dbRefBattle?.removeEventListener(battleListener!!)
    }

    private fun checkIfBattleExists() {
        dbRefBattle!!.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                        setUpUI()
                }else{
                    val battle = FirebaseBattle()
                    battle.battleId = battleId
                    battle.timestamp = System.currentTimeMillis()
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
        marks = SharedPreferences.readOnlineMarks(requireContext())
        binding.mainPlayerMark.setImageDrawable(
            if(marks.playerMark == X) XDrawable(requireContext(),marks.playerColor,true) else ODrawable(requireContext(),marks.playerColor,true)
        )
        binding.opponentPlayerMark.setImageDrawable(
            if(marks.opponentMark == X) XDrawable(requireContext(),marks.opponentColor,true) else ODrawable(requireContext(),marks.opponentColor,true)
        )
        startGame()
    }

    private fun startGame() {
        battleListener = dbRefBattle!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                onlineBattle = snapshot.getValue(FirebaseBattle::class.java)!!
                setOnClickListeners()
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
            if(myTurn){
                if(onlineBattle.field.topLeft == ""){
                    dbRefBattle!!.child("field").child("topLeft").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
            }
        }
        binding.topMidField.setOnClickListener {
            if(myTurn){
                if(onlineBattle.field.topMid == ""){
                    dbRefBattle!!.child("field").child("topMid").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
                }
        }
        binding.topRightField.setOnClickListener {
            if(myTurn){
                if(onlineBattle.field.topRight == ""){
                    dbRefBattle!!.child("field").child("topRight").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
                }
        }

        binding.midLeftField.setOnClickListener {
            if(myTurn){
                if(onlineBattle.field.midLeft == ""){
                    dbRefBattle!!.child("field").child("midLeft").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
            }
        }
        binding.midMidField.setOnClickListener {
            if(myTurn){
                if(onlineBattle.field.midMid == ""){
                    dbRefBattle!!.child("field").child("midMid").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
            }
        }
        binding.midRightField.setOnClickListener {
            if(myTurn){
                if(onlineBattle.field.midRight == ""){
                    dbRefBattle!!.child("field").child("midRight").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
            }
        }

        binding.bottomLeftField.setOnClickListener {
            if(myTurn){
                if(onlineBattle.field.bottomLeft == ""){
                    dbRefBattle!!.child("field").child("bottomLeft").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
            }
        }
        binding.bottomMidField.setOnClickListener {
            if(myTurn){
                if(onlineBattle.field.bottomMid == ""){
                    dbRefBattle!!.child("field").child("bottomMid").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
            }
        }
        binding.bottomRightField.setOnClickListener {
            if(myTurn){
                if(onlineBattle.field.bottomRight == ""){
                    dbRefBattle!!.child("field").child("bottomRight").setValue(currentUser!!.uid)
                    checkWinningAndChangeTurn()
                }
            }
        }
    }

    private fun checkWinningAndChangeTurn(){
        //todo check winning
        dbRefBattle!!.child("turn").setValue(request!!.opponentId)
    }

    private fun gameLogic() {
        myTurn = onlineBattle.turn == currentUser!!.uid
        if(myTurn){
            binding.mainPlayerPointerUpper.visibility = View.VISIBLE
            binding.opponentPointerUpper.visibility = View.GONE
        }else{
            binding.mainPlayerPointerUpper.visibility = View.GONE
            binding.opponentPointerUpper.visibility = View.VISIBLE
        }
    }



    private fun prepareUI() {
        setSizes()
        setDrawables()
        setConstraint()
        binding.mainPlayerPointerUpper.visibility = View.GONE
        binding.opponentPointerUpper.visibility = View.GONE
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

        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.TOP,binding.onlineBattleLayout.id,
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
            ConstraintSet.TOP,binding.onlineBattleLayout.id,
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

        set.connect(binding.mainPlayerPointerUpper.id,
            ConstraintSet.BOTTOM,binding.mainPlayerWins.id,
            ConstraintSet.TOP,0)
        set.connect(binding.mainPlayerPointerUpper.id,
            ConstraintSet.LEFT,binding.mainPlayerWins.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerPointerUpper.id,
            ConstraintSet.RIGHT,binding.mainPlayerWins.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.opponentPointerUpper.id,
            ConstraintSet.BOTTOM,binding.opponentPlayerWins.id,
            ConstraintSet.TOP,0)
        set.connect(binding.opponentPointerUpper.id,
            ConstraintSet.LEFT,binding.opponentPlayerWins.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.opponentPointerUpper.id,
            ConstraintSet.RIGHT,binding.opponentPlayerWins.id,
            ConstraintSet.RIGHT,0)

        set.applyTo(binding.onlineBattleLayout)

    }

    private fun setDrawables() {
        binding.onlineBattleLayout.background = BackgroundColorDrawable(requireContext())

        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))
        binding.mainPlayerWins.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.opponentPlayerWins.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.mainPlayerName.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.opponentPlayerName.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        binding.mainPlayerPointerUpper.setImageDrawable(PointerUpperDrawable(requireContext()))
        binding.opponentPointerUpper.setImageDrawable(PointerUpperDrawable(requireContext()))
        
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

        binding.mainPlayerWins.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit*0.8f)
        binding.opponentPlayerWins.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit*0.8f)

        binding.mainPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit).toFloat())
        binding.opponentPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit).toFloat())

        binding.mainPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,
            ActionBar.LayoutParams.WRAP_CONTENT)
        binding.opponentPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,
            ActionBar.LayoutParams.WRAP_CONTENT)

        binding.mainPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            binding.mainPlayerName.setAutoSizeTextTypeUniformWithConfiguration(
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
        }

        binding.mainPlayerPointerUpper.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentPointerUpper.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
    }

}