package com.example.pixabayapp.presentation.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.pixabayapp.R
import com.example.pixabayapp.presentation.uiState.AppError

@Composable
fun ErrorBox(appError: AppError, onRetry: (() -> Unit)? = null) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val errorMessage = when (appError) {
                is AppError.NetworkError -> stringResource(R.string.network_error_message)
                is AppError.ServerError -> stringResource(R.string.server_error_message)
                is AppError.ExternalServiceError -> stringResource(R.string.external_service_error_message)
                is AppError.UnknownError -> stringResource(
                    R.string.unknown_error_message,
                    appError.exception.message ?: ""
                )
            }

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
            )

            onRetry?.let {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
                Button(onClick = it) {
                    Text(text = stringResource(R.string.retry))
                }
            }
        }
    }
}