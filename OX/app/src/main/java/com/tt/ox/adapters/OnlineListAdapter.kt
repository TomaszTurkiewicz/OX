package com.tt.ox.adapters

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tt.ox.R
import com.tt.ox.databinding.OnlineListItemBinding
import com.tt.ox.drawables.ActiveCircleDrawable
import com.tt.ox.drawables.ButtonBackground
import com.tt.ox.drawables.ListItemBackgroundDrawable
import com.tt.ox.drawables.SendInvitationDrawable
import com.tt.ox.helpers.AVAILABLE
import com.tt.ox.helpers.DateUtils
import com.tt.ox.helpers.FirebaseHistory
import com.tt.ox.helpers.FirebaseRequests
import com.tt.ox.helpers.FirebaseUser
import com.tt.ox.helpers.ScreenMetricsCompat

class OnlineListAdapter(
    private val context:Context,
    private val userId:String,
    private val sendInvitation: (FirebaseUser)->Unit
) : ListAdapter<FirebaseUser, OnlineListAdapter.OnlineListViewHolder>(DiffCallback) {

    private val width = ScreenMetricsCompat().getWindowWidth(context)
    private val height = width*0.3

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
        holder.layout.layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        holder.name.text = current.userName



        val user = Firebase.database.getReference("Users").child(current.id!!)
//        user.addListenerForSingleValueEvent(object : ValueEventListener{
        user.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val u = snapshot.getValue(FirebaseUser::class.java)
                    holder.activity.text = DateUtils().getLastActivity(u!!.unixTime)
                    holder.activeImage.setImageDrawable(ActiveCircleDrawable(context,DateUtils().getActivityBoolean(u!!.unixTime)))


                    val dbHistory = Firebase.database.getReference("History").child(userId).child(current.id!!)
                    dbHistory.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if(snapshot.exists()){
                                val history = snapshot.getValue(FirebaseHistory::class.java)
                                holder.generalStat.text = "${u.wins}-${u.loses}"
                                holder.personalStat.text = "${history!!.wins}-${history.loses}"
//                                holder.wins.text = "${history!!.wins} (${u.wins})"
//                                holder.loses.text = "${history!!.loses} (${u.loses})"
                            }else{
                                holder.generalStat.text = "${u.wins}-${u.loses}"
                                holder.personalStat.text = "0-0"
//                                holder.wins.text = "0 (${u.wins})"
//                                holder.loses.text = "0 (${u.loses})"
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        setSizes(holder)
        setColors(holder)
        setConstraint(holder)
        setDrawables(holder,current)
        val dbRequests = Firebase.database.getReference("Requests").child(current.id!!)
        dbRequests.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val request = snapshot.getValue(FirebaseRequests::class.java)
                    if(request!!.status== AVAILABLE){
                        holder.sendInvitation.setImageDrawable(SendInvitationDrawable(context,true))
                    }else{
                        holder.sendInvitation.setImageDrawable(SendInvitationDrawable(context,false))
                    }
                }else{
                    holder.sendInvitation.setImageDrawable(SendInvitationDrawable(context,true))
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        holder.sendInvitation.setOnClickListener {
            sendInvitation(current)
        }
    }

    private fun setConstraint(holder: OnlineListViewHolder) {
        val set = ConstraintSet()
        set.clone(holder.layout)

        set.connect(holder.background.id, ConstraintSet.LEFT, holder.layout.id, ConstraintSet.LEFT, 0)
        set.connect(holder.background.id, ConstraintSet.RIGHT, holder.layout.id, ConstraintSet.RIGHT, 0)
        set.connect(holder.background.id, ConstraintSet.TOP, holder.layout.id, ConstraintSet.TOP, 0)
        set.connect(holder.background.id, ConstraintSet.BOTTOM, holder.layout.id, ConstraintSet.BOTTOM, 0)

        set.connect(holder.sendInvitation.id,ConstraintSet.TOP,holder.layout.id,ConstraintSet.TOP,0)
        set.connect(holder.sendInvitation.id,ConstraintSet.BOTTOM,holder.layout.id,ConstraintSet.BOTTOM,0)
        set.connect(holder.sendInvitation.id,ConstraintSet.RIGHT,holder.layout.id,ConstraintSet.RIGHT, (height/3).toInt())

//        set.connect(holder.name.id, ConstraintSet.BOTTOM, holder.background.id, ConstraintSet.BOTTOM, (height/3).toInt())
        set.connect(holder.name.id, ConstraintSet.LEFT, holder.background.id, ConstraintSet.LEFT, (width*0.1).toInt())
        set.connect(holder.name.id, ConstraintSet.TOP, holder.background.id, ConstraintSet.TOP,
            (height*0.15).toInt()
        )

        set.connect(holder.generalStat.id, ConstraintSet.BOTTOM,holder.name.id,ConstraintSet.BOTTOM,0)
        set.connect(holder.generalStat.id, ConstraintSet.TOP,holder.name.id,ConstraintSet.TOP,0)
        set.connect(holder.generalStat.id, ConstraintSet.LEFT,holder.name.id,ConstraintSet.RIGHT,0)
        set.connect(holder.generalStat.id, ConstraintSet.RIGHT,holder.sendInvitation.id,ConstraintSet.LEFT,0)

//        set.connect(holder.line.id, ConstraintSet.TOP,holder.wins.id,ConstraintSet.TOP,0)
//        set.connect(holder.line.id, ConstraintSet.BOTTOM,holder.wins.id,ConstraintSet.BOTTOM,0)
//        set.connect(holder.line.id, ConstraintSet.LEFT,holder.wins.id,ConstraintSet.RIGHT, 0)

        set.connect(holder.personalStat.id, ConstraintSet.TOP,holder.name.id,ConstraintSet.BOTTOM,(height*0.02).toInt())
        set.connect(holder.personalStat.id, ConstraintSet.LEFT,holder.name.id,ConstraintSet.LEFT, 0)

        set.connect(holder.activeImage.id,ConstraintSet.TOP,holder.personalStat.id,ConstraintSet.TOP,0)
        set.connect(holder.activeImage.id,ConstraintSet.BOTTOM,holder.personalStat.id,ConstraintSet.BOTTOM,0)
        set.connect(holder.activeImage.id,ConstraintSet.LEFT,holder.personalStat.id,ConstraintSet.RIGHT,
            (height*0.1).toInt()
        )

        set.connect(holder.activity.id, ConstraintSet.TOP,holder.activeImage.id,ConstraintSet.TOP,0)
        set.connect(holder.activity.id, ConstraintSet.BOTTOM,holder.activeImage.id,ConstraintSet.BOTTOM,0)
        set.connect(holder.activity.id, ConstraintSet.LEFT,holder.activeImage.id,ConstraintSet.RIGHT,(height*0.02).toInt())




        set.applyTo(holder.layout)
    }

    private fun setDrawables(holder: OnlineListViewHolder,current: FirebaseUser) {
        holder.background.setImageDrawable(ListItemBackgroundDrawable(context))
        holder.sendInvitation.background = ButtonBackground(context)


    }

    private fun setColors(holder: OnlineListViewHolder) {
        holder.name.setTextColor(ContextCompat.getColor(context, R.color.black))
        holder.generalStat.setTextColor(ContextCompat.getColor(context, R.color.black))
        holder.personalStat.setTextColor(ContextCompat.getColor(context, R.color.black))
//        holder.line.setTextColor(ContextCompat.getColor(context, R.color.black))
        holder.activity.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    private fun setSizes(holder: OnlineListViewHolder) {
        holder.background.layoutParams =
            ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, height.toInt())
        holder.name.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.45).toInt(), (width * 0.1).toInt())
        holder.sendInvitation.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.15).toInt(), (width * 0.15).toInt())
//        holder.line.setTextSize(TypedValue.COMPLEX_UNIT_DIP, (width*0.1).toFloat())

//        holder.generalStat.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width*0.04).toFloat())
        holder.generalStat.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.15).toInt(), (width * 0.1).toInt())
//        holder.line.setTextSize(TypedValue.COMPLEX_UNIT_PX, (width*0.07).toFloat())
        holder.personalStat.layoutParams =
            ConstraintLayout.LayoutParams((width * 0.35).toInt(), (width * 0.1).toInt())
        holder.activeImage.layoutParams = ConstraintLayout.LayoutParams((width*0.04).toInt(), (width*0.04).toInt())
        holder.activity.layoutParams = ConstraintLayout.LayoutParams((width*0.15).toInt(), (width*0.04).toInt())


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.name.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.name,
                1,
                200,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.generalStat.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                20,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.generalStat,
                1,
                20,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.personalStat.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                100,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.personalStat,
                1,
                100,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            holder.activity.setAutoSizeTextTypeUniformWithConfiguration(
                1,
                100,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        } else {
            TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                holder.activity,
                1,
                100,
                1,
                TypedValue.COMPLEX_UNIT_DIP
            )
        }
    }

    class OnlineListViewHolder(binding: OnlineListItemBinding) : RecyclerView.ViewHolder(binding.root){
        val layout = binding.onlineListLayout
        val background = binding.background
        val name = binding.name
//        val line = binding.line
        val generalStat = binding.generalStat
        val personalStat = binding.personalStat
        val activity = binding.activity
        val sendInvitation = binding.imageView1
        val activeImage = binding.imageView2
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

