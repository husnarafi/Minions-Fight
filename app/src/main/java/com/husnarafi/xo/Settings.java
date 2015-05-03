package com.husnarafi.xo;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.husnarafi.R;


public class Settings extends PreferenceActivity implements ColorPickerDialog.OnColorChangedListener {

    public static final String SOUND_PREFERENCE_KEY = "sound";
    public static final String GOES_FIRST_PREFERENCE_KEY = "goes_first";
    public static final String BOARD_COLOR_PREFERENCE_KEY = "board_color";
    public static final String DIFFICULTY_PREFERENCE_KEY = "difficulty_level";
    public static final String PLAYER_NAME_KEY = "player_name";
    public static final String VICTORY_MESSAGE_PREFERENCE_KEY = "victory_message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        final ListPreference goesFirstPref = (ListPreference) findPreference(GOES_FIRST_PREFERENCE_KEY);
        String goesFirst = prefs.getString(GOES_FIRST_PREFERENCE_KEY, "Alternate");
        goesFirstPref.setSummary((CharSequence) goesFirst);
        goesFirstPref
                .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        goesFirstPref.setSummary((CharSequence) newValue);

                        // Since we are handling the pref, we must save it
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString(GOES_FIRST_PREFERENCE_KEY,
                                newValue.toString());
                        ed.commit();

                        return true;
                    }
                });

        final ListPreference difficultyLevelPref = (ListPreference) findPreference(DIFFICULTY_PREFERENCE_KEY);
        String difficulty = prefs.getString(DIFFICULTY_PREFERENCE_KEY,
                getResources().getString(R.string.difficulty_expert));
        difficultyLevelPref.setSummary((CharSequence) difficulty);
        difficultyLevelPref
                .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        difficultyLevelPref.setSummary((CharSequence) newValue);

                        // Since we are handling the pref, we must save it
                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString(DIFFICULTY_PREFERENCE_KEY,
                                newValue.toString());
                        ed.commit();

                        return true;
                    }
                });

        final EditTextPreference victoryMessagePref = (EditTextPreference) findPreference(VICTORY_MESSAGE_PREFERENCE_KEY);
        String victoryMessage = prefs.getString(VICTORY_MESSAGE_PREFERENCE_KEY,
                getResources().getString(R.string.result_human_wins));
        victoryMessagePref.setSummary("\"" + victoryMessage + "\"");
        victoryMessagePref
                .setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference,
                                                      Object newValue) {
                        victoryMessagePref.setSummary((CharSequence) newValue);

                        SharedPreferences.Editor ed = prefs.edit();
                        ed.putString(VICTORY_MESSAGE_PREFERENCE_KEY,
                                newValue.toString());
                        ed.commit();

                        return true;
                    }
                });

        final Preference boardColorPref = (Preference) findPreference(BOARD_COLOR_PREFERENCE_KEY);
        boardColorPref
                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        int color = prefs.getInt(BOARD_COLOR_PREFERENCE_KEY,
                                Color.GRAY);
                        new ColorPickerDialog(Settings.this, Settings.this,
                                color).show();
                        return true;
                    }
                });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null; // = new ColorPickerDialog();
        return dialog;
    }

    @Override
    public void colorChanged(int color) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putInt(BOARD_COLOR_PREFERENCE_KEY, color).commit();
    }
}
