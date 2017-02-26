package com.shakdwipeea.tuesday;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.shakdwipeea.tuesday.auth.AuthActivity;
import com.shakdwipeea.tuesday.data.Preferences;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int white = ContextCompat.getColor(this, android.R.color.white);
        int black = ContextCompat.getColor(this, android.R.color.black);
        int gray = ContextCompat.getColor(this, R.color.tw__light_gray);

        // Create your first page
        addSlide(AppIntro2Fragment.newInstance("Too many IDs to share?",
                "Fill in all account IDs in your Tuesday's profile & and just share your registered " +
                        "phone number to your friends", R.drawable.sharee, white, black, black));

        addSlide(AppIntro2Fragment.newInstance("Changing your number?",
                "Just update your new number in Tuesday's profile & all your friend's phonebook " +
                        "will be updated automatically", R.drawable.updartee, white, black, black));

        addSlide(AppIntro2Fragment.newInstance("Transfer contacts to new phone?",
                "Just install TUESDAY in new phone & TA-DA all your contacts will appear in an " +
                        "instant in your new phone", R.drawable.transfere, white, black, black));


        // Hide Skip/Done button.
        showSkipButton(false);
        setProgressButtonEnabled(true);

        setColorDoneText(black);

        setDoneText("Start Tuesday");

        setIndicatorColor(black, gray);
        setNextArrowColor(black);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }

        setDepthAnimation();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Preferences.getInstance(this).setOnboardingDone(true);

        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }

}
