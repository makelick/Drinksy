package com.makelick.drinksy.reviews.data

import com.makelick.drinksy.profile.data.User

data class ReviewWithUser(
    val review: Review,
    val user: User
)