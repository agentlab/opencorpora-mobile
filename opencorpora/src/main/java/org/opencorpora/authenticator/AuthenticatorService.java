package org.opencorpora.authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.opencorpora.authenticator.Authenticator;

public class AuthenticatorService extends Service {
    public AuthenticatorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Authenticator authenticator = new Authenticator(this);
        return authenticator.getIBinder();
    }
}
