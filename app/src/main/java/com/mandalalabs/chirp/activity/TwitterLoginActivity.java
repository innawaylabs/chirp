package com.mandalalabs.chirp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.mandalalabs.chirp.UserSession;
import com.mandalalabs.chirp.net.TwitterClient;
import com.mandalalabs.chirp.utils.Constants;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

public class TwitterLoginActivity extends OAuthLoginActivity<TwitterClient> {
    private static final String TAG = Constants.LOG_TAG;

    private boolean loggedInWithTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.d(TAG, "Restoring Instance State");
            loggedInWithTwitter = savedInstanceState.getBoolean("logged_in_with_twitter");
        }
        if (!loggedInWithTwitter) {
            getClient().connect();
        }
    }

    @Override
    public void onLoginSuccess() {
        Log.d(TAG, "Twitter login Successful!!");
        Toast.makeText(TwitterLoginActivity.this, "Twitter login successful!!!", Toast.LENGTH_LONG).show();
        loggedInWithTwitter = true;
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    UserSession.loggedInUser = user;
                    Intent i = new Intent(TwitterLoginActivity.this, ChatRoomActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onLoginFailure(Exception e) {
        loggedInWithTwitter = false;
        e.printStackTrace();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "Saving Instance State");
        outState.putBoolean("logged_in_with_twitter", loggedInWithTwitter);
    }
}
