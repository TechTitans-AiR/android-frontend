package com.example.ttpay.model

import java.time.LocalDateTime
import java.time.OffsetDateTime

class Catalog (
    val id: String?,
    val name: String,
    val articles: List<Article>,
    val service: List<Service>,
    val users: List<User>?,
    val date_created: OffsetDateTime,
    val date_modified: OffsetDateTime
)