package com.example.model

import kotlin.math.ceil

/**
 * Reusable text analysis component.
 *
 * Implements deterministic, whitespace-aware, and edge-case resilient text metrics:
 * - Words: whitespace-delimited tokens containing alphanumeric content.
 * - Characters (Total): total character count including whitespace and newlines.
 * - Characters (No Spaces): non-whitespace characters only.
 * - Sentences: meaningful segments ending in [.!?] or standalone clauses with letters/digits.
 * - Paragraphs: non-empty text blocks separated by one or more blank lines.
 * - Punctuation: punctuation marks adhering to Unicode standards and ASCII punctuation.
 * - Numbers: discrete numeric entities (integers and decimals).
 */
object TextAnalyzer {

    private val SENTENCE_DELIMITER_REGEX = Regex("[.!?]+")
    private val NUMBER_TOKEN_REGEX = Regex("\\b\\d+(?:[.,]\\d+)*\\b|\\d+")
    private val PARAGRAPH_SPLIT_REGEX = Regex("(\\r?\\n\\s*){2,}")
    private val WHITESPACE_REGEX = Regex("\\s+")

    /**
     * Performs a complete analysis of the provided text string.
     */
    fun analyze(text: String): TextMetrics {
        if (text.isBlank()) {
            return TextMetrics.ZERO
        }

        val totalChars = text.length
        val charsNoSpaces = countCharactersNoSpaces(text)
        val wordList = extractWords(text)
        val wordCount = wordList.size
        val sentenceCount = countSentences(text)
        val paragraphCount = countParagraphs(text)
        val punctuationCount = countPunctuation(text)
        val numberCount = countNumbers(text)
        val digitCount = countNumericDigits(text)

        val readingSeconds = if (wordCount > 0) ceil((wordCount / 200.0) * 60.0).toInt() else 0
        val speakingSeconds = if (wordCount > 0) ceil((wordCount / 130.0) * 60.0).toInt() else 0

        val totalWordCharLength = wordList.sumOf { it.length }
        val avgWordLength = if (wordCount > 0) totalWordCharLength.toFloat() / wordCount else 0.0f

        return TextMetrics(
            words = wordCount,
            characters = totalChars,
            charactersNoSpaces = charsNoSpaces,
            sentences = sentenceCount,
            paragraphs = paragraphCount,
            punctuation = punctuationCount,
            numbers = numberCount,
            numericDigits = digitCount,
            readingTimeSeconds = readingSeconds,
            speakingTimeSeconds = speakingSeconds,
            avgWordLength = avgWordLength
        )
    }

    /**
     * Counts characters excluding all forms of whitespace (spaces, tabs, newlines).
     */
    fun countCharactersNoSpaces(text: String): Int {
        var count = 0
        for (i in text.indices) {
            if (!text[i].isWhitespace()) {
                count++
            }
        }
        return count
    }

    /**
     * Extracts meaningful word tokens. Multiple consecutive whitespace characters
     * do not produce empty words. Isolated punctuation marks are filtered out.
     */
    fun extractWords(text: String): List<String> {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return emptyList()

        return trimmed.split(WHITESPACE_REGEX)
            .filter { token ->
                // A word token must contain at least one letter or digit
                token.any { ch -> ch.isLetterOrDigit() }
            }
    }

    /**
     * Counts the number of words.
     */
    fun countWords(text: String): Int {
        return extractWords(text).size
    }

    /**
     * Counts sentences. Recognizes terminal punctuation (. ! ?) and avoids counting
     * empty sentences or consecutive punctuation (e.g. ellipses '...').
     * Standalone non-punctuated text containing alphanumeric characters counts as 1 sentence.
     */
    fun countSentences(text: String): Int {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return 0

        val rawParts = trimmed.split(SENTENCE_DELIMITER_REGEX)
        val validSentences = rawParts.map { it.trim() }.filter { segment ->
            segment.any { ch -> ch.isLetterOrDigit() }
        }

        return validSentences.size
    }

    /**
     * Counts punctuation marks using Unicode classification and common typographical punctuation.
     */
    fun countPunctuation(text: String): Int {
        var count = 0
        for (i in text.indices) {
            val ch = text[i]
            if (isPunctuation(ch)) {
                count++
            }
        }
        return count
    }

    /**
     * Determines whether a character is a punctuation mark.
     */
    fun isPunctuation(ch: Char): Boolean {
        val type = Character.getType(ch)
        return when (type) {
            Character.CONNECTOR_PUNCTUATION.toInt(),
            Character.DASH_PUNCTUATION.toInt(),
            Character.START_PUNCTUATION.toInt(),
            Character.END_PUNCTUATION.toInt(),
            Character.INITIAL_QUOTE_PUNCTUATION.toInt(),
            Character.FINAL_QUOTE_PUNCTUATION.toInt(),
            Character.OTHER_PUNCTUATION.toInt() -> true
            else -> ch in "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~¡¿«»“”‘’—–…"
        }
    }

    /**
     * Counts distinct numeric entities (e.g., "1", "42", "3.14").
     */
    fun countNumbers(text: String): Int {
        if (text.isBlank()) return 0
        return NUMBER_TOKEN_REGEX.findAll(text).count()
    }

    /**
     * Counts individual numeric digit characters ('0'..'9').
     */
    fun countNumericDigits(text: String): Int {
        var count = 0
        for (i in text.indices) {
            if (text[i].isDigit()) {
                count++
            }
        }
        return count
    }

    /**
     * Counts paragraphs. Text blocks separated by one or more blank lines form paragraphs.
     * Empty lines, leading blank lines, and trailing blank lines are never counted.
     */
    fun countParagraphs(text: String): Int {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return 0

        val paragraphs = trimmed.split(PARAGRAPH_SPLIT_REGEX)
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return paragraphs.size
    }
}
