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

import java.io.IOException;

public class TaskSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "TaskSyncAdapter";
    private Context mContext;
    public TaskSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
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
                    .blockingGetAuthToken(account, "login_pass_auth", false);
            Log.i(LOG_TAG, "Sync started for " + account.name + ". With token " + token);
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            e.printStackTrace();
        }
        // ToDo: sync all

        Log.i(LOG_TAG, "Sync is completed");
    }
}
