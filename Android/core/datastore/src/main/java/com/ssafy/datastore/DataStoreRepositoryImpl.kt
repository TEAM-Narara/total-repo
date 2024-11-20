package com.ssafy.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.model.user.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepositoryImpl @Inject constructor(@ApplicationContext val context: Context) :
    DataStoreRepository {
    private val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = USER)

    override suspend fun saveUser(user: User) {
        context.datastore.edit { preferences ->
            val id = longPreferencesKey(USER_ID)
            val nickname = stringPreferencesKey(USER_NICKNAME)
            val email = stringPreferencesKey(USER_EMAIL)
            val profile = stringPreferencesKey(USER_PROFILE)

            user.memberId.takeIf { it != 0L }?.let { preferences[id] = it }
            user.nickname.ifNotBlank()?.let { preferences[nickname] = it }
            user.email.ifNotBlank()?.let { preferences[email] = it }
            user.profileImgUrl.ifNotBlank()?.let { preferences[profile] = it }
        }
    }

    override suspend fun getUser(): User {
        val preferences = context.datastore.data.first()
        return User(
            memberId = preferences[longPreferencesKey(USER_ID)] ?: 0L,
            nickname = preferences[stringPreferencesKey(USER_NICKNAME)] ?: "",
            email = preferences[stringPreferencesKey(USER_EMAIL)] ?: "",
            profileImgUrl = preferences[stringPreferencesKey(USER_PROFILE)] ?: ""
        )
    }

    override suspend fun clearUser() {
        context.datastore.edit { preferences ->
            preferences.remove(stringPreferencesKey(USER_NICKNAME))
            preferences.remove(stringPreferencesKey(USER_EMAIL))
            preferences.remove(stringPreferencesKey(USER_PROFILE))
        }
    }

    override suspend fun saveAccessToken(token: String) {
        context.datastore.edit { preferences ->
            preferences[stringPreferencesKey(ACCESS_TOKEN)] = token
        }
    }

    override suspend fun getAccessToken(): String {
        val preferences = context.datastore.data.first()
        return preferences[stringPreferencesKey(ACCESS_TOKEN)] ?: ""
    }

    override suspend fun clearAccessToken() {
        context.datastore.edit { preferences ->
            preferences.remove(stringPreferencesKey(ACCESS_TOKEN))
        }
    }

    override suspend fun saveRefreshToken(token: String) {
        context.datastore.edit { preferences ->
            preferences[stringPreferencesKey(REFRESH_TOKEN)] = token
        }
    }

    override suspend fun getRefreshToken(): String {
        val preferences = context.datastore.data.first()
        return preferences[stringPreferencesKey(REFRESH_TOKEN)] ?: ""
    }

    override suspend fun clearRefreshToken() {
        context.datastore.edit { preferences ->
            preferences.remove(stringPreferencesKey(REFRESH_TOKEN))
        }
    }

    override suspend fun clearAll() {
        context.datastore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun getStompOffset(topic: String): Long {
        val preferences = context.datastore.data.first()
        return preferences[longPreferencesKey("$TOPIC_OFFSET/$topic")] ?: -1L
    }

    override suspend fun saveStompOffset(topic: String, offset: Long) {
        if (offset <= getStompOffset(topic)) return
        context.datastore.edit { preferences ->
            preferences[longPreferencesKey("$TOPIC_OFFSET/$topic")] = offset
        }
    }

    private fun String?.ifNotBlank() = if (!this.isNullOrBlank()) this else null

    companion object {
        const val USER = "user"
        const val USER_ID = "user_id"
        const val USER_NICKNAME = "user_nickname"
        const val USER_EMAIL = "user_email"
        const val USER_PROFILE = "user_profile"

        const val ACCESS_TOKEN = "access_token"
        const val REFRESH_TOKEN = "refresh_token"

        const val TOPIC_OFFSET = "topic_offset"
    }
}
