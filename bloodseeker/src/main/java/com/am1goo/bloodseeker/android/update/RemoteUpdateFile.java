package com.am1goo.bloodseeker.android.update;

import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class RemoteUpdateFile {

    public static final byte[] HEADER_BYTES = new byte[] { 66, 77, 88, 63 };
    public static final short VERSION = 1;
    public static final String CHARSET_NAME = getDefaultCharset();

    private static final byte CYPHER_NONE = 0;
    private static final byte CYPHER_AES = 1;
    private static final byte CYPHER_TYPE = getDefaultCypher();

    private static String getDefaultCharset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return StandardCharsets.US_ASCII.name();
        }
        else {
            return "ascii";
        }
    }

    private static byte getDefaultCypher() {
        return CYPHER_AES;
    }

    private final byte[] secretKey;
    private final List<IRemoteUpdateTrail> trails;

    public RemoteUpdateFile(byte[] secretKey) {
        this.secretKey = secretKey;
        this.trails = new ArrayList<>();
    }

    public List<IRemoteUpdateTrail> getTrails() {
        return trails;
    }

    public void setTrails(List<IRemoteUpdateTrail> trails) {
        this.trails.clear();
        this.trails.addAll(trails);
    }

    public void load(InputStream inputStream) throws Exception {
        RemoteUpdateReader reader = new RemoteUpdateReader(inputStream);
        load(reader);
    }

    private void load(RemoteUpdateReader reader) throws Exception {
        reader.readHeader();
        int version = reader.readVersion();
        byte cypher = reader.readByte();

        int payloadLength = reader.readInt();
        byte[] payload = reader.readBytes(payloadLength);
        payload = decode(payload, cypher);

        trails.clear();
        try (InputStream payloadStream = new ByteArrayInputStream(payload)) {
            try (RemoteUpdateReader payloadReader = new RemoteUpdateReader(payloadStream)) {
                int trailsCount = payloadReader.readInt();
                for (int i = 0; i < trailsCount; ++i) {
                    String className = payloadReader.readString();
                    Class<?> classObj = Class.forName(className);
                    IRemoteUpdateTrail trail = (IRemoteUpdateTrail)classObj.newInstance();
                    trail.load(payloadReader);
                    trails.add(trail);
                }
            }
        }
    }

    public void save(OutputStream outputStream) throws Exception {
        RemoteUpdateWriter writer = new RemoteUpdateWriter(outputStream);
        save(writer);
    }

    private void save(RemoteUpdateWriter writer) throws Exception {
        writer.writeHeader();
        writer.writeVersion();
        writer.writeByte(CYPHER_TYPE);

        byte[] payload;
        try (ByteArrayOutputStream payloadStream = new ByteArrayOutputStream()) {
            try (RemoteUpdateWriter payloadWriter = new RemoteUpdateWriter(payloadStream)) {
                payloadWriter.writeInt(trails.size());
                for (int i = 0; i < trails.size(); ++i) {
                    IRemoteUpdateTrail trail = trails.get(i);
                    String className = trail.getClass().getName();
                    payloadWriter.writeString(className, CHARSET_NAME);
                    trail.save(payloadWriter);
                }
                payload = payloadStream.toByteArray();
            }
        }

        payload = encode(payload, CYPHER_TYPE);
        writer.writeBytes(payload);
    }

    private byte[] decode(byte[] bytes, byte cipher) throws Exception {
        switch (cipher) {
            case CYPHER_NONE:
                return bytes;
            case CYPHER_AES:
                return aesDecrypt(bytes);
            default:
                throw new InvalidParameterException("unsupported cipher " + cipher + " type");
        }
    }

    private byte[] encode(byte[] bytes, byte cipher) throws Exception {
        switch (cipher) {
            case CYPHER_NONE:
                return bytes;
            case CYPHER_AES:
                return aesEncrypt(bytes);
            default:
                throw new InvalidParameterException("unsupported cipher " + cipher + " type");
        }
    }

    private static final String AES = "AES";

    private byte[] aesDecrypt(byte[] bytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        return aesDecrypt(bytes, secretKey);
    }

    private static byte[] aesDecrypt(byte[] encryptedData, byte[] secretKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Key key = generateKey(secretKey);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        return c.doFinal(encryptedData);
    }

    private byte[] aesEncrypt(byte[] bytes)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        return aesEncrypt(bytes, secretKey);
    }

    private static byte[] aesEncrypt(byte[] Data, byte[] secretKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Key key = generateKey(secretKey);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(Data);
    }

    private static Key generateKey(byte[] secretKey) throws IllegalArgumentException {
        if (secretKey == null)
            throw new IllegalArgumentException("secretKey is undefined");

        return new SecretKeySpec(secretKey, AES);
    }
}
