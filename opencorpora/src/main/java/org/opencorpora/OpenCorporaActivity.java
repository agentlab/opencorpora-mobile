package org.opencorpora;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.opencorpora.model.TopUsersList;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenCorporaActivity extends Activity {
    private static final String SERVER_URL = "http://agentlab.ru/tools/opencorpora/statistics";

    private TextView mTextView;
    private TextView mResultsOutputTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opencorpora_layout);
        loadItems();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onClick(View view) {
        Account[] accounts
                = AccountManager.get(this).getAccountsByType(InternalContract.ACCOUNT_TYPE);
        if(accounts.length > 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(accounts[0],
                    InternalContract.TASK_PROVIDER_AUTHORITY,
                    bundle);
            mTextView.setText("Sync is active? = " + ContentResolver.getSyncAdapterTypes().length);
        }
        else {
            mTextView.setText("Sync types count = " + ContentResolver.getSyncAdapterTypes().length);
        }
    }

    public void tryAuth(View view) {
        Account[] accounts =
                AccountManager.get(this).getAccountsByType(InternalContract.ACCOUNT_TYPE);
        mTextView.setText("Found: " + accounts.length + " opencorpora accounts.");
    }

    public void onLoadRecordsClick(View view) {
        String result = getData(SERVER_URL);
        if (result != null) {
            TopUsersList topUsersList = new GsonBuilder().create().fromJson(result, TopUsersList.class);
            mResultsOutputTextView.setText(topUsersList.getUsers().toString());
        }
        else {
            mResultsOutputTextView.setText(R.string.outputText_Error);
        }
    }

    private void loadItems() {
        mTextView = (TextView) findViewById(R.id.text_view);
        mResultsOutputTextView = (TextView) findViewById(R.id.recordsOutputTextView);
    }

    private String getData(String urlString)
    {
        try {
            return new GetDataTask().execute(urlString).get();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return null;
    }

    private static class GetDataTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urlStings) {
            try {
                URL url = new URL(urlStings[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();

                InputStream in = new BufferedInputStream(connection.getInputStream());

                return IOUtils.toString(in);
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }

            return null;
        }
    }
}
