package com.mandalalabs.chirp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.UserSession;
import com.mandalalabs.chirp.adapter.NeighborsListAdapter;
import com.mandalalabs.chirp.utils.Constants;
import com.mandalalabs.chirp.utils.PermissionUtils;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = Constants.LOG_TAG;
    public static final int REQUEST_CODE_LOCATION_PERMISSION = 100;
    private static final int REQUEST_CHECK_SETTINGS = 1000;
    private RecyclerView rvNeighbors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        TextView tvUser = (TextView) findViewById(R.id.userDetails);
        tvUser.setText(UserSession.loggedInUser == null ? "NULL USER" : UserSession.loggedInUser.getUsername());
        Toast.makeText(ChatRoomActivity.this, "Welcome to the chat room!!!", Toast.LENGTH_SHORT).show();

        UserSession.locationRequest = new LocationRequest();
        UserSession.locationRequest.setInterval(1000);
        UserSession.locationRequest.setFastestInterval(500);
        UserSession.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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
                final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    ChatRoomActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });

        rvNeighbors = (RecyclerView) findViewById(R.id.rvNeighbors);

        if (UserSession.neighborsList == null) {
            UserSession.neighborsList = new ArrayList<ParseObject>();
        }
        // Create adapter passing in the sample user data
        NeighborsListAdapter adapter = new NeighborsListAdapter(UserSession.neighborsList);
        // Attach the adapter to the recyclerview to populate items
        rvNeighbors.setAdapter(adapter);
        // Set layout manager to position the items
        rvNeighbors.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS) {
            Log.d(TAG, "Result for check settings: " + resultCode);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location update received: " + location.toString());
        UserSession.currentLocation = location;
        transmitUserLocation(location);
        setLocationText(UserSession.currentLocation);
    }

    private void transmitUserLocation(Location location) {
        if (location != null) {
            if (UserSession.userLocationInfo == null) {
                UserSession.userLocationInfo = new ParseObject(Constants.TABLE_USER_LOCATION_INFO);
                UserSession.userLocationInfo.put("userId", UserSession.loggedInUser.getObjectId());
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
        new Exception().printStackTrace();
        String[] locationPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ArrayList<String> missingPermissions = PermissionUtils.getMissingPermissions(
                getApplicationContext(), locationPermissions);
        if (missingPermissions.isEmpty() == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(locationPermissions, REQUEST_CODE_LOCATION_PERMISSION);
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
        String[] locationPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ArrayList<String> missingPermissions = PermissionUtils.getMissingPermissions(
                getApplicationContext(), locationPermissions);
        if (missingPermissions.isEmpty() == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(locationPermissions, REQUEST_CODE_LOCATION_PERMISSION);
            }
            return;
        }

        UserSession.lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                UserSession.googleApiClient);

        Log.d(TAG, "Last known location: " + UserSession.lastKnownLocation);
        if (UserSession.lastKnownLocation != null) {
            Log.d(TAG, "Known last location: " + UserSession.lastKnownLocation.toString());
            setLocationText(UserSession.lastKnownLocation);
        }
    }

    private void setLocationText(Location userLocation) {
        TextView tvUserLocation = (TextView) findViewById(R.id.userLocation);
        tvUserLocation.setText(
                (userLocation == null) ?
                        getResources().getString(R.string.no_clue_where_you_are) :
                        "Long: " + userLocation.getLongitude() + "; Lat: " + userLocation.getLatitude()
        );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection to LocationServices suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "Connection attempt to LocationServices FAILED with error: " + connectionResult.getErrorMessage());
    }

    public void getNeighbors(View view) {
        Location userLocation = (UserSession.currentLocation == null ? UserSession.lastKnownLocation : UserSession.currentLocation);

        if (userLocation != null) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.TABLE_USER_LOCATION_INFO);
            query.whereWithinMiles(Constants.LOCATION_KEY, new ParseGeoPoint(userLocation.getLatitude(), userLocation.getLongitude()), 1.0);
            query.setLimit(10);
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    UserSession.neighborsList.addAll(objects);
                    Log.d(TAG, "List of neighboring users: size = " + objects.size());
                    for (ParseObject object: objects) {
                        ParseGeoPoint userLocation = (ParseGeoPoint) object.get(Constants.LOCATION_KEY);
                        Log.d(TAG, "User ID:" + object.get("userId") + "; Location: " + userLocation.getLatitude() + ", " + userLocation.getLongitude());


                    }
                    rvNeighbors.getAdapter().notifyDataSetChanged();
                }
            });
        }
    }
}
