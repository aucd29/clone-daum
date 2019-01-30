package com.example.clone_daum


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    @JvmField
    var mGrantPermissionRule =
        GrantPermissionRule.grant(
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.RECORD_AUDIO",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )

    @Test
    fun mainActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(100)

        val tabView = onView(
            allOf(
                withContentDescription("랭킹"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tab),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val appCompatTextView = onView(
            allOf(
                withId(R.id.search),
                childAtPosition(
                    allOf(
                        withId(R.id.search_area),
                        childAtPosition(
                            withId(R.id.toolbar_layout),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatTextView.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.search_edit),
                childAtPosition(
                    allOf(
                        withId(R.id.search_container),
                        childAtPosition(
                            withId(R.id.container),
                            1
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("이지금"), closeSoftKeyboard())

        val constraintLayout = onView(
            allOf(
                withId(R.id.suggest_item),
                childAtPosition(
                    allOf(
                        withId(R.id.search_recycler),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        constraintLayout.perform(click())

        val constraintLayout2 = onView(
            allOf(
                withId(R.id.suggest_item),
                childAtPosition(
                    allOf(
                        withId(R.id.search_recycler),
                        childAtPosition(
                            withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        constraintLayout2.perform(click())

        pressBack()

        val appCompatTextView2 = onView(
            allOf(
                withId(R.id.realtime_issue),
                childAtPosition(
                    allOf(
                        withId(R.id.search_area),
                        childAtPosition(
                            withId(R.id.toolbar_layout),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatTextView2.perform(click())

        pressBack()

        val appCompatTextView3 = onView(
            allOf(
                withId(R.id.realtime_issue),
                childAtPosition(
                    allOf(
                        withId(R.id.search_area),
                        childAtPosition(
                            withId(R.id.toolbar_layout),
                            0
                        )
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatTextView3.perform(click())

        pressBack()

        val appCompatImageView = onView(
            allOf(
                withId(R.id.realtime_issue_extend),
                childAtPosition(
                    allOf(
                        withId(R.id.search_area),
                        childAtPosition(
                            withId(R.id.toolbar_layout),
                            0
                        )
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())

        val tabView2 = onView(
            allOf(
                withContentDescription("뉴스"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.realtime_issue_tab),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        tabView2.perform(click())

        val tabView3 = onView(
            allOf(
                withContentDescription("연예"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.realtime_issue_tab),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        tabView3.perform(click())

        val tabView4 = onView(
            allOf(
                withContentDescription("스포츠"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.realtime_issue_tab),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        tabView4.perform(click())

        val appCompatImageView2 = onView(
            allOf(
                withId(R.id.realtime_issue_tab_menu),
                childAtPosition(
                    allOf(
                        withId(R.id.realtime_issue_container),
                        childAtPosition(
                            withId(R.id.design_bottom_sheet),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatImageView2.perform(click())

        val constraintLayout3 = onView(
            allOf(
                withId(R.id.weather_layout),
                childAtPosition(
                    allOf(
                        withId(R.id.search_area),
                        childAtPosition(
                            withId(R.id.toolbar_layout),
                            0
                        )
                    ),
                    7
                ),
                isDisplayed()
            )
        )
        constraintLayout3.perform(click())

        val constraintLayout4 = onView(
            allOf(
                withId(R.id.weather_refresh_area),
                childAtPosition(
                    allOf(
                        withId(R.id.weather_area),
                        childAtPosition(
                            withId(R.id.weather_container),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        constraintLayout4.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val view = onView(
            allOf(
                withId(R.id.touch_outside),
                childAtPosition(
                    allOf(
                        withId(R.id.coordinator),
                        childAtPosition(
                            withId(R.id.container),
                            0
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        view.perform(click())

        val appCompatImageView3 = onView(
            allOf(
                withId(R.id.search_extend_menu),
                childAtPosition(
                    allOf(
                        withId(R.id.search_area),
                        childAtPosition(
                            withId(R.id.toolbar_layout),
                            0
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatImageView3.perform(click())

        val constraintLayout5 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.media_search_button_layout),
                        childAtPosition(
                            withId(R.id.media_search_extend_menu_container),
                            0
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        constraintLayout5.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val appCompatButton = onView(
            allOf(
                withId(android.R.id.button1), withText("확인"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.buttonPanel),
                        0
                    ),
                    3
                )
            )
        )
        appCompatButton.perform(scrollTo(), click())

        pressBack()

        val appCompatImageView4 = onView(
            allOf(
                withId(R.id.tab_menu),
                childAtPosition(
                    allOf(
                        withId(R.id.tab_layout),
                        childAtPosition(
                            withId(R.id.search_bar),
                            1
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatImageView4.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(50)

        val constraintLayout6 = onView(
            allOf(
                childAtPosition(
                    allOf(
                        withId(R.id.navi_bottom),
                        childAtPosition(
                            withId(R.id.navi_layout),
                            10
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        constraintLayout6.perform(click())

        pressBack()
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
