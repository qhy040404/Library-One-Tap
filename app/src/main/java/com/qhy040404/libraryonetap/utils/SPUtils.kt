package com.qhy040404.libraryonetap.utils

import android.content.Context
import android.content.SharedPreferences
import com.qhy040404.libraryonetap.LibraryOneTapApp
import com.qhy040404.libraryonetap.constant.GlobalValues.SP_NAME

object SPUtils {
    private val sp: SharedPreferences by lazy {
        LibraryOneTapApp.app.getSharedPreferences(
            SP_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun <T> getValue(name: String, default: T): T = with(sp) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default).orEmpty()
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw java.lang.IllegalArgumentException()
        }
        @Suppress("UNCHECKED_CAST")
        res as T
    }

    fun <T> setValue(name: String, value: T) = with(sp.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            else -> throw IllegalArgumentException("This type can't be saved into Preferences")
        }.apply()
    }
}