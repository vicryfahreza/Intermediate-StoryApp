package com.vicryfahreza.storyapp.response

import com.google.gson.annotations.SerializedName

data class Login(

	@field:SerializedName("loginResult")
	val loginResult: LoginResponse,

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)

data class LoginResponse(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("userId")
	val userId: String,

	@field:SerializedName("token")
	val token: String
)
