package com.tt.ox.adapters

import android.app.ActionBar.LayoutParams
import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tt.ox.R
import com.tt.ox.database.Opponent
import com.tt.ox.databinding.ChooseOpponentListItemBinding
import com.tt.ox.drawables.BinDrawable
import com.tt.ox.drawables.ListItemBackgroundDrawable
import com.tt.ox.helpers.ScreenMetricsCompat
import com.tt.ox.helpers.SharedPreferences

class ChooseOpponentAdapter(
    private val context: Context,
    private val onDeleteOpponentClicked: (Opponent) -> Unit,
    private val onOpponentClicked: (Opponent)-> Unit) :
    ListAdapter<Opponent, ChooseOpponentAdapter.ChooseOpponentViewHolder>(DiffCallback) {

    private var deletable = false

    private val width = ScreenMetricsCompat().getWindowWidth(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseOpponentViewHolder {
        return ChooseOpponentViewHolder(
            ChooseOpponentListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: ChooseOpponentViewHolder, position: Int) {
        val current = getItem(position)
        holder.layout.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        holder.opponentName.text = current.getName()
        holder.opponentWin.text = current.getLoses().toString()
        holder.playerWin.text = current.getWins().toString()
        val playerName = SharedPreferences.readPlayerName(context)
        holder.playerName.text = playerName
//            holder.playerName.setImageDrawable(TextDrawable(context,playerName))

//        holder.playerMark.setImageDrawable(if(current.getMainPlayerMark()==X) XDrawable(context,current.getMainPlayerMarkColor()) else ODrawable(context,current.getMainPlayerMarkColor()))
//        holder.opponentMark.setImageDrawable(if(current.getOpponentMark()==X) XDrawable(context,current.getOpponentMarkColor()) else ODrawable(context,current.getOpponentMarkColor()))

        setSizes(holder)

        setColors(holder)

        setDrawables(holder)

        setConstraint(holder)

        holder.opponentName.setOnClickListener {
            playOpponent(current)
        }
        holder.playerName.setOnClickListener {
            playOpponent(current)
        }
        holder.vs.setOnClickListener {
            playOpponent(current)
        }
        holder.playerWin.setOnClickListener {
            playOpponent(current)
        }
        holder.opponentWin.setOnClickListener {
            playOpponent(current)
        }
        holder.delete.setOnClickListener {
            if (deletable) {
                onDeleteOpponentClicked(current)
            }
        }
        if (deletable) {
            holder.delete.visibility = View.VISIBLE
        } else {
            holder.delete.visibility = View.GONE
        }
    }

    private fun playOpponent(current: Opponent) {
        if (!deletable) {
            onOpponentClicked(current)
        }
    }

    private fun setDrawables(holder: ChooseOpponentViewHolder) {
        holder.background.setImageDrawable(ListItemBackgroundDrawable(context))

        holder.delete.setImageDrawable(BinDrawable(context, true))
    }

    private fun setColors(holder: ChooseOpponentViewHolder) {
        holder.vs.setTextColor(ContextCompat.getColor(context, R.color.black))
        holder.playerName.setTextColor(ContextCompat.getColor(context, R.color.black))
        holder.opponentName.setTextColor(ContextCompat.getColor(context, R.color.black))
        holder.playerWin.setTextColor(ContextCompat.getColor(context, R.color.black))
        holder.opponentWin.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    private fun setSizes(holder: ChooseOpponentViewHolder) {

        holder.vs.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.1).toInt(), (width * 0.1).toInt())
        holder.vs.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width * 0.05).toFloat())

        holder.background.layoutParams =
            ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, (width * 0.3).toInt())

        holder.playerName.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.4).toInt(), (width * 0.1).toInt())
        holder.opponentName.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.4).toInt(), (width * 0.1).toInt())

        holder.playerWin.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.4).toInt(), (width * 0.1).toInt())
        holder.opponentWin.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.4).toInt(), (width * 0.1).toInt())

        holder.delete.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.15).toInt(), (width * 0.15).toInt())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.playerName.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
            holder.opponentName.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
            holder.playerWin.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
            holder.opponentWin.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.playerName,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.opponentName,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.playerWin,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.opponentWin,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }
    }

    private fun setConstraint(holder: ChooseOpponentViewHolder) {
        val set = ConstraintSet()
        set.clone(holder.layout)

        set.connect(
            holder.background.id,
            ConstraintSet.LEFT,
            holder.layout.id,
            ConstraintSet.LEFT,
            0
        )
        set.connect(
            holder.background.id,
            ConstraintSet.RIGHT,
            holder.layout.id,
            ConstraintSet.RIGHT,
            0
        )
        set.connect(holder.background.id, ConstraintSet.TOP, holder.layout.id, ConstraintSet.TOP, 0)
        set.connect(
            holder.background.id,
            ConstraintSet.BOTTOM,
            holder.layout.id,
            ConstraintSet.BOTTOM,
            0
        )

        set.connect(holder.vs.id, ConstraintSet.LEFT, holder.layout.id, ConstraintSet.LEFT, 0)
        set.connect(holder.vs.id, ConstraintSet.RIGHT, holder.layout.id, ConstraintSet.RIGHT, 0)
        set.connect(holder.vs.id, ConstraintSet.TOP, holder.layout.id, ConstraintSet.TOP, 0)
        set.connect(holder.vs.id, ConstraintSet.BOTTOM, holder.layout.id, ConstraintSet.BOTTOM, 0)

        set.connect(holder.playerName.id, ConstraintSet.RIGHT, holder.vs.id, ConstraintSet.LEFT, 0)
        set.connect(
            holder.playerName.id, ConstraintSet.BOTTOM, holder.vs.id, ConstraintSet.BOTTOM,
            (width * 0.05).toInt()
        )

        set.connect(
            holder.opponentName.id,
            ConstraintSet.LEFT,
            holder.vs.id,
            ConstraintSet.RIGHT,
            0
        )
        set.connect(
            holder.opponentName.id,
            ConstraintSet.BOTTOM,
            holder.vs.id,
            ConstraintSet.BOTTOM,
            (width * 0.05).toInt()
        )

        set.connect(holder.playerWin.id, ConstraintSet.RIGHT, holder.vs.id, ConstraintSet.LEFT, 0)
        set.connect(
            holder.playerWin.id,
            ConstraintSet.TOP,
            holder.playerName.id,
            ConstraintSet.BOTTOM,
            0
        )

        set.connect(holder.opponentWin.id, ConstraintSet.LEFT, holder.vs.id, ConstraintSet.RIGHT, 0)
        set.connect(
            holder.opponentWin.id,
            ConstraintSet.TOP,
            holder.opponentName.id,
            ConstraintSet.BOTTOM,
            0
        )

        set.connect(holder.delete.id, ConstraintSet.TOP, holder.layout.id, ConstraintSet.TOP, 0)
        set.connect(
            holder.delete.id,
            ConstraintSet.BOTTOM,
            holder.layout.id,
            ConstraintSet.BOTTOM,
            0
        )
        set.connect(holder.delete.id, ConstraintSet.RIGHT, holder.layout.id, ConstraintSet.RIGHT, 0)
        set.connect(holder.delete.id, ConstraintSet.LEFT, holder.layout.id, ConstraintSet.LEFT, 0)

        set.applyTo(holder.layout)
    }

    fun delete(deletable: Boolean) {
        this.deletable = deletable
    }

    class ChooseOpponentViewHolder(binding: ChooseOpponentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val layout = binding.chooseOpponentListItemLayout
        val playerName = binding.mainPlayerName
        val opponentName = binding.opponentName
        val playerWin = binding.playerWin
        val background = binding.background
        val opponentWin = binding.opponentWin
        val delete = binding.deleteItem
        val vs = binding.vs
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Opponent>() {
            override fun areItemsTheSame(oldItem: Opponent, newItem: Opponent): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Opponent, newItem: Opponent): Boolean {
                return oldItem.getName() == newItem.getName()
            }
        }
    }
}