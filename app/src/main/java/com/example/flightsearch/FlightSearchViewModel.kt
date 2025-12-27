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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class FlightViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val airportRepository: AirportRepository,
    private val searchPreferencesRepository: SearchPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FlightUiState())
    val uiState: StateFlow<FlightUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")


    init {
        observeFavorites()
        restoreSearchQuery()
        loadAllAirports()
        observeSearchQuery()
    }


    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteRepository.favorites.collect { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryFlow.value = query
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
                    performSearch(savedQuery)
                }
            }
        }
    }

    private fun loadAllAirports() {
        viewModelScope.launch {
            try {
                val airports = airportRepository.getAllAirports()
                _uiState.update {
                    it.copy(allAirports = airports)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
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

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            viewModelScope.launch {
                searchPreferencesRepository.clearSearchQuery()
            }

            _uiState.update {
                it.copy(
                    searchResult = emptyList(),
                    selectedAirport = null,
                    isSearching = false
                )
            }
            return
        }

        _uiState.update { it.copy(isSearching = true) }

        val results = _uiState.value.allAirports.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.iata_code.contains(query, ignoreCase = true)
        }

        _uiState.update {
            it.copy(
                searchResult = results,
                isSearching = false,
                errorMessage = null
            )
        }
    }



    fun onSuggestionSelected(airport: AirportItem) {
        viewModelScope.launch {
            try {

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
    val searchResult: List<AirportItem> = emptyList(),
    val allAirports: List<AirportItem> = emptyList(),
    val favorites: List<FavoriteItem> = emptyList(),
    val selectedAirport: AirportItem? = null,
    val isSearching: Boolean = false,
    val errorMessage: String? = null
)


