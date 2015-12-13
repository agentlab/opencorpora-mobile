package org.opencorpora;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class OpenCorporaActivity extends Activity {
    private TextView mTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opencorpora_layout);
        mTextView = (TextView) findViewById(R.id.text_view);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void onClick(View view){
        Account[] accounts = AccountManager.get(this).getAccountsByType("org.opencorpora");
        if(accounts.length > 0){
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            ContentResolver.requestSync(accounts[0], "org.opencorpora.task.provider", bundle);
            mTextView.setText("Sync is active? = " + ContentResolver.getSyncAdapterTypes().length);
        }
        else{
            mTextView.setText("Sync types count = " + ContentResolver.getSyncAdapterTypes().length);
        }
    }

    public void tryAuth(View view){
        Account[] accounts = AccountManager.get(this).getAccountsByType("org.opencorpora");
        mTextView.setText("Found: " + accounts.length + " opencorpora accounts.");
    }
}
