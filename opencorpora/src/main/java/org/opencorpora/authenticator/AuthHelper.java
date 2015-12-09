package org.opencorpora.authenticator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Vector;

public class AuthHelper {
    final private String LOG_TAG = "[AuthHelper]";

    final private Vector<IAuthListener> mListeners;
    private static class HelperHolder{
        public static final AuthHelper INSTANCE = new AuthHelper();
    }

    private AuthHelper(){
        mListeners = new Vector<>();
    }

    public static AuthHelper getInstance(){
        return HelperHolder.INSTANCE;
    }


    /**
     * @param username Имя пользователя
     * @param password Пароль
     * @param accountType Тип аккаунта (не используется)
     * @return Строка с токеном, либо null, если авторизоваться не удалось
     */
    public String signIn(String username, String password, String accountType){
        // Stub. Need synchronized query
        return "thisIsStubToken";
    }

    public void authorize(String login, String password, Activity context) {

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(LOG_TAG, "Broadcast message received");
                Log.i(LOG_TAG, "Success = " + intent.getBooleanExtra("isSuccess", false));
                if(intent.getBooleanExtra("isSuccess", false)){
                    String token = intent.getStringExtra("token");
                    Log.i(LOG_TAG, token);
                    for(IAuthListener listener : mListeners){
                        listener.onSuccess();
                    }
                }
                else{
                    for(IAuthListener listener : mListeners){
                        listener.onFail();
                    }
                }

                context.unregisterReceiver(this);
            }
        };

        IntentFilter  filter = new IntentFilter("My login action");
        context.registerReceiver(receiver, filter);

        context.startService(new Intent(context, AuthService.class)
                .putExtra("login", login)
                .putExtra("password", password));
    }

    public void subscribe(IAuthListener listener) {
        mListeners.add(listener);
    }

    public void unSubscribe(IAuthListener listener){
        mListeners.remove(listener);
    }
}
