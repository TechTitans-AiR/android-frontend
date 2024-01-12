package hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement

import android.os.Parcel
import android.os.Parcelable

class Catalog (
    val id: String?,
    val name: String,
    val articles: String,
    val services: String,
    val users: String,
    val date_created: String?,
    val date_modified: String?,
    var disabled: Boolean
): Parcelable {

    // Konstruktor za čitanje iz Parcela
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    )

    // Metoda za pisanje u Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(articles)
        parcel.writeString(services)
        parcel.writeString(users)
        parcel.writeString(date_created)
        parcel.writeString(date_modified)
        parcel.writeByte(if (disabled) 1 else 0)
    }

    // Metoda za kreiranje objekta iz Parcela
    override fun describeContents(): Int {
        return 0
    }

    // Creator objekta, potreban za deserialize
    companion object CREATOR : Parcelable.Creator<Catalog> {
        override fun createFromParcel(parcel: Parcel): Catalog {
            return Catalog(parcel)
        }

        override fun newArray(size: Int): Array<Catalog?> {
            return arrayOfNulls(size)
        }
    }
}
