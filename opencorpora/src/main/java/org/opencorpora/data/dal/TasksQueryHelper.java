package org.opencorpora.data.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import org.opencorpora.data.SolvedTask;
import org.opencorpora.data.Task;
import org.opencorpora.data.DatabaseHelper;
import org.opencorpora.data.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.opencorpora.data.DatabaseHelper.*;

public class TasksQueryHelper {
    private static String LOG_TAG = "TasksQueryHelper";
    private static final String SQL_GET_ALL_COMPLETED_TASKS = "SELECT "
            + COMPLETED_TASK_ID_COLUMN + ", "
            + COMPLETED_TASK_TYPE_COLUMN + ", "
            + COMPLETED_TASK_ANSWER_COLUMN + ", "
            + COMPLETED_TASK_SECONDS_COLUMN + ", "
            + COMPLETED_TASK_IS_LEFT_SHOWED_COLUMN + ", "
            + COMPLETED_TASK_IS_RIGHT_SHOWED_COLUMN + ", "
            + COMPLETED_TASK_IS_COMMENTED_COLUMN + ", "
            + COMPLETED_TASK_COMMENT_COLUMN + ", "
            + TASK_TYPE_NAME_COLUMN + ", "
            + TASK_TYPE_COMPLEXITY_COLUMN + " "
            + " FROM " + COMPLETED_TASK_TABLE_NAME
            + " JOIN " + TASK_TYPE_TABLE_NAME + " ON "
            + TASK_TYPE_ID_COLUMN + " = "
            + COMPLETED_TASK_TYPE_COLUMN;

    private DatabaseHelper mDbHelper;

    public TasksQueryHelper(Context context){
        mDbHelper = new DatabaseHelper(context);
    }

    public ArrayList<SolvedTask> getReadyTasks(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.rawQuery(SQL_GET_ALL_COMPLETED_TASKS, null);
        ArrayList<SolvedTask> result = new ArrayList<>();
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

        long diffTime = System.currentTimeMillis() - startTime;
        Log.d(LOG_TAG, "Tasks fetched: " + result.size() + ". Time(ms):" + diffTime);

        return result;
    }

    public ArrayList<Integer> getTaskIdsForActualize() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.query(TASK_TABLE_NAME,
                new String[]{TASK_ID_COLUMN},
                null, null, null, null, null);
        ArrayList<Integer> taskForActualize = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Bundle record = cursor.getExtras();
            taskForActualize.add(record.getInt(TASK_ID_COLUMN));
            cursor.moveToNext();
        }

        cursor.close();
        long diffTime = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Getting ids for actualize completed. Count: "
                + taskForActualize.size() + ". Time(ms): " + diffTime);
        return taskForActualize;
    }

    public void removeTasksByIds(ArrayList<Integer> tasks){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long startTime = System.currentTimeMillis();
        db.beginTransaction();
        for (Integer taskId
                : tasks){
            db.delete(TASK_TABLE_NAME,
                    TASK_TABLE_NAME + "." + TASK_ID_COLUMN + "=" + "?",
                    new String[]{ taskId.toString() });
        }
        db.endTransaction();

        long diffTime = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Removing not actual tasks by id completed. Count: "
                + tasks.size() + ". Time(ms): " + diffTime);
    }

    public void saveTasks(ArrayList<Task> tasks){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long startTime = System.currentTimeMillis();

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

        long diffTime = System.currentTimeMillis() - startTime;
        Log.d(LOG_TAG, "Saving complete. Count: " + tasks.size() + ". Time(ms): " + diffTime);
    }

    public void deleteCompletedTasks(ArrayList<SolvedTask> tasks) {
        Log.i(LOG_TAG, "Starting deletion completed tasks");
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        for (SolvedTask task:
                tasks) {
            String whereClause = COMPLETED_TASK_ID_COLUMN + " = ? ";
            Integer id = task.getId();

            db.delete(COMPLETED_TASK_TABLE_NAME, whereClause, new String[]{id.toString()});

            Log.i(LOG_TAG, "Task " + id + " deleted.");
        }
        db.endTransaction();
        long diffTime = System.currentTimeMillis() - startTime;
        Log.d(LOG_TAG, "Complete tasks deletion. Count: " + tasks.size() + ". Time(ms):" + diffTime);
    }
}
