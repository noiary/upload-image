package com.maodq.imguploaddemo.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * IOUtil
 * Created by maodeqiang on 2015/7/13.
 */
public class IOUtil {
    static final String TAG = "IOUtil";

    /**
     * InputStream to byte[]
     */
    public static byte[] readStream(InputStream inStream) {
        byte[] returnByte = null;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream outSteam = null;
        try {
            outSteam = new ByteArrayOutputStream();
            int len = -1;
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            returnByte = outSteam.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(outSteam);
            close(inStream);
        }
        return returnByte;
    }

    public static void close(Closeable... ios) {
        for (Closeable io : ios) {
            try {
                if (io != null)
                    io.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void writeTo(ByteArrayOutputStream baos, File file) {
        try {
            baos.writeTo(new FileOutputStream(file, false));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createNewFile(File file) {
        try {
            boolean newFile = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
