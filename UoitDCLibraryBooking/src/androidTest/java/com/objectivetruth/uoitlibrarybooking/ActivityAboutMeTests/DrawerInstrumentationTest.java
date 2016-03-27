package com.objectivetruth.uoitlibrarybooking.ActivityAboutMeTests;

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.objectivetruth.uoitlibrarybooking.ActivityAboutMe;
import com.objectivetruth.uoitlibrarybooking.R;
import com.squareup.spoon.Spoon;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.*;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class DrawerInstrumentationTest {
    ActivityAboutMe mActivity;

    @Rule
    public IntentsTestRule<ActivityAboutMe> mActivityRule = new IntentsTestRule<ActivityAboutMe>(
            ActivityAboutMe.class);

    @Before
    public void stubAllExternalIntents() {
        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal()))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, null));
    }

    @Before
    public void setUp() {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void isDrawerNotDisplayedAtStart() {
        onView(ViewMatchers.withId(R.id.left_drawer_aboutme)).check(matches(not((isDisplayed()))));

/*        // test
        intended(allOf(
                hasAction(Intent.ACTION_CALL),
                hasData("tel:0123456789"),
                toPackage("com.android.server.telecom")));*/

    }
/* Normal test code... */

    @Test
    public void isDrawerDisplayedCorrectlyAfterClicking() {
        Spoon.screenshot(mActivity, "before_click");
        onView(withId(R.id.drawer_layout_aboutme)).perform(DrawerActions.open());
        onView(withId(R.id.left_drawer_aboutme)).check(matches((isDisplayed())));
        Spoon.screenshot(mActivity, "after_click");
    }

/*    @Test
    public void DoesCalendarLoad(){
        final Button button = (Button) getA.findViewById(com.company.R.id.open_next_activity);
        Instrumentation.ActivityMonitor activityMonitor = InstrumentationRegistry.getInstrumentation()
                .addMonitor(GuidelinesPoliciesActivity.class.getName(), null, false);
        TouchUtils.clickView(this, );
        onView(withId(R.id.textView1)).check(matches(withText("About")));
    }*/
}