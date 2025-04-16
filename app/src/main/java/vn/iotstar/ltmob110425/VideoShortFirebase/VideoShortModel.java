package vn.iotstar.ltmob110425.VideoShortFirebase;

import java.io.Serializable;

public class VideoShortModel implements Serializable {
    // same field names as keys in Firebase Realtime Database
    private String uploadId;
    private String title;
    private String desc;
    private String url;
    private String userId;
    private int likeCount;

    public VideoShortModel() {}

    public VideoShortModel(String uploadId, String title, String desc, String videoUrl, String userId, int likeCount) {
        this.uploadId = uploadId;
        this.title = title;
        this.desc = desc;
        this.url = videoUrl;
        this.userId = userId;
        this.likeCount = likeCount;
    }

    public String getUploadId() {
        return uploadId;
    }
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
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

    public int getLikeCount() {
        return likeCount;
    }
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
