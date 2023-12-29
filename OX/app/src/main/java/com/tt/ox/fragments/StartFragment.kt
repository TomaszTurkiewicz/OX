package com.tt.ox.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tt.ox.OXApplication
import com.tt.ox.R
import com.tt.ox.alertDialogs.AlertDialogChangeName
import com.tt.ox.alertDialogs.AlertDialogLogin
import com.tt.ox.databinding.FragmentStartBinding
import com.tt.ox.drawables.BackgroundColorDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.MultiPlayerButtonDrawable
import com.tt.ox.drawables.OnlinePlayerButtonDrawable
import com.tt.ox.drawables.SettingButtonDrawable
import com.tt.ox.drawables.SinglePlayerButtonDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences
import com.tt.ox.viewModel.GameViewModel
import com.tt.ox.viewModel.GameViewModelFactory


class StartFragment : FragmentCoroutine() {

    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!

    private var unit = 0
    private var windowHeight = 0
    private var width = 0

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var dialogLogin:AlertDialog? = null

    private val gameViewModel: GameViewModel by activityViewModels {
        GameViewModelFactory(
            (activity?.application as OXApplication).database.opponentDao()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unit = ScreenMetricsCompat().getUnit(requireContext())
        windowHeight = ScreenMetricsCompat().getWindowHeight(requireContext())
        width = (ScreenMetricsCompat().getWindowWidth(requireContext())*0.9).toInt()

        auth = Firebase.auth

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
                displayLoginAlertDialogLogin()

            }
        }

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

        val playerNameSetUp = SharedPreferences.checkIfPlayerNameSetUp(requireContext())
        if(playerNameSetUp){
            clicks()
        }
        else{
            createAlertDialog()
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
                        val action = StartFragmentDirections.actionStartFragmentToOnlineChooseOpponentFragment()
                        findNavController().navigate(action)
                        // create user if not exists or compare if exists
                    }
                }
            }
    }

    private fun displayLoginAlertDialogLogin() {
        dialogLogin = AlertDialogLogin(
            requireContext(),
            layoutInflater,
            "LOGIN",
            "To play online you have to be logged in. Do You want login?",
            {
                playButtonClick()
                val signInIntent = googleSignInClient.signInIntent
                resultLauncher.launch(signInIntent)
                dialogLogin?.dismiss()
            },
            {
                playButtonClick()
                dialogLogin?.dismiss()
            }
        ).create()
        dialogLogin?.show()
    }

    private fun createAlertDialog() {
        var alertDialogSetName:AlertDialog? = null
        alertDialogSetName = AlertDialogChangeName(
            requireContext(),
            layoutInflater,
            cancelButtonEnable = false,
            readNameFromMemory = false,
            title = "What's Your name?",
            message = "Type here your name. Between 2 and 14 characters",
            dismissClick = {

            },
            saveClick = {
                playButtonClick()
                gameViewModel.addNewOpponent("PHONE")
                SharedPreferences.saveMainPlayer(requireContext(),it)
                clicks()
                alertDialogSetName?.dismiss()
            }
        ).create()
        alertDialogSetName.show()
    }

    private fun clicks() {
        binding.let {
            it.singlePlayerButton.setOnClickListener {
                playButtonClick()
                val action = StartFragmentDirections.actionStartFragmentToSinglePlayerFragment()
                findNavController().navigate(action)
            }
            it.multiPlayerButton.setOnClickListener {
                playButtonClick()
                val action = StartFragmentDirections.actionStartFragmentToChooseOpponentFragment()
                findNavController().navigate(action)
            }
            it.optionsButton.setOnClickListener {
                playButtonClick()
                val action = StartFragmentDirections.actionStartFragmentToOptionsFragment()
                findNavController().navigate(action)
            }
            it.onlinePlayerButton.setOnClickListener {
                playButtonClick()
                val currentUser = auth.currentUser
                if(currentUser!=null){
                    val action = StartFragmentDirections.actionStartFragmentToOnlineChooseOpponentFragment()
                    findNavController().navigate(action)
                }else{
                    displayLoginAlertDialogLogin()
                }
            }
            it.otherGamesButton.setOnClickListener {
                val link = getString(R.string.other_games_link)
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            // todo check for new apps!!!
            // todo change icon for other apps!!!
        }
    }



    private fun prepareUI() {

        binding.multiPlayerButton.layoutParams = ConstraintLayout.LayoutParams(8*unit,3*unit)
        binding.singlePlayerButton.layoutParams = ConstraintLayout.LayoutParams(8*unit,3*unit)
        binding.onlinePlayerButton.layoutParams = ConstraintLayout.LayoutParams(8*unit,3*unit)
        binding.optionsButton.layoutParams = ConstraintLayout.LayoutParams(2*unit,2*unit)
        binding.otherGamesButton.layoutParams = ConstraintLayout.LayoutParams(2*unit,2*unit)

        binding.fragmentStartLayout.background = BackgroundColorDrawable(requireContext())
        binding.singlePlayerButton.background = ButtonBackground(requireContext())
        binding.multiPlayerButton.background = ButtonBackground(requireContext())
        binding.onlinePlayerButton.background = ButtonBackground(requireContext())
        binding.optionsButton.background = ButtonBackground(requireContext())
        binding.otherGamesButton.background = ButtonBackground(requireContext())

        binding.optionsButton.setImageDrawable(SettingButtonDrawable(requireContext()))
        binding.otherGamesButton.setImageDrawable(SettingButtonDrawable(requireContext()))
        binding.singlePlayerButton.setImageDrawable(SinglePlayerButtonDrawable(requireContext()))
        binding.multiPlayerButton.setImageDrawable(MultiPlayerButtonDrawable(requireContext()))
        binding.onlinePlayerButton.setImageDrawable(OnlinePlayerButtonDrawable(requireContext()))

        setConstraints()
    }

    private fun setConstraints(){
        val heightWithoutSettings = windowHeight-2.5*unit
        val offset = (heightWithoutSettings-9*unit)/4
        val set = ConstraintSet()
        set.clone(binding.fragmentStartLayout)

        set.connect(binding.optionsButton.id, ConstraintSet.TOP, binding.fragmentStartLayout.id, ConstraintSet.TOP,unit/2)
        set.connect(binding.optionsButton.id, ConstraintSet.RIGHT, binding.fragmentStartLayout.id, ConstraintSet.RIGHT,unit/2)

        set.connect(binding.otherGamesButton.id, ConstraintSet.TOP, binding.fragmentStartLayout.id, ConstraintSet.TOP,unit/2)
        set.connect(binding.otherGamesButton.id, ConstraintSet.LEFT, binding.fragmentStartLayout.id, ConstraintSet.LEFT,unit/2)

        set.connect(binding.singlePlayerButton.id,ConstraintSet.LEFT,binding.fragmentStartLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.singlePlayerButton.id,ConstraintSet.RIGHT,binding.fragmentStartLayout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.singlePlayerButton.id,ConstraintSet.TOP,binding.optionsButton.id,ConstraintSet.TOP,
            offset.toInt()
        )

        set.connect(binding.multiPlayerButton.id,ConstraintSet.LEFT,binding.fragmentStartLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.multiPlayerButton.id,ConstraintSet.RIGHT,binding.fragmentStartLayout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.multiPlayerButton.id,ConstraintSet.TOP,binding.singlePlayerButton.id,ConstraintSet.BOTTOM,
            offset.toInt()
        )

        set.connect(binding.onlinePlayerButton.id,ConstraintSet.LEFT,binding.fragmentStartLayout.id,ConstraintSet.LEFT,0)
        set.connect(binding.onlinePlayerButton.id,ConstraintSet.RIGHT,binding.fragmentStartLayout.id,ConstraintSet.RIGHT,0)
        set.connect(binding.onlinePlayerButton.id,ConstraintSet.TOP,binding.multiPlayerButton.id,ConstraintSet.BOTTOM,
            offset.toInt()
        )

        set.applyTo(binding.fragmentStartLayout)
    }
}