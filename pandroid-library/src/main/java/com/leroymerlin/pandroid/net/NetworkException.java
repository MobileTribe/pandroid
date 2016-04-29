package com.leroymerlin.pandroid.net;

import android.text.TextUtils;
import android.util.Log;

import com.leroymerlin.pandroid.app.PandroidConfig;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Created by florian on 09/12/15.
 */
public class NetworkException extends Exception {

    private static final String TAG = "NetworkException";


    private final String url;
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final byte[] body;
    private final long networkTime;

    public NetworkException(String url, int statusCode, Map<String, List<String>> headers, Exception error, byte[] body, long networkTime) {
        super(error);
        this.url = url;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
        this.networkTime = networkTime;
    }

    public String getErrorMessage() {
        if (getCause().getMessage() == null) {
            return "";
        }
        return getCause().getMessage();
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Wasp Error: ");
        if (getCause().getMessage() != null) {
            builder.append(", Message:").append(getCause().getMessage());
        }
        builder.append("Status Code: ").append(statusCode)
                .append("Url ").append(url);
        return builder.toString();
    }

    public String getBody() {
        if (body == null)
            return null;
        else
            return new String(body);

    }

    public void logWaspError() {

        if (PandroidConfig.DEBUG) {

            Log.d(TAG, "<--- ERROR " + statusCode + " " + url);
            Log.d(TAG, "Cause -" + getCause());
            Log.d(TAG, "Message - " + "[" + getErrorMessage() + "]");
            if (!headers.isEmpty()) {
                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                    String l = "Header - [" + entry.getKey() + ": ";
                    for (String e : entry.getValue()) {
                        l += e + " ,";
                    }
                    Log.d(TAG, l + "]");

                }
            }

            String bodyString = "";
            try {
                bodyString = new String(body, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                bodyString = "Unable to parse error body!!!!!";
            }
            Log.d(TAG, TextUtils.isEmpty(bodyString) ? "Body - no body" : "Body - " + bodyString);
            Log.d(TAG, "<--- END " + "(Size: " + body.length + " bytes - Network time: " + networkTime + " ms)");
        }
    }
}
