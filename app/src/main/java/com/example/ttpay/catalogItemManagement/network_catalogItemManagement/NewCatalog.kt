package com.example.ttpay.catalogItemManagement.network_catalogItemManagement

import com.example.ttpay.model.Article
import com.example.ttpay.model.Service
import com.example.ttpay.model.User

data class NewCatalog(
    val name: String,
    val articles:List<String>,
    val services:List<String>,
    val users:List<String>?,
    var disabled: Boolean
)
