package org.opencorpora.authenticator;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;
import org.opencorpora.BuildConfig;
import org.opencorpora.data.api.OpenCorporaRequestQueue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

class AuthHelper {
    private static final String LOG_TAG = "AuthHelper";
    private static final String AUTH_URL = BuildConfig.server_address + "api/login.php";

    private Context mContext;

    public AuthHelper(Context context){
        mContext = context;
    }

    public String singIn(final String username, final String password){
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.POST ,AUTH_URL, future, future){
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("login", username);
                        params.put("token", password);
                        return params;
                    }
                };
        OpenCorporaRequestQueue.getInstance(mContext).getRequestQueue().add(request);
        String token = null;

        try{
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            token = response.getString("token");
            Log.i(LOG_TAG, "Auth request successfully complete. Token: " + token);
        } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
            e.printStackTrace();
        }

        return token;
    }
}
