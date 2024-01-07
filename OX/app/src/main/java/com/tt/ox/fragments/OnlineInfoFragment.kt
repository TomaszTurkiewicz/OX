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
import com.tt.ox.drawables.RecyclerViewFrameDrawable
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
private const val NOTHING_TO_SHOW = 5
class OnlineInfoFragment : Fragment() {


    private var historyListSize = 0
    private var _binding:FragmentOnlineInfoBinding? = null
    private val binding get() = _binding!!
    private var unit = 0
    private var width = 0
    private lateinit var auth: FirebaseAuth
    private var currentUser:com.google.firebase.auth.FirebaseUser? = null
    private val _stageRanking = MutableLiveData<Int>()
    private val stageRanking:LiveData<Int> = _stageRanking
    private val _stageHistory = MutableLiveData<Int>()
    private val stageHistory:LiveData<Int> = _stageHistory
    private var mainUser:FirebaseUser? = null
    private val dbRefUsers = Firebase.database.getReference("Users")
    private val dbRefRanking = Firebase.database.getReference("Ranking")
    private val dbRefHistory = Firebase.database.getReference("History")
    private val idList:MutableList<FirebaseUserId> = mutableListOf()
    private var datesListSize = 0
    private var currentDatePosition = 0
    private val userList:MutableList<com.tt.ox.helpers.FirebaseUser> = mutableListOf()
    private val historyList:MutableList<com.tt.ox.helpers.FirebaseUser> = mutableListOf()
    private var currentUserPosition = 0
    private var currentHistoryPosition = 0
    private var userListSize = 0
    private var history:MutableList<HistoryWithUserId> = mutableListOf()
    private lateinit var rankingAdapter: RankingAdapter
    private lateinit var historyAdapter: RankingAdapter
    private var rankingHeight = 0
    private var rankingFrameWidth = 0
    private var rankingFrameHeight = 0
    private var frameWidth = 0

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
        rankingHeight = (width*0.6).toInt()
        rankingFrameWidth = width+(width*0.1).toInt()
        rankingFrameHeight = rankingHeight+(width*0.1).toInt()
        frameWidth = (width*0.05).toInt()
        unit = ScreenMetricsCompat().getUnit(requireContext())
        auth = Firebase.auth
        currentUser = auth.currentUser


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

    private fun prepareHistoryList() {
        if(history.size>0){
            historyList.clear()
            historyListSize = history.size
            currentHistoryPosition = 0
            readHistoryUsersFromFirebase()
        }
        else{
            _stageHistory.value = NOTHING_TO_SHOW
        }
    }

    private fun readHistoryUsersFromFirebase() {
        if(currentHistoryPosition<historyListSize){
            val percent:Double = (currentHistoryPosition+1).toDouble()/historyListSize.toDouble()
            try {
                binding?.loadingHistoryInfoProgressBar?.setImageDrawable(ProgressBarDrawable(requireContext(), percent, COLOR_BLUE))
            }catch (e:Exception){
                // do nothing
            }
            val dbRef = dbRefUsers.child(history[currentHistoryPosition].id)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.getValue(FirebaseUser::class.java)
                        user!!.wins = history[currentHistoryPosition].history.loses
                        user.loses = history[currentHistoryPosition].history.wins
                        historyList.add(user)
                    }
                    currentHistoryPosition+=1
                    readHistoryUsersFromFirebase()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
        }
        else{
            val dbRef = dbRefUsers.child(currentUser!!.uid)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.getValue(FirebaseUser::class.java)
                        user!!.wins = 0
                        user.loses = 0
                        historyList.add(user)
                    }
                    sortAndDisplayHistory()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
    }

    private fun sortAndDisplayHistory() {
        checkIfFragmentAttached {
            _stageHistory.value = SORTING
            sortHistory()
        }

    }

    private fun sortHistory() {
        var boolean = false
        if(historyList.size>1){
            for(i in 0..<historyList.size-1){
                val currentDif = historyList[i].wins - historyList[i].loses
                val nextDif = historyList[i+1].wins - historyList[i+1].loses
                if(currentDif<nextDif) {
                    val temp = historyList[i]
                    historyList[i] = historyList[i+1]
                    historyList[i+1] = temp
                    boolean = true
                }
                else {
                    if(currentDif==nextDif){
                        val currentWins = historyList[i].wins
                        val nextWins = historyList[i+1].wins
                        if(currentWins<nextWins){
                            val temp = historyList[i]
                            historyList[i] = historyList[i+1]
                            historyList[i+1] = temp
                            boolean = true
                        }
                    }
                }
            }
            if(boolean){
                sortHistory()
            }else{
                displayHistory()
            }
        }
        else{
            displayHistory()
        }
    }

    private fun displayHistory() {
        checkIfFragmentAttached {
            _stageHistory.value = READY
            historyAdapter = RankingAdapter(requireContext(), currentUser!!.uid, width, rankingHeight / 5, true)
            binding.recyclerHistory.adapter = historyAdapter
            binding.recyclerHistory.layoutManager = LinearLayoutManager(this)
            historyAdapter.submitList(historyList)

            var userPosition = -1
            for(i in 0..<historyList.size){
                if(historyList[i].id == currentUser!!.uid){
                    userPosition = i
                }
            }
            if(userPosition != -1){
                binding.recyclerHistory.scrollToPosition(userPosition-2)
            }
        }
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
            sort()
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
            rankingAdapter = RankingAdapter(requireContext(), currentUser!!.uid, width, rankingHeight / 5, false)
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
                binding.recyclerRanking.scrollToPosition(userPosition-2)
            }
        }
    }

    private fun checkIfFragmentAttached(operation: Context.() -> Unit){
        if(isAdded && context != null){
            operation(requireContext())
        }
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
        prepareRanking()
        prepareHistory()
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
        stageHistory.observe(this.viewLifecycleOwner){
            if(it== READY){
                binding.loadingHistoryInfoTextView.visibility = View.GONE
                binding.loadingHistoryInfoProgressBar.visibility = View.GONE
            }else if(it == NOTHING_TO_SHOW){
                binding.loadingHistoryInfoTextView.visibility = View.VISIBLE
                binding.loadingHistoryInfoProgressBar.visibility = View.GONE
                binding.loadingHistoryInfoTextView.text = "NOTHING TO SHOW"
            }
            else{
                binding.loadingHistoryInfoTextView.visibility = View.VISIBLE
                binding.loadingHistoryInfoProgressBar.visibility = View.VISIBLE
                binding.loadingHistoryInfoTextView.text = when(it){
                    USER -> "DOWNLOADING YOUR DATA"
                    ACTIVITIES -> "CHECKING ACTIVE USERS"
                    USERS -> "DOWNLOADING USERS DATA"
                    SORTING -> "CREATING RANKING"
                    else -> "DOING SOMETHING"
                }
            }
        }
    }

    private fun prepareHistory() {
        _stageHistory.value = USERS
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
                    prepareHistoryList()
                }
                else{
                    _stageHistory.value = NOTHING_TO_SHOW
                }
            }
            override fun onCancelled(error: DatabaseError) {
            // do nothing
            }
        })
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
        binding.historyTextView.text = "HISTORY"
    }

    private fun setConstraints() {
        val set = ConstraintSet()
        set.clone(binding.layout)

        set.connect(binding.rankingTextView.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,unit)
        set.connect(binding.rankingTextView.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.rankingTextView.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.rankingFrame.id,ConstraintSet.TOP, binding.rankingTextView.id,ConstraintSet.BOTTOM,unit/4)
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



        set.connect(binding.historyTextView.id,ConstraintSet.TOP,binding.recyclerRanking.id,ConstraintSet.BOTTOM,unit)
        set.connect(binding.historyTextView.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.historyTextView.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.historyFrame.id,ConstraintSet.TOP, binding.historyTextView.id,ConstraintSet.BOTTOM,unit/4)
        set.connect(binding.historyFrame.id,ConstraintSet.LEFT, binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.historyFrame.id,ConstraintSet.RIGHT, binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.recyclerHistory.id,ConstraintSet.LEFT, binding.historyFrame.id,ConstraintSet.LEFT,0)
        set.connect(binding.recyclerHistory.id,ConstraintSet.RIGHT, binding.historyFrame.id,ConstraintSet.RIGHT,0)
        set.connect(binding.recyclerHistory.id,ConstraintSet.TOP, binding.historyFrame.id,ConstraintSet.TOP,0)
        set.connect(binding.recyclerHistory.id,ConstraintSet.BOTTOM, binding.historyFrame.id,ConstraintSet.BOTTOM,0)

        set.connect(binding.loadingHistoryInfoTextView.id,ConstraintSet.LEFT,binding.recyclerHistory.id,ConstraintSet.LEFT,0)
        set.connect(binding.loadingHistoryInfoTextView.id,ConstraintSet.RIGHT,binding.recyclerHistory.id,ConstraintSet.RIGHT,0)
        set.connect(binding.loadingHistoryInfoTextView.id,ConstraintSet.TOP,binding.recyclerHistory.id,ConstraintSet.TOP,unit/2)

        set.connect(binding.loadingHistoryInfoProgressBar.id,ConstraintSet.LEFT,binding.recyclerHistory.id,ConstraintSet.LEFT,0)
        set.connect(binding.loadingHistoryInfoProgressBar.id,ConstraintSet.RIGHT,binding.recyclerHistory.id,ConstraintSet.RIGHT,0)
        set.connect(binding.loadingHistoryInfoProgressBar.id,ConstraintSet.BOTTOM,binding.recyclerHistory.id,ConstraintSet.BOTTOM,unit/2)


        set.applyTo(binding.layout)

    }

    private fun setDrawables() {
        binding.layout.background = BackgroundColorDrawable(requireContext())
        binding.loadingRankingInfoTextView.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.loadingRankingInfoProgressBar.background = ProgressBarBackgroundDrawable(requireContext())
        binding.rankingTextView.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.recyclerRanking.background = BackgroundColorDrawable(requireContext())

        binding.loadingHistoryInfoTextView.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.loadingHistoryInfoProgressBar.background = ProgressBarBackgroundDrawable(requireContext())
        binding.historyTextView.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.recyclerHistory.background = BackgroundColorDrawable(requireContext())

        binding.historyFrame.setImageDrawable(RecyclerViewFrameDrawable(requireContext(),frameWidth))
        binding.rankingFrame.setImageDrawable(RecyclerViewFrameDrawable(requireContext(),frameWidth))
    }

    private fun setSizes() {
        binding.loadingRankingInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit/2).toFloat())
        binding.loadingRankingInfoProgressBar.layoutParams = ConstraintLayout.LayoutParams(2*unit, 2*unit)
        binding.rankingTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit/2).toFloat())
        binding.rankingFrame.layoutParams = ConstraintLayout.LayoutParams(rankingFrameWidth,rankingFrameHeight)
        binding.recyclerRanking.layoutParams = ConstraintLayout.LayoutParams(width,rankingHeight)

        binding.loadingHistoryInfoTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit/2).toFloat())
        binding.loadingHistoryInfoProgressBar.layoutParams = ConstraintLayout.LayoutParams(2*unit, 2*unit)
        binding.historyTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (unit/2).toFloat())
        binding.historyFrame.layoutParams = ConstraintLayout.LayoutParams(rankingFrameWidth,rankingFrameHeight)
        binding.recyclerHistory.layoutParams = ConstraintLayout.LayoutParams(width,rankingHeight)
    }

}