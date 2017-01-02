package com.shakdwipeea.tuesday.data.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AccountService extends Service {
    public AccountService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        TuesdayAuthenticator authenticator = new TuesdayAuthenticator(this);
        return authenticator.getIBinder();
    }
}
