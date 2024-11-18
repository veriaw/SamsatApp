package com.veriaw.samsatapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.veriaw.kriptografiapp.data.entity.UserEntity
import com.veriaw.samsatapp.data.entity.KeyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KeyDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(key: KeyEntity)

    @Update
    suspend fun update(key: KeyEntity)

    @Delete
    suspend fun delete(key: KeyEntity)

    @Query("SELECT * from keys WHERE submissionid= :submissionId")
    fun getKeySubmission(submissionId: Int): Flow<KeyEntity>
}