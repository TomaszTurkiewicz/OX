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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.activityViewModels
import com.tt.ox.DARK_MODE_AUTO
import com.tt.ox.DARK_MODE_OFF
import com.tt.ox.DARK_MODE_ON
import com.tt.ox.OXApplication
import com.tt.ox.X
import com.tt.ox.alertDialogs.AlertDialogChangeName
import com.tt.ox.alertDialogs.AlertDialogResetStats
import com.tt.ox.databinding.FragmentOptionsBinding
import com.tt.ox.drawables.ArrowLeftDrawable
import com.tt.ox.drawables.ArrowRightDrawable
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ChooserBackground
import com.tt.ox.drawables.ChooserDrawable
import com.tt.ox.drawables.DarkModeChooserBackground
import com.tt.ox.drawables.DividerLine
import com.tt.ox.drawables.EditDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.SwapMarksDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.Marks
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences
import com.tt.ox.helpers.Theme
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch


class OptionsFragment : FragmentCoroutine() {

    private var _binding:FragmentOptionsBinding? = null
    private val binding get() = _binding!!
    private var unit = 0
    private var width = 0
    private val marks = Marks()
    private var random = false
    private val displayMarksHandler = Handler(Looper.getMainLooper())
    private var controlsEnable = false

    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }

    private var alertDialogReset:AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        width = ScreenMetricsCompat().getWindowWidth(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUI()
        displayUserName()
        displayTexts()
        displayMarksSelection()
        displaySoundsSelectors()
        clicks()
        initializeStatistics()
    }

    private fun initializeStatistics() {
        gameViewModel.getOpponentMultiPlayer(1).observe(this.viewLifecycleOwner){
            player ->
            run {
                gameViewModel.initializeGame(requireContext(),player)
                binding.winsValue.text = player.getWins().toString()
                binding.losesValue.text = player.getLoses().toString()
                binding.clearStats.setOnClickListener {
                    //todo
                    if(alertDialogReset==null){
                        alertDialogReset = AlertDialogResetStats(
                            requireContext(),
                            layoutInflater,
                            {
                                playButtonClick()
                                alertDialogReset?.dismiss()
                                alertDialogReset = null
                            },
                            {
                                playButtonClick()
                                gameViewModel.game.value!!.resetStats()
                                launch {
                                    gameViewModel.updateOpponent(
                                        gameViewModel.game.value!!.getOpponent()
                                    )
                                }
                                alertDialogReset?.dismiss()
                                alertDialogReset = null
                            }
                        ).create()
                        alertDialogReset?.show()
                    }
                }
            }
        }
    }

    private fun displaySoundsSelectors(){
        binding.buttonSoundSelector.setImageDrawable(null)
        binding.effectsSoundSelector.setImageDrawable(null)
        val buttonSound = SharedPreferences.readButtonSound(requireContext())
        if(buttonSound){
            binding.buttonSoundSelector.setImageDrawable(ChooserDrawable(requireContext()))
        }
        val effectsSound = SharedPreferences.readEffectsSound(requireContext())
        if(effectsSound){
            binding.effectsSoundSelector.setImageDrawable(ChooserDrawable(requireContext()))
        }
    }

    private fun displayMarksSelection() {
        random = SharedPreferences.readRandomMarks(requireContext())
        binding.marksCustomSelector.setImageDrawable(null)
        binding.marksRandomSelector.setImageDrawable(null)
        if(random){
            binding.marksRandomSelector.setImageDrawable(ChooserDrawable(requireContext()))
        }else{
            binding.marksCustomSelector.setImageDrawable(ChooserDrawable(requireContext()))
        }
    }

    private fun displayControls(){
        controlsEnable = !SharedPreferences.readRandomMarks(requireContext())
        binding.swapMarks.background = ButtonBackground(requireContext(),controlsEnable)
        binding.playerLeftArrow.background = ButtonBackground(requireContext(),controlsEnable)
        binding.playerRightArrow.background = ButtonBackground(requireContext(),controlsEnable)
        binding.opponentLeftArrow.background = ButtonBackground(requireContext(),controlsEnable)
        binding.opponentRightArrow.background = ButtonBackground(requireContext(),controlsEnable)
        binding.swapMarks.setImageDrawable(SwapMarksDrawable(requireContext(),controlsEnable))
        binding.playerLeftArrow.setImageDrawable(ArrowLeftDrawable(requireContext(),controlsEnable))
        binding.opponentLeftArrow.setImageDrawable(ArrowLeftDrawable(requireContext(),controlsEnable))
        binding.playerRightArrow.setImageDrawable(ArrowRightDrawable(requireContext(),controlsEnable))
        binding.opponentRightArrow.setImageDrawable(ArrowRightDrawable(requireContext(),controlsEnable))
    }

    private fun displayTexts() {
        binding.darkModeLabel.text = "DARK MODE"
        binding.darkModeOnTv.text = "ON"
        binding.darkModeOffTv.text = "OFF"
        binding.darkModeAutoTv.text = "AUTO"
        binding.marksLabel.text = "MARKS"
        binding.marksRandomTv.text = "RANDOM"
        binding.marksCustomTv.text = "CUSTOM"
        binding.vsTv.text = "VS"
        binding.soundsLabel.text = "SOUNDS"
        binding.buttonSoundTv.text = "BUTTONS"
        binding.effectsSoundTv.text = "EFFECTS"
        binding.statisticsLabel.text = "STATISTICS"
        binding.winsText.text = "WINS"
        binding.losesText.text = "LOSES"
        binding.clearStats.text = "CLEAR"
    }

    private fun clicks(){
        binding.userNameChange.setOnClickListener {
            playButtonClick()
            changeNameClick()
        }
        binding.darkModeAutoSelector.setOnClickListener {
            playButtonClick()
            SharedPreferences.saveDarkMode(requireContext(), DARK_MODE_AUTO)
            setDrawables()
        }
        binding.darkModeOnSelector.setOnClickListener {
            playButtonClick()
            SharedPreferences.saveDarkMode(requireContext(), DARK_MODE_ON)
            setDrawables()
        }
        binding.darkModeOffSelector.setOnClickListener {
            playButtonClick()
            SharedPreferences.saveDarkMode(requireContext(), DARK_MODE_OFF)
            setDrawables()
        }
        binding.marksRandomSelector.setOnClickListener {
            playButtonClick()
            SharedPreferences.saveRandomMarks(requireContext(),true)
            displayMarksSelection()
            displayMarks()
            displayControls()
        }
        binding.marksCustomSelector.setOnClickListener {
            playButtonClick()
            SharedPreferences.saveRandomMarks(requireContext(),false)
            displayMarksSelection()
            displayMarks()
            displayControls()
        }

        binding.swapMarks.setOnClickListener {
            if(controlsEnable){
                playButtonClick()
                marks.swapMarks(requireContext())
                displayMarks()
            }
        }

        binding.playerLeftArrow.setOnClickListener {
            if(controlsEnable){
                playButtonClick()
                marks.decreasePlayerColor(requireContext())
                displayMarks()
            }
        }

        binding.playerRightArrow.setOnClickListener {
            if(controlsEnable){
                playButtonClick()
                marks.increasePlayerColor(requireContext())
                displayMarks()
            }
        }

        binding.opponentLeftArrow.setOnClickListener {
            if(controlsEnable){
                playButtonClick()
                marks.decreaseOpponentColor(requireContext())
                displayMarks()
            }
        }

        binding.opponentRightArrow.setOnClickListener {
            if(controlsEnable){
                playButtonClick()
                marks.increaseOpponentColor(requireContext())
                displayMarks()
            }
        }
        binding.buttonSoundSelector.setOnClickListener {
            playInvertedButtonClick()
            val sound = SharedPreferences.readButtonSound(requireContext())
            SharedPreferences.saveButtonSound(requireContext(),!sound)
            displaySoundsSelectors()
        }
        binding.effectsSoundSelector.setOnClickListener {
            playButtonClick()
            playInvertedWinSound()
            val sound = SharedPreferences.readEffectsSound(requireContext())
            SharedPreferences.saveEffectsSound(requireContext(),!sound)
            displaySoundsSelectors()
        }
    }

    private fun changeNameClick() {
        var alertDialog:AlertDialog? = null
        alertDialog = AlertDialogChangeName(
            requireContext(),
            layoutInflater,
            cancelButtonEnable = true,
            readNameFromMemory = true,
            title = "Change Your name",
            message = "Change your name here. Between 2 and 14 characters",
            dismissClick = {
                playButtonClick()
                alertDialog?.dismiss()
            },
            saveClick = {
                playButtonClick()
                SharedPreferences.saveMainPlayer(requireContext(),it)
                displayUserName()
                alertDialog?.dismiss()
            }
        ).create()
        alertDialog.show()
    }


    private fun displayUserName(){
        val name = SharedPreferences.readPlayerName(requireContext())
        binding.userName.text = name
    }

    private fun prepareUI(){
        setSizes()
        setDrawables()
        setConstraints()
        displayControls()
    }
    private fun setSizes(){
        binding.userName.layoutParams = ConstraintLayout.LayoutParams((width*0.8).toInt(),unit)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            binding.userName.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else{
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                binding.userName,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        binding.userNameChange.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.userNameDividerLine.layoutParams = ConstraintLayout.LayoutParams(width,(unit*0.05).toInt())
        binding.darkModeDividerLine.layoutParams = ConstraintLayout.LayoutParams(width,(unit*0.05).toInt())
        binding.darkModeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.8f)
        binding.darkModeChooserBackground.layoutParams = ConstraintLayout.LayoutParams(width,unit)
        binding.darkModeOnTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.darkModeOffTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.darkModeAutoTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.darkModeOnSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.darkModeOffSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.darkModeAutoSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.marksLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.8f)
        binding.marksRandomSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.marksCustomSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.marksRandomTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.marksCustomTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.playerMark.layoutParams = ConstraintLayout.LayoutParams(3*unit,3*unit)
        binding.opponentMark.layoutParams = ConstraintLayout.LayoutParams(3*unit,3*unit)
        binding.vsTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.swapMarks.layoutParams = ConstraintLayout.LayoutParams(unit,unit)

        binding.playerLeftArrow.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.playerRightArrow.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentLeftArrow.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentRightArrow.layoutParams = ConstraintLayout.LayoutParams(unit,unit)

        binding.marksDividerLine.layoutParams = ConstraintLayout.LayoutParams(width,(unit*0.05).toInt())
        binding.soundsLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.8f)

        binding.buttonSoundTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.effectsSoundTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.buttonSoundSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.effectsSoundSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)

        binding.soundsDividerLine.layoutParams = ConstraintLayout.LayoutParams(width,(unit*0.05).toInt())

        binding.statisticsLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.8f)

        binding.winsText.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.winsValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.losesText.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.losesValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.clearStats.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.clearStats.layoutParams = ConstraintLayout.LayoutParams(4*unit,unit)
    }

    private fun setDrawables(){
        binding.layout.background = BackgroundColorDrawable(requireContext())
        binding.userName.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.userNameChange.setImageDrawable(EditDrawable(requireContext()))
        binding.userNameChange.background = ButtonBackground(requireContext())
        binding.userNameDividerLine.setImageDrawable(DividerLine(requireContext()))
        binding.darkModeLabel.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.darkModeChooserBackground.setImageDrawable(DarkModeChooserBackground(requireContext()))
        binding.darkModeOnTv.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.darkModeOffTv.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.darkModeAutoTv.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        setDarkModeChooserDrawable()
        binding.darkModeDividerLine.setImageDrawable(DividerLine(requireContext()))
        binding.marksLabel.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.marksRandomSelector.background = ChooserBackground(requireContext())
        binding.marksCustomSelector.background = ChooserBackground(requireContext())
        binding.marksRandomTv.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.marksCustomTv.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.vsTv.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        displayMarks()
        displayControls()
        binding.marksDividerLine.setImageDrawable(DividerLine(requireContext()))
        binding.soundsLabel.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.buttonSoundSelector.background = ChooserBackground(requireContext())
        binding.effectsSoundSelector.background = ChooserBackground(requireContext())
        binding.buttonSoundTv.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.effectsSoundTv.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.soundsDividerLine.setImageDrawable(DividerLine(requireContext()))

        binding.statisticsLabel.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))

        binding.winsText.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.winsValue.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getGreenColor()))
        binding.losesText.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.losesValue.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getRedColor()))

        binding.clearStats.background = ButtonBackground(requireContext())
        binding.clearStats.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))

    }

    private fun displayMarks(){
        displayMarksHandler.removeCallbacksAndMessages(null)
        marks.initialize(requireContext())
        displayMarksDrawable()
        random = SharedPreferences.readRandomMarks(requireContext())
        if(random){
            displayMarksHandler.postDelayed(displayMarksRunnable(),1000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        displayMarksHandler.removeCallbacksAndMessages(null)
    }
    private fun displayMarksRunnable():Runnable = Runnable {
        marks.initialize(requireContext())
        displayMarksDrawable()
        displayMarksHandler.postDelayed(displayMarksRunnable(),1000)

    }

    private fun displayMarksDrawable(){
        view?.let {
            if (marks.opponentMark == X) {
                binding.opponentMark.setImageDrawable(
                    XDrawable(
                        requireContext(),
                        marks.opponentColor,
                        true
                    )
                )
            } else {
                binding.opponentMark.setImageDrawable(
                    ODrawable(
                        requireContext(),
                        marks.opponentColor,
                        true
                    )
                )
            }
            if (marks.playerMark == X) {
                binding.playerMark.setImageDrawable(
                    XDrawable(
                        requireContext(),
                        marks.playerColor,
                        true
                    )
                )
            } else {
                binding.playerMark.setImageDrawable(
                    ODrawable(
                        requireContext(),
                        marks.playerColor,
                        true
                    )
                )
            }
        }
    }

    private fun setDarkModeChooserDrawable() {
        val darkMode = SharedPreferences.readDarkMode(requireContext())
        binding.darkModeOnSelector.setImageDrawable(null)
        binding.darkModeOffSelector.setImageDrawable(null)
        binding.darkModeAutoSelector.setImageDrawable(null)

        when (darkMode){
            DARK_MODE_ON -> binding.darkModeOnSelector.setImageDrawable(ChooserDrawable(requireContext()))
            DARK_MODE_OFF -> binding.darkModeOffSelector.setImageDrawable(ChooserDrawable(requireContext()))
            else -> binding.darkModeAutoSelector.setImageDrawable(ChooserDrawable(requireContext()))
        }
    }

    private fun setConstraints(){
        val set = ConstraintSet()
        set.clone(binding.layout)

        set.connect(binding.userName.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.userName.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,2*unit)
        set.connect(binding.userName.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,unit/2)

        set.connect(binding.userNameChange.id, ConstraintSet.TOP,binding.userName.id,ConstraintSet.TOP,0)
        set.connect(binding.userNameChange.id, ConstraintSet.BOTTOM,binding.userName.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.userNameChange.id, ConstraintSet.LEFT,binding.userName.id,ConstraintSet.RIGHT,0)
        set.connect(binding.userNameChange.id, ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.userNameDividerLine.id,ConstraintSet.TOP,binding.userName.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.userNameDividerLine.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.userNameDividerLine.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.darkModeLabel.id,ConstraintSet.TOP,binding.userNameDividerLine.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.darkModeLabel.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeLabel.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.darkModeChooserBackground.id, ConstraintSet.LEFT, binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeChooserBackground.id, ConstraintSet.RIGHT, binding.layout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.darkModeChooserBackground.id, ConstraintSet.TOP, binding.darkModeLabel.id,ConstraintSet.BOTTOM,unit/2)

        set.connect(binding.darkModeAutoTv.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeAutoTv.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.darkModeAutoTv.id,ConstraintSet.TOP,binding.darkModeChooserBackground.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.darkModeOnTv.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeOnTv.id,ConstraintSet.RIGHT,binding.horizontalMiddle.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeOnTv.id,ConstraintSet.TOP,binding.darkModeChooserBackground.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.darkModeOffTv.id,ConstraintSet.LEFT,binding.horizontalMiddle.id,ConstraintSet.RIGHT,0)
        set.connect(binding.darkModeOffTv.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.darkModeOffTv.id,ConstraintSet.TOP,binding.darkModeChooserBackground.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.darkModeAutoSelector.id,ConstraintSet.LEFT,binding.darkModeChooserBackground.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeAutoSelector.id,ConstraintSet.RIGHT,binding.darkModeChooserBackground.id,ConstraintSet.RIGHT,0)
        set.connect(binding.darkModeAutoSelector.id,ConstraintSet.TOP,binding.darkModeChooserBackground.id,ConstraintSet.TOP,0)
        set.connect(binding.darkModeAutoSelector.id,ConstraintSet.BOTTOM,binding.darkModeChooserBackground.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.darkModeOffSelector.id,ConstraintSet.LEFT,binding.horizontalMiddle.id,ConstraintSet.RIGHT,0)
        set.connect(binding.darkModeOffSelector.id,ConstraintSet.RIGHT,binding.darkModeChooserBackground.id,ConstraintSet.RIGHT,0)
        set.connect(binding.darkModeOffSelector.id,ConstraintSet.TOP,binding.darkModeChooserBackground.id,ConstraintSet.TOP,0)
        set.connect(binding.darkModeOffSelector.id,ConstraintSet.BOTTOM,binding.darkModeChooserBackground.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.darkModeOnSelector.id,ConstraintSet.LEFT,binding.darkModeChooserBackground.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeOnSelector.id,ConstraintSet.RIGHT,binding.horizontalMiddle.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeOnSelector.id,ConstraintSet.TOP,binding.darkModeChooserBackground.id,ConstraintSet.TOP,0)
        set.connect(binding.darkModeOnSelector.id,ConstraintSet.BOTTOM,binding.darkModeChooserBackground.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.darkModeDividerLine.id,ConstraintSet.TOP,binding.darkModeAutoTv.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.darkModeDividerLine.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.darkModeDividerLine.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.marksLabel.id,ConstraintSet.TOP,binding.darkModeDividerLine.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.marksLabel.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.marksLabel.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.marksRandomSelector.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,unit)
        set.connect(binding.marksRandomSelector.id,ConstraintSet.TOP,binding.marksLabel.id,ConstraintSet.BOTTOM,unit/2)

        set.connect(binding.marksCustomSelector.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,unit)
        set.connect(binding.marksCustomSelector.id,ConstraintSet.TOP,binding.marksRandomSelector.id,ConstraintSet.BOTTOM,unit/2)

        set.connect(binding.marksRandomTv.id, ConstraintSet.LEFT, binding.marksRandomSelector.id, ConstraintSet.RIGHT,unit/2)
        set.connect(binding.marksRandomTv.id, ConstraintSet.TOP, binding.marksRandomSelector.id, ConstraintSet.TOP,0)
        set.connect(binding.marksRandomTv.id, ConstraintSet.BOTTOM, binding.marksRandomSelector.id, ConstraintSet.BOTTOM,0)

        set.connect(binding.marksCustomTv.id, ConstraintSet.LEFT, binding.marksCustomSelector.id, ConstraintSet.RIGHT,unit/2)
        set.connect(binding.marksCustomTv.id, ConstraintSet.TOP, binding.marksCustomSelector.id, ConstraintSet.TOP,0)
        set.connect(binding.marksCustomTv.id, ConstraintSet.BOTTOM, binding.marksCustomSelector.id, ConstraintSet.BOTTOM,0)

        set.connect(binding.playerMark.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.playerMark.id,ConstraintSet.RIGHT,binding.horizontalMiddle.id,ConstraintSet.LEFT,0)
        set.connect(binding.playerMark.id,ConstraintSet.TOP,binding.marksCustomSelector.id,ConstraintSet.BOTTOM,unit/2)

        set.connect(binding.opponentMark.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.opponentMark.id,ConstraintSet.LEFT,binding.horizontalMiddle.id,ConstraintSet.RIGHT,0)
        set.connect(binding.opponentMark.id,ConstraintSet.TOP,binding.marksCustomSelector.id,ConstraintSet.BOTTOM,unit/2)

        set.connect(binding.vsTv.id,ConstraintSet.LEFT,binding.playerMark.id,ConstraintSet.RIGHT,0)
        set.connect(binding.vsTv.id,ConstraintSet.RIGHT,binding.opponentMark.id,ConstraintSet.LEFT,0)
        set.connect(binding.vsTv.id,ConstraintSet.TOP,binding.playerMark.id,ConstraintSet.TOP,0)
        set.connect(binding.vsTv.id,ConstraintSet.BOTTOM,binding.playerMark.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.swapMarks.id,ConstraintSet.LEFT,binding.playerMark.id,ConstraintSet.RIGHT,0)
        set.connect(binding.swapMarks.id,ConstraintSet.RIGHT,binding.opponentMark.id,ConstraintSet.LEFT,0)
        set.connect(binding.swapMarks.id,ConstraintSet.TOP,binding.playerMark.id,ConstraintSet.TOP,0)

        set.connect(binding.playerLeftArrow.id,ConstraintSet.TOP,binding.playerMark.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.playerLeftArrow.id,ConstraintSet.LEFT,binding.playerMark.id,ConstraintSet.LEFT,0)

        set.connect(binding.playerRightArrow.id,ConstraintSet.TOP,binding.playerMark.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.playerRightArrow.id,ConstraintSet.RIGHT,binding.playerMark.id,ConstraintSet.RIGHT,0)

        set.connect(binding.opponentLeftArrow.id,ConstraintSet.TOP,binding.opponentMark.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentLeftArrow.id,ConstraintSet.LEFT,binding.opponentMark.id,ConstraintSet.LEFT,0)

        set.connect(binding.opponentRightArrow.id,ConstraintSet.TOP,binding.opponentMark.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentRightArrow.id,ConstraintSet.RIGHT,binding.opponentMark.id,ConstraintSet.RIGHT,0)

        set.connect(binding.marksDividerLine.id,ConstraintSet.TOP,binding.playerLeftArrow.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.marksDividerLine.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.marksDividerLine.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.soundsLabel.id,ConstraintSet.TOP,binding.marksDividerLine.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.soundsLabel.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.soundsLabel.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.buttonSoundSelector.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,unit)
        set.connect(binding.buttonSoundSelector.id,ConstraintSet.TOP,binding.soundsLabel.id,ConstraintSet.BOTTOM,unit/2)

        set.connect(binding.effectsSoundSelector.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,unit)
        set.connect(binding.effectsSoundSelector.id,ConstraintSet.TOP,binding.buttonSoundSelector.id,ConstraintSet.BOTTOM,unit/2)

        set.connect(binding.buttonSoundTv.id, ConstraintSet.LEFT, binding.buttonSoundSelector.id, ConstraintSet.RIGHT,unit/2)
        set.connect(binding.buttonSoundTv.id, ConstraintSet.TOP, binding.buttonSoundSelector.id, ConstraintSet.TOP,0)
        set.connect(binding.buttonSoundTv.id, ConstraintSet.BOTTOM, binding.buttonSoundSelector.id, ConstraintSet.BOTTOM,0)

        set.connect(binding.effectsSoundTv.id, ConstraintSet.LEFT, binding.effectsSoundSelector.id, ConstraintSet.RIGHT,unit/2)
        set.connect(binding.effectsSoundTv.id, ConstraintSet.TOP, binding.effectsSoundSelector.id, ConstraintSet.TOP,0)
        set.connect(binding.effectsSoundTv.id, ConstraintSet.BOTTOM, binding.effectsSoundSelector.id, ConstraintSet.BOTTOM,0)

        set.connect(binding.soundsDividerLine.id,ConstraintSet.TOP,binding.effectsSoundSelector.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.soundsDividerLine.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.soundsDividerLine.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.statisticsLabel.id,ConstraintSet.TOP,binding.soundsDividerLine.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.statisticsLabel.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.statisticsLabel.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.winsText.id,ConstraintSet.TOP,binding.statisticsLabel.id, ConstraintSet.BOTTOM, unit/2)
        set.connect(binding.winsText.id,ConstraintSet.LEFT,binding.layout.id, ConstraintSet.LEFT, unit)

        set.connect(binding.losesText.id,ConstraintSet.TOP,binding.winsText.id, ConstraintSet.BOTTOM, unit/2)
        set.connect(binding.losesText.id,ConstraintSet.LEFT,binding.layout.id, ConstraintSet.LEFT, unit)

        set.connect(binding.winsValue.id, ConstraintSet.BOTTOM, binding.winsText.id, ConstraintSet.BOTTOM,0)
        set.connect(binding.winsValue.id, ConstraintSet.LEFT, binding.winsText.id, ConstraintSet.RIGHT,unit/2)

        set.connect(binding.losesValue.id, ConstraintSet.BOTTOM, binding.losesText.id, ConstraintSet.BOTTOM,0)
        set.connect(binding.losesValue.id, ConstraintSet.LEFT, binding.losesText.id, ConstraintSet.RIGHT,unit/2)

        set.connect(binding.clearStats.id, ConstraintSet.LEFT,binding.layout.id, ConstraintSet.LEFT,0)
        set.connect(binding.clearStats.id, ConstraintSet.RIGHT,binding.layout.id, ConstraintSet.RIGHT,0)
        set.connect(binding.clearStats.id, ConstraintSet.TOP,binding.losesText.id, ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.clearStats.id, ConstraintSet.BOTTOM,binding.layout.id, ConstraintSet.BOTTOM,unit/2)


        set.applyTo(binding.layout)
    }

}

