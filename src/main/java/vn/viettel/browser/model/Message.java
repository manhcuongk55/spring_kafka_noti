package vn.viettel.browser.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Message {
    private String title;
    private String body;
    private String icon;

    public Message() {
        super();
    }

    public Message(String title, String body, String icon) {
        super();
        this.title = title;
        this.body = body;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public JSONObject toJSON() throws JSONException {

        JSONObject jo = new JSONObject();
        jo.put("title", title);
        jo.put("body", body);
        jo.put("icon", icon);

        return jo;
    }


}
