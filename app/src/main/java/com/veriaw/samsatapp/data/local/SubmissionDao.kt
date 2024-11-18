package com.veriaw.samsatapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.veriaw.samsatapp.data.entity.SubmissionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubmissionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(submission: SubmissionEntity)

    @Update
    suspend fun update(submission: SubmissionEntity)

    @Delete
    suspend fun delete(submission: SubmissionEntity)

    @Query("SELECT * from submissions WHERE userid= :userid")
    fun getSubmissionByUser(userid:Int): Flow<List<SubmissionEntity>>

    @Query("SELECT * from submissions")
    fun getSubmissionByAdmin(): Flow<List<SubmissionEntity>>

    @Query("SELECT * from submissions where status='Selesai' and id= :userId")
    fun getHistory(userId: Int): Flow<List<SubmissionEntity>>

    @Query("UPDATE submissions set status= :status, payment= :payment, price= :price WHERE id= :submissionId")
    suspend fun updateStatusByAdmin(status: String, payment: String, submissionId: Int, price: Int)

    @Query("SELECT max(id) from submissions")
    suspend fun getLastInsertedRowId(): Int

    @Query("SELECT * from submissions where id= :subId")
    fun getDetailSubmission(subId: Int): Flow<SubmissionEntity>
}