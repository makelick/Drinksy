package com.makelick.drinksy.lists.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ShareListScreen(
    listId: String,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShareListViewModel = hiltViewModel()
) {
    Text(
        text = "Share $listId List Screen",
        modifier = modifier
    )
}