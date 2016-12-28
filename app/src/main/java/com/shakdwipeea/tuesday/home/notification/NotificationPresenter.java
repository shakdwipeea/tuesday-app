package com.shakdwipeea.tuesday.home.notification;

import com.shakdwipeea.tuesday.data.entities.NotificationDetail;
import com.shakdwipeea.tuesday.data.entities.user.GrantedToDetails;
import com.shakdwipeea.tuesday.data.entities.user.Provider;
import com.shakdwipeea.tuesday.data.firebase.FirebaseService;
import com.shakdwipeea.tuesday.data.firebase.UserService;
import com.shakdwipeea.tuesday.util.Util;

import java.util.ArrayList;
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

    private NotificationContract.NotificationView notificationView;
    private CompositeSubscription compositeSubscription;

    private UserService userService;

    public NotificationPresenter(NotificationContract.NotificationView notificationView) {
        this.notificationView = notificationView;
        compositeSubscription = new CompositeSubscription();
    }

    @Override
    public void subscribe() {
        // TODO: 30-11-2016 Check if using getInstance here is wise 🤔
        userService = UserService.getInstance();

        subscribeRequestNotifications();
        getGrantedDetails();
    }

    private void subscribeRequestNotifications() {
        Subscription subscribe = userService.getProvider()
                .doOnNext(this::getRequestNotifications)
                .compose(Util.applySchedulers())
                .subscribe(
                        providers -> {},
                        Throwable::printStackTrace
                );

        compositeSubscription.add(subscribe);
    }

    private void getRequestNotifications(List<Provider> providerList) {
        Subscription subscription = Observable.from(providerList)
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
                        notificationView.addRequestNotification(notificationDetail))
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> notificationView.clearRequestNotification())
                .subscribe(
                        notificationDetail -> {},
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
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

    private void getGrantedDetails() {
        Subscription subscription = userService.getGrantedDetails()
                .doOnNext(this::inflateNotificationDetailForGranted)
                .compose(Util.applySchedulers())
                .subscribe(
                        grantedToDetailses -> {},
                        Throwable::printStackTrace
                );
        compositeSubscription.add(subscription);
    }

    private void inflateNotificationDetailForGranted(ArrayList<GrantedToDetails>
                                                             grantedToDetailsList) {
        Subscription subscription = Observable.from(grantedToDetailsList)
                .flatMap(details -> {
                    FirebaseService firebaseService = new FirebaseService(details.grantedByuid);
                    NotificationDetail notificationDetail = new NotificationDetail();
                    notificationDetail.provider.name = details.providerName;
                    return firebaseService.inflateNotificationUser(notificationDetail);
                })
                .compose(Util.applySchedulers())
                .doOnSubscribe(() -> notificationView.clearGrantedNotification())
                .subscribe(
                        notificationDetail ->
                                notificationView.addGrantedNotification(notificationDetail),
                        throwable -> {
                            notificationView.displayError(throwable.getMessage());
                            throwable.printStackTrace();
                        }
                );
        compositeSubscription.add(subscription);
    }

    @Override
    public void unSubscribe() {
        compositeSubscription.unsubscribe();
    }
}
