package com.leroymerlin.pandroid.net.mock;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leroymerlin.pandroid.log.PandroidLogger;
import com.leroymerlin.pandroid.net.http.Mock;
import com.leroymerlin.pandroid.utils.IoUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * Created by florian on 16/02/16.
 */
public class ServiceMock {

    private static final String TAG = "ServiceMock";
    protected Mock mockInfo;
    protected final Gson gson;
    private Type returnType;

    public ServiceMock() {
        gson = new GsonBuilder().create();
    }

    public void setMockInfo(Mock mockInfo) {
        this.mockInfo = mockInfo;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public int getStatusCode() {
        return mockInfo.statusCode();
    }

    public int getResponseDelay() {
        return mockInfo.delay();
    }

    public <R> Response<R> getMockResponse(Request request, Context context) {
        R content = null;
        String path = mockInfo.path();
        try {
            content = gson.fromJson(getJson(context, path), returnType);
        } catch (Exception e) {
            PandroidLogger.getInstance().e(TAG, "can't read " + path, e);
        }
        if (mockInfo.statusCode() == 200)
            return Response.success(content);
        else {
            ResponseBody body = ResponseBody.create(okhttp3.MediaType.parse(""), "mock error");
            return Response.error(mockInfo.statusCode(), body);
        }

    }

    @NonNull
    protected String getJson(Context context, String path) throws IOException {
        return IoUtils.readFileFromAssets(context, path);
    }

    public boolean isEnable() {
        return mockInfo.enable();
    }
}