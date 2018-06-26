package com.linuxluigi.opendecibelmeter.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * rest api client for opensenemap.org
 * https://api.opensensemap.org/
 */
public class Client {
    // base url
    private static final String BASE_URL = "https://api.opensensemap.org/";

    // SharedPreferences
    public static final String PREFERENCE_BASE = "Opensensemap";
    public static final String PREFERENCE_TOKEN = "token";
    public static final String PREFERENCE_EMAIL = "email";
    public static final String PREFERENCE_REFRESH_TOKEN = "refresh-token";
    public static final String PREFERENCE_NAME = "username";
    public static final String PREFERENCE_BOX_ID = "box-id";
    public static final String PREFERENCE_BOX_NAME = "box-name";
    public static final String PREFERENCE_SENSOR_ID = "sensor-id";


    private static AsyncHttpClient client;

    /**
     * set the client default header
     */
    private static void initClient() {
        client = new AsyncHttpClient();
        client.addHeader("Accept", "application/json");
        client.addHeader("Content-Type", "application/json");
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        initClient();
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, RequestParams params, String authToken, AsyncHttpResponseHandler responseHandler) {
        initClient();
        client.addHeader("Authorization", "Bearer " + authToken);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        initClient();
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, String authToken, AsyncHttpResponseHandler responseHandler) {
        initClient();
        client.addHeader("Authorization", "Bearer " + authToken);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        initClient();
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        initClient();
        client.post(url, params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
