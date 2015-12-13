package org.opencorpora.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TaskService extends Service {
    private static final String LOG_TAG = "TaskService";

    private static final Object sSyncAdapterLock = new Object();
    private static TaskSyncAdapter sTaskSyncAdapter= null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sTaskSyncAdapter == null)
                sTaskSyncAdapter = new TaskSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return sTaskSyncAdapter.getSyncAdapterBinder();
    }
}
