package hr.foi.techtitans.ttpay.products.model_products

import java.io.Serializable

data class Article(
    val id: String,
    val itemCategory: ItemCategory,
    val name: String,
    val description: String,
    val price: Double,
    val quantity_in_stock: Int,
    val weight: Double?,
    val material: String?,
    val brand: String,
    val currency: String
): Serializable

data class NewArticle(
    val name: String,
    //val itemCategory: ItemCategory,
    val description: String,
    val price: Double,
    val quantity_in_stock: Int,
    val weight: Double?,
    val material: String?,
    val brand: String,
    val currency: String
): Serializable