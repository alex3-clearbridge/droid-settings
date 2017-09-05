package com.livingspaces.proshopper.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by alexeyredchets on 2017-08-30.
 */

public class MessageResponse {

    @SerializedName("message")
    String message;

    public MessageResponse() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
