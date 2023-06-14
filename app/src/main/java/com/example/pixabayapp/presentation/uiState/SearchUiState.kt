package com.example.pixabayapp.presentation.uiState


data class SearchUiState(val searchingText: String, val uiState: UIState<List<ImageItemUiState>>)