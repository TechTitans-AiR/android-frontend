package hr.foi.techtitans.ttpay.products.model_products

import java.io.Serializable

data class NewService(
    val serviceName: String,
    val description: String,
    val serviceProvider: String,
    val price: Int,
    val currency: String,
    val duration: Int,
    val availability: String,
    val serviceLocation: String,
    val durationUnit: String
): Serializable

