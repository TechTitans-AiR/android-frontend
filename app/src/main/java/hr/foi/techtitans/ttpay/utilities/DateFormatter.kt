package hr.foi.techtitans.ttpay.utilities

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateFormatter {
    companion object {
        private const val OUTPUT_DATE_FORMAT = "dd.MM.yyyy HH:mm:ss"

        fun formatDate(dateString: String?): String {
            return try {
                val date = parseDate(dateString)
                formatOutputDate(date)
            } catch (e: Exception) {
                dateString ?: ""
            }
        }

        private fun parseDate(dateString: String?): Date? {
            val supportedFormats = listOf(
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss"
                // adding more supported formats as needed
            )

            for (format in supportedFormats) {
                try {
                    val parser = SimpleDateFormat(format, Locale.getDefault())
                    return parser.parse(dateString ?: "")
                } catch (e: ParseException) {
                    // Ignore, try the next format
                }
            }
            return null
        }

        private fun formatOutputDate(date: Date?): String {
            val outputFormat = SimpleDateFormat(OUTPUT_DATE_FORMAT, Locale.getDefault())
            return outputFormat.format(date ?: "")
        }
    }
}