package com.shakdwipeea.tuesday;

import android.content.Intent;
import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.shakdwipeea.tuesday.auth.AuthActivity;
import com.shakdwipeea.tuesday.data.Preferences;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AhoyOnboarderActivity {
    List<AhoyOnboarderCard> onboarderPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboarderPages = new ArrayList<>();

        // Create your first page
        AhoyOnboarderCard AhoyOnboarderCard1 = new AhoyOnboarderCard("Title 1", "Description 1", R.drawable.transfere);
        AhoyOnboarderCard AhoyOnboarderCard2 = new AhoyOnboarderCard("Go back", "Go ahead", R.drawable.updartee);
        AhoyOnboarderCard AhoyOnboarderCard3 = new AhoyOnboarderCard("Go back", "Go ahead", R.drawable.sharee);

        // You can define title and description colors (by default white)
        AhoyOnboarderCard1.setTitleColor(R.color.black);
        AhoyOnboarderCard1.setDescriptionColor(R.color.black);

        AhoyOnboarderCard2.setTitleColor(R.color.black);
        AhoyOnboarderCard2.setDescriptionColor(R.color.black);

        AhoyOnboarderCard3.setTitleColor(R.color.black);
        AhoyOnboarderCard3.setDescriptionColor(R.color.black);

        // Don't forget to set background color for your page
        AhoyOnboarderCard1.setBackgroundColor(R.color.white);
        AhoyOnboarderCard2.setBackgroundColor(R.color.white);
        AhoyOnboarderCard3.setBackgroundColor(R.color.white);

        // Add your pages to the list
        onboarderPages.add(AhoyOnboarderCard1);
        onboarderPages.add(AhoyOnboarderCard2);
        onboarderPages.add(AhoyOnboarderCard3);

        // And pass your pages to 'setOnboardPagesReady' method
        setOnboardPages(onboarderPages);
        setGradientBackground();

    }

    @Override
    public void onFinishButtonPressed() {
        Preferences.getInstance(this).setOnboardingDone(true);

        Intent intent = new Intent(this, AuthActivity.class);
        startActivity(intent);
        finish();
    }
}
