package com.veriaw.samsatapp.model

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.veriaw.kriptografiapp.data.UserRepository
import com.veriaw.kriptografiapp.data.entity.UserEntity
import com.veriaw.samsatapp.data.SubmissionRepository
import com.veriaw.samsatapp.data.entity.SubmissionEntity
import kotlinx.coroutines.launch

class SubmissionViewModel(application: Application): ViewModel() {
    private val submissionRepository: SubmissionRepository = SubmissionRepository(application)

    private val _insertedKeyId = MutableLiveData<Int>()
    val insertedKeyId: LiveData<Int> get() = _insertedKeyId

    fun getDetailSubmission(subId: Int): LiveData<SubmissionEntity> = submissionRepository.getDetailSubmission(subId).asLiveData()
    fun getSubmissionByUser(userId: Int): LiveData<List<SubmissionEntity>> = submissionRepository.getSubmissionByUser(userId).asLiveData()
    fun getSubmissionByAdmin(): LiveData<List<SubmissionEntity>> = submissionRepository.getSubmissionByAdmin().asLiveData()
    fun getHistory(userId: Int): LiveData<List<SubmissionEntity>> = submissionRepository.getHistory(userId).asLiveData()
    fun insertSubmission(submission: SubmissionEntity){
        viewModelScope.launch {
            submissionRepository.insert(submission)
            val id=submissionRepository.getLastInsertedRowId()
            _insertedKeyId.postValue(id)
        }
    }
    fun updateSubmission(status: String, payment: String, submissionId: Int, price: Int){
        viewModelScope.launch {
            submissionRepository.updateStatusAndPayment(status, payment, submissionId, price)
        }
    }
}