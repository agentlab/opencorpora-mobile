package org.opencorpora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.opencorpora.data.TaskType;
import org.opencorpora.data.dal.TypesQueryHelper;

import java.util.HashMap;

public class TypesActivity extends Activity {
    private static final String LOG_TAG = "TypesActivity";

    private ListView mListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);

        mListView = (ListView) findViewById(R.id.types_list_view);

        TypesQueryHelper helper = new TypesQueryHelper(this);
        HashMap<Integer, TaskType> types = helper.loadTypes();

        ArrayAdapter<TaskType> adapter = new ArrayAdapter<>(this, R.layout.list_item);
        adapter.addAll(types.values());
        mListView.setAdapter(adapter);
        final Context context = this;
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(context, TasksActivity.class));
            }
        });
        Log.i(LOG_TAG, "Created");
    }
}
