package com.mikhailn.wolt_assignment.data.domain

import com.mikhailn.wolt_assignment.data.api.WoltRestaurantsApi
import com.mikhailn.wolt_assignment.data.local.RestaurantPreferences
import javax.inject.Inject

class RestaurantRepository @Inject constructor(
    private val api: WoltRestaurantsApi,
    private val preferences: RestaurantPreferences
) {

    suspend fun getRestaurants(place: Place): Result<List<Restaurant>> {
        val response = runCatching {
            api.getRestaurants(place.longitude, place.latitude)
            .sections[1]
            .items
            .take(15)
            .map {
                Restaurant(
                    // Not-null assertion is a bad move.
                    // But we do it here for the sake of simplicity, cause we
                    // are sure that it is definitely not null :)
                    it.venue!!.id,
                    it.image.url,
                    it.venue.shortDescription,
                    preferences.isFavourite(it.venue.id),
                    it.venue.name
                )
            }
        }

        return response
    }

    fun addToFavorites(restaurant: Restaurant) {
        preferences.addToFavourites(restaurant.id)
    }

    fun removeFromFavorites(restaurant: Restaurant) {
        preferences.removeFromFavourites(restaurant.id)
    }
}
