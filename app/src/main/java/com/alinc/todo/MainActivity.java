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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoItemDatabase.ToDoItem> todoList;
    private ToDoItemAdapter todoAdapter;

    private ListView lvItems;
    private static final int REQUEST_CODE = 200;
    private TodoItemDatabase db;

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
                        db.deleteItem(todoList.get(pos));
                        todoList.remove(pos);
                        todoAdapter.notifyDataSetChanged();
                        return true;
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
                        startActivityForResult(i, REQUEST_CODE);
                    }
                }
        );
    }

    private void readItems() {
        try {
            db = TodoItemDatabase.getInstance(this);
            todoList = new ArrayList<TodoItemDatabase.ToDoItem>();
            todoList.addAll(db.getAllItems());
        } catch (Exception e) {
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
        Date today = Calendar.getInstance().getTime();
        etNewItem.setText("");
        newItem.description = itemText;
        newItem.priority = 3;
        newItem.action = "add";
        newItem.dueDate = today;
        todoAdapter.add(newItem);
        todoAdapter.notifyDataSetChanged();
        db.addOrUpdateItem(newItem);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            TodoItemDatabase.ToDoItem editItem = db.getItem();
            editItem.description = data.getExtras().getString("updateItemText");
            editItem.priority = data.getExtras().getInt("priority");
            editItem.id = Integer.parseInt(data.getExtras().getString("itemId").toString());
            editItem.action = "update";
            editItem.dueDate = new Date();
            todoList.set(data.getExtras().getInt("position"), editItem);
            todoAdapter.notifyDataSetChanged();
            db.addOrUpdateItem(editItem);
        }
    }
}
