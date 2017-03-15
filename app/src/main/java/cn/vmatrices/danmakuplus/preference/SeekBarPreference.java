package cn.vmatrices.danmakuplus.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import cn.vmatrices.danmakuplus.R;

/**
 * Created by iBelieve on 2017/3/4.
 */

public class SeekBarPreference extends DialogPreference {

    private Context context;
    private SeekBar sx;
    private SharedPreferences preferences;
    private int mValue;

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }


    @Override
    protected View onCreateDialogView() {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        View view = LayoutInflater.from(context).inflate(R.layout.pref_dialog_seekbar, null);
        sx = (SeekBar) view.findViewById(R.id.seekBar);
        sx.setProgress(preferences.getInt(getKey(), 20));
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(getKey(), sx.getProgress());
            editor.commit();
            callChangeListener(0);
        }
        super.onDialogClosed(positiveResult);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, Color.BLACK);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mValue = getPersistedInt(0);
        } else {
            persistInt((int) defaultValue);
            mValue = (int) defaultValue;
        }
    }
}
