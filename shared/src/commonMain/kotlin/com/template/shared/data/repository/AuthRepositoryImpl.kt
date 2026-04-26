package com.template.shared.data.repository

import com.russhwolf.settings.Settings
import com.template.shared.domain.models.AuthResponse
import com.template.shared.domain.models.User
import com.template.shared.domain.models.toUser
import com.template.shared.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val settings: Settings
) : AuthRepository {

    private val _isLoggedIn = MutableStateFlow(settings.getString(KEY_TOKEN, "").isNotEmpty())
    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun login(username: String, password: String): Result<User> {
        return try {
            val response = httpClient.post("https://dummyjson.com/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password))
            }
            
            if (response.status.value in 200..299) {
                val authResponse = response.body<AuthResponse>()
                saveAuthData(authResponse)
                Result.success(authResponse.toUser())
            } else {
                Result.failure(Exception("Login failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAuthStatus(): Flow<Boolean> = _isLoggedIn.asStateFlow()

    override fun getLoggedInUser(): User? {
        val userJson = settings.getString(KEY_USER, "")
        return if (userJson.isNotEmpty()) {
            json.decodeFromString<User>(userJson)
        } else {
            null
        }
    }

    override suspend fun logout() {
        settings.remove(KEY_TOKEN)
        settings.remove(KEY_USER)
        _isLoggedIn.value = false
    }

    private fun saveAuthData(authResponse: AuthResponse) {
        settings.putString(KEY_TOKEN, authResponse.accessToken)
        settings.putString(KEY_USER, json.encodeToString(authResponse.toUser()))
        _isLoggedIn.value = true
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_USER = "auth_user"
    }
}
