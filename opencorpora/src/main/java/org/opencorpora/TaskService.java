package org.opencorpora;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TaskService extends Service {
    private static final String LOG_TAG = "TaskService";

    private static TaskSyncAdapter sTaskSyncAdapter= null;
    public TaskService() {
        if(sTaskSyncAdapter == null){
            sTaskSyncAdapter = new TaskSyncAdapter(getApplicationContext(), true);
        }
        Log.d(LOG_TAG, "TaskService created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return sTaskSyncAdapter.getSyncAdapterBinder();
    }
}
