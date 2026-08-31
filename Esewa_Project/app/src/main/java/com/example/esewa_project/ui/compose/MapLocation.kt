package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale

@Composable
fun MapLocation(
    onLocationConfirmed: (Double, Double) -> Unit,
    onClose: () -> Unit
) {
    // Default Location: Pulchowk, Lalitpur
    val initialLocation = LatLng(27.6766, 85.3184)

    // Tracks the map's movement and position
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialLocation, 17f)
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. The Google Map Layer
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = false), // Set to true if you request location permission later
            uiSettings = MapUiSettings(zoomControlsEnabled = false) // Hiding default zoom buttons for a cleaner UI
        )

        // 2. Top Search Bar Overlay
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(top = 32.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
                Text(
                    text = "Search location...",
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. The Static Center Pin (Remains fixed while map moves behind it)
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
            // The pin needle (pointing to the exact center)
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(12.dp)
                    .background(Color(0xFF2ABB00), CircleShape)
            )
        }

        // 4. "My Location" Target Button
        FloatingActionButton(
            onClick = { /* Will implement real GPS centering here later */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 140.dp, end = 16.dp),
            containerColor = Color.White,
            contentColor = Color(0xFF2ABB00)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "My Location")
        }

        // 5. Confirmation Card (Bottom)
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

                // Live updates based on map drag
                val currentTarget = cameraPositionState.position.target
                Text(
                    text = "Lat: ${String.format(Locale.US, "%.4f", currentTarget.latitude)}, Lng: ${String.format(Locale.US, "%.4f", currentTarget.longitude)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        onLocationConfirmed(currentTarget.latitude, currentTarget.longitude)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CONFIRM LOCATION", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}