package com.shakdwipeea.tuesday.profile.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.shakdwipeea.tuesday.auth.otp.OtpContract;
import com.shakdwipeea.tuesday.data.entities.user.ProviderDetails;
import com.shakdwipeea.tuesday.databinding.CallItemBinding;
import com.shakdwipeea.tuesday.util.adapter.ItemViewModel;
import com.shakdwipeea.tuesday.util.perm.PermViewUtil;
import com.shakdwipeea.tuesday.util.perm.RequestPermissionInterface;

/**
 * Created by akash on 21/1/17.
 */

public class CallItemViewModel implements ItemViewModel<CallItemBinding> {
    PermViewUtil permViewUtil;
    RequestPermissionInterface requestPermissionInterface;

    public CallItemViewModel(PermViewUtil permViewUtil,
                             RequestPermissionInterface requestPermissionInterface) {
        this.permViewUtil = permViewUtil;
        this.requestPermissionInterface = requestPermissionInterface;
    }

    public void callPerson(View view, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
        permViewUtil.performActionWithPermissions(
                view.getContext(),
                Manifest.permission.CALL_PHONE,
                new String[]{Manifest.permission.CALL_PHONE},
                requestPermissionInterface,
                () -> { view.getContext().startActivity(intent); }
        );
    }

    public void messagePerson(View view, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        view.getContext().startActivity(intent);
    }

    @Override
    public void bindDetail(CallItemBinding binding) {
        // Any required binding can be done here
    }
}
