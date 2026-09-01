package com.example.esewa_project.ui.compose

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.example.esewa_project.data.model.LocationSearchResult
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun MapLocation(
    onLocationConfirmed: (Double, Double) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val initialLocation = LatLng(27.6766, 85.3184)

    val placesClient = remember {
        try {
            if (!Places.isInitialized()) {
                val appInfo = context.packageManager.getApplicationInfo(
                    context.packageName,
                    PackageManager.GET_META_DATA
                )
                val apiKey = appInfo.metaData?.getString("com.google.android.geo.API_KEY") ?: ""
                if (apiKey.isNotEmpty()) {
                    Places.initialize(context.applicationContext, apiKey)
                }
            }
            if (Places.isInitialized()) Places.createClient(context) else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 17f)
    }

    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<LocationSearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery) {
        if (searchQuery.trim().length >= 2) {
            isSearching = true
            delay(400)

            if (placesClient != null) {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(searchQuery)
                    .setCountries("NP")
                    .build()

                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        suggestions = response.autocompletePredictions.map { prediction ->
                            LocationSearchResult(
                                title = prediction.getPrimaryText(null).toString(),
                                subtitle = prediction.getSecondaryText(null).toString(),
                                placeId = prediction.placeId
                            )
                        }
                        isSearching = false
                    }
                    .addOnFailureListener {
                        coroutineScope.launch(Dispatchers.IO) {
                            val results = queryGeocoderFallback(context, searchQuery)
                            withContext(Dispatchers.Main) {
                                suggestions = results
                                isSearching = false
                            }
                        }
                    }
            } else {
                withContext(Dispatchers.IO) {
                    val results = queryGeocoderFallback(context, searchQuery)
                    withContext(Dispatchers.Main) {
                        suggestions = results
                        isSearching = false
                    }
                }
            }
        } else {
            suggestions = emptyList()
            isSearching = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false),
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFF2ABB00), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Selected ✓",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(12.dp)
                    .background(Color(0xFF2ABB00), CircleShape)
            )
        }

        // Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
                .padding(top = 32.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.Gray
                        )
                    }
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search location...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF2ABB00)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
            // Suggestions List
            if (suggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 220.dp)
                    ) {
                        items(suggestions.size) { index ->
                            val result = suggestions[index]
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (result.placeId != null && placesClient != null) {
                                            val placeFields = listOf(Place.Field.LAT_LNG)
                                            val fetchRequest = FetchPlaceRequest.newInstance(
                                                result.placeId,
                                                placeFields
                                            )
                                            placesClient.fetchPlace(fetchRequest)
                                                .addOnSuccessListener { response ->
                                                    val latLng = response.place.latLng
                                                    if (latLng != null) {
                                                        coroutineScope.launch {
                                                            cameraPositionState.animate(
                                                                CameraUpdateFactory.newLatLngZoom(
                                                                    latLng,
                                                                    17f
                                                                )
                                                            )
                                                        }
                                                    }
                                                    searchQuery = ""
                                                    suggestions = emptyList()
                                                }
                                        } else if (result.latLng != null) {
                                            coroutineScope.launch {
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newLatLngZoom(
                                                        result.latLng,
                                                        17f
                                                    )
                                                )
                                            }
                                            searchQuery = ""
                                            suggestions = emptyList()
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = result.title,
                                        fontSize = 14.sp,
                                        color = Color(0xFF292A40),
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (result.subtitle.isNotEmpty()) {
                                        Text(
                                            text = result.subtitle,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                            HorizontalDivider(color = Color(0xFFF1F1F5))
                        }
                    }
                }
            }
        }

        // My Location FAB
        FloatingActionButton(
            onClick = {
                coroutineScope.launch {
                    cameraPositionState.animate(
                        CameraUpdateFactory.newLatLngZoom(initialLocation, 17f)
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 140.dp, end = 16.dp),
            containerColor = Color.White,
            contentColor = Color(0xFF2ABB00)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = "My Location"
            )
        }

        // Bottom Confirmation Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Selected Location",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF292A40)
                )

                val currentTarget = cameraPositionState.position.target
                Text(
                    text = "Lat: ${
                        String.format(
                            Locale.US,
                            "%.4f",
                            currentTarget.latitude
                        )
                    }, Lng: ${String.format(Locale.US, "%.4f", currentTarget.longitude)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onLocationConfirmed(
                            currentTarget.latitude,
                            currentTarget.longitude
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2ABB00)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "CONFIRM LOCATION",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
private fun queryGeocoderFallback(
    context: Context,
    query: String
): List<LocationSearchResult> {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val queryWithCountry = if (query.lowercase().contains("nepal")) query else "$query, Nepal"

        val addresses = geocoder.getFromLocationName(queryWithCountry, 5) ?: emptyList()

        addresses.map { address ->
            LocationSearchResult(
                title = address.featureName ?: address.locality ?: query,
                subtitle = address.getAddressLine(0) ?: "Nepal",
                latLng = LatLng(address.latitude, address.longitude)
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}