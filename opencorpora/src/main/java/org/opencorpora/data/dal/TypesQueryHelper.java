package org.opencorpora.data.dal;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.opencorpora.data.DatabaseHelper;
import org.opencorpora.data.Task;
import org.opencorpora.data.TaskType;

import java.util.ArrayList;

import static org.opencorpora.data.DatabaseHelper.*;


public class TypesQueryHelper {
    private final String LOG_TAG = "TypesQueryHelper";
    private static DatabaseHelper mDbHelper;


    /**
     * @see <a href="http://stackoverflow.com/questions/3634984/insert-if-not-exists-else-update">
     *     about update if exists</a>
     */

    public TypesQueryHelper(Context context){
        mDbHelper = new DatabaseHelper(context);
    }

    public void updateTypes(ArrayList<TaskType> types) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        /* We can not bulk insert with SQLiteDatabase object.*/
        long startTime = System.currentTimeMillis();
        db.beginTransaction();
        for(TaskType type
                : types){
            values.clear();
            values.put(TASK_TYPE_ID_COLUMN, type.getId());
            values.put(TASK_TYPE_NAME_COLUMN, type.getName());
            values.put(TASK_TYPE_COMPLEXITY_COLUMN, type.getComplexity());
            db.insertWithOnConflict(TASK_TYPE_TABLE_NAME,
                    null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
        db.endTransaction();

        long timeDiff = System.currentTimeMillis() - startTime;
        Log.i(LOG_TAG, "Complete types save in " + timeDiff);
    }

    public ArrayList<TaskType> loadTypes(){
        return new ArrayList<>();
    }
}
