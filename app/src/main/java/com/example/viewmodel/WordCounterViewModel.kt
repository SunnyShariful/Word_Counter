package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.HistoryItem
import com.example.model.TextAnalyzer
import com.example.model.TextMetrics
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null, val onAction: (() -> Unit)? = null) : UiEvent()
}

class WordCounterViewModel : ViewModel() {

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _metrics = MutableStateFlow(TextMetrics.ZERO)
    val metrics: StateFlow<TextMetrics> = _metrics.asStateFlow()

    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history: StateFlow<List<HistoryItem>> = _history.asStateFlow()

    private val _uiEvents = MutableSharedFlow<UiEvent>()
    val uiEvents: SharedFlow<UiEvent> = _uiEvents.asSharedFlow()

    private var previousClearedText: String? = null
    private var sampleIndex = 0

    companion object {
        private const val MAX_HISTORY_ITEMS = 25

        val SAMPLE_PRESETS = listOf(
            """System boot sequence initiated at 0400 hours. The core reactor has 3 operational nodes, running at 99.8% capacity!

All tactical units are deployed across Sector 7. Warning: anomalies detected near waypoint 42; proceed with caution.

Is the transmission secure? Yes! Mission status: GO.""",

            """The war started on September 1. Sad.

Good morning, soldiers! Prepare for combat round 3. Remember: victory requires 100% focus and zero mistakes.""",

            """Welcome to the Word Counter diagnostics lab. Here is test batch #42.

Notice how multiple spaces, tabs, and line breaks are handled seamlessly.
Does the paragraph counter detect blank lines? Yes, absolutely!

Final test metric: 123 items analyzed with 99.9% precision."""
        )
    }

    /**
     * Updates input text and recalculates metrics in real time.
     */
    fun onTextChanged(newText: String) {
        _inputText.value = newText
        _metrics.value = TextAnalyzer.analyze(newText)
    }

    /**
     * Clears editor input and provides an undo option via Snackbar.
     */
    fun clearText() {
        val current = _inputText.value
        if (current.isEmpty()) {
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowSnackbar("Editor is already empty."))
            }
            return
        }

        previousClearedText = current
        onTextChanged("")

        viewModelScope.launch {
            _uiEvents.emit(
                UiEvent.ShowSnackbar(
                    message = "Input text cleared.",
                    actionLabel = "UNDO",
                    onAction = { undoClear() }
                )
            )
        }
    }

    /**
     * Restores previously cleared text.
     */
    fun undoClear() {
        previousClearedText?.let { textToRestore ->
            onTextChanged(textToRestore)
            previousClearedText = null
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowSnackbar("Text restored."))
            }
        }
    }

    /**
     * Inserts sample test text containing words, sentences, punctuation, numbers, and paragraphs.
     */
    fun generateSampleText() {
        val sample = SAMPLE_PRESETS[sampleIndex % SAMPLE_PRESETS.size]
        sampleIndex++
        onTextChanged(sample)
        // Automatically snapshot the generated sample into history
        recordCurrentToHistory(silent = true)
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowSnackbar("Sample test scenario generated."))
        }
    }

    /**
     * Saves the current text analysis into the calculation history.
     */
    fun recordCurrentToHistory(silent: Boolean = false) {
        val text = _inputText.value
        if (text.isBlank()) {
            if (!silent) {
                viewModelScope.launch {
                    _uiEvents.emit(UiEvent.ShowSnackbar("Input is empty. Enter text first."))
                }
            }
            return
        }

        val currentMetrics = _metrics.value
        val existing = _history.value.firstOrNull { it.fullText == text }
        if (existing != null) {
            if (!silent) {
                viewModelScope.launch {
                    _uiEvents.emit(UiEvent.ShowSnackbar("Telemetry snapshot already recorded."))
                }
            }
            return
        }

        val newItem = HistoryItem(
            fullText = text,
            metrics = currentMetrics
        )

        val updatedList = listOf(newItem) + _history.value
        _history.value = updatedList.take(MAX_HISTORY_ITEMS)

        if (!silent) {
            viewModelScope.launch {
                _uiEvents.emit(UiEvent.ShowSnackbar("Telemetry snapshot recorded in history."))
            }
        }
    }

    /**
     * Loads a historical item back into the text editor.
     */
    fun loadHistoryItem(item: HistoryItem) {
        onTextChanged(item.fullText)
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowSnackbar("Loaded historical snapshot into editor."))
        }
    }

    /**
     * Deletes a specific history record.
     */
    fun deleteHistoryItem(item: HistoryItem) {
        _history.value = _history.value.filter { it.id != item.id }
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowSnackbar("Telemetry record removed."))
        }
    }

    /**
     * Clears all calculation history records.
     */
    fun clearHistory() {
        _history.value = emptyList()
        viewModelScope.launch {
            _uiEvents.emit(UiEvent.ShowSnackbar("Calculation history cleared."))
        }
    }
}
