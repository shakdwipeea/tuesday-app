package com.shakdwipeea.tuesday.home.notification;

import com.shakdwipeea.tuesday.data.entities.NotificationDetail;
import com.shakdwipeea.tuesday.data.entities.Provider;
import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.util.Util;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
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

        subscribeNotifications();
    }

    private void subscribeNotifications() {
        Subscription subscribe = userService.getProvider()
                .doOnNext(this::getNotifications)
                .compose(Util.applySchedulers())
                .subscribe();

        compositeSubscription.add(subscribe);
    }

    private void getNotifications(List<Provider> providerList) {
        Observable.from(providerList)
                .filter(provider ->
                        provider.providerDetails.requestedBy != null
                                && provider.providerDetails.requestedBy.size() > 0)
                .lift(getNotificationDetailOperator())
                .flatMap(notificationDetail -> {
                    FirebaseService firebaseService =
                            new FirebaseService(notificationDetail.user.uid);
                    return firebaseService.inflateNotificationUser(notificationDetail);
                })
                .doOnNext(notificationDetail ->
                        notificationView.addNotification(notificationDetail))
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> notificationView.clearNotification())
                .subscribe();
    }


    /**
     * It combines the requested uid list with the provider and returns it
     */
    public Observable.Operator<NotificationDetail, Provider> getNotificationDetailOperator() {
        return subscriber -> new Subscriber<Provider>() {
            @Override
            public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                }
            }

            @Override
            public void onNext(Provider provider) {
                if (!subscriber.isUnsubscribed()) {
                    for (String uid : provider.getProviderDetails().requestedBy) {
                        NotificationDetail detail = new NotificationDetail();
                        detail.user.uid = uid;
                        detail.provider = provider;
                        subscriber.onNext(detail);
                    }
                }
            }
        };
    }

    @Override
    public void unSubscribe() {
        compositeSubscription.unsubscribe();
    }
}
