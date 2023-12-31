package hr.foi.techtitans.ttpay.login_username_password.data

import android.os.Parcel
import android.os.Parcelable

data class LoggedInUser(
    val username :String,
    val token: String,
    val role:String
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(username)
        parcel.writeString(token)
        parcel.writeString(role)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoggedInUser> {
        override fun createFromParcel(parcel: Parcel): LoggedInUser {
            return LoggedInUser(parcel)
        }

        override fun newArray(size: Int): Array<LoggedInUser?> {
            return arrayOfNulls(size)
        }
    }
}
