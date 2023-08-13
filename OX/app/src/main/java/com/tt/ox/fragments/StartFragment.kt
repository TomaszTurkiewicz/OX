package com.tt.ox.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import com.tt.ox.R
import com.tt.ox.databinding.FragmentStartBinding
import com.tt.ox.helpers.ScreenMetricsCompat


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
        clicks()
    }

    private fun clicks() {
        binding.let {
            it.singlePlayerButton.setOnClickListener {
                val action = StartFragmentDirections.actionStartFragmentToSinglePlayerFragment()
                findNavController().navigate(action)
            }
            it.multiPlayerButton.setOnClickListener {
                val action = StartFragmentDirections.actionStartFragmentToMultiPlayerFragment()
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