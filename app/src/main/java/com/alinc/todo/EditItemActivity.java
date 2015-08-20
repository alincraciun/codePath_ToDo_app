package com.alinc.todo;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.cp_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        EditText editItem = (EditText)findViewById(R.id.etEditItem);
        editItem.setText(getIntent().getStringExtra("currentItemText"));
        editItem.setSelection(editItem.getText().length());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
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

    public void onSubmit(View v) {
        EditText etUpdateItem = (EditText) findViewById(R.id.etEditItem);
        Intent data = new Intent();
        data.putExtra("updateItemText", etUpdateItem.getText().toString());
        data.putExtra("position", getIntent().getExtras().getInt("pos"));
        data.putExtra("id", getIntent().getExtras().getLong("id"));
        data.putExtra("code", 200);

        setResult(RESULT_OK, data);
        finish();
    }
}
