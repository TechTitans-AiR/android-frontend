package com.example.ttpay.model

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.Date

data class User(
    val id: String?,
    val username: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val address: String?,
    val phone: String?,
    val date_of_birth: String?, // date format "dd.MM.yyyy"
    val date_created: String, // date format "dd.MM.yyyy HH:mm:ss"
    val date_modified: String?, // date format "dd.MM.yyyy HH:mm:ss"
    val userRole: UserRole?,
    val userStatus: UserStatus?
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readParcelable(UserRole::class.java.classLoader),
        parcel.readParcelable(UserStatus::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeString(email)
        parcel.writeString(address)
        parcel.writeString(phone)
        parcel.writeString(date_of_birth)
        parcel.writeString(date_created)
        parcel.writeString(date_modified)
        parcel.writeParcelable(userRole, flags)
        parcel.writeParcelable(userStatus, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

    val admin: User
        get() {
            val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date())

            return User(
                id = "1",
                first_name = "Admin",
                last_name = "Admin",
                date_of_birth = "01.01.1990",
                address = "Zrmanjska",
                phone = "099709980",
                email = "admin@example.com",
                username = "admin",
                password = "admin",
                userRole = UserRole.admin,
                userStatus = UserStatus.active,
                date_created = currentDate,
                date_modified = currentDate
            )
        }
}

data class newUser(
    val username: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val address: String?,
    val phone: String?,
    val date_of_birth: String?, // date format "dd.MM.yyyy"
    val date_created: String, // date format "dd.MM.yyyy HH:mm:ss"
    val date_modified: String?, // date format "dd.MM.yyyy HH:mm:ss"
    val userRole: String?,
    val userStatus: String?
)