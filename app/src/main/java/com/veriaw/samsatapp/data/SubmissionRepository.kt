package com.veriaw.samsatapp.data

import android.app.Application
import com.veriaw.kriptografiapp.data.entity.UserEntity
import com.veriaw.kriptografiapp.data.local.AppDatabase
import com.veriaw.kriptografiapp.data.local.UserDao
import com.veriaw.samsatapp.data.entity.SubmissionEntity
import com.veriaw.samsatapp.data.local.SubmissionDao
import kotlinx.coroutines.flow.Flow

class SubmissionRepository(
    application: Application
) {
    private val submissionDao: SubmissionDao

    init {
        val db = AppDatabase.getInstance(application)
        submissionDao = db.submissionDao()
    }

    suspend fun insert(submission: SubmissionEntity){
        submissionDao.insert(submission)
    }

    suspend fun updateStatusAndPayment(status: String, payment: String, submissionId: Int, price: Int) {
        submissionDao.updateStatusByAdmin(status, payment, submissionId, price)
    }

    fun getDetailSubmission(subId: Int): Flow<SubmissionEntity>{
        return submissionDao.getDetailSubmission(subId)
    }


    fun getSubmissionByUser(userId: Int): Flow<List<SubmissionEntity>>{
        return submissionDao.getSubmissionByUser(userId)
    }

    fun getSubmissionByAdmin(): Flow<List<SubmissionEntity>>{
        return submissionDao.getSubmissionByAdmin()
    }

    fun getHistory(userId: Int): Flow<List<SubmissionEntity>>{
        return submissionDao.getHistory(userId)
    }

    suspend fun getLastInsertedRowId(): Int {
        return submissionDao.getLastInsertedRowId()
    }
}