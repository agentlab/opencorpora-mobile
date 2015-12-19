package org.opencorpora.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.opencorpora.InternalContract;
import org.opencorpora.data.SolvedTask;
import org.opencorpora.data.TaskType;
import org.opencorpora.data.api.OpenCorporaClient;
import org.opencorpora.data.dal.TasksQueryHelper;
import org.opencorpora.data.dal.TypesQueryHelper;

import java.io.IOException;
import java.util.ArrayList;

public class TaskSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "TaskSyncAdapter";

    private Context mContext;
    private TasksQueryHelper mTasksHelper;
    private TypesQueryHelper mTypesHelper;

    public TaskSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mTasksHelper = new TasksQueryHelper(context);
        mTypesHelper = new TypesQueryHelper(context);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.i(LOG_TAG, "Start sync");
        long startTime = System.currentTimeMillis();
        String token = null;
        try {
            token = AccountManager.get(mContext)
                    .blockingGetAuthToken(account, InternalContract.AUTH_TOKEN_TYPE, false);
            Log.i(LOG_TAG, "Sync started for " + account.name + ". With token " + token);
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            e.printStackTrace();
        }

        if(token == null){
            Log.w(LOG_TAG, "Sync failed. Unauthorized.");
            return;
        }

        OpenCorporaClient client = new OpenCorporaClient(mContext);
        ArrayList<TaskType> types = client.getTypes(account.name, token);
        mTypesHelper.updateTypes(types);

        sendCompleted();
        ArrayList<Integer> tasksIds = mTasksHelper.getTaskIdsForActualize();
        // ToDo: actualize tasksIds
        tasksIds.clear();                                   // stub
        ArrayList<Integer> old = new ArrayList<>(tasksIds); // stub
        mTasksHelper.removeTasksByIds(old);

        long diffTime = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Sync completed in " + diffTime);
    }

    public void sendCompleted() {
        ArrayList<SolvedTask> tasksForSend = mTasksHelper.getReadyTasks();
        boolean success = false;
        for (SolvedTask task : tasksForSend) {
            Log.d(LOG_TAG, "Send task with id:" + task.getId());
            // ToDo: implement logic for sending tasks to server
            // Logic for send task to server
            success = true;
        }

        if(success) {
            mTasksHelper.deleteCompletedTasks(tasksForSend);
        }

        Log.d(LOG_TAG, "Send ready tasks completed.");
    }
}
