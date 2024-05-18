package com.example.pixabayapp.presentation.uiState

sealed class UIState<out T> {
    object Loading : UIState<Nothing>()
    data class Success<T>(val data: T) : UIState<T>()
    data class Error(val error: AppError) : UIState<Nothing>()
}

sealed class AppError {
    object NetworkError : AppError()
    object ServerError : AppError()
    object ExternalServiceError : AppError()
    data class UnknownError(val exception: Throwable) : AppError()
}
