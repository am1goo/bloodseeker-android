package com.am1goo.bloodseeker.utilities;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class Crc32UtilitiesTest {

    @Test
    public void testChecksumOfRandomBytes() {
        byte[] randomBytes = getRandomBytes(89);

        long checksum1 = Crc32Utilities.getCrc32(randomBytes);
        long checksum2 = Crc32Utilities.getCrc32(randomBytes);
        System.out.println("checksum1=" + checksum1 + ", checksum2=" + checksum2);
        Assert.assertEquals(checksum1, checksum2);
    }

    private byte[] getRandomBytes(int length) {
        Random random = new Random();
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; ++i) {
            byte b = (byte)random.nextInt(Byte.MAX_VALUE);
            bytes[i] = b;
        }
        return bytes;
    }
}
