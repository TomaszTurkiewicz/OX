package com.tt.ox.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tt.ox.OXApplication
import com.tt.ox.adapters.ChooseOpponentAdapter
import com.tt.ox.alertDialogs.AlertDialogChangeName
import com.tt.ox.databinding.FragmentChooseOpponentBinding
import com.tt.ox.drawables.AddDrawable
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.BinDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory


class ChooseOpponentFragment : FragmentCoroutine() {

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
                playButtonClick()
                gameViewModel.deleteOpponentMultiPlayer(it)
            }){
            playButtonClick()
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
    }

    private fun displayAlertDialogAddOpponent(){
        var alertDialogAddOpponent:AlertDialog? = null
        alertDialogAddOpponent = AlertDialogChangeName(
            requireContext(),
            layoutInflater,
            cancelButtonEnable = true,
            readNameFromMemory = false,
            title = "Opponent name",
            message = "Type opponent name here. Between 2 and 14 characters",
            dismissClick = {
                playButtonClick()
                alertDialogAddOpponent?.dismiss()
            },
            saveClick = {
                playButtonClick()
                gameViewModel.addNewOpponent(it)
                alertDialogAddOpponent?.dismiss()
            }
        ).create()
        alertDialogAddOpponent.show()

    }

    private fun prepareUI(){
        setSizes()
        setDrawables()
        setConstraint()

    }

    private fun click() {
        binding.addOpponent.setOnClickListener {
            if(!deletable){
                playButtonClick()
                addNewOpponent()
            }

        }
        binding.deleteOpponent.setOnClickListener {
            playButtonClick()
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
        binding.chooseOpponentFragmentLayout.background = BackgroundColorDrawable(requireContext())
        binding.addOpponent.setImageDrawable(AddDrawable(requireContext()))
        binding.deleteOpponent.setImageDrawable(BinDrawable(requireContext(),deletable))
        binding.addOpponent.background = ButtonBackground(requireContext())
        binding.deleteOpponent.background = ButtonBackground(requireContext())
    }

    private fun setSizes() {
        val buttonSize = unit
        binding.addOpponent.layoutParams = ConstraintLayout.LayoutParams(buttonSize,buttonSize)
        binding.deleteOpponent.layoutParams = ConstraintLayout.LayoutParams(buttonSize,buttonSize)

    }

}