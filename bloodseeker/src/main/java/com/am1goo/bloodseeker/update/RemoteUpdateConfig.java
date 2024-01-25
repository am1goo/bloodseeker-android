package com.am1goo.bloodseeker.update;

public class RemoteUpdateConfig {

    private String url;
    private String secretKey;
    private long cacheTTL;
    private Keystore keystore;

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

    public Keystore getKeystore() {
        return keystore;
    }

    public void setKeystore(Keystore keystore) {
        this.keystore = keystore;
    }

    public static class Keystore {

        private byte[] cert;
        private String pwd;

        public byte[] getCert() {
            return cert;
        }

        public void setCert(byte[] cert) {
            this.cert = cert;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }
    }
}
