@file:OptIn(ExperimentalFoundationApi::class)

package com.example.pixabayapp.presentation.view.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.pixabayapp.R
import com.example.pixabayapp.presentation.uiState.ImageItemUiState
import com.example.pixabayapp.presentation.uiState.UIState
import com.example.pixabayapp.presentation.uiState.tagList
import com.example.pixabayapp.presentation.view.screen.Nav.IMAGE_DETAILS_SCREEN_ROUTE
import com.example.pixabayapp.presentation.viewModel.SearchImagesViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode

@Composable
fun SearchImagesScreen(viewModel: SearchImagesViewModel, navHostController: NavHostController) {
    val state = viewModel.uiState.collectAsState().value
    val listState = state.uiState

    Column {
        SearchBar(modifier = Modifier
            .padding(5.dp),
            viewModel = viewModel, onSearch = {
            viewModel.loadImages()
        })
        when (listState) {
            is UIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is UIState.Success -> {
                ImageList(viewModel, listState.data, navHostController)
            }

            is UIState.Error -> {
                val error = (listState).exception
                Text(text = "Error: ${error.message}")
            }
        }
    }

}

@Composable
fun ImageList(
    viewModel: SearchImagesViewModel,
    images: List<ImageItemUiState>,
    navHostController: NavHostController
) {
    LazyColumn {
        items(images) { image ->
            ImageCard(viewModel, image, navHostController)
        }
    }
}

@Composable
fun ImageCard(
    viewModel: SearchImagesViewModel,
    image: ImageItemUiState,
    navHostController: NavHostController
) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.details_next_dialog))
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    val imageId = image.id
                    navHostController.navigate(IMAGE_DETAILS_SCREEN_ROUTE + "$imageId")
                }) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = stringResource(R.string.no))
                }
            }
        )
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
            .clickable {
                showDialog.value = true
            }
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.Top
        ) {

            AsyncImage(
                model = image.url,
                contentDescription = "",
                modifier = Modifier
                    .size(125.dp)
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f).fillMaxHeight(),) {
                Text(
                    text = "Użytkownik: ${image.user}",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Start,
                )
                TagList(tags = image.tagList)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagList(tags: List<String>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        mainAxisSpacing = 2.dp,
        mainAxisSize = SizeMode.Wrap
    ) {
        tags.forEach { tag ->
            SuggestionChip(
                onClick = { },
                colors = AssistChipDefaults.assistChipColors(
                    leadingIconContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                label = {

                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                    )

                }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    viewModel: SearchImagesViewModel,
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {},
) {
    var textFieldValue = viewModel.uiState.collectAsState().value

    OutlinedTextField(
        value = textFieldValue.searchingText,
        onValueChange = {
            viewModel.updateState(viewModel.uiState.value.copy(it))
        },
        keyboardActions = KeyboardActions(
            onDone = { onSearch(textFieldValue.searchingText) }
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        label = { Text(text = hint) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = MaterialTheme.shapes.small,
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
    )
}
