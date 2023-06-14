package com.example.pixabayapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ImageItem(
    val id: Int,
    val previewURL: String,
    val user: String,
    val tags: String,
    val largeImageURL: String,
    val likes: Int,
    val downloads: Int,
    val comments: Int
)