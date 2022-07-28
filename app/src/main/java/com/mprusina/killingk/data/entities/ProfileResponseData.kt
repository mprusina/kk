package com.mprusina.killingk.data.entities

import com.google.gson.annotations.SerializedName

/**
 * Model class for profile data API response
 */
data class ProfileResponseData(
    @SerializedName("title") var title: String,
    @SerializedName("message") var message: String
)
