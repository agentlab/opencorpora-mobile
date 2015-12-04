package org.opencorpora;

import android.content.SharedPreferences;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AuthRequestHandler extends JsonHttpResponseHandler {

    private SharedPreferences pref;

    public AuthRequestHandler(SharedPreferences authPrefs) {
        pref = authPrefs;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        try {
            pref.edit().putString("token", response.getString("token")).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onSuccess(statusCode, headers, response);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
    }
}
