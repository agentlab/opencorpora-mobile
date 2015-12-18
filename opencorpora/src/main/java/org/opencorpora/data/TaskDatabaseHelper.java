package org.opencorpora.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "opencorpora_db";

    /* tables */
    public static final String TASK_TYPE_TABLE_NAME = "task_type";
    public static final String TASK_TABLE_NAME = "task";
    public static final String CHOICE_TABLE_NAME = "choice";
    public static final String COMPLETED_TASK_TABLE_NAME = "completed_task";

    /* task_type columns */
    public static final String TASK_TYPE_ID_COLUMN = "id";
    public static final String TASK_TYPE_NAME_COLUMN = "name";
    public static final String TASK_TYPE_COMPLEXITY_COLUMN = "complexity";

    /* task columns */
    public static final String TASK_ID_COLUMN = "id";
    public static final String TASK_TYPE_COLUMN = "type_id";
    public static final String TASK_TARGET_COLUMN = "target";
    public static final String TASK_LEFT_CONTEXT_COLUMN = "left_context";
    public static final String TASK_RIGHT_CONTEXT_COLUMN = "right_context";
    public static final String TASK_HAS_INSTRUCTION_COLUMN = "has_instruction";

    /* choice columns */
    public static final String CHOICE_ID_COLUMN = "id";
    public static final String CHOICE_TASK_ID_COLUMN = "task_id";
    public static final String CHOICE_ANSWER_COLUMN = "answer";
    public static final String CHOICE_ANSWER_NUM_COLUMN = "answer_num";

    /* completed_task columns */
    public static final String COMPLETED_TASK_ID_COLUMN = "id";
    public static final String COMPLETED_TASK_TYPE_COLUMN = "type_id";
    public static final String COMPLETED_TASK_ANSWER_COLUMN = "answer";
    public static final String COMPLETED_TASK_SECONDS_COLUMN = "seconds_before_answer";
    public static final String COMPLETED_TASK_IS_LEFT_SHOWED_COLUMN = "is_left_context_showed";
    public static final String COMPLETED_TASK_IS_RIGHT_SHOWED_COLUMN = "is_right_context_showed";
    public static final String COMPLETED_TASK_IS_COMMENTED_COLUMN = "is_commented";
    public static final String COMPLETED_TASK_COMMENT_COLUMN = "comment_text";

    private static final String TASK_TYPE_TABLE_CREATE = "CREATE TABLE " + TASK_TYPE_TABLE_NAME
            + "(" + TASK_TYPE_ID_COLUMN + " INTEGER PRIMARY KEY " + ", "
            + TASK_TYPE_NAME_COLUMN + " TEXT " + ", "
            + TASK_TYPE_COMPLEXITY_COLUMN + "INTEGER" + ");";

    private static final String TASK_TABLE_CREATE = "CREATE TABLE " + TASK_TABLE_NAME + "("
            + TASK_ID_COLUMN + " INTEGER PRIMARY KEY " + ", "
            + TASK_TYPE_COLUMN + " INTEGER " + ", "
            + TASK_TARGET_COLUMN + " TEXT " + ", "
            + TASK_LEFT_CONTEXT_COLUMN + " TEXT " + ", "
            + TASK_RIGHT_CONTEXT_COLUMN + " TEXT " + ", "
            + TASK_HAS_INSTRUCTION_COLUMN + " BOOLEAN " + ", "
            + "FOREIGN KEY(" + TASK_TYPE_COLUMN + ") REFERENCES "
            + TASK_TYPE_TABLE_NAME + "(" + TASK_TYPE_ID_COLUMN + "));";

    private static final String CHOICE_TABLE_CREATE = "CREATE TABLE " + CHOICE_TABLE_NAME + "("
            + CHOICE_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT " + ", "
            + CHOICE_TASK_ID_COLUMN + " INTEGER " + ", "
            + CHOICE_ANSWER_COLUMN + " TEXT " + ", "
            + CHOICE_ANSWER_NUM_COLUMN + " INTEGER " + ", "
            + "FOREIGN KEY(" + CHOICE_TASK_ID_COLUMN + ") REFERENCES "
            + TASK_TABLE_NAME + "(" + TASK_ID_COLUMN + "));";

    private static final String COMPLETED_TASK_TABLE_CREATE =
            "CREATE TABLE " + COMPLETED_TASK_TABLE_NAME + "("
            + COMPLETED_TASK_ID_COLUMN + " INTEGER PRIMARY KEY " + ", "
            + COMPLETED_TASK_TYPE_COLUMN + " INTEGER " + ", "
            + COMPLETED_TASK_ANSWER_COLUMN + " TEXT " + ", "
            + COMPLETED_TASK_SECONDS_COLUMN + " INTEGER " + ", "
            + COMPLETED_TASK_IS_LEFT_SHOWED_COLUMN + " BOOLEAN " + ", "
            + COMPLETED_TASK_IS_RIGHT_SHOWED_COLUMN + " BOOLEAN " + ", "
            + COMPLETED_TASK_IS_COMMENTED_COLUMN + " BOOLEAN " + ", "
            + COMPLETED_TASK_COMMENT_COLUMN + "TEXT" + ", "
            + "FOREIGN KEY(" + COMPLETED_TASK_TYPE_COLUMN + ") REFERENCES "
            + TASK_TYPE_TABLE_NAME + "(" + TASK_TYPE_ID_COLUMN + "));";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TASK_TYPE_TABLE_CREATE);
        db.execSQL(TASK_TABLE_CREATE);
        db.execSQL(CHOICE_TABLE_CREATE);
        db.execSQL(COMPLETED_TASK_TABLE_CREATE);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
