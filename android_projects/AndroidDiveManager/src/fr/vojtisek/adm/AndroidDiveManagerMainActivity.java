package fr.vojtisek.adm;

import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.vojtisek.adm.data.DiveEntry;
import fr.vojtisek.adm.data.ORMLiteDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class AndroidDiveManagerMainActivity extends OrmLiteBaseActivity<ORMLiteDBHelper> {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.main);
        
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
		
		
		// get our dao
		RuntimeExceptionDao<DiveEntry, Integer> simpleDao = getHelper().getDiveEntriesDao();
		// query for all of the data objects in the database
		List<DiveEntry> list = simpleDao.queryForAll();
		// our string builder for building the content-view
		StringBuilder sb = new StringBuilder();
		builder.append("\ngot ").append(list.size()).append(" entries.\n");
		
		 
		TextView settingsTextView = (TextView) findViewById(R.id.debug_text_view);
		settingsTextView.setText(builder.toString());
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, 0, 0, R.string.preferences_option).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(Menu.NONE, 1, 1, R.string.create_fake_data_option).setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            case 1:
                createFakeData();
                return true;
        }
        return false;
    }
    
    public void createFakeData(){
    	RuntimeExceptionDao<DiveEntry, Integer> dao = getHelper().getDiveEntriesDao();
		long millis = System.currentTimeMillis();
		// create some entries in the onCreate
		DiveEntry diveEntry = new DiveEntry(new Date(millis), "Rennes", 20, "0:30");
		dao.create(diveEntry);
        //diveEntries.add(new DiveEntry(new Date(2011,17,7), "Dinard", 25, "0:35"));
        //diveEntries.add(new DiveEntry(new Date(2008,4,15), "St Malo", 12, "0:45"));
		
    	refreshScreenData();
    }
    
    
    
    public void onClickBtnDiveList(View view){
    	// open the view activity
		startActivity(new Intent(this, DiveListViewActivity.class));
    }
}