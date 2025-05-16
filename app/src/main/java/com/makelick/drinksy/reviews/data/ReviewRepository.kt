package com.makelick.drinksy.reviews.data

object ReviewRepository {
    private val reviews = mutableMapOf<String, MutableList<Review>>()

    fun getReviewsForElement(elementId: String): List<Review> {
        return reviews[elementId] ?: emptyList()
    }

    fun addReview(elementId: String, review: Review) {
        if (!reviews.containsKey(elementId)) {
            reviews[elementId] = mutableListOf()
        }
        reviews[elementId]?.add(review)
    }
}