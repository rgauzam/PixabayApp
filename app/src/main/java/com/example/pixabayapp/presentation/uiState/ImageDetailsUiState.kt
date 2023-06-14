package com.example.pixabayapp.presentation.uiState

data class ImageDetailsUiState(
    val bigUrl: String,
    val userName: String,
    val tags: String,
    val likes: Int,
    val downloads: Int,
    val comments: Int
)