package com.makelick.drinksy.reviews.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.makelick.drinksy.R
import com.makelick.drinksy.profile.data.User
import com.makelick.drinksy.reviews.data.Review
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(
    cocktailId: String,
    navigateToAddReview: (String) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: ReviewsViewModel = hiltViewModel()
) {
    val reviews by viewModel.reviews.collectAsState()
    val cocktailName by viewModel.cocktailName.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Load reviews when the screen is first shown
    LaunchedEffect(cocktailId) {
        viewModel.loadReviews(cocktailId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.titleLarge
                        )
                        if (cocktailName.isNotEmpty()) {
                            Text(
                                text = "for $cocktailName",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
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
                    IconButton(onClick = { /* Filter reviews */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter reviews"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navigateToAddReview(cocktailId) },
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                text = { Text("Leave Review") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (reviews.isEmpty()) {
            // Empty state
            EmptyReviewsState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onAddReview = { navigateToAddReview(cocktailId) }
            )
        } else {
            // Content state - List of reviews
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(reviews) { reviewWithUser ->
                    ReviewCard(
                        review = reviewWithUser.review,
                        user = reviewWithUser.user,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Add some space at the bottom for the FAB
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    user: User,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // User info row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User profile picture
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.profilePictureUrl)
                        .crossfade(true)
                        .placeholder(R.drawable.ic_person) // You'll need a placeholder drawable
                        .error(R.drawable.ic_person)
                        .build(),
                    contentDescription = "Profile picture of ${user.name}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // User name and review date
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = formatReviewDate(review.date),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Review rating
                ReviewRating(
                    rating = review.rating,
                    starSize = 16.dp,
                    starSpacing = 2.dp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Review content
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Review actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { /* Like review */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.ThumbUp,
                        contentDescription = "Like review",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Helpful",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(
                    onClick = { /* Report review */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = "Report review",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Report",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewRating(
    rating: Float,
    starSize: Dp = 20.dp,
    starSpacing: Dp = 2.dp,
    starCount: Int = 5
) {
    Row {
        for (i in 1..starCount) {
            val isFilled = i <= rating
            val isHalfFilled = !isFilled && (i - 0.5f <= rating)

            Icon(
                imageVector = when {
                    isFilled -> Icons.Default.Star
                    isHalfFilled -> Icons.AutoMirrored.Filled.StarHalf
                    else -> Icons.Default.StarBorder
                },
                contentDescription = null,
                tint = Color(0xFFFFC107), // Amber color for stars
                modifier = Modifier.size(starSize)
            )

            if (i < starCount) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}

@Composable
fun EmptyReviewsState(
    modifier: Modifier = Modifier,
    onAddReview: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.RateReview,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(bottom = 16.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Text(
            text = "No reviews yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Be the first to share your thoughts!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onAddReview,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Write a Review")
        }
    }
}

// Helper function to format dates
fun formatReviewDate(date: Date): String {
    val now = Calendar.getInstance()
    val reviewDate = Calendar.getInstance().apply { time = date }

    return when {
        // Same day
        now.get(Calendar.YEAR) == reviewDate.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == reviewDate.get(Calendar.DAY_OF_YEAR) -> {
            "Today"
        }
        // Yesterday
        now.get(Calendar.YEAR) == reviewDate.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - reviewDate.get(Calendar.DAY_OF_YEAR) == 1 -> {
            "Yesterday"
        }
        // Within last 7 days
        now.get(Calendar.YEAR) == reviewDate.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) - reviewDate.get(Calendar.DAY_OF_YEAR) < 7 -> {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(date) // Day name
        }
        // This year
        now.get(Calendar.YEAR) == reviewDate.get(Calendar.YEAR) -> {
            SimpleDateFormat("MMM d", Locale.getDefault()).format(date) // Month and day
        }
        // Different year
        else -> {
            SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(date) // Full date
        }
    }
}

@Preview
@Composable
private fun ReviewsScreenPreview() {
    // We would typically create sample data here for the preview
    Text(text = "Preview content would be here")
}
