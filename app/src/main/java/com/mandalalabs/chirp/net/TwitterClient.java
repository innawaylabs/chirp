package com.mandalalabs.chirp.net;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.codepath.oauth.OAuthBaseClient;
import com.mandalalabs.chirp.R;
import com.mandalalabs.chirp.utils.Constants;

public class TwitterClient extends OAuthBaseClient {
    private static final String TAG = Constants.LOG_TAG;
    public static final Class<? extends Api> REST_API_CLASS = TwitterApi.SSL.class;
    public static final String REST_CALLBACK_URL = "oauth://chirp";

    public TwitterClient(Context context) {
        super(context,
                REST_API_CLASS,
                context.getResources().getString(R.string.base_url),
                context.getResources().getString(R.string.twitter_api_key),
                context.getResources().getString(R.string.twitter_api_secret),
                REST_CALLBACK_URL);
        Log.d(TAG, "Created a Twitter Client");

    }

    public void getUserInfo(AsyncHttpResponseHandler handler, String screenName) {
        if (screenName != null && !screenName.isEmpty()) {
            RequestParams params = new RequestParams();
            params.put(Constants.keyScreenName, screenName.substring(2));
            Log.d(TAG, "Sending API call to users/show.json");
            client.get(getApiUrl("users/show.json"), params, handler);
        } else {
            Log.d(TAG, "Sending API call to account/verify_credentials.json");
            client.get(getApiUrl("account/verify_credentials.json"), null, handler);
        }
    }
}
