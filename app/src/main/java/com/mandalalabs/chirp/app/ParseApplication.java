package com.mandalalabs.chirp.app;

import android.app.Application;
import android.util.Log;

import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.model.UserDetails;
import com.mandalalabs.chirp.utils.Constants;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.interceptors.ParseLogInterceptor;

public class ParseApplication extends Application {
    private static final String TAG = Constants.LOG_TAG;
    ParseUser loggedInUser = null;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(UserDetails.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getResources().getString(R.string.parse_app_id))
                .clientKey(getResources().getString(R.string.parse_master_key))
                .server(getResources().getString(R.string.parse_server))
                .addNetworkInterceptor(new ParseLogInterceptor())
//                .addNetworkInterceptor(new ParseStethoInterceptor())
                .build());

        ParseInstallation.getCurrentInstallation().saveInBackground();

        // Test creation of object
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
//        if (ParseUser.getCurrentUser() != null) {
//            loggedInUser = ParseUser.getCurrentUser();
//        } else {
//            login();
//        }
//
//        ParseUser user = new ParseUser();
//        user.setUsername("my name");
//        user.setPassword("my pass");
//        user.setEmail("email@example.com");
//
//        // other fields can be set just like with ParseObject
//        user.put("phone", "650-253-0000");
//
//
//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    loggedInUser = ParseUser.getCurrentUser();
//                    Log.d(TAG, "Login successful: " + loggedInUser.toString());
//                } else {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    loggedInUser = ParseUser.getCurrentUser();
                    Log.d(TAG, "Logged in Anonymous user: " + loggedInUser.toString());
//                    startWithCurrentUser();
                }
            }
        });
    }
}