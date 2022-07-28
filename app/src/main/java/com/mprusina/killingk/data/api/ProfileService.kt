package com.mprusina.killingk.data.api

import com.mprusina.killingk.data.entities.ProfileResponse
import retrofit2.http.GET

/**
 * Profile data API service
 * completeProfile() fetches profile data from an endpoint
 */
interface ProfileService {
    @GET("app/chatreward-kytrn/endpoint/hello")
    suspend fun completeProfile(): ProfileResponse
}