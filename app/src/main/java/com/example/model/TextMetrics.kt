package com.example.model

/**
 * Immutable data holder for all calculated text metrics.
 */
data class TextMetrics(
    val words: Int = 0,
    val characters: Int = 0,
    val charactersNoSpaces: Int = 0,
    val sentences: Int = 0,
    val paragraphs: Int = 0,
    val punctuation: Int = 0,
    val numbers: Int = 0,
    val numericDigits: Int = 0,
    val readingTimeSeconds: Int = 0,
    val speakingTimeSeconds: Int = 0,
    val avgWordLength: Float = 0.0f
) {
    val isEmpty: Boolean
        get() = characters == 0

    /**
     * Formats reading time into a human-readable string (e.g. "45s" or "2m 15s").
     */
    fun formattedReadingTime(): String {
        if (readingTimeSeconds < 60) {
            return "${readingTimeSeconds}s"
        }
        val mins = readingTimeSeconds / 60
        val secs = readingTimeSeconds % 60
        return if (secs > 0) "${mins}m ${secs}s" else "${mins}m"
    }

    /**
     * Formats speaking time into a human-readable string.
     */
    fun formattedSpeakingTime(): String {
        if (speakingTimeSeconds < 60) {
            return "${speakingTimeSeconds}s"
        }
        val mins = speakingTimeSeconds / 60
        val secs = speakingTimeSeconds % 60
        return if (secs > 0) "${mins}m ${secs}s" else "${mins}m"
    }

    /**
     * Generates a clean telemetry text report suitable for clipboard sharing.
     */
    fun toTelemetryReport(): String {
        return buildString {
            appendLine("=== WORD COUNTER TELEMETRY REPORT ===")
            appendLine("• Words: $words")
            appendLine("• Characters (Total): $characters")
            appendLine("• Characters (No Spaces): $charactersNoSpaces")
            appendLine("• Sentences: $sentences")
            appendLine("• Paragraphs: $paragraphs")
            appendLine("• Punctuation Marks: $punctuation")
            appendLine("• Numbers: $numbers ($numericDigits numeric digits)")
            appendLine("• Estimated Reading Time: ${formattedReadingTime()}")
            appendLine("• Estimated Speaking Time: ${formattedSpeakingTime()}")
            appendLine("• Average Word Length: ${String.format(java.util.Locale.US, "%.1f", avgWordLength)} chars")
            append("======================================")
        }
    }

    companion object {
        val ZERO = TextMetrics()
    }
}
