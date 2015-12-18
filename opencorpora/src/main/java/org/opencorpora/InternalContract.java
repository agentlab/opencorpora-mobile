package org.opencorpora;

import android.accounts.AccountManager;

public class InternalContract {
    public static final String TASK_PROVIDER_AUTHORITY = "org.opencorpora.task.provider";
    public static final String ACCOUNT_TYPE = "org.opencorpora";
    public static final String AUTH_TOKEN_TYPE = "opencorpora_token";
    public static final String AUTH_TYPE = "login_pass_auth";

    /* Keys for key-values objects (Bundle, Intent) */
    public static final String KEY_ACCOUNT_AUTHENTICATOR_RESPONSE
                                = AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
    public static final String KEY_ACCOUNT_NAME = AccountManager.KEY_ACCOUNT_NAME;
    public static final String KEY_ACCOUNT_TYPE =  AccountManager.KEY_ACCOUNT_TYPE;
    public static final String KEY_AUTH_TOKEN = AccountManager.KEY_AUTHTOKEN;
    public static final String KEY_AUTH_TYPE = "auth_type";
    public static final String KEY_INTENT = AccountManager.KEY_INTENT;
    public static final String KEY_IS_ADDING_NEW_ACCOUNT = "is_adding_new_account";
    public static final String KEY_USER_PASSWORD = "user_password";
}
