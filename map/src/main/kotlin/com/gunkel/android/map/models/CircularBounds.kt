package com.gunkel.android.map.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.LocationRestriction

abstract class CircularBounds() : LocationRestriction {
    constructor(parcel: Parcel) : this() {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object {

    }
}