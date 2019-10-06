package com.speciial.travelchest

import android.content.Context
import android.content.SharedPreferences

object PreferenceHelper {

    val TRIP_ID = "TRIP_ID"
    val SAVE_ONLINE = "SAVE_ONLINE"

    fun customPreference(context: Context, name: String): SharedPreferences = context.getSharedPreferences(name,
        Context. MODE_PRIVATE)

    inline fun SharedPreferences.editMe(operation: (SharedPreferences.Editor) -> Unit) {
        val editMe = edit()
        operation(editMe)
        editMe.apply()
    }

    fun SharedPreferences.Editor.put(pair: Pair<String, Any>) {
        val key = pair. first
        when (val value = pair. second) {
            is String -> putString(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
            is Long -> putLong(key, value)
            is Float -> putFloat(key, value)
            else -> error("Only primitive types can be stored in SharedPreferences")
        }
    }

    var SharedPreferences. tripId
        get() = getLong( TRIP_ID, 0)
        set(value) {
            editMe {
                it.put(TRIP_ID to value)
            }
        }
    var SharedPreferences. save_online
        get() = getBoolean(SAVE_ONLINE, false)
        set(value) {
            editMe {
                it.put(SAVE_ONLINE to value)
            }
        }

    var SharedPreferences.clearValues
        get() = run { }
        set(_) {
            editMe {
                it.remove( TRIP_ID)
            }
        }
}