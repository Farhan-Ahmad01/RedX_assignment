package com.example.clickretina_assignment

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clickretina_assignment.api.ProfileDataModel
import com.example.clickretina_assignment.api.RetrofitInstance
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {

    private val profileApi = RetrofitInstance.profileApi
    private val _profileResult = MutableLiveData<NetworkResponse<ProfileDataModel>>()
    val profileResult : LiveData<NetworkResponse<ProfileDataModel>> = _profileResult

    fun fetchUserProfile(context: Context) {
        if (!NetworkUtils.isNetworkAvailable(context)) {
            _profileResult.value = NetworkResponse.Error("No internet connection.")
            Log.d("PROFILE_VM", "No internet connection detected.")
            return
        }

        _profileResult.value = NetworkResponse.Loading
        viewModelScope.launch {
            try {
                val response = profileApi.getProfileData()
                if (response.isSuccessful) {
                    response.body()?.let {
                        _profileResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _profileResult.value = NetworkResponse.Error("Failed to fetch data: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("PROFILE_VM", "An exception occurred", e)
                _profileResult.value = NetworkResponse.Error("An unexpected error occurred.")
            }
        }
    }
}