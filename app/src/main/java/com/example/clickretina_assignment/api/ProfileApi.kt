package com.example.clickretina_assignment.api

import retrofit2.Response
import retrofit2.http.GET

interface ProfileApi {
    @GET("android-assesment/profile/refs/heads/main/data.json")
    suspend fun getProfileData() : Response<ProfileDataModel>

}