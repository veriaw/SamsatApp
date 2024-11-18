package com.veriaw.samsatapp.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.veriaw.kriptografiapp.data.UserRepository
import com.veriaw.kriptografiapp.data.entity.UserEntity
import com.veriaw.samsatapp.data.KeyRepository
import com.veriaw.samsatapp.data.entity.KeyEntity
import kotlinx.coroutines.launch

class KeyViewModel(application: Application): ViewModel() {
    private val keyRepository: KeyRepository = KeyRepository(application)
    fun getKeySubmission(submissionId: Int): LiveData<KeyEntity> = keyRepository.getKeySubmission(submissionId).asLiveData()
    fun insertKey(key: KeyEntity){
        viewModelScope.launch {
            keyRepository.insertUser(key) // Asumsikan ini mengembalikan ID yang dihasilkan
        }
    }
}