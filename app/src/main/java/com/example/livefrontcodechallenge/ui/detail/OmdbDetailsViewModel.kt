package com.example.livefrontcodechallenge.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.livefrontcodechallenge.models.LoadableData
import com.example.livefrontcodechallenge.models.OmdbEntryDetail
import com.example.livefrontcodechallenge.repository.OmdbRepository
import com.example.livefrontcodechallenge.ui.OmdbDetailsScreenRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class OmdbDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val omdbRepository: OmdbRepository
) : ViewModel() {

    private val omdbDetailsScreenRoute = savedStateHandle.toRoute<OmdbDetailsScreenRoute>()

    private val _state: MutableStateFlow<LoadableData<OmdbEntryDetail>> =
        MutableStateFlow(LoadableData.Loading)
    val state: StateFlow<LoadableData<OmdbEntryDetail>> = _state

    init {
        loadOmdbDetails()
    }

    private fun loadOmdbDetails() {
        viewModelScope.launch {
            omdbRepository
                .getOmdbEntryDetails(omdbDetailsScreenRoute.imdbId)
                .collect(_state)
        }
    }

    fun onErrorRetry() {
        loadOmdbDetails()
    }
}
