package com.example.pixabayapp.presentation.view.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.pixabayapp.R
import com.example.pixabayapp.presentation.uiState.ImageItemUiState
import com.example.pixabayapp.presentation.uiState.UIState
import com.example.pixabayapp.presentation.uiState.tagList
import com.example.pixabayapp.presentation.view.components.ErrorBox
import com.example.pixabayapp.presentation.view.screen.Nav.IMAGE_DETAILS_SCREEN_ROUTE
import com.example.pixabayapp.presentation.viewModel.SearchImagesViewModel
import com.google.accompanist.flowlayout.FlowRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchImagesScreen(viewModel: SearchImagesViewModel, navHostController: NavHostController) {
    val state = viewModel.uiState.collectAsState().value
    val listState = state.uiState
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SearchBar(
                modifier = Modifier,
                viewModel = viewModel,
                onSearch = { viewModel.loadImages() },
            )
        },
    ) { innerPadding ->
        when (listState) {
            is UIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.testTag(stringResource(R.string.loading_indicator)))
                }
            }

            is UIState.Success -> {
                ImageList(
                    listState.data,
                    navHostController,
                    modifier = Modifier.padding(innerPadding)
                )
            }

            is UIState.Error -> {
                ErrorBox(appError = listState.error) {
                    viewModel.loadImages()
                }
            }
        }
    }
}

@Composable
fun ImageList(
    images: List<ImageItemUiState>,
    navHostController: NavHostController,
    modifier: Modifier
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(200.dp),
        verticalItemSpacing = dimensionResource(R.dimen.card_spacer),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.card_spacer)),
        content = {
            items(images) { image ->
                ImageCard(image, navHostController)
            }
        },
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun ImageCard(
    image: ImageItemUiState,
    navHostController: NavHostController
) {
    val showDialog = remember { mutableStateOf(false) }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(text = stringResource(R.string.details_next_dialog)) },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    navHostController.navigate(IMAGE_DETAILS_SCREEN_ROUTE + image.id)
                }) {
                    Text(text = stringResource(R.string.yes))
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text(text = stringResource(R.string.no))
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog.value = true }
            .padding(dimensionResource(R.dimen.card_spacer))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column {
            AsyncImage(
                model = image.url,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimensionResource(R.dimen.spacing_medium))
            ) {
                Text(
                    text = image.user,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                FlowRow(
                    mainAxisSpacing = dimensionResource(R.dimen.spacing_small),
                    crossAxisSpacing = dimensionResource(R.dimen.spacing_small),
                    modifier = Modifier.padding(top = dimensionResource(R.dimen.spacing_small))
                ) {
                    image.tagList.forEach { tag ->
                        Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SearchBar(
    viewModel: SearchImagesViewModel,
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
) {
    val textFieldValue = viewModel.uiState.collectAsState().value.searchingText

    Row(
        modifier = modifier
            .height(dimensionResource(R.dimen.search_bar_height))
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(dimensionResource(R.dimen.corner_radius_large)),
            )
            .padding(dimensionResource(R.dimen.padding_small)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = {
                viewModel.updateState(viewModel.uiState.value.copy(searchingText = it))
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch(textFieldValue)
            }),
            decorationBox = { innerTextField ->
                if (textFieldValue.isEmpty()) {
                    Text(
                        text = stringResource(R.string.hint_search),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
                innerTextField()
            },
            modifier = Modifier
                .weight(1f)
                .padding(dimensionResource(R.dimen.padding_small))

        )

        Icon(
            imageVector = if (textFieldValue.isNotEmpty()) Icons.Filled.Clear else Icons.Filled.Search,
            contentDescription = if (textFieldValue.isNotEmpty()) stringResource(R.string.content_desc_clear_search) else stringResource(
                R.string.content_desc_search
            ),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clickable {
                    if (textFieldValue.isNotEmpty()) {
                        viewModel.updateState(viewModel.uiState.value.copy(searchingText = ""))
                    } else {
                        onSearch(textFieldValue)
                    }
                }
                .padding(dimensionResource(R.dimen.padding_small))
        )
    }
}
