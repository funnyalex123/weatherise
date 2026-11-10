package com.example.weatherwise

import org.junit.Assert.*
import org.junit.Test

class DomainTest {
 private val good = Hour(1000, 18.0, 10, 10.0, 1, daylight = true)

 @Test fun walkAcceptsExactBoundaries() {
  assertTrue(suitable(good.copy(temperature = 5.0, rain = 30, wind = 25.0), Activity.WALK))
  assertTrue(suitable(good.copy(temperature = 28.0), Activity.WALK))
  assertFalse(suitable(good.copy(temperature = 28.1), Activity.WALK))
 }

 @Test fun rejectsMissingValues() {
  assertFalse(suitable(good.copy(temperature = null), Activity.WALK))
  assertFalse(suitable(good.copy(rain = null), Activity.WALK))
  assertFalse(suitable(good.copy(wind = null), Activity.WALK))
  assertFalse(suitable(good.copy(code = null), Activity.WALK))
  assertFalse(suitable(good.copy(daylight = null), Activity.WALK))
 }

 @Test fun excludesNightAndBadWeather() {
  assertFalse(suitable(good.copy(daylight = false), Activity.WALK))
  listOf(45, 61, 71, 95).forEach { assertFalse(suitable(good.copy(code = it), Activity.WALK)) }
 }

 @Test fun activitiesHaveDifferentThresholds() {
  val windy = good.copy(wind = 22.0)
  assertTrue(suitable(windy, Activity.WALK))
  assertFalse(suitable(windy, Activity.CYCLE))
  assertFalse(suitable(windy, Activity.PICNIC))
 }

 @Test fun pastHoursAreExcludedAndListIsBounded() {
  val hours = (0..30).map { good.copy(epoch = it * 3600L) }
  assertEquals(24, upcoming(hours, 3600).size)
  assertEquals(3600L, upcoming(hours, 3600).first().epoch)
  assertTrue(upcoming(hours, 200000).isEmpty())
 }

 @Test fun destinationZoneControlsDisplayAcrossDst() {
  val before = java.time.Instant.parse("2026-10-25T00:30:00Z").epochSecond
  val after = java.time.Instant.parse("2026-10-25T01:30:00Z").epochSecond
  assertEquals("01:30 +01:00", formatTime(before, "Europe/London", "HH:mm XXX"))
  assertEquals("01:30 Z", formatTime(after, "Europe/London", "HH:mm XXX"))
 }

 @Test fun conditionMappingCoversAllWeatherCodes() {
  assertEquals("Clear sky", condition(0))
  assertEquals("Partly cloudy", condition(1))
  assertEquals("Partly cloudy", condition(2))
  assertEquals("Overcast", condition(3))
  assertEquals("Fog", condition(45))
  assertEquals("Drizzle", condition(51))
  assertEquals("Rain", condition(61))
  assertEquals("Snow", condition(71))
  assertEquals("Rain showers", condition(80))
  assertEquals("Thunderstorm", condition(95))
  assertEquals("Unavailable", condition(null))
  assertEquals("Unavailable", condition(-1))
 }

 @Test fun parseForecastSuccessfullyParsesValidJsonPayload() {
  val sampleJson = """
   {
    "timezone": "Europe/London",
    "hourly": {
     "time": [1700000000, 1700003600],
     "temperature_2m": [15.5, 16.0],
     "precipitation_probability": [10, 20],
     "wind_speed_10m": [12.0, 14.5],
     "weather_code": [1, 2],
     "is_day": [1.0, 1.0]
    }
   }
  """.trimIndent()
  val forecast = parseForecast(sampleJson)
  assertEquals("Europe/London", forecast.zone)
  assertEquals(2, forecast.hours.size)
  assertEquals(15.5, forecast.hours[0].temperature!!, 0.01)
  assertEquals(10, forecast.hours[0].rain!!)
  assertEquals(12.0, forecast.hours[0].wind!!, 0.01)
  assertEquals(1, forecast.hours[0].code!!)
  assertEquals(true, forecast.hours[0].daylight)
 }

 @Test(expected = IllegalArgumentException::class)
 fun parseForecastThrowsExceptionForEmptyHourlyData() {
  val emptyJson = """
   {
    "timezone": "UTC",
    "hourly": {
     "time": []
    }
   }
  """.trimIndent()
  parseForecast(emptyJson)
 }
}
