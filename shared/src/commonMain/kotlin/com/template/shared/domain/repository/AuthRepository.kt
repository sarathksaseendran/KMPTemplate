package com.template.shared.domain.repository

import com.template.shared.domain.models.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<User>
    fun getAuthStatus(): Flow<Boolean>
    fun getLoggedInUser(): User?
    suspend fun logout()
}
