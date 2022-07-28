package com.mprusina.killingk.data.entities

import com.google.gson.annotations.SerializedName

/**
 * Model class for profile data API response
 */
data class ProfileResponse(
    @SerializedName("success") var success: Boolean,
    @SerializedName("data") var data: ProfileResponseData
)
