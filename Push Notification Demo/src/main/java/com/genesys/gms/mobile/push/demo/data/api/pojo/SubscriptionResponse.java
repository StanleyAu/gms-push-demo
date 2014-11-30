package com.genesys.gms.mobile.push.demo.data.api.pojo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by stau on 30/11/2014.
 */
public class SubscriptionResponse {
    @SerializedName("id") private String _id;

    public SubscriptionResponse(String _id) {
        this._id = _id;
    }

    public String getId() {
        return _id;
    }

    @Override public String toString() {
        return getClass().getName() + "@" + hashCode() +
                "[" +
                "id=" + _id +
                "]";
    }
}
