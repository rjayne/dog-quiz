package au.com.jayne.dogquiz.espresso

import android.content.Context
import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.screenshot.Screenshot
import au.com.jayne.dogquiz.screenshots.ScreenCaptureProcessor
import junit.framework.AssertionFailedError
import org.hamcrest.Matcher


object EspressoTestHelper {

    fun enterText(editTextId: Int, text: String) {
        onView(withId(editTextId)).perform(ViewActions.typeText(text))
    }

    fun isViewEnabled(viewMatcher: Matcher<View>): Boolean {
        try {
            onView(
                viewMatcher
            ).check(ViewAssertions.matches(isEnabled()))
            // View is displayed
            return true
        } catch (e: AssertionFailedError) { // View not displayed
        } catch (ex: Exception) {
            // View not displayed
        } catch(ex: NoMatchingViewException) {
            // View not displayed
        }
        return false
    }

    fun clickButton(buttonId: Int) {
        onView(withId(buttonId)).perform(ViewActions.click())
    }

    fun clickMenuItem(menuItemId: Int) {
        onView(withId(menuItemId)).perform(ViewActions.click())
    }

    fun takeScreenshot(context: Context, screenShotName: String) {
        val processors = setOf(ScreenCaptureProcessor(context))

        val screenCapture = Screenshot.capture()
        screenCapture.apply {
            name = screenShotName
            process(processors)
        }
    }

    fun pressBack() {
        Espresso.pressBack()
//        onView(isRoot()).perform(ViewActions.pressBack())
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    fun isViewDisplayed(viewMatcher: Matcher<View>): Boolean {
        try {
            onView(viewMatcher).check(ViewAssertions.matches(isDisplayed()))
            // View is displayed
            return true
        } catch (e: AssertionFailedError) { // View not displayed
        } catch (ex: Exception) {
            // View not displayed
        } catch(ex: NoMatchingViewException) {
            // View not displayed
        }
        return false
    }

}