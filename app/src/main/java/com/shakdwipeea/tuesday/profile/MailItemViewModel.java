package com.shakdwipeea.tuesday.profile;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * Created by akash on 22/1/17.
 */

public class MailItemViewModel {
    public void emailPerson(View view, String email) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email));
        view.getContext().startActivity(intent);
    }
}
