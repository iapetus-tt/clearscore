package com.frogbucket.clearscore.techtest.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frogbucket.clearscore.techtest.R
import com.frogbucket.clearscore.techtest.model.CreditReportInfo
import com.frogbucket.clearscore.techtest.retrofit.CreditRepository
import kotlinx.coroutines.*

class DonutViewModel constructor(private val creditRepository: CreditRepository): ViewModel() {
    val creditRating = MutableLiveData<CreditReportInfo>()
    val errorMessage = MutableLiveData<Int>()
    var job: Job? = null

    fun getCreditRating() {
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = creditRepository.getUserData()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        response.body()?.creditReportInfo?.let {
                            creditRating.postValue(it)
                            errorMessage.postValue(0)
                        }
                    } else {
                    errorMessage.postValue(R.string.error)
                    }
                }
            } catch (e: Exception) {
                errorMessage.postValue(R.string.error)
            }

        }

    }
}