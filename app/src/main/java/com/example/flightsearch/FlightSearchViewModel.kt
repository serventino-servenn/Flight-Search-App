package com.example.flightsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.flightsearch.data.favorite.FavoriteItem
import com.example.flightsearch.data.favorite.FavoriteRepository
import com.example.flightsearch.data.flights.AirportItem
import com.example.flightsearch.data.flights.AirportRepository
import com.example.flightsearch.data.preferences.SearchPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FlightViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val airportRepository: AirportRepository,
    private val searchPreferencesRepository: SearchPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlightUiState())
    val uiState: StateFlow<FlightUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
        restoreSearchQuery()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.favorites.collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }
    }
    private fun restoreSearchQuery() {
        viewModelScope.launch {
            searchPreferencesRepository.searchQuery.collect { savedQuery ->
                _uiState.update { it.copy(searchQuery = savedQuery) }

                if (savedQuery.isBlank()) {
                    _uiState.update {
                        it.copy(
                            searchResult = emptyList(),
                            selectedAirport = null,
                            isSearching = false
                        )
                    }
                } else {
                    onSearchQuery(savedQuery)
                }
            }
        }
    }

    fun toggleFavorite(departure: AirportItem?, destination: AirportItem) {
        val depCode = departure?.iata_code ?: return
        val destCode = destination.iata_code

        viewModelScope.launch {
            if (_uiState.value.favorites.any { it.departure_code == depCode && it.destination_code == destCode }) {
                favoriteRepository.deleteFavoriteByCodes(depCode, destCode)
            } else {
                favoriteRepository.insertFavorite(FavoriteItem(departure_code = depCode, destination_code = destCode))
            }
        }
    }

    fun onSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearching = true) }

        viewModelScope.launch {
            try {
                if (query.isBlank()) {
                    searchPreferencesRepository.clearSearchQuery()
                    _uiState.update {
                        it.copy(
                            searchResult = emptyList(),
                            selectedAirport = null,
                            isSearching = false
                        )
                    }
                    return@launch
                }

                val airports = airportRepository.searchAirports("%$query%")
                _uiState.update {
                    it.copy(
                        searchResult = airports,
                        isSearching = false,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSearching = false, errorMessage = e.message)
                }
            }
        }
    }

    /** User selects an airport suggestion */
    fun onSuggestionSelected(airport: AirportItem) {
        viewModelScope.launch {
            try {
                // Persist FINAL search value
                searchPreferencesRepository.saveSearchQuery(airport.name)

                val destinations = airportRepository
                    .getAllAirports()
                    .filter { it.id != airport.id }

                _uiState.update {
                    it.copy(
                        selectedAirport = airport,
                        searchQuery = airport.name,
                        searchResult = destinations,
                        isSearching = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun addFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            favoriteRepository.insertFavorite(item)
        }
    }

    fun removeFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            favoriteRepository.deleteFavoriteByCodes(
                depCode = item.departure_code,
                destCode = item.destination_code
            )
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[APPLICATION_KEY] as FlightSearchApplication
                val container = application.container

                FlightViewModel(
                    airportRepository = container.airportRepository,
                    favoriteRepository = container.favoriteRepository,
                    searchPreferencesRepository = container.userPreferencesRepository
                )
            }
        }
    }
}

data class FlightUiState(
    val searchQuery: String = "",
    val searchResult:List<AirportItem>  = emptyList(),
    val favorites:List<FavoriteItem> = emptyList(),
    val selectedAirport: AirportItem? = null,
    val isSearching:Boolean = false,
    val errorMessage:String? = null
)

