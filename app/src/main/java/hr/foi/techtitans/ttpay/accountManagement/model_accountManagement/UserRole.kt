package hr.foi.techtitans.ttpay.accountManagement.model_accountManagement

import android.os.Parcel
import android.os.Parcelable

data class UserRole(
    val id: String,
    val name: String,
    val code: String
) : Parcelable {

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

        val admin = UserRole("655509aa074f8ba48dd82a12", "admin", "A")
        val merchant = UserRole("655509aa074f8ba48dd82a13", "merchant", "M")
    }

}
