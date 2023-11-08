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
import com.tt.ox.alertDialogs.AlertDialogChangeName
import com.tt.ox.databinding.FragmentOptionsBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ButtonBackground
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
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
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
        clicks()
    }

    private fun clicks(){
        binding.userName.setOnClickListener {
            changeNameClick()
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
    }

    private fun setDrawables(){
        binding.layout.background = BackgroundColorDrawable(requireContext())
        binding.userName.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.userName.background = ButtonBackground(requireContext())
    }

    private fun setConstraints(){
        val set = ConstraintSet()
        set.clone(binding.layout)

        set.connect(binding.userName.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.userName.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.userName.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,unit/2)

        set.applyTo(binding.layout)
    }

}

