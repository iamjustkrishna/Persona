package com.krishnajeena.persona.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.persona.data_layer.RadioLibraryResponse
import com.krishnajeena.persona.data_layer.RadioRepository
import com.krishnajeena.persona.data_layer.RadioStation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: RadioRepository
) : ViewModel() {

    private val _stations = MutableStateFlow<List<RadioStation>>(emptyList())
    val stations: StateFlow<List<RadioStation>> = _stations

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        // Fetch as soon as the app starts
        refreshRadioStations()
    }

    fun refreshRadioStations() {
        viewModelScope.launch(Dispatchers.IO) {
           _isLoading.value = true
            _stations.value = repository.fetchAndCacheStations()
            _isLoading.value = false
        }
    }
}