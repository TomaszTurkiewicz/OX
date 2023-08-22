package com.tt.ox.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface OpponentDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(opponent: Opponent)

    @Update
    suspend fun update(opponent: Opponent)

    @Delete
    suspend fun delete(opponent: Opponent)

    @Query("Select * from opponent")
    fun getOpponents(): Flow<List<Opponent>>

    @Query("Select * from opponent where id =:id")
    fun getOpponent(id:Int): Flow<Opponent>
}