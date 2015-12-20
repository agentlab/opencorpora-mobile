package org.opencorpora;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;

import org.opencorpora.data.Task;
import org.opencorpora.data.TaskType;
import org.opencorpora.data.dal.TasksQueryHelper;

import java.util.ArrayList;

public class TasksActivity extends Activity {
    private static final String LOG_TAG = "TasksActivity";

    private TasksQueryHelper mTasksHelper;
    private TaskType mTaskType;
    private ArrayList<Task> mTasks;

    public static String TYPE_ID_KEY = "type_id";
    public static String TYPE_NAME_KEY = "type_name";
    public static String TYPE_COMPLEXITY_KEY = "type_complexity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        mTasksHelper = new TasksQueryHelper(this);
        Intent intent = getIntent();
        int typeId = intent.getIntExtra(TYPE_ID_KEY, -1);
        String typeName = intent.getStringExtra(TYPE_NAME_KEY);
        int typeComplexity = intent.getIntExtra(TYPE_COMPLEXITY_KEY, -1);
        if(typeId == -1 || typeComplexity == -1){
            Log.e(LOG_TAG, "Wrong type parameter in intent.");
            return;
        }

        mTaskType = new TaskType(typeId, typeName, typeComplexity);
        LoadTasks loadTask = new LoadTasks();
        loadTask.execute();
        Log.i(LOG_TAG, "Created");
    }

    private class LoadTasks extends AsyncTask<Void, Void, ArrayList<Task>>{
        @Override
        protected ArrayList<Task> doInBackground(Void... params) {
            return mTasksHelper.getTasksByType(mTaskType);
        }

        @Override
        protected void onPostExecute(ArrayList<Task> tasks) {
            super.onPostExecute(tasks);
            mTasks = tasks;
            Log.i(LOG_TAG, "Tasks load complete. Count: " + tasks.size());
            if(mTasks.size() != 0) {
                setTaskByPosition(0);
            }
        }
    }

    private void setTaskByPosition(int position){

    }
}
