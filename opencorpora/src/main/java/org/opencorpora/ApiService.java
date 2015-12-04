package org.opencorpora;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ApiService extends Service {

    final private String LOG_TAG = "[Service]";

    public ApiService() {
        Log.d(LOG_TAG, "In constructor");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "In onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
