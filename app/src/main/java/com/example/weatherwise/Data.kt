package com.example.weatherwise

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.time.ZoneId

@Entity data class Place(@PrimaryKey val id: Long, val name: String, val region: String, val country: String, val latitude: Double, val longitude: Double, val zone: String)
@Entity(foreignKeys = [ForeignKey(entity = Place::class, parentColumns = ["id"], childColumns = ["placeId"], onDelete = ForeignKey.CASCADE)])
data class CachedForecast(@PrimaryKey val placeId: Long, val json: String, val updated: Long)
@Dao interface WeatherDao {
 @Query("SELECT * FROM Place ORDER BY name") fun observePlaces(): Flow<List<Place>>
 @Query("SELECT * FROM Place ORDER BY name") suspend fun places(): List<Place>
 @Query("SELECT * FROM Place WHERE id = :id") suspend fun place(id: Long): Place?
 @Upsert suspend fun save(place: Place)
 @Query("DELETE FROM Place WHERE id = :id") suspend fun remove(id: Long)
 @Query("SELECT * FROM CachedForecast WHERE placeId = :id") fun observeForecast(id: Long): Flow<CachedForecast?>
 @Upsert suspend fun cache(forecast: CachedForecast)
}
@Database(entities = [Place::class, CachedForecast::class], version = 1, exportSchema = true)
abstract class WeatherDatabase : RoomDatabase() { abstract fun dao(): WeatherDao }

// Open-Meteo (2026). Forecast API and Geocoding API. Accessed 3 November 2026.
// https://open-meteo.com/en/docs and https://open-meteo.com/en/docs/geocoding-api
// Original implementation; data attribution is also displayed in the UI.
class WeatherApi {
 private suspend fun get(address: String): JSONObject = withContext(Dispatchers.IO) {
  val connection = URL(address).openConnection() as HttpURLConnection
  try {
   connection.connectTimeout = 12000; connection.readTimeout = 12000
   connection.instanceFollowRedirects = false
   check(connection.responseCode == 200) { "Forecast service unavailable" }
   val text = connection.inputStream.bufferedReader().use { it.readText() }
   JSONObject(text)
  } finally { connection.disconnect() }
 }
 suspend fun search(input: String): List<Place> = withContext(Dispatchers.IO) {
  val term = URLEncoder.encode(input.trim(), "UTF-8")
  val results = get("https://geocoding-api.open-meteo.com/v1/search?name=$term&count=8&language=en&format=json").optJSONArray("results") ?: return@withContext emptyList()
  (0 until results.length()).map { i -> results.getJSONObject(i).let {
   Place(it.getLong("id"), it.getString("name"), it.optString("admin1", ""), it.optString("country", ""), it.getDouble("latitude"), it.getDouble("longitude"), it.optString("timezone", "UTC"))
  } }
 }
 suspend fun forecast(place: Place): String = get(
  "https://api.open-meteo.com/v1/forecast?latitude=${place.latitude}&longitude=${place.longitude}" +
  "&hourly=temperature_2m,precipitation_probability,wind_speed_10m,weather_code,is_day" +
  "&temperature_unit=celsius&wind_speed_unit=kmh&forecast_days=3&timezone=auto&timeformat=unixtime",
 ).toString()
}
// Epoch times avoid ambiguous local timestamps around daylight-saving transitions.
fun parseForecast(payload: String): Forecast {
 val json = JSONObject(payload)
 val zone = json.getString("timezone"); ZoneId.of(zone)
 val hourly = json.getJSONObject("hourly")
 val time = hourly.getJSONArray("time")
 require(time.length() > 0) { "Empty forecast" }
 fun number(field: String, index: Int): Double? {
  val values = hourly.optJSONArray(field) ?: return null
  if (values.isNull(index)) return null
  return values.optDouble(index).takeIf { it.isFinite() }
 }
 return Forecast((0 until time.length()).map { i ->
  Hour(
   time.getLong(i),
   number("temperature_2m", i),
   number("precipitation_probability", i)?.toInt(),
   number("wind_speed_10m", i),
   number("weather_code", i)?.toInt(),
   number("is_day", i)?.let { it == 1.0 },
  )
 }, zone)
}
class WeatherRepository(context: Context) {
 val dao = Room.databaseBuilder(context, WeatherDatabase::class.java, "weatherwise.db").build().dao()
 val api = WeatherApi()
 suspend fun refresh(place: Place) {
  val json = api.forecast(place)
  parseForecast(json) // Reject malformed responses before replacing a usable offline cache.
  dao.cache(CachedForecast(place.id, json, System.currentTimeMillis()))
 }
}
