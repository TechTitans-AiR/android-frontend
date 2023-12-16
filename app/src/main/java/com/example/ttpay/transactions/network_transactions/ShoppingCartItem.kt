package com.example.ttpay.transactions.network_transactions

import android.os.Parcel
import android.os.Parcelable

data class ShoppingCartItem(
    val name: String,
    var quantity: Int,
    var unitPrice: Double,
    val isArticle: Boolean
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(quantity)
        parcel.writeDouble(unitPrice)
        parcel.writeByte(if (isArticle) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ShoppingCartItem> {
        override fun createFromParcel(parcel: Parcel): ShoppingCartItem {
            return ShoppingCartItem(parcel)
        }

        override fun newArray(size: Int): Array<ShoppingCartItem?> {
            return arrayOfNulls(size)
        }
    }
}