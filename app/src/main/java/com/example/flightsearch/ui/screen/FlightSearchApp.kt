package com.example.flightsearch.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.FlightViewModel
import com.example.flightsearch.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFlightApp() {
    val viewModel: FlightViewModel = viewModel(factory = FlightViewModel.Factory)
    val uiState by viewModel.uiState.collectAsState()
    val hasNoResults =
        uiState.searchQuery.isNotBlank() &&
                uiState.searchResult.isEmpty() &&
                uiState.selectedAirport == null &&
                !uiState.isSearching



    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.appbar)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =  MaterialTheme.colorScheme.surfaceVariant
                )

            )
        }
    ) { innerPadding ->
        SearchFlightScreen(
            viewModel = viewModel,
            hasNoResults = hasNoResults,
            onSuggestionSelected = {viewModel.onSuggestionSelected(it)},
            onQueryChange = {viewModel.onSearchQueryChanged(it)},
            onToggleFavorite = viewModel::toggleFavorite,
            uiState = uiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}