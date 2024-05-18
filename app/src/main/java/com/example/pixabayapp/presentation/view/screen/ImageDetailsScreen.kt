package com.example.pixabayapp.presentation.view.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.pixabayapp.R
import com.example.pixabayapp.presentation.uiState.ItemDetailsUiState
import com.example.pixabayapp.presentation.uiState.UIState
import com.example.pixabayapp.presentation.view.components.ErrorBox
import com.example.pixabayapp.presentation.viewModel.ImageDetailsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ImageDetailsScreen(viewModel: ImageDetailsViewModel, navHostController: NavHostController) {
    val state = viewModel.uiState.collectAsState().value
    state ?: return

    Scaffold(
        topBar = {
            NavTopBar(
                title = stringResource(id = R.string.details),
                navHostController = navHostController
            )
        }
    ) {
        when (val uiState = state.uiState) {
            is UIState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.testTag(stringResource(R.string.loading_indicator)))
                }
            }

            is UIState.Success<ItemDetailsUiState> -> {
                ImageDetailsView(uiState.data)
            }

            is UIState.Error -> {
                ErrorBox(appError = uiState.error) {
                    viewModel.loadItem()
                }
            }
        }
    }
}

@Composable
fun ImageDetailsView(state: ItemDetailsUiState) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))) {
            item {
                AsyncImage(
                    model = state.bigUrl,
                    contentDescription = stringResource(R.string.label_detailed_image),
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.image_height))
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))

                InfoRow(header = stringResource(R.string.label_name), content = state.userName)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
                InfoRow(header = stringResource(R.string.label_tags), content = state.tags)
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
                InfoRow(header = stringResource(R.string.label_likes), content = "${state.likes}")
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                InfoRow(
                    header = stringResource(R.string.label_downloads),
                    content = "${state.downloads}"
                )
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                InfoRow(
                    header = stringResource(R.string.label_comments),
                    content = "${state.comments}"
                )
            }
        }
    }
}

@Composable
fun InfoRow(header: String, content: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_small)),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = header,
            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavTopBar(
    modifier: Modifier = Modifier,
    title: String,
    navHostController: NavHostController
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
        },
        navigationIcon = {
            IconButton(onClick = { navHostController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        modifier = modifier

    )
}