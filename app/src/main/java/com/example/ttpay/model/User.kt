package com.example.ttpay.model

import java.util.Date

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Date,
    val address: String,
    val phone: String,
    val email: String,
    val username: String,
    val password: String,
    val role: UserRole,
    val userStatus: UserStatus,
    val dateCreated: Date,
    val dateModified: Date
) {
    companion object {
        // function to create an admin user
        fun createAdmin(): User {
            return User(
                id = 1,
                firstName = "Admin",
                lastName = "Admin",
                dateOfBirth = Date(),
                address = "Zrmanjska ulica",
                phone = "099709980",
                email = "admin@example.com",
                username = "admin",
                password = "admin",
                role = UserRole.admin,
                userStatus = UserStatus.active,
                dateCreated = Date(),
                dateModified = Date()
            )
        }
    }
}