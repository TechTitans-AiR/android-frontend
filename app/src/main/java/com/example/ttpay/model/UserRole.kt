package com.example.ttpay.model

data class UserRole(
    val id: String,
    val name: String,
    val code: String
) {
    companion object {
        val admin = UserRole("1", "admin", "A")
        val merchant = UserRole("2", "merchant", "M")
    }
}
