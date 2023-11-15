package com.example.ttpay.model

data class UserRole(
    val id: Int,
    val name: String,
    val attributeCode: String
) {
    companion object {
        val admin = UserRole(1, "admin", "A")
        val merchant = UserRole(2, "merchant", "M")
    }
}
