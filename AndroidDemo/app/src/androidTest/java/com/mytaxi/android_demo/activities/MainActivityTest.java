package com.mytaxi.android_demo.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.filters.SmallTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.mytaxi.android_demo.R;
import com.mytaxi.android_demo.models.Driver;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

/**
 * Espresso test to login to MyTaxi demo App and perform a search for Drivers name which begin with "sa" and
 * in the AutoComplete choose NOT by position/index BUT by using actual value
 *
 * @SaiPrasad
 */

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private static final String SEARCH = "sa";
    private static final String USERID = "crazydog335";
    private static final String PASSWORD = "venture";
    private static final String DRIVER = "Sarah Scott";
    private static final int SECOND = 0;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    public MainActivityTest() {
        super();
    }

    private static void waitLooperThread(int timeSec) {

        try {
            Thread.sleep(timeSec * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static Matcher<Object> withItemContent(final String itemTextMatcher) {
        checkNotNull(itemTextMatcher);
        return new BoundedMatcher<Object, Driver>(Driver.class) {
            @Override
            public boolean matchesSafely(Driver iconRow) {
                Log.d("mytaxi", iconRow.getName() + " |" + itemTextMatcher);
                return iconRow.getName().toUpperCase().trim().equals(String.valueOf(itemTextMatcher).toUpperCase().trim());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with item content: " + itemTextMatcher);

            }
        };
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @SmallTest
    public void testBlah() {
        assertEquals(1, 1);
    }

    @Test
    public void mainActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        waitLooperThread(3);

        // Check if the App is already logged in, logout in which case.
        /**
         * If the User is already logged in, Just logout and relogin
         */
        try {
            ViewInteraction hamburgerMenu = onView(
                    allOf(withContentDescription("Open navigation drawer"),
                            childAtPosition(
                                    allOf(withId(R.id.toolbar),
                                            childAtPosition(
                                                    withClassName(is("android.support.design.widget.AppBarLayout")),
                                                    0)),
                                    1),
                            isDisplayed()));
            hamburgerMenu.perform(click());

            ViewInteraction logoutBtn = onView(
                    allOf(childAtPosition(
                            allOf(withId(R.id.design_navigation_view),
                                    childAtPosition(
                                            withId(R.id.nav_view),
                                            0)),
                            1),
                            isDisplayed()));
            logoutBtn.perform(click());
        } catch (Exception e) {
            e.printStackTrace();
        }

        waitLooperThread(3);

        /**
         * User is to login/relogin
         */
        onView(withId(R.id.edt_username)).perform(typeText(MainActivityTest.USERID));
        onView(withId(R.id.edt_password)).perform(typeText(MainActivityTest.PASSWORD));
        onView(withId(R.id.btn_login)).perform(click());
        waitLooperThread(3);

        /**
         * Search for sa in the search field
         */
        onView(withId(R.id.textSearch)).perform(typeText(MainActivityTest.SEARCH), closeSoftKeyboard());


        /**
         * Click on the AutoCompleteText without Position number but by text Value
         */
        onData(withItemContent(MainActivityTest.DRIVER))
                .inRoot(RootMatchers.withDecorView(not(is(mActivityTestRule
                        .getActivity().getWindow().getDecorView()))))
                .perform(click());


        /**
         * Check if the User is shown in CardView
         */
        onView(withId(R.id.imageViewDriverAvatar)).check(matches(isDisplayed()));
        waitLooperThread(2);
        /**
         * Make the call
         */
        onView(withId(R.id.fab)).perform(click());
        waitLooperThread(3);

        /**
         * Cancel the call
         */
        try {
            pressBack();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
