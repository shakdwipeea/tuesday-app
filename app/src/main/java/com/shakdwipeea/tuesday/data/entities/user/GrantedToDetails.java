package com.shakdwipeea.tuesday.data.entities.user;

/**
 * Created by ashak on 10-12-2016.
 */

public class GrantedToDetails {
    public String grantedByuid;
    public String providerName;

    // only for call and work
    // this is not save in firebase, it is going to be constructed at runtime
    public String providerDetailType;
}
