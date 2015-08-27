package com.alinc.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alinc on 8/21/15.
 */
public class TodoItemDatabase extends SQLiteOpenHelper{

    private static TodoItemDatabase sInstance;
    private static final String DATABASE_NAME = "toDo.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_TODO = "ACTIVITIES";
    private static final String KEY_DESCRIPTION = "DESCRIPTION";
    private static final String KEY_ID = "ID";
    private static final String KEY_PRIORITY = "PRIORITY";
    private static final String KEY_CREATED_DATE = "CREATED_DATE";
    private static final String KEY_DUE_DATE = "DUE_DATE";
    private static final String KEY_MODIFIED_DATE = "MODIFIED_DATE";

    public static synchronized TodoItemDatabase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TodoItemDatabase(context.getApplicationContext());
        }
        return sInstance;
    }

    private TodoItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TODO + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_PRIORITY + " INTEGER,"
                + KEY_CREATED_DATE + " INTEGER DEFAULT (strftime('%s','now')), "
                + KEY_DUE_DATE + " INTEGER DEFAULT (strftime('%s','now')), "
                + KEY_MODIFIED_DATE + " INTEGER DEFAULT (strftime('%s','now'))"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Notification: ", "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public class ToDoItem {
        public int id;
        public String description;
        public int priority;
        public long dueDate;
        public long modifiedDate;
        public long createdDate;
        public String action;

    }

    public long addOrUpdateItem(ToDoItem toDoItem) {
        SQLiteDatabase db = getWritableDatabase();
        long id = -1;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_DESCRIPTION, toDoItem.description);
            values.put(KEY_PRIORITY, toDoItem.priority);
            values.put(KEY_DUE_DATE, toDoItem.dueDate);
            values.put(KEY_MODIFIED_DATE, System.currentTimeMillis());

            if(toDoItem.action.contentEquals(CommonConstants.insertRecord)) {
                values.put(KEY_CREATED_DATE, System.currentTimeMillis());
                id = db.insertOrThrow(TABLE_TODO, null, values);
                db.setTransactionSuccessful();
            }
            else if (toDoItem.action.contentEquals(CommonConstants.updateRecord)) {
                id = db.update(TABLE_TODO, values, KEY_ID + " = ?", new String[] {Integer.toString(toDoItem.id)});
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("ERROR: ", "Error while trying to update table. Exception is: " + e);
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public boolean deleteItem(ToDoItem toDoItem) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TODO, KEY_ID + " = ?", new String[]{Integer.toString(toDoItem.id)});
            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            Log.d("ERROR: ", "Error while trying to delete item record. " + e);
        } finally {
            db.endTransaction();
        }

        return false;
    }

    public List<ToDoItem> getAllItems() {
        List<ToDoItem> items = new ArrayList<>();

        String items_select_all = String.format("SELECT * FROM %s", TABLE_TODO);
        SQLiteDatabase db = getReadableDatabase();
        if(db == null)
            return null;
        Cursor c = db.rawQuery(items_select_all, null);

        try {
            if(c.moveToFirst()) {
                do {
                    ToDoItem newItem = new ToDoItem();
                    newItem.description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                    newItem.id = c.getInt(c.getColumnIndex(KEY_ID));
                    newItem.priority = c.getInt(c.getColumnIndex(KEY_PRIORITY));
                    newItem.modifiedDate = c.getLong(c.getColumnIndex(KEY_MODIFIED_DATE));
                    newItem.dueDate = c.getLong(c.getColumnIndex(KEY_DUE_DATE));
                    newItem.createdDate = c.getLong(c.getColumnIndex(KEY_CREATED_DATE));
                    items.add(newItem);
                } while (c.moveToNext());
            }

        } catch (Exception e) {
            Log.d(getClass().getName(), "Exception caught while parsing cursor: " + e);
        } finally {
            if(c != null && !c.isClosed())
                c.close();
        }

        return items;
    }

    public ToDoItem getItem() {
        return new ToDoItem();
    }
}





