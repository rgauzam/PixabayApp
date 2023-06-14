package com.example.pixabayapp.presentation.uiState

data class ImageItemUiState(
    val id: Int,
    val url: String,
    val user: String,
    val tags: String
)

val ImageItemUiState.tagList: List<String> get() = tags.split(",")