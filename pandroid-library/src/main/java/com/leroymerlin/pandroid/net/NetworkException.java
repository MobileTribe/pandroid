package com.leroymerlin.pandroid.net;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by florian on 09/12/15.
 */
public class NetworkException extends Exception {

    private static final long serialVersionUID = -8397564457960144524L;

    private final String url;
    private final int statusCode;
    private final TreeMap<String, List<String>> headers;
    private final byte[] body;
    private final long networkTime;

    public NetworkException(String url, int statusCode, TreeMap<String, List<String>> headers, Exception error, byte[] body, long networkTime) {
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
        builder.append("<--- ERROR ").append(statusCode).append(" ").append(url).append("\n");
        builder.append("Cause -").append(getCause()).append("\n");
        builder.append("Message - " + "[").append(getErrorMessage()).append("]").append("\n");

        if (!headers.isEmpty()) {
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                builder.append("Header - [").append(entry.getKey()).append(": ");
                for (String e : entry.getValue()) {
                    builder.append(e).append(" ,");
                }
                builder.append("]").append("\n");
            }
        }

        String bodyString = "";
        try {
            bodyString = new String(body, "UTF-8");
        } catch (UnsupportedEncodingException ignored) {
            bodyString = "Unable to parse error body to UTF-8 String";
        }

        builder.append(TextUtils.isEmpty(bodyString) ? "Body - no body" : "Body - " + bodyString).append("\n");
        builder.append("<--- END " + "(Size: ").append(body.length).append(" bytes - Network time: ").append(networkTime).append(" ms)");
        return builder.toString();
    }

    public String getBody() {
        if (body == null)
            return null;
        else
            return new String(body);

    }
}
