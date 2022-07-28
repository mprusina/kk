package com.mprusina.killingk.data

import com.mprusina.killingk.data.api.ProfileService
import com.mprusina.killingk.data.entities.ProfileResponse
import javax.inject.Inject

/**
 * Repository class for Profile data
 * completeProfile() fetches data from an endpoint using ProfileService API
 */
class ProfileRepository @Inject constructor(private val profileService: ProfileService) {
    suspend fun completeProfile(): ProfileResponse {
        return profileService.completeProfile()
    }
}