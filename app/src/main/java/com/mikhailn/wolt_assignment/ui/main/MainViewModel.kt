package com.mikhailn.wolt_assignment.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.mikhailn.wolt_assignment.LoopedPlaces
import com.mikhailn.wolt_assignment.data.domain.Place
import com.mikhailn.wolt_assignment.data.domain.Restaurant
import com.mikhailn.wolt_assignment.data.domain.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.wait
import java.util.function.Consumer
import javax.inject.Inject
enum class UiState{
    LOADING,
    LOADED,
    ERROR
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RestaurantRepository,
    private val places: LoopedPlaces
) : ViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            places.forEach {
                Log.i("PLACE_EMITTER", "Posting place: $it")
                _currentPlace.postValue(it)
                delay(10000)
            }
        }

    }

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState

    private val _currentPlace = MutableLiveData<Place>()
    val currentPlace: LiveData<Place> = _currentPlace

    private val _restaurants = MutableLiveData<List<Restaurant>>()
    val restaurants: LiveData<List<Restaurant>> = _restaurants

    suspend fun updateRestaurants(place: Place) {
        _uiState.value = UiState.LOADING
        withContext(Dispatchers.IO) {
            repository.getRestaurants(place)
                .onSuccess {
                    _restaurants.postValue(it)
                    _uiState.postValue(UiState.LOADED)
                }
                .onFailure {
                    _uiState.postValue(UiState.ERROR)
                }

        }
    }

    fun removeObservables(owner: LifecycleOwner) {
        restaurants.removeObservers(owner)
        currentPlace.removeObservers(owner)
        uiState.removeObservers(owner)
    }

    fun addToFavorites(restaurant: Restaurant) {
        repository.addToFavorites(restaurant)
    }

    fun removeFromFavorites(restaurant: Restaurant) {
        repository.removeFromFavorites(restaurant)
    }
}
