package com.alinc.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by alinc on 8/21/15.
 */
public class TodoItemDatabase extends SQLiteOpenHelper{

    private static TodoItemDatabase sInstance;
    private static final String DATABASE_NAME = "toDo.db";
    private static final int DATABASE_VERSION = 2;

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
                + KEY_CREATED_DATE + " DATE,"
                + KEY_DUE_DATE + " DATE,"
                + KEY_MODIFIED_DATE + " DATE"
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
        public Date dueDate;
        public Date modifiedDate;
        public Date createdDate;
        public String action;

    }

    public long addOrUpdateItem(ToDoItem toDoItem) {
        SQLiteDatabase db = getWritableDatabase();
        SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        long id = -1;
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_DESCRIPTION, toDoItem.description);
            values.put(KEY_PRIORITY, toDoItem.priority);
            values.put(KEY_DUE_DATE, toDate.format(toDoItem.dueDate));
            values.put(KEY_MODIFIED_DATE, toDate.format(new Date()));

            if(toDoItem.action.contentEquals("add")) {
                values.put(KEY_CREATED_DATE, toDate.format(new Date()));
                id = db.insertOrThrow(TABLE_TODO, null, values);
                db.setTransactionSuccessful();
            }
            else if (toDoItem.action.contentEquals("update")) {
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

    public void deleteItem(ToDoItem toDoItem) {
        SQLiteDatabase db = getWritableDatabase();
        System.out.print("To be deleted: " + toDoItem.description + " - " + toDoItem.id);
        db.beginTransaction();
        try {
            db.delete(TABLE_TODO, KEY_ID + " = ?", new String[]{Integer.toString(toDoItem.id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("ERROR: ", "Error while trying to delete item record. " + e);
        } finally {
            db.endTransaction();
        }
    }

    public List<ToDoItem> getAllItems() {
        List<ToDoItem> items = new ArrayList<>();

        String items_select_all = String.format("SELECT * FROM %s", TABLE_TODO);
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(items_select_all, null);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if(c.moveToFirst()) {
                do {
                    ToDoItem newItem = new ToDoItem();
                    newItem.description = c.getString(c.getColumnIndex(KEY_DESCRIPTION));
                    newItem.id = c.getInt(c.getColumnIndex(KEY_ID));
                    newItem.priority = c.getInt(c.getColumnIndex(KEY_PRIORITY));
                    newItem.modifiedDate = dateFormat.parse(c.getString(c.getColumnIndex(KEY_MODIFIED_DATE)));
                    newItem.dueDate = dateFormat.parse(c.getString(c.getColumnIndex(KEY_DUE_DATE)));
                    newItem.createdDate = dateFormat.parse(c.getString(c.getColumnIndex(KEY_CREATED_DATE)));
                    items.add(newItem);
                } while (c.moveToNext());
            }

        } catch (Exception e) {

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





