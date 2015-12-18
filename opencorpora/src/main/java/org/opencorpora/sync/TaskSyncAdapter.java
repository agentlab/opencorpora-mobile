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
import org.opencorpora.data.dal.TasksQueryHelper;

import java.io.IOException;

public class TaskSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "TaskSyncAdapter";
    private Context mContext;
    private TasksQueryHelper mTasksHelper;
    public TaskSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mTasksHelper = new TasksQueryHelper(context);
    }

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.i(LOG_TAG, "Start sync");
        try {
            String token = AccountManager.get(mContext)
                    .blockingGetAuthToken(account, InternalContract.AUTH_TOKEN_TYPE, false);
            Log.i(LOG_TAG, "Sync started for " + account.name + ". With token " + token);
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            e.printStackTrace();
        }

        mTasksHelper.sendCompleted();
        mTasksHelper.getTaskIdsForActualize();

        Log.i(LOG_TAG, "Sync is completed");
    }




}
