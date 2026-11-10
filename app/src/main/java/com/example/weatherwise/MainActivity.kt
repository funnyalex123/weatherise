package com.example.weatherwise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import java.time.Instant
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds

private val Ink = Color(0xFF182B49)
private val Blue = Color(0xFF2855B8)
private val Cream = Color(0xFFF5F7FB)
private val Amber = Color(0xFFFFB800)

class MainActivity : ComponentActivity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  enableEdgeToEdge()
  setContent {
   val darkTheme = isSystemInDarkTheme()
   val colors = if (darkTheme) {
    darkColorScheme(
     primary = Color(0xFF8CAEFF),
     secondary = Color(0xFF79B093),
     background = Color(0xFF101928),
     surface = Color(0xFF1B2638),
     onSurface = Color(0xFFE2E8F4),
     onBackground = Color(0xFFE2E8F4),
    )
   } else {
    lightColorScheme(
     primary = Blue,
     secondary = Color(0xFF386A50),
     background = Cream,
     surface = Color.White,
     onSurface = Ink,
     onBackground = Ink,
    )
   }
   MaterialTheme(colorScheme = colors) {
    WeatherApp()
   }
  }
 }
}

private data class NavItem(val route: String, val label: String, val iconSelected: ImageVector, val iconUnselected: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp(vm: WeatherViewModel = viewModel()) {
 val weather by vm.weather.collectAsStateWithLifecycle()
 val search by vm.search.collectAsStateWithLifecycle()
 val places by vm.places.collectAsStateWithLifecycle()
 val query by vm.query.collectAsStateWithLifecycle()
 val notice by vm.notice.collectAsStateWithLifecycle()
 val nav = rememberNavController()
 val entry by nav.currentBackStackEntryAsState()
 val current = entry?.destination?.route ?: "today"
 val snackbar = remember { SnackbarHostState() }
 var placeMenuOpen by remember { mutableStateOf(false) }

 LaunchedEffect(notice) { notice?.let { snackbar.showSnackbar(it); vm.clearNotice() } }
 fun navigate(route: String) {
  nav.navigate(route) { popUpTo(nav.graph.startDestinationId) { saveState = true }; launchSingleTop = true; restoreState = true }
 }
 var now by remember { mutableLongStateOf(Instant.now().epochSecond) }
 LaunchedEffect(Unit) { while (true) { now = Instant.now().epochSecond; delay(60.seconds) } }

 val navItems = listOf(
  NavItem("today", "Today", Icons.Filled.Star, Icons.Outlined.Star),
  NavItem("plan", "Plan outdoors", Icons.Filled.DateRange, Icons.Outlined.DateRange),
  NavItem("places", "Places", Icons.Filled.LocationOn, Icons.Outlined.LocationOn),
 )

 Scaffold(
  topBar = {
   TopAppBar(
    title = {
     Box {
      Column {
       Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("WeatherWise", fontWeight = FontWeight.Bold)
        if (places.size > 1) {
         IconButton(onClick = { placeMenuOpen = true }, modifier = Modifier.size(28.dp)) {
          Icon(Icons.Default.ArrowDropDown, contentDescription = "Switch place")
         }
        }
       }
       Text(
        weather.place?.name?.let { "$it · Forecast" } ?: "A little forecast. A better day.",
        style = MaterialTheme.typography.labelMedium,
        color = Color(0xFF53647C)
       )
      }
      DropdownMenu(expanded = placeMenuOpen, onDismissRequest = { placeMenuOpen = false }) {
       places.forEach { place ->
        DropdownMenuItem(
         text = { Text(place.name, fontWeight = if (place.id == weather.place?.id) FontWeight.Bold else FontWeight.Normal) },
         onClick = { vm.select(place); placeMenuOpen = false },
         leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Blue) }
        )
       }
      }
     }
    },
    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
   )
  },
  containerColor = MaterialTheme.colorScheme.background,
  snackbarHost = { SnackbarHost(snackbar) },
  bottomBar = {
   NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 8.dp) {
    navItems.forEach { item ->
     NavigationBarItem(
      selected = current == item.route,
      onClick = { navigate(item.route) },
      icon = { Icon(if (current == item.route) item.iconSelected else item.iconUnselected, contentDescription = item.label) },
      label = { Text(item.label) }
     )
    }
   }
  }
 ) { padding ->
  NavHost(navController = nav, startDestination = "today", modifier = Modifier.padding(padding)) {
   composable("today") { TodayScreen(weather, now, vm::refresh, choose = { navigate("places") }, plan = { navigate("plan") }) }
   composable("plan") { PlannerScreen(weather, now, vm::refresh, choose = { navigate("places") }) }
   composable("places") { PlacesScreen(query, search, places, vm::queryChanged, vm::findPlaces, choose = { vm.select(it); navigate("today") }, remove = vm::remove) }
  }
 }
}

@Composable
private fun Heading(eyebrow: String, title: String, subtitle: String) {
 Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
  Text(eyebrow.uppercase(), color = Blue, style = MaterialTheme.typography.labelMedium, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
  Text(title, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
  Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF53647C))
 }
}

@Composable
private fun Message(text: String) {
 Surface(color = Color(0xFFFFF0D5), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
  Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
   Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF664919))
   Text(text, color = Color(0xFF664919), style = MaterialTheme.typography.bodyMedium)
  }
 }
}

@Composable
private fun Attribution() {
 val uri = androidx.compose.ui.platform.LocalUriHandler.current
 Column {
  TextButton(onClick = { uri.openUri("https://open-meteo.com/") }) { Text("Weather: Open-Meteo · Places: GeoNames", color = Color(0xFF53647C)) }
  TextButton(onClick = { uri.openUri("https://creativecommons.org/licenses/by/4.0/") }) { Text("CC BY 4.0 · Activity suggestions by WeatherWise", color = Color(0xFF53647C)) }
 }
}

@Composable
private fun ForecastStatus(state: WeatherState, now: Long, refresh: () -> Unit) {
 Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
  if (state.loading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
  state.error?.let { Message(it) }
  state.updated?.let {
   val zone = state.forecast?.zone ?: state.place?.zone ?: "UTC"
   Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF53647C))
    Text("Last downloaded ${formatTime(it / 1000, zone)} · $zone", style = MaterialTheme.typography.labelMedium, color = Color(0xFF53647C))
   }
   if (now - (it / 1000) > 21600) Message("This forecast is more than 6 hours old. Refresh before planning your day.")
  }
  OutlinedButton(
   onClick = refresh,
   enabled = !state.loading && state.place != null,
   shape = RoundedCornerShape(12.dp)
  ) {
   Icon(Icons.Outlined.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
   Spacer(Modifier.width(6.dp))
   Text(if (state.loading) "Refreshing…" else "Refresh forecast")
  }
 }
}

@Composable
private fun EmptyPlace(onChoose: () -> Unit) {
 Card(
  modifier = Modifier.fillMaxWidth(),
  shape = RoundedCornerShape(24.dp),
  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
 ) {
  Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
   Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
    Surface(shape = RoundedCornerShape(16.dp), color = Blue.copy(alpha = 0.1f)) {
     Icon(Icons.Outlined.Star, contentDescription = null, modifier = Modifier.padding(12.dp).size(36.dp), tint = Blue)
    }
    Column {
     Text("YOUR DAY, THOUGHTFULLY PLANNED", color = Blue, style = MaterialTheme.typography.labelMedium, letterSpacing = 1.5.sp, fontWeight = FontWeight.Bold)
     Text("Find your next good-weather moment.", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
   }
   Text("Choose a town to explore the forecast and plan time outdoors.", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF53647C))
   Button(
    onClick = onChoose,
    shape = RoundedCornerShape(14.dp),
    modifier = Modifier.fillMaxWidth()
   ) {
    Icon(Icons.Default.Search, contentDescription = null)
    Spacer(Modifier.width(8.dp))
    Text("Find a place", fontWeight = FontWeight.SemiBold)
   }
   Surface(shape = RoundedCornerShape(12.dp), color = Cream) {
    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
     Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF53647C), modifier = Modifier.size(18.dp))
     Text("No account. No GPS permission. Saved forecasts stay on your device.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF53647C))
    }
   }
  }
 }
}

@Composable
private fun WeatherHero(place: Place, hour: Hour?, zone: String) {
 Card(
  shape = RoundedCornerShape(28.dp),
  colors = CardDefaults.cardColors(containerColor = Color.Transparent),
  modifier = Modifier.fillMaxWidth()
 ) {
  Column(
   Modifier.fillMaxWidth()
    .background(Brush.linearGradient(listOf(Color(0xFF173B84), Color(0xFF3262C6))))
    .padding(24.dp),
   verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
    Column(Modifier.weight(1f)) {
     Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color(0xFFDFE8FF), modifier = Modifier.size(20.dp))
      Text(place.name, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
     }
     Text(listOf(place.region, place.country).filter { it.isNotBlank() }.distinct().joinToString(", "), color = Color(0xFFDFE8FF))
    }
    Icon(Icons.Filled.Star, contentDescription = null, tint = Amber, modifier = Modifier.size(44.dp))
   }
   Text(hour?.temperature?.let { "${it.roundToInt()}°" } ?: "—", fontSize = 72.sp, color = Color.White, fontWeight = FontWeight.Light)
   Text(condition(hour?.code), color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Medium)
   Text(hour?.let { "Hourly forecast · ${formatTime(it.epoch, zone)}" } ?: "Refresh to load the forecast", color = Color(0xFFDFE8FF), style = MaterialTheme.typography.bodyMedium)
   Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
     Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      Text("Rain ${hour?.rain?.let { "$it%" } ?: "—"}", color = Color.White, style = MaterialTheme.typography.labelLarge)
     }
    }
    Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
     Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Icon(Icons.Outlined.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
      Text("Wind ${hour?.wind?.let { "${it.roundToInt()} km/h" } ?: "—"}", color = Color.White, style = MaterialTheme.typography.labelLarge)
     }
    }
   }
  }
 }
}

@Composable
private fun ForecastChart(hours: List<Hour>, zone: String, activity: Activity = Activity.WALK) {
 if (hours.isEmpty()) return
 Card(
  shape = RoundedCornerShape(24.dp),
  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  modifier = Modifier.fillMaxWidth(),
  elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
 ) {
  Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    Column {
     Text("24-HOUR TREND", style = MaterialTheme.typography.labelMedium, color = Blue, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
     Text("Temperature & Rain", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
     Box(Modifier.size(10.dp).background(Color(0xFF386A50), CircleShape))
     Text("Good fit", style = MaterialTheme.typography.labelSmall, color = Color(0xFF53647C))
    }
   }
   LazyRow(
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
   ) {
    items(hours.take(24), key = { it.epoch }) { h ->
     val good = suitable(h, activity)
     val tempStr = h.temperature?.let { "${it.roundToInt()}°" } ?: "—"
     val rain = h.rain ?: 0
     Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(6.dp),
      modifier = Modifier.width(52.dp)
     ) {
      Text(tempStr, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge, color = Ink)
      Box(
       modifier = Modifier
        .width(36.dp)
        .height(70.dp)
        .background(
         color = if (good) Color(0xFFEAF3E2) else Color(0xFFF0F4FA),
         shape = RoundedCornerShape(12.dp)
        ),
       contentAlignment = Alignment.BottomCenter
      ) {
       if (rain > 0) {
        Box(
         modifier = Modifier
          .fillMaxWidth()
          .fillMaxHeight((rain / 100f).coerceIn(0.15f, 1f))
          .background(Color(0xFF5282E0).copy(alpha = 0.45f), RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
        )
       }
       Column(
        Modifier.fillMaxSize().padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
       ) {
        if (good) {
         Box(Modifier.padding(top = 4.dp).size(8.dp).background(Color(0xFF2E5E3B), CircleShape))
        } else {
         Spacer(Modifier.height(8.dp))
        }
        Text("$rain%", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp, color = if (rain > 30) Blue else Color(0xFF7A8A9E), fontWeight = FontWeight.Bold)
       }
      }
      Text(formatTime(h.epoch, zone, "HH:mm"), style = MaterialTheme.typography.labelSmall, color = Color(0xFF53647C))
     }
    }
   }
  }
 }
}

@Composable
private fun PlanInvite(hour: Hour?, zone: String, onPlan: () -> Unit) {
 Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF3E2)), shape = RoundedCornerShape(24.dp)) {
  Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
   Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF2E5E3B), modifier = Modifier.size(18.dp))
    Text("MAKE ROOM FOR OUTSIDE", style = MaterialTheme.typography.labelMedium, color = Color(0xFF2E5E3B), fontWeight = FontWeight.Bold)
   }
   Text(hour?.let { "A good hour for a walk: ${formatTime(it.epoch, zone)}" } ?: "Find an hour that fits your plans.", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
   Text("Compare the next 24 forecast hours for walking, cycling or a picnic.")
   Button(onClick = onPlan, shape = RoundedCornerShape(12.dp)) {
    Text("Plan outdoors")
    Spacer(Modifier.width(6.dp))
    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
   }
  }
 }
}

@Composable
private fun TodayScreen(state: WeatherState, now: Long, refresh: () -> Unit, choose: () -> Unit, plan: () -> Unit) {
 val place = state.place
 if (place == null) { Column { state.error?.let { Message(it) }; EmptyPlace(choose) }; return }
 val forecast = state.forecast
 val hours = upcoming(forecast?.hours.orEmpty(), now)
 val current = forecast?.hours?.lastOrNull { it.epoch <= now && now - it.epoch < 3600 }
 val zone = forecast?.zone ?: place.zone
 BoxWithConstraints(Modifier.fillMaxSize()) {
  val wide = this.maxWidth >= 700.dp
  LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
   item { Heading("Your daily outlook", "Hello, outdoors.", "Local forecast for ${place.name}. All times use the place’s time zone.") }
   item {
    if (wide) Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
     Box(Modifier.weight(1f)) { WeatherHero(place, current, zone) }
     Box(Modifier.weight(1f)) { PlanInvite(hours.firstOrNull { suitable(it, Activity.WALK) }, zone, plan) }
    } else Column(verticalArrangement = Arrangement.spacedBy(18.dp)) { WeatherHero(place, current, zone); PlanInvite(hours.firstOrNull { suitable(it, Activity.WALK) }, zone, plan) }
   }
   item { ForecastChart(hours, zone, Activity.WALK) }
   item { ForecastStatus(state, now, refresh) }
   item { Text("Coming up", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
   if (hours.isEmpty() && !state.loading) item { Message("No upcoming hours are available. Connect and refresh to download a forecast.") }
   items(hours.take(6), key = { it.epoch }) { HourCard(it, zone, null) }
   item { TextButton(onClick = choose) { Text("Change place") }; Attribution() }
  }
 }
}

@Composable
private fun HourCard(hour: Hour, zone: String, activity: Activity?) {
 val good = activity?.let { suitable(hour, it) } == true
 Card(
  colors = CardDefaults.cardColors(containerColor = if (good) Color(0xFFEAF3E2) else MaterialTheme.colorScheme.surface),
  shape = RoundedCornerShape(18.dp),
  modifier = Modifier.fillMaxWidth(),
  elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
 ) {
  Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
     Icon(Icons.Filled.Star, contentDescription = null, tint = if (good) Color(0xFF2E5E3B) else Blue, modifier = Modifier.size(20.dp))
     Text(formatTime(hour.epoch, zone), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
    Text(hour.temperature?.let { "${it.roundToInt()}°C" } ?: "—", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
   }
   Text(condition(hour.code), style = MaterialTheme.typography.bodyMedium, color = Ink)
   Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
     Text("Rain ${hour.rain?.let { "$it%" } ?: "—"}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF53647C))
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
     Text("Wind ${hour.wind?.let { "${it.roundToInt()} km/h" } ?: "—"}", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF53647C))
    }
   }
   if (activity != null) {
    Surface(
     color = if (good) Color(0xFF386A50).copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.1f),
     shape = RoundedCornerShape(8.dp)
    ) {
     Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
      Icon(
       if (good) Icons.Default.CheckCircle else Icons.Default.Info,
       contentDescription = null,
       modifier = Modifier.size(14.dp),
       tint = if (good) Color(0xFF2E5E3B) else Color(0xFF58647A)
      )
      Text(
       if (good) "Good fit for your ${activity.label.lowercase()}" else "Outside preferences or incomplete data",
       color = if (good) Color(0xFF2E5E3B) else Color(0xFF58647A),
       style = MaterialTheme.typography.labelMedium
      )
     }
    }
   }
  }
 }
}

@Composable
private fun PlannerScreen(state: WeatherState, now: Long, refresh: () -> Unit, choose: () -> Unit) {
 if (state.place == null) { EmptyPlace(choose); return }
 var activityName by rememberSaveable { mutableStateOf(value = Activity.WALK.name) }
 var onlyGood by rememberSaveable { mutableStateOf(value = false) }
 val activity = Activity.valueOf(activityName)
 val hours = upcoming(state.forecast?.hours.orEmpty(), now)
 val displayed = if (onlyGood) hours.filter { suitable(it, activity) } else hours

 LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
  item { Heading("A forecast with a purpose", "Plan outdoors", "${state.place.name} · Next 24 forecast hours") }
  item {
   Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    Activity.entries.forEach { a ->
     FilterChip(
      selected = activity == a,
      onClick = { activityName = a.name },
      label = { Text(a.label) }
     )
    }
   }
  }
  item { ForecastChart(hours, state.forecast?.zone ?: state.place.zone, activity) }
  item {
   Surface(color = Amber.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp)) {
    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
     Icon(Icons.Outlined.Info, contentDescription = null, tint = Color(0xFF8A6200))
     Text(activity.explanation, color = Color(0xFF5C4100), style = MaterialTheme.typography.bodyMedium)
    }
   }
  }
  item {
   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    Text("Only show good fits", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    Switch(checked = onlyGood, onCheckedChange = { onlyGood = it })
   }
  }
  item { ForecastStatus(state, now, refresh) }
  if (displayed.isEmpty()) item { Message(if (hours.isEmpty()) "No upcoming forecast is saved. Refresh when connected." else "No hours match these preferences. Try another activity or show all hours.") }
  items(displayed, key = { it.epoch }) { HourCard(it, state.forecast?.zone ?: state.place.zone, activity) }
  item { Text("Suggestions are simple preferences, not safety advice. Check local warnings before heading out.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF53647C)); Attribution() }
 }
}

@Composable
private fun PlacesScreen(query: String, state: SearchState, places: List<Place>, change: (String) -> Unit, search: () -> Unit, choose: (Place) -> Unit, remove: (Place) -> Unit) {
 var deleting by remember { mutableStateOf<Place?>(null) }
 deleting?.let { place -> AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Remove ${place.name}?") }, text = { Text("This also deletes its downloaded forecast from this device.") }, confirmButton = { TextButton(onClick = { remove(place); deleting = null }) { Text("Remove") } }, dismissButton = { TextButton(onClick = { deleting = null }) { Text("Keep") } }) }
 LazyColumn(contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
  item { Heading("Near home or far away", "Your places", "Search by town or city. Selecting a result saves it for offline use.") }
  item {
   OutlinedTextField(
    value = query,
    onValueChange = change,
    label = { Text("Town or city") },
    placeholder = { Text("Try Aberdeen") },
    singleLine = true,
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
    trailingIcon = {
     if (query.isNotEmpty()) {
      IconButton(onClick = { change("") }) { Icon(Icons.Default.Clear, contentDescription = "Clear") }
     }
    },
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
    keyboardActions = KeyboardActions(onSearch = { search() })
   )
  }
  item {
   Button(
    onClick = search,
    enabled = !state.loading,
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp)
   ) {
    Icon(Icons.Default.Search, contentDescription = null)
    Spacer(Modifier.width(8.dp))
    Text(if (state.loading) "Searching…" else "Search places")
   }
  }
  if (state.loading) item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
  state.error?.let { item { Message(it) } }
  if (state.searched && state.results.isEmpty()) item { Message("No places found. Try a nearby city or a different spelling.") }
  if (state.results.isNotEmpty()) item { Text("Search results", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
  items(state.results, key = { "search-${it.id}" }) { place -> PlaceCard(place, { choose(place) }, null) }
  item { Text("Saved places · ${places.size}/10", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
  if (places.isEmpty()) item { Text("Your saved places will appear here. Search for your first town above.", color = Color(0xFF53647C)) }
  items(places, key = { "saved-${it.id}" }) { place -> PlaceCard(place, { choose(place) }, { deleting = place }) }
  item { Text("Search text and selected coordinates are sent to Open-Meteo. No GPS access or account is needed. Saved places refresh approximately every six hours when connected; Android controls the exact timing.", style = MaterialTheme.typography.bodySmall, color = Color(0xFF53647C)); Attribution() }
 }
}

@Composable
private fun PlaceCard(place: Place, choose: () -> Unit, remove: (() -> Unit)?) {
 Card(
  onClick = choose,
  shape = RoundedCornerShape(18.dp),
  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  modifier = Modifier.fillMaxWidth(),
  elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
 ) {
  Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
   Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
     Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = Blue, modifier = Modifier.size(20.dp))
     Text(place.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
    Text(place.zone, style = MaterialTheme.typography.labelSmall, color = Color(0xFF53647C))
   }
   Text(listOf(place.region, place.country).filter { it.isNotBlank() }.distinct().joinToString(", "), color = Color(0xFF53647C), style = MaterialTheme.typography.bodyMedium)
   Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    FilledTonalButton(onClick = choose, shape = RoundedCornerShape(10.dp)) {
     Text("View forecast")
    }
    if (remove != null) {
     OutlinedButton(onClick = remove, shape = RoundedCornerShape(10.dp)) {
      Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
      Spacer(Modifier.width(4.dp))
      Text("Remove")
     }
    }
   }
  }
 }
}
