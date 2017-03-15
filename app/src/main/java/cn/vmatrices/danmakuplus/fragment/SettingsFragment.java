package cn.vmatrices.danmakuplus.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.Snackbar;

import cn.vmatrices.danmakuplus.R;
import cn.vmatrices.danmakuplus.tool.Common;
import cn.vmatrices.danmakuplus.ui.ExcludeActivity;

/**
 * Created by iBelieve on 2017/3/9.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

    private Preference bgColor;
    private Preference setting;
    private Intent updateIntent = new Intent(Common.ACTION_UPDATE);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);

        bgColor = findPreference(Common.PREF_COLOR_BG);
        setting = findPreference(Common.PREF_SETTING);


        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        if (!preferences.getBoolean(Common.PREF_COLOR_BG_ENABLE, true)) {
            bgColor.setEnabled(false);
        }

        if (!preferences.getBoolean(Common.PREF_ENABLE, true)) {
            setting.setEnabled(false);
        }

        findPreference(Common.PREF_SPEED).setOnPreferenceChangeListener(this);
        findPreference(Common.PREF_FONT_SIZE).setOnPreferenceChangeListener(this);
        findPreference(Common.PREF_COLOR_BG).setOnPreferenceChangeListener(this);
        findPreference(Common.PREF_COLOR_FONT).setOnPreferenceChangeListener(this);
        findPreference(Common.PREF_POSITION).setOnPreferenceChangeListener(this);
        findPreference(Common.PREF_EXCLUDE_WORD).setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        SharedPreferences sharedPreferences = preference.getSharedPreferences();

        switch (preference.getKey()) {
            case Common.PREF_ENABLE:
                if (sharedPreferences.getBoolean(Common.PREF_ENABLE, true)) {
                    setting.setEnabled(true);
                    Snackbar.make(getView(), R.string.snackbar_service_on, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    setting.setEnabled(false);
                    Snackbar.make(getView(), R.string.snackbar_service_off, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            case Common.PREF_COLOR_BG_ENABLE:
                if (sharedPreferences.getBoolean(Common.PREF_COLOR_BG_ENABLE, true)) {
                    bgColor.setEnabled(true);
                } else {
                    bgColor.setEnabled(false);
                }
                break;
            case Common.PREF_EXCLUDE_APP:
                startActivity(new Intent(getActivity(), ExcludeActivity.class));
        }
        update();
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        update();
        return true;
    }

    private void update() {
        getActivity().sendBroadcast(updateIntent);
    }
}