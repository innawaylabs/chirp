package com.mandalalabs.chirp.app;

import android.app.Application;

import com.mandalalabs.chirp.R;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(this).applicationId(),
                getResources().getString(R.string.parse_app_id),
                getResources().getString(R.string.parse_master_key));

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
  }
}