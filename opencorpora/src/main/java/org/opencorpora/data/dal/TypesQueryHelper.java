package org.opencorpora.data.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import org.opencorpora.data.DatabaseHelper;
import org.opencorpora.data.TaskType;

import java.util.ArrayList;
import java.util.HashMap;

import static org.opencorpora.data.DatabaseHelper.TASK_TYPE_COMPLEXITY_COLUMN;
import static org.opencorpora.data.DatabaseHelper.TASK_TYPE_ID_COLUMN;
import static org.opencorpora.data.DatabaseHelper.TASK_TYPE_NAME_COLUMN;
import static org.opencorpora.data.DatabaseHelper.TASK_TYPE_TABLE_NAME;

public class TypesQueryHelper {
    private static final String LOG_TAG = "TypesQueryHelper";
    private static DatabaseHelper mDbHelper;

    public TypesQueryHelper(Context context){
        mDbHelper = new DatabaseHelper(context);
    }

    public void updateTypes(ArrayList<TaskType> types) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        /* We can not bulk insert with SQLiteDatabase object.*/
        long startTime = System.currentTimeMillis();
        db.beginTransaction();
        for(TaskType type : types){
            values.clear();
            values.put(TASK_TYPE_ID_COLUMN, type.getId());
            values.put(TASK_TYPE_NAME_COLUMN, type.getName());
            values.put(TASK_TYPE_COMPLEXITY_COLUMN, type.getComplexity());
            db.insertWithOnConflict(TASK_TYPE_TABLE_NAME,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.endTransaction();


        long timeDiff = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Types save completed. Count: " + types.size()
                + ". Time(ms): " + timeDiff);
    }

    public HashMap<Integer, TaskType> loadTypes(){
        HashMap<Integer, TaskType> result = new HashMap<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        long startTime = System.currentTimeMillis();
        Cursor cursor = db.query(TASK_TYPE_TABLE_NAME,
                new String[]{
                        TASK_TYPE_ID_COLUMN,
                        TASK_TYPE_NAME_COLUMN,
                        TASK_TYPE_COMPLEXITY_COLUMN
                }, null, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Bundle record = cursor.getExtras();
            int id = record.getInt(TASK_TYPE_ID_COLUMN);
            String name = record.getString(TASK_TYPE_NAME_COLUMN);
            int complexity = record.getInt(TASK_TYPE_COMPLEXITY_COLUMN);
            TaskType type = new TaskType(id, name, complexity);
            result.put(id, type);
        }
        cursor.close();

        long timeDiff = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Types load complete. Count: " + result.size()
                + ". Time(ms): " + timeDiff);

        return result;
    }
}
