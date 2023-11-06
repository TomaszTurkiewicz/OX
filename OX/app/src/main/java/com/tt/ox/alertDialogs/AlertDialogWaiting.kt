package com.tt.ox.alertDialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.R
import com.tt.ox.databinding.AlertDialogInvitationBinding
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable
import com.tt.ox.helpers.FirebaseRequests
import com.tt.ox.helpers.FirebaseUser
import com.tt.ox.helpers.ScreenMetricsCompat

class AlertDialogWaiting(
    private val context: Context,
    layoutInflater: LayoutInflater,
    private val userId:String,
    private val request: FirebaseRequests,
    private val message:String,
    private val positiveButtonEnabled:Boolean,
    private val showTimeInvitation:Boolean,
    private val positiveButtonText:String,
    private val negativeButtonText:String,
    private val endTimeCallBack: () -> Unit,
    private val negativeButtonPressed: () -> Unit,
    private val positiveButtonPressed: () -> Unit
    ) {
    private val builder = AlertDialog.Builder(context)
    private val alertDialog = AlertDialogInvitationBinding.inflate(layoutInflater)
    val width = (ScreenMetricsCompat().getWindowWidth(context)*0.9).toInt()

    fun create():AlertDialog{

        setAlertDialogColors(alertDialog)
        setButtonsUI(alertDialog)
        setAlertDialogSizes(alertDialog)

        setAlertDialogConstraints(alertDialog)

        builder.setView(alertDialog.root)

        displayUI(alertDialog)

        if(showTimeInvitation) {
            displayTimeInvitationUI(alertDialog).run()
        }
        if(!positiveButtonEnabled){
            alertDialog.positiveButton.visibility = View.GONE
        }

        alertDialog.negativeButton.setOnClickListener {
            negativeButtonPressed()
        }

        alertDialog.positiveButton.setOnClickListener {
            positiveButtonPressed()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        return dialog
    }

    private fun setButtonsUI(alertDialog: AlertDialogInvitationBinding) {
        alertDialog.positiveButton.background = ButtonBackground(context)
        alertDialog.negativeButton.background = ButtonBackground(context)
        alertDialog.positiveButton.setImageDrawable(ButtonWithTextDrawable(context,positiveButtonText))
        alertDialog.negativeButton.setImageDrawable(ButtonWithTextDrawable(context,negativeButtonText))

    }

    private fun displayTimeInvitationUI(
        alertDialog: AlertDialogInvitationBinding
    ):Runnable = Runnable {
        val handler = Handler(Looper.getMainLooper())
        val time = request.timestamp
        val waitingEnd = time+60000
        val currentTime = System.currentTimeMillis()
        val timeLeft = (waitingEnd-currentTime)/1000


        if(timeLeft>=0) {
            handler.postDelayed(displayTimeInvitationUI(alertDialog), 1000)
            alertDialog.time.text = timeLeft.toString()
        }else{

            alertDialog.time.text = "0"
            handler.removeCallbacksAndMessages(null)
            endTimeCallBack()
        }
    }

    private fun displayUI(alertDialog: AlertDialogInvitationBinding) {
        alertDialog.title.text = "WAITING"
        alertDialog.message.text = message
        val dbRef = Firebase.database.getReference("Users").child(userId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.let {
                    val user = it.getValue(FirebaseUser::class.java)
                    alertDialog.userName.text = user!!.userName
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })


    }

    private fun setAlertDialogColors(alertDialog: AlertDialogInvitationBinding) {
        alertDialog.title.setTextColor(ContextCompat.getColor(context, R.color.black))
        alertDialog.message.setTextColor(ContextCompat.getColor(context, R.color.black))
        alertDialog.userName.setTextColor(ContextCompat.getColor(context, R.color.black))
        alertDialog.time.setTextColor(ContextCompat.getColor(context, R.color.black))

    }

    private fun setAlertDialogSizes(alertDialog: AlertDialogInvitationBinding) {
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.1f)
        alertDialog.message.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.05f)
        alertDialog.message.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)
        alertDialog.userName.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.05f)
        alertDialog.userName.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)
        alertDialog.time.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width*0.05).toFloat())
        alertDialog.positiveButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
        alertDialog.negativeButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
    }

    private fun setAlertDialogConstraints(alertDialog: AlertDialogInvitationBinding) {
        val set = ConstraintSet()
        set.clone(alertDialog.layout)

        set.connect(alertDialog.title.id,
            ConstraintSet.TOP,alertDialog.layout.id,
            ConstraintSet.TOP)
        set.connect(alertDialog.title.id,
            ConstraintSet.LEFT,alertDialog.layout.id,
            ConstraintSet.LEFT)
        set.connect(alertDialog.title.id,
            ConstraintSet.RIGHT,alertDialog.layout.id,
            ConstraintSet.RIGHT)

        set.connect(alertDialog.message.id, ConstraintSet.TOP,alertDialog.title.id, ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.message.id, ConstraintSet.LEFT,alertDialog.layout.id, ConstraintSet.LEFT)
        set.connect(alertDialog.message.id, ConstraintSet.RIGHT,alertDialog.layout.id, ConstraintSet.RIGHT)

        set.connect(alertDialog.userName.id, ConstraintSet.TOP,alertDialog.message.id, ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.userName.id, ConstraintSet.LEFT,alertDialog.layout.id, ConstraintSet.LEFT)
        set.connect(alertDialog.userName.id, ConstraintSet.RIGHT,alertDialog.layout.id, ConstraintSet.RIGHT)

        set.connect(alertDialog.time.id,ConstraintSet.TOP,alertDialog.userName.id,ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.time.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT)
        set.connect(alertDialog.time.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT)

        set.connect(alertDialog.negativeButton.id,
            ConstraintSet.LEFT,alertDialog.layout.id,
            ConstraintSet.LEFT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.negativeButton.id,
            ConstraintSet.TOP,alertDialog.time.id,
            ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.negativeButton.id,
            ConstraintSet.BOTTOM,alertDialog.layout.id,
            ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )


        set.connect(alertDialog.positiveButton.id,
            ConstraintSet.RIGHT,alertDialog.layout.id,
            ConstraintSet.RIGHT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.positiveButton.id,
            ConstraintSet.TOP,alertDialog.time.id,
            ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.positiveButton.id,
            ConstraintSet.BOTTOM,alertDialog.layout.id,
            ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )

        set.applyTo(alertDialog.layout)
    }

}