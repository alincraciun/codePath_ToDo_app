package com.alinc.todo;

import android.content.Intent;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<TodoItemDatabase.ToDoItem> todoList;
    private ToDoItemAdapter todoAdapter;

    private ListView lvItems;
    public static final int REQUEST_CODE = 200;
    public static final int REFRESH_REQUEST = 999;
    private TodoItemDatabase db;
    private Menu menu;

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

        final TextView tvNewItem = (TextView) findViewById(R.id.etNewItem);
        tvNewItem.setTypeface(Typeface.createFromAsset(getAssets(), "daniel.ttf"));
        final Button addButton = (Button) findViewById(R.id.btnAddItem);

        tvNewItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(tvNewItem.getText().toString().length() == 0)
                    addButton.setEnabled(false);
                else
                    addButton.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        PreferenceManager.setDefaultValues(this, R.xml.preference_screen, false);

        setupListViewListener();
        setupEditListener();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        boolean deleted = db.deleteItem(todoList.get(pos));
                        if (deleted) {
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
        todoList = new ArrayList<TodoItemDatabase.ToDoItem>();
        if(db != null) {
            todoList.addAll(db.getAllItems());
        }
        todoAdapter = new ToDoItemAdapter(this, android.R.layout.simple_list_item_1, todoList);
        lvItems.setAdapter(todoAdapter);
        todoAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_preferences:
                openPreferences();
                return true;
            case R.id.action_today_filter:
                todayFilter(item);
                return true;
            case R.id.action_settings:
                openSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openPreferences() {
        Intent intent = new Intent();
        intent.setClassName(this, "com.alinc.todo.SettingsActivity");
        startActivityForResult(intent, REFRESH_REQUEST);
    }

    private void todayFilter(MenuItem item) {

        if(item.isChecked()) {
            item.setChecked(false);
            item.setIcon(R.drawable.ic_action_today0);
            db.today = false;
            readItems();
        }
        else {
            item.setChecked(true);
            item.setIcon(R.drawable.ic_action_today1);
            item.setTitle("Today");
            db.today = true;
            readItems();
        }

    }

    private void openSettings() {
       // SharedPreferences sharedSettings = PreferenceManager.getDefaultSharedPreferences(this);
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
            //todoAdapter.add(newItem);
            readItems();
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
            db.addOrUpdateItem(editItem);
            readItems();
        }
        else if(resultCode == RESULT_OK && requestCode == REFRESH_REQUEST) {
            readItems();
        }
    }
}
