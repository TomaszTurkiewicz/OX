package com.tt.ox.fragments

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.databinding.FragmentOnlineInfoBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ProgressBarBackgroundDrawable
import com.tt.ox.drawables.ProgressBarDrawable
import com.tt.ox.helpers.COLOR_BLUE
import com.tt.ox.helpers.COLOR_RED
import com.tt.ox.helpers.DateUtils
import com.tt.ox.helpers.FirebaseUser
import com.tt.ox.helpers.FirebaseUserId
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.Theme

private const val USER = 0
private const val ACTIVITIES = 1
private const val USERS = 2
private const val SORTING = 3
class OnlineInfoFragment : Fragment() {


    private var _binding:FragmentOnlineInfoBinding? = null
    private val binding get() = _binding!!
    private var unit = 0
    private var width = 0
    private lateinit var auth: FirebaseAuth
    private var currentUser:com.google.firebase.auth.FirebaseUser? = null
    private val _stage = MutableLiveData<Int>()
    private val stage:LiveData<Int> = _stage
    private var mainUser:FirebaseUser? = null
    private val dbRefUsers = Firebase.database.getReference("Users")
    private val dbRefRanking = Firebase.database.getReference("Ranking")
    private val idList:MutableList<FirebaseUserId> = mutableListOf()
    private var datesListSize = 0
    private var currentDatePosition = 0
    private val userList:MutableList<com.tt.ox.helpers.FirebaseUser> = mutableListOf()
    private var currentUserPosition = 0
    private var userListSize = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
        unit = ScreenMetricsCompat().getUnit(requireContext())
        auth = Firebase.auth
        currentUser = auth.currentUser
        prepareStats()

    }

    private fun prepareStats() {
        _stage.value = USER
        val dbRef = dbRefUsers.child(currentUser!!.uid)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mainUser = snapshot.getValue(FirebaseUser::class.java)
                checkActivities()
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })


    }

    private fun checkActivities() {
        _stage.value = ACTIVITIES
        idList.clear()
        val dates = DateUtils().getLastMonth()
        datesListSize = dates.size
        currentDatePosition = 0
        readIdListFromFirebase(dates)

    }

    private fun readIdListFromFirebase(dates: MutableList<Int>) {
        if(currentDatePosition<datesListSize){
            val percent:Double = (currentDatePosition+1).toDouble()/datesListSize.toDouble()
            try{
                binding?.loadingInfoProgressBar?.setImageDrawable(ProgressBarDrawable(requireContext(), percent, COLOR_RED))
            }catch (e:Exception){
                // do nothing
            }

            val dbRef = dbRefRanking.child(dates[currentDatePosition].toString())
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(id in snapshot.children){
                            val tId = id.getValue(FirebaseUserId::class.java)
                            idList.add(tId!!)
                        }
                    }
                    currentDatePosition+=1
                    readIdListFromFirebase(dates)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }
        else{
            readUserListFromFirebase()
        }
    }

    private fun readUserListFromFirebase() {
        _stage.value = USERS
        userList.clear()
        userListSize = idList.size
        currentUserPosition = 0
        readAllUsersFromFirebase()

    }

    private fun readAllUsersFromFirebase() {
        if(currentUserPosition<userListSize){
            val percent:Double = (currentUserPosition+1).toDouble()/userListSize.toDouble()
            try{
                binding?.loadingInfoProgressBar?.setImageDrawable(ProgressBarDrawable(requireContext(), percent, COLOR_BLUE))
            }catch (e:Exception){
                //do nothing
            }
            val dbRef = dbRefUsers.child(idList[currentUserPosition].userId!!)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.getValue(FirebaseUser::class.java)
                        userList.add(user!!)
                    }
                    currentUserPosition+=1
                    readAllUsersFromFirebase()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }else{
            readHistoryFromFirebase()
        }

    }

    private fun readHistoryFromFirebase() {


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineInfoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareUI()
        stage.observe(this.viewLifecycleOwner){
            binding.loadingInfoTextView.text = when(it){
                USER -> "DOWNLOADING YOUR DATA"
                ACTIVITIES -> "CHECKING ACTIVE USERS"
                USERS -> "DOWNLOADING USERS DATA"
                SORTING -> "CREATING RANKING"
                else -> "DOING SOMETHING"
            }
        }
    }

    private fun prepareUI() {
        setSizes()
        setDrawables()
        setConstraints()

    }

    private fun setConstraints() {
        val set = ConstraintSet()
        set.clone(binding.layout)

        set.connect(binding.middleOfTheScreen.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,0)
        set.connect(binding.middleOfTheScreen.id,ConstraintSet.BOTTOM,binding.layout.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.middleOfTheScreen.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.middleOfTheScreen.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.loadingInfoTextView.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.loadingInfoTextView.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.loadingInfoTextView.id,ConstraintSet.BOTTOM,binding.middleOfTheScreen.id,ConstraintSet.TOP,unit/2)

        set.connect(binding.loadingInfoProgressBar.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.loadingInfoProgressBar.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.loadingInfoProgressBar.id,ConstraintSet.TOP,binding.middleOfTheScreen.id,ConstraintSet.BOTTOM,unit/2)

        set.applyTo(binding.layout)

    }

    private fun setDrawables() {
        binding.layout.background = BackgroundColorDrawable(requireContext())
        binding.loadingInfoTextView.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.loadingInfoProgressBar.background = ProgressBarBackgroundDrawable(requireContext())
    }

    private fun setSizes() {
        binding.loadingInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit/2).toFloat())
        binding.loadingInfoProgressBar.layoutParams = ConstraintLayout.LayoutParams(2*unit, 2*unit)
    }

}