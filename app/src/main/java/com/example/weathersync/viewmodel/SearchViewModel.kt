package com.example.weathersync.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weathersync.data.model.Response
import com.example.weathersync.data.model.remote.PlaceData
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SearchViewModel(context: Context) : ViewModel() {

    private val placesClient: PlacesClient = Places.createClient(context)
    private val sessionToken = AutocompleteSessionToken.newInstance()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _places = MutableSharedFlow<Response<List<PlaceData>>>(replay = 1)
    val places: SharedFlow<Response<List<PlaceData>>> = _places.asSharedFlow()

    private val _message = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val message: SharedFlow<String> = _message.asSharedFlow()

    init {
        observeSearchQuery()
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        _searchQuery
            .debounce(500)
            .filter { it.isNotEmpty() }
            .onEach { fetchPlacePredictions(it) }
            .launchIn(viewModelScope)
    }

    private fun fetchPlacePredictions(query: String) {
        viewModelScope.launch { _places.emit(Response.Loading) }

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(sessionToken)
            .setCountries("EG")
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions.map { prediction ->
                    PlaceData(
                        placeId = prediction.placeId,
                        displayName = prediction.getPrimaryText(null).toString(),
                        address = prediction.getFullText(null).toString()
                    )
                }
                viewModelScope.launch { _places.emit(Response.Success(predictions)) }
            }
            .addOnFailureListener { exception ->
                handleFailure("Error fetching predictions", exception)
            }
    }

    suspend fun fetchPlaceDetails(placeId: String): Pair<Double, Double>? {
        return try {
            val request = FetchPlaceRequest.builder(
                placeId,
                listOf(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
            ).build()

            val response = placesClient.fetchPlace(request).await()

            val place = response.place
            val placeData = PlaceData(
                placeId = placeId,
                displayName = place.name ?: "Unknown",
                latLng = place.latLng,
                address = place.address
            )

            place.latLng?.let { Pair(it.latitude, it.longitude) }
        } catch (exception: Exception) {
            handleFailure("Error fetching place details", exception)
            null
        }
    }


    private fun handleFailure(message: String, exception: Exception) {
        Log.e("SearchViewModel", "$message: ${exception.localizedMessage}")
        viewModelScope.launch {
            _places.emit(Response.Failure(exception))
            _message.emit("$message: ${exception.localizedMessage ?: "Unknown error"}")
        }
    }

}
