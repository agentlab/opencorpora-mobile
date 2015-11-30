package org.opencorpora;

import android.app.Activity;
import android.os.Bundle;
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
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("Server address: " + BuildConfig.server_address);
    }
}
