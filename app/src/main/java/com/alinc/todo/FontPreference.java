package com.alinc.todo;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by alinc on 9/7/15.
 */
public class FontPreference extends ListPreference {


    public FontPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");

        /*setEntries(entries());
        setEntryValues(entryValues());
        setValueIndex(initializeIndex());*/
    }

    public FontPreference(Context context) {
        this(context, null);
    }

    /* @Override
    protected View onCreateDialogView() {

    } */

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);

    }

    @Override
    protected void onDialogClosed(boolean result) {

    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {

    }
}
