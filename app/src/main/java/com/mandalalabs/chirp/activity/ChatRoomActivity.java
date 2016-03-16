package com.mandalalabs.chirp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.UserSession;
import com.mandalalabs.chirp.adapter.ChirpFragmentPagerAdapter;
import com.mandalalabs.chirp.fragment.OnListFragmentInteractionListener;
import com.mandalalabs.chirp.fragment.ProfileFragment;
import com.mandalalabs.chirp.utils.Constants;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Random;

public class ChatRoomActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        OnListFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener {
    private static final String TAG = Constants.LOG_TAG;
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 100;
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    ChirpFragmentPagerAdapter chirpFragmentPagerAdapter;
    ViewPager vpViewPager;
    PagerSlidingTabStrip tsTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        chirpFragmentPagerAdapter = new ChirpFragmentPagerAdapter(getSupportFragmentManager());
        vpViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (vpViewPager != null)
            vpViewPager.setAdapter(chirpFragmentPagerAdapter);

        tsTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        if (tsTabStrip != null)
            tsTabStrip.setViewPager(vpViewPager);

        if (UserSession.loggedInUser == null) {
            Log.d(TAG, "User: NULL");
        } else {
            Log.d(TAG, "User: " + UserSession.loggedInUser.getUsername());
        }
        Toast.makeText(ChatRoomActivity.this, "Welcome to the chat room!!!", Toast.LENGTH_SHORT).show();

        if (UserSession.locationRequest == null) {
            UserSession.locationRequest = new LocationRequest();
            UserSession.locationRequest.setInterval(10000);
            UserSession.locationRequest.setFastestInterval(5000);
            UserSession.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        if (UserSession.googleApiClient == null) {
            UserSession.googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (UserSession.googleApiClient != null) {
            UserSession.googleApiClient.connect();
        }

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(UserSession.locationRequest);
        final PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        UserSession.googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.e(TAG, "Need resolution of Location settings!!!");
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    ChatRoomActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // TODO: Switch to offline more
                        Log.w(TAG, "Location settings unavailable. Switching to offline mode!!!");
                        break;
                }
            }
        });

        initUser();
    }

    private void initUser() {
        ParseUser user = UserSession.loggedInUser;
        if (user == null) {
            Log.w(TAG, "Failed to initialize user because it's null");
            return;
        }
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.TABLE_USER_LOCATION_INFO);
        query.whereEqualTo(Constants.USER_ID_KEY, user.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() >= 1) {
                    UserSession.userLocationInfo = objects.get(0);
                }
            }
        });
        query = ParseQuery.getQuery(Constants.TABLE_USER_DETAILS);
        query.whereEqualTo(Constants.USER_ID_KEY, user.getObjectId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() == 0) {
                    UserSession.userDetails = new ParseObject(Constants.TABLE_USER_DETAILS);
                    UserSession.userDetails.put(Constants.USER_ID_KEY, UserSession.loggedInUser.getObjectId());

                    if (UserSession.loggedInUser.getUsername() != null) {
                        UserSession.userDetails.put(Constants.USER_NAME_KEY, UserSession.loggedInUser.getUsername());
                    }
                    try {
                        UserSession.userDetails.save();
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else if (objects.size() == 1) {
                    UserSession.userDetails = objects.get(0);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            Log.d(TAG, "Result for check settings: " + resultCode);
            startLocationUpdates();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location update received: " + location.toString());
        UserSession.currentLocation = location;
        transmitUserLocation(location);
    }

    private void transmitUserLocation(Location location) {
        if (location != null) {
            if (UserSession.userLocationInfo == null) {
                UserSession.userLocationInfo = new ParseObject(Constants.TABLE_USER_LOCATION_INFO);
                UserSession.userLocationInfo.put(Constants.USER_ID_KEY, UserSession.loggedInUser.getObjectId());
            }

            ParseGeoPoint userGeoLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
            UserSession.userLocationInfo.put(Constants.LOCATION_KEY, userGeoLocation);
            UserSession.userLocationInfo.saveInBackground();
            UserSession.loggedInUser.put(Constants.LOCATION_KEY, userGeoLocation);
            UserSession.loggedInUser.saveInBackground();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates()");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION} , REQUEST_CODE_LOCATION_PERMISSION);
            }
            return;
        }

        if (UserSession.googleApiClient != null && UserSession.googleApiClient.isConnected()) {
            Log.d(TAG, "Starting location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(UserSession.googleApiClient, UserSession.locationRequest, this);
        }
    }

    private void stopLocationUpdates() {
        if (UserSession.googleApiClient != null && UserSession.googleApiClient.isConnected()) {
            Log.d(TAG, "Stopping location updates");
            LocationServices.FusedLocationApi.removeLocationUpdates(UserSession.googleApiClient, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            boolean allPermissionsGranted = true;

            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
            Log.d(TAG, "All permissions granted? " + allPermissionsGranted);
            startLocationUpdates();
        } else {
            Log.e(TAG, "Unknown permission request result received!!!");
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "Connected to LocationServices");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION} , REQUEST_CODE_LOCATION_PERMISSION);
            }
            return;
        }
        UserSession.lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                UserSession.googleApiClient);

        Log.d(TAG, "Last known location: " + UserSession.lastKnownLocation);
        if (UserSession.lastKnownLocation != null) {
            Log.d(TAG, "Known last location: " + UserSession.lastKnownLocation.toString());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection to LocationServices suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection attempt to LocationServices FAILED with error: " + connectionResult.getErrorMessage());
    }

    @Override
    public void onListFragmentInteraction(ParseObject item) {

    }

    public void onChirp(View view) {
        ParseObject chirp = new ParseObject(Constants.TABLE_CHIRPS);

        chirp.put(Constants.SENDER_KEY, UserSession.loggedInUser);
        chirp.put(Constants.MESSAGE_KEY, UserSession.loggedInUser.getUsername() + "'s Random chirp " + new Random().nextInt(1000));
        chirp.saveInBackground();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        Toast.makeText(ChatRoomActivity.this, "Profile clicked", Toast.LENGTH_SHORT).show();
    }
}
