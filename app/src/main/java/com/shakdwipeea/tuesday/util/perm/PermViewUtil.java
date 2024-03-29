package com.shakdwipeea.tuesday.util.perm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.shakdwipeea.tuesday.util.Util;

import java.util.Random;

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
     * @param permissions Permission to request
     * @param permissionInterface Interface that defines how permission is requested.
     *                            Don't get tempted and pass a lambda.
     * @param actionInterface Contains the action to perform
     */
    public void performActionWithPermissions(Context context,
                                             String permissionToCheck,
                                             String[] permissions,
                                             RequestPermissionInterface permissionInterface,
                                             ActionInterface actionInterface) {
        int reqCode = new Random().nextInt(100);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.d(TAG, "performActionWithPermissions: Performing action directly, api less than 23");
            actionInterface.performAction();
        } else {
            if (ContextCompat.checkSelfPermission(context,
                    permissionToCheck) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "performActionWithPermissions: will be performed later with req code "
                        + reqCode);
                pendingActionMap.append(reqCode, actionInterface);
                permissionInterface.requestPermissions(permissions, reqCode);
            } else {
                Log.d(TAG, "performActionWithPermissions: Performing now");
                actionInterface.performAction();
            }
        }
    }

    /**
     * This method should be called when the activity/fragment has received the request permission
     *
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    public void onPermissionResult(int requestCode, @NonNull String[] permissions,
                                   @NonNull int[] grantResults) {
        Log.d(TAG, "onPermissionResult: Forwarded req code is " + requestCode);

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
