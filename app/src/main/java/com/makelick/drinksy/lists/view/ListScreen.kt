package com.makelick.drinksy.lists.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.makelick.drinksy.cocktails.view.CocktailCard

@Composable
fun ListScreen(
    listId: String,
    navigateBack: () -> Unit,
    navigateToCocktail: (String) -> Unit,
    onShareList: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = hiltViewModel()
) {
    // Load cocktails when listId changes
    LaunchedEffect(listId) {
        viewModel.loadCocktailsForList(listId)
    }

    val list by viewModel.list.collectAsState()

    Scaffold(
        topBar = {
            ListTopAppBar(
                title = list.name.ifEmpty { "Cocktail List" },
                onBackPressed = navigateBack,
                onShare = onShareList
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (list.cocktails.isEmpty()) {
                com.makelick.drinksy.cocktails.view.EmptyState(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list.cocktails) { cocktail ->
                        CocktailCard(
                            cocktail = cocktail,
                            onFavoriteClick = { isFavorite ->
                                viewModel.updateCocktailFavoriteStatus(cocktail.id, isFavorite)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navigateToCocktail(cocktail.id)
                                }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTopAppBar(
    title: String,
    onBackPressed: () -> Unit,
    onShare: () -> Unit
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
            IconButton(onClick = { onShare() }) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Share list"
                )
            }
        }
    )
}
