package com.veriaw.kriptografiapp.data

import android.app.Application
import com.veriaw.kriptografiapp.data.entity.UserEntity
import com.veriaw.kriptografiapp.data.local.AppDatabase
import com.veriaw.kriptografiapp.data.local.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(
    application: Application
) {
    private val userDao:UserDao

    init {
        val db = AppDatabase.getInstance(application)
        userDao = db.userDao()
    }

    fun getUserByUsername(username: String): Flow<UserEntity>{
        return userDao.getUserByUsername(username)
    }

    suspend fun insertUser(user: UserEntity){
        userDao.insert(user)
    }

    suspend fun updateUser(user: UserEntity){
        userDao.update(user)
    }
}