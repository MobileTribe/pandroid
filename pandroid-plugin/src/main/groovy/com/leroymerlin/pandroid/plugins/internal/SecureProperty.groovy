package com.leroymerlin.pandroid.plugins.internal

import com.leroymerlin.pandroid.security.AESEncryption
import org.gradle.api.Project

import java.security.KeyStore
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateEncodingException

/**
 * Created by florian on 18/02/16.
 */
class SecureProperty {
    String name;

    Map<String, String> props = new HashMap<>();

    public SecureProperty(String name) {
        this.name = name;
    }

    public void secureField(String key, String value) {
        props.put(key, value);
    }

    public void apply(Project project) {
        /*def hasAppPlugin = project.plugins.hasPlugin("com.android.application")
        if (hasAppPlugin) {*/

        /*project.android.buildTypes.each {
            buildTypes ->
                applyBuildConfigField(project, buildTypes)
        }*/

        project.android.applicationVariants.all {
            variant ->

                String name = variant.name
                if(variant.flavorName.equals(this.name)){
                    applyBuildConfigField(project, variant);
                }
        }

    }

    // variant.variantData.variantConfiguration.signingConfig

    private void applyBuildConfigField(Project project, def variant) {
        def signingConfig = variant.buildType.signingConfig;

        if (signingConfig == null) {
            return;
        }
        KeyStore keyStore = KeyStore.getInstance(signingConfig.getStoreType() != null ?
                signingConfig.getStoreType() : KeyStore.getDefaultType());
        FileInputStream fis = new FileInputStream(signingConfig.getStoreFile());
        keyStore.load(fis, signingConfig.getStorePassword().toCharArray());
        fis.close();
        char[] keyPassword = signingConfig.getKeyPassword().toCharArray();
        KeyStore.PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
                signingConfig.getKeyAlias(),
                new KeyStore.PasswordProtection(keyPassword));
        String sha1 = getFingerprint(entry.getCertificate(), "SHA1");


        props.each {
            key, value
                ->
                    String encrypt = AESEncryption.symetricEncrypt(sha1, value);
                    variant.buildConfigField("String", "SECURE_" + key, "\"$encrypt\"")
        }
    }

    public static String getFingerprint(Certificate cert, String hashAlgorithm) {
        if (cert == null) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
            return toHexadecimalString(digest.digest(cert.getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            // ignore
        } catch (CertificateEncodingException e) {
            // ignore
        }
        return null;
    }

    private static String toHexadecimalString(byte[] value) {
        StringBuilder sb = new StringBuilder();
        int len = value.length;
        for (int i = 0; i < len; i++) {
            int num = ((int) value[i]) & 0xff;
            if (num < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(num));
            if (i < len - 1) {
                sb.append(':');
            }
        }
        return sb.toString().toUpperCase(Locale.US);
    }

}
