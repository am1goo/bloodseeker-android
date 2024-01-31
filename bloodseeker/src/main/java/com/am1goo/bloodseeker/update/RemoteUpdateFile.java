package com.am1goo.bloodseeker.update;

import androidx.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class RemoteUpdateFile implements RemoteUpdateSerializable {

    public static final String EXTENSION = ".bmx";
    public static final byte[] HEADER_BYTES = new byte[] { 66, 77, 88, 63 };
    public static final short VERSION = 1;
    public static final String CHARSET_NAME = getDefaultCharset();

    private static final byte CYPHER_NONE = 0;
    private static final byte CYPHER_AES = 1;
    private static final byte CYPHER_TYPE = getDefaultCypher();

    private static String getDefaultCharset() {
        return StandardCharsets.US_ASCII.name();
    }

    private static byte getDefaultCypher() {
        return CYPHER_AES;
    }

    private short version;
    private final byte[] secretKey;
    private final List<IRemoteUpdateTrail> trails;

    public RemoteUpdateFile(byte[] secretKey) {
        this.version = VERSION;
        this.secretKey = secretKey;
        this.trails = new ArrayList<>();
    }

    public short getVersion() {
        return version;
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

    public void load(RemoteUpdateReader reader) throws Exception {
        reader.readHeader();
        version = reader.readVersion();
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
                    byte[] trailBytes = payloadReader.readBytes();

                    Class<?> classObj = getClassSafe(className);
                    if (classObj == null) {
                        System.err.println("load: class " + className + " not found, skipped");
                        continue;
                    }

                    Constructor<?> classCctr = classObj.getDeclaredConstructor();
                    boolean accessibleChanged = false;
                    if (!classCctr.isAccessible()) {
                        classCctr.setAccessible(true);
                        accessibleChanged = true;
                    }
                    IRemoteUpdateTrail trail = (IRemoteUpdateTrail) classCctr.newInstance();
                    try (ByteArrayInputStream trailStream = new ByteArrayInputStream(trailBytes)) {
                        try (RemoteUpdateReader trailReader = new RemoteUpdateReader(trailStream)) {
                            trail.load(trailReader);
                            trails.add(trail);
                        }
                    }
                    if (accessibleChanged)
                        classCctr.setAccessible(false);
                }
            }
        }
    }

    public void save(OutputStream outputStream) throws Exception {
        RemoteUpdateWriter writer = new RemoteUpdateWriter(outputStream);
        save(writer);
    }

    public void save(RemoteUpdateWriter writer) throws Exception {
        writer.writeHeader();
        writer.writeVersion(version);
        writer.writeByte(CYPHER_TYPE);

        byte[] payload;
        try (ByteArrayOutputStream payloadStream = new ByteArrayOutputStream()) {
            try (RemoteUpdateWriter payloadWriter = new RemoteUpdateWriter(payloadStream)) {
                payloadWriter.writeInt(trails.size());
                for (int i = 0; i < trails.size(); ++i) {
                    IRemoteUpdateTrail trail = trails.get(i);
                    String className = trail.getClass().getName();
                    payloadWriter.writeString(className, CHARSET_NAME);

                    try (ByteArrayOutputStream trailStream = new ByteArrayOutputStream()) {
                        try (RemoteUpdateWriter trailWriter = new RemoteUpdateWriter(trailStream)) {
                            trail.save(trailWriter);

                            byte[] trailBytes = trailStream.toByteArray();
                            payloadWriter.writeBytes(trailBytes);
                        }
                    }
                }
                payload = payloadStream.toByteArray();
            }
        }

        payload = encode(payload, CYPHER_TYPE);
        writer.writeBytes(payload);
    }

    public byte[] toByteArray() throws Exception {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            save(outputStream);
            return outputStream.toByteArray();
        }
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

        byte[] secretHash = generateHash(secretKey);
        return aesDecrypt(bytes, secretHash);
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

        byte[] secretHash = generateHash(secretKey);
        return aesEncrypt(bytes, secretHash);
    }

    private static byte[] aesEncrypt(byte[] Data, byte[] secretKey)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Key key = generateKey(secretKey);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        return c.doFinal(Data);
    }

    private static byte[] generateHash(byte[] secretKey) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(secretKey);
    }

    private static Key generateKey(byte[] key) throws IllegalArgumentException {
        if (key == null)
            throw new IllegalArgumentException("key is undefined");

        return new SecretKeySpec(key, AES);
    }

    @Nullable
    private static Class<?> getClassSafe(String className) {
        try {
            return Class.forName(className);
        }
        catch (ClassNotFoundException ex) {
            return null;
        }
    }
}
