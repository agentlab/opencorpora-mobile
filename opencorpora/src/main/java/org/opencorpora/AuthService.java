package org.opencorpora;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class AuthService extends Service {

    final private String LOG_TAG = "[Service]";

    public AuthService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "Try auth in api");
        auth();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void auth(){
        Log.d(LOG_TAG, "Try auth");
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.add("login", "vasya");
        params.add("password", "qwerty");
        client.post(BuildConfig.server_address + "api/login.php",
                params,
                new AuthRequestHandler(getSharedPreferences("AuthPrefs", MODE_APPEND)));
    }
}
