package com.alinc.todo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by alinc on 8/23/15.
 */
public class ToDoItemAdapter extends ArrayAdapter<TodoItemDatabase.ToDoItem> {


    public ToDoItemAdapter(Context context, int textViewResourceId, ArrayList<TodoItemDatabase.ToDoItem> items) {
        super(context, textViewResourceId, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TodoItemDatabase.ToDoItem item = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.todo_item, parent, false);
        }

        if(item != null) {
            TextView tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);
            TextView tvDueDate = (TextView) convertView.findViewById(R.id.tvDueDate);
            tvDescription.setText(item.description);

            SimpleDateFormat date = new SimpleDateFormat("MMM dd");
            if(item.dueDate != null)
                tvDueDate.setText(date.format(item.dueDate));
            else
                tvDueDate.setText(date.format(new Date()));

            if(item.priority == 1) {
                tvDescription.setTextColor(Color.RED);
            }
            else if (item.priority == 2) {
                tvDescription.setTextColor(Color.rgb(255,69,0));
            }
            else {
                tvDescription.setTextColor(Color.BLACK);
            }
        }

        return convertView;
    }
}
