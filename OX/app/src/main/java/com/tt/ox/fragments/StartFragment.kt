package com.tt.ox.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.tt.ox.OXApplication
import com.tt.ox.R
import com.tt.ox.database.Opponent
import com.tt.ox.database.OpponentDatabase
import com.tt.ox.databinding.FragmentStartBinding
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope


class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private var unit = 0
    private var windowHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        windowHeight = ScreenMetricsCompat().getWindowHeight(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUI()

        val playerNameSetUp = SharedPreferences.checkIfPlayerNameSetUp(requireContext())
        if(playerNameSetUp){
            clicks()
        }
        else{
            createAlertDialog()
        }

    }

    private fun createAlertDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("What's your nickname")

        val inputName = EditText(requireContext())
        inputName.inputType = InputType.TYPE_CLASS_TEXT
        alertDialog.setView(inputName)
        alertDialog.setPositiveButton("SAVE",null)
        alertDialog.setCancelable(false)
        alertDialog.create()

        var dialog = alertDialog.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            if(inputName.text.toString().trim().isNotBlank()){
                        SharedPreferences.saveMainPlayerName(requireContext(),inputName.text.toString())
                        clicks()
                dialog.dismiss()
            }
        }

//        {
//                alertDialog, _ ->
//                    if(inputName.text.toString().trim().isNotEmpty()){
//                        SharedPreferences.saveMainPlayerName(requireContext(),inputName.text.toString())
//                        clicks()
//                    }else{
//                        alertDialog.dismiss()
//                    }
//
//
//        }

    }

    private fun clicks() {
        binding.let {
            it.singlePlayerButton.setOnClickListener {
                val action = StartFragmentDirections.actionStartFragmentToSinglePlayerFragment()
                findNavController().navigate(action)
            }
            it.multiPlayerButton.setOnClickListener {
                val action = StartFragmentDirections.actionStartFragmentToChooseOpponentFragment()
                findNavController().navigate(action)
            }
            it.optionsButton.setOnClickListener {
                val action = StartFragmentDirections.actionStartFragmentToOptionsFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun prepareUI() {

        binding.multiPlayerButton.layoutParams = ConstraintLayout.LayoutParams(8*unit,3*unit)
        binding.singlePlayerButton.layoutParams = ConstraintLayout.LayoutParams(8*unit,3*unit)
        binding.optionsButton.layoutParams = ConstraintLayout.LayoutParams(2*unit,2*unit)
        setConstraints()
    }

    private fun setConstraints(){
        val offset = (windowHeight-8*unit)/3
        val set = ConstraintSet()
        set.clone(binding.fragmentStartLayout)

        set.connect(binding.optionsButton.id, ConstraintSet.TOP, binding.fragmentStartLayout.id, ConstraintSet.TOP,unit/2)
        set.connect(binding.optionsButton.id, ConstraintSet.RIGHT, binding.fragmentStartLayout.id, ConstraintSet.RIGHT,unit/2)

        set.connect(binding.singlePlayerButton.id,ConstraintSet.LEFT,binding.fragmentStartLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.singlePlayerButton.id,ConstraintSet.RIGHT,binding.fragmentStartLayout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.singlePlayerButton.id,ConstraintSet.TOP,binding.fragmentStartLayout.id,ConstraintSet.TOP,offset)

        set.connect(binding.multiPlayerButton.id,ConstraintSet.LEFT,binding.fragmentStartLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.multiPlayerButton.id,ConstraintSet.RIGHT,binding.fragmentStartLayout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.multiPlayerButton.id,ConstraintSet.TOP,binding.singlePlayerButton.id,ConstraintSet.BOTTOM,offset)

        set.applyTo(binding.fragmentStartLayout)
    }

}