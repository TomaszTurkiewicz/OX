package com.tt.ox.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.R
import com.tt.ox.adapters.OnlineListAdapter
import com.tt.ox.databinding.AlertDialogLogInBinding
import com.tt.ox.databinding.FragmentOnlinePlayerBinding
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable
import com.tt.ox.helpers.ACCEPTED
import com.tt.ox.helpers.AVAILABLE
import com.tt.ox.helpers.AlertDialogWaiting
import com.tt.ox.helpers.DateUtils
import com.tt.ox.helpers.FirebaseRequests
import com.tt.ox.helpers.FirebaseUserId
import com.tt.ox.helpers.PLAY
import com.tt.ox.helpers.RECEIVED
import com.tt.ox.helpers.REJECTED
import com.tt.ox.helpers.SEND
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences

class OnlinePlayerFragment : Fragment() {

    private var _binding: FragmentOnlinePlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var width = 0
    private var currentUser: FirebaseUser? = null
    private val idList:MutableList<FirebaseUserId> = mutableListOf()
    private lateinit var adapter: OnlineListAdapter
    private val userList:MutableList<com.tt.ox.helpers.FirebaseUser> = mutableListOf()
    private var loopCounter = 0
    private var listSize = 0
    private var datesListSize = 0
    private var currentPosition = 0
    private var currentUserPosition = 0
    private val dbRefRequest = Firebase.database.getReference("Requests")
    private val dbRefUsers = Firebase.database.getReference("Users")
    private val dbRefRanking = Firebase.database.getReference("Ranking")
    private val dbRefBattle = Firebase.database.getReference("Battle")
    private var dialogInvitation:AlertDialog? = null
    private var dialogMoves:AlertDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("329182313552-1nrhrejp03ndlhnvff60leaj2p87sk5p.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
            if(result.resultCode == Activity.RESULT_OK){
                val data: Intent? = result.data
                doSomething(data)
            }
            else if(result.resultCode == Activity.RESULT_CANCELED){
                displayLoginAlertDialog()
            }
        }
    }

    private fun doSomething(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account.idToken)
        }catch (e: ApiException){
            Log.w("TAG","Google sign in failed", e)
        }
    }

    private fun firebaseAuthWithGoogle(idToken:String?){
        val credentials = GoogleAuthProvider.getCredential(idToken,null)
        auth.signInWithCredential(credentials)
            .addOnCompleteListener(requireActivity()){ task ->
                if(task.isSuccessful){
                    val user = Firebase.auth.currentUser
                    if(user!=null){
                        currentUser = user
                        prepareUIAndCheckUserInFirebase()
                        // create user if not exists or compare if exists
                    }
                }
            }
    }

    private fun prepareUIAndCheckUserInFirebase() {
        binding.logout.setOnClickListener {
            auth.signOut()
            findNavController().navigateUp()
        }



        val dbRef = dbRefUsers.child(currentUser!!.uid)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
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

        val dbRefR = dbRefRequest.child(currentUser!!.uid)
        dbRefR.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                // todo finish this first retrieve user list from requests!!!!
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
                val dbRefUser = dbRefUsers.child(user.id.toString())
                dbRefUser.setValue(user)

                startFragment()
                //do nothing
            }else{
                dbRefRanking.child(user.timestamp.toString()).child(user.id.toString()).removeValue()
                user.timestamp = currentDate
                user.unixTime = System.currentTimeMillis()
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
        val movesDbRef = dbRefUsers.child(currentUser!!.uid).child("moves")
        movesDbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    displayAddMovesAlertDialog()
                }else{
                    val moves = snapshot.getValue(Int::class.java)
                    if(moves!!<=0){
                        displayAddMovesAlertDialog()
                    }else{
                        dialogMoves?.dismiss()
                        checkInvitations()
                        prepareUserList()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun displayAddMovesAlertDialog() {
        //todo!!!
    }

    private fun checkInvitations() {
        val invitationsDbRef = dbRefRequest.child(currentUser!!.uid)
        invitationsDbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot?.let {
                    val invitation = it.getValue(FirebaseRequests::class.java)
                    invitation?.let {inv ->
                        when(inv.status){
                            SEND -> displayWaitingAlertDialog(inv)
                            RECEIVED -> displayReceivedAlertDialog(inv)
                            REJECTED -> displayRejectedAlertDialog(inv)
                            ACCEPTED -> displayAcceptedAlertDialog(inv)
                            PLAY -> moveToOnlineBattleFragment()
                            //TODO anything different clear my request
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
        dialogInvitation?.dismiss()
        val action = OnlinePlayerFragmentDirections.actionOnlinePlayerFragmentToOnlineBattle()
        findNavController().navigate(action)
    }


    private fun prepareUserList() {
        idList.clear()
        val dates = DateUtils().getLastMonth()
        datesListSize = dates.size
        currentPosition = 0
        readListFromFirebase(dates)

    }

    private fun readListFromFirebase(dates: MutableList<Int>) {
        if(currentPosition<datesListSize){
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
                readListFromFirebase(dates)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        }
        else{
            setAdapter()
        }
    }

    private fun readUsersFromFirebase(filteredIdList: List<FirebaseUserId>) {
        if(currentUserPosition<listSize){
            val dbRef = dbRefUsers.child(filteredIdList[currentUserPosition].userId!!)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val user = snapshot.getValue(com.tt.ox.helpers.FirebaseUser::class.java)
                        userList.add(user!!)
                    }
                    currentUserPosition+=1
                    readUsersFromFirebase(filteredIdList)
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        }
        else{
            // todo check if list not empty (if empty show "No users active last month")
            adapter.submitList(userList)

        }
    }



    private fun setAdapter() {
        adapter = OnlineListAdapter(requireContext()){
            sendInvitation(it)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        userList.clear()
        loopCounter = 0
        val filteredIdList = idList.filter { id -> id.userId != currentUser!!.uid }
        listSize = filteredIdList.size
        currentUserPosition = 0
        readUsersFromFirebase(filteredIdList)
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

    private fun displayAcceptedAlertDialog(requests: FirebaseRequests) {
        dialogInvitation?.dismiss()
        dialogInvitation = AlertDialogWaiting(
            requireContext(),
            layoutInflater,
            requests.opponentId!!,
            requests,
            "Invitation accepted by",
            false,
            showTime = false,
            positiveButtonText = "",
            negativeButtonText = "PLAY",
            endTimeCallBack = {
                //do nothing
            }, negativeButtonPressed = {
                val dbRef = dbRefRequest.child(currentUser!!.uid).child("status")
                dbRef.setValue(PLAY)
            }){
            //do nothing
        }.create()

        dialogInvitation?.show()
    }

    private fun displayRejectedAlertDialog(request: FirebaseRequests){
        dialogInvitation?.dismiss()
        dialogInvitation = AlertDialogWaiting(
            requireContext(),
            layoutInflater,
            request.opponentId!!,
            request,
            "Invitation rejected by",
            positiveButtonEnabled = false,
            showTime = false,
            positiveButtonText = "",
            negativeButtonText = "DISMISS",
            endTimeCallBack = {
                //do nothing
            },
            negativeButtonPressed = {
                val dbRef = dbRefRequest.child(currentUser!!.uid)
                val request1 = FirebaseRequests()
                request1.status = AVAILABLE
                dbRef.setValue(request1)
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
            showTime = true,
            positiveButtonText = "ACCEPT",
            negativeButtonText = "REJECT",
            endTimeCallBack = {
                val dbRef = dbRefRequest.child(currentUser!!.uid)
                val request1 = FirebaseRequests()
                request1.status = AVAILABLE
                dbRef.setValue(request1)
            }, negativeButtonPressed = {
                val dbRef = dbRefRequest.child(currentUser!!.uid)
                val request1 = FirebaseRequests()
                request1.status = AVAILABLE
                dbRef.setValue(request1)

                val dbRef2 = dbRefRequest.child(request.opponentId!!).child("status")
                dbRef2.setValue(REJECTED)
            }
        ){
            val dbRefB = dbRefBattle.child(request.battle!!)
            dbRefB.setValue(true)
            val dbRef1 = dbRefRequest.child(request.opponentId!!).child("status")
            dbRef1.setValue(ACCEPTED)
            val dbRef2 = dbRefRequest.child(currentUser!!.uid).child("status")
            dbRef2.setValue(PLAY)
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
            showTime = true,
            positiveButtonText = "OK",
            negativeButtonText = "DISMISS",
            endTimeCallBack = {val dbRef = dbRefRequest.child(currentUser!!.uid)
                val request1 = FirebaseRequests()
                request1.status = AVAILABLE
                dbRef.setValue(request1)
            },
            negativeButtonPressed = {
                val dbRefMy = dbRefRequest.child(currentUser!!.uid)
                val requestMy = FirebaseRequests()
                requestMy.status = AVAILABLE
                dbRefMy.setValue(requestMy)
                val dbRefOpponent = dbRefRequest.child(request.opponentId!!)
                val requestOpponent = FirebaseRequests()
                requestOpponent.status = AVAILABLE
                dbRefOpponent.setValue(requestOpponent)
            }){
          // do nothing
        }.create()

        dialogInvitation?.show()

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
        dbRequests.setValue(true)

        startFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnlinePlayerBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentUser = auth.currentUser
        if(currentUser==null){
            displayLoginAlertDialog()
        }
        else{
            prepareUIAndCheckUserInFirebase()
        }
    }

    private fun displayLoginAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = AlertDialogLogInBinding.inflate(layoutInflater)
        displayAlertDialogUI(alertDialog)
        builder.setView(alertDialog.root)

        val dialog = builder.create()
        dialog.setCancelable(false)

        alertDialog.loginButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            resultLauncher.launch(signInIntent)
            dialog.dismiss()
        }

        alertDialog.cancelButton.setOnClickListener {
            dialog.dismiss()
            findNavController().navigateUp()
        }

        dialog.show()
    }

    private fun displayAlertDialogUI(alertDialog: AlertDialogLogInBinding) {
        alertDialog.title.text = "LOGIN"
        alertDialog.message.text = "To play online you have to be logged in. Do You want login?"
        setAlertDialogColors(alertDialog)
        setAlertDialogSizes(alertDialog)
        setAlertDialogDrawables(alertDialog,"LOGIN", "CANCEL")
        setAlertDialogConstraints(alertDialog)
    }

    private fun setAlertDialogConstraints(alertDialog: AlertDialogLogInBinding) {
        val set = ConstraintSet()
        set.clone(alertDialog.alertDialogLogIn)

        set.connect(alertDialog.title.id,
            ConstraintSet.TOP,alertDialog.alertDialogLogIn.id,
            ConstraintSet.TOP)
        set.connect(alertDialog.title.id,
            ConstraintSet.LEFT,alertDialog.alertDialogLogIn.id,
            ConstraintSet.LEFT)
        set.connect(alertDialog.title.id,
            ConstraintSet.RIGHT,alertDialog.alertDialogLogIn.id,
            ConstraintSet.RIGHT)

        set.connect(alertDialog.message.id,
            ConstraintSet.TOP,alertDialog.title.id,
            ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.message.id,
            ConstraintSet.LEFT,alertDialog.alertDialogLogIn.id,
            ConstraintSet.LEFT)
        set.connect(alertDialog.message.id,
            ConstraintSet.RIGHT,alertDialog.alertDialogLogIn.id,
            ConstraintSet.RIGHT)

        set.connect(alertDialog.cancelButton.id,
            ConstraintSet.LEFT,alertDialog.alertDialogLogIn.id,
            ConstraintSet.LEFT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.cancelButton.id,
            ConstraintSet.TOP,alertDialog.message.id,
            ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.cancelButton.id,
            ConstraintSet.BOTTOM,alertDialog.alertDialogLogIn.id,
            ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )

        set.connect(alertDialog.loginButton.id,
            ConstraintSet.RIGHT,alertDialog.alertDialogLogIn.id,
            ConstraintSet.RIGHT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.loginButton.id,
            ConstraintSet.TOP,alertDialog.message.id,
            ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.loginButton.id,
            ConstraintSet.BOTTOM,alertDialog.alertDialogLogIn.id,
            ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )


        set.applyTo(alertDialog.alertDialogLogIn)
    }

    private fun setAlertDialogDrawables(alertDialog: AlertDialogLogInBinding,positive:String,negative:String) {
        alertDialog.loginButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),positive))
        alertDialog.cancelButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),negative))
        alertDialog.loginButton.background = ButtonBackground(requireContext())
        alertDialog.cancelButton.background = ButtonBackground(requireContext())
    }

    private fun setAlertDialogSizes(alertDialog: AlertDialogLogInBinding) {
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.1f)
        alertDialog.message.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.05f)
        alertDialog.message.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)
        alertDialog.loginButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
        alertDialog.cancelButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
    }

    private fun setAlertDialogColors(alertDialog: AlertDialogLogInBinding) {
        alertDialog.title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        alertDialog.message.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

    }
}

