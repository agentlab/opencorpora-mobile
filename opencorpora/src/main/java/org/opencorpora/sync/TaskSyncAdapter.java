package org.opencorpora.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import org.opencorpora.InternalContract;
import org.opencorpora.data.SolvedTask;
import org.opencorpora.data.Task;
import org.opencorpora.data.TaskDatabaseHelper;
import org.opencorpora.data.TaskType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.opencorpora.data.TaskDatabaseHelper.*;

public class TaskSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String LOG_TAG = "TaskSyncAdapter";
    private Context mContext;
    private TaskDatabaseHelper mDbHelper;
    public TaskSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        mDbHelper = new TaskDatabaseHelper(context);
    }

    private static final String COMMA = ", ";
    private static final String SQL_GET_ALL_COMPLETED_TASKS = "SELECT "
            + COMPLETED_TASK_ID_COLUMN + COMMA
            + COMPLETED_TASK_TYPE_COLUMN + COMMA
            + COMPLETED_TASK_ANSWER_COLUMN + COMMA
            + COMPLETED_TASK_SECONDS_COLUMN + COMMA
            + COMPLETED_TASK_IS_LEFT_SHOWED_COLUMN + COMMA
            + COMPLETED_TASK_IS_RIGHT_SHOWED_COLUMN + COMMA
            + COMPLETED_TASK_IS_COMMENTED_COLUMN + COMMA
            + COMPLETED_TASK_COMMENT_COLUMN + COMMA
            + TASK_TYPE_NAME_COLUMN + COMMA
            + TASK_TYPE_COMPLEXITY_COLUMN + " "
            + " FROM " + COMPLETED_TASK_TABLE_NAME
            + " JOIN " + TASK_TYPE_TABLE_NAME + " ON "
            + TASK_TYPE_ID_COLUMN + " = "
            + COMPLETED_TASK_TYPE_COLUMN;

    private static final String SQL_GET_TASKS_BY_TYPE = "";

    private static final String SQL_INSERT_NEW_TASKS = "";

    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.i(LOG_TAG, "Start sync");
        try {
            String token = AccountManager.get(mContext)
                    .blockingGetAuthToken(account, InternalContract.AUTH_TOKEN_TYPE, false);
            Log.i(LOG_TAG, "Sync started for " + account.name + ". With token " + token);
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            e.printStackTrace();
        }

        sendCompleted();
        actualizeOldTasks();

        Log.i(LOG_TAG, "Sync is completed");
    }

    private void sendCompleted() {
        ArrayList<SolvedTask> tasksForSend = getReadyTasks();
        boolean success = false;
        for (SolvedTask task:
                tasksForSend) {
            Log.d(LOG_TAG, "Send task with id:" + task.getId());
            // ToDo: implement logic for sending tasks to server
            // Logic for send task to server
            success = true;
        }

        if(success){
            deleteCompletedTasks(tasksForSend);
        }

        Log.d(LOG_TAG, "Send sync complete.");
    }

    private void actualizeOldTasks() {

    }

    private void deleteCompletedTasks(ArrayList<SolvedTask> tasks) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        for (SolvedTask task:
                tasks) {
            String whereClause = COMPLETED_TASK_ID_COLUMN + " = ? ";
            Integer id = task.getId();
            db.delete(COMPLETED_TASK_TABLE_NAME, whereClause, new String[] { id.toString() });
            Log.i(LOG_TAG, "Task " + id + " deleted.");
        }

        Log.d(LOG_TAG, "Complete tasks deletion. Count: " + tasks.size());
    }

    private ArrayList<SolvedTask> getReadyTasks(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SQL_GET_ALL_COMPLETED_TASKS, null);
        ArrayList<SolvedTask> result = new ArrayList<>(cursor.getCount());
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Bundle record = cursor.getExtras();

            TaskType type = new TaskType(
                    record.getInt(COMPLETED_TASK_TYPE_COLUMN),
                    record.getString(TASK_TYPE_NAME_COLUMN),
                    record.getInt(TASK_TYPE_COMPLEXITY_COLUMN));

            SolvedTask task = new SolvedTask(record
                    .getInt(COMPLETED_TASK_ID_COLUMN), type);

            task.setAnswer(record.getInt(COMPLETED_TASK_ANSWER_COLUMN));
            task.setSecondsBeforeAnswer(record.getInt(COMPLETED_TASK_SECONDS_COLUMN));
            task.setIsLeftContextShowed(record.getBoolean(COMPLETED_TASK_IS_LEFT_SHOWED_COLUMN));
            task.setIsRightContextShowed(record.getBoolean(COMPLETED_TASK_IS_RIGHT_SHOWED_COLUMN));
            task.setIsCommented(record.getBoolean(COMPLETED_TASK_IS_COMMENTED_COLUMN));
            task.setComment(record.getString(COMPLETED_TASK_SECONDS_COLUMN));

            result.add(task);

            cursor.moveToNext();
        }

        cursor.close();

        Log.d(LOG_TAG, "Tasks fetched: " + result.size());

        return result;
    }

    private void saveTasks(ArrayList<Task> tasks){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        for (Task task :
                tasks) {
            ContentValues values = new ContentValues();
            values.put(TASK_ID_COLUMN, task.getId());
            values.put(TASK_TYPE_COLUMN, task.getType().getId());
            values.put(TASK_TARGET_COLUMN, task.getTarget());
            values.put(TASK_LEFT_CONTEXT_COLUMN, task.getLeftContext());
            values.put(TASK_RIGHT_CONTEXT_COLUMN, task.getRightContext());
            values.put(TASK_HAS_INSTRUCTION_COLUMN, task.hasInstruction());
            db.insert(TASK_TABLE_NAME, null, values);

            HashMap<Integer, String> choices = task.getChoices();
            for (Map.Entry<Integer, String> choice:
                    choices.entrySet()) {
                ContentValues choiceValues = new ContentValues();
                choiceValues.put(CHOICE_TASK_ID_COLUMN, task.getId());
                choiceValues.put(CHOICE_ANSWER_NUM_COLUMN, choice.getKey());
                choiceValues.put(CHOICE_ANSWER_COLUMN, choice.getValue());
                db.insert(CHOICE_TABLE_NAME, null, choiceValues);
            }

            Log.d(LOG_TAG, "Task " + task.getId() + " successfully saved");
        }

        Log.d(LOG_TAG, "Saving complete. Count: " + tasks.size());
    }
}
