package com.trifork.timandroid.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * AuthenticatedUsers helps us save the user defined userName, with the TIM library returned user id
 * Be aware that this code does not take error handling into account. It should therefore not be used directly in production code.
 */
class AuthenticatedUsers(val context: Context) {
    private val sharedPreferencesUtil: SharedPreferencesUtil
    private val availableUsers = "AvailableUsers"

    init {
        val appContext = context.applicationContext
        sharedPreferencesUtil = SharedPreferencesUtil(appContext)
    }

    private val availableUserIds: Map<String, String>
        get() {
            return try {
                Json.decodeFromString(sharedPreferencesUtil.get(availableUsers))
            }
            catch (throwable: Throwable) {
                emptyMap()
            }
        }

    fun getName(userId: String) : String? = availableUserIds[userId]

    fun clear(userId: String) {
        val availableUserNames = availableUserIds.toMutableMap()
        availableUserNames.remove(userId)

        encodeAndStore(availableUserNames)
    }

    fun addAvailableUser(userId: String, userName: String) {
        val availableUserNames = availableUserIds.toMutableMap()
        availableUserNames.put(userId, userName)

        encodeAndStore(availableUserNames)
    }

    private fun encodeAndStore(users: Map<String, String> ) {
        sharedPreferencesUtil.store(
            Json.encodeToString(users),
            availableUsers
        )
    }
}

private class SharedPreferencesUtil(val context: Context) {
    private val fileKey = "SharedPreferenceFileKey"
    private fun getSharedPref(context: Context) = context.getSharedPreferences(fileKey, Context.MODE_PRIVATE)
    private val sharedPreferences: SharedPreferences

    init {
        val appContext = context.applicationContext
        sharedPreferences = getSharedPref(appContext)
    }

    fun store(
        data: String,
        storageKey: String
    ) = performEditAndCommit {
        it.putString(storageKey, data)
    }

    fun get(storageKey: String): String {
        return sharedPreferences.getString(
            storageKey,
            ""
        ) ?: ""
    }

    private fun performEditAndCommit(action: (SharedPreferences.Editor) -> Unit) {
        sharedPreferences.edit(commit = true, action)
    }
}