package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * Represents a single calculation record in the application runtime history.
 */
data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val fullText: String,
    val previewText: String = createPreview(fullText),
    val metrics: TextMetrics
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(timestamp))

    companion object {
        private const val MAX_PREVIEW_LENGTH = 50

        fun createPreview(text: String): String {
            val singleLine = text.replace(Regex("\\s+"), " ").trim()
            return if (singleLine.length > MAX_PREVIEW_LENGTH) {
                singleLine.take(MAX_PREVIEW_LENGTH) + "…"
            } else {
                singleLine
            }
        }
    }
}
