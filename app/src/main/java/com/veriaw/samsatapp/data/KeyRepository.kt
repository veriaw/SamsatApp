package com.veriaw.samsatapp.data

import android.app.Application
import com.veriaw.kriptografiapp.data.entity.UserEntity
import com.veriaw.kriptografiapp.data.local.AppDatabase
import com.veriaw.kriptografiapp.data.local.UserDao
import com.veriaw.samsatapp.data.entity.KeyEntity
import com.veriaw.samsatapp.data.local.KeyDao
import kotlinx.coroutines.flow.Flow

class KeyRepository(
    application: Application
) {
    private val keyDao: KeyDao

    init {
        val db = AppDatabase.getInstance(application)
        keyDao = db.keyDao()
    }

    fun getKeySubmission(submissionId: Int): Flow<KeyEntity> {
        return keyDao.getKeySubmission(submissionId)
    }

    suspend fun insertUser(key: KeyEntity){
        keyDao.insert(key)
    }

}