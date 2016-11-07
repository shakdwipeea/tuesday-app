package com.shakdwipeea.tuesday.home;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.shakdwipeea.tuesday.data.entities.User;
import com.shakdwipeea.tuesday.profile.ProfileActivity;

import org.parceler.Parcels;

/**
 * Created by ashak on 06-11-2016.
 */

public class ContactItemActionHandler {

    /**
     * Called by the data binding library when clicked on a search item
     * @param user User whose profile is to be loaded
     */
    public void openProfile(View view, User user) {
        Context context = view.getContext();

//        Bundle bundle = new Bundle();
//        bundle.putParcelable(ProfileActivity.USER_EXTRA_KEY, Parcels.wrap(user));

        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.USER_EXTRA_KEY, Parcels.wrap(User.class, user));
        context.startActivity(intent);
    }
}
