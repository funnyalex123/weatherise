package com.example.weatherwise

import android.app.Application
import android.content.Context
import androidx.work.*
import kotlinx.coroutines.CancellationException
import java.util.concurrent.TimeUnit

class WeatherApplication : Application() {
 val repository by lazy { WeatherRepository(this) }
 override fun onCreate() {
  super.onCreate()
  // Android Developers (2026), persistent work: https://developer.android.com/develop/background-work/background-tasks/persistent-work
  val request = PeriodicWorkRequestBuilder<ForecastWorker>(6, TimeUnit.HOURS)
   .setConstraints(Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()).build()
  WorkManager.getInstance(this).enqueueUniquePeriodicWork("forecast-sync", ExistingPeriodicWorkPolicy.KEEP, request)
 }
}
class ForecastWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
 override suspend fun doWork(): Result {
  val repo = (applicationContext as WeatherApplication).repository
  return try {
   var failed = false
   repo.dao.places().forEach { place ->
    try { repo.refresh(place) }
    catch (e: CancellationException) { throw e }
    catch (_: Exception) { failed = true }
   }
   if ((failed && runAttemptCount < 3)) Result.retry() else if (failed) Result.failure() else Result.success()
  } catch (e: CancellationException) { throw e }
    catch (_: Exception) { if (runAttemptCount < 3) Result.retry() else Result.failure() }
 }
}
