package com.mprusina.killingk

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mprusina.killingk.ui.MainActivity
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityUiTest {

    @get:Rule
    var mainActivityRule: ActivityScenarioRule<MainActivity> =
        ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun givenBottomSheetClosed_whenClickOnToggleMenuButton_thenOpenBottomSheet() {
        onView(withId(R.id.toggle_menu)).perform(click())
        onView(withId(R.id.bottomSheet)).check(matches(isDisplayed()))
    }

    @Test
    fun givenBottomSheetOpen_whenClickOnToggleMenuButton_thenCloseBottomSheet() {
        onView(withId(R.id.toggle_menu)).perform(click())
        onView(withId(R.id.toggle_menu)).perform(click())
        onView(withId(R.id.bottomSheet)).check(matches(isNotFocused()))
    }

    @Test
    fun givenProfileNotCompleted_thenCompleteProfileButtonTextMatches() {
        onView(withId(R.id.toggle_menu)).perform(click())
        onView(withId(R.id.complete_profile_button)).check(matches(withText("Let's get it done")))
    }

    @Test
    fun givenProfileCompleted_thenCompleteProfileButtonTextMatches() {
        onView(withId(R.id.toggle_menu)).perform(click())
        onView(withId(R.id.complete_profile_button)).perform(click())
        onView(isRoot()).perform(waitFor(3000))
        onView(withId(R.id.complete_profile_button)).check(matches(withText("Success!")))
    }

    private fun waitFor(delay: Long): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for $delay milliseconds"
            override fun perform(uiController: UiController, v: View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
}