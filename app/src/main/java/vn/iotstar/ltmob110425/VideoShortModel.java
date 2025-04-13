package vn.iotstar.ltmob110425;

import java.io.Serializable;

public class VideoShortModel implements Serializable {
    // same field names as keys in Firebase Realtime Database
    private String title;
    private String desc;
    private String url;
    private String userId;

    public VideoShortModel() { }

    public VideoShortModel(String title, String desc, String videoUrl, String userId) {
        this.title = title;
        this.desc = desc;
        this.url = videoUrl;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
