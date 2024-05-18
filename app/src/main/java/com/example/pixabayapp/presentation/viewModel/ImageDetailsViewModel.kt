package com.example.pixabayapp.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.pixabayapp.data.repository.ImagesRepository
import com.example.pixabayapp.presentation.uiState.DetailsUiState
import com.example.pixabayapp.presentation.uiState.ItemDetailsUiState
import com.example.pixabayapp.data.Outcome
import com.example.pixabayapp.presentation.uiState.UIState
import com.example.pixabayapp.presentation.view.screen.Nav.IMAGE_DETAILS_ID_PARAM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val imagesRepository: ImagesRepository
) :
    ViewModel() {

    private val imageId: Int = checkNotNull(savedStateHandle[IMAGE_DETAILS_ID_PARAM])

    private val _uiState: MutableStateFlow<DetailsUiState> =
        MutableStateFlow(DetailsUiState(UIState.Loading))
    val uiState: StateFlow<DetailsUiState?> = _uiState

    init {
        loadItem()
    }

    fun loadItem() {
        CoroutineScope(Dispatchers.Default).launch {
            imagesRepository.getImageDetails(imageId)
                .collect { result ->
                    when (result) {
                        is Outcome.Success -> {
                            val imageDetails = result.data.hits[0]
                            val uiState = ItemDetailsUiState(
                                imageDetails.largeImageURL,
                                imageDetails.user,
                                imageDetails.tags,
                                imageDetails.likes,
                                imageDetails.downloads,
                                imageDetails.comments
                            )
                            _uiState.value = DetailsUiState(UIState.Success(uiState))
                        }

                        is Outcome.Error -> {
                            _uiState.value = DetailsUiState(UIState.Error(result.error))
                        }

                    }
                }
        }
    }
}

