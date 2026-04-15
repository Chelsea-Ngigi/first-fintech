package com.first.fintech.util

import android.content.Context

/*
* Used for session management
* */

object SessionManager {
    private const val PREF_NAME  = "first_prefs"
    private const val KEY_TOKEN  = "auth_token"
    private const val KEY_EMAIL  = "user_email"
    private const val KEY_NAME   = "user_name"

    fun saveSession(context: Context, token: String, email: String, name: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .putString(KEY_NAME, name)
            .apply()
    }

    fun getToken(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TOKEN, "") ?: ""

    fun getEmail(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_EMAIL, "") ?: ""

    fun getName(context: Context): String =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_NAME, "") ?: ""

    fun isLoggedIn(context: Context): Boolean = getToken(context).isNotEmpty()

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}