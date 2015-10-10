package com.seadee.degree.utility;


import com.seadee.degree.settting.SettingsPreferenceFragment;

import android.widget.Button;

/**
 * Interface letting {@link SettingsPreferenceFragment} access to bottom bar inside
 * {@link android.preference.PreferenceActivity}.
 */
public interface ButtonBarHandler {
    public boolean hasNextButton();
    public Button getNextButton();
}