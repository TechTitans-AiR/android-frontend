package com.example.ttpay.products.model_products

import java.io.Serializable

data class Article(
    val id: String,
    val itemCategory: ItemCategory,
    val name: String,
    val description: String,
    val price: Double,
    val quantityInStock: Int,
    val weight: Double?,
    val material: String?,
    val brand: String,
    val currency: String
): Serializable
