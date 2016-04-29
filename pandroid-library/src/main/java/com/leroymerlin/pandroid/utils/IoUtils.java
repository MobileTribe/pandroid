package com.leroymerlin.pandroid.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by adrienleroy on 28/05/14.
 */
public class IoUtils {

    /**
     * Copie un fichier depuis l'asset folder dans le repertoire data de l'application. Utile quand une librairie externe ne peut pas lire
     * directement dans le fichier en utilisant l'asset manager
     *
     * @param context
     * @param sourceFileName
     * @param destFileName
     * @return
     */
    public static boolean copyFileFromAsset(Context context, String sourceFileName, String destFileName) throws IOException {
        File destFile = new File(destFileName);
        return copyFileFromAsset(context, sourceFileName, destFile);
    }

    /**
     * Copie un fichier depuis l'asset folder dans le repertoire data de l'application. Utile quand une librairie externe ne peut pas lire
     * directement dans le fichier en utilisant l'asset manager
     *
     * @param context
     * @param sourceFileName
     * @param destFile
     * @return
     */
    public static boolean copyFileFromAsset(Context context, String sourceFileName, File destFile) throws IOException {

        File destParentDir = destFile.getParentFile();
        if (!destParentDir.exists()) {
            if (!destParentDir.mkdir()) {
                return false;
            }
        }

        InputStream in = null;
        OutputStream out = null;
        try {
            AssetManager assetManager = context.getAssets();
            in = assetManager.open(sourceFileName);
            out = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.flush();
                out.close();
                out = null;
            }
        }
        return true;
    }

    public static void copy(File src, File dst) throws IOException {
        FileInputStream in = new FileInputStream(src);
        FileOutputStream out = new FileOutputStream(dst);

        try {
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } finally {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.flush();
                out.close();
                out = null;
            }
        }
    }

    public static boolean assetsFileExists(Context context, String filePath) {

        try {
            InputStream inputStream = context.getAssets().open(filePath);
            inputStream.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static String readFileFromAssets(Context context, String filePath) throws IOException {

        StringBuilder builder = new StringBuilder();

        InputStream inputStream = context.getAssets().open(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String str;

        while ((str = reader.readLine()) != null) {
            builder.append(str);
        }

        reader.close();

        return builder.toString();
    }

    public static byte[] readFileBytesFromAssets(Context context, String filePath) throws IOException {
        InputStream is = context.getAssets().open(filePath);
        byte[] fileBytes = new byte[is.available()];
        is.read(fileBytes);
        is.close();
        return fileBytes;
    }
}
