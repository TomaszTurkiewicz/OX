package com.tt.ox.helpers

import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.tt.ox.R
import com.tt.ox.databinding.AlertDialogAddOpponentBinding
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable

class AlertDialogChangeName(
    private val context: Context,
    layoutInflater: LayoutInflater,
    private val cancelButtonEnable:Boolean,
    private val readNameFromMemory:Boolean,
    private val dismissClick: () -> Unit,
    private val saveClick: (String) -> Unit
) {
    private val builder = AlertDialog.Builder(context)
    private val alertDialog = AlertDialogAddOpponentBinding.inflate(layoutInflater)
    val width = (ScreenMetricsCompat().getWindowWidth(context)*0.9).toInt()

    fun create():AlertDialog{
        displayAlertDialogUI(alertDialog)
        if(readNameFromMemory) {
            val name = SharedPreferences.readPlayerName(context)
            alertDialog.inputName.setText(name)
        }else{
            alertDialog.inputName.setText("")
        }
        if(!cancelButtonEnable){
            alertDialog.cancelButton.visibility = View.GONE
        }

        builder.setView(alertDialog.root)

        alertDialog.cancelButton.setOnClickListener {
            dismissClick()
        }

        alertDialog.saveButton.setOnClickListener {
            val string = alertDialog.inputName.text.toString()
            string.let {
                if(it.length>1){
                    saveClick(it)
                }
                else{
                    Toast.makeText(context,"Name too short", Toast.LENGTH_LONG).show()
                }
            }

        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        return dialog
    }

    private fun displayAlertDialogUI(alertDialog: AlertDialogAddOpponentBinding){
        alertDialog.title.text = "What's your nickname"
        alertDialog.message.text = "Type your nickname. Between 2 and 14 characters."

        setAlertDialogColors(alertDialog)
        setAlertDialogSizes(alertDialog)
        setAlertDialogDrawables(alertDialog)
        setAlertDialogConstraints(alertDialog)
    }

    private fun setAlertDialogConstraints(alertDialog: AlertDialogAddOpponentBinding) {
        val set = ConstraintSet()
        set.clone(alertDialog.alertDialogAddOpponentLayout)

        set.connect(alertDialog.title.id,ConstraintSet.TOP,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.TOP)
        set.connect(alertDialog.title.id,ConstraintSet.LEFT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.LEFT)
        set.connect(alertDialog.title.id,ConstraintSet.RIGHT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.RIGHT)

        set.connect(alertDialog.message.id,ConstraintSet.TOP,alertDialog.title.id,ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.message.id,ConstraintSet.LEFT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.LEFT)
        set.connect(alertDialog.message.id,ConstraintSet.RIGHT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.RIGHT)

        set.connect(alertDialog.inputName.id,ConstraintSet.TOP,alertDialog.message.id,ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.inputName.id,ConstraintSet.LEFT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.LEFT)
        set.connect(alertDialog.inputName.id,ConstraintSet.RIGHT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.RIGHT)

        set.connect(alertDialog.cancelButton.id,ConstraintSet.LEFT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.LEFT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.cancelButton.id,ConstraintSet.TOP,alertDialog.inputName.id,ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.cancelButton.id,ConstraintSet.BOTTOM,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )

        set.connect(alertDialog.saveButton.id,ConstraintSet.RIGHT,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.RIGHT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.saveButton.id,ConstraintSet.TOP,alertDialog.inputName.id,ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.saveButton.id,ConstraintSet.BOTTOM,alertDialog.alertDialogAddOpponentLayout.id,ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )

        set.applyTo(alertDialog.alertDialogAddOpponentLayout)
    }

    private fun setAlertDialogDrawables(alertDialog: AlertDialogAddOpponentBinding) {
        alertDialog.saveButton.setImageDrawable(ButtonWithTextDrawable(context,"SAVE"))
        alertDialog.cancelButton.setImageDrawable(ButtonWithTextDrawable(context,"CANCEL"))
        alertDialog.saveButton.background = ButtonBackground(context)
        alertDialog.cancelButton.background = ButtonBackground(context)

    }

    private fun setAlertDialogSizes(alertDialog: AlertDialogAddOpponentBinding) {
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, width*0.1f)
        alertDialog.message.setTextSize(TypedValue.COMPLEX_UNIT_PX, width*0.05f)

        alertDialog.message.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)
        alertDialog.inputName.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)

        alertDialog.saveButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
        alertDialog.cancelButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
    }

    private fun setAlertDialogColors(alertDialog: AlertDialogAddOpponentBinding) {
        alertDialog.title.setTextColor(ContextCompat.getColor(context, R.color.black))
        alertDialog.message.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

}
