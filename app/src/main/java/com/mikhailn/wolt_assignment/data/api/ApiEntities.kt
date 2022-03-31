package com.mikhailn.wolt_assignment.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiRestaurantResponse(
    val sections: List<ApiRestaurantResponseSection>
)

@JsonClass(generateAdapter = true)
data class ApiRestaurantResponseSection(
    val items: List<ApiRestaurant>
)

@JsonClass(generateAdapter = true)
data class ApiRestaurant(
    val image: ApiRestaurantImage,
    val venue: ApiRestaurantVenue?
)

@JsonClass(generateAdapter = true)
data class ApiRestaurantImage(
    val url: String
)

@JsonClass(generateAdapter = true)
data class ApiRestaurantVenue(
    val id: String,
    val name: String,
    @field:Json(name = "short_description") val shortDescription: String?,
)
