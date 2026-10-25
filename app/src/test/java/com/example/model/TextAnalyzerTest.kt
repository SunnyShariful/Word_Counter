package com.example.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TextAnalyzerTest {

    @Test
    fun `analyze returns ZERO for blank text`() {
        val result = TextAnalyzer.analyze("   \n  ") //[cite: 1]
        assertEquals(TextMetrics.ZERO, result) //[cite: 1]
    }

    @Test
    fun `analyze calculates complete metrics correctly for zero word branches`() {
        val result = TextAnalyzer.analyze("!!!") //[cite: 1]
        assertEquals(0, result.words) //[cite: 1]
        assertEquals(0, result.readingTimeSeconds) //[cite: 1]
        assertEquals(0, result.speakingTimeSeconds) //[cite: 1]
        assertEquals(0.0f, result.avgWordLength) //[cite: 1]
    }

    @Test
    fun `analyze calculates complete metrics correctly for normal text`() {
        val text = "Hello world! This is 42."
        val result = TextAnalyzer.analyze(text) //[cite: 1]

        assertEquals(24, result.characters) //[cite: 1]
        assertEquals(20, result.charactersNoSpaces) //[cite: 1]
        assertEquals(5, result.words) //[cite: 1]
        assertEquals(2, result.sentences) //[cite: 1]
        assertEquals(1, result.paragraphs) //[cite: 1]
        assertEquals(2, result.punctuation) // ! and .[cite: 1]
        assertEquals(1, result.numbers) // 42[cite: 1]
        assertEquals(2, result.numericDigits) // 4, 2[cite: 1]
        assertEquals(2, result.readingTimeSeconds) // ceil((5/200)*60) = 2[cite: 1]
        assertEquals(3, result.speakingTimeSeconds) // ceil((5/130)*60) = 3[cite: 1]
        assertEquals(3.6f, result.avgWordLength) // (5+5+4+2+2)/5 = 18/5 = 3.6[cite: 1]
    }

    @Test
    fun `countCharactersNoSpaces excludes all whitespace`() {
        val text = "a \t b \n c"
        assertEquals(3, TextAnalyzer.countCharactersNoSpaces(text)) //[cite: 1]
    }

    @Test
    fun `extractWords handles consecutive whitespaces and isolated punctuation`() {
        val text = "  Hello   world  -  "
        val words = TextAnalyzer.extractWords(text) //[cite: 1]
        assertEquals(listOf("Hello", "world"), words) //[cite: 1]
    }

    @Test
    fun `extractWords returns empty list for blank text`() {
        assertTrue(TextAnalyzer.extractWords("   ").isEmpty()) //[cite: 1]
    }

    @Test
    fun `countWords matches extractWords size`() {
        assertEquals(2, TextAnalyzer.countWords("Hello world")) //[cite: 1]
    }

    @Test
    fun `countSentences recognizes terminal punctuation and standalone clauses`() {
        assertEquals(0, TextAnalyzer.countSentences("   ")) //[cite: 1]
        assertEquals(2, TextAnalyzer.countSentences("Sentence one. Sentence two!")) //[cite: 1]
        assertEquals(1, TextAnalyzer.countSentences("Standalone clause without punctuation")) //[cite: 1]
        assertEquals(0, TextAnalyzer.countSentences("... !!! ???")) // No alphanumeric[cite: 1]
    }

    @Test
    fun `countPunctuation and isPunctuation identify characters correctly`() {
        val text = "a, b! (c) 'd' “e” —"
        assertEquals(9, TextAnalyzer.countPunctuation(text)) //[cite: 1]

        // Test isPunctuation branches
        assertTrue(TextAnalyzer.isPunctuation(',')) //[cite: 1]
        assertTrue(TextAnalyzer.isPunctuation('-')) //[cite: 1]
        assertTrue(TextAnalyzer.isPunctuation('(')) //[cite: 1]
        assertTrue(TextAnalyzer.isPunctuation(')')) //[cite: 1]
        assertTrue(TextAnalyzer.isPunctuation('“')) //[cite: 1]
        assertTrue(TextAnalyzer.isPunctuation('”')) //[cite: 1]
        assertTrue(TextAnalyzer.isPunctuation('~')) //[cite: 1]
        assertFalse(TextAnalyzer.isPunctuation('a')) //[cite: 1]
        assertFalse(TextAnalyzer.isPunctuation('1')) //[cite: 1]
        assertFalse(TextAnalyzer.isPunctuation(' ')) //[cite: 1]
    }

    @Test
    fun `countNumbers handles integers and decimals`() {
        assertEquals(0, TextAnalyzer.countNumbers("   ")) //[cite: 1]
        assertEquals(3, TextAnalyzer.countNumbers("I have 1 apple, 2.5 oranges, and 1,000 grapes.")) //[cite: 1]
    }

    @Test
    fun `countNumericDigits counts distinct digit characters`() {
        assertEquals(4, TextAnalyzer.countNumericDigits("Version 1.23a4")) //[cite: 1]
    }

    @Test
    fun `countParagraphs identifies blocks separated by multiple newlines`() {
        assertEquals(0, TextAnalyzer.countParagraphs("   \n   ")) //[cite: 1]
        assertEquals(1, TextAnalyzer.countParagraphs("Single paragraph.")) //[cite: 1]
        assertEquals(2, TextAnalyzer.countParagraphs("Para 1\n\nPara 2")) //[cite: 1]
        assertEquals(2, TextAnalyzer.countParagraphs("\n\nPara 1\r\n\r\nPara 2\n\n")) //[cite: 1]
    }
}