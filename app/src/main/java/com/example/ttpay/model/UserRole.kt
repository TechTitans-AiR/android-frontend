package com.example.ttpay.model

import android.os.Parcel
import android.os.Parcelable

data class UserRole(
    val id: String,
    val name: String,
    val code: String
) : Parcelable {

    // Parcelable implementacija
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(code)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserRole> {
        override fun createFromParcel(parcel: Parcel): UserRole {
            return UserRole(parcel)
        }

        override fun newArray(size: Int): Array<UserRole?> {
            return arrayOfNulls(size)
        }

        // Definicija statičkih objekata
        val admin = UserRole("1", "admin", "A")
        val merchant = UserRole("2", "merchant", "M")
    }

}
