package com.tt.ox.fragments


import android.app.AlertDialog
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
import com.tt.ox.database.Opponent
import com.tt.ox.databinding.AlertDialogChangeMarkColorBinding
import com.tt.ox.databinding.FragmentMultiPlayerBinding
import com.tt.ox.drawables.LeftArrowDrawable
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.RightArrowDrawable
import com.tt.ox.drawables.WinLineDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.MarkColors
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory
import kotlinx.coroutines.launch

const val PLAYER_MARK_PRESSED = 1
const val OPPONENT_MARK_PRESSED = 2

class MultiPlayerFragment : FragmentCoroutine() {

    private var _binding: FragmentMultiPlayerBinding? = null
    private val binding get() = _binding!!
    private var unit =0
    private var fPlay = false
    private var fMoves = false
    private var fSwitch = false
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

        binding.mainPlayerMark.setOnClickListener {
            if(fSwitch){
                openChangeColorAlertDialog(PLAYER_MARK_PRESSED)
            }
        }

        binding.opponentPlayerMark.setOnClickListener {
            if(fSwitch){
                openChangeColorAlertDialog(OPPONENT_MARK_PRESSED)
            }
        }

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

    private fun openChangeColorAlertDialog(mark:Int) {
        val opponent = gameViewModel.game.value!!.getOpponent()
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = AlertDialogChangeMarkColorBinding.inflate(layoutInflater)
        var pointer = 0
        val colors = MarkColors()

        displayAlertDialogUI(alertDialog,mark,opponent)
        // todo logic here

        builder.setView(alertDialog.root)
        val dialog = builder.create()

        alertDialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        alertDialog.arrowLeft.setOnClickListener {
            if(pointer==0){
                pointer = colors.colors.size-1
            }else{
                pointer-=1
            }
            val color = colors.colors[pointer]
            if(mark== MAIN_PLAYER){
                opponent.setPlayerColor(color)
            }else{
                opponent.setOpponentColor(color)
            }
            displayAlertDialogUI(alertDialog,mark,opponent)

        }

        alertDialog.arrowRight.setOnClickListener {
            pointer = (pointer+1).mod(colors.colors.size)
            val color = colors.colors[pointer]
            if(mark== MAIN_PLAYER){
                opponent.setPlayerColor(color)
            }else{
                opponent.setOpponentColor(color)
            }
            displayAlertDialogUI(alertDialog,mark,opponent)

        }

        alertDialog.saveButton.setOnClickListener {
            gameViewModel.setMainPlayerMarkColor(opponent.getMainPlayerMarkColor())
            gameViewModel.setOpponentMarkColor(opponent.getOpponentMarkColor())
            launch {
                gameViewModel.updateOpponent(
                    gameViewModel.game.value!!.getOpponent()
                )
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun displayAlertDialogUI(alertDialog: AlertDialogChangeMarkColorBinding,mark:Int,opponent: Opponent) {
        val name = if(mark== PLAYER_MARK_PRESSED) SharedPreferences.readPlayerName(requireContext()) else opponent.getName()
        alertDialog.title.text = "$name change color"
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,unit/2.toFloat())
        val fMark = if(mark== PLAYER_MARK_PRESSED) opponent.getMainPlayerMark() else opponent.getOpponentMark()
        val color = if(mark== PLAYER_MARK_PRESSED) opponent.getMainPlayerMarkColor() else opponent.getOpponentMarkColor()
        val markSize = 4*unit
        alertDialog.imageView.layoutParams = ConstraintLayout.LayoutParams(markSize,markSize)
        alertDialog.imageView.setImageDrawable(if(fMark==X) XDrawable(requireContext(),color) else ODrawable(requireContext(),color))
        alertDialog.arrowLeft.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        alertDialog.arrowRight.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        alertDialog.arrowLeft.setImageDrawable(LeftArrowDrawable(requireContext()))
        alertDialog.arrowRight.setImageDrawable(RightArrowDrawable(requireContext()))

        setAlertDialogConstraints(alertDialog)

    }


    private fun setAlertDialogConstraints(alertDialog: AlertDialogChangeMarkColorBinding) {
        val set = ConstraintSet()
        set.clone(alertDialog.layout)

        set.connect(alertDialog.title.id, ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.title.id, ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.title.id, ConstraintSet.TOP,alertDialog.layout.id,ConstraintSet.TOP,0)

        set.connect(alertDialog.imageView.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.imageView.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.imageView.id,ConstraintSet.TOP,alertDialog.title.id,ConstraintSet.BOTTOM,0)

        set.connect(alertDialog.arrowLeft.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.arrowLeft.id,ConstraintSet.RIGHT,alertDialog.middleDivider.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.arrowLeft.id,ConstraintSet.TOP,alertDialog.imageView.id,ConstraintSet.BOTTOM,0)

        set.connect(alertDialog.arrowRight.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.arrowRight.id,ConstraintSet.LEFT,alertDialog.middleDivider.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.arrowRight.id,ConstraintSet.TOP,alertDialog.imageView.id,ConstraintSet.BOTTOM,0)

        set.connect(alertDialog.cancelButton.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.cancelButton.id,ConstraintSet.RIGHT,alertDialog.middleDivider.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.cancelButton.id,ConstraintSet.TOP,alertDialog.arrowLeft.id,ConstraintSet.BOTTOM,unit)

        set.connect(alertDialog.saveButton.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.saveButton.id,ConstraintSet.LEFT,alertDialog.middleDivider.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.saveButton.id,ConstraintSet.TOP,alertDialog.arrowRight.id,ConstraintSet.BOTTOM,unit)

        set.applyTo(alertDialog.layout)
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
                    XDrawable(requireContext(),it.getOpponentMarkColor())
                }else{
                    ODrawable(requireContext(),it.getOpponentMarkColor())
                }
            )
            binding.mainPlayerMark.setImageDrawable(
                if(it.getMainPlayerMark()==X){
                    XDrawable(requireContext(),it.getMainPlayerMarkColor())
                }else{
                    ODrawable(requireContext(),it.getMainPlayerMarkColor())
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
            fSwitch = it
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
        val color = if(mark == gameViewModel.game.value!!.getMainPlayerMark()) gameViewModel.game.value!!.getMainPlayerMarkColor() else gameViewModel.game.value!!.getOpponentMarkColor()

        when(mark){
            NOTHING -> view.setImageDrawable(null)
            X -> view.setImageDrawable(XDrawable(requireContext(), color))
            O -> view.setImageDrawable(ODrawable(requireContext(), color))
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