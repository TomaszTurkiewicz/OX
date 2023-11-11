package com.tt.ox.alertDialogs

import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import com.tt.ox.databinding.AlertDialogEndGameOnlineBattleBinding
import com.tt.ox.drawables.AlertDialogBackgroundDrawableColor
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ButtonWithTextDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.Theme

class AlertDialogEndGameOnlineBattle(
    private val context: Context,
    private val layoutInflater: LayoutInflater,
    private val message:String,
    private val buttonClicked: () -> Unit
    )
{
    private val builder = AlertDialog.Builder(context)
    private val alertDialog = AlertDialogEndGameOnlineBattleBinding.inflate(layoutInflater)
    private val width = (ScreenMetricsCompat().getWindowWidth(context)*0.9).toInt()


    fun create() : AlertDialog {
        setColors(alertDialog)
        setButtonsUI(alertDialog)
        setSizes(alertDialog)
        setConstraints(alertDialog)

        builder.setView(alertDialog.root)

        alertDialog.message.text = message

        alertDialog.button.setOnClickListener {
            buttonClicked()
        }

        val dialog = builder.create()
        dialog.setCancelable(false)
        return dialog
    }

    private fun setConstraints(alertDialog: AlertDialogEndGameOnlineBattleBinding) {
        val set = ConstraintSet()
        set.clone(alertDialog.layout)

        set.connect(alertDialog.message.id,ConstraintSet.TOP,alertDialog.layout.id,ConstraintSet.TOP,
            (width*0.05).toInt()
        )
        set.connect(alertDialog.message.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.message.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)

        set.connect(alertDialog.button.id,ConstraintSet.TOP,alertDialog.message.id,ConstraintSet.BOTTOM,
            (width*0.1).toInt()
        )
        set.connect(alertDialog.button.id,ConstraintSet.LEFT,alertDialog.layout.id,ConstraintSet.LEFT,0)
        set.connect(alertDialog.button.id,ConstraintSet.RIGHT,alertDialog.layout.id,ConstraintSet.RIGHT,0)
        set.connect(alertDialog.button.id,ConstraintSet.BOTTOM,alertDialog.layout.id,ConstraintSet.BOTTOM,
            (width*0.05).toInt()
        )
        set.applyTo(alertDialog.layout)
    }

    private fun setSizes(alertDialog: AlertDialogEndGameOnlineBattleBinding) {
        alertDialog.layout.background = AlertDialogBackgroundDrawableColor(context)
        alertDialog.message.setTextSize(TypedValue.COMPLEX_UNIT_PX,width*0.1f)
        alertDialog.button.layoutParams = ConstraintLayout.LayoutParams((width*0.4).toInt(),(width*0.1).toInt())
    }

    private fun setButtonsUI(alertDialog: AlertDialogEndGameOnlineBattleBinding) {
        alertDialog.button.background = ButtonBackground(context)
        alertDialog.button.setImageDrawable(ButtonWithTextDrawable(context,"OK"))
    }

    private fun setColors(alertDialog: AlertDialogEndGameOnlineBattleBinding) {
        alertDialog.message.setTextColor(ContextCompat.getColor(context, Theme(context).getAccentColor()))
    }
}