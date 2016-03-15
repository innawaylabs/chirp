package com.mandalalabs.chirp;

import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

public class UserSession {
    public static ParseUser loggedInUser;
    public static GoogleApiClient googleApiClient;
    public static Location lastKnownLocation;
    public static Location currentLocation;
    public static ParseObject userLocationInfo;
    public static ParseObject userDetails;
    public static LocationRequest locationRequest;
    public static ArrayList<ParseObject> neighborsList;
    public static ArrayList<ParseObject> chirpsList;
}
