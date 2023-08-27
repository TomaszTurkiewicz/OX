package com.tt.ox.fragments


import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.tt.ox.MAIN_PLAYER
import com.tt.ox.NOTHING
import com.tt.ox.O
import com.tt.ox.OPPONENT
import com.tt.ox.OXApplication
import com.tt.ox.R
import com.tt.ox.X
import com.tt.ox.databinding.FragmentMultiPlayerBinding
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.WinLineDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory
import kotlinx.coroutines.launch


class MultiPlayerFragment : FragmentCoroutine() {

    private var _binding: FragmentMultiPlayerBinding? = null
    private val binding get() = _binding!!
    private var unit =0
    private var fPlay = false
    private var fMoves = false
    private val navArgs: MultiPlayerFragmentArgs by navArgs()
    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }
    private var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        id = navArgs.opponentId
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMultiPlayerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameViewModel.initializeMoves(requireContext())
        gameViewModel.initialize(true)

        if(id>0){
            gameViewModel.getOpponentMultiPlayer(id).observe(this.viewLifecycleOwner){
                    selectedOpponent ->
                gameViewModel.initializeGame(requireContext(), selectedOpponent)
                prepareUI()
                setObserves()
                clicks()
            }
        }
    }


    private fun clicks() {

        binding.addMoves.setOnClickListener {
            gameViewModel.addMoves(requireContext())
        }

        binding.topLeftField.setOnClickListener {
            gameViewModel.setTopLeft(requireContext())

        }
        binding.topMidField.setOnClickListener {
            gameViewModel.setTopMid(requireContext())

        }
        binding.topRightField.setOnClickListener {
            gameViewModel.setTopRight(requireContext())

        }

        binding.midLeftField.setOnClickListener {
            gameViewModel.setMidLeft(requireContext())

        }
        binding.midMidField.setOnClickListener {
            gameViewModel.setMidMid(requireContext())

        }
        binding.midRightField.setOnClickListener {
            gameViewModel.setMidRight(requireContext())

        }

        binding.bottomLeftField.setOnClickListener {
            gameViewModel.setBottomLeft(requireContext())

        }
        binding.bottomMidField.setOnClickListener {
            gameViewModel.setBottomMid(requireContext())

        }
        binding.bottomRightField.setOnClickListener {
            gameViewModel.setBottomRight(requireContext())

        }

        binding.reset.setOnClickListener {
            gameViewModel.initialize(false)
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

    private fun updateWins(){
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

    private fun setObserves() {

        gameViewModel.board.topLeft.observe(this.viewLifecycleOwner){
            setMark(binding.topLeftField,it)
        }
        gameViewModel.board.topMid.observe(this.viewLifecycleOwner){
            setMark(binding.topMidField,it)
        }
        gameViewModel.board.topRight.observe(this.viewLifecycleOwner){
            setMark(binding.topRightField,it)
        }
        gameViewModel.board.midLeft.observe(this.viewLifecycleOwner){
            setMark(binding.midLeftField,it)
        }
        gameViewModel.board.midMid.observe(this.viewLifecycleOwner){
            setMark(binding.midMidField,it)
        }
        gameViewModel.board.midRight.observe(this.viewLifecycleOwner){
            setMark(binding.midRightField,it)
        }
        gameViewModel.board.bottomLeft.observe(this.viewLifecycleOwner){
            setMark(binding.bottomLeftField,it)
        }
        gameViewModel.board.bottomMid.observe(this.viewLifecycleOwner){
            setMark(binding.bottomMidField,it)
        }
        gameViewModel.board.bottomRight.observe(this.viewLifecycleOwner){
            setMark(binding.bottomRightField,it)
        }


        gameViewModel.win.observe(this.viewLifecycleOwner){
            if(it){
                gameViewModel.resetWin()
                    launch {
                        updateWins()
                    }
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
            binding.opponentPlayerMark.setImageDrawable(
                if(it.getOpponentMark()==X){
                    XDrawable(requireContext())
                }else{
                    ODrawable(requireContext())
                }
            )
            binding.mainPlayerMark.setImageDrawable(
                if(it.getMainPlayerMark()==X){
                    XDrawable(requireContext())
                }else{
                    ODrawable(requireContext())
                }
            )
        }

//        gameViewModel.topLeft.observe(this.viewLifecycleOwner){
//            setMark(binding.topLeftField,it)
//        }
//        gameViewModel.topMid.observe(this.viewLifecycleOwner){
//            setMark(binding.topMidField,it)
//        }
//        gameViewModel.topRight.observe(this.viewLifecycleOwner){
//            setMark(binding.topRightField,it)
//        }
//
//        gameViewModel.midLeft.observe(this.viewLifecycleOwner){
//            setMark(binding.midLeftField,it)
//        }
//        gameViewModel.midMid.observe(this.viewLifecycleOwner){
//            setMark(binding.midMidField,it)
//        }
//        gameViewModel.midRight.observe(this.viewLifecycleOwner){
//            setMark(binding.midRightField,it)
//        }
//
//        gameViewModel.bottomLeft.observe(this.viewLifecycleOwner){
//            setMark(binding.bottomLeftField,it)
//        }
//        gameViewModel.bottomMid.observe(this.viewLifecycleOwner){
//            setMark(binding.bottomMidField,it)
//        }
//        gameViewModel.bottomRight.observe(this.viewLifecycleOwner){
//            setMark(binding.bottomRightField,it)
//        }
        gameViewModel.play.observe(this.viewLifecycleOwner){
            fPlay = it
            displayUI()
        }

        gameViewModel.turn.observe(this.viewLifecycleOwner){
            if(it){
                binding.mainPlayerName.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.red))
                binding.opponentPlayerName.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.white))
            }else{
                binding.mainPlayerName.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.white))
                binding.opponentPlayerName.setBackgroundColor(ContextCompat.getColor(requireContext(),
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

    private fun displayUI(){
        if(fPlay){
            binding.winLine.setImageDrawable(null)
            binding.winLine.visibility = View.GONE
            binding.reset.visibility = View.GONE
            binding.addMoves.visibility = View.GONE
        }else{
            binding.winLine.visibility = View.VISIBLE
            binding.winLine.setImageDrawable(WinLineDrawable(requireContext(),
                gameViewModel.getHorizontalTop(),gameViewModel.getHorizontalMid(),
                gameViewModel.getHorizontalBottom(),gameViewModel.getVerticalLeft(),
                gameViewModel.getVerticalMid(),gameViewModel.getVerticalRight(),
                gameViewModel.getAngleUp(),gameViewModel.getAngleDown()))
            if(fMoves){
                binding.reset.visibility = View.GONE
                binding.addMoves.visibility = View.VISIBLE
            }else{
                binding.reset.visibility = View.VISIBLE
                binding.addMoves.visibility = View.GONE
            }
        }
    }

    private fun setMark(view:ImageView, mark:Int){
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

    private fun setDrawables(){
        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))
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

    private fun setConstraint() {
        val set = ConstraintSet()

        set.clone(binding.multiPlayerLayout)

        set.connect(binding.backgroundField.id,ConstraintSet.TOP, binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.backgroundField.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.backgroundField.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.backgroundField.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.midMidField.id,ConstraintSet.TOP, binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.midMidField.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.midMidField.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.midMidField.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.topMidField.id,ConstraintSet.BOTTOM,binding.midMidField.id,ConstraintSet.TOP,0)
        set.connect(binding.topMidField.id,ConstraintSet.LEFT,binding.midMidField.id,ConstraintSet.LEFT,0)
        set.connect(binding.topMidField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.RIGHT,0)

        set.connect(binding.topRightField.id,ConstraintSet.LEFT,binding.midMidField.id,ConstraintSet.RIGHT,0)
        set.connect(binding.topRightField.id,ConstraintSet.BOTTOM,binding.midMidField.id,ConstraintSet.TOP,0)

        set.connect(binding.midRightField.id,ConstraintSet.LEFT, binding.midMidField.id,ConstraintSet.RIGHT,0)
        set.connect(binding.midRightField.id,ConstraintSet.TOP, binding.midMidField.id,ConstraintSet.TOP,0)
        set.connect(binding.midRightField.id,ConstraintSet.BOTTOM, binding.midMidField.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.bottomRightField.id,ConstraintSet.LEFT,binding.midMidField.id,ConstraintSet.RIGHT,0)
        set.connect(binding.bottomRightField.id,ConstraintSet.TOP,binding.midMidField.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.bottomMidField.id,ConstraintSet.TOP,binding.midMidField.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.bottomMidField.id,ConstraintSet.LEFT,binding.midMidField.id,ConstraintSet.LEFT,0)
        set.connect(binding.bottomMidField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.RIGHT,0)

        set.connect(binding.bottomLeftField.id,ConstraintSet.TOP,binding.midMidField.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.bottomLeftField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.LEFT,0)

        set.connect(binding.midLeftField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.LEFT,0)
        set.connect(binding.midLeftField.id,ConstraintSet.TOP,binding.midMidField.id,ConstraintSet.TOP,0)
        set.connect(binding.midLeftField.id,ConstraintSet.BOTTOM,binding.midMidField.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.topLeftField.id,ConstraintSet.BOTTOM,binding.midMidField.id,ConstraintSet.TOP,0)
        set.connect(binding.topLeftField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.LEFT,0)

        set.connect(binding.winLine.id,ConstraintSet.TOP, binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.winLine.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.winLine.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.winLine.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)


        set.connect(binding.reset.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.reset.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.reset.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.addMoves.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.addMoves.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.addMoves.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.TOP,binding.multiPlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.LEFT,binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.TOP,binding.multiPlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.RIGHT,binding.multiPlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerName.id,
            ConstraintSet.TOP,binding.mainPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.LEFT,binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.TOP,binding.opponentPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.RIGHT,binding.multiPlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerMark.id,ConstraintSet.LEFT,binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerMark.id,ConstraintSet.TOP,binding.mainPlayerName.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.opponentPlayerMark.id,ConstraintSet.RIGHT,binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerMark.id,ConstraintSet.TOP,binding.opponentPlayerName.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.switchMarks.id,ConstraintSet.LEFT,binding.mainPlayerMark.id,ConstraintSet.RIGHT,0)
        set.connect(binding.switchMarks.id,ConstraintSet.TOP,binding.mainPlayerMark.id,ConstraintSet.TOP,0)
        set.connect(binding.switchMarks.id,ConstraintSet.BOTTOM,binding.mainPlayerMark.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.switchMarks.id,ConstraintSet.RIGHT,binding.opponentPlayerMark.id,ConstraintSet.LEFT,0)

        set.connect(binding.moves.id,ConstraintSet.TOP,binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.moves.id,ConstraintSet.LEFT,binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.moves.id,ConstraintSet.RIGHT,binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.applyTo(binding.multiPlayerLayout)

    }


}

// todo customize players marks