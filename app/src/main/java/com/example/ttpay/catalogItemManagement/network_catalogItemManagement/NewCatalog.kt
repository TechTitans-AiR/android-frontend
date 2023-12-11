package com.example.ttpay.catalogItemManagement.network_catalogItemManagement

import com.example.ttpay.model.Article
import com.example.ttpay.model.Service
import com.example.ttpay.model.User

data class NewCatalog(
    val name: String,
    val listArticles:List<Article>,
    val listServices:List<Service>,
    val user: List<User>
)
