package com.tt.ox.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.MOVES
import com.tt.ox.R
import com.tt.ox.TEST
import com.tt.ox.adapters.OnlineListAdapter
import com.tt.ox.alertDialogs.AlertDialogAddMoves
import com.tt.ox.alertDialogs.AlertDialogLogin
import com.tt.ox.alertDialogs.AlertDialogWaiting
import com.tt.ox.databinding.FragmentOnlineChooseOpponentBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.EditTextBackground
import com.tt.ox.drawables.LogoutDrawable
import com.tt.ox.drawables.ProgressBarBackgroundDrawable
import com.tt.ox.drawables.ProgressBarDrawable
import com.tt.ox.drawables.SearchDrawable
import com.tt.ox.drawables.UpdateListDrawable
import com.tt.ox.helpers.ACCEPTED
import com.tt.ox.helpers.AVAILABLE
import com.tt.ox.helpers.COLOR_BLUE
import com.tt.ox.helpers.COLOR_RED
import com.tt.ox.helpers.DateUtils
import com.tt.ox.helpers.FirebaseBattle
import com.tt.ox.helpers.FirebaseRequests
import com.tt.ox.helpers.FirebaseUserId
import com.tt.ox.helpers.PLAY
import com.tt.ox.helpers.RECEIVED
import com.tt.ox.helpers.REJECTED
import com.tt.ox.helpers.SEND
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences
import com.tt.ox.helpers.Theme
import kotlin.random.Random

private const val DATES = 0
private const val USERS = 1
private const val FILTERING = 2

class OnlineChooseOpponentFragment : FragmentCoroutine() {
    private var _binding: FragmentOnlineChooseOpponentBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private var width = 0
    private var unit = 0
    private var currentUser: FirebaseUser? = null
    private val dbRefRequest = Firebase.database.getReference("Requests")
    private val dbRefUsers = Firebase.database.getReference("Users")
    private val dbRefRanking = Firebase.database.getReference("Ranking")
    private val dbRefBattle = Firebase.database.getReference("Battle")
    private var invitationsDbRef: DatabaseReference? = null
    private var invitationsListener: ValueEventListener? = null
    private var dialogMoves:AlertDialog? = null
    private val idList:MutableList<FirebaseUserId> = mutableListOf()
    private var datesListSize = 0
    private var currentPosition = 0
    private lateinit var adapter: OnlineListAdapter
    private val userList:MutableList<com.tt.ox.helpers.FirebaseUser> = mutableListOf()
    private var loopCounter = 0
    private var listSize = 0
    private var currentUserPosition = 0
    private var dialogInvitation:AlertDialog? = null
    private val _moves = MutableLiveData<Int>()
    private val moves:LiveData<Int> = _moves
    private val _listReady = MutableLiveData<Boolean>()
    private var listReady:LiveData<Boolean> = _listReady
    private val listHandler = Handler(Looper.getMainLooper())
    private var dialogLogout:AlertDialog? = null
    private val _stage = MutableLiveData<Int>()
    private val stage:LiveData<Int> = _stage
    private val _searching = MutableLiveData<Boolean>()
    private val searching:LiveData<Boolean> = _searching
    private val _noUsers = MutableLiveData<Boolean>()
    private val noUsers:LiveData<Boolean> = _noUsers
    private var mRewardedAd : RewardedAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        currentUser = auth.currentUser

        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()
        unit = ScreenMetricsCompat().getUnit(requireContext())
        _moves.value = SharedPreferences.readOnlineMoves(requireContext())
        _listReady.value = false
        _searching.value = false
        _noUsers.value = false

        prepareRewardedAd()
        prepareList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlineChooseOpponentBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listReady.observe(this.viewLifecycleOwner){
            binding.updateList.setImageDrawable(UpdateListDrawable(requireContext(),it))
            binding.searchButton.setImageDrawable(SearchDrawable(requireContext(),it))
            setInfoVisibility(it)
        }
        setUI()
        moves.observe(this.viewLifecycleOwner){
            binding.moves.text = it.toString()
        }

        stage.observe(this.viewLifecycleOwner){
            binding.infoText.text = when(it){
                DATES -> "CHECKING ACTIVITIES"
                USERS -> "DOWNLOADING USERS"
                FILTERING -> "FILTERING USERS"
                else -> "ELSE"
            }
        }

        noUsers.observe(this.viewLifecycleOwner){
            binding.noUsers.visibility = if(it) View.VISIBLE else View.GONE
        }

        searching.observe(this.viewLifecycleOwner){
            if(it){
                binding.searchEditText.visibility = View.VISIBLE
            }else{
                binding.searchEditText.visibility = View.GONE
                if(listReady.value!!){
                    adapter.submitList(getUserList(null))
                }
            }
        }

        binding.logout.setOnClickListener {
            playButtonClick()
                dialogLogout = AlertDialogLogin(
                    requireContext(),
                    layoutInflater,
                    "LOGOUT",
                    "Are You sure You want to logout?",
                    {
                        playButtonClick()
                        auth.signOut()
                        dialogLogout?.dismiss()
                        findNavController().navigateUp()
                    },
                    {
                        playButtonClick()
                        dialogLogout?.dismiss()
                    }
                ).create()
                dialogLogout?.show()

        }

        binding.updateList.setOnClickListener {
            playButtonClick()
            if(listReady.value!!){
                _noUsers.value = false
                _listReady.value = false
                adapter.submitList(null)
                prepareList()
                displayList().run()
            }else{
                Toast.makeText(requireContext(),"LIST NOT READY YET",Toast.LENGTH_LONG).show()
            }
        }

        binding.searchButton.setOnClickListener {
            playButtonClick()
            if(listReady.value!!){
                var s = searching.value!!
                s = !s
                _searching.value = s
                if(s){
                    adapter.submitList(getUserList(binding.searchEditText.text.toString()))
                    binding.searchEditText.requestFocus()
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showSoftInput(binding.searchEditText, InputMethodManager.SHOW_IMPLICIT)
                }else{
                    val inputManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputManager.hideSoftInputFromWindow(view.windowToken,0)
                }
            }else{
                Toast.makeText(requireContext(),"LIST NOT READY YET",Toast.LENGTH_LONG).show()
            }
        }

        binding.searchEditText.doAfterTextChanged {
            if(searching.value!!){
                adapter.submitList(getUserList(it.toString()))
            }
        }

    }

    private fun setInfoVisibility(it: Boolean) {
        if(it){
            binding.infoText.visibility = View.GONE
            binding.progressBar.visibility = View.GONE
        }else{
            binding.infoText.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private fun setUI() {
        setSizes()
        setDrawables()
        setConstraints()

    }

    private fun setConstraints() {
        val set = ConstraintSet()
        set.clone(binding.layout)

        set.connect(binding.middleDivider.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,0)
        set.connect(binding.middleDivider.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.middleDivider.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.rightDivider.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,0)
        set.connect(binding.rightDivider.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.rightDivider.id,ConstraintSet.LEFT,binding.middleDivider.id,ConstraintSet.RIGHT,0)

        set.connect(binding.leftDivider.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,0)
        set.connect(binding.leftDivider.id,ConstraintSet.RIGHT,binding.middleDivider.id,ConstraintSet.LEFT,0)
        set.connect(binding.leftDivider.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)

        set.connect(binding.logout.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,unit/2)
        set.connect(binding.logout.id,ConstraintSet.LEFT,binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.logout.id,ConstraintSet.RIGHT,binding.leftDivider.id,ConstraintSet.LEFT,0)

        set.connect(binding.moves.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,unit/2)
        set.connect(binding.moves.id,ConstraintSet.LEFT,binding.leftDivider.id,ConstraintSet.RIGHT,0)
        set.connect(binding.moves.id,ConstraintSet.RIGHT,binding.middleDivider.id,ConstraintSet.LEFT,0)

        set.connect(binding.updateList.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,unit/2)
        set.connect(binding.updateList.id,ConstraintSet.LEFT,binding.middleDivider.id,ConstraintSet.RIGHT,0)
        set.connect(binding.updateList.id,ConstraintSet.RIGHT,binding.rightDivider.id,ConstraintSet.LEFT,0)

        set.connect(binding.searchButton.id,ConstraintSet.TOP,binding.layout.id,ConstraintSet.TOP,unit/2)
        set.connect(binding.searchButton.id,ConstraintSet.LEFT,binding.rightDivider.id,ConstraintSet.RIGHT,0)
        set.connect(binding.searchButton.id,ConstraintSet.RIGHT,binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.searchEditText.id,ConstraintSet.TOP,binding.searchButton.id,ConstraintSet.BOTTOM,0)
        set.connect(binding.searchEditText.id,ConstraintSet.LEFT, binding.layout.id,ConstraintSet.LEFT,0)
        set.connect(binding.searchEditText.id,ConstraintSet.RIGHT, binding.layout.id,ConstraintSet.RIGHT,0)

        set.connect(binding.progressBar.id, ConstraintSet.LEFT, binding.infoText.id,ConstraintSet.LEFT,0)
        set.connect(binding.progressBar.id, ConstraintSet.RIGHT, binding.infoText.id,ConstraintSet.RIGHT,0)
        set.connect(binding.progressBar.id, ConstraintSet.TOP, binding.infoText.id,ConstraintSet.BOTTOM,unit)

        set.applyTo(binding.layout)
    }

    private fun setDrawables() {
        binding.layout.background = BackgroundColorDrawable(requireContext())

        binding.logout.background = ButtonBackground(requireContext())
        binding.updateList.background = ButtonBackground(requireContext())
        binding.searchButton.background = ButtonBackground(requireContext())

        binding.logout.setImageDrawable(LogoutDrawable(requireContext()))

        binding.moves.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.infoText.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.noUsers.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))
        binding.searchEditText.setTextColor(ContextCompat.getColor(requireContext(), Theme(requireContext()).getAccentColor()))

        binding.searchEditText.background = EditTextBackground(requireContext())
        binding.progressBar.background = ProgressBarBackgroundDrawable(requireContext())

    }

    private fun setSizes() {
        val buttonSize = (1.4*unit).toInt()
        binding.moves.layoutParams = ConstraintLayout.LayoutParams(buttonSize,buttonSize)
        binding.searchButton.layoutParams = ConstraintLayout.LayoutParams(buttonSize,buttonSize)
        binding.updateList.layoutParams = ConstraintLayout.LayoutParams(buttonSize,buttonSize)
        binding.logout.layoutParams = ConstraintLayout.LayoutParams(buttonSize,buttonSize)
        binding.moves.setTextSize(TypedValue.COMPLEX_UNIT_PX,unit.toFloat())
        binding.infoText.setTextSize(TypedValue.COMPLEX_UNIT_PX,unit/2.toFloat())
        binding.noUsers.setTextSize(TypedValue.COMPLEX_UNIT_PX,unit/2.toFloat())
        binding.searchEditText.layoutParams = ConstraintLayout.LayoutParams(width,buttonSize)
        binding.searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX,unit/2.toFloat())
        binding.progressBar.layoutParams = ConstraintLayout.LayoutParams(2*unit,2*unit)
    }

    override fun onResume() {
        super.onResume()
        if(currentUser!=null) {
            prepareUIAndCheckUserInFirebase()
        }
        else{
            findNavController().navigateUp()
        }
    }

    override fun onPause() {
        super.onPause()
        invitationsDbRef?.removeEventListener(invitationsListener!!)
        invitationsDbRef= null
        listHandler.removeCallbacksAndMessages(null)
    }

    private fun displayList():Runnable = Runnable {
        if(listReady.value!!){
            listHandler.removeCallbacksAndMessages(null)
            adapter.submitList(getUserList(binding.searchEditText.text.toString()))
        }else{
            listHandler.postDelayed(displayList(),1000)
        }
    }

    private fun getUserList(string:String?):List<com.tt.ox.helpers.FirebaseUser>{
        return if(searching.value!!){
            if(string!=null){
                val newList = userList.filter { user -> user.userName.contains(string,false) }
                _noUsers.value = newList.isEmpty()
                newList
            }else{
                _noUsers.value = userList.isEmpty()
                userList
            }
        }else{
            _noUsers.value = userList.isEmpty()
            userList
        }
    }

    private fun prepareUIAndCheckUserInFirebase() {

        val dbRef = dbRefUsers.child(currentUser!!.uid)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    createUser(currentUser!!.uid)
                }
                else{
                    updateTimeStamp(snapshot)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun updateTimeStamp(snapshot: DataSnapshot) {
        val user = snapshot.getValue(com.tt.ox.helpers.FirebaseUser::class.java)
        val currentDate = DateUtils().getCurrentDate()
        if(user!=null){
            if(user.timestamp==currentDate){
                user.unixTime = System.currentTimeMillis()
                user.userName = SharedPreferences.readPlayerName(requireContext())
                val dbRefUser = dbRefUsers.child(user.id.toString())
                dbRefUser.setValue(user)

                startFragment()
                //do nothing
            }else{
                dbRefRanking.child(user.timestamp.toString()).child(user.id.toString()).removeValue()
                user.timestamp = currentDate
                user.unixTime = System.currentTimeMillis()
                user.userName = SharedPreferences.readPlayerName(requireContext())
                val dbRefUser = dbRefUsers.child(user.id.toString())
                dbRefUser.setValue(user)
                val dbRefRanking = dbRefRanking.child(currentDate.toString()).child(user.id.toString())
                val newRankingUser = FirebaseUserId()
                newRankingUser.userId = user.id
                dbRefRanking.setValue(newRankingUser)

                startFragment()
            }
        }
    }

    private fun startFragment(){
        checkIfFragmentAttached {
            if (_moves.value!! <= 0) {
                displayAddMovesAlertDialog()
            } else {
                dialogMoves?.dismiss()
                checkInvitations()
                adapter = OnlineListAdapter(requireContext(), currentUser!!.uid) {
                    playButtonClick()
                    sendInvitation(it)
                }
                binding.recyclerView.adapter = adapter
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                displayList().run()
            }
        }
    }


    private fun checkIfFragmentAttached(operation: Context.() -> Unit){
        if(isAdded && context != null){
            operation(requireContext())
        }
    }


    private fun prepareList(){
        _stage.value = DATES
        idList.clear()
        val dates = DateUtils().getActive()
        datesListSize = dates.size
        currentPosition = 0
        readListFromFirebaseNew(dates)
    }

    private fun readListFromFirebaseNew(dates: MutableList<Int>){
        if(currentPosition<datesListSize){
            val percent:Double = (currentPosition+1).toDouble()/datesListSize.toDouble()
            try {
                binding?.progressBar?.setImageDrawable(ProgressBarDrawable(requireContext(),percent, COLOR_RED))
            }catch (e:Exception){
                //do nothing
            }

            val dbRef = dbRefRanking.child(dates[currentPosition].toString())
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        for(id in snapshot.children){
                            val tId = id.getValue(FirebaseUserId::class.java)
                            idList.add(tId!!)
                        }
                    }
                    currentPosition+=1
                    readListFromFirebaseNew(dates)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }else{
            readUserListFromFirebaseNew()
        }
    }

    private fun readUserListFromFirebaseNew(){
        userList.clear()
        loopCounter = 0
        _stage.value = FILTERING
        val filteredIdList = idList.filter { id -> id.userId != currentUser!!.uid }
        listSize = filteredIdList.size
        currentUserPosition = 0
        _stage.value = USERS
        readUsersFromFirebaseNew(filteredIdList)
    }

    private fun readUsersFromFirebaseNew(filteredIdList: List<FirebaseUserId>){
        if(currentUserPosition<listSize){
            val percent:Double = (currentUserPosition+1).toDouble()/listSize.toDouble()
            try {
                binding?.progressBar?.setImageDrawable(ProgressBarDrawable(requireContext(),percent,
                    COLOR_BLUE))
            }catch (e:Exception){
                //do nothing
            }
            val dbRef = dbRefUsers.child(filteredIdList[currentUserPosition].userId!!)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.getValue(com.tt.ox.helpers.FirebaseUser::class.java)
                        userList.add(user!!)
                    }
                    currentUserPosition+=1
                    readUsersFromFirebaseNew(filteredIdList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
        else{
            _listReady.value = true
        }
    }


    private fun sendInvitation(user:com.tt.ox.helpers.FirebaseUser){
        val dbRequests = dbRefRequest.child(user.id.toString())
        dbRequests.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    val time = System.currentTimeMillis()
                    val request = FirebaseRequests()
                    request.status = RECEIVED
                    request.opponentId = currentUser!!.uid
                    request.timestamp = time
                    request.battle = currentUser!!.uid+user.id
                    dbRequests.setValue(request)

                    val dbRef = dbRefRequest.child(currentUser!!.uid)
                    val requestMy = FirebaseRequests()
                    requestMy.status = SEND
                    requestMy.opponentId = user.id
                    requestMy.timestamp = time
                    requestMy.battle = currentUser!!.uid+user.id
                    dbRef.setValue(requestMy)
                }else{
                    val invitation = snapshot.getValue(FirebaseRequests::class.java)
                    invitation?.let {
                        if(it.status== AVAILABLE){
                            val time = System.currentTimeMillis()
                            val request = FirebaseRequests()
                            request.status = RECEIVED
                            request.opponentId = currentUser!!.uid
                            request.timestamp = time
                            request.battle = currentUser!!.uid+user.id
                            dbRequests.setValue(request)

                            val dbRef = dbRefRequest.child(currentUser!!.uid)
                            val requestMy = FirebaseRequests()
                            requestMy.status = SEND
                            requestMy.opponentId = user.id
                            requestMy.timestamp = time
                            requestMy.battle = currentUser!!.uid+user.id
                            dbRef.setValue(requestMy)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun checkInvitations() {
        invitationsDbRef = dbRefRequest.child(currentUser!!.uid)
        invitationsListener = invitationsDbRef!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.let {
                    val invitation = it.getValue(FirebaseRequests::class.java)
                    invitation?.let {inv ->
                        when(inv.status){
                            SEND -> displayWaitingAlertDialog(inv)
                            RECEIVED -> displayReceivedAlertDialog(inv)
                            REJECTED -> displayRejectedAlertDialog(inv)
                            ACCEPTED -> displayAcceptedAlertDialog(inv)
                            PLAY -> moveToOnlineBattleFragment()
                            else -> dialogInvitation?.dismiss()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun moveToOnlineBattleFragment() {
        val temp = _moves.value!!-1
        _moves.value = temp
        SharedPreferences.saveOnlineMoves(requireContext(),_moves.value!!)
        dialogInvitation?.dismiss()
        dialogInvitation=null
        invitationsDbRef?.removeEventListener(invitationsListener!!)
        invitationsDbRef= null
        val action = OnlineChooseOpponentFragmentDirections.actionOnlineChooseOpponentFragmentToOnlineBattle()
        findNavController().navigate(action)
    }

    private fun displayAcceptedAlertDialog(
        requests: FirebaseRequests) {
        dialogInvitation?.dismiss()
        dialogInvitation = AlertDialogWaiting(
            requireContext(),
            layoutInflater,
            requests.opponentId!!,
            requests,
            "Invitation accepted by",
            false,
            showTimeInvitation = false,
            positiveButtonText = "",
            negativeButtonText = "PLAY",
            endTimeCallBack = {
                //do nothing
            }, negativeButtonPressed = {
                playButtonClick()
                dialogInvitation?.dismiss()
                val dbRef = dbRefRequest.child(currentUser!!.uid).child("status")
                dbRef.setValue(PLAY)
            }){
            //do nothing
        }.create()

        dialogInvitation?.show()
    }

    private fun displayRejectedAlertDialog(
        request: FirebaseRequests){
        dialogInvitation?.dismiss()
        dialogInvitation = AlertDialogWaiting(
            requireContext(),
            layoutInflater,
            request.opponentId!!,
            request,
            "Invitation rejected by",
            positiveButtonEnabled = false,
            showTimeInvitation = false,
            positiveButtonText = "",
            negativeButtonText = "DISMISS",
            endTimeCallBack = {
                //do nothing
            },
            negativeButtonPressed = {
                playButtonClick()
                val dbRef = dbRefRequest.child(currentUser!!.uid)
                val request1 = FirebaseRequests()
                request1.status = AVAILABLE
                dbRef.setValue(request1)
                dialogInvitation?.dismiss()
            }
        ){
            //do nothing
        }.create()
        dialogInvitation?.show()
    }

    private fun displayReceivedAlertDialog(request: FirebaseRequests) {
        dialogInvitation = AlertDialogWaiting(
            requireContext(),
            layoutInflater,
            request.opponentId!!,
            request,
            "Invitation received from",
            positiveButtonEnabled = true,
            showTimeInvitation = true,
            positiveButtonText = "ACCEPT",
            negativeButtonText = "REJECT",
            endTimeCallBack = {
                val dbRef = dbRefRequest.child(currentUser!!.uid)
                val request1 = FirebaseRequests()
                request1.status = AVAILABLE
                dbRef.setValue(request1)
            }, negativeButtonPressed = {
                playButtonClick()
                val dbRef = dbRefRequest.child(currentUser!!.uid)
                val request1 = FirebaseRequests()
                request1.status = AVAILABLE
                dbRef.setValue(request1)

                val dbRef2 = dbRefRequest.child(request.opponentId!!).child("status")
                dbRef2.setValue(REJECTED)
                dialogInvitation?.dismiss()
            }
        ){
            playButtonClick()
            val dbRefB = dbRefBattle.child(request.battle!!)
            val battle = FirebaseBattle()
            battle.battleId = request.battle!!
            battle.timestamp = System.currentTimeMillis()
            val random = Random.nextBoolean()
            val startingPerson = if(random) currentUser!!.uid else request.opponentId!!
            battle.turn = startingPerson
            dbRefB.setValue(battle)

            val dbRef1 = dbRefRequest.child(request.opponentId!!).child("status")
            dbRef1.setValue(ACCEPTED)
            val dbRef2 = dbRefRequest.child(currentUser!!.uid).child("status")
            dbRef2.setValue(PLAY)
            dialogInvitation?.dismiss()
        }.create()

        dialogInvitation?.show()
    }

    private fun displayWaitingAlertDialog(
        request: FirebaseRequests
    ) {

        dialogInvitation = AlertDialogWaiting(
            requireContext(),
            layoutInflater,
            request.opponentId!!,
            request,
            "Invitation sent to",
            positiveButtonEnabled = false,
            showTimeInvitation = true,
            positiveButtonText = "OK",
            negativeButtonText = "DISMISS",
            endTimeCallBack = {val dbRef = dbRefRequest.child(currentUser!!.uid)
                val request1 = FirebaseRequests()
                request1.status = AVAILABLE
                dbRef.setValue(request1)
            },
            negativeButtonPressed = {
                playButtonClick()
                val dbRefMy = dbRefRequest.child(currentUser!!.uid)
                val requestMy = FirebaseRequests()
                requestMy.status = AVAILABLE
                dbRefMy.setValue(requestMy)
                val dbRefOpponent = dbRefRequest.child(request.opponentId!!)
                val requestOpponent = FirebaseRequests()
                requestOpponent.status = AVAILABLE
                dbRefOpponent.setValue(requestOpponent)
                dialogInvitation?.dismiss()
            }){
            // do nothing
        }.create()

        dialogInvitation?.show()

    }

    private fun displayAddMovesAlertDialog() {
        dialogMoves = AlertDialogAddMoves(
            requireContext(),
            layoutInflater,
            {playButtonClick()
                dialogMoves?.dismiss()
                findNavController().navigateUp()
            }){
            playButtonClick()
            if(mRewardedAd!=null){
                dialogMoves?.dismiss()
                showAdvertReward()
            }else{
                Toast.makeText(requireContext(),"POOR INTERNET", Toast.LENGTH_SHORT).show()
            }
        }.create()
        dialogMoves?.show()
    }

    private fun showAdvertReward(){
        mRewardedAd?.fullScreenContentCallback = object  : FullScreenContentCallback(){
            override fun onAdClicked() {
                super.onAdClicked()
                //do nothing
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                //do nothing
                dialogInvitation?.dismiss()
                dialogInvitation=null
                invitationsDbRef?.removeEventListener(invitationsListener!!)
                invitationsDbRef= null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                super.onAdFailedToShowFullScreenContent(p0)
                // do nothing
            }

            override fun onAdImpression() {
                super.onAdImpression()
                // do nothing
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                dialogInvitation?.dismiss()
                dialogInvitation=null
                invitationsDbRef?.removeEventListener(invitationsListener!!)
                invitationsDbRef= null
                prepareRewardedAd()
            }
        }

        if(mRewardedAd != null){
            mRewardedAd?.show(requireActivity()){
            _moves.value = MOVES
            SharedPreferences.saveOnlineMoves(requireContext(),_moves.value!!)
            checkInvitations()
            adapter = OnlineListAdapter(requireContext(),currentUser!!.uid){
                sendInvitation(it)
            }
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
            displayList().run()
            }
        }
    }

    private fun prepareRewardedAd(){
        val adRequest = com.google.android.gms.ads.AdRequest.Builder().build()
        val adId = if(TEST) getString(R.string.testRewardedAd) else getString(R.string.onlinePlayerRewardedAd)
        RewardedAd.load(requireContext(),adId,adRequest, object : RewardedAdLoadCallback(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                mRewardedAd = null
            }

            override fun onAdLoaded(p0: RewardedAd) {
                super.onAdLoaded(p0)
                mRewardedAd = p0
            }
        })
    }

    private fun createUser(userId: String) {
        val currentDate = DateUtils().getCurrentDate()
        val newUser = com.tt.ox.helpers.FirebaseUser()
        newUser.id = userId
        newUser.userName = SharedPreferences.readPlayerName(requireContext())
        newUser.timestamp = currentDate
        newUser.unixTime = System.currentTimeMillis()

        // create user in Users
        val dbRefUser = dbRefUsers.child(userId)
        dbRefUser.setValue(newUser)

        // create ranking list with timestamp
        val dbRefRanking = dbRefRanking.child(currentDate.toString()).child(userId)
        val newRankingUser = FirebaseUserId()
        newRankingUser.userId = userId
        dbRefRanking.setValue(newRankingUser)

        val dbRequests = dbRefRequest.child(userId)
        val request = FirebaseRequests()
        dbRequests.setValue(request)

        startFragment()
    }


}