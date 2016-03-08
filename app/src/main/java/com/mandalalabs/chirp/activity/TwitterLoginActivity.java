package com.mandalalabs.chirp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.codepath.oauth.OAuthLoginActivity;
import com.mandalalabs.chirp.net.TwitterClient;
import com.mandalalabs.chirp.utils.Constants;

public class TwitterLoginActivity extends OAuthLoginActivity<TwitterClient> {

    private boolean loggedInWithTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            Log.d(Constants.LOG_TAG, "Restoring Instance State");
            loggedInWithTwitter = savedInstanceState.getBoolean("logged_in_with_twitter");
        }
        if (!loggedInWithTwitter) {
            getClient().connect();
        }
    }

    @Override
    public void onLoginSuccess() {
        Log.d(Constants.LOG_TAG, "Twitter login Successful!!");
        Toast.makeText(TwitterLoginActivity.this, "Twitter login successful!!!", Toast.LENGTH_LONG).show();
        loggedInWithTwitter = true;
        Intent i = new Intent(this, ChatRoomActivity.class);
        startActivity(i);
    }

    @Override
    public void onLoginFailure(Exception e) {
        loggedInWithTwitter = false;
        e.printStackTrace();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(Constants.LOG_TAG, "Saving Instance State");
        outState.putBoolean("logged_in_with_twitter", loggedInWithTwitter);
    }
}
