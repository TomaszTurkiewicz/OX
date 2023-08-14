package com.tt.ox.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.tt.ox.R
import com.tt.ox.databinding.FragmentMultiPlayerBinding
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.helpers.ScreenMetricsCompat


class MultiPlayerFragment : Fragment() {

    private var _binding: FragmentMultiPlayerBinding? = null
    private val binding get() = _binding!!
    private var unit =0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())

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
        prepareUI()

    }

    private fun prepareUI() {
        setSizes()
        setDrawables()
        setConstraint()
    }

    private fun setDrawables(){
        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))
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
    }

    private fun setConstraint() {
        val set = ConstraintSet()

        set.clone(binding.multiPlayerLayout)

        set.connect(binding.backgroundField.id,ConstraintSet.TOP, binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.backgroundField.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.backgroundField.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.backgroundField.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.midMidField.id,ConstraintSet.TOP, binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.midMidField.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.midMidField.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.midMidField.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.topMidField.id,ConstraintSet.BOTTOM,binding.midMidField.id,ConstraintSet.TOP,0)
        set.connect(binding.topMidField.id,ConstraintSet.LEFT,binding.midMidField.id,ConstraintSet.LEFT,0)
        set.connect(binding.topMidField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.RIGHT,0)

        set.connect(binding.topRightField.id,ConstraintSet.LEFT,binding.midMidField.id,ConstraintSet.RIGHT,0)
        set.connect(binding.topRightField.id,ConstraintSet.BOTTOM,binding.midMidField.id,ConstraintSet.TOP,0)

        set.connect(binding.midRightField.id,ConstraintSet.LEFT, binding.midMidField.id,ConstraintSet.RIGHT,0)
        set.connect(binding.midRightField.id,ConstraintSet.TOP, binding.midMidField.id,ConstraintSet.TOP,0)
        set.connect(binding.midRightField.id,ConstraintSet.BOTTOM, binding.midMidField.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.bottomRightField.id,ConstraintSet.LEFT,binding.midMidField.id,ConstraintSet.RIGHT,0)
        set.connect(binding.bottomRightField.id,ConstraintSet.TOP,binding.midMidField.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.bottomMidField.id,ConstraintSet.TOP,binding.midMidField.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.bottomMidField.id,ConstraintSet.LEFT,binding.midMidField.id,ConstraintSet.LEFT,0)
        set.connect(binding.bottomMidField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.RIGHT,0)

        set.connect(binding.bottomLeftField.id,ConstraintSet.TOP,binding.midMidField.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.bottomLeftField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.LEFT,0)

        set.connect(binding.midLeftField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.LEFT,0)
        set.connect(binding.midLeftField.id,ConstraintSet.TOP,binding.midMidField.id,ConstraintSet.TOP,0)
        set.connect(binding.midLeftField.id,ConstraintSet.BOTTOM,binding.midMidField.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.topLeftField.id,ConstraintSet.BOTTOM,binding.midMidField.id,ConstraintSet.TOP,0)
        set.connect(binding.topLeftField.id,ConstraintSet.RIGHT,binding.midMidField.id,ConstraintSet.LEFT,0)

        set.applyTo(binding.multiPlayerLayout)

    }


}