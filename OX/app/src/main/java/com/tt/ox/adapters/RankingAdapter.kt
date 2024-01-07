package com.tt.ox.adapters

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tt.ox.databinding.RankingListItemBinding
import com.tt.ox.drawables.BackgroundRankingItemDrawable
import com.tt.ox.helpers.FirebaseUser
import com.tt.ox.helpers.Theme

class RankingAdapter(
    private val context: Context,
    private val userId: String,
    private val width: Int,
    private val height:Int
) : ListAdapter<FirebaseUser, RankingAdapter.RankingViewHolder>(DiffCallback){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        return RankingViewHolder(
            RankingListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        val current = getItem(position)
        holder.position.text = (position+1).toString()
        holder.name.text = current.userName
        holder.wins.text = current.wins.toString()
        holder.loses.text = current.loses.toString()

        setSizes(holder)
        setColors(holder)
        setDrawables(holder, current.id!!)

    }



    private fun setDrawables(holder: RankingViewHolder, id: String) {
        if(userId == id){
            holder.layout.background = BackgroundRankingItemDrawable(context)
        }else{
            holder.layout.background = null
        }
    }

    private fun setColors(holder: RankingViewHolder) {
        holder.position.setTextColor(ContextCompat.getColor(context,Theme(context).getAccentColor()))
        holder.name.setTextColor(ContextCompat.getColor(context,Theme(context).getAccentColor()))
        holder.wins.setTextColor(ContextCompat.getColor(context,Theme(context).getGreenColor()))
        holder.loses.setTextColor(ContextCompat.getColor(context,Theme(context).getRedColor()))
    }

    private fun setSizes(holder: RankingViewHolder) {
        holder.layout.layoutParams = LinearLayout.LayoutParams(width,height)
        holder.position.layoutParams = LinearLayout.LayoutParams((width*0.1).toInt(),height)
        holder.name.layoutParams = LinearLayout.LayoutParams((width*0.4).toInt(),height)
        holder.wins.layoutParams = LinearLayout.LayoutParams((width*0.25).toInt(),height)
        holder.loses.layoutParams = LinearLayout.LayoutParams((width*0.25).toInt(),height)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            holder.position.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                height/2,
                1,
                TypedValue.COMPLEX_UNIT_PX
            )
            holder.name.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                height/2,
                1,
                TypedValue.COMPLEX_UNIT_PX
            )
            holder.wins.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                height/2,
                1,
                TypedValue.COMPLEX_UNIT_PX
            )
            holder.loses.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                height/2,
                1,
                TypedValue.COMPLEX_UNIT_PX
            )
        }
        else
        {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.position,
                1,
                height/2,
                1,
                TypedValue.COMPLEX_UNIT_PX
            )
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.name,
                1,
                height/2,
                1,
                TypedValue.COMPLEX_UNIT_PX
            )
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.wins,
                1,
                height/2,
                1,
                TypedValue.COMPLEX_UNIT_PX
            )
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.loses,
                1,
                height/2,
                1,
                TypedValue.COMPLEX_UNIT_PX
            )
        }

    }


    class RankingViewHolder(binding:RankingListItemBinding) : RecyclerView.ViewHolder(binding.root){
        val layout = binding.rankingItemLayout
        val position = binding.position
        val name = binding.name
        val wins = binding.wins
        val loses = binding.loses
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<FirebaseUser>(){
            override fun areItemsTheSame(oldItem: FirebaseUser, newItem: FirebaseUser): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FirebaseUser, newItem: FirebaseUser): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }


}

