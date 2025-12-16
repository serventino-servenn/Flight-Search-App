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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FlightViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val airportRepository: AirportRepository
): ViewModel() {
    private val _flights = MutableStateFlow(FlightUiState())
    val uiState: StateFlow<FlightUiState> = _flights.asStateFlow()



    init {
        onSearchQuery("")
    }
//
    fun onSearchQuery(query:String){
        _flights.update {
            it.copy(searchQuery = query, isSearching = true)
        }
        viewModelScope.launch {
            try {
                if(query.isBlank()){
                    val favorites = favoriteRepository.getAllFavorite()
                    _flights.update {
                        it.copy(
                            favorites = favorites,
                            searchResult = emptyList(),
                            isSearching = false,
                            errorMessage = null
                        )

                    }
                    return@launch
                }
               //otherwise search for airports
                val airports = airportRepository.searchAirports("%$query%")
                _flights.update {
                    it.copy(
                        searchResult = airports,
                        isSearching = false,
                        errorMessage = null
                    )
                }

            }catch (e: Exception){
               _flights.update { it.copy(
                   isSearching = false, errorMessage = e.message
               ) }
            }
        }
    }

    fun onSuggestionSelected(airport: AirportItem) {
        viewModelScope.launch {
            try {
                // Fetch all airports except the selected one
                val destinations = airportRepository.getAllAirports()
                    .filter { it.id != airport.id }

                _flights.update {
                    it.copy(
                        selectedAirport = airport,
                        searchResult = destinations, // destinations are the searchResult
                        searchQuery = airport.name,
                        isSearching = false
                    )
                }
            } catch (e: Exception) {
                _flights.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    //
//    private fun loadFavorites(){
//        viewModelScope.launch {
//            favoriteRepository.getAllFavorite()
//                .catch { e->_flights.update {it.copy(errorMessage = e.message)  } }
//                .collect { favorites->
//                    _flights.update { it.copy(favorites = favorites) }
//                }
//        }
//    }
    fun addFavorite(favoriteItem: FavoriteItem) {
        viewModelScope.launch {
            favoriteRepository.insertFavorite(favoriteItem)
        }
    }
    fun removeFavorite(favoriteItem: FavoriteItem) {
        viewModelScope.launch {
            favoriteRepository.deleteFavorite(favoriteItem)
        }
    }
//
//    private fun AirportItem

    private fun AirportItem.toUiModel() = FlightUiModel(
        code = iata_code,
        name = name
    )
    companion object{
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as FlightSearchApplication)
                val container = application.container
                FlightViewModel(
                    airportRepository = container.airportRepository,
                    favoriteRepository = container.favoriteRepository
                )
            }
        }
    }


}


data class FlightUiModel(
    val code: String,
    val name: String,
)

data class FlightUiState(
    val searchQuery: String = "",
    val searchResult:List<AirportItem>  = emptyList(),
    val favorites:List<FavoriteItem> = emptyList(),
    val selectedAirport: AirportItem? = null,
    val isSearching:Boolean = false,
    val errorMessage:String? = null
)
//data class FavoriteUiModel(
//    val departureCode:String,
//    val destinationCode:String
//)
//
//data class FlightUiState(
//    val searchQuery: String = "",
//    val airports: List<FlightUiState> = emptyList(),
//    val favorites: List<FavoriteItem> = emptyList(),
//    val isLoading: Boolean = false,
//    val errorMessage: String? = null
//)

