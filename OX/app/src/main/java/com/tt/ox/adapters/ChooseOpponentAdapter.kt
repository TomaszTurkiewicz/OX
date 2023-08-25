package com.tt.ox.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tt.ox.database.Opponent
import com.tt.ox.databinding.ChooseOpponentListItemBinding

class ChooseOpponentAdapter(
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
        holder.opponentName.text = current.opponentName
        holder.opponentWin.text = current.opponentWin.toString()
        holder.playerWin.text = current.mainPlayerWin.toString()
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

    fun delete(){
        deletable = !deletable
    }

    class ChooseOpponentViewHolder(binding: ChooseOpponentListItemBinding): RecyclerView.ViewHolder(binding.root){
        val layout = binding.chooseOpponentListItemLayout
        val opponentName = binding.opponentName
        val playerWin = binding.playerWin
        val underScore = binding.underScore
        val opponentWin = binding.opponentWin
        val delete = binding.deleteItem
    }

    companion object{
        private val DiffCallback = object :DiffUtil.ItemCallback<Opponent>(){
            override fun areItemsTheSame(oldItem: Opponent, newItem: Opponent): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Opponent, newItem: Opponent): Boolean {
                return oldItem.opponentName == newItem.opponentName
            }
        }
    }

}