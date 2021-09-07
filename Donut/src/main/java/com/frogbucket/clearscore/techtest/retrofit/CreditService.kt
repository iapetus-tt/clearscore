package com.frogbucket.clearscore.techtest.retrofit

import com.frogbucket.clearscore.techtest.model.UserData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface CreditService {
    @GET("endpoint.json")
    suspend fun getUserData() : Response<UserData>

    companion object {
        var creditService: CreditService? = null
        fun getInstance() : CreditService {
            if (creditService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://android-interview.s3.eu-west-2.amazonaws.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                creditService = retrofit.create(CreditService::class.java)
            }
            return creditService!!
        }

    }
}