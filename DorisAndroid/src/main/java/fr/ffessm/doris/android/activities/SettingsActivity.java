package fr.ffessm.doris.android.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.tools.ThemeUtil;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String LOG_TAG = SettingsActivity.class.getCanonicalName();
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false); // For edge-to-edge
        super.onCreate(savedInstanceState);
        ThemeUtil.onActivityCreateSetTheme(this); // Apply your custom theme

        setContentView(R.layout.activity_settings_host); // You'll need to create this layout

        // Optional: Setup Toolbar as ActionBar
        // Toolbar toolbar = findViewById(R.id.toolbar_settings); // Assuming you have a Toolbar in activity_settings_host.xml
        // if (toolbar != null) {
        //     setSupportActionBar(toolbar);
        // }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            // actionBar.setTitle(R.string.preference_titre); // Or get from fragment
        }

        // Apply window insets to the container that holds the fragment
        View settingsContainer = findViewById(R.id.settings_container); // This is your FrameLayout
        if (settingsContainer != null) {
            ViewCompat.setOnApplyWindowInsetsListener(settingsContainer, (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                // Apply the insets as padding to the container
                v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
                return WindowInsetsCompat.CONSUMED; // Or return windowInsets if you want children to also get them
            });
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment()) // R.id.settings_container is in activity_settings_host.xml
                    .commit();
        }

        // Stop any ongoing image download
        TelechargePhotosAsync_BgActivity telechargePhotosFiches_BgActivity = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;
        if (telechargePhotosFiches_BgActivity != null && telechargePhotosFiches_BgActivity.getStatus() == AsyncTask.Status.RUNNING) {
            Toast.makeText(this, R.string.bg_notifToast_arretTelecharg, Toast.LENGTH_LONG).show();
            DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);
        }

    }

    /**
     * Called when the user clicks on a Preference that represents a PreferenceScreen.
     * handle replacing the current fragment with the new one.
     */
    @Override
    public boolean onPreferenceStartFragment(@NonNull PreferenceFragmentCompat caller, @NonNull Preference pref) {
        if (pref instanceof PreferenceScreen) { // Check if it's a PreferenceScreen that was clicked
            // Create a new instance of your main SettingsFragment (or a specific one if designed)
            // and tell it which sub-screen to load using its key as the rootKey.
            SettingsFragment fragment = new SettingsFragment(); // Or your specific fragment for this sub-screen
            Bundle args = new Bundle();
            // The key of the PreferenceScreen that was clicked will be passed as the rootKey
            // so the new fragment instance loads that specific screen.
            args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.getKey());
            // Pass the title of the clicked PreferenceScreen to the new fragment instance
            if (pref.getTitle() != null) {
                args.putCharSequence(SettingsFragment.ARG_SCREEN_TITLE, pref.getTitle());
            }
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.settings_container, fragment)
                    .addToBackStack(pref.getKey()) // Use key for backstack name for clarity
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Respond to the action bar's Up/Home button
            if (getSupportFragmentManager().popBackStackImmediate()) {
                return true;
            }
            finish(); // Or super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Allow the fragment to update the Activity's title
    public void setActivityTitle(CharSequence title) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }

}