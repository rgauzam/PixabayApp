package com.example.pixabayapp.presentation.view.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pixabayapp.R
import com.example.pixabayapp.presentation.view.screen.ImageDetailsScreen
import com.example.pixabayapp.presentation.view.screen.Nav.IMAGE_DETAILS_ID_PARAM
import com.example.pixabayapp.presentation.view.screen.Nav.IMAGE_DETAILS_SCREEN_ROUTE
import com.example.pixabayapp.presentation.view.screen.Nav.SEARCH_IMAGES_SCREEN_ROUTE
import com.example.pixabayapp.presentation.view.screen.Nav.SEARCH_TEXT_PARAM
import com.example.pixabayapp.presentation.view.screen.SearchImagesScreen
import com.example.pixabayapp.presentation.viewModel.ImageDetailsViewModel
import com.example.pixabayapp.presentation.viewModel.SearchImagesViewModel
import com.example.pixabayapp.presentation.view.theme.PixabayAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PixabayAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PixabayApp()
                }
            }
        }
    }
}

@Composable
private fun PixabayApp() {
    val navController = rememberNavController()
    val startSearchText = stringResource(R.string.start_search_text)
    NavHost(
        navController = navController,
        startDestination = "$SEARCH_IMAGES_SCREEN_ROUTE{$SEARCH_TEXT_PARAM}"
    ) {
        composable(
            "$SEARCH_IMAGES_SCREEN_ROUTE{$SEARCH_TEXT_PARAM}",
            arguments = listOf(navArgument(SEARCH_TEXT_PARAM) {
                type = NavType.StringType; defaultValue = startSearchText
            })
        ) {
            val viewModel = hiltViewModel<SearchImagesViewModel>()
            SearchImagesScreen(viewModel, navController)
        }

        composable(
            IMAGE_DETAILS_SCREEN_ROUTE + "{$IMAGE_DETAILS_ID_PARAM}",
            arguments = listOf(navArgument(IMAGE_DETAILS_ID_PARAM) { type = NavType.IntType })
        ) {
            val viewModel = hiltViewModel<ImageDetailsViewModel>()
            ImageDetailsScreen(viewModel)
        }
    }

}
