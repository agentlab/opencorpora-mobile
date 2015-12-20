package org.opencorpora;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
    private TypesQueryHelper mTypesHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_types);
        mTypesHelper = new TypesQueryHelper(this);
        mListView = (ListView) findViewById(R.id.types_list_view);

        LoadTypesTask loadTypesTask = new LoadTypesTask();
        loadTypesTask.execute(this);

        final Context context = this;
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, TasksActivity.class);
                TaskType selectedType = (TaskType) parent.getItemAtPosition(position);
                intent.putExtra(TasksActivity.TYPE_ID_KEY, selectedType.getId());
                intent.putExtra(TasksActivity.TYPE_NAME_KEY, selectedType.getName());
                intent.putExtra(TasksActivity.TYPE_COMPLEXITY_KEY, selectedType.getComplexity());
                startActivity(intent);
            }
        });
        Log.i(LOG_TAG, "Created");
    }

    private class LoadTypesTask extends AsyncTask<Context, Void, ArrayAdapter<TaskType>> {
        @Override
        protected ArrayAdapter<TaskType> doInBackground(Context... params) {
            HashMap<Integer, TaskType> types = mTypesHelper.loadTypes();
            Context context = params[0];
            ArrayAdapter<TaskType> adapter = new ArrayAdapter<>(context, R.layout.list_item);
            adapter.addAll(types.values());
            return adapter;
        }

        @Override
        protected void onPostExecute(ArrayAdapter<TaskType> adapter) {
            super.onPostExecute(adapter);
            mListView.setAdapter(adapter);
        }
    }
}
