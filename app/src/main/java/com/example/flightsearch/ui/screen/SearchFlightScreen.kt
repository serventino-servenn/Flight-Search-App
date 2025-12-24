package com.example.flightsearch.ui.screen


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.flightsearch.FlightUiState
import com.example.flightsearch.FlightViewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.favorite.FavoriteItem
import com.example.flightsearch.data.flights.AirportItem
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun SearchFlightScreen(
    viewModel: FlightViewModel,
    uiState: FlightUiState,
    modifier: Modifier = Modifier
) {
    var showSuggestions by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(id = R.dimen.padding_screen))
    ) {
        SearchField(
            query = uiState.searchQuery,
            onQueryChange = {
                viewModel.onSearchQuery(it)
                showSuggestions = true
            }
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

        SearchContent(
            viewModel = viewModel,
            uiState = uiState,
            showSuggestions = showSuggestions,
            onSuggestionSelected = {
                showSuggestions = false
                viewModel.onSuggestionSelected(it)
            }
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
        placeholder = { Text(stringResource(id = R.string.search_airport_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            Icon(
                Icons.Default.Mic,
                contentDescription = stringResource(id = R.string.voice_input_desc)
            )
        },
        singleLine = true
    )
}

@Composable
fun LoadingState() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorState(message: String) {
    Text(
        text = stringResource(R.string.error_label, message),
        color = Color.Red
    )
}

@Composable
fun FavoritesSection(
    favorites: List<FavoriteItem>,
    onRemoveFavorite: (FavoriteItem) -> Unit
) {
    if (favorites.isEmpty()) {
        Text(stringResource(id = R.string.empty_favorites))
    } else {
        LazyColumn {
            items(favorites) { favorite ->
                FavoriteCard(
                    favorite = favorite,
                    onRemoveFavorite = onRemoveFavorite
                )
            }
        }
    }
}

@Composable
fun FavoriteCard(
    favorite: FavoriteItem,
    onRemoveFavorite: (FavoriteItem) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.card_padding_vertical))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.card_padding_horizontal),
                    vertical = dimensionResource(id = R.dimen.spacing_large)
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(stringResource(R.string.departure_label, favorite.departure_code))
                Text(stringResource(R.string.destination_label, favorite.destination_code))
            }

            IconButton(onClick = { onRemoveFavorite(favorite) }) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(id = R.string.remove_favorite_desc),
                    tint = Color.Red
                )
            }
        }
    }
}

@Composable
fun SuggestionsList(
    airports: List<AirportItem>,
    favorites: List<FavoriteItem>,
    onSuggestionSelected: (AirportItem) -> Unit,
    onToggleFavorite: (AirportItem?, AirportItem) -> Unit
) {
    LazyColumn {
        items(airports) { airport ->
            val isFavorite = favorites.any { it.departure_code == airport.iata_code }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionSelected(airport) }
                    .padding(dimensionResource(id = R.dimen.spacing_large)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${airport.name} (${airport.iata_code})")
                IconButton(
                    onClick = { onToggleFavorite(null, airport) }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(id = R.string.favorite_desc)
                    )
                }
            }
        }
    }
}

@Composable
fun DestinationResults(
    departure: AirportItem?,
    destinations: List<AirportItem>,
    favorites: List<FavoriteItem>,
    onToggleFavorite: (AirportItem?, AirportItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(destinations) { destination ->
            val isFavorite = departure?.let { dep ->
                favorites.any {
                    it.departure_code == dep.iata_code &&
                            it.destination_code == destination.iata_code
                }
            } ?: false

            DestinationCard(
                departure = departure,
                destination = destination,
                isFavorite = isFavorite,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.padding(
                    vertical = dimensionResource(id = R.dimen.spacing_small),
                    horizontal = dimensionResource(id = R.dimen.spacing_medium)
                )
            )
        }
    }
}

@Composable
fun DestinationCard(
    departure: AirportItem?,
    destination: AirportItem,
    isFavorite: Boolean,
    onToggleFavorite: (AirportItem?, AirportItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = R.dimen.card_elevation)
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        val lineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        val strokeWidth = dimensionResource(id = R.dimen.line_stroke_width)
        val dotSpacing = dimensionResource(id = R.dimen.dot_spacing)
        Column(modifier = Modifier.padding(dimensionResource(id = R.dimen.destination_card_padding))) {
            // Small heart icon
            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.heart_icon_box_size))
                    .clip(CircleShape)
                    .background(
                        if (isFavorite)
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                        else
                            MaterialTheme.colorScheme.surface
                    )
                    .clickable { onToggleFavorite(departure, destination) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = stringResource(id = R.string.favorite_desc),
                    modifier = Modifier.size(dimensionResource(id = R.dimen.heart_icon_size)),
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline
                )
            }

            Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_extra_large)))

            // Route with airplane and heart combined
            Box(modifier = Modifier.fillMaxWidth()) {
                // Dotted line
                Canvas(modifier = Modifier.fillMaxWidth()) {
                    val strokeWidth = strokeWidth.toPx()
                    val dotSpacing = dotSpacing.toPx()
                    val startX = 0f
                    val endX = size.width
                    val centerY = size.height / 2

                    drawLine(
                        color = lineColor,
                        start = Offset(startX, centerY),
                        end = Offset(endX, centerY),
                        strokeWidth = strokeWidth,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(dotSpacing, dotSpacing), 0f)
                    )
                }

                // Airplane with heart trail
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = dimensionResource(id = R.dimen.airplane_offset)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Departure
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = departure?.iata_code ?: stringResource(id = R.string.unknown_code),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Airplane icon
                    Box(
                        modifier = Modifier.offset(y = dimensionResource(id = R.dimen.spacing_small)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AirplanemodeActive,
                            contentDescription = stringResource(id = R.string.flight_desc),
                            modifier = Modifier.size(dimensionResource(id = R.dimen.airplane_icon_size)),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Destination
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = destination.iata_code,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }

            Spacer(Modifier.height(dimensionResource(id = R.dimen.destination_card_spacing)))

            // Airport names
            Text(
                text = "${departure?.name ?: stringResource(id = R.string.select_departure_placeholder)} → ${destination.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}


@Composable
fun SearchContent(
    viewModel: FlightViewModel,
    uiState: FlightUiState,
    showSuggestions: Boolean,
    onSuggestionSelected: (AirportItem) -> Unit
) {
    when {
        uiState.isSearching -> LoadingState()
        uiState.errorMessage != null -> ErrorState(uiState.errorMessage)
        uiState.searchQuery.isBlank() ->
            FavoritesSection(
                favorites = uiState.favorites,
                onRemoveFavorite = viewModel::removeFavorite
            )
        showSuggestions -> SuggestionsList(
            airports = uiState.searchResult,
            favorites = uiState.favorites,
            onSuggestionSelected = onSuggestionSelected,
            onToggleFavorite = viewModel::toggleFavorite
        )
        else -> DestinationResults(
            departure = uiState.selectedAirport,
            destinations = uiState.searchResult,
            favorites = uiState.favorites,
            onToggleFavorite = viewModel::toggleFavorite
        )
    }
}



@Preview
@Composable
fun FlightSearchScreenPreview(){
    FlightSearchTheme {

    }
}
