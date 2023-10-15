package com.tt.ox.fragments

import android.app.ActionBar
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.MainActivity
import com.tt.ox.R
import com.tt.ox.databinding.FragmentOnlineBattleBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.MeshDrawable
import com.tt.ox.drawables.PointerUpperDrawable
import com.tt.ox.helpers.FirebaseRequests
import com.tt.ox.helpers.ScreenMetricsCompat

class OnlineBattleFragment : Fragment() {
    private var _binding:FragmentOnlineBattleBinding? = null
    private val binding get() = _binding!!
    private var unit =0
    private var width = 0
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var dbRefBattle:DatabaseReference? = null
    private val dbRefRequest = Firebase.database.getReference("Requests")
    private var battleId:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        unit = ScreenMetricsCompat().getUnit(requireContext())
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
        val activity = activity as MainActivity
        activity.setBack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineBattleBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUI()

    }

    override fun onResume() {
        super.onResume()
        val dbR = dbRefRequest.child(currentUser!!.uid)
        dbR.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val req = snapshot.getValue(FirebaseRequests::class.java)
                    battleId = req!!.battle!!
                    dbRefBattle = Firebase.database.getReference("Battle").child(battleId)
                    checkIfBattleExists()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onPause() {
        super.onPause()
        dbRefBattle = null
    }

    private fun checkIfBattleExists() {
        dbRefBattle!!.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                }else{
                    val req = FirebaseRequests()
                    dbRefRequest.child(currentUser!!.uid).setValue(req)
//                    val currentFragment = findNavController().currentDestination
//                    val a = 100
                    val action = OnlineBattleFragmentDirections.actionOnlineBattleToOnlineChooseOpponentFragment()
                    findNavController().navigateUp()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }

    private fun prepareUI() {
        setSizes()
        setDrawables()
        setConstraint()
    }

    private fun setConstraint() {
        val set = ConstraintSet()

        set.clone(binding.onlineBattleLayout)

        set.connect(binding.backgroundField.id,
            ConstraintSet.TOP, binding.onlineBattleLayout.id,
            ConstraintSet.TOP,3*unit)
        set.connect(binding.backgroundField.id,
            ConstraintSet.BOTTOM, binding.onlineBattleLayout.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.LEFT, binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.backgroundField.id,
            ConstraintSet.RIGHT, binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.midMidField.id,
            ConstraintSet.TOP, binding.backgroundField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.BOTTOM, binding.backgroundField.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.LEFT, binding.backgroundField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.midMidField.id,
            ConstraintSet.RIGHT, binding.backgroundField.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.topMidField.id,
            ConstraintSet.BOTTOM,binding.midMidField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.topMidField.id,
            ConstraintSet.LEFT,binding.midMidField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.topMidField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.topRightField.id,
            ConstraintSet.LEFT,binding.midMidField.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.topRightField.id,
            ConstraintSet.BOTTOM,binding.midMidField.id,
            ConstraintSet.TOP,0)

        set.connect(binding.midRightField.id,
            ConstraintSet.LEFT, binding.midMidField.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.midRightField.id,
            ConstraintSet.TOP, binding.midMidField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.midRightField.id,
            ConstraintSet.BOTTOM, binding.midMidField.id,
            ConstraintSet.BOTTOM,0)

        set.connect(binding.bottomRightField.id,
            ConstraintSet.LEFT,binding.midMidField.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.bottomRightField.id,
            ConstraintSet.TOP,binding.midMidField.id,
            ConstraintSet.BOTTOM,0)

        set.connect(binding.bottomMidField.id,
            ConstraintSet.TOP,binding.midMidField.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.bottomMidField.id,
            ConstraintSet.LEFT,binding.midMidField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.bottomMidField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.bottomLeftField.id,
            ConstraintSet.TOP,binding.midMidField.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.bottomLeftField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.midLeftField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.midLeftField.id,
            ConstraintSet.TOP,binding.midMidField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.midLeftField.id,
            ConstraintSet.BOTTOM,binding.midMidField.id,
            ConstraintSet.BOTTOM,0)

        set.connect(binding.topLeftField.id,
            ConstraintSet.BOTTOM,binding.midMidField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.topLeftField.id,
            ConstraintSet.RIGHT,binding.midMidField.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.winLine.id,
            ConstraintSet.TOP, binding.backgroundField.id,
            ConstraintSet.TOP,0)
        set.connect(binding.winLine.id,
            ConstraintSet.BOTTOM, binding.backgroundField.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.winLine.id,
            ConstraintSet.LEFT, binding.backgroundField.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.winLine.id,
            ConstraintSet.RIGHT, binding.backgroundField.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.divider.id, ConstraintSet.TOP,binding.onlineBattleLayout.id, ConstraintSet.TOP,0)
        set.connect(binding.divider.id,
            ConstraintSet.LEFT,binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.divider.id,
            ConstraintSet.RIGHT,binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.TOP,binding.onlineBattleLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.mainPlayerWins.id,
            ConstraintSet.LEFT,binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerWins.id, ConstraintSet.RIGHT,
            binding.divider.id, ConstraintSet.LEFT,0)

        set.connect(binding.mainPlayerName.id,
            ConstraintSet.TOP,binding.mainPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.LEFT,binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerName.id,
            ConstraintSet.RIGHT,binding.divider.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.LEFT,binding.onlineBattleLayout.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.TOP,binding.mainPlayerName.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.mainPlayerMark.id,
            ConstraintSet.RIGHT,binding.divider.id,
            ConstraintSet.LEFT,0)

        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.TOP,binding.onlineBattleLayout.id,
            ConstraintSet.TOP,0)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.RIGHT,binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,unit)
        set.connect(binding.opponentPlayerWins.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,unit)

        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.TOP,binding.opponentPlayerWins.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.RIGHT,binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerName.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.RIGHT,binding.onlineBattleLayout.id,
            ConstraintSet.RIGHT,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.TOP,binding.opponentPlayerName.id,
            ConstraintSet.BOTTOM,0)
        set.connect(binding.opponentPlayerMark.id,
            ConstraintSet.LEFT,binding.divider.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.mainPlayerPointerUpper.id,
            ConstraintSet.BOTTOM,binding.mainPlayerWins.id,
            ConstraintSet.TOP,0)
        set.connect(binding.mainPlayerPointerUpper.id,
            ConstraintSet.LEFT,binding.mainPlayerWins.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.mainPlayerPointerUpper.id,
            ConstraintSet.RIGHT,binding.mainPlayerWins.id,
            ConstraintSet.RIGHT,0)

        set.connect(binding.opponentPointerUpper.id,
            ConstraintSet.BOTTOM,binding.opponentPlayerWins.id,
            ConstraintSet.TOP,0)
        set.connect(binding.opponentPointerUpper.id,
            ConstraintSet.LEFT,binding.opponentPlayerWins.id,
            ConstraintSet.LEFT,0)
        set.connect(binding.opponentPointerUpper.id,
            ConstraintSet.RIGHT,binding.opponentPlayerWins.id,
            ConstraintSet.RIGHT,0)

        set.applyTo(binding.onlineBattleLayout)

    }

    private fun setDrawables() {
        binding.onlineBattleLayout.background = BackgroundColorDrawable(requireContext())

        binding.backgroundField.setImageDrawable(MeshDrawable(requireContext()))
        binding.mainPlayerWins.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.opponentPlayerWins.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.mainPlayerName.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.opponentPlayerName.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        binding.mainPlayerPointerUpper.setImageDrawable(PointerUpperDrawable(requireContext()))
        binding.opponentPointerUpper.setImageDrawable(PointerUpperDrawable(requireContext()))
        
    }

    private fun setSizes() {
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

        binding.mainPlayerWins.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit*0.8f)
        binding.opponentPlayerWins.setTextSize(TypedValue.COMPLEX_UNIT_PX, unit*0.8f)

        binding.mainPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit).toFloat())
        binding.opponentPlayerName.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit).toFloat())

        binding.mainPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,
            ActionBar.LayoutParams.WRAP_CONTENT)
        binding.opponentPlayerName.layoutParams = ConstraintLayout.LayoutParams(4*unit,
            ActionBar.LayoutParams.WRAP_CONTENT)

        binding.mainPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentPlayerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            binding.mainPlayerName.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            binding.opponentPlayerName.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        binding.mainPlayerPointerUpper.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        binding.opponentPointerUpper.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
    }

}