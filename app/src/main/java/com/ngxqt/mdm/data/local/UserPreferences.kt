package com.ngxqt.mdm.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ngxqt.mdm.data.model.User
import com.ngxqt.mdm.util.KeyStoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")

class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val appContext = context.applicationContext
    private val gson = Gson()
    private val keyStoreManager = KeyStoreManager(appContext)
    /**User Information*/
    suspend fun accessTokenString(): String? {
        val cipherText = appContext.dataStore.data.first()[ACCESS_TOKEN]
        return keyStoreManager.decryptString(cipherText,KeyStoreManager.ALIAS_TOKEN)
    }

    val accessToken: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }

    suspend fun saveToken(accessToken: String) {
        keyStoreManager.createKey(KeyStoreManager.ALIAS_TOKEN)
        val cipherText = keyStoreManager.encryptString(accessToken, KeyStoreManager.ALIAS_TOKEN)
        appContext.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = cipherText
        }
    }

    /**Base URl*/
    suspend fun accessBaseUrlString(): String? {
        Log.d("accessBaseUrlString","${appContext.dataStore.data.first()[BASE_URL]}")
        return appContext.dataStore.data.first()[BASE_URL]
    }

    val accessBaseUrl: Flow<String?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[BASE_URL]
        }

    suspend fun saveBaseUrl(baseUrl: String) {
        appContext.dataStore.edit { preferences ->
            preferences[BASE_URL] = baseUrl
            Log.d("saveBaseUrl","${baseUrl}")
        }
    }

    suspend fun accessUserInfo(): User? {
        val jsonString = appContext.dataStore.data.first()?.get(USER_INFO)
        return if (jsonString != null) {
            Gson().fromJson(jsonString, User::class.java)
        } else {
            null
        }
    }

    val accessUserInfoFlow: Flow<User?>
        get() = appContext.dataStore.data.map { preferences ->
            val json = preferences[USER_INFO] ?: return@map null
            gson.fromJson<User>(json, object : TypeToken<User>() {}.type)
        }

    suspend fun saveUserInfo(user: User) {
        appContext.dataStore.edit { preferences ->
            preferences[USER_INFO] = gson.toJson(user)
        }
    }
    /** Setting Information*/
    val accessSettingPassword: Flow<Boolean?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[SETTING_PASSWORD]
        }

    suspend fun saveSettingPassword(isTurnOn: Boolean) {
        appContext.dataStore.edit { preferences ->
            preferences[SETTING_PASSWORD] = isTurnOn
        }
    }

    suspend fun accessSettingBiometricBoolean(): Boolean? {
        return appContext.dataStore.data.first()[SETTING_BIOMETRIC]
    }

    val accessSettingBiometric: Flow<Boolean?>
        get() = appContext.dataStore.data.map { preferences ->
            preferences[SETTING_BIOMETRIC]
        }

    suspend fun saveSettingBiometric(isTurnOn: Boolean) {
        appContext.dataStore.edit { preferences ->
            preferences[SETTING_BIOMETRIC] = isTurnOn
        }
    }

    /** Clear Datastore*/
    suspend fun clearData() {
        appContext.dataStore.edit { preferences ->
            preferences.clear()
        }
        keyStoreManager.deleteKey(KeyStoreManager.ALIAS_TOKEN)
    }

    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("key_access_token")
        private val BASE_URL = stringPreferencesKey("base_url")
        private val USER_INFO = stringPreferencesKey("user_info")
        private val SETTING_PASSWORD = booleanPreferencesKey("setting_password")
        private val SETTING_BIOMETRIC = booleanPreferencesKey("setting_biometric")
    }
}