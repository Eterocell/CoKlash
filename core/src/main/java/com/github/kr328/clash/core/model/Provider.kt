package com.github.kr328.clash.core.model

import android.os.Parcel
import android.os.Parcelable
import com.github.kr328.clash.core.util.Parcelizer
import kotlinx.serialization.Serializable

@Serializable
data class Provider(
    val name: String,
    val type: Type,
    val vehicleType: VehicleType,
    val updatedAt: Long,
) : Parcelable,
    Comparable<Provider> {
    enum class Type {
        Proxy,
        Rule,
    }

    enum class VehicleType {
        HTTP,
        File,
        Compatible,
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        Parcelizer.encodeToParcel(serializer(), parcel, this)
    }

    override fun describeContents(): Int = 0

    override fun compareTo(other: Provider): Int = compareValuesBy(this, other, Provider::type, Provider::name)

    companion object CREATOR : Parcelable.Creator<Provider> {
        override fun createFromParcel(parcel: Parcel): Provider = Parcelizer.decodeFromParcel(serializer(), parcel)

        override fun newArray(size: Int): Array<Provider?> = arrayOfNulls(size)
    }
}
