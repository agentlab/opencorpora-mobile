package org.opencorpora;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class OpenCorporaActivity extends Activity implements IAuthListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opencorpora_layout);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onSuccess() {
        TextView textView = (TextView) findViewById(R.id.text_view);
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        String token = prefs.getString("token", "not found :(");
        Log.d("prefs result", token);

        textView.setText(getString(R.string.TokenReceivedPrefix) + token);
        AuthHelper.getInstance().unSubscribe(this);
    }

    @Override
    public void onFail() {
        TextView textView = (TextView) findViewById(R.id.text_view);
        textView.setText(R.string.RequestFailed);
        AuthHelper.getInstance().unSubscribe(this);
    }

    public void OnClick(View view){
        AuthHelper.getInstance().authorize("login", "password", this);
    }
}
