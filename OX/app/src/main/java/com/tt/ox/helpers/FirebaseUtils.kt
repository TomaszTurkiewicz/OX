package com.tt.ox.helpers

import android.content.Context
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseUtils(val context: Context) {

    fun listOfUsers():MutableList<FirebaseUserId>{
        val list:MutableList<FirebaseUserId> = mutableListOf()
        list.clear()
        val currentDate = DateUtils().getCurrentDate()
        val dbRefRanking = Firebase.database.getReference("Ranking").child(currentDate.toString())
        dbRefRanking.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(id in snapshot.children){
                        val tId = id.getValue(FirebaseUserId::class.java)
                        list.add(tId!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }

        })
        return list
    }

}