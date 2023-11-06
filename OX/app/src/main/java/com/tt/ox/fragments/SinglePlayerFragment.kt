package com.tt.ox.fragments

import android.app.ActionBar.LayoutParams
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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.tt.ox.EASY_GAME
import com.tt.ox.HARD_GAME
import com.tt.ox.MAIN_PLAYER
import com.tt.ox.NORMAL_GAME
import com.tt.ox.NOTHING
import com.tt.ox.O
import com.tt.ox.OPPONENT
import com.tt.ox.OXApplication
import com.tt.ox.R
import com.tt.ox.TEST
import com.tt.ox.X
import com.tt.ox.databinding.FragmentSinglePlayerBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.ResetButtonDrawable
import com.tt.ox.drawables.TurnDrawable
import com.tt.ox.drawables.WinLineDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.AlertDialogAddMoves
import com.tt.ox.helpers.Marks
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch


class SinglePlayerFragment : FragmentCoroutine() {

    private var _binding:FragmentSinglePlayerBinding? = null
    private val binding get() = _binding!!
    private var unit = 0
    private var width = 0
    private var fPlay = false
    private var fMoves = false
    private var fTurn = false
//    private var fSwitch = false
    private val handler = Handler(Looper.getMainLooper())
    private val resetHandler = Handler(Looper.getMainLooper())
    private val winningLineHandler = Handler(Looper.getMainLooper())
    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }

//    private val _marks = MutableLiveData<Marks>()
//    private var marks:LiveData<Marks> = _marks

    private var marks = Marks()

    private var mode = EASY_GAME
    private var addMovesDialog:AlertDialog? = null

    private var mRewardedAd : RewardedAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
        prepareRewardedAd()
        readMarks()
//        _marks.value!!.initialize(requireContext())
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
            displayMarks()
            handler.postDelayed(gameLoop,1000)
        }
    }

    private val gameLoop:Runnable = Runnable {
    if(fPlay) {
        if (fTurn) {
             // wait for click
        } else {
             gameViewModel.playPhone(requireContext(),mode,marks)
         }
     }
    }

    private val resetLoop:Runnable = Runnable {
        resetHandler.removeCallbacksAndMessages(null)
        if(fMoves){
            binding.reset.visibility = View.GONE
            if(!fPlay){
                displayAddMoves()
            }
//            else{
//                addMovesDialog?.dismiss()
//                addMovesDialog = null
//            }


//            binding.addMoves.visibility = View.VISIBLE
        }else{
            binding.reset.visibility = View.VISIBLE
//            binding.addMoves.visibility = View.GONE
        }
    }

    private val showWinningLine:Runnable = Runnable {
        winningLineHandler.removeCallbacksAndMessages(null)
        binding.winLine.setImageDrawable(
            WinLineDrawable(requireContext(),
                gameViewModel.getHorizontalTop(),gameViewModel.getHorizontalMid(),
                gameViewModel.getHorizontalBottom(),gameViewModel.getVerticalLeft(),
                gameViewModel.getVerticalMid(),gameViewModel.getVerticalRight(),
                gameViewModel.getAngleUp(),gameViewModel.getAngleDown())
        )
    }

    private fun click() {

//        binding.addMoves.setOnClickListener {
//            gameViewModel.addMoves(requireContext())
//            binding.addMoves.visibility = View.GONE
//        }

        binding.topLeftField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setTopLeft(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.topMidField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setTopMid(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.topRightField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setTopRight(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }

        binding.midLeftField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setMidLeft(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.midMidField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setMidMid(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.midRightField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setMidRight(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }

        binding.bottomLeftField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setBottomLeft(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.bottomMidField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setBottomMid(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }
        binding.bottomRightField.setOnClickListener {
            if(fTurn) {
                gameViewModel.setBottomRight(requireContext(),marks)
                handler.postDelayed(gameLoop,1000)
            }
        }

        binding.reset.setOnClickListener {
            gameViewModel.initialize(false)
            readMarks()
            displayMarks()
//            _marks.value!!.initialize(requireContext())
            handler.postDelayed(gameLoop,1000)
        }
//        binding.switchMarks.setOnClickListener {
//            gameViewModel.switchMarks()
//            launch {
//                gameViewModel.updateOpponent(
//                    gameViewModel.game.value!!.getOpponent()
//                )
//            }
//        }

//        binding.mainPlayerMark.setOnClickListener {
//            if(fSwitch){
//                openChangeColorAlertDialog(PLAYER_MARK_PRESSED)
//            }
//        }

//        binding.opponentPlayerMark.setOnClickListener {
//            if(fSwitch){
//                openChangeColorAlertDialog(OPPONENT_MARK_PRESSED)
//            }
//        }

    }

    private fun readMarks(){
        marks.initialize(requireContext())
    }

    private fun prepareRewardedAd(){
        val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()
        val adId = if(TEST) getString(R.string.testRewardedAd) else getString(R.string.singlePlayerRewardedAd)
        RewardedAd.load(requireContext(),adId,adRequest, object : RewardedAdLoadCallback(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                mRewardedAd = null
            }

            override fun onAdLoaded(p0: RewardedAd) {
                super.onAdLoaded(p0)
                mRewardedAd = p0
            }
        })
    }

    private fun showAdvertReward(){
        mRewardedAd?.fullScreenContentCallback = object  : FullScreenContentCallback(){
            override fun onAdClicked() {
                super.onAdClicked()
                //do nothing
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                //do nothing
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                // do nothing
            }

            override fun onAdImpression() {
                super.onAdImpression()
                // do nothing
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                prepareRewardedAd()
            }
        }

        if(mRewardedAd != null){
            mRewardedAd?.show(requireActivity()){
                gameViewModel.addMoves(requireContext())
            }
        }
    }

//    private fun openChangeColorAlertDialog(mark:Int) {
//        val opponent = gameViewModel.game.value!!.getOpponent()
//        val builder = AlertDialog.Builder(requireContext())
//        val alertDialog = AlertDialogChangeMarkColorBinding.inflate(layoutInflater)
//        val currentColor = if(mark== PLAYER_MARK_PRESSED) opponent.getMainPlayerMarkColor() else opponent.getOpponentMarkColor()
//        val colors = MarkColors(currentColor)
//
//        var leftColor = colors.getLeftColor()
//        var rightColor = colors.getRightColor()
//
//        displayAlertDialogUI(alertDialog,mark,opponent,leftColor,rightColor)
//
//        builder.setView(alertDialog.root)
//        val dialog = builder.create()
//
//        alertDialog.cancelButton.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        alertDialog.arrowLeft.setOnClickListener {
//            colors.decreasePointer()
//            val color = colors.getColor()
//            leftColor = colors.getLeftColor()
//            rightColor = colors.getRightColor()
//            if(mark== MAIN_PLAYER){
//                opponent.setPlayerColor(color)
//            }else{
//                opponent.setOpponentColor(color)
//            }
//            displayAlertDialogUI(alertDialog,mark,opponent,leftColor,rightColor)
//
//        }
//
//        alertDialog.arrowRight.setOnClickListener {
//            colors.increasePointer()
//            leftColor = colors.getLeftColor()
//            rightColor = colors.getRightColor()
//            val color = colors.getColor()
//            if(mark== MAIN_PLAYER){
//                opponent.setPlayerColor(color)
//            }else{
//                opponent.setOpponentColor(color)
//            }
//            displayAlertDialogUI(alertDialog,mark,opponent,leftColor,rightColor)
//
//        }
//
//        alertDialog.saveButton.setOnClickListener {
//            gameViewModel.setMainPlayerMarkColor(opponent.getMainPlayerMarkColor())
//            gameViewModel.setOpponentMarkColor(opponent.getOpponentMarkColor())
//            launch {
//                gameViewModel.updateOpponent(
//                    gameViewModel.game.value!!.getOpponent()
//                )
//            }
//            dialog.dismiss()
//        }
//
//        dialog.show()
//    }

//    private fun displayAlertDialogUI(alertDialog: AlertDialogChangeMarkColorBinding,mark:Int,opponent: Opponent,leftColor:Int,rightColor:Int) {
//        alertDialog.title.text = "change color"
//        alertDialog.title.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
//        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.1f)
//        val fMark = if(mark== PLAYER_MARK_PRESSED) opponent.getMainPlayerMark() else opponent.getOpponentMark()
//        val color = if(mark== PLAYER_MARK_PRESSED) opponent.getMainPlayerMarkColor() else opponent.getOpponentMarkColor()
//        val markSize = 4*unit
//        alertDialog.imageView.layoutParams = ConstraintLayout.LayoutParams(markSize,markSize)
//        alertDialog.imageView.setImageDrawable(if(fMark==X) XDrawable(requireContext(),color,false) else ODrawable(requireContext(),color,false))
//        alertDialog.arrowLeft.layoutParams = ConstraintLayout.LayoutParams(2*unit,2*unit)
//        alertDialog.arrowRight.layoutParams = ConstraintLayout.LayoutParams(2*unit,2*unit)
//        alertDialog.arrowLeft.setImageDrawable(LeftArrowDrawable(requireContext(),leftColor))
//        alertDialog.arrowRight.setImageDrawable(RightArrowDrawable(requireContext(),rightColor))
//
//        alertDialog.saveButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
//        alertDialog.cancelButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
//
//        alertDialog.saveButton.background = ButtonBackground(requireContext())
//        alertDialog.cancelButton.background = ButtonBackground(requireContext())
//        alertDialog.arrowRight.background = ButtonBackground(requireContext())
//        alertDialog.arrowLeft.background = ButtonBackground(requireContext())
//
//        alertDialog.saveButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),"SAVE"))
//        alertDialog.cancelButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),"CANCEL"))
//
//        setAlertDialogConstraints(alertDialog)
//
//    }

//    private fun setAlertDialogConstraints(alertDialog: AlertDialogChangeMarkColorBinding) {
//        val set = ConstraintSet()
//        set.clone(alertDialog.layout)
//
//        set.connect(alertDialog.title.id, ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
//        set.connect(alertDialog.title.id, ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
//        set.connect(alertDialog.title.id, ConstraintSet.TOP,alertDialog.layout.id,ConstraintSet.TOP,0)
//
//        set.connect(alertDialog.imageView.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
//        set.connect(alertDialog.imageView.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
//        set.connect(alertDialog.imageView.id,ConstraintSet.TOP,alertDialog.title.id,ConstraintSet.BOTTOM,0)
//
//        set.connect(alertDialog.arrowLeft.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
//        set.connect(alertDialog.arrowLeft.id,ConstraintSet.RIGHT,alertDialog.middleDivider.id,ConstraintSet.LEFT,0)
//        set.connect(alertDialog.arrowLeft.id,ConstraintSet.TOP,alertDialog.imageView.id,ConstraintSet.BOTTOM,0)
//
//        set.connect(alertDialog.arrowRight.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
//        set.connect(alertDialog.arrowRight.id,ConstraintSet.LEFT,alertDialog.middleDivider.id,ConstraintSet.RIGHT,0)
//        set.connect(alertDialog.arrowRight.id,ConstraintSet.TOP,alertDialog.imageView.id,ConstraintSet.BOTTOM,0)
//
//        set.connect(alertDialog.cancelButton.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
//        set.connect(alertDialog.cancelButton.id,ConstraintSet.RIGHT,alertDialog.middleDivider.id,ConstraintSet.LEFT,0)
//        set.connect(alertDialog.cancelButton.id,ConstraintSet.TOP,alertDialog.arrowLeft.id,ConstraintSet.BOTTOM,unit)
//        set.connect(alertDialog.cancelButton.id,ConstraintSet.BOTTOM,alertDialog.layout.id,ConstraintSet.BOTTOM,
//            (width*0.1).toInt()
//        )
//
//        set.connect(alertDialog.saveButton.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
//        set.connect(alertDialog.saveButton.id,ConstraintSet.LEFT,alertDialog.middleDivider.id,ConstraintSet.RIGHT,0)
//        set.connect(alertDialog.saveButton.id,ConstraintSet.TOP,alertDialog.arrowRight.id,ConstraintSet.BOTTOM,unit)
//        set.connect(alertDialog.saveButton.id,ConstraintSet.BOTTOM,alertDialog.layout.id,ConstraintSet.BOTTOM,
//            (width*0.1).toInt()
//        )
//
//        set.applyTo(alertDialog.layout)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(gameLoop)
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
                    updateWins()

            }
        }

        gameViewModel.moves.observe(this.viewLifecycleOwner){
            binding.moves.text = it.toString()
            fMoves = it == 0
            displayUI()
//            displayAddMoves()

        }

        gameViewModel.game.observe(this.viewLifecycleOwner){
            binding.mainPlayerName.text = it.getMainPlayerName()
            binding.opponentPlayerName.text = it.getOpponentName()
            binding.mainPlayerWins.text = it.getWins().toString()
            binding.opponentPlayerWins.text = it.getLoses().toString()
//            if(it.getMarks().opponentMark==X){
//                binding.opponentPlayerMark.setImageDrawable(XDrawable(requireContext(),it.getMarks().opponentColor,true))
//            } else{
//                binding.opponentPlayerMark.setImageDrawable(ODrawable(requireContext(),it.getMarks().opponentColor,true))
//            }
//            if(it.getMarks().playerMark==X){
//                binding.mainPlayerMark.setImageDrawable(XDrawable(requireContext(),it.getMarks().playerColor,true))
//            } else{
//                binding.mainPlayerMark.setImageDrawable(ODrawable(requireContext(),it.getMarks().playerColor,true))
//            }

            val dif = it.getWins()-it.getLoses()

            mode = if(dif<10){
                EASY_GAME
            } else if(dif>20){
                HARD_GAME
            }else{
                NORMAL_GAME
            }
        }

//        marks.observe(this.viewLifecycleOwner){
//            if(it.opponentMark==X){
//                binding.opponentPlayerMark.setImageDrawable(XDrawable(requireContext(),it.opponentColor,true))
//            } else{
//                binding.opponentPlayerMark.setImageDrawable(ODrawable(requireContext(),it.opponentColor,true))
//            }
//            if(it.playerMark==X){
//                binding.mainPlayerMark.setImageDrawable(XDrawable(requireContext(),it.playerColor,true))
//            } else{
//                binding.mainPlayerMark.setImageDrawable(ODrawable(requireContext(),it.playerColor,true))
//            }
//        }

        gameViewModel.play.observe(this.viewLifecycleOwner){
            fPlay = it
//            if(!it and(fMoves)){
//                displayAddMoves()
//            }else{
//                addMovesDialog?.dismiss()
//                addMovesDialog = null
//            }
            displayUI()
        }

        gameViewModel.turn.observe(this.viewLifecycleOwner){
            fTurn = it
            if(it){
                binding.turn.setImageDrawable(TurnDrawable(requireContext(),0.25))
            }else{
                binding.turn.setImageDrawable(TurnDrawable(requireContext(),0.75))
            }
        }

//        gameViewModel.buttonSwitch.observe(this.viewLifecycleOwner){
//            fSwitch = it
//            if(it){
//                binding.switchMarks.visibility = View.VISIBLE
//            }else{
//                binding.switchMarks.visibility = View.GONE
//            }
//        }
    }

    private fun displayMarks(){
            if(marks.opponentMark==X){
                binding.opponentPlayerMark.setImageDrawable(XDrawable(requireContext(),marks.opponentColor,true))
            } else{
                binding.opponentPlayerMark.setImageDrawable(ODrawable(requireContext(),marks.opponentColor,true))
            }
            if(marks.playerMark==X){
                binding.mainPlayerMark.setImageDrawable(XDrawable(requireContext(),marks.playerColor,true))
            } else{
                binding.mainPlayerMark.setImageDrawable(ODrawable(requireContext(),marks.playerColor,true))
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
                if(mRewardedAd!=null){
                    addMovesDialog?.dismiss()
                    addMovesDialog = null
                    showAdvertReward()
                }else{
                    Toast.makeText(requireContext(),"POOR INTERNET", Toast.LENGTH_SHORT).show()
                }
//                gameViewModel.addMoves(requireContext())
            }.create()
            addMovesDialog?.show()
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



    private fun setMark(view: ImageView, mark:Int){
        val color = if(mark == marks.playerMark) marks.playerColor else marks.opponentColor

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

        binding.turn.layoutParams = ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT,4*unit)
        binding.mainPlayerWins.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
        binding.opponentPlayerWins.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
        binding.mainPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
        binding.opponentPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
        binding.mainPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.moves.setTextSize(TypedValue.COMPLEX_UNIT_PX,unit*0.9f)

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

    private fun setDrawables(){
        binding.singlePlayerLayout.background = BackgroundColorDrawable(requireContext())
        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))
        binding.moves.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.mainPlayerWins.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.opponentPlayerWins.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.mainPlayerName.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.opponentPlayerName.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
        binding.reset.setImageDrawable(ResetButtonDrawable(requireContext()))
        binding.reset.background = ButtonBackground(requireContext())
    }

    private fun setConstraint() {
        val set = ConstraintSet()

        set.clone(binding.singlePlayerLayout)

        set.connect(binding.backgroundField.id,
            ConstraintSet.TOP, binding.singlePlayerLayout.id,
            ConstraintSet.TOP,3*unit)
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
            ConstraintSet.TOP,binding.singlePlayerLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.moves.id,
            ConstraintSet.LEFT,binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.moves.id,
            ConstraintSet.RIGHT,binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.divider.id,ConstraintSet.TOP,binding.moves.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.divider.id,ConstraintSet.LEFT,binding.singlePlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.divider.id,ConstraintSet.RIGHT,binding.singlePlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.TOP,binding.moves.id,
            ConstraintSet.BOTTOM,unit)
        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.LEFT,binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerWins.id,ConstraintSet.RIGHT,
            binding.divider.id,ConstraintSet.LEFT,0)

        set.connect(binding.mainPlayerName.id,
            ConstraintSet.TOP,binding.mainPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.LEFT,binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.RIGHT,binding.divider.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.LEFT,binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.TOP,binding.mainPlayerName.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.RIGHT,binding.divider.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.TOP,binding.moves.id,
            ConstraintSet.BOTTOM,unit)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.RIGHT,binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,unit)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,unit)

        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.TOP,binding.opponentPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.RIGHT,binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.RIGHT,binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.TOP,binding.opponentPlayerName.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.turn.id,ConstraintSet.TOP,binding.mainPlayerWins.id,ConstraintSet.TOP,0)
        set.connect(binding.turn.id,ConstraintSet.BOTTOM,binding.mainPlayerMark.id,ConstraintSet.BOTTOM,0)

//        set.connect(binding.switchMarks.id,
//            ConstraintSet.LEFT,binding.mainPlayerMark.id,
//            ConstraintSet.RIGHT,0)
//        set.connect(binding.switchMarks.id,
//            ConstraintSet.TOP,binding.mainPlayerMark.id,
//            ConstraintSet.TOP,0)
//        set.connect(binding.switchMarks.id,
//            ConstraintSet.BOTTOM,binding.mainPlayerMark.id,
//            ConstraintSet.BOTTOM,0)
//        set.connect(binding.switchMarks.id,
//            ConstraintSet.RIGHT,binding.opponentPlayerMark.id,
//            ConstraintSet.LEFT,0)



        set.connect(binding.reset.id,
            ConstraintSet.BOTTOM, binding.singlePlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.reset.id,
            ConstraintSet.LEFT, binding.singlePlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.reset.id,
            ConstraintSet.RIGHT, binding.singlePlayerLayout.id,
            ConstraintSet.RIGHT,0)

//        set.connect(binding.addMoves.id,
//            ConstraintSet.BOTTOM, binding.singlePlayerLayout.id,
//            ConstraintSet.BOTTOM,0)
//        set.connect(binding.addMoves.id,
//            ConstraintSet.LEFT, binding.singlePlayerLayout.id,
//            ConstraintSet.LEFT,0)
//        set.connect(binding.addMoves.id,
//            ConstraintSet.RIGHT, binding.singlePlayerLayout.id,
//            ConstraintSet.RIGHT,0)

        set.applyTo(binding.singlePlayerLayout)

    }
}