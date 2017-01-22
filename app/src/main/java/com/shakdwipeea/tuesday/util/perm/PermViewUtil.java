package com.shakdwipeea.tuesday.util.perm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.shakdwipeea.tuesday.util.Util;

import java.util.HashMap;

/**
 * Created by ashak on 18-11-2016.
 */

public class PermViewUtil {
    private static final String TAG = "PermViewUtil";

    private static final int REQUEST_WRITE_CONTACTS = 123;
    private static final int REQUEST_CALL = 456;

    // Contains all the actions for which permission is being requested
    private SparseArray<ActionInterface> pendingActionMap = new SparseArray<>();

    private View rootView;

    public PermViewUtil(View rootView) {
        this.rootView = rootView;
    }

    /**
     * Perform any action which requires permission at run time
     *
     * @param context Context
     * @param permissionToCheck Permission to check before action
     * @param fragment Fragment
     * @param actionInterface Contains the action to perform
     */
    public void performActionWithPermissions(Context context,
                                             String permissionToCheck,
                                             String[] permissions,
                                             RequestPermissionInterface permissionInterface,
                                             ActionInterface actionInterface) {
        if (ContextCompat.checkSelfPermission(context,
                permissionToCheck) != PackageManager.PERMISSION_GRANTED) {
            pendingActionMap.append(REQUEST_CALL, actionInterface);
            permissionInterface.requestPermission(permissions, REQUEST_CALL);
        } else {
            actionInterface.performAction();
        }
    }

    /**
     * This method should be called when the activity/fragment has received the request permission
     *
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    public void onPermissionResult(int requestCode, @NonNull String[] permissions,
                                   @NonNull int[] grantResults) {
        ActionInterface actionInterface = pendingActionMap.get(requestCode);

        if (actionInterface != null && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            actionInterface.performAction();
        } else {
            Log.e(TAG, "onPermissionResult: Failed for reqCode" + requestCode);
            Util.displaySnack(rootView, "We don't have permission to do so.");
        }
    }

}
