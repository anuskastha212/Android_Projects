package com.example.esewa_project.data.model

import com.google.android.gms.maps.model.LatLng

data class LocationSearchResult(
    val title: String,
    val subtitle: String,
    val placeId: String? = null,
    val latLng: LatLng? = null
)
