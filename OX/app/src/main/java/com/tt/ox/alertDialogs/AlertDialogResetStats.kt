package com.tt.ox.alertDialogs

import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.tt.ox.databinding.ResetStatsAlertDialogBinding
import com.tt.ox.drawables.AlertDialogBackgroundDrawableColor
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.Theme

class AlertDialogResetStats(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val dismissClick: () -> Unit,
    private val positiveClick: () -> Unit
    ) {
    private val builder = AlertDialog.Builder(context)
    private val alertDialog = ResetStatsAlertDialogBinding.inflate(layoutInflater)
    val width = (ScreenMetricsCompat().getWindowWidth(context)*0.9).toInt()

    fun create(): AlertDialog{
        setAlertDialogColors(alertDialog)
        setButtonsUI(alertDialog)
        setAlertDialogSizes(alertDialog)
        setAlertDialogConstraints(alertDialog)

        builder.setView(alertDialog.root)

        alertDialog.negativeButton.setOnClickListener {
            dismissClick()
        }

        alertDialog.positiveButton.setOnClickListener {
            positiveClick()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        return dialog
    }

    private fun setAlertDialogConstraints(alertDialog: ResetStatsAlertDialogBinding) {
        val set = ConstraintSet()
        set.clone(alertDialog.layout)

        set.connect(alertDialog.title.id, ConstraintSet.TOP,alertDialog.layout.id,ConstraintSet.TOP,0)
        set.connect(alertDialog.title.id, ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.title.id, ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)

        set.connect(alertDialog.messageWins.id, ConstraintSet.TOP,alertDialog.title.id,ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.messageWins.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.messageWins.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)

        set.connect(alertDialog.messageLoses.id, ConstraintSet.TOP,alertDialog.messageWins.id,ConstraintSet.BOTTOM,(width*0.05).toInt())
        set.connect(alertDialog.messageLoses.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.messageLoses.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)

        set.connect(alertDialog.positiveButton.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT, (width*0.05).toInt())
        set.connect(alertDialog.positiveButton.id,ConstraintSet.TOP,alertDialog.messageLoses.id,ConstraintSet.BOTTOM,(width*0.1).toInt())
        set.connect(alertDialog.positiveButton.id,ConstraintSet.BOTTOM,alertDialog.layout.id,ConstraintSet.BOTTOM,(width*0.05).toInt())

        set.connect(alertDialog.negativeButton.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT, (width*0.05).toInt())
        set.connect(alertDialog.negativeButton.id,ConstraintSet.TOP,alertDialog.messageLoses.id,ConstraintSet.BOTTOM,(width*0.1).toInt())
        set.connect(alertDialog.negativeButton.id,ConstraintSet.BOTTOM,alertDialog.layout.id,ConstraintSet.BOTTOM,(width*0.05).toInt())

        set.applyTo(alertDialog.layout)
    }

    private fun setAlertDialogSizes(alertDialog: ResetStatsAlertDialogBinding) {
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX, width*0.1f)
        alertDialog.messageWins.setTextSize(TypedValue.COMPLEX_UNIT_PX, width*0.05f)
        alertDialog.messageLoses.setTextSize(TypedValue.COMPLEX_UNIT_PX, width*0.05f)
        alertDialog.positiveButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
        alertDialog.negativeButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())

    }

    private fun setButtonsUI(alertDialog: ResetStatsAlertDialogBinding) {
        alertDialog.positiveButton.background = ButtonBackground(context)
        alertDialog.positiveButton.setImageDrawable(ButtonWithTextDrawable(context,"RESET"))

        alertDialog.negativeButton.background = ButtonBackground(context)
        alertDialog.negativeButton.setImageDrawable(ButtonWithTextDrawable(context, "CANCEL"))
    }

    private fun setAlertDialogColors(alertDialog: ResetStatsAlertDialogBinding) {
        alertDialog.layout.background = AlertDialogBackgroundDrawableColor(context)
        alertDialog.title.setTextColor(ContextCompat.getColor(context, Theme(context).getRedColor()))
        alertDialog.messageWins.setTextColor(ContextCompat.getColor(context, Theme(context).getAccentColor()))
        alertDialog.messageLoses.setTextColor(ContextCompat.getColor(context, Theme(context).getAccentColor()))
    }
}