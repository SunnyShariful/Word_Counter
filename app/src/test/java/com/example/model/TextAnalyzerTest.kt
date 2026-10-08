package com.example.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [TextAnalyzer] calculation logic.
 * Covers all required university lab test cases and edge cases.
 */
class TextAnalyzerTest {

    // 1. Empty input
    @Test
    fun testEmptyInput_returnsAllZeros() {
        val metrics = TextAnalyzer.analyze("")
        assertEquals(0, metrics.words)
        assertEquals(0, metrics.characters)
        assertEquals(0, metrics.charactersNoSpaces)
        assertEquals(0, metrics.sentences)
        assertEquals(0, metrics.paragraphs)
        assertEquals(0, metrics.punctuation)
        assertEquals(0, metrics.numbers)
        assertEquals(0, metrics.numericDigits)
        assertTrue(metrics.isEmpty)
    }

    @Test
    fun testWhitespaceOnlyInput_returnsAllZeros() {
        val metrics = TextAnalyzer.analyze("   \t\n\r   \n\n   ")
        assertEquals(0, metrics.words)
        assertEquals(0, metrics.sentences)
        assertEquals(0, metrics.paragraphs)
        assertEquals(0, metrics.punctuation)
        assertEquals(0, metrics.numbers)
    }

    // 2. Single word
    @Test
    fun testSingleWord_calculatesCorrectly() {
        val metrics = TextAnalyzer.analyze("Kotlin")
        assertEquals(1, metrics.words)
        assertEquals(6, metrics.characters)
        assertEquals(6, metrics.charactersNoSpaces)
        assertEquals(1, metrics.sentences)
        assertEquals(1, metrics.paragraphs)
        assertEquals(0, metrics.punctuation)
        assertEquals(0, metrics.numbers)
    }

    // 3. Multiple words
    @Test
    fun testMultipleWords_calculatesCorrectly() {
        val metrics = TextAnalyzer.analyze("Alpha Beta Gamma Delta")
        assertEquals(4, metrics.words)
        assertEquals(22, metrics.characters)
        assertEquals(19, metrics.charactersNoSpaces)
        assertEquals(1, metrics.sentences)
        assertEquals(1, metrics.paragraphs)
    }

    // 4. Multiple consecutive spaces
    @Test
    fun testMultipleSpaces_doesNotProduceEmptyWords() {
        val metrics = TextAnalyzer.analyze("Hello     world")
        assertEquals(2, metrics.words)
        assertEquals(15, metrics.characters)
        assertEquals(10, metrics.charactersNoSpaces)
    }

    // 5. Leading and trailing whitespace
    @Test
    fun testLeadingAndTrailingWhitespace_handledCleanly() {
        val metrics = TextAnalyzer.analyze("   \n\t  Quick brown fox   \t\n  ")
        assertEquals(3, metrics.words)
        assertEquals(1, metrics.sentences)
        assertEquals(1, metrics.paragraphs)
        assertEquals(13, metrics.charactersNoSpaces)
    }

    // 6. Multiple sentences & prompt benchmark
    @Test
    fun testPromptBenchmark_GoodMorning() {
        // "Good morning." → 1 sentence, 2 words, 1 punctuation, 0 numbers
        val metrics = TextAnalyzer.analyze("Good morning.")
        assertEquals(1, metrics.sentences)
        assertEquals(2, metrics.words)
        assertEquals(1, metrics.punctuation)
        assertEquals(0, metrics.numbers)
    }

    @Test
    fun testPromptBenchmark_TheWarStarted() {
        // "The war started on September 1. Sad." → 2 sentences, 7 words, 2 punctuation, 1 number
        val metrics = TextAnalyzer.analyze("The war started on September 1. Sad.")
        assertEquals(2, metrics.sentences)
        assertEquals(7, metrics.words)
        assertEquals(2, metrics.punctuation)
        assertEquals(1, metrics.numbers)
    }

    @Test
    fun testMultipleSentences_withVariedPunctuationAndEllipses() {
        val text = "Are you ready? Yes, I am! Let's go..."
        val metrics = TextAnalyzer.analyze(text)
        assertEquals(3, metrics.sentences)
        // Words: "Are", "you", "ready?", "Yes,", "I", "am!", "Let's", "go..." = 8 words
        assertEquals(8, metrics.words)
        // Punctuation marks: '?' + ',' + '!' + '\'' + '.' + '.' + '.' = 7
        assertEquals(7, metrics.punctuation)
    }

    // 7. Numbers
    @Test
    fun testNumbers_discreteEntitiesAndDigits() {
        val text = "Room 404 has 3 beds and 2.5 bathrooms."
        val metrics = TextAnalyzer.analyze(text)
        // Numbers: 404, 3, 2.5 -> 3 numbers
        assertEquals(3, metrics.numbers)
        // Digits: 4,0,4, 3, 2,5 -> 6 digits
        assertEquals(6, metrics.numericDigits)
    }

    // 8. Punctuation
    @Test
    fun testPunctuationMarks_comprehensiveTypes() {
        val text = "Hello, world! (Test: \"quoted\"-text; [1] / {2})"
        val metrics = TextAnalyzer.analyze(text)
        // Punctuation: , ! ( : " " - ; [ ] / { } = 14 punctuation marks
        assertTrue(metrics.punctuation >= 14)
    }

    // 9. Multiple paragraphs
    @Test
    fun testMultipleParagraphs_separatedByDefaultBlankLines() {
        val text = "Paragraph one starts here.\n\nParagraph two continues here.\n\nParagraph three concludes."
        val metrics = TextAnalyzer.analyze(text)
        assertEquals(3, metrics.paragraphs)
        assertEquals(3, metrics.sentences)
    }

    // 10. Consecutive blank lines
    @Test
    fun testConsecutiveBlankLines_doesNotInflateParagraphCount() {
        val text = "\n\n\nFirst block.\n\n\n\n\nSecond block.\n\n\n"
        val metrics = TextAnalyzer.analyze(text)
        assertEquals(2, metrics.paragraphs)
    }

    // 11. Single linebreaks inside a single paragraph
    @Test
    fun testSingleLineBreak_treatedAsSingleParagraph() {
        val text = "Line 1 of same paragraph\nLine 2 of same paragraph"
        val metrics = TextAnalyzer.analyze(text)
        assertEquals(1, metrics.paragraphs)
        assertEquals(10, metrics.words)
    }

    // 12. Lone punctuation marks
    @Test
    fun testIsolatedPunctuation_notCountedAsWordsOrSentences() {
        val text = "... --- !!! ???"
        val metrics = TextAnalyzer.analyze(text)
        assertEquals(0, metrics.words)
        assertEquals(0, metrics.sentences)
        assertEquals(12, metrics.punctuation)
    }

    // 13. Telemetry report generation
    @Test
    fun testTelemetryReport_containsAllMetrics() {
        val metrics = TextAnalyzer.analyze("Test run 1. Completed successfully!")
        val report = metrics.toTelemetryReport()
        assertTrue(report.contains("Words: 5"))
        assertTrue(report.contains("Sentences: 2"))
        assertTrue(report.contains("Numbers: 1"))
    }
}
