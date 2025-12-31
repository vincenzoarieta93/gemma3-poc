package it.spindox.home.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import it.spindox.designsystem.utils.ThemePreviews
import it.spindox.result.Resource
import it.spindox.result.loading
import it.spindox.designsystem.theme.MainAppTheme

@Composable
fun MainPage(
    viewModel: MainViewModel = hiltViewModel(),
    onModelSelected: () -> Unit,
) {

    val state = viewModel.uiState.collectAsState().value
    val event = viewModel.event.copy (
        onModelSelected = { model ->
            viewModel.onLlmModelUiSelected(model)
            onModelSelected()
        }
    )

    MainPageUi(
        state = state,
        event = event,
    )
}

@Composable
fun MainPageUi(
    state: MainState,
    event: MainEvent,
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(state.modelsList) {
            is Resource.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(state.modelsList.data) { index, item ->
                        ItemCard(
                            item = item,
                            onFavoriteClick = {},
                            onClick = {
                                event.onModelSelected(item)
                            },
                        )
                    }
                }
            }
            is Resource.Error -> {
                Text(
                    text = "Error: ${state.modelsList.getErrorMessage()}"
                )
            }
            Resource.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ItemCard (
    item: LlmModelUi,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit,
) {
    Row (
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier
                .weight(1f),
            text = item.name.replaceFirstChar { it.uppercase() },
            color = MaterialTheme.colorScheme.primary
        )
        Icon(
            modifier = Modifier.clickable { onFavoriteClick() },
            imageVector = if (false) Icons.Filled.Favorite
                else Icons.Outlined.FavoriteBorder,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = "Add to favorite icon",
        )
    }
}


// -- Previews -- //

@ThemePreviews
@Composable
private fun MainPageUiResultsPreview() = MainAppTheme {
    MainPageUi(
        state = sampleMainState,
        event = emptyMainEvent,
    )
}

@ThemePreviews
@Composable
private fun MainPageUiLoadingPreview() = MainAppTheme {
    MainPageUi(
        state = sampleMainState.copy(
            modelsList = loading()
        ),
        event = emptyMainEvent,
    )
}