package com.tt.ox.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.ox.OXApplication
import com.tt.ox.adapters.ChooseOpponentAdapter
import com.tt.ox.database.Opponent
import com.tt.ox.databinding.FragmentChooseOpponentBinding
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory


class ChooseOpponentFragment : Fragment() {

    private var _binding: FragmentChooseOpponentBinding? = null
    private val binding get() = _binding!!

    private var unit = 0

    private var state: Parcelable? = null

    private lateinit var adapter: ChooseOpponentAdapter

    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
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

        adapter = ChooseOpponentAdapter(unit,
            {
                gameViewModel.deleteOpponent(it)
            }){
            val action = ChooseOpponentFragmentDirections.actionChooseOpponentFragmentToMultiPlayerFragment(it.id)
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
            state = binding.recyclerView.layoutManager?.onSaveInstanceState()
                adapter.submitList(list)
                {
                    binding.recyclerView.layoutManager?.onRestoreInstanceState(state)
                }
        }
        }
    }

    private fun addNewOpponent() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("Add Player")

        val inputName = EditText(requireContext())
        inputName.inputType = InputType.TYPE_CLASS_TEXT
        alertDialog.setView(inputName)

        alertDialog.setNegativeButton(
            "CANCEL"
        ) { dialogInterface, _ ->
            dialogInterface.cancel()
        }

        alertDialog.setPositiveButton("SAVE"){
                _, _ ->
                gameViewModel.addNewOpponent(inputName.text.toString())
        }

        alertDialog.show()
    }

    private fun prepareUI(){
        setSizes()
        setDrawables()
        setConstraint()

    }

    private fun click() {
        binding.addOpponent.setOnClickListener {
            addNewOpponent()
        }
        binding.deleteOpponent.setOnClickListener {
            state = binding.recyclerView.layoutManager?.onSaveInstanceState()
            adapter.delete()
            binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager?.onRestoreInstanceState(state)

        }
    }

    private fun setConstraint() {
        val set = ConstraintSet()
        set.clone(binding.chooseOpponentFragmentLayout)

        set.connect(binding.topBar.id,ConstraintSet.LEFT,binding.chooseOpponentFragmentLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.topBar.id,ConstraintSet.RIGHT,binding.chooseOpponentFragmentLayout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.topBar.id,ConstraintSet.TOP,binding.chooseOpponentFragmentLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.recyclerView.id,
            ConstraintSet.TOP,binding.topBar.id,
            ConstraintSet.BOTTOM,0)
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

    }

    private fun setSizes() {
        val topBarHeight = 3*unit
        binding.topBar.layoutParams = ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT,topBarHeight)

    }

}
