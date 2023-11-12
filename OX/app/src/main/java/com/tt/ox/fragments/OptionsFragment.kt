package com.tt.ox.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import com.tt.ox.DARK_MODE_AUTO
import com.tt.ox.DARK_MODE_OFF
import com.tt.ox.DARK_MODE_ON
import com.tt.ox.alertDialogs.AlertDialogChangeName
import com.tt.ox.databinding.FragmentOptionsBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ChooserDrawable
import com.tt.ox.drawables.DarkModeChooserBackground
import com.tt.ox.drawables.DividerLine
import com.tt.ox.drawables.EditDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences
import com.tt.ox.helpers.Theme


class OptionsFragment : Fragment() {

    private var _binding:FragmentOptionsBinding? = null
    private val binding get() = _binding!!
    private var unit = 0
    private var width = 0


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
        clicks()
    }

    private fun displayTexts() {
        binding.darkModeLabel.text = "DARK MODE"
        binding.darkModeOnTv.text = "ON"
        binding.darkModeOffTv.text = "OFF"
        binding.darkModeAutoTv.text = "AUTO"
    }

    private fun clicks(){
        binding.userNameChange.setOnClickListener {
            changeNameClick()
        }
        binding.darkModeAutoSelector.setOnClickListener {
            SharedPreferences.saveDarkMode(requireContext(), DARK_MODE_AUTO)
            setDrawables()
        }
        binding.darkModeOnSelector.setOnClickListener {
            SharedPreferences.saveDarkMode(requireContext(), DARK_MODE_ON)
            setDrawables()
        }
        binding.darkModeOffSelector.setOnClickListener {
            SharedPreferences.saveDarkMode(requireContext(), DARK_MODE_OFF)
            setDrawables()
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
                alertDialog?.dismiss()
            },
            saveClick = {
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
        binding.darkModeLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.8f)
        binding.darkModeChooserBackground.layoutParams = ConstraintLayout.LayoutParams(width,unit)
        binding.darkModeOnTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.darkModeOffTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.darkModeAutoTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit* 0.5f)
        binding.darkModeOnSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.darkModeOffSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.darkModeAutoSelector.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
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

        set.applyTo(binding.layout)
    }

}

