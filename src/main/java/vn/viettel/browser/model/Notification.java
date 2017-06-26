package vn.viettel.browser.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Notification {
    private String to;
    private Message notification;

    public Notification(String to, Message notification) {
        super();
        this.to = to;
        this.notification = notification;
    }

    public Notification() {
        super();
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Message getNotification() {
        return notification;
    }

    public void setNotification(Message notification) {
        this.notification = notification;
    }


    public JSONObject toJSON() throws JSONException {

        JSONObject jo = new JSONObject();
        jo.put("to", to);
        jo.put("notification", notification.toJSON());

        return jo;
    }


}
