package com.frogbucket.clearscore.techtest.retrofit

class CreditRepository constructor(private val creditService: CreditService) {
    suspend fun getUserData() = creditService.getUserData()
}