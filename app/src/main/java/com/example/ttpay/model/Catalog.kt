package com.example.ttpay.model

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDateTime
import java.time.OffsetDateTime

class Catalog (
    val id: String?,
    val name: String,
    val articles: String,
    val services: String,
    val users: String,
    val date_created: String?,
    val date_modified: String?
)
