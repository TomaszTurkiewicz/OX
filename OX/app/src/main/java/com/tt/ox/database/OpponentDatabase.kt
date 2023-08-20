package com.tt.ox.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Opponent::class],
    version = 1,
    exportSchema = false
)
abstract class OpponentDatabase : RoomDatabase() {
    abstract fun opponentDao(): OpponentDao

    companion object{
        @Volatile
        private var INSTANCE: OpponentDatabase? = null

        fun getDatabase(context: Context): OpponentDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OpponentDatabase::class.java,
                    "opponent_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}