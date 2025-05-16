package com.makelick.drinksy.cocktails.view

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.reviews.data.Review
import kotlinx.coroutines.launch
import java.sql.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailScreen(
    cocktailId: String,
    navigateToReviews: (String) -> Unit,
    navigateToLists: (String) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: CocktailViewModel = hiltViewModel()
) {
    val cocktail by viewModel.cocktail.collectAsState()
    val bottomSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showRecipeSheet by remember { mutableStateOf(false) }

    // Load cocktail details when the screen is first shown
    LaunchedEffect(cocktailId) {
        viewModel.loadCocktail(cocktailId)
    }

    // Display recipe bottom sheet when requested
    if (showRecipeSheet && cocktail != null) {
        ModalBottomSheet(
            onDismissRequest = { showRecipeSheet = false },
            sheetState = bottomSheetState
        ) {
            RecipeBottomSheet(
                instructions = cocktail!!.instructions,
                ingredients = cocktail!!.ingredients,
                onDismiss = {
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            showRecipeSheet = false
                        }
                    }
                }
            )
        }
    }

    Scaffold(
        topBar = {
            CocktailTopAppBar(
                title = cocktail?.name ?: "Cocktail Details",
                onBackPressed = onBackPressed,
                onFavoriteToggle = { viewModel.toggleFavoriteStatus() },
                isFavorite = cocktail?.isFavorite ?: false
            )
        }
    ) { paddingValues ->
        cocktail?.let { cocktailData ->
            CocktailContent(
                cocktail = cocktailData,
                modifier = Modifier.padding(paddingValues),
                onRecipeClick = { showRecipeSheet = true },
                onReviewsClick = { navigateToReviews(cocktailId) },
                onMapClick = { navigateToLists(cocktailId) }
            )
        } ?: run {
            // Show loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailTopAppBar(
    title: String,
    onBackPressed: () -> Unit,
    onFavoriteToggle: () -> Unit,
    isFavorite: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Go back"
                )
            }
        },
        actions = {
            IconButton(onClick = onFavoriteToggle) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (isFavorite) Color.Red else LocalContentColor.current
                )
            }
            IconButton(onClick = { /* Share cocktail */ }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share cocktail"
                )
            }
        }
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun CocktailContent(
    cocktail: Cocktail,
    modifier: Modifier = Modifier,
    onRecipeClick: () -> Unit,
    onReviewsClick: () -> Unit,
    onMapClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Hero image with gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
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

            // Gradient overlay at the bottom for better text visibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            )
                        )
                    )
            )

            // Category chip
            Surface(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
            ) {
                Text(
                    text = cocktail.category,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Rating display
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%.1f", cocktail.rating),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${cocktail.reviews.size})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Main content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Description
            Text(
                text = cocktail.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Action buttons
            ActionButtons(
                onRecipeClick = onRecipeClick,
                onReviewsClick = onReviewsClick,
                onMapClick = onMapClick,
                reviewCount = cocktail.reviews.size
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick ingredients preview
            IngredientsPreview(
                ingredients = cocktail.ingredients,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ActionButtons(
    onRecipeClick: () -> Unit,
    onReviewsClick: () -> Unit,
    onMapClick: () -> Unit,
    reviewCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Reviews button
        Button(
            onClick = onReviewsClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Comment,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Reviews ($reviewCount)"
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Recipe button
        Button(
            onClick = onRecipeClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Recipe")
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Where button
        Button(
            onClick = onMapClick,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Where")
            }
        }
    }
}

@Composable
fun IngredientsPreview(
    ingredients: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(16.dp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val displayedIngredients = if (expanded) ingredients else ingredients.take(3)

        displayedIngredients.forEach { ingredient ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = ingredient,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (!expanded && ingredients.size > 3) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "+ ${ingredients.size - 3} more",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
fun RecipeBottomSheet(
    instructions: String,
    ingredients: List<String>,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 32.dp)
    ) {
        // Title and close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recipe",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close recipe"
                )
            }
        }

        HorizontalDivider()

        // Ingredients section
        Text(
            text = "Ingredients",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        ingredients.forEachIndexed { index, ingredient ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "${index + 1}.",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(24.dp)
                )
                Text(
                    text = ingredient,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        // Instructions section
        Text(
            text = "Instructions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = instructions,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // "Try it" button
        Button(
            onClick = { /* Save to favorites or user list */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Save to My List",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Preview
@Composable
fun CocktailScreenPreview() {
    val sampleCocktail = Cocktail(
        id = "1",
        name = "Mojito",
        description = "A refreshing Cuban cocktail with rum, lime, mint, and sugar. Perfect for summer days and beach parties. This classic cocktail has a bright, tangy flavor with a hint of sweetness.",
        imageUrl = "https://example.com/mojito.jpg",
        ingredients = listOf(
            "60ml white rum",
            "30ml fresh lime juice",
            "2 tsp sugar",
            "8-10 mint leaves",
            "Soda water",
            "Crushed ice"
        ),
        instructions = "Muddle mint leaves with sugar and lime juice in a highball glass. Add rum and fill the glass with crushed ice. Top with soda water and stir gently. Garnish with a mint sprig and lime wheel.",
        category = "Classic",
        rating = 4.5f,
        reviews = List(12) {
            Review(
                "", "", 0f, "", Date(System.currentTimeMillis())
            )
        },
        isFavorite = true
    )

    CocktailContent(
        cocktail = sampleCocktail,
        onRecipeClick = {},
        onReviewsClick = {},
        onMapClick = {}
    )
}