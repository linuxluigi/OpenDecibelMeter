package com.linuxluigi.opendecibelmeter;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.linuxluigi.opendecibelmeter.api.Client;
import com.linuxluigi.opendecibelmeter.api.OpensensemapClient;
import com.linuxluigi.opendecibelmeter.db.DecibelContract;
import com.linuxluigi.opendecibelmeter.db.DecibelDbHelper;
import com.linuxluigi.opendecibelmeter.models.SingleBox;
import com.linuxluigi.opendecibelmeter.utli.GravatarUserImage;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MeasureActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // location
    EditText latituteEditText;
    EditText longitudeEditText;

    // decibel view
    TextView mStatusView;
    EditText calibarteForm;

    // settings for recording
    Boolean calibarteDevice = false;
    Boolean logEnable = false;
    Boolean autoUpload = false;
    Boolean autoUpdateCalibration = false;

    // decibel meter process vars
    MediaRecorder mRecorder;
    Thread runner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;

    private static final String PREFERENCE_DEVICE_BASE = "device-base";
    private static final String PREFERENCE_DEVICE_AMPLITUDE = "amplitude";

    // saving last 600 records for calculating the average
    List<Double> recordStack;

    // record data
    SingleBox box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set ui Elements to private vars
        latituteEditText = findViewById(R.id.positionLatitude);
        longitudeEditText = findViewById(R.id.positionLongitude);
        calibarteForm = findViewById(R.id.calibrateForm);

        // set action listener
        setButtons();

        // update user profile on nav bar
        updateUserProfile();

        // update token
        refreshAuthToken();

        // start background process for decibel meter
        mStatusView = findViewById(R.id.decibelView);
        if (runner == null) {
            runner = new Thread() {
                public void run() {
                    while (runner != null) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException ignored) {
                        }
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            // go to login activity
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_profile) {
            // go to profile activity
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_graph) {
            // go to graph activity
            Intent i = new Intent(getApplicationContext(), GraphActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * set all buttons for activity
     */
    private void setButtons() {
        // get current Position Button
        Button positionBtn = findViewById(R.id.positionBtn);
        positionBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                Snackbar.make(view, getResources().getString(R.string.update_location), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                LocationManager locationManager = (LocationManager)
                        getSystemService(Context.LOCATION_SERVICE);

                LocationListener locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String s, int i, Bundle bundle) {

                    }

                    @Override
                    public void onProviderEnabled(String s) {

                    }

                    @Override
                    public void onProviderDisabled(String s) {

                    }
                };

                assert locationManager != null;
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, 5000, 10, locationListener);


                Location location = locationManager.getLastKnownLocation((LocationManager.GPS_PROVIDER));
                double latitude = location.getLatitude();
                double logitute = location.getLongitude();

                latituteEditText.setText(String.valueOf(latitude));
                longitudeEditText.setText(String.valueOf(logitute));
            }
        });

        // calibrate device Button
        final Button calibrateBtn = findViewById(R.id.calibrateBtn);
        calibrateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout calibrateView = findViewById(R.id.calibrateView);
                SharedPreferences sp = getSharedPreferences(PREFERENCE_DEVICE_BASE, MODE_PRIVATE);

                if (calibarteDevice) {
                    // save Amplitude
                    // change button text
                    calibrateBtn.setText(R.string.measure_calibrate);

                    // save Amplitude
                    SharedPreferences.Editor Ed = sp.edit();
                    Ed.putFloat(PREFERENCE_DEVICE_AMPLITUDE, (float) Double.parseDouble(calibarteForm.getText().toString()));
                    Ed.apply();

                    calibrateView.setVisibility(View.GONE);
                } else {
                    // change button text
                    calibrateBtn.setText(R.string.measure_save_amplitude);

                    calibrateView.setVisibility(View.VISIBLE);

                    calibarteForm.setText(String.format("%s", sp.getFloat(PREFERENCE_DEVICE_AMPLITUDE, 0)));
                }

                calibarteDevice = !calibarteDevice;
            }
        });

        // log Button (Start / Stop)
        final Button logBtn = findViewById(R.id.logBtn);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (logEnable) {
                    // change button text
                    logBtn.setText(R.string.measure_logging_start);
                } else {
                    // change button text
                    logBtn.setText(R.string.measure_logging_stop);

                    SharedPreferences sp = getSharedPreferences(Client.PREFERENCE_BASE, MODE_PRIVATE);
                    box = new SingleBox(
                            sp.getString(Client.PREFERENCE_BOX_NAME, ""),
                            sp.getString(Client.PREFERENCE_BOX_ID, ""),
                            sp.getString(Client.PREFERENCE_SENSOR_ID, "")
                    );

                    // reset recordStack
                    recordStack = new ArrayList<>();
                }
                logEnable = !logEnable;
            }
        });

        // auto upload switch
        Switch autoUploadSwitch = findViewById(R.id.autoUploadSwitch);
        autoUploadSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoUpload = isChecked;
            }
        });

        // auto update calibration switch
        Switch autoUpdateCalibrationSwitch = findViewById(R.id.autoUpdate);
        autoUpdateCalibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoUpdateCalibration = isChecked;
            }
        });

        // location view btn
        final Button locationBtn = findViewById(R.id.locationBtn);
        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout locationView = findViewById(R.id.locationView);

                if (locationView.getVisibility() == View.VISIBLE) {
                    locationView.setVisibility(View.GONE);
                } else {
                    locationView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    final Runnable updater = new Runnable() {

        public void run() {
            try {
                updateView();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    final Handler mHandler = new Handler();

    /**
     * restart recording on resume
     */
    public void onResume() {
        super.onResume();
        startRecorder();
    }

    /**
     * pause record on pause
     */
    public void onPause() {
        super.onPause();
        stopRecorder();
    }


    /**
     * start recoding
     */
    public void startRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                android.util.Log.e("[Monkey]", "IOException: " +
                        android.util.Log.getStackTraceString(ioe));

            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }
            try {
                mRecorder.start();
            } catch (java.lang.SecurityException e) {
                android.util.Log.e("[Monkey]", "SecurityException: " +
                        android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }

    /**
     * stop recording
     */
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    /**
     * update decibel value view
     *
     * @throws JSONException
     */
    public void updateView() throws JSONException {

        if (this.calibarteDevice && this.autoUpdateCalibration) {
            Log.i("Noise", "AmplitudeEMA " + getAmplitudeEMA());
            calibarteForm.setText(String.format("%s", getAmplitudeEMA()));
        }

        SharedPreferences sp = this.getSharedPreferences(PREFERENCE_DEVICE_BASE, MODE_PRIVATE);
        double amplitude = (double) sp.getFloat(PREFERENCE_DEVICE_AMPLITUDE, 0.1f);

        double decibel = soundDb(amplitude);
        mStatusView.setText(String.format("%s dB", (int) decibel));
        Log.i("Noise", "db " + Double.toString(soundDb(3.0)));

        // update noise level view
        updateNoiseLevel((int) decibel);

        if (logEnable) {
            recordStack.add(decibel);

            if (recordStack.size() >= 600) {

                double average = 0;

                for (int i = 0; i < recordStack.size(); i++) {
                    average += recordStack.get(i);
                }
                average = average / recordStack.size();
                recordStack = new ArrayList<>();

                // update box
                box.setDecibel(average);
                box.setLatitude(Double.parseDouble(this.latituteEditText.getText().toString()));
                box.setLongitude(Double.parseDouble(this.longitudeEditText.getText().toString()));

                insertLogEntry(box);

                if (autoUpload) {
                    sp = this.getSharedPreferences(Client.PREFERENCE_BASE, MODE_PRIVATE);
                    String token = sp.getString(Client.PREFERENCE_TOKEN, null);

                    OpensensemapClient opensensemapClient = new OpensensemapClient(this);
                    opensensemapClient.uploadData(box, token);
                }
            }
        }

    }

    /**
     * calculating decibel from the amplitude
     *
     * @param ampl mic amplitude
     * @return
     */
    public double soundDb(double ampl) {
        double decibel = 20 * Math.log10(getAmplitudeEMA() / ampl);
        if (decibel < 0) {
            return 0;
        } else {
            return decibel;
        }
    }

    /**
     * @return
     */
    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;

    }

    /**
     * get the mic amplitude
     *
     * @return amplitude
     */
    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    /**
     * update the user profile in the nav menu
     */
    public void updateUserProfile() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        LinearLayout headerView = (LinearLayout) navigationView.getHeaderView(0);

        // get user data from SharedPreferences
        SharedPreferences sp = this.getSharedPreferences(Client.PREFERENCE_BASE, MODE_PRIVATE);
        String userEmail = sp.getString(Client.PREFERENCE_EMAIL, getResources().getString(R.string.default_email));
        String userName = sp.getString(Client.PREFERENCE_NAME, getResources().getString(R.string.default_username));

        // display current user in nav menu
        new GravatarUserImage((ImageView) headerView.findViewById(R.id.userImage))
                .execute(userEmail);
        TextView userNameView = headerView.findViewById(R.id.userName);
        userNameView.setText(userName);
        TextView userEmailView = headerView.findViewById(R.id.userEmail);
        userEmailView.setText(userEmail);
    }

    /**
     * Get user input from editor and save new log into database.
     */
    private void insertLogEntry(SingleBox singleBox) {
        // Create database helper
        DecibelDbHelper mDbHelper = new DecibelDbHelper(this);

        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(DecibelContract.LogEntry.COLUMN_LOG_DECIBEL, singleBox.getDecibel());
        values.put(DecibelContract.LogEntry.COLUMN_LOG_LATITUDE, singleBox.getLatitude());
        values.put(DecibelContract.LogEntry.COLUMN_LOG_LONGITUTE, singleBox.getLongitude());

        // Insert a new row for pet in the database, returning the ID of that new row.
        long newRowId = db.insert(DecibelContract.LogEntry.TABLE_NAME, null, values);

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, getResources().getString(R.string.error_db_record), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * refresh the auth token from opensensemap
     */
    private void refreshAuthToken() {
        SharedPreferences sp = this.getSharedPreferences(Client.PREFERENCE_BASE, MODE_PRIVATE);
        String refreshToken = sp.getString(Client.PREFERENCE_REFRESH_TOKEN, null);

        if (refreshToken != null) {
            OpensensemapClient opensensemapClient = new OpensensemapClient(this);
            opensensemapClient.refreshAuthToken(refreshToken, sp);
        }
    }

    /**
     * update noise level view (image + text)
     *
     * @param decibel decibel value to display
     */
    private void updateNoiseLevel(int decibel) {

        TextView noiseText = findViewById(R.id.noiseText);
        ImageView noiseImage = findViewById(R.id.noiseImage);

        if (decibel < 10) {
            noiseText.setText(R.string.noise_0);
            noiseImage.setImageResource(R.drawable.noise_0);
        } else if (decibel < 20) {
            noiseText.setText(R.string.noise_10);
            noiseImage.setImageResource(R.drawable.noise_10);
        } else if (decibel < 30) {
            noiseText.setText(R.string.noise_20);
            noiseImage.setImageResource(R.drawable.noise_20);
        } else if (decibel < 40) {
            noiseText.setText(R.string.noise_30);
            noiseImage.setImageResource(R.drawable.noise_30);
        } else if (decibel < 50) {
            noiseText.setText(R.string.noise_40);
            noiseImage.setImageResource(R.drawable.noise_40);
        } else if (decibel < 60) {
            noiseText.setText(R.string.noise_50);
            noiseImage.setImageResource(R.drawable.noise_50);
        } else if (decibel < 70) {
            noiseText.setText(R.string.noise_60);
            noiseImage.setImageResource(R.drawable.noise_60);
        } else if (decibel < 80) {
            noiseText.setText(R.string.noise_70);
            noiseImage.setImageResource(R.drawable.noise_70);
        } else if (decibel < 90) {
            noiseText.setText(R.string.noise_80);
            noiseImage.setImageResource(R.drawable.noise_80);
        } else if (decibel < 100) {
            noiseText.setText(R.string.noise_90);
            noiseImage.setImageResource(R.drawable.noise_90);
        } else if (decibel < 110) {
            noiseText.setText(R.string.noise_100);
            noiseImage.setImageResource(R.drawable.noise_100);
        } else if (decibel < 120) {
            noiseText.setText(R.string.noise_110);
            noiseImage.setImageResource(R.drawable.noise_110);
        } else if (decibel < 130) {
            noiseText.setText(R.string.noise_120);
            noiseImage.setImageResource(R.drawable.noise_120);
        } else if (decibel < 140) {
            noiseText.setText(R.string.noise_130);
            noiseImage.setImageResource(R.drawable.noise_130);
        } else if (decibel < 150) {
            noiseText.setText(R.string.noise_140);
            noiseImage.setImageResource(R.drawable.noise_140);
        } else if (decibel < 160) {
            noiseText.setText(R.string.noise_150);
            noiseImage.setImageResource(R.drawable.noise_150);
        } else if (decibel < 170) {
            noiseText.setText(R.string.noise_160);
            noiseImage.setImageResource(R.drawable.noise_160);
        } else if (decibel < 180) {
            noiseText.setText(R.string.noise_170);
            noiseImage.setImageResource(R.drawable.noise_170);
        } else {
            noiseText.setText(R.string.noise_180);
            noiseImage.setImageResource(R.drawable.noise_180);
        }
    }
}
