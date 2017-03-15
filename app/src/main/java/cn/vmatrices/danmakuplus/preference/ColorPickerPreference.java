package cn.vmatrices.danmakuplus.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import cn.vmatrices.danmakuplus.R;
import cn.vmatrices.danmakuplus.tool.LinearColor;

/**
 * Created by iBelieve on 2017/3/4.
 */

public class ColorPickerPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    Context context;
    private SharedPreferences preferences;
    private SeekBar colorSeekBar;
    private SeekBar lightSeekBar;
    private CheckBox checkBox;

    GradientDrawable colorPanelA;
    GradientDrawable colorPanelB;

    private float colorIndex = 0.5f;
    private float colorBrightness = 0.5f;
    private boolean defaultRandom;
    private int defaultColor;

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, Color.BLACK);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (!restorePersistedValue) {
            persist((int) defaultValue, defaultRandom);
        }
    }

    @Override
    protected View onCreateDialogView() {

        int color = getPersistedInt(Color.BLACK);

        View view = LayoutInflater.from(context).inflate(R.layout.pref_dialog_colorpicker, null);
        colorSeekBar = (SeekBar) view.findViewById(R.id.seekBar1);
        lightSeekBar = (SeekBar) view.findViewById(R.id.seekBar2);
        checkBox = (CheckBox) view.findViewById(R.id.checkBox1);

        colorSeekBar.setOnSeekBarChangeListener(this);
        lightSeekBar.setOnSeekBarChangeListener(this);
        checkBox.setOnCheckedChangeListener(this);

        colorPanelA = (GradientDrawable) view.findViewById(R.id.color_panel1).getBackground();
        colorPanelB = (GradientDrawable) view.findViewById(R.id.color_panel2).getBackground();

        if (preferences.getBoolean(getKey() + "_random", false)) {
            colorSeekBar.setEnabled(false);
            lightSeekBar.setEnabled(false);
            checkBox.setChecked(true);
        }

        colorPanelA.setColor(color);
        colorPanelB.setColor(color);
        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persist(LinearColor.getColor(colorIndex, colorBrightness), checkBox.isChecked());
            callChangeListener(0);
        }
        super.onDialogClosed(positiveResult);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == colorSeekBar) {
            colorIndex = progress / 100f;
        } else {
            colorBrightness = progress / 100f;
        }
        colorPanelB.setColor(LinearColor.getColor(colorIndex, colorBrightness));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        colorSeekBar.setEnabled(!isChecked);
        lightSeekBar.setEnabled(!isChecked);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.context = context;
        if (attributeSet != null) {
            TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.ColorPickerAttribs);
            defaultRandom = array.getBoolean(R.styleable.ColorPickerAttribs_defaultRandom, false);
            array.recycle();
        } else {
            defaultRandom = false;
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private void persist(int color, boolean random) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getKey(), color);
        editor.putBoolean(getKey() + "_random", random);
        editor.commit();
    }

}
