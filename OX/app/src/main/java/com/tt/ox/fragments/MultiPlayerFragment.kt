package com.tt.ox.fragments


import android.app.ActionBar
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
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.tt.ox.MAIN_PLAYER
import com.tt.ox.NOTHING
import com.tt.ox.O
import com.tt.ox.OPPONENT
import com.tt.ox.OXApplication
import com.tt.ox.R
import com.tt.ox.TEST
import com.tt.ox.X
import com.tt.ox.alertDialogs.AlertDialogAddMoves
import com.tt.ox.databinding.FragmentMultiPlayerBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.ResetButtonDrawable
import com.tt.ox.drawables.TurnDrawable
import com.tt.ox.drawables.WinLineDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.Marks
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.Theme
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
    private val resetHandler = Handler(Looper.getMainLooper())
    private val winningLineHandler = Handler(Looper.getMainLooper())
    private val navArgs: MultiPlayerFragmentArgs by navArgs()
    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }
    private var marks = Marks()
    private var id = 0
    private var addMovesDialog:AlertDialog? = null
    private var mRewardedAd : RewardedAd? = null

    private var turnPointer = 0.5
    private var turnPointerLeft = false
    private val pointerJump = 0.05
    private val pointerLeft = 0.25
    private val pointerRight = 0.75
    private val turnHandler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        id = navArgs.opponentId
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
        prepareRewardedAd()
        readMarks()
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
                displayMarks()
            }
        }
    }

    private fun prepareRewardedAd(){
        val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()
        val adId = if(TEST) getString(R.string.testRewardedAd) else getString(R.string.multiPlayerRewardedAd)
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


    private fun clicks() {

        binding.topLeftField.setOnClickListener {
            playButtonClick()
            gameViewModel.setTopLeft(requireContext(),marks)

        }
        binding.topMidField.setOnClickListener {
            playButtonClick()
            gameViewModel.setTopMid(requireContext(),marks)

        }
        binding.topRightField.setOnClickListener {
            playButtonClick()
            gameViewModel.setTopRight(requireContext(),marks)

        }

        binding.midLeftField.setOnClickListener {
            playButtonClick()
            gameViewModel.setMidLeft(requireContext(),marks)

        }
        binding.midMidField.setOnClickListener {
            playButtonClick()
            gameViewModel.setMidMid(requireContext(),marks)

        }
        binding.midRightField.setOnClickListener {
            playButtonClick()
            gameViewModel.setMidRight(requireContext(),marks)

        }

        binding.bottomLeftField.setOnClickListener {
            playButtonClick()
            gameViewModel.setBottomLeft(requireContext(),marks)

        }
        binding.bottomMidField.setOnClickListener {
            playButtonClick()
            gameViewModel.setBottomMid(requireContext(),marks)

        }
        binding.bottomRightField.setOnClickListener {
            playButtonClick()
            gameViewModel.setBottomRight(requireContext(),marks)

        }

        binding.reset.setOnClickListener {
            playButtonClick()
            gameViewModel.initialize(false)
            readMarks()
            displayMarks()
        }
    }
    private fun updateWins(){
        when (gameViewModel.getWiningPerson()) {
            MAIN_PLAYER -> {
                playWinSound()
                gameViewModel.game.value!!.addWin()
                launch {
                    gameViewModel.updateOpponent(
                        gameViewModel.game.value!!.getOpponent()
                    )
                }

            }
            OPPONENT -> {
                playLoseSound()
                gameViewModel.game.value!!.addLose()
                launch {
                    gameViewModel.updateOpponent(
                        gameViewModel.game.value!!.getOpponent()
                    )
                }
            }
            else -> {
                playDrawSound()
            }
        }
    }

    private fun readMarks(){
        marks.initialize(requireContext())
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
        }


        gameViewModel.play.observe(this.viewLifecycleOwner){
            fPlay = it
            displayUI()
        }

        gameViewModel.turn.observe(this.viewLifecycleOwner){
            if(it){
                turnPointerLeft = true
                turnHandler.removeCallbacksAndMessages(null)
                turnHandler.postDelayed(movePointer(),0)
            }else{
                turnPointerLeft = false
                turnHandler.removeCallbacksAndMessages(null)
                turnHandler.postDelayed(movePointer(),0)
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

    private fun displayUI(){
        if(fPlay){
            binding.winLine.setImageDrawable(null)
            binding.winLine.visibility = View.GONE
            binding.reset.visibility = View.GONE
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
        } else {
            binding.reset.visibility = View.VISIBLE
        }
    }

    private fun displayAddMoves(){
        if(addMovesDialog==null) {
            addMovesDialog = AlertDialogAddMoves(
                requireContext(),
                layoutInflater,
                {
                    playButtonClick()
                    addMovesDialog?.dismiss()
                    addMovesDialog = null
                    findNavController().navigateUp()
                }
            ) {
                playButtonClick()
                if(mRewardedAd!=null){
                    addMovesDialog?.dismiss()
                    addMovesDialog = null
                    showAdvertReward()
                }else{
                    Toast.makeText(requireContext(),"POOR INTERNET", Toast.LENGTH_SHORT).show()
                }
            }.create()
            addMovesDialog?.show()
        }
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

    private fun setDrawables(){
        binding.multiPlayerLayout.background = BackgroundColorDrawable(requireContext())
        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))
        binding.moves.setTextColor(ContextCompat.getColor(requireContext(),Theme(requireContext()).getAccentColor()))
        binding.mainPlayerWins.setTextColor(ContextCompat.getColor(requireContext(),Theme(requireContext()).getAccentColor()))
        binding.opponentPlayerWins.setTextColor(ContextCompat.getColor(requireContext(),Theme(requireContext()).getAccentColor()))
        binding.mainPlayerName.setTextColor(ContextCompat.getColor(requireContext(),Theme(requireContext()).getAccentColor()))
        binding.opponentPlayerName.setTextColor(ContextCompat.getColor(requireContext(),Theme(requireContext()).getAccentColor()))
        binding.reset.setImageDrawable(ResetButtonDrawable(requireContext()))
        binding.reset.background = ButtonBackground(requireContext())
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
        binding.turn.layoutParams = ConstraintLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,4*unit)
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
            ConstraintSet.BOTTOM,unit)
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
            ConstraintSet.BOTTOM,unit)
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

        set.connect(binding.turn.id,ConstraintSet.TOP,binding.mainPlayerWins.id,ConstraintSet.TOP,0)
        set.connect(binding.turn.id,ConstraintSet.BOTTOM,binding.mainPlayerMark.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.reset.id,
            ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.reset.id,
            ConstraintSet.LEFT, binding.multiPlayerLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.reset.id,
            ConstraintSet.RIGHT, binding.multiPlayerLayout.id,
            ConstraintSet.RIGHT,0)

        set.applyTo(binding.multiPlayerLayout)

    }


}