package com.example.ttpay.model

import android.os.Parcel
import android.os.Parcelable

data class UserStatus(
    val id: String,
    val name: String
)  : Parcelable {

    // Parcelable implementacija
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserStatus> {
        override fun createFromParcel(parcel: Parcel): UserStatus {
            return UserStatus(parcel)
        }

        override fun newArray(size: Int): Array<UserStatus?> {
            return arrayOfNulls(size)
        }

        // Definicija statičkih objekata
        val active = UserStatus("1", "active")
        val inactive = UserStatus("2", "inactive")
        val blocked = UserStatus("3", "blocked")
    }
    override fun toString(): String {
        return name
    }
}
