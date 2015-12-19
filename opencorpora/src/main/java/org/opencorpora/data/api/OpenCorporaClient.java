package org.opencorpora.data.api;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencorpora.BuildConfig;
import org.opencorpora.data.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class OpenCorporaClient {
    private static final String LOG_TAG = "OpenCorporaClient";
    private static final String TYPES_URL = BuildConfig.server_address + "api/pool_types.php";

    private OpenCorporaRequestQueue mQueue;

    public OpenCorporaClient(Context context){
        mQueue = OpenCorporaRequestQueue.getInstance(context);
    }

    public ArrayList<TaskType> getTypes(String uid, String token){
        final String uidValue = uid;
        final String tokenValue = token;
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request =
                new JsonObjectRequest(Request.Method.GET ,TYPES_URL, future, future){
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("uid", uidValue);
                        params.put("token", tokenValue);
                        return params;
                    }
                };
        mQueue.getRequestQueue().add(request);
        ArrayList<TaskType> result = new ArrayList<>();
        try{
            JSONObject response = future.get(10, TimeUnit.SECONDS);
            JSONArray types = response.getJSONArray("items");
            for(int i = 0; i < types.length(); ++i){
                int id = types.getJSONObject(i).getInt("type_id");
                String name = types.getJSONObject(i).getString("name");
                int complexity = types.getJSONObject(i).getInt("complexity");
                TaskType type = new TaskType(id, name, complexity);
                result.add(type);
            }
            Log.i(LOG_TAG, "Receive " + types.length() + " items");
        } catch (InterruptedException | ExecutionException | TimeoutException | JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
