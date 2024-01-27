package com.am1goo.bloodseeker.update;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class RemoteUpdateWriter extends DataOutputStream {

    public RemoteUpdateWriter(OutputStream outputStream) {
        super(outputStream);
    }

    public void writeBytes(byte[] bytes) throws IOException {
        int length = bytes.length;
        writeInt(length);
        write(bytes, 0, bytes.length);
    }

    public void writeHeader() throws IOException {
        write(RemoteUpdateFile.HEADER_BYTES);
    }

    public void writeVersion(short version) throws IOException {
        writeShort(version);
    }

    public void writeString(String str, String charsetName) throws IOException {
        byte[] bytes = str.getBytes(charsetName);
        writeInt(bytes.length);
        write(bytes, 0, bytes.length);
    }

    public void writeStringList(List<String> list, String charsetName) throws IOException {
        writeInt(list.size());
        for (int i = 0; i < list.size(); ++i) {
            writeString(list.get(i), charsetName);
        }
    }

    public void writeStringArray(String[] array, String charsetName) throws IOException {
        writeInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            writeString(array[i], charsetName);
        }
    }

    public void writeArray(Object[] array) throws Exception {
        writeInt(array.length);
        for (int i = 0; i < array.length; ++i) {
            Object classObj = array[i];
            byte[] classBytes;
            if (classObj instanceof RemoteUpdateSerializable) {
                RemoteUpdateSerializable serializable = (RemoteUpdateSerializable)classObj;
                try (ByteArrayOutputStream classStream = new ByteArrayOutputStream()) {
                    try (RemoteUpdateWriter classWriter = new RemoteUpdateWriter(classStream)) {
                        serializable.save(classWriter);
                        classBytes = classStream.toByteArray();
                    }
                }
            }
            else {
                classBytes = new byte[0];
            }
            writeBytes(classBytes);
        }
    }
}
