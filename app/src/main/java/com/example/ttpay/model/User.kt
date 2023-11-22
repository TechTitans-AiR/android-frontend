package com.example.ttpay.model

import java.text.SimpleDateFormat
import java.util.Date

data class User(
    val id: String,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val address: String,
    val phone: String,
    val dateOfBirth: String, // Change to String for date format "dd.MM.yyyy"
    val dateCreated: String, // Change to String for date format "dd.MM.yyyy HH:mm:ss"
    val dateModified: String, // Change to String for date format "dd.MM.yyyy HH:mm:ss"
    val role: UserRole,
    val userStatus: UserStatus
) {
    companion object {
        // function to create an admin user
        fun createAdmin(): User {
            val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date())

            return User(
                id = "1",
                firstName = "Admin",
                lastName = "Admin",
                dateOfBirth = "01.01.1990",
                address = "Zrmanjska",
                phone = "099709980",
                email = "admin@example.com",
                username = "admin",
                password = "admin",
                role = UserRole.admin,
                userStatus = UserStatus.active,
                dateCreated = currentDate,
                dateModified = currentDate
            )
        }
    }
}