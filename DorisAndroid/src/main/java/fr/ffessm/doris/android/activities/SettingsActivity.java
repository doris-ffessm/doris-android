package fr.ffessm.doris.android.activities; // Or your preferred package

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar; // If you're using a Toolbar
import androidx.core.view.WindowCompat;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.ThemeUtil;

public class SettingsActivity extends AppCompatActivity {

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

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment()) // R.id.settings_container is in activity_settings_host.xml
                    .commit();
        }

        // Apply window insets for edge-to-edge to the container
        // View settingsContainer = findViewById(R.id.settings_container);
        // ViewCompat.setOnApplyWindowInsetsListener(settingsContainer, (v, insets) -> {
        // Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
        // v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
        // return insets;
        // });
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
}