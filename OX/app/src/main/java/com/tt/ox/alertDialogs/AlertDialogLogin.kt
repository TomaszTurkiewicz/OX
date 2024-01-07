package com.tt.ox.alertDialogs

import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.tt.ox.databinding.AlertDialogLogInBinding
import com.tt.ox.drawables.AlertDialogBackgroundDrawableColor
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.Theme

class AlertDialogLogin(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val title:String,
    private val warning:Boolean,
    private val message:String,
    private val positiveClick: () -> Unit,
    private val cancelClick: () -> Unit
) {
    private val builder = AlertDialog.Builder(context)
    private val alertDialog = AlertDialogLogInBinding.inflate(layoutInflater)
    private var width = (ScreenMetricsCompat().getWindowWidth(context)*0.9).toInt()

    fun create():AlertDialog{
        displayAlertDialogUILogin(alertDialog)

        builder.setView(alertDialog.root)

        alertDialog.loginButton.setOnClickListener {
            positiveClick()
        }
        alertDialog.cancelButton.setOnClickListener {
            cancelClick()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        return dialog
    }


    private fun displayAlertDialogUILogin(alertDialog: AlertDialogLogInBinding){
        alertDialog.title.text = title
        alertDialog.message.text = message
        setAlertDialogColors(alertDialog)
        setAlertDialogSizes(alertDialog)
        setAlertDialogDrawables(alertDialog,title)
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

    private fun setAlertDialogDrawables(
        alertDialog: AlertDialogLogInBinding,
        positiveText: String
    ) {
        alertDialog.alertDialogLogIn.background = AlertDialogBackgroundDrawableColor(context)
        if(warning){
            alertDialog.loginButton.setImageDrawable(ButtonWithTextDrawable(context,"DELETE"))
        }else{
            alertDialog.loginButton.setImageDrawable(ButtonWithTextDrawable(context,positiveText))
        }

        alertDialog.cancelButton.setImageDrawable(ButtonWithTextDrawable(context,"CANCEL"))
        alertDialog.loginButton.background = ButtonBackground(context)
        alertDialog.cancelButton.background = ButtonBackground(context)
    }

    private fun setAlertDialogSizes(alertDialog: AlertDialogLogInBinding) {
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.1f)
        alertDialog.message.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.05f)
        alertDialog.message.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)
        alertDialog.loginButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
        alertDialog.cancelButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
    }

    private fun setAlertDialogColors(alertDialog: AlertDialogLogInBinding) {
        if(warning){
            alertDialog.title.setTextColor(ContextCompat.getColor(context, Theme(context).getRedColor()))
        }else{
            alertDialog.title.setTextColor(ContextCompat.getColor(context, Theme(context).getAccentColor()))
        }
        alertDialog.message.setTextColor(ContextCompat.getColor(context, Theme(context).getAccentColor()))

    }
}