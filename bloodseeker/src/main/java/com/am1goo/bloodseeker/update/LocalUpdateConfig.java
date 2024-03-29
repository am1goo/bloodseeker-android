package com.am1goo.bloodseeker.update;

import com.am1goo.bloodseeker.utilities.IOUtilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocalUpdateConfig {

    private byte[] file;
    private String secretKey;

    public byte[] getFile() {
        return file;
    }

    public void setFile(String path) throws IOException {
        try (InputStream inputStream = new FileInputStream(path)) {
            byte[] bytes = IOUtilities.readAllBytes(inputStream);
            setFile(bytes);
        }
    }

    public void setFile(byte[] bytes) {
        this.file = bytes;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
