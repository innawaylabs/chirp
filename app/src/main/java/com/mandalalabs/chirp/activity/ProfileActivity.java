package com.mandalalabs.chirp.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.utils.Constants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String userId = getIntent().getStringExtra(Constants.USER_ID_KEY);
        ParseQuery<ParseUser> user = ParseUser.getQuery();
        user.getInBackground(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser currentUser, ParseException e) {
                Log.d(Constants.LOG_TAG, "Got parse user's profile by ID: " + currentUser.getObjectId());
            }
        });
    }
}
