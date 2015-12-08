package org.opencorpora;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Vector;

public class AuthHelper {
    final private String LOG_TAG = "[AuthHelper]";

    private Vector<IAuthListener> mListeners;
    private static class HelperHolder{
        public static final AuthHelper INSTANCE = new AuthHelper();
    }

    public AuthHelper(){
        mListeners = new Vector<>();
    }

    public static AuthHelper getInstance(){
        return HelperHolder.INSTANCE;
    }

    public void authorize(String login, String password, Activity context) {
        if (context instanceof IAuthListener) {
            subscribe((IAuthListener) context);
        }

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

    public void unsubscribe(IAuthListener listener){
        mListeners.remove(listener);
    }
}
