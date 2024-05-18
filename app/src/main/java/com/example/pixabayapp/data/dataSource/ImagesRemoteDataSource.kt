package com.example.pixabayapp.data.dataSource

import com.example.pixabayapp.BuildConfig
import com.example.pixabayapp.data.model.SearchImagesResponse
import com.example.pixabayapp.presentation.uiState.AppError
import com.example.pixabayapp.data.Outcome
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject


class ImagesRemoteDataSource @Inject constructor(private val client: HttpClient) {

    private val baseUrl = "https://pixabay.com/api"
    private val apiKey = BuildConfig.API_KEY

    suspend fun getImages(query: String): Flow<Outcome<SearchImagesResponse>> = flow {
        try {
            val response: SearchImagesResponse = client.get {
                url("$baseUrl/?key=$apiKey&q=$query&image_type=photo")
            }.body()
            emit(Outcome.Success(response))
        } catch (e: Exception) {
            emit(Outcome.Error(mapToAppError(e)))
        }
    }

    suspend fun getImageDetails(id: Int): Flow<Outcome<SearchImagesResponse>> = flow {
        try {
            val response: SearchImagesResponse = client.get {
                url("$baseUrl/?key=$apiKey&id=$id&image_type=photo")
            }.body()
            emit(Outcome.Success(response))
        } catch (e: Exception) {
            emit(Outcome.Error(mapToAppError(e)))
        }
    }

    private fun mapToAppError(e: Exception): AppError {
        return when (e) {
            is ClientRequestException -> AppError.ServerError // 4xx errors
            is ServerResponseException -> AppError.ServerError // 5xx errors
            is SocketTimeoutException,
            is IOException -> AppError.NetworkError // Network errors
            else -> AppError.UnknownError(e) // Other unknown errors
        }
    }
}