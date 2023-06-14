package com.example.pixabayapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SearchImagesResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<ImageItem>
)