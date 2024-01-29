package hr.foi.techtitans.ttpay.accountManagement.model_accountManagement

import android.os.Parcel
import android.os.Parcelable

data class UserStatus(
    val id: String,
    val name: String
)  : Parcelable {

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

        val active = UserStatus("65550a49074f8ba48dd82a19", "active")
        val inactive = UserStatus("65550a49074f8ba48dd82a1a", "inactive")
        val blocked = UserStatus("65550a49074f8ba48dd82a1b", "blocked")
    }
    override fun toString(): String {
        return name
    }
}
