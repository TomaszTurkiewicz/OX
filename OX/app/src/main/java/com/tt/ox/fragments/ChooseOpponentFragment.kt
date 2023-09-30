package com.tt.ox.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.ox.OXApplication
import com.tt.ox.R
import com.tt.ox.adapters.ChooseOpponentAdapter
import com.tt.ox.databinding.AlertDialogAddOpponentBinding
import com.tt.ox.databinding.FragmentChooseOpponentBinding
import com.tt.ox.drawables.AddDrawable
import com.tt.ox.drawables.BinDrawable
import com.tt.ox.drawables.ButtonWithTextDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory


class ChooseOpponentFragment : Fragment() {

    private var _binding: FragmentChooseOpponentBinding? = null
    private val binding get() = _binding!!
    private var unit = 0
    private var state: Parcelable? = null
    private var deletable = false
    private lateinit var adapter: ChooseOpponentAdapter
    private var width = 0
    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
    }
    override fun onPause() {
        super.onPause()
        state = binding.recyclerView.layoutManager?.onSaveInstanceState()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseOpponentBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUI()
        click()

        adapter = ChooseOpponentAdapter(requireContext(),
            {
                gameViewModel.deleteOpponentMultiPlayer(it)
            }){
            val action = ChooseOpponentFragmentDirections.actionChooseOpponentFragmentToMultiPlayerFragment(it.getId())
            findNavController().navigate(action)
        }
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)

        state?.let {
            binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
        }
        gameViewModel.listOfOpponents.observe(this.viewLifecycleOwner){
            opponent -> opponent.let {list ->
            val filteredList = list.filter{opponent -> opponent.getId() != 1 }
            state = binding.recyclerView.layoutManager?.onSaveInstanceState()
                adapter.submitList(filteredList)
                {
                    binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
                }
        }
        }
    }

    private fun addNewOpponent() {
        displayAlertDialogAddOpponent()
//        val alertDialog = AlertDialog.Builder(requireContext())
//        alertDialog.setTitle("Add Player")
//
//        val inputName = EditText(requireContext())
//        inputName.inputType = InputType.TYPE_CLASS_TEXT
//        alertDialog.setView(inputName)
//
//        alertDialog.setNegativeButton(
//            "CANCEL"
//        ) { dialogInterface, _ ->
//            dialogInterface.cancel()
//        }
//
//        alertDialog.setPositiveButton("SAVE"){
//                _, _ ->
//                gameViewModel.addNewOpponent(inputName.text.toString())
//        }
//
//        alertDialog.show()
    }

    private fun displayAlertDialogAddOpponent(){
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = AlertDialogAddOpponentBinding.inflate(layoutInflater)
        displayAlertDialogUI(alertDialog)
        builder.setView(alertDialog.root)

        val dialog = builder.create()
        dialog.setCancelable(false)

        alertDialog.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        alertDialog.saveButton.setOnClickListener {
            val string = alertDialog.inputName.text
            string?.let {
                if(it.toString().length>1){
                    gameViewModel.addNewOpponent(it.toString())
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(requireContext(),"Name too short",Toast.LENGTH_LONG).show()
                }
            }

        }

        dialog.show()

    }

    private fun displayAlertDialogUI(alertDialog: AlertDialogAddOpponentBinding) {
        alertDialog.title.text = "Add New Opponent"
        alertDialog.message.text = "Type new opponent name. Between 2 and 14 characters."

        setAlertDialogColors(alertDialog)

        setAlertDialogSizes(alertDialog)

        setAlertDialogDrawables(alertDialog)

        setAlertDialogConstraints(alertDialog)
    }

    private fun setAlertDialogDrawables(alertDialog: AlertDialogAddOpponentBinding) {
        alertDialog.saveButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),"ADD"))
        alertDialog.cancelButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),"CANCEL"))
    }

    private fun setAlertDialogConstraints(alertDialog: AlertDialogAddOpponentBinding) {
        val set = ConstraintSet()
        set.clone(alertDialog.alertDialogAddOpponentLayout)

        set.connect(alertDialog.title.id,ConstraintSet.TOP,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.TOP)
        set.connect(alertDialog.title.id,ConstraintSet.LEFT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.LEFT)
        set.connect(alertDialog.title.id,ConstraintSet.RIGHT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.RIGHT)

        set.connect(alertDialog.message.id,ConstraintSet.TOP,alertDialog.title.id,ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.message.id,ConstraintSet.LEFT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.LEFT)
        set.connect(alertDialog.message.id,ConstraintSet.RIGHT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.RIGHT)

        set.connect(alertDialog.inputName.id,ConstraintSet.TOP,alertDialog.message.id,ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.inputName.id,ConstraintSet.LEFT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.LEFT)
        set.connect(alertDialog.inputName.id,ConstraintSet.RIGHT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.RIGHT)

        set.connect(alertDialog.cancelButton.id,ConstraintSet.LEFT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.LEFT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.cancelButton.id,ConstraintSet.TOP,alertDialog.inputName.id,ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.cancelButton.id,ConstraintSet.BOTTOM,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )

        set.connect(alertDialog.saveButton.id,ConstraintSet.RIGHT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.RIGHT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.saveButton.id,ConstraintSet.TOP,alertDialog.inputName.id,ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.saveButton.id,ConstraintSet.BOTTOM,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )


        set.applyTo(alertDialog.alertDialogAddOpponentLayout)
    }

    private fun setAlertDialogSizes(alertDialog: AlertDialogAddOpponentBinding) {
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.1f)
        alertDialog.message.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.05f)

        alertDialog.message.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)
        alertDialog.inputName.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)

        alertDialog.saveButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
        alertDialog.cancelButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())



    }

    private fun setAlertDialogColors(alertDialog: AlertDialogAddOpponentBinding) {
        alertDialog.title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        alertDialog.message.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

    }

    private fun prepareUI(){
        setSizes()
        setDrawables()
        setConstraint()

    }

    private fun click() {
        binding.addOpponent.setOnClickListener {
            if(!deletable){
                addNewOpponent()
            }

        }
        binding.deleteOpponent.setOnClickListener {
            state = binding.recyclerView.layoutManager?.onSaveInstanceState()
            deletable = !deletable
            adapter.delete(deletable)
            binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
            binding.deleteOpponent.setImageDrawable(BinDrawable(requireContext(),deletable))

        }
    }

    private fun setConstraint() {
        val set = ConstraintSet()
        set.clone(binding.chooseOpponentFragmentLayout)

        set.connect(binding.addOpponent.id,ConstraintSet.LEFT,binding.chooseOpponentFragmentLayout.id,ConstraintSet.LEFT,unit/2)
        set.connect(binding.addOpponent.id,ConstraintSet.TOP,binding.chooseOpponentFragmentLayout.id,ConstraintSet.TOP,unit/2)

        set.connect(binding.deleteOpponent.id,ConstraintSet.RIGHT,binding.chooseOpponentFragmentLayout.id,ConstraintSet.RIGHT,unit/2)
        set.connect(binding.deleteOpponent.id,ConstraintSet.TOP,binding.chooseOpponentFragmentLayout.id,ConstraintSet.TOP,unit/2)

        set.connect(binding.recyclerView.id,
            ConstraintSet.TOP,binding.addOpponent.id,
            ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.recyclerView.id,
            ConstraintSet.LEFT,binding.chooseOpponentFragmentLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.recyclerView.id,
            ConstraintSet.RIGHT,binding.chooseOpponentFragmentLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.recyclerView.id,
            ConstraintSet.BOTTOM,binding.chooseOpponentFragmentLayout.id,
            ConstraintSet.BOTTOM,0)
        set.applyTo(binding.chooseOpponentFragmentLayout)
    }

    private fun setDrawables() {
        binding.addOpponent.setImageDrawable(AddDrawable(requireContext()))
        binding.deleteOpponent.setImageDrawable(BinDrawable(requireContext(),deletable))
    }

    private fun setSizes() {
        val buttonSize = unit
        binding.addOpponent.layoutParams = ConstraintLayout.LayoutParams(buttonSize,buttonSize)
        binding.deleteOpponent.layoutParams = ConstraintLayout.LayoutParams(buttonSize,buttonSize)

    }

}