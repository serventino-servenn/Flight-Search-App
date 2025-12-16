package com.example.flightsearch.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.FlightUiState
import com.example.flightsearch.FlightViewModel
import com.example.flightsearch.data.favorite.FavoriteItem
import com.example.flightsearch.data.flights.AirportItem
import com.example.flightsearch.ui.theme.FlightSearchTheme



@Composable
fun SearchFlightScreen(
    viewModel: FlightViewModel,
    uiState: FlightUiState,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSuggestions by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchField(
            query = uiState.searchQuery,
            onQueryChange = {
                onQueryChange(it)
                showSuggestions = true
            }
        )

        Spacer(Modifier.height(16.dp))

        SearchContent(
            uiState = uiState,
            showSuggestions = showSuggestions,
            onSuggestionSelected = {
                showSuggestions = false
                viewModel.onSuggestionSelected(it)
            },
            onAddFavorite = viewModel::addFavorite,
            onRemoveFavorite = viewModel::removeFavorite
        )
    }
}


@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search airport...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = { Icon(Icons.Default.Mic, contentDescription = "Voice input") },
        singleLine = true
    )
}


@Composable
fun SearchContent(
    uiState: FlightUiState,
    showSuggestions: Boolean,
    onSuggestionSelected: (AirportItem) -> Unit,
    onAddFavorite: (FavoriteItem) -> Unit,
    onRemoveFavorite: (FavoriteItem) -> Unit
) {
    when {
        uiState.isSearching -> LoadingState()
        uiState.errorMessage != null -> ErrorState(uiState.errorMessage)
        uiState.searchQuery.isBlank() -> FavoritesSection(uiState.favorites)
        showSuggestions -> SuggestionsList(
            airports = uiState.searchResult,
            onSuggestionSelected = onSuggestionSelected
        )
        else -> DestinationResults(
            departure = uiState.selectedAirport,
            destinations = uiState.searchResult,
            onAddFavorite = onAddFavorite,
            onRemoveFavorite = onRemoveFavorite
        )
    }
}

@Composable
fun LoadingState() {
    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(message: String) {
    Text(
        text = "Error: $message",
        color = Color.Red
    )
}

@Composable
fun FavoritesSection(favorites: List<FavoriteItem>) {
    if (favorites.isEmpty()) {
        Text("Your favorite routes will appear here.")
    } else {
        LazyColumn {
            items(favorites) { favorite ->
                FavoriteCard(favorite)
            }
        }
    }
}

@Composable
fun FavoriteCard(favorite: FavoriteItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Departure: ${favorite.departure_code}")
            Text("Destination: ${favorite.destination_code}")
        }
    }
}

@Composable
fun SuggestionsList(
    airports: List<AirportItem>,
    onSuggestionSelected: (AirportItem) -> Unit
) {
    LazyColumn {
        items(airports) { airport ->
            Text(
                text = "${airport.name} (${airport.iata_code})",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionSelected(airport) }
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun DestinationResults(
    departure: AirportItem?,
    destinations: List<AirportItem>,
    onAddFavorite: (FavoriteItem) -> Unit,
    onRemoveFavorite: (FavoriteItem) -> Unit
) {
    LazyColumn {
        items(destinations) { destination ->
            DestinationCard(
                departure = departure,
                destination = destination,
                onAddFavorite = onAddFavorite,
                onRemoveFavorite = onRemoveFavorite
            )
        }
    }
}

@Composable
fun DestinationCard(
    departure: AirportItem?,
    destination: AirportItem,
    onAddFavorite: (FavoriteItem) -> Unit,
    onRemoveFavorite: (FavoriteItem) -> Unit
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    "Departure: ${departure?.name} (${departure?.iata_code})",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Destination: ${destination.name} (${destination.iata_code})",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(
                onClick = {
                    isFavorite = !isFavorite
                    departure?.let {
                        val item = FavoriteItem(
                            departure_code = it.iata_code,
                            destination_code = destination.iata_code
                        )
                        if (isFavorite) onAddFavorite(item) else onRemoveFavorite(item)
                    }
                }
            ) {
                Icon(
                    imageVector = if (isFavorite)
                        Icons.Default.Favorite
                    else
                        Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite"
                )
            }
        }
    }
}



@Preview
@Composable
fun FlightSearchScreenPreview(){
    FlightSearchTheme {

    }
}
