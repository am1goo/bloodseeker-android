package com.am1goo.bloodseeker.update;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoteUpdateReader extends DataInputStream {

    RemoteUpdateReader(InputStream inputStream) {
        super(inputStream);
    }

    public byte[] readBytes() throws IOException {
        int length = readInt();
        return readBytes(length);
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        int num = read(bytes);
        if (num != length)
            throw new IOException("wrong byte array size, actual " + num + ", expected " + length);

        return bytes;
    }

    public void readHeader() throws IOException {
        byte[] header = readBytes(RemoteUpdateFile.HEADER_BYTES.length);
        if (!Arrays.equals(header, RemoteUpdateFile.HEADER_BYTES))
            throw new IOException("wrong header");
    }

    public short readVersion() throws IOException {
        return readShort();
    }

    public String readString() throws IOException {
        int bytesCount = readInt();
        byte[] bytes = new byte[bytesCount];
        int bytesRead = read(bytes, 0, bytesCount);
        if (bytesRead != bytesCount)
            throw new IOException("wrong string bytes, actual " + bytesRead + ", expected " + bytesCount);

        return new String(bytes);
    }

    public List<String> readStringList() throws IOException {
        List<String> list = new ArrayList<>();
        int listSize = readInt();
        for (int i = 0; i < listSize; ++i) {
            String str = readString();
            list.add(str);
        }
        return list;
    }

    public String[] readStringArray() throws IOException {
        int arrayLength = readInt();
        String[] array = new String[arrayLength];
        for (int i = 0; i < arrayLength; ++i) {
            array[i] = readString();
        }
        return array;
    }
}
