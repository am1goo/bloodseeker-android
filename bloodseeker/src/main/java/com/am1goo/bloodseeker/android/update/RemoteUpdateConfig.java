package com.am1goo.bloodseeker.android.update;

public class RemoteUpdateConfig {

    private String url;
    private String secretKey;
    private long cacheTTL;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getCacheTTL() {
        return cacheTTL;
    }

    public void setCacheTTL(long cacheTTL) {
        this.cacheTTL = cacheTTL;
    }
}
