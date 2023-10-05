package com.tt.ox.helpers

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseUtils {

    fun checkUser(userId:String){
        val dbRef = Firebase.database.getReference("Users").child(userId)
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()){
                    createUser(userId)
                }
                else{
                    updateTimeStamp(userId)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // do nothing
            }
        })
    }

    private fun createUser(userId: String) {
        val currentDate = DateUtils().getCurrentDate()
        val dbRefUser = Firebase.database.getReference("Users").child(userId)
        dbRefUser.setValue(currentDate)

        val dbRefRanking = Firebase.database.getReference("Ranking").child(currentDate.toString())
        dbRefRanking.setValue(userId)
        //todo finish this first!!!!!!!!!
    }

    private fun updateTimeStamp(userId: String) {
        //todo
    }
}