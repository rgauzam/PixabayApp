package com.example.pixabayapp.data.repository

import com.example.pixabayapp.data.Outcome
import com.example.pixabayapp.data.dataSource.ImagesRemoteDataSource
import com.example.pixabayapp.data.model.ImageItem
import com.example.pixabayapp.data.model.SearchImagesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImagesRepository @Inject constructor(
    private val remoteDataSource: ImagesRemoteDataSource
) {

    val images = mutableMapOf<Int, ImageItem>()

    suspend fun getImages(query: String): Flow<Outcome<SearchImagesResponse>> {
        return remoteDataSource.getImages(query)
    }


    suspend fun getImageDetails(id: Int): Flow<Outcome<SearchImagesResponse>> {
        val image = images.get(id)

        if (image == null) {
            return remoteDataSource.getImageDetails(id).onEach { item ->

                if (item is Outcome.Success) images.put(id, item.data.hits[0])
            }
        }
        return flowOf(Outcome.Success(SearchImagesResponse(1, 1, listOf(image))))
    }
}