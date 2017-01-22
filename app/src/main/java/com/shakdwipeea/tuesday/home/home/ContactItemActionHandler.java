package com.shakdwipeea.tuesday.home.home;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import com.shakdwipeea.tuesday.data.entities.user.User;
import com.shakdwipeea.tuesday.profile.ProfileActivity;
import com.shakdwipeea.tuesday.profile.view.ProfileViewFragment;

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

        if (!TextUtils.isEmpty(user.uid)) {
            Intent intent = new Intent(context, ProfileActivity.class);
            intent.putExtra(ProfileViewFragment.USER_EXTRA_KEY, Parcels.wrap(User.class, user));
            context.startActivity(intent);
        } else {
            Snackbar.make(view, "Not available on tuesday", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }
}
