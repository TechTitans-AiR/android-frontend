package com.example.ttpay.model

data class UserStatus(
    val id: Int,
    val name: String
) {
    companion object {
        val active = UserStatus(1, "active")
        val inactive = UserStatus(2, "inactive")
        val blocked = UserStatus(3, "blocked")
    }
}
