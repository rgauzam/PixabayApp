package com.example.pixabayapp.data.repository

import com.example.pixabayapp.data.dataSource.ImagesRemoteDataSource
import com.example.pixabayapp.data.model.ImageItem
import com.example.pixabayapp.data.model.SearchImagesResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesRepository @Inject constructor(private val imagesRemoteDataSource: ImagesRemoteDataSource) {

    private lateinit var searchImagesResponse : SearchImagesResponse
    suspend fun getImages(query: String): SearchImagesResponse {
        searchImagesResponse = imagesRemoteDataSource.getImages(query)
        return searchImagesResponse
    }

    suspend fun getImageDetails(imageId: Int) : ImageItem {
        searchImagesResponse.hits.find {
            it.id == imageId
        }?.let {
            return it
        }

        return imagesRemoteDataSource.getImageDetails(imageId).hits.get(0)
    }

}