/*
 * Copyright (C) 2012 Slimroms Project
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

import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.IWindowManager;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

public class UserInterface extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    public static final String TAG = "UserInterface";

    private static final String MISC_SETTINGS = "misc";
    private static final String PREF_USE_ALT_RESOLVER = "use_alt_resolver";
    
    private static final String KEY_HIGH_END_GFX = "high_end_gfx";

   
    private CheckBoxPreference mUseAltResolver;
    private PreferenceCategory mMisc;
    private CheckBoxPreference mHighEndGfx;

   

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.user_interface_settings);

        PreferenceScreen prefs = getPreferenceScreen();

        mMisc = (PreferenceCategory) prefs.findPreference(MISC_SETTINGS);

        mUseAltResolver = (CheckBoxPreference) findPreference(PREF_USE_ALT_RESOLVER);
        mUseAltResolver.setChecked(Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.ACTIVITY_RESOLVER_USE_ALT, 0) == 1);

       

     
        mHighEndGfx = (CheckBoxPreference) findPreference(KEY_HIGH_END_GFX);

        if (!ActivityManager.isHighEndGfx()) {
            // Only show this if the device does not have HighEndGfx enabled natively
            try {
                mHighEndGfx.setChecked(Settings.System.getInt(getContentResolver(),Settings.System.HIGH_END_GFX_ENABLED) == 1);
            } catch (Exception e) {
                Settings.System.putInt(getContentResolver(),Settings.System.HIGH_END_GFX_ENABLED, mHighEndGfx.isChecked() ? 1 : 0 );
            }
        } else {
            getPreferenceScreen().removePreference(mHighEndGfx);
        }
    }

   

    @Override
    public void onResume() {
        super.onResume();
        updateRamBar();
    }

    @Override
    public void onPause() {
        super.onResume();
        updateRamBar();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mUseAltResolver) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ACTIVITY_RESOLVER_USE_ALT,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
            return true;
        } else if (preference == mHighEndGfx) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HIGH_END_GFX_ENABLED,
                    ((CheckBoxPreference) preference).isChecked() ? 1 : 0);
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

}
