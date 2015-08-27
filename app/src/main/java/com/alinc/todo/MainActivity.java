package com.alinc.todo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoItemDatabase.ToDoItem> todoList;
    private ToDoItemAdapter todoAdapter;

    private ListView lvItems;
    private static final int REQUEST_CODE = 200;
    private TodoItemDatabase db;
    //public static final String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.cp_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        lvItems = (ListView) findViewById(R.id.lvItems);
        readItems();
        todoAdapter = new ToDoItemAdapter(this, android.R.layout.simple_list_item_1, todoList);
        lvItems.setAdapter(todoAdapter);

        setupListViewListener();
        setupEditListener();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        boolean deleted = db.deleteItem(todoList.get(pos));
                        if(deleted) {
                            todoList.remove(pos);
                            todoAdapter.notifyDataSetChanged();
                            return true;
                        }
                        Log.d(getClass().getName(), "Failed to delete item!");
                        return false;
                    }
                }
        );
    }

    private void setupEditListener() {
        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                        i.putExtra("pos", pos);
                        i.putExtra("id", id);
                        i.putExtra("itemText", todoList.get(pos).description);
                        i.putExtra("itemId", Integer.toString(todoList.get(pos).id));
                        i.putExtra("priority", Integer.toString(todoList.get(pos).priority));
                        i.putExtra("dueDate", todoList.get(pos).dueDate);
                        startActivityForResult(i, REQUEST_CODE);
                    }
                }
        );
    }

    private void readItems() {
        try {
            db = TodoItemDatabase.getInstance(this);
        } catch (Exception e) {
            Log.d(getClass().getName(), "Unable to get database instance!");
        }
        if(db != null) {
            todoList = new ArrayList<TodoItemDatabase.ToDoItem>();
            todoList.addAll(db.getAllItems());
        }
        else {
            todoList = new ArrayList<TodoItemDatabase.ToDoItem>();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        TodoItemDatabase.ToDoItem newItem = db.getItem();
        etNewItem.setText("");
        newItem.description = itemText;
        newItem.priority = CommonConstants.STANDARD_PRIORITY;
        newItem.action = CommonConstants.insertRecord;
        newItem.dueDate = System.currentTimeMillis();
        long id = db.addOrUpdateItem(newItem);
        if(id > -1) {
            todoAdapter.add(newItem);
            todoAdapter.notifyDataSetChanged();
        }
        else {
            Log.d(getClass().getName(), "Unable to add item.");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            TodoItemDatabase.ToDoItem editItem = db.getItem();
            editItem.description = data.getExtras().getString("updateItemText");
            editItem.priority = data.getExtras().getInt("priority");
            editItem.id = Integer.parseInt(data.getExtras().getString("itemId").toString());
            editItem.dueDate = data.getExtras().getLong("dueDate");
            editItem.action = CommonConstants.updateRecord;
            todoList.set(data.getExtras().getInt("position"), editItem);
            todoAdapter.notifyDataSetChanged();
            db.addOrUpdateItem(editItem);
        }
    }
}
