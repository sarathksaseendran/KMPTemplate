package com.template.shared.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String,
    val image: String,
    val accessToken: String,
    val refreshToken: String
)

fun AuthResponse.toUser(): User = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    gender = gender,
    image = image
)
