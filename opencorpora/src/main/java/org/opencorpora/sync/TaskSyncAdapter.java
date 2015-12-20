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
import org.opencorpora.data.Task;
import org.opencorpora.data.TaskType;
import org.opencorpora.data.api.OpenCorporaClient;
import org.opencorpora.data.dal.TasksQueryHelper;
import org.opencorpora.data.dal.TypesQueryHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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

        HashMap<Integer, TaskType> oldTypes = mTypesHelper.loadTypes();
        Log.i(LOG_TAG, "Old types count = " + oldTypes.size() + ".");

        OpenCorporaClient client = new OpenCorporaClient(mContext);
        ArrayList<TaskType> types = client.getTypes(account.name, token);
        mTypesHelper.updateTypes(types);

        ArrayList<Integer> tasksIds = mTasksHelper.getTaskIdsForActualize();
        ArrayList<Integer> old = client.actualizeTasks(tasksIds, account.name, token);
        tasksIds.removeAll(old);
        mTasksHelper.removeTasksByIds(tasksIds);

        ArrayList<Task> tasks = client.getTasksByType(account.name, token, types.get(0));
        mTasksHelper.saveTasks(tasks);

        ArrayList<SolvedTask> readyTasks = mTasksHelper.getReadyTasks();
        SolvedTask ready = new SolvedTask(1, oldTypes.get(0));
        ready.setComment("comment");
        ready.setIsCommented(true);
        ready.setIsRightContextShowed(true);
        ready.setAnswer(1);
        ready.setSecondsBeforeAnswer(10);
        readyTasks.add(ready);
        boolean success = client.putReadyTasks(account.name, token, readyTasks);

        if(success) {
            mTasksHelper.deleteCompletedTasks(readyTasks);
        }

        long diffTime = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Sync completed in " + diffTime + " ms.");
    }
}
