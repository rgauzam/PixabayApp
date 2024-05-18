package com.example.pixabayapp.presentation.uiState

import androidx.compose.runtime.Stable

@Stable
data class SearchUiState(val searchingText: String, val uiState: UIState<List<ImageItemUiState>>)