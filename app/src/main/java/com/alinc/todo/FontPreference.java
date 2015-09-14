package com.alinc.todo;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by alinc on 9/7/15.
 */
public class FontPreference extends DialogPreference implements DialogInterface.OnClickListener {

    private List<String> fontFiles;
    private List<String> fontNames;

    public FontPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);

        HashMap<String, String> fonts = new HashMap<>();
        fonts.put("daniel.ttf", "Daniel");
        fonts.put("black_jack.ttf", "Black Jack");
        fonts.put("jr_hand.ttf", "JR Hand");
        fonts.put("KaushanScript-Regular.otf", "Kaushan");
        fonts.put("GoodDog.otf", "Good Dog");
        fonts.put("Raleway.ttf", "Raleway");

        fontFiles = new ArrayList<String>();
        fontNames = new ArrayList<String>();

        String selectedFontPath = getSharedPreferences().getString(getKey(), "");
        int idx = 0, checked_item = 0;
        for (String path : fonts.keySet()) {
            if (path.equals(selectedFontPath))
                checked_item = idx;
            fontFiles.add(path);
            fontNames.add(fonts.get(path));
            idx++;
        }

        FontAdapter adapter = new FontAdapter();
        builder.setSingleChoiceItems(adapter, checked_item, this);
        builder.setPositiveButton(null, null);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (which >= 0 && which < fontFiles.size()) {
            String selectedFontPath = fontFiles.get(which);
            Editor editor = getSharedPreferences().edit();
            editor.putString(getKey(), selectedFontPath);
            editor.putString("fontName", fontNames.get(which));
            editor.commit();
            dialog.dismiss();
        }
    }

    public class FontAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return fontNames.size();
        }

        @Override
        public Object getItem(int position) {
            return fontNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                final LayoutInflater inflater = (LayoutInflater)
                        getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.select_dialog_singlechoice, parent, false);
            }
            if (view != null) {
                CheckedTextView tv = (CheckedTextView) view.findViewById(android.R.id.text1);
                Typeface tface = Typeface.createFromAsset(getContext().getAssets(), fontFiles.get(position));
                tv.setTypeface(tface);
                tv.setText(fontNames.get(position));
            }
            return view;
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.summary);
        titleView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), getSharedPreferences().getString(getKey(), "daniel.ttf")));
    }
}
