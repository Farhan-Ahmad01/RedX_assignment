package com.example.clickretina_assignment.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private const val baseUrl = "https://raw.githubusercontent.com/"

    private fun getInstance(): Retrofit {
        return Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build()
    }

    val profileApi : ProfileApi = getInstance().create(ProfileApi::class.java)

}