/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.slim;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.TwoStatePreference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class StatusBar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String TAG = "StatusBar";

    private static final String STATUS_BAR_SIGNAL = "status_bar_signal";
    private static final String STATUS_BAR_CATEGORY_GENERAL = "status_bar_general";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String STATUS_BAR_AUTO_HIDE = "status_bar_auto_hide";
    private static final String STATUS_BAR_QUICK_PEEK = "status_bar_quick_peek";
    private static final String PREF_STATUS_BAR_TRAFFIC_ENABLE = "status_bar_traffic_enable";
    private static final String PREF_STATUS_BAR_TRAFFIC_HIDE = "status_bar_traffic_hide";

    private StatusBarBrightnessChangedObserver mStatusBarBrightnessChangedObserver;

    private ListPreference mStatusBarCmSignal;
    private PreferenceScreen mClockStyle;
    private PreferenceCategory mPrefCategoryGeneral;
    private CheckBoxPreference mStatusBarBrightnessControl;
    private ListPreference mStatusBarAutoHide;
    private CheckBoxPreference mStatusBarQuickPeek;
    private CheckBoxPreference mStatusBarTraffic_enable;
    private CheckBoxPreference mStatusBarTraffic_hide;
    
    private static ContentResolver mContentResolver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.status_bar);
	
	ContentResolver resolver = getActivity().getContentResolver();
        PreferenceScreen prefSet = getPreferenceScreen();
	mContentResolver = getContentResolver();

	mStatusBarTraffic_enable = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_TRAFFIC_ENABLE);
        mStatusBarTraffic_enable.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_TRAFFIC_ENABLE, 0) == 1));

        mStatusBarTraffic_hide = (CheckBoxPreference) prefSet.findPreference(PREF_STATUS_BAR_TRAFFIC_HIDE);
        mStatusBarTraffic_hide.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_TRAFFIC_HIDE, 1) == 1)); 

        mStatusBarCmSignal = (ListPreference) prefSet.findPreference(STATUS_BAR_SIGNAL);
        int signalStyle = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUS_BAR_SIGNAL_TEXT, 0);
        mStatusBarCmSignal.setValue(String.valueOf(signalStyle));
        mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntry());
        mStatusBarCmSignal.setOnPreferenceChangeListener(this);

        mStatusBarBrightnessControl = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_BRIGHTNESS_CONTROL);
        mStatusBarBrightnessControl.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                            Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1));
        mStatusBarBrightnessControl.setOnPreferenceChangeListener(this);

        // Start observing for changes on auto brightness
        mStatusBarBrightnessChangedObserver = new StatusBarBrightnessChangedObserver(new Handler());
        mStatusBarBrightnessChangedObserver.startObserving();

        mStatusBarAutoHide = (ListPreference) prefSet.findPreference(STATUS_BAR_AUTO_HIDE);
        int statusBarAutoHideValue = Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.AUTO_HIDE_STATUSBAR, 0);
        mStatusBarAutoHide.setValue(String.valueOf(statusBarAutoHideValue));
        updateStatusBarAutoHideSummary(statusBarAutoHideValue);
        mStatusBarAutoHide.setOnPreferenceChangeListener(this);

        mStatusBarQuickPeek = (CheckBoxPreference) prefSet.findPreference(STATUS_BAR_QUICK_PEEK);
        mStatusBarQuickPeek.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.STATUSBAR_PEEK, 0) == 1));
        mStatusBarQuickPeek.setOnPreferenceChangeListener(this);

        mPrefCategoryGeneral = (PreferenceCategory) findPreference(STATUS_BAR_CATEGORY_GENERAL);

        if (Utils.isWifiOnly(getActivity())) {
            mPrefCategoryGeneral.removePreference(mStatusBarCmSignal);
        }

        if (Utils.isTablet(getActivity())) {
            mPrefCategoryGeneral.removePreference(mStatusBarBrightnessControl);
        }

        mClockStyle = (PreferenceScreen) prefSet.findPreference("clock_style_pref");
        if (mClockStyle != null) {
            updateClockStyleDescription();
        }

        updateStatusBarBrightnessControl();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;
        if (preference == mStatusBarCmSignal) {
            int signalStyle = Integer.valueOf((String) newValue);
            int index = mStatusBarCmSignal.findIndexOfValue((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_SIGNAL_TEXT, signalStyle);
            mStatusBarCmSignal.setSummary(mStatusBarCmSignal.getEntries()[index]);
            return true;
        } else if (preference == mStatusBarBrightnessControl) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mStatusBarQuickPeek) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUSBAR_PEEK,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mStatusBarAutoHide) {
            int statusBarAutoHideValue = Integer.valueOf((String) newValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.AUTO_HIDE_STATUSBAR, statusBarAutoHideValue);
            updateStatusBarAutoHideSummary(statusBarAutoHideValue);
            return true;
        }
        return false;
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
	boolean value;
	if (preference == mStatusBarTraffic_enable) {
	value = mStatusBarTraffic_enable.isChecked();
	Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
		Settings.System.STATUS_BAR_TRAFFIC_ENABLE, value ? 1: 0);
	return true;
	} else if (preference == mStatusBarTraffic_hide) {
            value = mStatusBarTraffic_hide.isChecked();
      Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.STATUS_BAR_TRAFFIC_HIDE, value ? 1 : 0);
        return true; 
	}
	return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

    private void updateClockStyleDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
               Settings.System.STATUS_BAR_CLOCK, 1) == 1) {
            mClockStyle.setSummary(getString(R.string.clock_enabled));
        } else {
            mClockStyle.setSummary(getString(R.string.clock_disabled));
         }
    }

    private void updateStatusBarAutoHideSummary(int value) {
        if (value == 0) {
            /* StatusBar AutoHide deactivated */
            mStatusBarAutoHide.setSummary(getResources().getString(R.string.auto_hide_statusbar_off));
        } else {
            mStatusBarAutoHide.setSummary(getResources().getString(value == 1
                    ? R.string.auto_hide_statusbar_summary_nonperm
                    : R.string.auto_hide_statusbar_summary_all));
        }
    }

    private void updateStatusBarBrightnessControl() {
        int mode;
        try {
            if (mStatusBarBrightnessControl != null) {
                mode = Settings.System.getIntForUser(mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

                if (mode == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                    mStatusBarBrightnessControl.setEnabled(false);
                    mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_info);
                } else {
                    mStatusBarBrightnessControl.setEnabled(true);
                    mStatusBarBrightnessControl.setSummary(R.string.status_bar_toggle_brightness_summary);
                }
            }
        } catch (SettingNotFoundException e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateClockStyleDescription();
        updateStatusBarBrightnessControl();
    }

    private class StatusBarBrightnessChangedObserver extends ContentObserver {
        public StatusBarBrightnessChangedObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            updateStatusBarBrightnessControl();
        }

        public void startObserving() {
            final ContentResolver cr = getActivity().getApplicationContext().getContentResolver();
            cr.registerContentObserver(
                    Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS_MODE),
                    false, this);
        }
    }

   
}
