package com.example.pixabayapp.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pixabayapp.data.Outcome
import com.example.pixabayapp.data.model.SearchImagesResponse
import com.example.pixabayapp.data.repository.ImagesRepository
import com.example.pixabayapp.presentation.uiState.ImageItemUiState
import com.example.pixabayapp.presentation.uiState.SearchUiState
import com.example.pixabayapp.presentation.uiState.UIState
import com.example.pixabayapp.presentation.view.screen.Nav.SEARCH_TEXT_PARAM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchImagesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val imagesRepository: ImagesRepository
) : ViewModel() {

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
        viewModelScope.launch {
            val searchingText = _uiState.value.searchingText
            _uiState.value = _uiState.value.copy(uiState = UIState.Loading)
            imagesRepository.getImages(searchingText)
                .collect { result ->
                    when (result) {
                        is Outcome.Success -> {
                            val uiState = cr8UiStateFromResponse(result.data)
                            _uiState.value = _uiState.value.copy(uiState = uiState)
                        }

                        is Outcome.Error -> {
                            _uiState.value =
                                _uiState.value.copy(uiState = UIState.Error(result.error))
                        }
                    }
                }
        }
    }

    fun updateState(uiState: SearchUiState) {
        _uiState.value = uiState
    }

    private fun cr8UiStateFromResponse(searchImagesResponse: SearchImagesResponse): UIState.Success<List<ImageItemUiState>> {
        val itemsUiState = searchImagesResponse.hits.map {
            ImageItemUiState(it.id, it.previewURL, it.user, it.tags)
        }

        return UIState.Success(itemsUiState)
    }

}