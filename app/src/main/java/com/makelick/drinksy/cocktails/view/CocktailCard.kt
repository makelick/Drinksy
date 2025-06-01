package com.makelick.drinksy.cocktails.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.makelick.drinksy.cocktails.data.Cocktail

@Composable
fun CocktailCard(
    cocktail: Cocktail,
    onFavoriteClick: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(cocktail.isFavorite) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(cocktail.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "${cocktail.name} image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (onFavoriteClick != null) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .align(Alignment.TopEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                            .clickable {
                                isFavorite = !isFavorite
                                onFavoriteClick(isFavorite)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = cocktail.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cocktail.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                RatingBar(
                    rating = cocktail.rating,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    modifier: Modifier = Modifier,
    starCount: Int = 5,
    activeColor: Color = Color(0xFFFFC107),
    inactiveColor: Color = Color.Gray.copy(alpha = 0.5f)
) {
    Row(modifier = modifier) {
        for (i in 1..starCount) {
            val isFilled = i <= rating
//            val isHalfFilled = !isFilled && (i - 0.5f <= rating)

            Icon(
                imageVector = if (isFilled) Icons.Default.Star else Icons.Default.StarBorder,
                contentDescription = null,
                tint = if (isFilled) activeColor else inactiveColor,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(2.dp))
        }
    }
}

@Preview
@Composable
private fun CocktailCardPreview() {
    val sampleCocktail = Cocktail(
        id = "1",
        name = "Mojito",
        description = "A refreshing Cuban cocktail with rum, lime, mint, and sugar",
        imageUrl = "https://www.thecocktaildb.com/images/media/drink/sxpcj71487603345.jpg",
        ingredients = listOf("White rum", "Lime juice", "Sugar", "Mint", "Soda water"),
        instructions = "Muddle mint leaves with sugar and lime juice. Add rum and ice, top with soda water.",
        category = "Classics",
        rating = 4.5f,
        reviews = emptyList(),
        isFavorite = true
    )

    CocktailCard(
        cocktail = sampleCocktail,
        onFavoriteClick = { }
    )
}
