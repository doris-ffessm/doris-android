package fr.vojtisek.adm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AndroidDiveManagerMainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        refreshScreenData();
        
    }
    
    
    

    @Override
	protected void onResume() {
		super.onResume();
		
		refreshScreenData();
	}

    /**
     * Refresh data on this view
     */
    protected void refreshScreenData(){
    	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
      	 
        StringBuilder builder = new StringBuilder();
	 
        builder.append("\n" + R.string.surface_interval+": TODO");
		builder.append("\n" );
		builder.append("\n" + sharedPrefs.getString("length_unit", "NULL"));
		builder.append("\n" + sharedPrefs.getString("temperature_unit", "NULL"));
		 
		TextView settingsTextView = (TextView) findViewById(R.id.debug_text_view);
		settingsTextView.setText(builder.toString());
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, 0, 0, R.string.preferences_option).setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
        }
        return false;
    }
    
    public void onClickBtnDiveList(View view){
    	// open the view activity
		startActivity(new Intent(this, DiveListViewActivity.class));
    }
}