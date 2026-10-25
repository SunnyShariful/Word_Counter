package com.example.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.R
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WordCounterScreenTest {

  @get:Rule
  val composeTestRule = createAndroidComposeRule<ComponentActivity>()

  @Before
  fun setUp() {
    composeTestRule.setContent {
      WordCounterScreen()
    }
  }

  /**
   * Scenario 1: Initial Render & Structure Verification
   */
  @Test
  fun verifyInitialScreenElements_areDisplayed() {
    val appName = composeTestRule.activity.getString(R.string.app_name)

    composeTestRule.onNodeWithText(appName).assertIsDisplayed()
    composeTestRule.onNodeWithTag("main_screen_list").assertIsDisplayed()
    composeTestRule.onNode(hasSetTextAction()).assertIsDisplayed()

    composeTestRule.onNodeWithTag("metric_card_sentences")
      .performScrollTo()
      .assertIsDisplayed()

    composeTestRule.onNodeWithTag("metric_card_paragraphs")
      .performScrollTo()
      .assertIsDisplayed()
  }

  /**
   * Scenario 2: Dynamic Metric Calculation
   * Fixes AssertionError by searching for the text nodes *inside* the metric card ancestors
   * using the unmerged tree, rather than expecting the parent container to merge semantics.
   */
  @Test
  fun enterText_updatesMetricCardsAccurately() {
    val sampleInput = "First sentence with 10 words here.\n\nSecond sentence has numbers like 42 and 99!"

    composeTestRule.onNode(hasSetTextAction())
      .performTextInput(sampleInput)

    composeTestRule.onNodeWithTag("metric_card_sentences").performScrollTo()
    composeTestRule.onNode(
      hasText("2") and hasAnyAncestor(hasTestTag("metric_card_sentences")),
      useUnmergedTree = true
    ).assertIsDisplayed()

    composeTestRule.onNodeWithTag("metric_card_paragraphs").performScrollTo()
    composeTestRule.onNode(
      hasText("2") and hasAnyAncestor(hasTestTag("metric_card_paragraphs")),
      useUnmergedTree = true
    ).assertIsDisplayed()

    composeTestRule.onNodeWithTag("metric_card_numbers").performScrollTo()
    composeTestRule.onNode(
      hasText("3") and hasAnyAncestor(hasTestTag("metric_card_numbers")),
      useUnmergedTree = true
    ).assertIsDisplayed()
  }

  /**
   * Scenario 3: Derived Sub-value Logic Verification
   * Verifies that the dynamic calculation string (e.g., words/sent) updates accurately based on input.
   */
  @Test
  fun enterText_updatesDerivedSubValuesAccurately() {
    val sampleInput = "Hello world." // 2 words, 1 sentence, 1 paragraph

    composeTestRule.onNode(hasSetTextAction())
      .performTextReplacement(sampleInput)

    composeTestRule.onNodeWithTag("metric_card_sentences").performScrollTo()
    composeTestRule.onNode(
      hasText("2 avg words/sent") and hasAnyAncestor(hasTestTag("metric_card_sentences")),
      useUnmergedTree = true
    ).assertIsDisplayed()

    composeTestRule.onNodeWithTag("metric_card_paragraphs").performScrollTo()
    composeTestRule.onNode(
      hasText("1 sent/paragraph") and hasAnyAncestor(hasTestTag("metric_card_paragraphs")),
      useUnmergedTree = true
    ).assertIsDisplayed()
  }

  /**
   * Scenario 4: Top App Bar Actions
   * Tests the visibility and interaction of the TopAppBar copy telemetry button.
   */
  @Test
  fun clickCopyTelemetryButton_existsAndIsClickable() {
    composeTestRule.onNodeWithTag("copy_telemetry_report_button")
      .assertIsDisplayed()
      .performClick()
  }
}