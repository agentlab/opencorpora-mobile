package org.opencorpora;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


public class OpenCorporaActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opencorpora_layout);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", "not found :(");
        Log.d("prefs result", token);
        TextView textView = (TextView) findViewById(R.id.text_view);
        startService(new Intent(this, AuthService.class));

        textView.setText("token: " + token);
    }
}
