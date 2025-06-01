package com.makelick.drinksy.core

import android.content.Intent
import android.net.Uri

data class DeepLinkData(
    val type: DeepLinkType,
    val id: String
)

enum class DeepLinkType {
    COCKTAIL,
    LIST,
    UNKNOWN
}

object DeepLinkHandler {

    fun parseDeepLink(intent: Intent?): DeepLinkData? {
        val uri = intent?.data ?: return null
        return parseUri(uri)
    }

    private fun parseUri(uri: Uri): DeepLinkData? {
        // Handle custom scheme: cocktailapp://cocktail/123 or cocktailapp://list/456
        if (uri.scheme == "cocktailapp") {
            return parseCustomScheme(uri)
        }

        // Handle HTTPS links: https://drinksyapp.42web.io/cocktail/123 or https://drinksyapp.42web.io/list/456
        if (uri.scheme == "https" && uri.host == "drinksyapp.42web.io") {
            return parseHttpsLink(uri)
        }

        return null
    }

    private fun parseCustomScheme(uri: Uri): DeepLinkData? {
        val pathSegments = uri.pathSegments
        if (pathSegments.size < 2) return null

        val type = when (pathSegments[0]) {
            "cocktail" -> DeepLinkType.COCKTAIL
            "list" -> DeepLinkType.LIST
            else -> DeepLinkType.UNKNOWN
        }

        return DeepLinkData(type, pathSegments[1])
    }

    private fun parseHttpsLink(uri: Uri): DeepLinkData? {
        val pathSegments = uri.pathSegments
        if (pathSegments.size < 2) return null

        val type = when (pathSegments[0]) {
            "cocktail" -> DeepLinkType.COCKTAIL
            "list" -> DeepLinkType.LIST
            else -> DeepLinkType.UNKNOWN
        }

        return DeepLinkData(type, pathSegments[1])
    }

    fun generateCocktailLink(cocktailId: String): String {
        return "https://drinksyapp.42web.io/cocktail/$cocktailId"
    }

    fun generateListLink(listId: String): String {
        return "https://drinksyapp.42web.io/list/$listId"
    }
}