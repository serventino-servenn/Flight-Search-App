package com.example.flightsearch.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.appbar)) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more_options)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        SearchFlightScreen(
            viewModel = viewModel,
            uiState = uiState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}