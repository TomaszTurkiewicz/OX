package com.tt.ox.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tt.ox.R
import com.tt.ox.X
import com.tt.ox.database.Opponent
import com.tt.ox.databinding.ChooseOpponentListItemBinding
import com.tt.ox.drawables.ODrawable
import com.tt.ox.drawables.XDrawable
import com.tt.ox.helpers.SharedPreferences

class ChooseOpponentAdapter(
    private val context: Context,
    private val unit:Int,
    private val onDeleteOpponentClicked: (Opponent) -> Unit,
    private val onOpponentClicked: (Opponent)-> Unit) :
    ListAdapter<Opponent, ChooseOpponentAdapter.ChooseOpponentViewHolder>(DiffCallback) {

    private var deletable = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseOpponentViewHolder {
        return ChooseOpponentViewHolder(
            ChooseOpponentListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),parent,false
            )
        )
    }


    override fun onBindViewHolder(holder: ChooseOpponentViewHolder, position: Int) {
        val current = getItem(position)
            holder.layout.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,2*unit)
            holder.opponentName.text = current.getName()
            holder.opponentWin.text = current.getLoses().toString()
            holder.playerWin.text = current.getWins().toString()
            holder.playerName.text = SharedPreferences.readPlayerName(context)
        holder.playerMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        holder.opponentMark.layoutParams = ConstraintLayout.LayoutParams(unit,unit)
        holder.playerMark.setImageDrawable(if(current.getMainPlayerMark()==X) XDrawable(context,current.getMainPlayerMarkColor()) else ODrawable(context,current.getMainPlayerMarkColor()))
        holder.opponentMark.setImageDrawable(if(current.getOpponentMark()==X) XDrawable(context,current.getOpponentMarkColor()) else ODrawable(context,current.getOpponentMarkColor()))
        holder.layout.setBackgroundColor(ContextCompat.getColor(context, R.color.purple_500))
        setConstraint(holder)

            holder.opponentName.setOnClickListener{
                onOpponentClicked(current)
            }
            holder.delete.setOnClickListener {
                if(deletable) {
                    onDeleteOpponentClicked(current)
                }
            }
            if(deletable){
                holder.delete.visibility = View.VISIBLE
            }else{
                holder.delete.visibility = View.GONE
        }
    }

    private fun setConstraint(holder: ChooseOpponentViewHolder) {
        val set = ConstraintSet()
        set.clone(holder.layout)

        set.connect(holder.vs.id,ConstraintSet.LEFT,holder.layout.id,ConstraintSet.LEFT,0)
        set.connect(holder.vs.id,ConstraintSet.RIGHT,holder.layout.id,ConstraintSet.RIGHT,0)
        set.connect(holder.vs.id,ConstraintSet.TOP,holder.layout.id,ConstraintSet.TOP,0)

        set.connect(holder.playerName.id,ConstraintSet.RIGHT,holder.vs.id,ConstraintSet.LEFT,0)
        set.connect(holder.playerName.id,ConstraintSet.BOTTOM,holder.vs.id,ConstraintSet.BOTTOM,0)

        set.connect(holder.opponentName.id,ConstraintSet.LEFT,holder.vs.id,ConstraintSet.RIGHT,0)
        set.connect(holder.opponentName.id,ConstraintSet.BOTTOM,holder.vs.id,ConstraintSet.BOTTOM,0)

        set.connect(holder.underScore.id,ConstraintSet.LEFT,holder.layout.id,ConstraintSet.LEFT,0)
        set.connect(holder.underScore.id,ConstraintSet.RIGHT,holder.layout.id,ConstraintSet.RIGHT,0)
        set.connect(holder.underScore.id,ConstraintSet.TOP,holder.vs.id,ConstraintSet.BOTTOM,0)

        set.connect(holder.playerWin.id,ConstraintSet.RIGHT,holder.underScore.id,ConstraintSet.LEFT,0)
        set.connect(holder.playerWin.id,ConstraintSet.BOTTOM,holder.underScore.id,ConstraintSet.BOTTOM,0)

        set.connect(holder.opponentWin.id,ConstraintSet.LEFT,holder.underScore.id,ConstraintSet.RIGHT,0)
        set.connect(holder.opponentWin.id,ConstraintSet.BOTTOM,holder.underScore.id,ConstraintSet.BOTTOM,0)

        set.connect(holder.playerMark.id,ConstraintSet.TOP,holder.layout.id,ConstraintSet.TOP,0)
        set.connect(holder.playerMark.id,ConstraintSet.BOTTOM,holder.layout.id,ConstraintSet.BOTTOM,0)
        set.connect(holder.playerMark.id,ConstraintSet.LEFT,holder.layout.id,ConstraintSet.LEFT,0)

        set.connect(holder.opponentMark.id,ConstraintSet.TOP,holder.layout.id,ConstraintSet.TOP,0)
        set.connect(holder.opponentMark.id,ConstraintSet.BOTTOM,holder.layout.id,ConstraintSet.BOTTOM,0)
        set.connect(holder.opponentMark.id,ConstraintSet.RIGHT,holder.layout.id,ConstraintSet.RIGHT,0)

        set.connect(holder.delete.id,ConstraintSet.TOP,holder.layout.id,ConstraintSet.TOP,0)
        set.connect(holder.delete.id,ConstraintSet.BOTTOM,holder.layout.id,ConstraintSet.BOTTOM,0)
        set.connect(holder.delete.id,ConstraintSet.RIGHT,holder.layout.id,ConstraintSet.RIGHT,0)

        set.applyTo(holder.layout)
    }

    fun delete(){
        deletable = !deletable
    }

    class ChooseOpponentViewHolder(binding: ChooseOpponentListItemBinding): RecyclerView.ViewHolder(binding.root){
        val layout = binding.chooseOpponentListItemLayout
        val playerName = binding.mainPlayerName
        val opponentName = binding.opponentName
        val playerWin = binding.playerWin
        val underScore = binding.underScore
        val opponentWin = binding.opponentWin
        val playerMark = binding.mainPlayerMark
        val opponentMark = binding.opponentMark
        val delete = binding.deleteItem
        val vs = binding.vs
    }

    companion object{
        private val DiffCallback = object :DiffUtil.ItemCallback<Opponent>(){
            override fun areItemsTheSame(oldItem: Opponent, newItem: Opponent): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Opponent, newItem: Opponent): Boolean {
                return oldItem.getName() == newItem.getName()
            }
        }
    }
}//todo change it maybe to cardview?