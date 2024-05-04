package com.ngxqt.mdm.di

import com.ngxqt.mdm.data.local.UserPreferences
import kotlinx.coroutines.flow.Flow

interface UserPreferencesProvider {
    fun getBaseUrl(): Flow<String?>
}

class UserPreferencesProviderImpl(private val userPreferences: UserPreferences) : UserPreferencesProvider {
    override fun getBaseUrl(): Flow<String?> {
        return userPreferences.accessBaseUrl
    }
}