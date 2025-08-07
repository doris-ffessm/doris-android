package fr.ffessm.doris.android.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.view.WindowCompat;
import androidx.preference.PreferenceManager;

import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;


public class AbstractDorisActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper> {

    /**
     * Save the current theme to compare it for possible changes in the background in order to recreate the activity
     */
    private String currentTheme;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentTheme = prefs.getString(getString(R.string.pref_key_theme), ThemeUtil.THEME_DEFAULT);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String newTheme = prefs.getString(getString(R.string.pref_key_theme), ThemeUtil.THEME_DEFAULT);
        if (!newTheme.equals(currentTheme)) {
            recreate(); // Theme changed while we were in background
        }
    }
}
