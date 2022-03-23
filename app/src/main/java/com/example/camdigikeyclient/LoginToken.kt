package com.example.camdigikeyclient

import com.google.gson.annotations.SerializedName

data class LoginToken(
    @SerializedName("loginToken")
    val loginToken: String,
    @SerializedName("error")
    val error: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("loginUrl")
    val loginUrl: String
    )

