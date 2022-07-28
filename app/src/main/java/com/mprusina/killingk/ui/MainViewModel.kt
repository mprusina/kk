package com.mprusina.killingk.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mprusina.killingk.data.ProfileRepository
import com.mprusina.killingk.data.entities.ProfileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel class for MainActivity
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {

    private val profileData = MutableLiveData<ProfileResponse>()
    private var isProfileCompleted: Boolean = false

    /**
     * Launches coroutine to make a network call and fetch response data back to UI through LiveData
     */
    fun completeProfile() {
        viewModelScope.launch {
            delay(2000) // Deliberate delay to give time for animation display
            val profileResponse = repository.completeProfile()
            profileData.postValue(profileResponse)
            if (profileResponse.success)
                isProfileCompleted = true
        }
    }

    /**
     * Return profileData LiveData property to observe for response from completeProfile() network request
     */
    fun getProfileResponse(): LiveData<ProfileResponse> {
        return profileData
    }

    /**
     * Return boolean whether profile is already completed or not,
     * to manage subsequent user requests to complete profile and avoid unnecessary network requests
     */
    fun isProfileCompleted(): Boolean {
        return isProfileCompleted
    }
}