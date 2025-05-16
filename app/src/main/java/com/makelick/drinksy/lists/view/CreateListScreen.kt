package com.makelick.drinksy.lists.view

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CreateListScreen(
    listId: String?,
    navigateBack: () -> Unit,
    navigateToShareList: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateListViewModel = hiltViewModel()
) {
    Text(
        text = "Create List Screen",
        modifier = modifier
    )
}