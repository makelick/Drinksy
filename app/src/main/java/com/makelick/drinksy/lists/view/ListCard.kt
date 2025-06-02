package com.makelick.drinksy.lists.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.lists.data.CocktailList
import com.makelick.drinksy.lists.data.ListType

@Composable
fun CocktailListCard(
    cocktailList: CocktailList,
    creatorName: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Cover image or composite image
            CocktailListCoverImage(cocktailList)

            // Content overlay gradient and text
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Menu badge if applicable
                if (cocktailList.type == ListType.MENU) {
                    MenuLabel()
                }

                // Bottom text section
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = cocktailList.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (cocktailList.description.isNotBlank()) {
                        Text(
                            text = cocktailList.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Text(
                        text = "by $creatorName",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuLabel() {
    Surface(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .wrapContentSize(),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    ) {
        Text(
            text = "MENU",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CocktailListCoverImage(cocktailList: CocktailList) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (cocktailList.imageUrl.isNotBlank()) {
            // Display the main cover image
            AsyncImage(
                model = cocktailList.imageUrl,
                contentDescription = "${cocktailList.name} cover image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Create a composite image with thumbnails of first cocktails
            CompositeImageGrid(cocktailList.cocktails)
        }

        // Overlay gradient for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        ),
                        startY = 150f
                    )
                )
        )
    }
}

@Composable
private fun CompositeImageGrid(cocktails: List<Cocktail>) {
    when {
        cocktails.isEmpty() -> {
            // Show a placeholder if no cocktails exist
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        cocktails.size == 1 -> {
            // Just show the single cocktail image
            SingleCocktailImage(cocktails[0])
        }

        cocktails.size < 4 -> {
            // Show a 2-image grid
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    SingleCocktailImage(cocktails[0])
                }
                Box(modifier = Modifier.weight(1f)) {
                    SingleCocktailImage(cocktails.getOrNull(1) ?: cocktails[0])
                }
            }
        }

        else -> {
            // Show a 4-image grid
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SingleCocktailImage(cocktails[0])
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SingleCocktailImage(cocktails[1])
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SingleCocktailImage(cocktails[2])
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        SingleCocktailImage(cocktails[3])
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleCocktailImage(cocktail: Cocktail) {
    SubcomposeAsyncImage(
        model = cocktail.imageUrl,
        contentDescription = "${cocktail.name} image",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        },
        error = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = cocktail.name.take(1),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )
}

/**
 * Preview for CocktailListCard with sample data
 */
@Composable
@Preview
fun CocktailListCardPreview() {
    val sampleCocktails = listOf(
        Cocktail(
            id = "1",
            name = "Mojito",
            description = "Classic Cuban highball",
            imageUrl = "https://images.immediate.co.uk/production/volatile/sites/30/2022/06/Tequila-sunrise-fb8b3ab.jpg",
            ingredients = listOf("White rum", "Sugar", "Lime juice", "Soda water", "Mint"),
            instructions = "Muddle mint leaves with sugar and lime juice...",
            category = "Highball",
            rating = 4.5f,
            reviews = emptyList(),
            isFavorite = false
        ),
        Cocktail(
            id = "2",
            name = "Margarita",
            description = "A classic tequila cocktail",
            imageUrl = "https://images.immediate.co.uk/production/volatile/sites/30/2020/01/retro-cocktails-b12b00d.jpg?quality=90&resize=556,505",
            ingredients = listOf("Tequila", "Triple sec", "Lime juice", "Salt"),
            instructions = "Rub rim of glass with lime...",
            category = "All Day Cocktail",
            rating = 4.7f,
            reviews = emptyList(),
            isFavorite = true
        )
    )

    val sampleList = CocktailList(
        id = "list1",
        name = "Summer Favorites",
        imageUrl = "",  // Empty to test composite image
        description = "Perfect cocktails for hot summer days",
        type = ListType.COMPILATION,
        cocktails = sampleCocktails,
        creatorUser = "user123",
        isPublic = true
    )

    MaterialTheme {
        Surface {
            CocktailListCard(
                cocktailList = sampleList,
                creatorName = "Cocktail Enthusiast",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
