package com.tt.ox.helpers

import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.tt.ox.R
import com.tt.ox.databinding.AlertDialogAddMovesBinding
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable

class AlertDialogAddMoves(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val dismissClick: () -> Unit,
    private val addMovesClick: () -> Unit
) {

    private val builder = AlertDialog.Builder(context)
    private val alertDialog = AlertDialogAddMovesBinding.inflate(layoutInflater)
    val width = (ScreenMetricsCompat().getWindowWidth(context)*0.9).toInt()
    fun create(): AlertDialog {

        setAlertDialogColors(alertDialog)
        setButtonsUI(alertDialog)
        setAlertDialogSizes(alertDialog)
        setAlertDialogConstraints(alertDialog)

        builder.setView(alertDialog.root)

        alertDialog.negativeButton.setOnClickListener {
            dismissClick()
        }

        alertDialog.positiveButton.setOnClickListener {
            addMovesClick()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        return dialog
    }

    private fun setAlertDialogConstraints(alertDialog: AlertDialogAddMovesBinding) {
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

        set.connect(alertDialog.positiveButton.id,
            ConstraintSet.RIGHT,alertDialog.layout.id,
            ConstraintSet.RIGHT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.positiveButton.id,
            ConstraintSet.TOP,alertDialog.message.id,
            ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.positiveButton.id,
            ConstraintSet.BOTTOM,alertDialog.layout.id,
            ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )

        set.connect(alertDialog.negativeButton.id,
            ConstraintSet.LEFT,alertDialog.layout.id,
            ConstraintSet.LEFT,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.negativeButton.id,
            ConstraintSet.TOP,alertDialog.message.id,
            ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.negativeButton.id,
            ConstraintSet.BOTTOM,alertDialog.layout.id,
            ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )

        set.applyTo(alertDialog.layout)

    }

    private fun setAlertDialogSizes(alertDialog: AlertDialogAddMovesBinding) {
        alertDialog.title.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.1f)
        alertDialog.message.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.05f)
        alertDialog.message.setPadding((width*0.05).toInt(),0,(width*0.05).toInt(),0)
        alertDialog.positiveButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
        alertDialog.negativeButton.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
    }

    private fun setButtonsUI(alertDialog: AlertDialogAddMovesBinding) {
        alertDialog.positiveButton.background = ButtonBackground(context)
        alertDialog.positiveButton.setImageDrawable(ButtonWithTextDrawable(context,"ADD"))

        alertDialog.negativeButton.background = ButtonBackground(context)
        alertDialog.negativeButton.setImageDrawable(ButtonWithTextDrawable(context,"DISMISS"))
    }

    private fun setAlertDialogColors(alertDialog: AlertDialogAddMovesBinding) {
        alertDialog.title.setTextColor(ContextCompat.getColor(context, R.color.black))
        alertDialog.message.setTextColor(ContextCompat.getColor(context, R.color.black))
    }
}