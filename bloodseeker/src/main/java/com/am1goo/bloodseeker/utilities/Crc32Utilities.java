package com.am1goo.bloodseeker.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Crc32Utilities {

    public static long getCrc32(File file) {
        if (!file.exists())
            return 0;

        try {
            try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                return getCrc32(inputStream);
            }
        } catch (IOException ex) {
            return 0;
        }
    }

    public static long getCrc32(InputStream inputStream) {
        try {
            byte[] bytes = IOUtilities.readAllBytes(inputStream);
            return getCrc32(bytes);
        }
        catch (IOException ex) {
            return 0;
        }
    }

    public static long getCrc32(byte[] bytes) {
        Checksum crc32 = new CRC32();
        crc32.update(bytes, 0, bytes.length);
        return crc32.getValue();
    }
}
