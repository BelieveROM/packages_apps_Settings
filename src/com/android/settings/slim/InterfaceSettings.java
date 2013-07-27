/*
 * Copyright (C) 2013 SlimRoms
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.Spannable;
import android.widget.EditText;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

public class InterfaceSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String PREF_USE_ALT_RESOLVER = "use_alt_resolver";
    private static final String PREF_HIGH_END_GFX = "high_end_gfx";
    
    private static final String CATEGORY_INTERFACE = "interface_settings_action_prefs";

    
    private CheckBoxPreference mUseAltResolver;
    private CheckBoxPreference mHighEndGfx;

    

   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.slim_interface_settings);

        PreferenceScreen prefs = getPreferenceScreen();
        PreferenceCategory category = (PreferenceCategory) prefs.findPreference(CATEGORY_INTERFACE);

        mUseAltResolver = (CheckBoxPreference) findPreference(PREF_USE_ALT_RESOLVER);
        mUseAltResolver.setOnPreferenceChangeListener(this);
        mUseAltResolver.setChecked(Settings.System.getInt(
                getActivity().getContentResolver(),
                Settings.System.ACTIVITY_RESOLVER_USE_ALT, 0) == 1);

      
        mHighEndGfx = (CheckBoxPreference) findPreference(PREF_HIGH_END_GFX);
        mHighEndGfx.setOnPreferenceChangeListener(this);

        if (!ActivityManager.isHighEndGfx()) {
            // Only show this if the device does not have HighEndGfx enabled natively
            try {
                mHighEndGfx.setChecked(Settings.System.getInt(getContentResolver(), Settings.System.HIGH_END_GFX_ENABLED) == 1);
            } catch (Exception e) {
                Settings.System.putInt(getContentResolver(), Settings.System.HIGH_END_GFX_ENABLED, mHighEndGfx.isChecked() ? 1 : 0 );
            }
        } else {
            category.removePreference(mHighEndGfx);
        }

    }

   
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUseAltResolver) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ACTIVITY_RESOLVER_USE_ALT,
                    (Boolean) newValue ? 1 : 0);
            return true;
        } else if (preference == mHighEndGfx) {
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.HIGH_END_GFX_ENABLED,
                    (Boolean) newValue ? 1 : 0);
            return true;
        }
        return false;
    }

    }
