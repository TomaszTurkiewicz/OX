package com.tt.ox.fragments


import android.app.AlertDialog
import android.os.Build
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
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tt.ox.MAIN_PLAYER
import com.tt.ox.NOTHING
import com.tt.ox.O
import com.tt.ox.OPPONENT
import com.tt.ox.OPPONENT_MARK_PRESSED
import com.tt.ox.OXApplication
import com.tt.ox.PLAYER_MARK_PRESSED
import com.tt.ox.R
import com.tt.ox.X
import com.tt.ox.database.Opponent
import com.tt.ox.databinding.AlertDialogChangeMarkColorBinding
import com.tt.ox.databinding.FragmentMultiPlayerBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable
import com.tt.ox.drawables.LeftArrowDrawable
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.PointerUpperDrawable
import com.tt.ox.drawables.ResetButtonDrawable
import com.tt.ox.drawables.RightArrowDrawable
import com.tt.ox.drawables.SwitchDrawable
import com.tt.ox.drawables.WinLineDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.AlertDialogAddMoves
import com.tt.ox.helpers.MarkColors
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory
import kotlinx.coroutines.launch



class MultiPlayerFragment : FragmentCoroutine() {

    private var _binding: FragmentMultiPlayerBinding? = null
    private val binding get() = _binding!!
    private var unit =0
    private var width = 0
    private var fPlay = false
    private var fMoves = false
    private var fSwitch = false
    private val resetHandler = Handler(Looper.getMainLooper())
    private val winningLineHandler = Handler(Looper.getMainLooper())
    private val navArgs: MultiPlayerFragmentArgs by navArgs()
    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }
    private var id = 0
    private var addMovesDialog:AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        id = navArgs.opponentId
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
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

//        binding.addMoves.setOnClickListener {
//            gameViewModel.addMoves(requireContext())
//        }

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
        val currentColor = if(mark== PLAYER_MARK_PRESSED) opponent.getMainPlayerMarkColor() else opponent.getOpponentMarkColor()
        val colors = MarkColors(currentColor)
        var leftColor = colors.getLeftColor()
        var rightColor = colors.getRightColor()

        displayAlertDialogUI(alertDialog,mark,opponent,leftColor,rightColor)

        builder.setView(alertDialog.root)
        val dialog = builder.create()

        alertDialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        alertDialog.arrowLeft.setOnClickListener {
            colors.decreasePointer()
            val color = colors.getColor()
            leftColor = colors.getLeftColor()
            rightColor = colors.getRightColor()
            if(mark== MAIN_PLAYER){
                opponent.setPlayerColor(color)
            }else{
                opponent.setOpponentColor(color)
            }
            displayAlertDialogUI(alertDialog,mark,opponent,leftColor, rightColor)

        }

        alertDialog.arrowRight.setOnClickListener {
            colors.increasePointer()
            val color = colors.getColor()
            leftColor = colors.getLeftColor()
            rightColor = colors.getRightColor()
            if(mark== MAIN_PLAYER){
                opponent.setPlayerColor(color)
            }else{
                opponent.setOpponentColor(color)
            }
            displayAlertDialogUI(alertDialog,mark,opponent,leftColor, rightColor)

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

    private fun displayAlertDialogUI(alertDialog: AlertDialogChangeMarkColorBinding,mark:Int,opponent: Opponent,leftColor:Int, rightColor:Int) {
        alertDialog.title.text = "change color"
        alertDialog.title.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.1f)
        val fMark = if(mark== PLAYER_MARK_PRESSED) opponent.getMainPlayerMark() else opponent.getOpponentMark()
        val color = if(mark== PLAYER_MARK_PRESSED) opponent.getMainPlayerMarkColor() else opponent.getOpponentMarkColor()
        val markSize = 4*unit
        alertDialog.imageView.layoutParams = ConstraintLayout.LayoutParams(markSize,markSize)
        alertDialog.imageView.setImageDrawable(if(fMark==X) XDrawable(requireContext(),color,false) else ODrawable(requireContext(),color,false))
        alertDialog.arrowLeft.layoutParams = ConstraintLayout.LayoutParams(2*unit,2*unit)
        alertDialog.arrowRight.layoutParams = ConstraintLayout.LayoutParams(2*unit,2*unit)
        alertDialog.arrowLeft.setImageDrawable(LeftArrowDrawable(requireContext(),leftColor))
        alertDialog.arrowRight.setImageDrawable(RightArrowDrawable(requireContext(),rightColor))

        alertDialog.saveButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
        alertDialog.cancelButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())

        alertDialog.saveButton.background = ButtonBackground(requireContext())
        alertDialog.cancelButton.background = ButtonBackground(requireContext())
        alertDialog.arrowRight.background = ButtonBackground(requireContext())
        alertDialog.arrowLeft.background = ButtonBackground(requireContext())

        alertDialog.saveButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),"SAVE"))
        alertDialog.cancelButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),"CANCEL"))

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
        set.connect(alertDialog.cancelButton.id,ConstraintSet.BOTTOM,alertDialog.layout.id,ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )

        set.connect(alertDialog.saveButton.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.saveButton.id,ConstraintSet.LEFT,alertDialog.middleDivider.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.saveButton.id,ConstraintSet.TOP,alertDialog.arrowRight.id,ConstraintSet.BOTTOM,unit)
        set.connect(alertDialog.saveButton.id,ConstraintSet.BOTTOM,alertDialog.layout.id,ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )

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
                    XDrawable(requireContext(),it.getOpponentMarkColor(),true)
                }else{
                    ODrawable(requireContext(),it.getOpponentMarkColor(),true)
                }
            )
            binding.mainPlayerMark.setImageDrawable(
                if(it.getMainPlayerMark()==X){
                    XDrawable(requireContext(),it.getMainPlayerMarkColor(),true)
                }else{
                    ODrawable(requireContext(),it.getMainPlayerMarkColor(),true)
                }
            )
        }


        gameViewModel.play.observe(this.viewLifecycleOwner){
            fPlay = it
            displayUI()
        }

        gameViewModel.turn.observe(this.viewLifecycleOwner){
            if(it){
                binding.mainPlayerPointerUpper.visibility = View.VISIBLE
                binding.opponentPointerUpper.visibility = View.GONE
            }else{

                binding.mainPlayerPointerUpper.visibility = View.GONE
                binding.opponentPointerUpper.visibility = View.VISIBLE
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
//            binding.addMoves.visibility = View.GONE
        }else{
            binding.winLine.visibility = View.VISIBLE
            winningLineHandler.postDelayed(showWinningLine,500)
            resetHandler.postDelayed(resetLoop,1000)
        }
    }

    private val resetLoop:Runnable = kotlinx.coroutines.Runnable {
        resetHandler.removeCallbacksAndMessages(null)
        if (fMoves) {
            binding.reset.visibility = View.GONE
            if(!fPlay){
                displayAddMoves()
            }
//            binding.addMoves.visibility = View.VISIBLE
        } else {
            binding.reset.visibility = View.VISIBLE
//            binding.addMoves.visibility = View.GONE
        }
    }

    private fun displayAddMoves(){
        if(addMovesDialog==null) {
            addMovesDialog = AlertDialogAddMoves(
                requireContext(),
                layoutInflater,
                {
                    addMovesDialog?.dismiss()
                    addMovesDialog = null
                    findNavController().navigateUp()
                }
            ) {
                gameViewModel.addMoves(requireContext())
                addMovesDialog?.dismiss()
                addMovesDialog = null
            }.create()
            addMovesDialog?.show()
        }
    }

    private val showWinningLine:Runnable = kotlinx.coroutines.Runnable {
        winningLineHandler.removeCallbacksAndMessages(null)
        binding.winLine.setImageDrawable(
            WinLineDrawable(
                requireContext(),
                gameViewModel.getHorizontalTop(), gameViewModel.getHorizontalMid(),
                gameViewModel.getHorizontalBottom(), gameViewModel.getVerticalLeft(),
                gameViewModel.getVerticalMid(), gameViewModel.getVerticalRight(),
                gameViewModel.getAngleUp(), gameViewModel.getAngleDown()
            )
        )
    }

    private fun setMark(view:ImageView, mark:Int){
        val color = if(mark == gameViewModel.game.value!!.getMainPlayerMark()) gameViewModel.game.value!!.getMainPlayerMarkColor() else gameViewModel.game.value!!.getOpponentMarkColor()

        when(mark){
            NOTHING -> view.setImageDrawable(null)
            X -> view.setImageDrawable(XDrawable(requireContext(), color,false))
            O -> view.setImageDrawable(ODrawable(requireContext(), color,false))
        }
    }

    private fun prepareUI() {
        setSizes()
        setDrawables()
        setConstraint()
    }

    private fun setDrawables(){
        binding.multiPlayerLayout.background = BackgroundColorDrawable(requireContext())

        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))

        binding.moves.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.mainPlayerWins.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.opponentPlayerWins.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.mainPlayerName.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.opponentPlayerName.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))

        binding.mainPlayerPointerUpper.setImageDrawable(PointerUpperDrawable(requireContext()))
        binding.opponentPointerUpper.setImageDrawable(PointerUpperDrawable(requireContext()))

        binding.switchMarks.setImageDrawable(SwitchDrawable(requireContext()))

        binding.reset.setImageDrawable(ResetButtonDrawable(requireContext()))

//        binding.addMoves.setImageDrawable(AddMovesButton(requireContext()))

        binding.mainPlayerMark.background = ButtonBackground(requireContext())
        binding.opponentPlayerMark.background = ButtonBackground(requireContext())
        binding.switchMarks.background = ButtonBackground(requireContext())
        binding.reset.background = ButtonBackground(requireContext())
//        binding.addMoves.background = ButtonBackground(requireContext())
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
//        binding.addMoves.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)

        binding.mainPlayerWins.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit*0.8f)
        binding.opponentPlayerWins.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit*0.8f)

        binding.mainPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit).toFloat())
        binding.opponentPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit).toFloat())

        binding.mainPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit, unit)
        binding.opponentPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)

        binding.mainPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)

        binding.switchMarks.layoutParams = ConstraintLayout.LayoutParams(fieldSize,unit)

        binding.moves.setTextSize(TypedValue.COMPLEX_UNIT_PX,unit*0.9f)

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

    private fun setConstraint() {
        val set = ConstraintSet()

        set.clone(binding.multiPlayerLayout)

        set.connect(binding.backgroundField.id,
            ConstraintSet.TOP, binding.multiPlayerLayout.id,
            ConstraintSet.TOP,3*unit)
        set.connect(binding.backgroundField.id,
            ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.LEFT, binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.RIGHT, binding.multiPlayerLayout.id,
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

        set.connect(binding.moves.id,
            ConstraintSet.TOP,binding.multiPlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.moves.id,
            ConstraintSet.LEFT,binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.moves.id,
            ConstraintSet.RIGHT,binding.multiPlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.divider.id,ConstraintSet.TOP,binding.moves.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.divider.id,ConstraintSet.LEFT,binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.divider.id,ConstraintSet.RIGHT,binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.TOP,binding.moves.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.LEFT,binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerWins.id,ConstraintSet.RIGHT,
            binding.divider.id,ConstraintSet.LEFT,0)

        set.connect(binding.mainPlayerName.id,
            ConstraintSet.TOP,binding.mainPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.LEFT,binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.RIGHT,binding.divider.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.LEFT,binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.TOP,binding.mainPlayerName.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.RIGHT,binding.divider.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.TOP,binding.moves.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.RIGHT,binding.multiPlayerLayout.id,
            ConstraintSet.RIGHT,unit)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,unit)

        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.TOP,binding.opponentPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.RIGHT,binding.multiPlayerLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.RIGHT,binding.multiPlayerLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.TOP,binding.opponentPlayerName.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerPointerUpper.id,ConstraintSet.BOTTOM,binding.mainPlayerWins.id,ConstraintSet.TOP,0)
        set.connect(binding.mainPlayerPointerUpper.id,ConstraintSet.LEFT,binding.mainPlayerWins.id,ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerPointerUpper.id,ConstraintSet.RIGHT,binding.mainPlayerWins.id,ConstraintSet.RIGHT,0)

        set.connect(binding.opponentPointerUpper.id,ConstraintSet.BOTTOM,binding.opponentPlayerWins.id,ConstraintSet.TOP,0)
        set.connect(binding.opponentPointerUpper.id,ConstraintSet.LEFT,binding.opponentPlayerWins.id,ConstraintSet.LEFT,0)
        set.connect(binding.opponentPointerUpper.id,ConstraintSet.RIGHT,binding.opponentPlayerWins.id,ConstraintSet.RIGHT,0)

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



        set.connect(binding.reset.id,
            ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.reset.id,
            ConstraintSet.LEFT, binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.reset.id,
            ConstraintSet.RIGHT, binding.multiPlayerLayout.id,
            ConstraintSet.RIGHT,0)

//        set.connect(binding.addMoves.id,
//            ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,
//            ConstraintSet.BOTTOM,0)
//        set.connect(binding.addMoves.id,
//            ConstraintSet.LEFT, binding.multiPlayerLayout.id,
//            ConstraintSet.LEFT,0)
//        set.connect(binding.addMoves.id,
//            ConstraintSet.RIGHT, binding.multiPlayerLayout.id,
//            ConstraintSet.RIGHT,0)

        set.applyTo(binding.multiPlayerLayout)

    }


}