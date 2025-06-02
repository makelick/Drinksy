package com.makelick.drinksy.lists.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.makelick.drinksy.cocktails.data.Cocktail
import com.makelick.drinksy.lists.data.CocktailWithPrice
import com.makelick.drinksy.lists.data.ListType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CocktailListEditorScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    listId: String? = null,
    viewModel: CocktailListEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val allCocktails by viewModel.allCocktails.collectAsState()
    var showAddCocktailDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadImage(it) }
    }

    LaunchedEffect(listId) {
        viewModel.setListId(listId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show snackbar or toast
            viewModel.clearError()
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                Button(
                    onClick = {
                        viewModel.saveCocktailList()
                        onNavigateBack()
                    },
                    enabled = !uiState.isSaving && uiState.name.isNotBlank()
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    } else {
                        Text("Save")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Name Field
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text("List Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Cover Image
            val cockList = uiState.cocktailsWithPrice.map { it.cocktail }
            CoverImageSection(
                imageUrl = uiState.imageUrl,
                cocktails = cockList,
                isUploading = uiState.isUploading,
                onImageClick = { imagePickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description Field
            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::updateDescription,
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Public/Private Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Visibility")
                Switch(
                    checked = uiState.isPublic,
                    onCheckedChange = viewModel::updateIsPublic
                )
                Text(if (uiState.isPublic) "Public" else "Private")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Type Dropdown
            TypeDropdown(
                selectedType = uiState.type,
                onTypeSelected = viewModel::updateType
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Add Cocktail Button
            Button(
                onClick = { showAddCocktailDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Cocktail")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Cocktail List
        items(uiState.cocktailsWithPrice, key = { it.cocktail.id }) { cocktailWithPrice ->
            CocktailCard(
                cocktailWithPrice = cocktailWithPrice,
                isMenuType = uiState.type == ListType.MENU,
                onRemove = { viewModel.removeCocktail(cocktailWithPrice.cocktail.id) },
                onUpdatePrice = { price ->
                    viewModel.updateCocktailPrice(cocktailWithPrice.cocktail.id, price)
                },
                onUpdateComment = { comment ->
                    viewModel.updateCocktailComment(cocktailWithPrice.cocktail.id, comment)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Add Cocktail Dialog
    if (showAddCocktailDialog) {
        AddCocktailDialog(
            cocktails = allCocktails,
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            onAddCocktail = { viewModel.addCocktail(it) },
            onDismiss = { showAddCocktailDialog = false }
        )
    }
}

@Composable
private fun CoverImageSection(
    imageUrl: String,
    cocktails: List<Cocktail>,
    isUploading: Boolean,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clickable { onImageClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isUploading -> {
                    CircularProgressIndicator()
                }

                imageUrl.isNotEmpty() -> {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Cover Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                cocktails.isNotEmpty() -> {
                    CocktailCollage(cocktails = cocktails.take(4))
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Text("Tap to add cover image")
                    }
                }
            }
        }
    }
}

@Composable
private fun CocktailCollage(cocktails: List<Cocktail>) {
    when (cocktails.size) {
        1 -> {
            AsyncImage(
                model = cocktails[0].imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        2 -> {
            Row(modifier = Modifier.fillMaxSize()) {
                cocktails.forEach { cocktail ->
                    AsyncImage(
                        model = cocktail.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        3, 4 -> {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.weight(1f)) {
                    cocktails.take(2).forEach { cocktail ->
                        AsyncImage(
                            model = cocktail.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                if (cocktails.size > 2) {
                    Row(modifier = Modifier.weight(1f)) {
                        cocktails.drop(2).forEach { cocktail ->
                            AsyncImage(
                                model = cocktail.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeDropdown(
    selectedType: ListType,
    onTypeSelected: (ListType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Type")

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = when (selectedType) {
                    ListType.MENU -> "Menu"
                    ListType.COMPILATION -> "Compilation"
                },
                onValueChange = {},
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Menu") },
                    onClick = {
                        onTypeSelected(ListType.MENU)
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("Compilation") },
                    onClick = {
                        onTypeSelected(ListType.COMPILATION)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun CocktailCard(
    cocktailWithPrice: CocktailWithPrice,
    isMenuType: Boolean,
    onRemove: () -> Unit,
    onUpdatePrice: (String) -> Unit,
    onUpdateComment: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f)
                ) {
                    AsyncImage(
                        model = cocktailWithPrice.cocktail.imageUrl,
                        contentDescription = cocktailWithPrice.cocktail.name,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = cocktailWithPrice.cocktail.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFFFD700)
                            )
                            Text(
                                text = cocktailWithPrice.cocktail.rating.toString(),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Delete, contentDescription = "Remove")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isMenuType) {
                OutlinedTextField(
                    value = cocktailWithPrice.price,
                    onValueChange = onUpdatePrice,
                    label = { Text("Price") },
                    leadingIcon = { Text("$") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = cocktailWithPrice.comment,
                onValueChange = onUpdateComment,
                label = { Text("Comment") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCocktailDialog(
    cocktails: List<Cocktail>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddCocktail: (Cocktail) -> Unit,
    onDismiss: () -> Unit
) {
    val filteredCocktails = remember(cocktails, searchQuery) {
        if (searchQuery.isBlank()) {
            cocktails
        } else {
            cocktails.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Cocktail") },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = { Text("Search cocktails") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    modifier = Modifier.height(300.dp)
                ) {
                    items(filteredCocktails) { cocktail ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = cocktail.imageUrl,
                                    contentDescription = cocktail.name,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = cocktail.name,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            IconButton(
                                onClick = {
                                    onAddCocktail(cocktail)
                                    onDismiss()
                                }
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
