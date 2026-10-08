# Word Counter - Text Diagnostics & Analysis Engine

A native Android application built with **Kotlin**, **Jetpack Compose**, and **Material 3** for university Mobile Application Development Technologies laboratory coursework.

---

## 🎮 Aesthetic & Interface Design
The application features a sleek, high-tech **gaming utility aesthetic** built upon a dark graphite/black base (`#090D0B`) with vibrant neon green (`#00E676`) and cyber cyan (`#00E5FF`) telemetry accents, glowing status indicators, and responsive card layouts.

---

## 📊 Text Metrics Specification & Algorithms

### 1. Word Count (`Words`)
- **Algorithm:** The text is trimmed and split across arbitrary whitespace boundaries using the regular expression `\s+`.
- **Filtering:** Tokens that do not contain at least one alphanumeric character (e.g. isolated punctuation marks like `,` or `...`) are discarded.
- **Edge Cases Handled:**
  - Multiple consecutive spaces (e.g. `"Hello     world"`) yield exactly `2` words.
  - Leading and trailing whitespace or newlines do not produce empty words.
  - Hyphenated words (e.g. `"state-of-the-art"`) count as 1 word.
  - Empty or whitespace-only input strictly returns `0` words.

### 2. Character Counts (`Characters` & `Characters No Spaces`)
- **Total Characters:** Exact length of the input stream (`text.length`), including letters, digits, spaces, tabs, and newlines.
- **Characters (No Spaces):** Calculated by filtering out all whitespace characters via `text.count { !it.isWhitespace() }`.

### 3. Sentence Count (`Sentences`)
- **Algorithm:** Splits the text using terminal punctuation delimiters `[.!?]+`.
- **Filtering:** Each delimited substring is trimmed; segments containing at least one alphanumeric character are counted as distinct sentences.
- **Ellipses & Repeated Punctuation:** Sequences such as `...` or `?!` are treated as single delimiters and do not inflate the sentence count.
- **Unpunctuated Clauses:** Any standalone text block containing words without terminal punctuation counts as `1` sentence.
- **Lab Benchmark Examples:**
  - `"Good morning."` → 1 sentence
  - `"The war started on September 1. Sad."` → 2 sentences

### 4. Punctuation Count (`Punctuation`)
- **Rule:** Identifies characters matching Unicode punctuation classifications:
  - Connector punctuation (e.g. `_`)
  - Dash punctuation (e.g. `-`, `—`, `–`)
  - Start/End brackets and quotes (e.g. `(`, `)`, `[`, `]`, `{`, `}`, `“`, `”`, `«`, `»`)
  - Standard ASCII and typographical punctuation marks: `.` `,` `!` `?` `:` `;` `'` `"` `/` `\` `@` `#` `%` `*` `&` `^` `~`
- **Lab Benchmark Example:**
  - `"The war started on September 1. Sad."` → 2 punctuation marks (periods)

### 5. Number Count (`Numbers` & `Digits`)
- **Algorithm:** Recognizes discrete numeric entities via regex `\b\d+(?:[.,]\d+)*\b|\d+`.
- **Supported Formats:** Whole integers (e.g. `42`, `2026`) and decimal numbers (e.g. `99.8`, `3.14`).
- **Digits Sub-metric:** Additionally counts raw digit characters (`'0'..'9'`) for transparent inspection.
- **Lab Benchmark Example:**
  - `"The war started on September 1. Sad."` → 1 number (`1`)

### 6. Paragraph Count (`Paragraphs`)
- **Rule:** Identifies discrete blocks of text separated by one or more blank lines (matched by `(\r?\n\s*){2,}`).
- **Edge Cases Handled:**
  - Single line breaks within a paragraph do not increment the paragraph count.
  - Consecutive blank lines are collapsed and do not create false paragraphs.
  - Leading and trailing blank lines are excluded.
  - Empty or whitespace-only input returns `0` paragraphs.

---

## 🏛️ Architecture & Clean Code Principles
The codebase adheres strictly to Android **MVVM** architecture and separation of concerns:

- `com.example.model.TextAnalyzer`: Reusable, purely functional, standalone calculation engine. Tested independently of the Android UI runtime.
- `com.example.model.TextMetrics`: Immutable data holder containing all calculated statistics and telemetry formatting helpers.
- `com.example.model.HistoryItem`: Data model representing an execution snapshot in the runtime calculation history.
- `com.example.viewmodel.WordCounterViewModel`: State holder utilizing Kotlin Coroutines and `MutableStateFlow` to provide synchronous real-time UI updates.
- `com.example.ui.WordCounterScreen`: Compose screen integrating responsive Material 3 layout, adaptive constraints, and accessible touch targets.
- `com.example.ui.components`: Modular Composables (`HeroTelemetryCard`, `MetricCard`, `EditorSection`, `HistorySection`).

---

## 🧪 Unit Testing Coverage
Local JVM Unit Tests in `com.example.model.TextAnalyzerTest` cover:
1. Empty input
2. Single word
3. Multiple words
4. Multiple consecutive spaces (`"Hello     world"`)
5. Leading and trailing whitespace / tabs
6. Multiple sentences and terminal punctuation
7. Numbers (integers, decimals, and digit counts)
8. Comprehensive punctuation types
9. Multiple paragraphs separated by blank lines
10. Consecutive blank lines
11. Single line breaks inside a single paragraph
12. Lone punctuation marks without alphanumeric characters
13. University assignment benchmark phrases
