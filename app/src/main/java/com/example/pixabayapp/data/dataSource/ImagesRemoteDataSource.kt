package com.example.pixabayapp.data.dataSource

import com.example.pixabayapp.BuildConfig
import com.example.pixabayapp.data.model.SearchImagesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject

class ImagesRemoteDataSource @Inject constructor(private val client: HttpClient) {

    private val baseUrl = "https://pixabay.com/api"
    private val apiKey = BuildConfig.API_KEY

    suspend fun getImages(query: String) : SearchImagesResponse {
        return client.get {
            url("$baseUrl/?key=$apiKey&q=$query&image_type=photo")
        }.body()
    }

    suspend fun getImageDetails(id: Int) : SearchImagesResponse {
        return client.get {
            url("$baseUrl/?key=$apiKey&id=$id&image_type=photo")
        }.body()
    }

}