package org.opencorpora;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
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
        mTextView.setText("None");
    }

    public void tryAuth(View view){
        Account[] accounts = AccountManager.get(this).getAccountsByType("org.opencorpora");
        mTextView.setText("Found: " + accounts.length + " opencorpora accounts.");
    }
}
