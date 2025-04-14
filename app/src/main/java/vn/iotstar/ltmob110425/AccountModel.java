package vn.iotstar.ltmob110425;

import java.io.Serializable;

public class AccountModel implements Serializable {
    private String email;
    private String pfpUrl;
    private int videoCount;

    public AccountModel() {}

    public AccountModel(String email, String pfpUrl, int videoCount) {
        this.email = email;
        this.pfpUrl = pfpUrl;
        this.videoCount = videoCount;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPfpUrl() {
        return pfpUrl;
    }
    public void setPfpUrl(String pfpUrl) {
        this.pfpUrl = pfpUrl;
    }

    public int getVideoCount() {
        return videoCount;
    }
    public void setVideoCount(int videoCount) {
        this.videoCount = videoCount;
    }
}
