package com.alinc.todo;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class EditItemActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    private int itemPriority;
    private long dueDate;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.cp_logo);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        EditText editItem = (EditText)findViewById(R.id.etEditItem);
        editItem.setText(getIntent().getStringExtra("itemText"));
        editItem.setSelection(editItem.getText().length());

        SimpleDateFormat date = new SimpleDateFormat("MM/d/yyyy");
        dueDate = getIntent().getLongExtra("dueDate", System.currentTimeMillis());
        Button dueDateText = (Button)findViewById(R.id.btCalendar);
        dueDateText.setText(date.format(new Date(dueDate)));

        RadioButton radioButton3 = (RadioButton) findViewById(R.id.rbPriority3);
        radioButton3.setText(prefs.getString("priority_default", "Just do it"));

        RadioButton radioButton2 = (RadioButton) findViewById(R.id.rbPriority2);
        radioButton2.setText(prefs.getString("priority_high", "Get it done!"));

        RadioButton radioButton1 = (RadioButton) findViewById(R.id.rbPriority1);
        radioButton1.setText(prefs.getString("priority_highest", "Life Matter"));

        itemPriority = Integer.parseInt(getIntent().getStringExtra("priority"));
        switch(itemPriority) {
            case CommonConstants.STANDARD_PRIORITY: {
                radioButton3.performClick();
                editItem.setTextColor(editItem.getResources().getColor(R.color.standard_priority));
                break; }
            case CommonConstants.ELEVATED_PRIORITY: {
                radioButton2.performClick();
                editItem.setTextColor(editItem.getResources().getColor(R.color.elevated_priority));
                break; }
            case CommonConstants.HIGH_PRIORITY: {
                radioButton1.performClick();
                editItem.setTextColor(editItem.getResources().getColor(R.color.high_priority));
                break; }
        }

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
        data.putExtra("priority", itemPriority);
        data.putExtra("itemId", getIntent().getStringExtra("itemId"));
        data.putExtra("dueDate", dueDate);

        setResult(RESULT_OK, data);
        finish();
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        EditText editItem = (EditText)findViewById(R.id.etEditItem);
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.rbPriority3:
                if (checked) {
                    itemPriority = CommonConstants.STANDARD_PRIORITY;
                    editItem.setTextColor(editItem.getResources().getColor(R.color.standard_priority)); }
                    break;
            case R.id.rbPriority2:
                if (checked) {
                    itemPriority = CommonConstants.ELEVATED_PRIORITY;
                    editItem.setTextColor(editItem.getResources().getColor(R.color.elevated_priority)); }
                    break;
            case R.id.rbPriority1:
                if (checked) {
                    itemPriority = CommonConstants.HIGH_PRIORITY;
                    editItem.setTextColor(editItem.getResources().getColor(R.color.high_priority)); }
                break;
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    public void onDateSet(DatePicker view, int year, int month, int day) {

        Button dueDateText = (Button)findViewById(R.id.btCalendar);
        dueDateText.setText((month + 1) + "/" + day + "/" + year);
        GregorianCalendar gc = new GregorianCalendar();
        gc.clear();
        gc.set(year, month, day);
        dueDate = gc.getTimeInMillis();
    }
}
