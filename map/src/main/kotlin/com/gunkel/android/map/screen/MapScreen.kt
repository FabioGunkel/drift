package com.gunkel.android.map.screen

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Parcel
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.LocationBias
import com.google.android.libraries.places.api.model.LocationRestriction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.search.SearchBar
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.gunkel.android.map.R
import com.gunkel.android.map.commons.getBitmapFromVectorDrawable


@Composable
@RequiresPermission(anyOf = [ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION])
fun MapScreen() {
    var uiSettings by remember { mutableStateOf(MapUiSettings(myLocationButtonEnabled = true)) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    var currentLocation by remember {
        mutableStateOf(LatLng(0.0, 0.0))
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
    }

    var location = LocationServices.getFusedLocationProviderClient(LocalContext.current)

    var markerState = rememberMarkerState(position = currentLocation)
    location.lastLocation.addOnCompleteListener {
        currentLocation = LatLng(it.result.latitude, it.result.longitude)

        cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation, 15f)
        markerState.position = currentLocation
    }
    val markersList: ArrayList<Place> by remember {
        mutableStateOf(arrayListOf())
    }


    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            properties = properties,
            uiSettings = uiSettings,
            cameraPositionState = cameraPositionState
        ) {
            markersList.forEach { place ->
                Marker(
                    state = MarkerState(place.latLng),
                    title = place.name
                )
            }
            Marker(
                state = markerState,
                title = "You",
                snippet = "Marker in your location",
                icon = BitmapDescriptorFactory.fromBitmap(
                    getBitmapFromVectorDrawable(
                        LocalContext.current,
                        R.drawable.icon_walk
                    )
                )

            )
        }

        val context = LocalContext.current
        Button(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(vertical = 16.dp),
            onClick = {
                runPlacesAlgorithm(
                    context = context,
                    currentLocation = currentLocation,
                    markersList = markersList
                )
            }) {
            Text(text = "A Deriva!")
        }


    }
}

fun runPlacesAlgorithm(context: Context, currentLocation: LatLng, markersList: ArrayList<Place>) {
    // Create a new PlacesClient instance
    val placesClient = Places.createClient(context)
    placesClient.run {
        val builder = FindAutocompletePredictionsRequest.builder()
        builder.typesFilter = mutableListOf(
            PlaceTypes.RESTAURANT,
            PlaceTypes.BAR,
            PlaceTypes.PARK,
            PlaceTypes.MUSEUM,
            PlaceTypes.TOURIST_ATTRACTION
        )
        builder.setOrigin(currentLocation)

        val southWestBounds = LatLng(currentLocation.latitude - 1, currentLocation.longitude - 1)
        val northEastBounds = LatLng(currentLocation.latitude + 1, currentLocation.longitude + 1)
        builder.locationRestriction =
            RectangularBounds.newInstance(southWestBounds, northEastBounds)

        val research = findAutocompletePredictions(builder.build())
        research.addOnSuccessListener { predictions ->
            val placesSugestions = predictions.autocompletePredictions.toList()
            placesSugestions.forEach {prediction ->
                fetchPlace(
                    FetchPlaceRequest.builder(prediction.placeId, arrayListOf()).build()
                ).addOnSuccessListener {
                    markersList.add(it.place)
                }
            }

        }

    }
}


@SuppressLint("MissingPermission")
@Preview
@Composable
fun PreviewMapScreen() {
    MapScreen()
}