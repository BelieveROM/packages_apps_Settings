/*
 * Copyright (C) 2012 Slimroms
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

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.util.Helpers;

public class NotificationDrawerSettings extends SettingsPreferenceFragment {

    public static final String TAG = "NotificationDrawerSettings";
    private static final String PREF_NOTIFICATION_SHOW_WIFI_SSID = "notification_show_wifi_ssid";
    private static final String PREF_NOTIFICATION_OPTIONS = "options";
    private static final String PREF_NOTIFICATION_POWER_WIDGET = "power_widget";

    PreferenceCategory mAdditionalOptions;
    Preference mPowerWidget;
    CheckBoxPreference mShowWifiName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.notification_drawer_settings);

        PreferenceScreen prefs = getPreferenceScreen();

        mPowerWidget = findPreference(PREF_NOTIFICATION_POWER_WIDGET);
        if (mPowerWidget != null) {
              updatePowerWidgetDescription();
        }


        mAdditionalOptions = (PreferenceCategory) prefs.findPreference(PREF_NOTIFICATION_OPTIONS);

        if (Utils.isTablet(getActivity())) {
            // Nothing for tablets in the additional options section at the moment, remove it
            prefs.removePreference(mAdditionalOptions);
        } else {
            mShowWifiName = (CheckBoxPreference) findPreference(PREF_NOTIFICATION_SHOW_WIFI_SSID);
            mShowWifiName.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_SHOW_WIFI_SSID, 0) == 1);
        }

    }

    private void updatePowerWidgetDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.EXPANDED_VIEW_WIDGET, 0) == 1) {
            mPowerWidget.setSummary(getString(R.string.power_widget_enabled));
        } else {
            mPowerWidget.setSummary(getString(R.string.power_widget_disabled));
         }
    }

    @Override
    public void onResume() {
        super.onResume();
        updatePowerWidgetDescription();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mShowWifiName) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NOTIFICATION_SHOW_WIFI_SSID,
                    mShowWifiName.isChecked() ? 1 : 0);
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
