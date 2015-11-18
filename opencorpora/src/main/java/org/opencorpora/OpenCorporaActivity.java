package org.opencorpora;

import org.joda.time.LocalTime;

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
        LocalTime currentTime = new LocalTime();
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText("The current local time is: " + currentTime);
    }
}
