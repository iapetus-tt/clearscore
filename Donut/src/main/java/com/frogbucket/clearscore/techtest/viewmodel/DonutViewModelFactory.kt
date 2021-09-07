package com.frogbucket.clearscore.techtest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.frogbucket.clearscore.techtest.retrofit.CreditRepository
import java.lang.IllegalArgumentException

class DonutViewModelFactory constructor(val repository: CreditRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DonutViewModel::class.java)) {
            return DonutViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown view model")
    }
}