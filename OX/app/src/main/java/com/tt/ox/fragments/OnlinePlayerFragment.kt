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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tt.ox.R
import com.tt.ox.databinding.AlertDialogLogInBinding
import com.tt.ox.databinding.FragmentOnlinePlayerBinding
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable
import com.tt.ox.helpers.FirebaseUtils
import com.tt.ox.helpers.ScreenMetricsCompat

class OnlinePlayerFragment : Fragment() {

    private var _binding: FragmentOnlinePlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private var width = 0
    private var currentUser: FirebaseUser? = null


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

        FirebaseUtils().checkUser(currentUser!!.uid)
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
        setAlertDialogDrawables(alertDialog)
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

    private fun setAlertDialogDrawables(alertDialog: AlertDialogLogInBinding) {
        alertDialog.loginButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),"LOGIN"))
        alertDialog.cancelButton.setImageDrawable(ButtonWithTextDrawable(requireContext(),"CANCEL"))
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

/* todo
*   check if firebase user not null
*   if null ask for login
*   if not null display active users
*   when logging in create database if not created
*   time stamp and saving by date
 */