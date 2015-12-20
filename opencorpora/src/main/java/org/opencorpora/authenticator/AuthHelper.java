package org.opencorpora.authenticator;

import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencorpora.BuildConfig;

import cz.msebera.android.httpclient.Header;

class AuthHelper {
    private static final String LOG_TAG = "AuthHelper";

    private String mResult;

    public synchronized String signIn(String username, String password) {
        SyncHttpClient client = new SyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("login", username);
        params.add("password", password);
        mResult = null;
        client.post(BuildConfig.server_address + "api/login.php",
                params,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            Log.i(LOG_TAG, "Request successful");
                            mResult = response.getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode,
                                          Header[] headers,
                                          Throwable throwable,
                                          JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.i(LOG_TAG, "Request failed");
                    }
                }
        );

        return mResult;
    }
}
