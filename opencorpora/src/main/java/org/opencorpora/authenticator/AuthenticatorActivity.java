package org.opencorpora.authenticator;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.opencorpora.InternalContract;
import org.opencorpora.R;

public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    private AuthHelper mServerAuthenticate;
    private AccountManager mAccountManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);
        mAccountManager = AccountManager.get(this);
        mServerAuthenticate = new AuthHelper();
    }

    public void onClick(View view){
        submit();
    }

    public void submit() {
        final String userName = ((TextView) findViewById(R.id.accountName)).getText().toString();
        final String userPass =
                ((TextView) findViewById(R.id.accountPassword)).getText().toString();
        new AsyncTask<Void, Void, Intent>() {
            @Override
            protected Intent doInBackground(Void... params) {
                String authToken = mServerAuthenticate.signIn(userName, userPass);
                final Intent res = new Intent();
                res.putExtra(InternalContract.KEY_ACCOUNT_NAME, userName);
                res.putExtra(InternalContract.KEY_ACCOUNT_TYPE, InternalContract.ACCOUNT_TYPE);
                res.putExtra(InternalContract.KEY_AUTH_TOKEN, authToken);
                res.putExtra(InternalContract.KEY_USER_PASSWORD, userPass);
                return res;
            }
            @Override
            protected void onPostExecute(Intent intent) {
                finishLogin(intent);
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        String accountName = intent.getStringExtra(InternalContract.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(InternalContract.KEY_USER_PASSWORD);
        final Account account = 
                new Account(accountName, intent.getStringExtra(InternalContract.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(InternalContract.KEY_IS_ADDING_NEW_ACCOUNT, false)) {
            String authToken = intent.getStringExtra(InternalContract.KEY_AUTH_TOKEN);
            String authTokenType = InternalContract.AUTH_TOKEN_TYPE;

            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, authTokenType, authToken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
