package com.makelick.drinksy.profile.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun ProfileScreen(
    navigateToFavorites: () -> Unit,
    navigateToMyLists: () -> Unit,
    navigateToMyReviews: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditingUsername by remember { mutableStateOf(false) }
    var editedUsername by remember { mutableStateOf(uiState.username) }
    var isSelectingTastes by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        ProfilePicture(
            profilePictureUrl = uiState.profilePictureUrl,
            profilePictureUri = uiState.profilePictureUri,
            onImageSelected = { uri -> viewModel.updateProfilePicture(uri) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Username section
        if (isEditingUsername) {
            UsernameEditSection(
                initialUsername = uiState.username,
                onUsernameConfirmed = { newName ->
                    viewModel.updateUsername(newName)
                    isEditingUsername = false
                },
                onDismiss = { isEditingUsername = false }
            )
        } else {
            UsernameDisplaySection(
                username = uiState.username,
                onEditClick = {
                    editedUsername = uiState.username
                    isEditingUsername = true
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation buttons
        NavigationButtons(
            onFavoritesClick = navigateToFavorites,
            onMyListsClick = navigateToMyLists,
            onMyReviewsClick = navigateToMyReviews
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tastes section
        TastesSection(
            selectedTastes = uiState.selectedTastes,
            onAddClick = { isSelectingTastes = true },
            onRemoveClick = { taste -> viewModel.removeTaste(taste) }
        )

        if (isSelectingTastes) {
            TasteSelectionDialog(
                availableTastes = viewModel.availableTastes,
                selectedTastes = uiState.selectedTastes,
                onTasteSelected = { taste -> viewModel.addTaste(taste) },
                onDismiss = { isSelectingTastes = false }
            )
        }
    }
}

@Composable
fun ProfilePicture(
    profilePictureUrl: String,
    profilePictureUri: Uri?,
    onImageSelected: (Uri?) -> Unit
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }

    Box(
        modifier = Modifier
            .size(150.dp)
            .clickable { launcher.launch("image/*") },
        contentAlignment = Alignment.Center
    ) {
        if (profilePictureUri != null || profilePictureUrl.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(profilePictureUri)
                    .crossfade(true)
                    .build(),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }


        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Change Profile Picture",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(32.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    shape = CircleShape
                )
                .padding(6.dp)
        )
    }
}

@Composable
fun UsernameDisplaySection(
    username: String,
    onEditClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = username,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        IconButton(onClick = onEditClick) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Username"
            )
        }
    }
}

@Composable
fun UsernameEditSection(
    initialUsername: String,
    onUsernameConfirmed: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var username by remember { mutableStateOf(initialUsername) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = { onUsernameConfirmed(username) }) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Confirm",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel"
            )
        }
    }
}

@Composable
fun TastesSection(
    selectedTastes: List<String>,
    onAddClick: () -> Unit,
    onRemoveClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Tastes:",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        FlowLayout(
            modifier = Modifier.fillMaxWidth(),
            horizontalSpacing = 8,
            verticalSpacing = 8
        ) {
            selectedTastes.forEach { taste ->
                TasteChip(
                    taste = taste,
                    onRemoveClick = { onRemoveClick(taste) }
                )
            }

            // Add button
            Surface(
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .clickable { onAddClick() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add taste",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun TasteChip(
    taste: String,
    onRemoveClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = taste,
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemoveClick() }
            )
        }
    }
}

@Composable
fun TasteSelectionDialog(
    availableTastes: List<String>,
    selectedTastes: List<String>,
    onTasteSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Select Tastes",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                FlowLayout(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalSpacing = 8,
                    verticalSpacing = 8
                ) {
                    availableTastes.forEach { taste ->
                        val isSelected = selectedTastes.contains(taste)
                        SelectableTasteChip(
                            taste = taste,
                            isSelected = isSelected,
                            onClick = { onTasteSelected(taste) }
                        )
                    }
                }

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun SelectableTasteChip(
    taste: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = taste,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun NavigationButtons(
    onFavoritesClick: () -> Unit,
    onMyListsClick: () -> Unit,
    onMyReviewsClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onFavoritesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Favorites")
        }

        Button(
            onClick = onMyListsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("My Lists")
        }

        Button(
            onClick = onMyReviewsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("My Reviews")
        }
    }
}

// Custom layout for wrapping chips into multiple rows
@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    horizontalSpacing: Int = 0,
    verticalSpacing: Int = 0,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val horizontalSpacingPx = horizontalSpacing.dp.roundToPx()
        val verticalSpacingPx = verticalSpacing.dp.roundToPx()

        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val layoutWidth = constraints.maxWidth
        var xPos = 0
        var yPos = 0
        var rowHeight = 0

        placeables.forEach { placeable ->
            if (xPos + placeable.width > layoutWidth) {
                xPos = 0
                yPos += rowHeight + verticalSpacingPx
                rowHeight = 0
            }
            xPos += placeable.width + horizontalSpacingPx
            rowHeight = maxOf(rowHeight, placeable.height)
        }
        val layoutHeight = yPos + rowHeight

        layout(width = layoutWidth, height = layoutHeight) {
            var x = 0
            var y = 0
            var rowH = 0
            placeables.forEach { placeable ->
                if (x + placeable.width > layoutWidth) {
                    x = 0
                    y += rowH + verticalSpacingPx
                    rowH = 0
                }
                placeable.placeRelative(x = x, y = y)
                x += placeable.width + horizontalSpacingPx
                rowH = maxOf(rowH, placeable.height)
            }
        }
    }
}