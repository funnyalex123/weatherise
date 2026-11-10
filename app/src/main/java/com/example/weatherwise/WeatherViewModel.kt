package com.example.weatherwise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class WeatherState(val place: Place? = null, val forecast: Forecast? = null, val updated: Long? = null, val loading: Boolean = false, val error: String? = null)
data class SearchState(val results: List<Place> = emptyList(), val loading: Boolean = false, val searched: Boolean = false, val error: String? = null)
class WeatherViewModel(app: Application, private val saved: SavedStateHandle) : AndroidViewModel(app) {
 private val repo = (app as WeatherApplication).repository
 private val _weather = MutableStateFlow(WeatherState())
 val weather = _weather.asStateFlow()
 private val _search = MutableStateFlow(SearchState())
 val search = _search.asStateFlow()
 private val _notice = MutableStateFlow<String?>(null)
 val notice = _notice.asStateFlow()
 val places = repo.dao.observePlaces().catch { _notice.value = "Saved places could not be read. Restart the app and retry." }
  .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
 val query = saved.getStateFlow("query", "")
 private var selectionJob: Job? = null
 private var refreshJob: Job? = null
 private var searchJob: Job? = null
 init {
  viewModelScope.launch {
   try { saved.get<Long>("placeId")?.let { repo.dao.place(it) }?.let { select(it) } }
   catch (e: CancellationException) { throw e }
   catch (_: Exception) { _notice.value = "Could not restore the selected place." }
  }
 }
 fun queryChanged(value: String) { saved["query"] = value.take(80) }
 fun clearNotice() { _notice.value = null }
 fun findPlaces() {
  val input = query.value.trim()
  searchJob?.cancel()
  if (input.length < 2) { _search.value = SearchState(error = "Enter at least two characters."); return }
  searchJob = viewModelScope.launch {
   _search.value = SearchState(loading = true)
   try { _search.value = SearchState(results = repo.api.search(input), searched = true) }
   catch (e: CancellationException) { throw e }
   catch (_: Exception) { _search.value = SearchState(error = "Search failed. Check your connection and try again.") }
  }
 }
 fun select(place: Place) {
  selectionJob?.cancel(); refreshJob?.cancel()
  _weather.value = WeatherState(place = place, loading = true)
  selectionJob = viewModelScope.launch {
   try {
    if ((repo.dao.place(place.id) == null) && (repo.dao.places().size >= 10)) {
     _weather.value = WeatherState(error = "You can save up to 10 places. Remove one first."); return@launch
    }
    repo.dao.save(place)
    saved["placeId"] = place.id
    refresh()
    repo.dao.observeForecast(place.id).collect { cache ->
     try {
      _weather.update { it.copy(forecast = cache?.let { (_, json) -> parseForecast(json) }, updated = cache?.updated) }
     } catch (e: CancellationException) { throw e }
       catch (_: Exception) { _weather.update { it.copy(error = "Saved forecast could not be read. Tap Refresh.") } }
    }
   } catch (e: CancellationException) { throw e }
   catch (_: Exception) { _weather.update { it.copy(loading = false, error = "Could not save this place. Please retry.") } }
  }
 }
 fun refresh() {
  val place = _weather.value.place ?: return
  refreshJob?.cancel()
  refreshJob = viewModelScope.launch {
   _weather.update { it.copy(loading = true, error = null) }
   try { repo.refresh(place); _weather.update { it.copy(loading = false) } }
   catch (e: CancellationException) { throw e }
   catch (_: Exception) { _weather.update { it.copy(loading = false, error = "Refresh failed. Any saved forecast is shown below.") } }
  }
 }
 fun remove(place: Place) { viewModelScope.launch {
  try {
   if (_weather.value.place?.id == place.id) {
    selectionJob?.cancel(); refreshJob?.cancel(); saved.remove<Long>("placeId"); _weather.value = WeatherState()
   }
   repo.dao.remove(place.id)
  } catch (e: CancellationException) { throw e }
    catch (_: Exception) { _notice.value = "Could not remove this place. Please retry." }
 } }
}
