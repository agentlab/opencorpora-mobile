package org.opencorpora.data.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.opencorpora.data.DatabaseHelper;
import org.opencorpora.data.SolvedTask;
import org.opencorpora.data.Task;
import org.opencorpora.data.TaskType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.opencorpora.data.DatabaseHelper.*;


public class TasksQueryHelper {
    private static String LOG_TAG = "TasksQueryHelper";
    private static final String SQL_GET_ALL_COMPLETED_TASKS = "SELECT "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_ID_COLUMN + ", "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_TYPE_COLUMN + ", "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_ANSWER_COLUMN + ", "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_SECONDS_COLUMN + ", "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_IS_LEFT_SHOWED_COLUMN + ", "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_IS_RIGHT_SHOWED_COLUMN + ", "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_IS_COMMENTED_COLUMN + ", "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_COMMENT_COLUMN + ", "
            + TASK_TYPE_TABLE_NAME + "." + TASK_TYPE_NAME_COLUMN + ", "
            + TASK_TYPE_TABLE_NAME + "." + TASK_TYPE_COMPLEXITY_COLUMN + " "
            + " FROM " + COMPLETED_TASK_TABLE_NAME
            + " JOIN " + TASK_TYPE_TABLE_NAME + " ON "
            + TASK_TYPE_TABLE_NAME + "." + TASK_TYPE_ID_COLUMN + " = "
            + COMPLETED_TASK_TABLE_NAME + "." + COMPLETED_TASK_TYPE_COLUMN;

    private DatabaseHelper mDbHelper;

    public TasksQueryHelper(Context context) {
        mDbHelper = new DatabaseHelper(context);
    }

    public ArrayList<SolvedTask> getReadyTasks() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.rawQuery(SQL_GET_ALL_COMPLETED_TASKS, null);
        ArrayList<SolvedTask> result = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int type_id = cursor.getInt(cursor.getColumnIndex(COMPLETED_TASK_TYPE_COLUMN));
                    String type_name = cursor.getString(
                            cursor.getColumnIndex(TASK_TYPE_NAME_COLUMN));
                    int type_complexity = cursor.getInt(
                            cursor.getColumnIndex(TASK_TYPE_COMPLEXITY_COLUMN));
                    TaskType type = new TaskType(type_id, type_name, type_complexity);

                    int id = cursor.getInt(cursor.getColumnIndex(COMPLETED_TASK_ID_COLUMN));
                    SolvedTask task = new SolvedTask(id, type);

                    task.setAnswer(cursor.getInt(
                            cursor.getColumnIndex(COMPLETED_TASK_ANSWER_COLUMN)));

                    task.setSecondsBeforeAnswer(cursor.getInt(
                            cursor.getColumnIndex(COMPLETED_TASK_SECONDS_COLUMN)));

                    task.setIsLeftContextShowed(cursor.getInt(
                            cursor.getColumnIndex(COMPLETED_TASK_IS_LEFT_SHOWED_COLUMN)) > 0);

                    task.setIsRightContextShowed(cursor.getInt(
                            cursor.getColumnIndex(COMPLETED_TASK_IS_RIGHT_SHOWED_COLUMN)) > 0);

                    task.setIsCommented(cursor.getInt(
                            cursor.getColumnIndex(COMPLETED_TASK_IS_COMMENTED_COLUMN)) > 0);

                    task.setComment(cursor.getString(
                            cursor.getColumnIndex(COMPLETED_TASK_SECONDS_COLUMN)));

                    result.add(task);
                } while(cursor.moveToNext());
            }

            cursor.close();
        }

        long diffTime = System.currentTimeMillis() - startTime;
        Log.d(LOG_TAG, "Tasks fetched: " + result.size() + ". Time(ms):" + diffTime);

        return result;
    }

    public ArrayList<Integer> getTaskIdsForActualize() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        long startTime = System.currentTimeMillis();
        Cursor cursor = db.query(TASK_TABLE_NAME,
                new String[] { TASK_ID_COLUMN },
                null, null, null, null, null);
        ArrayList<Integer> taskForActualize = new ArrayList<>();

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(TASK_ID_COLUMN));
                    taskForActualize.add(id);
                } while(cursor.moveToNext());
            }

            cursor.close();
        }

        long diffTime = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Getting ids for actualize completed. Count: "
                + taskForActualize.size() + ". Time(ms): " + diffTime);
        return taskForActualize;
    }

    public void removeTasksByIds(ArrayList<Integer> tasks) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long startTime = System.currentTimeMillis();
        db.beginTransaction();
        for (Integer taskId : tasks) {
            db.delete(TASK_TABLE_NAME,
                    TASK_TABLE_NAME + "." + TASK_ID_COLUMN + "=" + "?",
                    new String[] { taskId.toString() });
        }
        db.setTransactionSuccessful();
        db.endTransaction();

        long diffTime = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Removing not actual tasks by id completed. Count: "
                + tasks.size() + ". Time(ms): " + diffTime);
    }

    public void saveTasks(ArrayList<Task> tasks) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long startTime = System.currentTimeMillis();

        db.beginTransaction();
        for (Task task : tasks) {
            db.beginTransaction();
            ContentValues values = new ContentValues();
            values.put(TASK_ID_COLUMN, task.getId());
            values.put(TASK_TYPE_COLUMN, task.getType().getId());
            values.put(TASK_TARGET_COLUMN, task.getTarget());
            values.put(TASK_LEFT_CONTEXT_COLUMN, task.getLeftContext());
            values.put(TASK_RIGHT_CONTEXT_COLUMN, task.getRightContext());
            values.put(TASK_HAS_INSTRUCTION_COLUMN, task.hasInstruction());
            db.insert(TASK_TABLE_NAME, null, values);

            HashMap<Integer, String> choices = task.getChoices();
            for (Map.Entry<Integer, String> choice : choices.entrySet()) {
                ContentValues choiceValues = new ContentValues();
                choiceValues.put(CHOICE_TASK_ID_COLUMN, task.getId());
                choiceValues.put(CHOICE_ANSWER_NUM_COLUMN, choice.getKey());
                choiceValues.put(CHOICE_ANSWER_COLUMN, choice.getValue());
                db.insert(CHOICE_TABLE_NAME, null, choiceValues);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
            Log.d(LOG_TAG, "Task " + task.getId() + " successfully saved");
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        long diffTime = System.currentTimeMillis() - startTime;
        Log.d(LOG_TAG, "Saving complete. Count: " + tasks.size() + ". Time(ms): " + diffTime);
    }

    public void deleteCompletedTasks(ArrayList<SolvedTask> tasks) {
        Log.i(LOG_TAG, "Starting deletion completed tasks");
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.beginTransaction();
        for (SolvedTask task : tasks) {
            String whereClause = COMPLETED_TASK_ID_COLUMN + " = ? ";
            Integer id = task.getId();

            db.delete(COMPLETED_TASK_TABLE_NAME, whereClause, new String[] { id.toString() });

            Log.i(LOG_TAG, "Task " + id + " deleted.");
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        long diffTime = System.currentTimeMillis() - startTime;
        Log.d(LOG_TAG,
                "Complete tasks deletion. Count: " + tasks.size() + ". Time(ms):" + diffTime);
    }

    public ArrayList<Task> getTasksByType(TaskType type) {
        ArrayList<Task> result = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(TASK_TABLE_NAME,
                new String[] {
                        TASK_ID_COLUMN,
                        TASK_TARGET_COLUMN,
                        TASK_LEFT_CONTEXT_COLUMN,
                        TASK_RIGHT_CONTEXT_COLUMN,
                        TASK_HAS_INSTRUCTION_COLUMN
                },
                TASK_TYPE_COLUMN + " = " + "?",
                new String[] {
                        Integer.toString(type.getId())
                },
                null,
                null,
                null);

        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(TASK_ID_COLUMN));
                    String target = cursor.getString(cursor.getColumnIndex(TASK_TARGET_COLUMN));
                    String leftContext = cursor.getString(
                            cursor.getColumnIndex(TASK_LEFT_CONTEXT_COLUMN));
                    String rightContext = cursor.getString(
                            cursor.getColumnIndex(TASK_RIGHT_CONTEXT_COLUMN));
                    boolean hasInstruction = cursor.getInt(
                            cursor.getColumnIndex(TASK_HAS_INSTRUCTION_COLUMN)) > 0;
                    result.add(new Task(id, type, target, leftContext,
                                        rightContext, hasInstruction));
                } while(cursor.moveToNext());
            }

            cursor.close();
        }

        long diffTime = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Task by type receiving completed. Count: "
                + result.size() + " . Time(ms): " + diffTime);
        return result;
    }
}
