package com.example.ttpay.model

import java.text.SimpleDateFormat
import java.util.Date

data class User(
    val id: String?,
    val username: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val address: String?,
    val phone: String?,
    val date_of_birth: String?, // Change to String for date format "dd.MM.yyyy"
    val date_created: String, // Change to String for date format "dd.MM.yyyy HH:mm:ss"
    val date_modified: String, // Change to String for date format "dd.MM.yyyy HH:mm:ss"
    val userRole: UserRole?,
    val userStatus: UserStatus?
) {
    companion object {
        // function to create an admin user
        fun createAdmin(): User {
            val currentDate = SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Date())

            return User(
                id = "1",
                first_name = "Admin",
                last_name = "Admin",
                date_of_birth = "01.01.1990",
                address = "Zrmanjska",
                phone = "099709980",
                email = "admin@example.com",
                username = "admin",
                password = "admin",
                userRole = UserRole.admin,
                userStatus = UserStatus.active,
                date_created = currentDate,
                date_modified = currentDate
            )
        }
    }
}