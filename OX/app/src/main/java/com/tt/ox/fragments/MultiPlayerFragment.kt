package com.tt.ox.fragments


import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.tt.ox.NOTHING
import com.tt.ox.O
import com.tt.ox.OXApplication
import com.tt.ox.R
import com.tt.ox.X
import com.tt.ox.databinding.FragmentMultiPlayerBinding
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.WinLineDrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory


class MultiPlayerFragment : Fragment() {

    private var _binding: FragmentMultiPlayerBinding? = null
    private val binding get() = _binding!!
    private var unit =0

    private val gameViewModel:GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        gameViewModel.initialize()
        gameViewModel.initializeMainPlayer()
        gameViewModel.initializeOpponentPlayer("Julia")
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
        setObserves()
        clicks()

    }

    private fun clicks() {
        binding.topLeftField.setOnClickListener {
            gameViewModel.setTopLeft()
        }
        binding.topMidField.setOnClickListener {
            gameViewModel.setTopMid()
        }
        binding.topRightField.setOnClickListener {
            gameViewModel.setTopRight()
        }

        binding.midLeftField.setOnClickListener {
            gameViewModel.setMidLeft()
        }
        binding.midMidField.setOnClickListener {
            gameViewModel.setMidMid()
        }
        binding.midRightField.setOnClickListener {
            gameViewModel.setMidRight()
        }

        binding.bottomLeftField.setOnClickListener {
            gameViewModel.setBottomLeft()
        }
        binding.bottomMidField.setOnClickListener {
            gameViewModel.setBottomMid()
        }
        binding.bottomRightField.setOnClickListener {
            gameViewModel.setBottomRight()
        }

        binding.reset.setOnClickListener {
            gameViewModel.initialize()
        }
        binding.switchMarks.setOnClickListener {
            gameViewModel.switchMarks()
        }
    }

    private fun setObserves() {
        gameViewModel.topLeft.observe(this.viewLifecycleOwner){
            setMark(binding.topLeftField,it)
        }
        gameViewModel.topMid.observe(this.viewLifecycleOwner){
            setMark(binding.topMidField,it)
        }
        gameViewModel.topRight.observe(this.viewLifecycleOwner){
            setMark(binding.topRightField,it)
        }

        gameViewModel.midLeft.observe(this.viewLifecycleOwner){
            setMark(binding.midLeftField,it)
        }
        gameViewModel.midMid.observe(this.viewLifecycleOwner){
            setMark(binding.midMidField,it)
        }
        gameViewModel.midRight.observe(this.viewLifecycleOwner){
            setMark(binding.midRightField,it)
        }

        gameViewModel.bottomLeft.observe(this.viewLifecycleOwner){
            setMark(binding.bottomLeftField,it)
        }
        gameViewModel.bottomMid.observe(this.viewLifecycleOwner){
            setMark(binding.bottomMidField,it)
        }
        gameViewModel.bottomRight.observe(this.viewLifecycleOwner){
            setMark(binding.bottomRightField,it)
        }
        gameViewModel.play.observe(this.viewLifecycleOwner){
            if(it){
                binding.winLine.setImageDrawable(null)
                binding.winLine.visibility = View.GONE
                binding.reset.visibility = View.GONE
            }else{
                binding.winLine.visibility = View.VISIBLE
                binding.reset.visibility = View.VISIBLE
                binding.winLine.setImageDrawable(WinLineDrawable(requireContext(),
                    gameViewModel.getHorizontalTop(),gameViewModel.getHorizontalMid(),
                    gameViewModel.getHorizontalBottom(),gameViewModel.getVerticalLeft(),
                    gameViewModel.getVerticalMid(),gameViewModel.getVerticalRight(),
                    gameViewModel.getAngleUp(),gameViewModel.getAngleDown()))
            }
        }

        gameViewModel.mainPlayer.value!!.mark.observe(this.viewLifecycleOwner){
            if(it==X){
                binding.mainPlayerMark.setImageDrawable(XDrawable(requireContext()))
            } else{
                binding.mainPlayerMark.setImageDrawable(ODrawable(requireContext()))
            }
        }

        gameViewModel.opponentPlayer.value!!.mark.observe(this.viewLifecycleOwner){
            if(it==X){
                binding.opponentPlayerMark.setImageDrawable(XDrawable(requireContext()))
            } else{
                binding.opponentPlayerMark.setImageDrawable(ODrawable(requireContext()))
            }

        }
        gameViewModel.opponentPlayer.value!!.turn.observe(this.viewLifecycleOwner){
            if(it){
                binding.opponentPlayerName.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.red))
            }else{
                binding.opponentPlayerName.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.white))
            }
        }
        gameViewModel.mainPlayer.value!!.turn.observe(this.viewLifecycleOwner){
            if(it){
                binding.mainPlayerName.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.red))
            }else{
                binding.mainPlayerName.setBackgroundColor(ContextCompat.getColor(requireContext(),
                    R.color.white))
            }
        }

        gameViewModel.buttonEnable.observe(this.viewLifecycleOwner){
            if(it){
                binding.switchMarks.visibility = View.VISIBLE
            }else{
                binding.switchMarks.visibility = View.GONE
            }
        }
    }

    private fun setMark(view:ImageView, mark:Int){
        when(mark){
            NOTHING -> view.setImageDrawable(null)
            X -> view.setImageDrawable(XDrawable(requireContext()))
            O -> view.setImageDrawable(ODrawable(requireContext()))
        }
    }

    private fun prepareUI() {
        setSizes()
        setDrawables()
        setNames()
        setConstraint()
    }

    private fun setNames() {
        binding.mainPlayerName.text = gameViewModel.getMainPlayerName()
        binding.opponentPlayerName.text = gameViewModel.getOpponentPlayerName()
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

        binding.winLine.layoutParams = ConstraintLayout.LayoutParams(3*fieldSize,3*fieldSize)

        binding.reset.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize)

        binding.mainPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (fieldSize/3).toFloat())
        binding.opponentPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (fieldSize/3).toFloat())

        binding.mainPlayerMark.layoutParams = ConstraintLayout.LayoutParams(fieldSize/3,fieldSize/3)
        binding.opponentPlayerMark.layoutParams = ConstraintLayout.LayoutParams(fieldSize/3,fieldSize/3)

        binding.switchMarks.layoutParams = ConstraintLayout.LayoutParams(fieldSize,fieldSize/3)
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

        set.connect(binding.winLine.id,ConstraintSet.TOP, binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.winLine.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.winLine.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.winLine.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)


        set.connect(binding.reset.id,ConstraintSet.BOTTOM, binding.multiPlayerLayout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.reset.id,ConstraintSet.LEFT, binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.reset.id,ConstraintSet.RIGHT, binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerName.id,ConstraintSet.TOP,binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.mainPlayerName.id,ConstraintSet.LEFT,binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerName.id,ConstraintSet.TOP,binding.multiPlayerLayout.id,ConstraintSet.TOP,0)
        set.connect(binding.opponentPlayerName.id,ConstraintSet.RIGHT,binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerMark.id,ConstraintSet.LEFT,binding.multiPlayerLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerMark.id,ConstraintSet.TOP,binding.mainPlayerName.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.opponentPlayerMark.id,ConstraintSet.RIGHT,binding.multiPlayerLayout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerMark.id,ConstraintSet.TOP,binding.opponentPlayerName.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.switchMarks.id,ConstraintSet.LEFT,binding.mainPlayerMark.id,ConstraintSet.RIGHT,0)
        set.connect(binding.switchMarks.id,ConstraintSet.TOP,binding.mainPlayerMark.id,ConstraintSet.TOP,0)
        set.connect(binding.switchMarks.id,ConstraintSet.BOTTOM,binding.mainPlayerMark.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.switchMarks.id,ConstraintSet.RIGHT,binding.opponentPlayerMark.id,ConstraintSet.LEFT,0)

        set.applyTo(binding.multiPlayerLayout)

    }


}