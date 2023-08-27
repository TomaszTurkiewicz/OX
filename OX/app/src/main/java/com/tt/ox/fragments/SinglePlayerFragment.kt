package com.tt.ox.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.tt.ox.MAIN_PLAYER
import com.tt.ox.NOTHING
import com.tt.ox.O
import com.tt.ox.OPPONENT
import com.tt.ox.OXApplication
import com.tt.ox.R
import com.tt.ox.X
import com.tt.ox.databinding.FragmentSinglePlayerBinding
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.WinLineDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory
import kotlinx.coroutines.launch


class SinglePlayerFragment : FragmentCoroutine() {

    private var _binding:FragmentSinglePlayerBinding? = null
    private val binding get() = _binding!!
    private var unit = 0
    private var fPlay = false
    private var fMoves = false
    private var fTurn = false
    private val handler = Handler(Looper.getMainLooper())
    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSinglePlayerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameViewModel.initializeMoves(requireContext())
        gameViewModel.initialize(true)
        gameViewModel.getOpponentMultiPlayer(1).observe(this.viewLifecycleOwner){
                mainPlayer ->
            gameViewModel.initializeGame(requireContext(),mainPlayer)
            prepareUI()
            setObserves()
            click()
            handler.postDelayed(gameLoop,1000)
        }
    }

    private val gameLoop:Runnable = Runnable {
    if(fPlay) {
        if (fTurn) {
             // wait for click
        } else {
             gameViewModel.playPhone(requireContext())
         }
     }
    }

    private fun click() {

        binding.addMoves.setOnClickListener {
            gameViewModel.addMoves(requireContext())
        }

        binding.topLeftField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setTopLeft(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.topMidField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setTopMid(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.topRightField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setTopRight(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }

        binding.midLeftField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setMidLeft(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.midMidField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setMidMid(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.midRightField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setMidRight(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }

        binding.bottomLeftField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setBottomLeft(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.bottomMidField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setBottomMid(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.bottomRightField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setBottomRight(requireContext())
                handler.postDelayed(gameLoop,1000)
            }
        }

        binding.reset.setOnClickListener {
            gameViewModel.initialize(false)
            handler.postDelayed(gameLoop,1000)
        }
        binding.switchMarks.setOnClickListener {
            gameViewModel.switchMarks()
            launch {
                gameViewModel.updateOpponent(
                    gameViewModel.game.value!!.getOpponent()
                )
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(gameLoop)
    }

    private fun setObserves() {

        gameViewModel.win.observe(this.viewLifecycleOwner){
            if(it){
                gameViewModel.resetWin()
                    updateWins()

            }
        }

        gameViewModel.moves.observe(this.viewLifecycleOwner){
            binding.moves.text = it.toString()
            fMoves = it == 0
            displayUI()
        }

        gameViewModel.game.observe(this.viewLifecycleOwner){
            binding.mainPlayerName.text = it.getMainPlayerName()
            binding.opponentPlayerName.text = it.getOpponentName()
            binding.mainPlayerWins.text = it.getWins().toString()
            binding.opponentPlayerWins.text = it.getLoses().toString()
            if(it.getOpponentMark()==X){
                binding.opponentPlayerMark.setImageDrawable(XDrawable(requireContext()))
            } else{
                binding.opponentPlayerMark.setImageDrawable(ODrawable(requireContext()))
            }
            if(it.getMainPlayerMark()==X){
                binding.mainPlayerMark.setImageDrawable(XDrawable(requireContext()))
            } else{
                binding.mainPlayerMark.setImageDrawable(ODrawable(requireContext()))
            }
        }

        gameViewModel.topLeft.observe(this.viewLifecycleOwner){
            setMark(binding.topLeftField,it)
        }
        gameViewModel.topMid.observe(this.viewLifecycleOwner){
            setMark(binding.topMidField,it)
        }
        gameViewModel.topRight.observe(this.viewLifecycleOwner){
            setMark(binding.topRightField,it)
        }

        gameViewModel.midLeft.observe(this.viewLifecycleOwner){
            setMark(binding.midLeftField,it)
        }
        gameViewModel.midMid.observe(this.viewLifecycleOwner){
            setMark(binding.midMidField,it)
        }
        gameViewModel.midRight.observe(this.viewLifecycleOwner){
            setMark(binding.midRightField,it)
        }

        gameViewModel.bottomLeft.observe(this.viewLifecycleOwner){
            setMark(binding.bottomLeftField,it)
        }
        gameViewModel.bottomMid.observe(this.viewLifecycleOwner){
            setMark(binding.bottomMidField,it)
        }
        gameViewModel.bottomRight.observe(this.viewLifecycleOwner){
            setMark(binding.bottomRightField,it)
        }
        gameViewModel.play.observe(this.viewLifecycleOwner){
            fPlay = it
            displayUI()
        }

        gameViewModel.turn.observe(this.viewLifecycleOwner){
            fTurn = it
            if(it){
                binding.mainPlayerName.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),
                    R.color.red))
                binding.opponentPlayerName.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),
                    R.color.white))
            }else{
                binding.mainPlayerName.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),
                    R.color.white))
                binding.opponentPlayerName.setBackgroundColor(
                    ContextCompat.getColor(requireContext(),
                    R.color.red))
            }
        }

        gameViewModel.buttonSwitch.observe(this.viewLifecycleOwner){
            if(it){
                binding.switchMarks.visibility = View.VISIBLE
            }else{
                binding.switchMarks.visibility = View.GONE
            }
        }
    }

    private fun updateWins() {
        val winningPerson = gameViewModel.getWiningPerson()
        if (winningPerson == MAIN_PLAYER) {
            gameViewModel.game.value!!.addWin()
            launch {
                gameViewModel.updateOpponent(
                    gameViewModel.game.value!!.getOpponent()
                )
            }

        } else if (winningPerson == OPPONENT) {
            gameViewModel.game.value!!.addLose()
            launch {
                gameViewModel.updateOpponent(
                    gameViewModel.game.value!!.getOpponent()
                )
            }

        }
    }

    private fun displayUI(){
        if(fPlay){
            binding.winLine.setImageDrawable(null)
            binding.winLine.visibility = View.GONE
            binding.reset.visibility = View.GONE
            binding.addMoves.visibility = View.GONE
        }else{
            binding.winLine.visibility = View.VISIBLE
            binding.winLine.setImageDrawable(
                WinLineDrawable(requireContext(),
                gameViewModel.getHorizontalTop(),gameViewModel.getHorizontalMid(),
                gameViewModel.getHorizontalBottom(),gameViewModel.getVerticalLeft(),
                gameViewModel.getVerticalMid(),gameViewModel.getVerticalRight(),
                gameViewModel.getAngleUp(),gameViewModel.getAngleDown())
            )
            if(fMoves){
                binding.reset.visibility = View.GONE
                binding.addMoves.visibility = View.VISIBLE
            }else{
                binding.reset.visibility = View.VISIBLE
                binding.addMoves.visibility = View.GONE
            }
        }
    }

    private fun setMark(view: ImageView, mark:Int){
        when(mark){
            NOTHING -> view.setImageDrawable(null)
            X -> view.setImageDrawable(XDrawable(requireContext()))
            O -> view.setImageDrawable(ODrawable(requireContext()))
        }
    }

    private fun prepareUI() {
        setSizes()
        setDrawables()
        setConstraint()
    }

    private fun setSizes(){
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

        binding.reset.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)
        binding.addMoves.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)

        binding.mainPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (fieldSize/3).toFloat())
        binding.opponentPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (fieldSize/3).toFloat())

        binding.mainPlayerMark.layoutParams = ConstraintLayout.LayoutParams(fieldSize/3,fieldSize/3)
        binding.opponentPlayerMark.layoutParams = ConstraintLayout.LayoutParams(fieldSize/3,fieldSize/3)

        binding.switchMarks.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize/3)

        binding.moves.setTextSize(TypedValue.COMPLEX_UNIT_PX,unit.toFloat())
    }

    private fun setDrawables(){
        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))
    }

    private fun setConstraint() {
        val set = ConstraintSet()

        set.clone(binding.singlePlayerLayout)

        set.connect(binding.backgroundField.id,
            ConstraintSet.TOP, binding.singlePlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.BOTTOM, binding.singlePlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.LEFT, binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.RIGHT, binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.midMidField.id,
            ConstraintSet.TOP, binding.singlePlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.BOTTOM, binding.singlePlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.LEFT, binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.RIGHT, binding.singlePlayerLayout.id,
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
            ConstraintSet.TOP, binding.singlePlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.winLine.id,
            ConstraintSet.BOTTOM, binding.singlePlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.winLine.id,
            ConstraintSet.LEFT, binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.winLine.id,
            ConstraintSet.RIGHT, binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)


        set.connect(binding.reset.id,
            ConstraintSet.BOTTOM, binding.singlePlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.reset.id,
            ConstraintSet.LEFT, binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.reset.id,
            ConstraintSet.RIGHT, binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.addMoves.id,
            ConstraintSet.BOTTOM, binding.singlePlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.addMoves.id,
            ConstraintSet.LEFT, binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.addMoves.id,
            ConstraintSet.RIGHT, binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.TOP,binding.singlePlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.LEFT,binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.TOP,binding.singlePlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.RIGHT,binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerName.id,
            ConstraintSet.TOP,binding.mainPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.LEFT,binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.TOP,binding.opponentPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.RIGHT,binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.LEFT,binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.TOP,binding.mainPlayerName.id,
            ConstraintSet.BOTTOM,0)

        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.RIGHT,binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.TOP,binding.opponentPlayerName.id,
            ConstraintSet.BOTTOM,0)

        set.connect(binding.switchMarks.id,
            ConstraintSet.LEFT,binding.mainPlayerMark.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.switchMarks.id,
            ConstraintSet.TOP,binding.mainPlayerMark.id,
            ConstraintSet.TOP,0)
        set.connect(binding.switchMarks.id,
            ConstraintSet.BOTTOM,binding.mainPlayerMark.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.switchMarks.id,
            ConstraintSet.RIGHT,binding.opponentPlayerMark.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.moves.id,
            ConstraintSet.TOP,binding.singlePlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.moves.id,
            ConstraintSet.LEFT,binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.moves.id,
            ConstraintSet.RIGHT,binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.applyTo(binding.singlePlayerLayout)

    }
}