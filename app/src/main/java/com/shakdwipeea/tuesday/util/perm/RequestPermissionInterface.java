package com.shakdwipeea.tuesday.util.perm;

/**
 * Created by akash on 21/1/17.
 */

public interface RequestPermissionInterface {

    /**
     * Request permission at run time
     *
     * @param permissions List of permission to be requested
     * @param permIdentifier Identifier used to map the requests
     */
    void requestPermissions(String[] permissions, int permIdentifier);
}
