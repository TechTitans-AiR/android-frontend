package com.example.ttpay.products.model_products

import java.io.Serializable

data class Service(
    val id: String,
    val serviceName: String,
    val description: String,
    val serviceProvider: String,
    val price: Int,
    val currency: String,
    val duration: Int,
    val availability: String,
    val serviceLocation: String,
    val durationUnit: String
): Serializable {

    fun convertPriceToDouble(): Double {
        return price.toDouble()
    }
}
