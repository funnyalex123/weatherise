package com.example.weatherwise

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class Hour(val epoch: Long, val temperature: Double?, val rain: Int?, val wind: Double?, val code: Int?, val daylight: Boolean?)
data class Forecast(val hours: List<Hour>, val zone: String)
enum class Activity(val label: String, val minTemp: Double, val maxTemp: Double, val maxRain: Int, val maxWind: Double) {
 WALK("Walk", 5.0, 28.0, 30, 25.0), CYCLE("Cycle", 8.0, 27.0, 20, 20.0), PICNIC("Picnic", 15.0, 28.0, 15, 15.0);
 val explanation get() = "${minTemp.toInt()}–${maxTemp.toInt()}°C · rain ≤$maxRain% · wind ≤${maxWind.toInt()} km/h · daylight · clear/cloudy"
}
// These are transparent convenience preferences, not weather warnings or safety guarantees.
fun suitable(hour: Hour, activity: Activity): Boolean {
 val temp = hour.temperature ?: return false
 val rain = hour.rain ?: return false
 val wind = hour.wind ?: return false
 val code = hour.code ?: return false
 return temp in activity.minTemp..activity.maxTemp && rain in 0..activity.maxRain &&
  wind in 0.0..activity.maxWind && code in 0..3 && hour.daylight == true
}
fun upcoming(hours: List<Hour>, now: Long) = hours.filter { it.epoch >= now }.take(24)
fun formatTime(epoch: Long, zone: String, pattern: String = "EEE HH:mm"): String =
 DateTimeFormatter.ofPattern(pattern, java.util.Locale.UK).withZone(ZoneId.of(zone)).format(Instant.ofEpochSecond(epoch))
fun condition(code: Int?): String = when (code) {
 0 -> "Clear sky"; 1, 2 -> "Partly cloudy"; 3 -> "Overcast"; 45, 48 -> "Fog"
 51, 53, 55, 56, 57 -> "Drizzle"; 61, 63, 65, 66, 67 -> "Rain"
 71, 73, 75, 77, 85, 86 -> "Snow"; 80, 81, 82 -> "Rain showers"
 95, 96, 99 -> "Thunderstorm"; else -> "Unavailable"
}
