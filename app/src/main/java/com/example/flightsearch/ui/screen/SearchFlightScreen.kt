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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.flightsearch.FlightUiState
import com.example.flightsearch.FlightViewModel
import com.example.flightsearch.R
import com.example.flightsearch.data.favorite.FavoriteItem
import com.example.flightsearch.data.flights.AirportItem
import com.example.flightsearch.ui.theme.FlightSearchTheme

@Composable
fun SearchFlightScreen(
    viewModel: FlightViewModel,
    onQueryChange: (String) -> Unit,
    onSuggestionSelected: (AirportItem) -> Unit,
    onToggleFavorite: (AirportItem?, AirportItem) -> Unit,
    hasNoResults:Boolean,
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
                onQueryChange(it)

                showSuggestions = true
            }
        )

        Spacer(Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

        SearchContent(
//            viewModel = viewModel,
            uiState = uiState,
            hasNoResults = hasNoResults,
            showSuggestions = showSuggestions,
            onSuggestionSelected = {
                showSuggestions = false
                onSuggestionSelected(it)
            },
            onToggleFavorite = onToggleFavorite
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
        placeholder = {
            Text(stringResource(R.string.search_airport_hint))
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(R.string.clear_button),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        },
        singleLine = true,


        shape = MaterialTheme.shapes.large,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )

}

@Composable
fun SearchContent(
//    viewModel: FlightViewModel,
    uiState: FlightUiState,
    hasNoResults: Boolean,
    showSuggestions: Boolean,
    onSuggestionSelected: (AirportItem) -> Unit,
    onToggleFavorite: (AirportItem?, AirportItem) -> Unit
) {
    when {
        uiState.isSearching -> LoadingState()
        uiState.errorMessage != null -> ErrorState(uiState.errorMessage)
        hasNoResults -> NoAirportsFound(uiState = uiState)

        uiState.searchQuery.isBlank() ->
            FavoritesSection(
                favorites = uiState.favorites,
                airports = uiState.allAirports,
                onToggleFavorite = onToggleFavorite
            )

        showSuggestions -> SuggestionsList(
            airports = uiState.searchResult,
            onSuggestionSelected = onSuggestionSelected,
        )
        else -> DestinationResults(
            departure = uiState.selectedAirport,
            destinations = uiState.searchResult,
            favorites = uiState.favorites,
            onToggleFavorite = onToggleFavorite
        )
    }
}


@Composable
fun NoAirportsFound(uiState: FlightUiState){
    Text(
        text = stringResource(R.string.no_airports_found_for, uiState.searchQuery),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.error,
        modifier = Modifier.padding(16.dp)
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
    airports: List<AirportItem>,
    onToggleFavorite: (AirportItem?, AirportItem) -> Unit
) {
    if (favorites.isEmpty()) {
        Text(stringResource(id = R.string.empty_favorites))
        return
    }

    LazyColumn {
        items(favorites) { favorite ->

            val departure = airports.firstOrNull {
                it.iata_code == favorite.departure_code
            }

            val destination = airports.firstOrNull {
                it.iata_code == favorite.destination_code
            }

            if (departure != null && destination != null) {
                DestinationCard(
                    departure = departure,
                    destination = destination,
                    isFavorite = true,
                    onToggleFavorite = onToggleFavorite,
                    modifier = Modifier.padding(
                        vertical = dimensionResource(id = R.dimen.spacing_small),
                        horizontal = dimensionResource(id = R.dimen.spacing_medium)
                    )
                )
            }
        }
    }
}

@Composable
fun SuggestionsList(
    airports: List<AirportItem>,
    onSuggestionSelected: (AirportItem) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(airports) { index, airport ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSuggestionSelected(airport) }
                    .padding(
                        horizontal = dimensionResource(id = R.dimen.spacing_large),
                        vertical = dimensionResource(id = R.dimen.spacing_medium)
                    )
            ) {
                Text(
                    text = airport.name,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = airport.iata_code,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (index < airports.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = dimensionResource(id = R.dimen.spacing_large)),
                    thickness = 0.5.dp
                )
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

            Box(modifier = Modifier.fillMaxWidth()) {
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = dimensionResource(id = R.dimen.airplane_offset)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = departure?.iata_code ?: stringResource(id = R.string.unknown_code),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

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



//preview section

@Preview(showBackground = true)
@Composable
fun SearchFieldPreview_Empty() {
    FlightSearchTheme {
        SearchField(
            query = "",
            onQueryChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteCardPreview() {
    FlightSearchTheme {
        FavoritesSection(
            favorites = listOf(FavoriteItem(0,"JFK", "LAX")),
            airports = listOf(
                AirportItem(1, "John F. Kennedy Intl", "JFK", passengers = 5),
                AirportItem(2, "Los Angeles Intl", "LAX", passengers = 6)
            ),
            onToggleFavorite = { _, _ -> }
        )
    }
}
@Preview(showBackground = true)
@Composable
fun SuggestionsListPreview() {
    FlightSearchTheme {
        SuggestionsList(
            airports = listOf(
                AirportItem(1, "John F. Kennedy Intl", "JFK", passengers = 5),
                AirportItem(2, "Los Angeles Intl", "LAX", passengers = 6)
            ),
            onSuggestionSelected = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DestinationCardPreview() {
    FlightSearchTheme {
        DestinationCard(
            departure = AirportItem(1, "John F. Kennedy Intl", "JFK", passengers = 6),
            destination = AirportItem(2, "Los Angeles Intl", "LAX", passengers = 10),
            isFavorite = true,
            onToggleFavorite = { _, _ -> }
        )
    }
}



