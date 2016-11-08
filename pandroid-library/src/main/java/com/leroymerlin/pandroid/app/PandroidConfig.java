package com.leroymerlin.pandroid.app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import com.leroymerlin.pandroid.log.PandroidLogger;
import com.leroymerlin.pandroid.security.AESEncryption;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by florian on 13/11/14.
 */
public class PandroidConfig {


    public static boolean DEBUG = false;
    public static String APPLICATION_ID = "";
    public static String BUILD_TYPE = "debug";
    public static String FLAVOR = "";
    public static int VERSION_CODE = 1;
    public static String VERSION_NAME = "1.0";


    /**
     * Configurator activate by Pandroid extension
     * Used to auto add code implementation when the dependencies are detected
     */
    public static List<String> LIBRARIES;


    public static boolean isLibraryEnable(String libraryName) {
        if (LIBRARIES == null) {
            PandroidLogger.getInstance().e("PandroidConfig", "Config not initialize ! PandroidConfigMapperImpl should haved done it. Check your PandroidApplication");
            return false;
        }

        String libName = libraryName.toLowerCase();
        for (String library : LIBRARIES) {
            Pattern pattern = Pattern.compile(".*" + libName + ".*");
            if (pattern.matcher(library).find()) {
                return true;
            }
        }
        return false;


    }

    public static String getSecureField(Context context, String encryptedField) {
        try {
            return AESEncryption.symetricDecrypt(getCertificateSHA1Fingerprint(context), encryptedField);
        } catch (Exception e) {
            PandroidLogger.getInstance().e("PandroidConfig", e);
        }
        return null;
    }

    private static String getCertificateSHA1Fingerprint(Context context) throws Exception {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        packageInfo = pm.getPackageInfo(packageName, flags);
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        c = (X509Certificate) cf.generateCertificate(input);
        String hexString = null;
        MessageDigest md = MessageDigest.getInstance("SHA1");
        byte[] publicKey = md.digest(c.getEncoded());
        hexString = byte2HexFormatted(publicKey);
        return hexString;
    }

    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }
}
