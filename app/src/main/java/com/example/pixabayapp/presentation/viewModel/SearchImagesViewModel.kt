package com.example.pixabayapp.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.pixabayapp.data.model.SearchImagesResponse
import com.example.pixabayapp.data.repository.ImagesRepository
import com.example.pixabayapp.presentation.uiState.ImageItemUiState
import com.example.pixabayapp.presentation.uiState.SearchUiState
import com.example.pixabayapp.presentation.uiState.UIState
import com.example.pixabayapp.presentation.view.screen.Nav.SEARCH_TEXT_PARAM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchImagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val imagesRepository: ImagesRepository
) :
    ViewModel() {

    private val startSearchText: String = checkNotNull(savedStateHandle[SEARCH_TEXT_PARAM])

    private val _uiState: MutableStateFlow<SearchUiState> = MutableStateFlow(
        SearchUiState(
            startSearchText,
            UIState.Loading
        )
    )
    val uiState: StateFlow<SearchUiState> = _uiState


    init {
        loadImages()
    }

    fun loadImages() {
        CoroutineScope(Dispatchers.Default).launch {
            val searchingText = _uiState.value.searchingText
            _uiState.value = _uiState.value.copy(uiState = UIState.Loading)
            try {
                val response = searchImages(searchingText)
                val uiState = cr8UiStateFromResponse(response)
                _uiState.value = _uiState.value.copy(uiState = uiState)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(uiState = UIState.Error(e))
            }
        }
    }

    fun updateState(uiState: SearchUiState) {
        _uiState.value = uiState
    }

    private suspend fun searchImages(query: String): SearchImagesResponse {
        return imagesRepository.getImages(query)
    }

    private fun cr8UiStateFromResponse(searchImagesResponse: SearchImagesResponse): UIState.Success<List<ImageItemUiState>> {
        val itemsUiState = searchImagesResponse.hits.map {
            ImageItemUiState(it.id, it.previewURL, it.user, it.tags)
        }

        return UIState.Success(itemsUiState)
    }

}