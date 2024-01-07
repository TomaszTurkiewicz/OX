package com.tt.ox.fragments

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.adapters.RankingAdapter
import com.tt.ox.databinding.FragmentOnlineInfoBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ProgressBarBackgroundDrawable
import com.tt.ox.drawables.ProgressBarDrawable
import com.tt.ox.helpers.COLOR_BLUE
import com.tt.ox.helpers.COLOR_RED
import com.tt.ox.helpers.DateUtils
import com.tt.ox.helpers.FirebaseHistory
import com.tt.ox.helpers.FirebaseUser
import com.tt.ox.helpers.FirebaseUserId
import com.tt.ox.helpers.HistoryWithUserId
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.Theme

private const val USER = 0
private const val ACTIVITIES = 1
private const val USERS = 2
private const val SORTING = 3
private const val READY = 4
class OnlineInfoFragment : Fragment() {


    private var _binding:FragmentOnlineInfoBinding? = null
    private val binding get() = _binding!!
    private var unit = 0
    private var width = 0
    private lateinit var auth: FirebaseAuth
    private var currentUser:com.google.firebase.auth.FirebaseUser? = null
    private val _stageRanking = MutableLiveData<Int>()
    private val stageRanking:LiveData<Int> = _stageRanking
    private var mainUser:FirebaseUser? = null
    private val dbRefUsers = Firebase.database.getReference("Users")
    private val dbRefRanking = Firebase.database.getReference("Ranking")
    private val dbRefHistory = Firebase.database.getReference("History")
    private val idList:MutableList<FirebaseUserId> = mutableListOf()
    private var datesListSize = 0
    private var currentDatePosition = 0
    private val userList:MutableList<com.tt.ox.helpers.FirebaseUser> = mutableListOf()
    private var currentUserPosition = 0
    private var userListSize = 0
    private var history:MutableList<HistoryWithUserId> = mutableListOf()
    private lateinit var rankingAdapter: RankingAdapter
    private var rankingHeight = 0
    private var rankingFrameWidth = 0
    private var rankingFrameHeight = 0

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
        rankingHeight = (width*0.6).toInt()
        rankingFrameWidth = ScreenMetricsCompat().getWindowWidth(requireContext())
        rankingFrameHeight = rankingHeight+(width*0.1).toInt()
        unit = ScreenMetricsCompat().getUnit(requireContext())
        auth = Firebase.auth
        currentUser = auth.currentUser


    }

    private fun prepareStats() {
        _stageRanking.value = USER
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
        _stageRanking.value = ACTIVITIES
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
                binding?.loadingRankingInfoProgressBar?.setImageDrawable(ProgressBarDrawable(requireContext(), percent, COLOR_RED))
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
        _stageRanking.value = USERS
        userList.clear()
        userListSize = idList.size
        currentUserPosition = 0
        readAllUsersFromFirebase()
    }

    private fun readAllUsersFromFirebase() {
        if(currentUserPosition<userListSize){
            val percent:Double = (currentUserPosition+1).toDouble()/userListSize.toDouble()
            try{
                binding?.loadingRankingInfoProgressBar?.setImageDrawable(ProgressBarDrawable(requireContext(), percent, COLOR_BLUE))
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
            sortAndDisplayRanking()
        }

    }

    private fun sortAndDisplayRanking() {
        checkIfFragmentAttached {
            _stageRanking.value = SORTING
            //todo sorting
            sort()

//            _stageRanking.value = READY
//            rankingAdapter = RankingAdapter(requireContext(), currentUser!!.uid, width, rankingHeight / 5)
//            binding.recyclerRanking.adapter = rankingAdapter
//            binding.recyclerRanking.layoutManager = LinearLayoutManager(this)
//            rankingAdapter.submitList(userList)
        }
    }

    private fun sort() {
        var boolean = false
        if(userList.size>1){
            for(i in 0..<userList.size-1){
                val currentDif = userList[i].wins - userList[i].loses
                val nextDif = userList[i+1].wins - userList[i+1].loses
                if(currentDif<nextDif) {
                    val temp = userList[i]
                    userList[i] = userList[i+1]
                    userList[i+1] = temp
                    boolean = true
                }
                else {
                    if(currentDif==nextDif){
                        val currentWins = userList[i].wins
                        val nextWins = userList[i+1].wins
                        if(currentWins<nextWins){
                            val temp = userList[i]
                            userList[i] = userList[i+1]
                            userList[i+1] = temp
                            boolean = true
                        }
                    }
                }
            }
            if(boolean){
                sort()
            }else{
                displayRanking()
            }
        }
        else{
            displayRanking()
        }
    }

    private fun displayRanking() {
        checkIfFragmentAttached {
            _stageRanking.value = READY
            rankingAdapter = RankingAdapter(requireContext(), currentUser!!.uid, width, rankingHeight / 5)
            binding.recyclerRanking.adapter = rankingAdapter
            binding.recyclerRanking.layoutManager = LinearLayoutManager(this)
            rankingAdapter.submitList(userList)

            var userPosition = -1
            for(i in 0..<userList.size){
                if(userList[i].id == currentUser!!.uid){
                    userPosition = i
                }
            }
            if(userPosition != -1){
                binding.recyclerRanking.scrollToPosition(userPosition)
            }
        }
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit){
        if(isAdded && context != null){
            operation(requireContext())
        }
    }

    private fun readHistoryFromFirebase() {

        history.clear()
        val dbRef = dbRefHistory.child(currentUser!!.uid)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(child in snapshot.children){
                        val historyF = child.getValue(FirebaseHistory::class.java)
                        val id = child.key
                        val historyWithId = HistoryWithUserId(id!!,historyF!!)
                       history.add(historyWithId)
                    }
                    _stageRanking.value = SORTING
                }
                else{
                    _stageRanking.value = SORTING
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }

        })

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
//        prepareStats()
        prepareRanking()
        stageRanking.observe(this.viewLifecycleOwner){
            if(it== READY){
                binding.loadingRankingInfoTextView.visibility = View.GONE
                binding.loadingRankingInfoProgressBar.visibility = View.GONE
            }else{
                binding.loadingRankingInfoTextView.visibility = View.VISIBLE
                binding.loadingRankingInfoProgressBar.visibility = View.VISIBLE
                binding.loadingRankingInfoTextView.text = when(it){
                    USER -> "DOWNLOADING YOUR DATA"
                    ACTIVITIES -> "CHECKING ACTIVE USERS"
                    USERS -> "DOWNLOADING USERS DATA"
                    SORTING -> "CREATING RANKING"
                    else -> "DOING SOMETHING"
                }
            }
        }
    }

    private fun prepareRanking() {
        _stageRanking.value = USER
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

    private fun prepareUI() {
        setSizes()
        setDrawables()
        setTexts()
        setConstraints()

    }

    private fun setTexts() {
        binding.rankingTextView.text = "RANKING"
    }

    private fun setConstraints() {
        val set = ConstraintSet()
        set.clone(binding.layout)

        set.connect(binding.rankingTextView.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,unit)
        set.connect(binding.rankingTextView.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.rankingTextView.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.rankingFrame.id,ConstraintSet.TOP, binding.rankingTextView.id,ConstraintSet.BOTTOM,unit/2)
        set.connect(binding.rankingFrame.id,ConstraintSet.LEFT, binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.rankingFrame.id,ConstraintSet.RIGHT, binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.recyclerRanking.id,ConstraintSet.LEFT, binding.rankingFrame.id,ConstraintSet.LEFT,0)
        set.connect(binding.recyclerRanking.id,ConstraintSet.RIGHT, binding.rankingFrame.id,ConstraintSet.RIGHT,0)
        set.connect(binding.recyclerRanking.id,ConstraintSet.TOP, binding.rankingFrame.id,ConstraintSet.TOP,0)
        set.connect(binding.recyclerRanking.id,ConstraintSet.BOTTOM, binding.rankingFrame.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.loadingRankingInfoTextView.id,ConstraintSet.LEFT,binding.recyclerRanking.id,ConstraintSet.LEFT,0)
        set.connect(binding.loadingRankingInfoTextView.id,ConstraintSet.RIGHT,binding.recyclerRanking.id,ConstraintSet.RIGHT,0)
        set.connect(binding.loadingRankingInfoTextView.id,ConstraintSet.TOP,binding.recyclerRanking.id,ConstraintSet.TOP,unit/2)

        set.connect(binding.loadingRankingInfoProgressBar.id,ConstraintSet.LEFT,binding.recyclerRanking.id,ConstraintSet.LEFT,0)
        set.connect(binding.loadingRankingInfoProgressBar.id,ConstraintSet.RIGHT,binding.recyclerRanking.id,ConstraintSet.RIGHT,0)
        set.connect(binding.loadingRankingInfoProgressBar.id,ConstraintSet.BOTTOM,binding.recyclerRanking.id,ConstraintSet.BOTTOM,unit/2)



        set.applyTo(binding.layout)

    }

    private fun setDrawables() {
        binding.layout.background = BackgroundColorDrawable(requireContext())
        binding.loadingRankingInfoTextView.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.loadingRankingInfoProgressBar.background = ProgressBarBackgroundDrawable(requireContext())
        binding.rankingTextView.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.recyclerRanking.background = BackgroundColorDrawable(requireContext())
    }

    private fun setSizes() {
        binding.loadingRankingInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit/2).toFloat())
        binding.loadingRankingInfoProgressBar.layoutParams = ConstraintLayout.LayoutParams(2*unit, 2*unit)
        binding.rankingTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit/2).toFloat())
        binding.rankingFrame.layoutParams = ConstraintLayout.LayoutParams(rankingFrameWidth,rankingFrameHeight)
        binding.recyclerRanking.layoutParams = ConstraintLayout.LayoutParams(width,rankingHeight)
    }

}