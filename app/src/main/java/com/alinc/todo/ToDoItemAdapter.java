package com.alinc.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by alinc on 8/23/15.
 */
public class ToDoItemAdapter extends ArrayAdapter<TodoItemDatabase.ToDoItem> {

    private static SharedPreferences sharedPreferences;

    public ToDoItemAdapter(Context context, int textViewResourceId, ArrayList<TodoItemDatabase.ToDoItem> items) {
        super(context, textViewResourceId, items);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TodoItemDatabase.ToDoItem item = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.todo_item, parent, false);
        }

        if(item != null) {
            SimpleDateFormat date = new SimpleDateFormat("MMM d");
            Typeface preferedFont = Typeface.createFromAsset(getContext().getAssets(), sharedPreferences.getString("lp_font", "daniel.ttf"));
            TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            TextView tvDueDate = (TextView) convertView.findViewById(R.id.tvDueDate);
            tvDescription.setText(item.description);
            tvDueDate.setText(date.format(new Date(item.dueDate)));

            tvDescription.setTypeface(preferedFont);
            tvDueDate.setTypeface(preferedFont);

            if(isToday(item.dueDate)) {
                tvDescription.setTypeface(preferedFont, Typeface.BOLD);
                tvDueDate.setTypeface(preferedFont, Typeface.BOLD);
            }
            if(item.priority == CommonConstants.HIGH_PRIORITY) {
                tvDescription.setTextColor(tvDescription.getResources().getColor(R.color.high_priority));
                tvDueDate.setTextColor(tvDueDate.getResources().getColor(R.color.high_priority));
            }
            else if (item.priority == CommonConstants.ELEVATED_PRIORITY) {
                tvDescription.setTextColor(tvDescription.getResources().getColor(R.color.elevated_priority));
                tvDueDate.setTextColor(tvDueDate.getResources().getColor(R.color.elevated_priority));
            }
            else {
                tvDescription.setTextColor(tvDescription.getResources().getColor(R.color.standard_priority));
                tvDueDate.setTextColor(tvDueDate.getResources().getColor(R.color.standard_priority));

            }
        }

        return convertView;
    }

    public static boolean isToday (long dueDate) {
        Calendar today = Calendar.getInstance();
        Calendar due = Calendar.getInstance();
        due.setTimeInMillis(dueDate);
        if(today.get(Calendar.YEAR) == due.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == due.get(Calendar.DAY_OF_YEAR))
            return true;
        else
            return false;
    }
}
