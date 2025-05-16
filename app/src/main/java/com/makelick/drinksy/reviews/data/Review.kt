package com.makelick.drinksy.reviews.data

import java.sql.Date

data class Review(
    val id: String,
    val userId: String,
    val rating: Float,
    val comment: String,
    val date: Date = Date(System.currentTimeMillis())
)
