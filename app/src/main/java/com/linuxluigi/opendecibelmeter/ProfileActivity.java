package com.linuxluigi.opendecibelmeter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.linuxluigi.opendecibelmeter.api.Client;
import com.linuxluigi.opendecibelmeter.models.Boxes;
import com.linuxluigi.opendecibelmeter.models.SingleBox;
import com.linuxluigi.opendecibelmeter.utli.GravatarUserImage;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

/**
 * display user profil from opensenemap
 */
public class ProfileActivity extends AppCompatActivity {

    Spinner boxesSpinner;
    Boxes boxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        SharedPreferences sp = this.getSharedPreferences(Client.PREFERENCE_BASE, MODE_PRIVATE);
        String token = sp.getString(Client.PREFERENCE_TOKEN, null);
        String email = sp.getString(Client.PREFERENCE_EMAIL, null);
        String username = sp.getString(Client.PREFERENCE_NAME, null);

        // fill user profile
        new GravatarUserImage((ImageView) findViewById(R.id.userImage))
                .execute(email);
        TextView emailText = findViewById(R.id.userEmail);
        emailText.setText(email);
        TextView usernameText = findViewById(R.id.userName);
        usernameText.setText(username);

        // todo move to OpensensemapClient
        Client.get("users/me/boxes", null, token, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("opensenemap-boxes", "success : " + response);
                JSONArray boxesArray;
                JSONObject data;
                try {
                    data = (JSONObject) response.get("data");
                    Log.d("opensenemap-boxes", "data : " + data);
                    boxesArray = data.getJSONArray("boxes");
                    Log.d("opensenemap-boxes", "boxes : " + boxes);
                    boxes = new Boxes(boxesArray);
                    fillSpinner();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Log.d("opensenemap-boxes", "onFailure : " + throwable);
                Log.d("opensenemap-boxes", "onFailure : " + jsonObject.toString());


                // save user data
                SharedPreferences sp = getSharedPreferences(Client.PREFERENCE_BASE, MODE_PRIVATE);
                SharedPreferences.Editor Ed = sp.edit();
                Ed.putString(Client.PREFERENCE_TOKEN, null);
                Ed.putString(Client.PREFERENCE_EMAIL, null);
                Ed.putString(Client.PREFERENCE_NAME, null);
                Ed.putString(Client.PREFERENCE_REFRESH_TOKEN, null);
                Ed.apply();

                startActivity(new Intent(getApplicationContext(), MeasureActivity.class));

            }

        });

        // logout Button
        final Button logoutBtn = findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // delete user data
                SharedPreferences sp = getSharedPreferences(Client.PREFERENCE_BASE, MODE_PRIVATE);
                SharedPreferences.Editor Ed = sp.edit();
                Ed.putString(Client.PREFERENCE_TOKEN, null);
                Ed.putString(Client.PREFERENCE_EMAIL, null);
                Ed.putString(Client.PREFERENCE_NAME, null);
                Ed.putString(Client.PREFERENCE_REFRESH_TOKEN, null);
                Ed.apply();

                // go back to MeasureActivity
                startActivity(new Intent(getApplicationContext(), MeasureActivity.class));
            }
        });
    }

    private void fillSpinner() {
        // fill spinner
        this.boxesSpinner = findViewById(R.id.boxesSpinner);
        List<String> list = this.boxes.getArrayList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boxesSpinner.setAdapter(adapter);

        this.boxesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Log.d("profile", boxesSpinner.getSelectedItem().toString());
                SingleBox box = boxes.getSingleBoxByName(
                        boxesSpinner.getSelectedItem().toString()
                );

                // save box & sensor id
                SharedPreferences sp = getSharedPreferences(Client.PREFERENCE_BASE, MODE_PRIVATE);
                SharedPreferences.Editor Ed = sp.edit();
                Ed.putString(Client.PREFERENCE_BOX_NAME, box.getBoxName());
                Ed.putString(Client.PREFERENCE_BOX_ID, box.getBoxId());
                Ed.putString(Client.PREFERENCE_SENSOR_ID, box.getSensorId());
                Ed.apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Log.d("profile", "nothing select");
            }

        });
    }

}

