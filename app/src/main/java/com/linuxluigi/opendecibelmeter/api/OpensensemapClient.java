package com.linuxluigi.opendecibelmeter.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.linuxluigi.opendecibelmeter.models.SingleBox;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class OpensensemapClient {

    private Context context;
    private SharedPreferences sp;

    public OpensensemapClient(Context context) {
        this.context = context;
    }

    public void uploadData(SingleBox box, String token) throws JSONException {

        RequestParams rp = new RequestParams();
        rp.put("value", box.getDecibel());
        JSONObject location = new JSONObject();
        location.put("lat", box.getLatitude());
        location.put("lng", box.getLongitude());
        rp.setUseJsonStreamer(true);

        Client.post("boxes/" + box.getBoxId() + "/" + box.getSensorId(), rp, token, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, String message, Throwable throwable) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("opensenemap-upload", "onFailure : " + throwable);
                Log.d("opensenemap-upload", "onFailure : " + message);
                showToast(message.replaceAll("\"", ""));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("opensenemap-upload", "onFailure : " + throwable);
                Log.d("opensenemap-upload", "onFailure : " + jsonObject.toString());
                showToast("Error while uploading box measurement");
            }
        });
    }

    public void refreshAuthToken(String refreshToken, SharedPreferences sharedPreferences) {

        this.sp = sharedPreferences;

        RequestParams rp = new RequestParams();
        rp.put("token", refreshToken);
        rp.setUseJsonStreamer(true);

        Client.post("users/refresh-auth", rp, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("opensenemap-token-refresh", "success : " + response);
                String token;
                String refreshToken;
                try {
                    token = response.getString("token");
                    refreshToken = response.getString("refreshToken");
                    Log.d("opensenemap-token", token);
                    Log.d("opensenemap-token-refresh", refreshToken);

                    // save user data
                    SharedPreferences.Editor Ed = sp.edit();
                    Ed.putString(Client.PREFERENCE_TOKEN, token);
                    Ed.putString(Client.PREFERENCE_REFRESH_TOKEN, refreshToken);
                    Ed.apply();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("opensenemap-token-refresh", "onFailure : " + throwable);
                Log.d("opensenemap-token-refresh", "onFailure : " + jsonObject.toString());

                // save user data
                SharedPreferences.Editor Ed = sp.edit();
                Ed.putString(Client.PREFERENCE_TOKEN, null);
                Ed.putString(Client.PREFERENCE_EMAIL, null);
                Ed.putString(Client.PREFERENCE_NAME, null);
                Ed.putString(Client.PREFERENCE_REFRESH_TOKEN, null);
                Ed.apply();
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }

}
