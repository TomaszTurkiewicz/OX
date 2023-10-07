package com.tt.ox.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tt.ox.databinding.OnlineListItemBinding
import com.tt.ox.helpers.FirebaseUser

class OnlineListAdapter(
    private val context:Context
) : ListAdapter<FirebaseUser, OnlineListAdapter.OnlineListViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnlineListViewHolder {
        return OnlineListViewHolder(
            OnlineListItemBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: OnlineListViewHolder, position: Int) {
        val current = getItem(position)
        holder.name.text = current.userName
    }

    class OnlineListViewHolder(binding: OnlineListItemBinding) : RecyclerView.ViewHolder(binding.root){
        val layout = binding.onlineListLayout
        val name = binding.name
        val wins = binding.wins
        val loses = binding.loses
        val sendInvitation = binding.imageView1
        val notUsedImage = binding.imageView2
    }

    companion object{
        private val DiffCallback = object : DiffUtil.ItemCallback<FirebaseUser>() {
            override fun areItemsTheSame(oldItem: FirebaseUser, newItem: FirebaseUser): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: FirebaseUser, newItem: FirebaseUser): Boolean {
                return oldItem.id == newItem.id
            }

        }
    }


}