package com.shakdwipeea.tuesday.home.notification;

import com.shakdwipeea.tuesday.data.firebase.UserService;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ashak on 30-11-2016.
 */

public class NotificationPresenter implements NotificationContract.Presenter {
    private static final String TAG = "NotificationPresenter";

    private NotificationContract.View notificationView;
    private CompositeSubscription compositeSubscription;

    private UserService userService;

    public NotificationPresenter(NotificationContract.View view) {
        this.notificationView = view;
    }

    @Override
    public void subscribe() {
        compositeSubscription = new CompositeSubscription();
        // TODO: 30-11-2016 Check if using getInstance here is wise 🤔
        userService = UserService.getInstance();

        getNotifications();
    }

    private void getNotifications() {
        Subscription subscribe = userService.getRequestedBy()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        notificationDetail -> notificationView.addNotification(notificationDetail),
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscribe);
    }

    @Override
    public void unSubscribe() {
        compositeSubscription.unsubscribe();
    }
}
