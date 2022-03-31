package com.mikhailn.wolt_assignment.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class RestaurantPreferences @Inject constructor(
    private val preferences: SharedPreferences
) {
    fun isFavourite(id: String): Boolean {
        return preferences.getBoolean(id, false)
    }

    fun addToFavourites(id: String) {
        with(preferences.edit()) {
            putBoolean(id, true)
            apply()
        }
    }

    fun removeFromFavourites(id: String) {
        with(preferences.edit()) {
            putBoolean(id, false)
            apply()
        }
    }
}
