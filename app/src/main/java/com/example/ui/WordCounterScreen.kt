package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SpaceBar
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.ui.components.EditorSection
import com.example.ui.components.HeroTelemetryCard
import com.example.ui.components.HistorySection
import com.example.ui.components.MetricCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonGreenBright
import com.example.ui.theme.NeonGreenPrimary
import com.example.viewmodel.UiEvent
import com.example.viewmodel.WordCounterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordCounterScreen(
    viewModel: WordCounterViewModel = viewModel()
) {
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current

    // Listen to ViewModel UI events (Snackbars, undo, etc.)
    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = if (event.actionLabel != null) SnackbarDuration.Short else SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        event.onAction?.invoke()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonGreenPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Terminal,
                                contentDescription = null,
                                tint = NeonGreenPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.app_subtitle),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Copy complete telemetry report action
                    IconButton(
                        onClick = {
                            if (metrics.isEmpty) {
                                snackbarHostState.currentSnackbarData?.dismiss()
                            } else {
                                val report = metrics.toTelemetryReport()
                                clipboardManager.setText(AnnotatedString(report))
                            }
                        },
                        modifier = Modifier
                            .testTag("copy_telemetry_report_button")
                            .padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.cd_copy_metrics),
                            tint = if (metrics.isEmpty) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else NeonGreenPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("main_screen_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 720.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. Hero Telemetry Banner (Words, Characters, Non-whitespace ratio, Read/Speak speed)
                        HeroTelemetryCard(
                            metrics = metrics
                        )

                        // 2. Large Vertically Scrollable Text Editor with Actions
                        EditorSection(
                            text = inputText,
                            onTextChanged = { viewModel.onTextChanged(it) },
                            onClearClicked = { viewModel.clearText() },
                            onGenerateSampleClicked = { viewModel.generateSampleText() },
                            onSaveToHistoryClicked = { viewModel.recordCurrentToHistory(silent = false) },
                            onCopiedTextFeedback = {
                                clipboardManager.setText(AnnotatedString(inputText))
                            },
                            onPastedTextFeedback = { /* Handled in EditorSection */ }
                        )

                        // 3. Grid of Detailed Metric Cards
                        DetailedMetricsGrid(
                            metrics = metrics
                        )

                        // 4. Runtime Calculation History Section
                        HistorySection(
                            historyList = history,
                            onLoadItem = { viewModel.loadHistoryItem(it) },
                            onDeleteItem = { viewModel.deleteHistoryItem(it) },
                            onClearAll = { viewModel.clearHistory() }
                        )

                        // Lab assignment footer notice
                        Text(
                            text = "Mobile Application Development Technologies • Lab Assignment // Text Analyzer Core v1.0",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailedMetricsGrid(
    metrics: com.example.model.TextMetrics,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Row 1: Sentences & Paragraphs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.metric_sentences),
                value = metrics.sentences,
                icon = Icons.Default.PendingActions,
                testTag = "metric_card_sentences",
                subValueText = if (metrics.sentences > 0) "${metrics.words / metrics.sentences.coerceAtLeast(1)} avg words/sent" else "0 sentences",
                accentColor = NeonGreenPrimary,
                glowTag = "SENTENCES",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = stringResource(R.string.metric_paragraphs),
                value = metrics.paragraphs,
                icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
                testTag = "metric_card_paragraphs",
                subValueText = if (metrics.paragraphs > 0) "${metrics.sentences / metrics.paragraphs.coerceAtLeast(1)} sent/paragraph" else "0 paragraphs",
                accentColor = CyberCyan,
                glowTag = "PARAS",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 2: Characters (No Spaces) & Punctuation Marks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.metric_characters_no_spaces),
                value = metrics.charactersNoSpaces,
                icon = Icons.Default.SpaceBar,
                testTag = "metric_card_characters_no_spaces",
                subValueText = "${metrics.characters - metrics.charactersNoSpaces} whitespace chars",
                accentColor = NeonGreenBright,
                glowTag = "NO SPACES",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = stringResource(R.string.metric_punctuation),
                value = metrics.punctuation,
                icon = Icons.Default.Spellcheck,
                testTag = "metric_card_punctuation",
                subValueText = "Symbols & marks",
                accentColor = Color(0xFFFFB300),
                glowTag = "PUNCT",
                modifier = Modifier.weight(1f)
            )
        }

        // Row 3: Numbers & Digits
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = stringResource(R.string.metric_numbers),
                value = metrics.numbers,
                icon = Icons.Default.Numbers,
                testTag = "metric_card_numbers",
                subValueText = stringResource(R.string.metric_digits_detail, metrics.numericDigits),
                accentColor = Color(0xFFFF5252),
                glowTag = "NUMBERS",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = stringResource(R.string.metric_avg_word_length),
                value = metrics.avgWordLength.toInt(),
                icon = Icons.Default.Calculate,
                testTag = "metric_card_avg_word_length",
                subValueText = "${String.format(java.util.Locale.US, "%.1f", metrics.avgWordLength)} characters/word",
                accentColor = Color(0xFFB388FF),
                glowTag = "AVG LEN",
                modifier = Modifier.weight(1f)
            )
        }
    }
}
