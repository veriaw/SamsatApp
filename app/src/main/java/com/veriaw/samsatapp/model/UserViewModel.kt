package com.veriaw.kriptografiapp.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.veriaw.kriptografiapp.data.UserRepository
import com.veriaw.kriptografiapp.data.entity.UserEntity
import kotlinx.coroutines.launch

class UserViewModel(application: Application): ViewModel() {
    private val userRepository: UserRepository = UserRepository(application)
    fun getUserByUsername(username: String): LiveData<UserEntity> = userRepository.getUserByUsername(username).asLiveData()
    fun registerUser(userEntity: UserEntity){
        viewModelScope.launch {
            userRepository.insertUser(userEntity)
        }
    }
    fun updateUser(userEntity: UserEntity){
        viewModelScope.launch {
            userRepository.updateUser(userEntity)
        }
    }
}