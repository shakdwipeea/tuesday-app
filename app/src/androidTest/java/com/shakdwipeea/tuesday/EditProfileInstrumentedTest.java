package com.shakdwipeea.tuesday;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.shakdwipeea.tuesday.profile.ProfileActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by akash on 11/2/17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class EditProfileInstrumentedTest {
    private String name;

    @Rule
    public ActivityTestRule<ProfileActivity> activityTestRule = new ActivityTestRule<>(
            ProfileActivity.class, false, false);

    @Before
    public void initString() {
        name = "Akash Shakdwipeea";
    }

    private void launchEditProfileFragment() {
        Intent intent = new Intent();
        intent.putExtra(ProfileActivity.MODE_KEY, ProfileActivity.ProfileActivityMode.EDIT_MODE);
        activityTestRule.launchActivity(intent);
    }

    private void launViewProfileFragment() {
        Intent intent = new Intent();
        intent.putExtra(ProfileActivity.MODE_KEY, ProfileActivity.ProfileActivityMode.VIEW_MODE);
        activityTestRule.launchActivity(intent);
    }

    @Test
    public void changeName() {
//        launchEditProfileFragment();
//        onView(withId(R.id.save_button))
//                .perform(click());
//
//        onView(withId(R.id.name_input))
//                .perform(clearText())
//                .perform(typeText(name), closeSoftKeyboard());
//
//        onView(withId(R.id.action_save_details))
//                .perform(click());
//
//        onView(withId(R.id.name))
//                .check(matches(withText(name)));

    }

}
