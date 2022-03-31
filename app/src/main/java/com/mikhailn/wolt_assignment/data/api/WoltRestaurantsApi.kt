package com.mikhailn.wolt_assignment.data.api

import android.graphics.Bitmap
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

object ApiConstants {
    const val BASE_URL = "https://restaurant-api.wolt.com/v1/"
}

interface WoltRestaurantsApi {
    @GET("pages/restaurants")
    suspend fun getRestaurants(
        @Query("lon") long: String,
        @Query("lat") lat: String
    ): ApiRestaurantResponse
}
