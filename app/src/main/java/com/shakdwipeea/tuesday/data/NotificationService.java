package com.shakdwipeea.tuesday.data;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.shakdwipeea.tuesday.R;
import com.shakdwipeea.tuesday.data.entities.NotificationDetail;
import com.shakdwipeea.tuesday.home.HomeActivity;
import com.shakdwipeea.tuesday.home.notification.NotificationContract;
import com.shakdwipeea.tuesday.home.notification.NotificationPresenter;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class NotificationService extends IntentService
        implements NotificationContract.NotificationView {
    private static final String TAG = "NotificationService";

    private int mId = 12;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationPresenter presenter = new NotificationPresenter(this);
        presenter.subscribe();
    }

    private void sendNotification(String title, String text) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, HomeActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(HomeActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }

    @Override
    public void displayError(String reason) {
        Log.e(TAG, "displayError: " + reason);
    }

    @Override
    public void displayProgressBar(boolean enable) {

    }

    @Override
    public void addRequestNotification(NotificationDetail notificationDetail) {
        sendNotification(notificationDetail.user.name,
                " is requesting for " + notificationDetail.provider.name + " information");
    }

    @Override
    public void clearRequestNotification() {

    }

    @Override
    public void addGrantedNotification(NotificationDetail notificationDetail) {
        //because granted info remain there so this will trigger an infnite loop pf notifications
//         sendNotification(notificationDetail.user.name,
//               "granted " + notificationDetail.provider.name + " information");
    }

    @Override
    public void clearGrantedNotification() {

    }
}
