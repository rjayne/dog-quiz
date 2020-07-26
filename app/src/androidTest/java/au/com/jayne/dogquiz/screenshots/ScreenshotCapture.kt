package au.com.jayne.dogquiz.screenshots

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import au.com.jayne.dogquiz.R
import au.com.jayne.dogquiz.espresso.ConditionWatcher
import au.com.jayne.dogquiz.espresso.EspressoTestHelper
import au.com.jayne.dogquiz.espresso.TextDisplayedInstruction
import au.com.jayne.dogquiz.espresso.ViewDisplayedInstruction
import au.com.jayne.dogquiz.feature.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Integration Test: Run this to capture screenshots of each screen and test the screen flow.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ScreenshotCapture {

    @get:Rule
    var activityTestRule = ActivityTestRule<MainActivity>(
        MainActivity::class.java, true, false)

    @Test
    fun testScreenshot_Doggy() {
        val activity = activityTestRule.launchActivity(null)

        ensureGameSelectionScreenDisplayed()

        // ** Screenshot initial Code Entry Screen
        EspressoTestHelper.takeScreenshot(activity, "Game_Selection")

        EspressoTestHelper.clickButton(R.id.doggy)
        ensureGameScreenDisplayed()

        EspressoTestHelper.takeScreenshot(activity, "Doggy_Digest")

        EspressoTestHelper.pressBack()

        ensureGameSelectionScreenDisplayed()

        EspressoTestHelper.clickMenuItem(R.id.navigation_leaderboard)

        ensureLeaderboardScreenDisplayed()

        EspressoTestHelper.takeScreenshot(activity, "Leaderboard")

        EspressoTestHelper.clickMenuItem(R.id.navigation_instructions)

        ensureInstructionScreenDisplayed()

        EspressoTestHelper.takeScreenshot(activity, "Instructions")

        EspressoTestHelper.clickMenuItem(R.id.navigation_settings)

        ensureSettingsScreenDisplayed()

        EspressoTestHelper.takeScreenshot(activity, "Settings")

        EspressoTestHelper.clickMenuItem(R.id.navigation_game_selection)

        ensureGameSelectionScreenDisplayed()
    }

    private fun ensureGameSelectionScreenDisplayed() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        ConditionWatcher.waitForCondition(TextDisplayedInstruction(R.string.game_doggy_name))
        Espresso.onView(ViewMatchers.withText(R.string.game_spaniel_name)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.game_hound_name)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withText(R.string.game_terrier_name)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
    }

    private fun ensureGameScreenDisplayed() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        ConditionWatcher.waitForCondition(ViewDisplayedInstruction(R.id.score))
        Espresso.onView(ViewMatchers.withId(R.id.bone_1)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.bone_2)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.bone_3)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.dog_image)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.button_1)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.button_2)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.button_3)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
        Espresso.onView(ViewMatchers.withId(R.id.button_4)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
    }

    private fun ensureLeaderboardScreenDisplayed() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        ConditionWatcher.waitForCondition(ViewDisplayedInstruction(R.id.label_spaniel_quiz))
        Espresso.onView(ViewMatchers.withText(R.string.game_spaniel_name)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.game_hound_name)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.game_terrier_name)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withText(R.string.game_doggy_name)).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
    }

    private fun ensureInstructionScreenDisplayed() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        ConditionWatcher.waitForCondition(ViewDisplayedInstruction(R.id.instructions_text))
    }

    private fun ensureSettingsScreenDisplayed() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()

        ConditionWatcher.waitForCondition(TextDisplayedInstruction("Reset Scores"))

        Espresso.onView(ViewMatchers.withText("Sounds")).check(ViewAssertions.matches(
            ViewMatchers.isDisplayed()))
    }

}