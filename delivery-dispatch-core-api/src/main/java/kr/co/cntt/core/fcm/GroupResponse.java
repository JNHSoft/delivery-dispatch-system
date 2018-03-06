package kr.co.cntt.core.fcm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupResponse {


    private String notification_key;

    public String getNotification_key() {
        return notification_key;
    }

    public void setNotification_key(String notification_key) {
        this.notification_key = notification_key;
    }

    @Override
    public String toString() {
        return "GroupResponse{" +
                "notification_key=" + notification_key +
                '}';
    }
}


