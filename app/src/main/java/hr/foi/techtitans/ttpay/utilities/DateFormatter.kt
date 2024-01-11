package hr.foi.techtitans.ttpay.utilities

import java.text.SimpleDateFormat
import java.util.Locale

class DateFormatter {
    companion object {
        fun formatDate(dateString: String?): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

                val date = inputFormat.parse(dateString ?: "")
                outputFormat.format(date ?: "")
            } catch (e: Exception) {
                dateString ?: ""
            }
        }
    }
}