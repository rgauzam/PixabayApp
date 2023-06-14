package com.example.pixabayapp.presentation.viewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.pixabayapp.data.repository.ImagesRepository
import com.example.pixabayapp.presentation.uiState.ImageDetailsUiState
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
    imagesRepository: ImagesRepository
) :
    ViewModel() {

    private val imageId: Int = checkNotNull(savedStateHandle[IMAGE_DETAILS_ID_PARAM])

    private val _uiState: MutableStateFlow<ImageDetailsUiState?> = MutableStateFlow(null)
    val uiState: StateFlow<ImageDetailsUiState?> = _uiState

    init {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                imagesRepository.getImageDetails(imageId)?.let {
                    val uiState = ImageDetailsUiState(
                        it.largeImageURL,
                        it.user,
                        it.tags,
                        it.likes,
                        it.downloads,
                        it.comments
                    )
                    _uiState.value = uiState
                }
            } catch (e: Exception) {

            }
        }
    }

}