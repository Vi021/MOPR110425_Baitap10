package vn.iotstar.ltmob110425;

import java.io.Serializable;

public class VideoShortModel implements Serializable {
    // same field names as keys in Firebase Realtime Database
    private String title;
    private String desc;
    private String url;

    public VideoShortModel() { }

    public VideoShortModel(String title, String desc, String videoUrl) {
        this.title = title;
        this.desc = desc;
        this.url = videoUrl;
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
}
