package com.mikhailn.wolt_assignment.data.domain

data class Restaurant(
    val id: String,
    val imageUrl: String,
    val shortDescription: String?,
    val isFavorite: Boolean,
    val name: String
)
