package org.opencorpora.authenticator;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import org.opencorpora.InternalContract;


public class Authenticator extends AbstractAccountAuthenticator {
    private static final String LOG_TAG = "Authenticator";
    private final Context mContext;

    private final AuthHelper mAuthService;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
        mAuthService = new AuthHelper();
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.d(LOG_TAG, "editProperties call");
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response,
                             String accountType,
                             String authTokenType,
                             String[] requiredFeatures,
                             Bundle options) throws NetworkErrorException {
        Log.d(LOG_TAG, "addAccount call");
        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(InternalContract.ACCOUNT_TYPE, accountType);
        intent.putExtra(InternalContract.AUTH_TYPE, authTokenType);
        intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response,
                                     Account account,
                                     Bundle options) throws NetworkErrorException {
        Log.d(LOG_TAG, "confirmCredentials call");
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response,
                               Account account,
                               String authTokenType,
                               Bundle options) throws NetworkErrorException {
        Log.d(LOG_TAG, "getAuthToken call");
        final AccountManager manager = AccountManager.get(mContext);

        String authToken = manager.peekAuthToken(account, authTokenType);

        if(TextUtils.isEmpty(authToken)){
            final String password = manager.getPassword(account);

            if(password != null){
                authToken = mAuthService.signIn(account.name, password);
            }
        }

        if(!TextUtils.isEmpty(authToken)){
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        final Intent intent = new Intent(mContext, AuthenticatorActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_NAME, account.type);
        intent.putExtra(InternalContract.AUTH_TYPE, authTokenType);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);

        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        Log.d(LOG_TAG, "getAuthTokenLabel call");
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response,
                                    Account account,
                                    String authTokenType,
                                    Bundle options) throws NetworkErrorException {
        Log.d(LOG_TAG, "updateCredentials call");
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response,
                              Account account,
                              String[] features) throws NetworkErrorException {
        Log.d(LOG_TAG, "hasFeatures call");
        return null;
    }
}
