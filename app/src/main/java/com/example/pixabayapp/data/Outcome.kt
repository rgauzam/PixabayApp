package com.example.pixabayapp.data

import com.example.pixabayapp.presentation.uiState.AppError

sealed class Outcome<out T> {
    data class Success<out T>(val data: T) : Outcome<T>()
    data class Error(val error: AppError) : Outcome<Nothing>()
}