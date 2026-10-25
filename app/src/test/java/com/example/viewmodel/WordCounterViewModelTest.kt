package com.example.viewmodel

import com.example.model.HistoryItem
import com.example.model.TextMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WordCounterViewModelTest {

    private lateinit var viewModel: WordCounterViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        viewModel = WordCounterViewModel() //[cite: 2]
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onTextChanged updates inputText and metrics`() {
        viewModel.onTextChanged("Hello") //[cite: 2]
        assertEquals("Hello", viewModel.inputText.value) //[cite: 2]
        assertEquals(1, viewModel.metrics.value.words) //[cite: 2]
    }

    @Test
    fun `clearText on empty text emits Snackbar without clearing`() = runTest {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvents.toList(events) }

        viewModel.clearText()
        val event = events.first() as UiEvent.ShowSnackbar
        assertEquals("Editor is already empty.", event.message)
    }

    @Test
    fun `clearText on populated text clears and offers undo`() = runTest {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvents.toList(events) }

        viewModel.onTextChanged("Test data")
        viewModel.clearText()

        assertEquals("", viewModel.inputText.value)

        val event = events.first() as UiEvent.ShowSnackbar
        assertEquals("Input text cleared.", event.message)
        assertNotNull(event.onAction)

        // Trigger undo
        event.onAction?.invoke()
        assertEquals("Test data", viewModel.inputText.value)

        val undoEvent = events.last() as UiEvent.ShowSnackbar
        assertEquals("Text restored.", undoEvent.message)
    }

    @Test
    fun `undoClear does nothing if previous text is null`() = runTest {
        viewModel.undoClear()
        assertEquals("", viewModel.inputText.value)
    }

    @Test
    fun `generateSampleText cycles presets and records to history silently`() = runTest {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvents.toList(events) }

        viewModel.generateSampleText()
        assertEquals(WordCounterViewModel.SAMPLE_PRESETS[0], viewModel.inputText.value)
        assertEquals(1, viewModel.history.value.size)
        assertEquals("Sample test scenario generated.", (events.first() as UiEvent.ShowSnackbar).message)

        viewModel.generateSampleText()
        assertEquals(WordCounterViewModel.SAMPLE_PRESETS[1], viewModel.inputText.value)
    }

    @Test
    fun `recordCurrentToHistory ignores blank text`() = runTest {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvents.toList(events) }

        viewModel.onTextChanged("   ")
        viewModel.recordCurrentToHistory(silent = false)

        assertTrue(viewModel.history.value.isEmpty())
        assertEquals("Input is empty. Enter text first.", (events.first() as UiEvent.ShowSnackbar).message)
    }

    @Test
    fun `recordCurrentToHistory ignores duplicates`() = runTest {
        val events = mutableListOf<UiEvent>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiEvents.toList(events) }

        viewModel.onTextChanged("Unique text")
        viewModel.recordCurrentToHistory(silent = false)
        viewModel.recordCurrentToHistory(silent = false)

        assertEquals(1, viewModel.history.value.size)
        assertEquals("Telemetry snapshot already recorded.", (events.last() as UiEvent.ShowSnackbar).message)
    }

    @Test
    fun `recordCurrentToHistory respects MAX_HISTORY_ITEMS`() = runTest {
        for (i in 1..26) {
            viewModel.onTextChanged("Text $i") //[cite: 2]
            viewModel.recordCurrentToHistory(silent = true) //[cite: 2]
        }
        assertEquals(25, viewModel.history.value.size) //[cite: 2]
        assertEquals("Text 26", viewModel.history.value.first().fullText) //[cite: 2]
    }

    @Test
    fun `loadHistoryItem updates editor`() = runTest {
        // Using named arguments prevents position-based type mismatches with default parameters like 'id'
        val item = HistoryItem(
            fullText = "History text",
            metrics = TextMetrics.ZERO
        )
        viewModel.loadHistoryItem(item) //[cite: 2]
        assertEquals("History text", viewModel.inputText.value) //[cite: 2]
    }

    @Test
    fun `deleteHistoryItem removes specific item`() = runTest {
        viewModel.onTextChanged("Keep") //[cite: 2]
        viewModel.recordCurrentToHistory(silent = true) //[cite: 2]
        viewModel.onTextChanged("Delete me") //[cite: 2]
        viewModel.recordCurrentToHistory(silent = true) //[cite: 2]

        val itemToDelete = viewModel.history.value.first { it.fullText == "Delete me" }
        viewModel.deleteHistoryItem(itemToDelete) //[cite: 2]

        assertEquals(1, viewModel.history.value.size) //[cite: 2]
        assertEquals("Keep", viewModel.history.value.first().fullText) //[cite: 2]
    }

    @Test
    fun `clearHistory empties list`() = runTest {
        viewModel.onTextChanged("Some text") //[cite: 2]
        viewModel.recordCurrentToHistory(silent = true) //[cite: 2]

        viewModel.clearHistory() //[cite: 2]
        assertTrue(viewModel.history.value.isEmpty()) //[cite: 2]
    }
}