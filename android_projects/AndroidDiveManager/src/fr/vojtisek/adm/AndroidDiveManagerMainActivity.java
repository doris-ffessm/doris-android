package fr.vojtisek.adm;

import java.util.Date;
import java.util.List;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import fr.vojtisek.adm.openintents.intents.FileManagerIntents;
import fr.vojtisek.adm.sdm2.SDM2Dive;
import fr.vojtisek.adm.sdm2.SDM2FileLoader;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.vojtisek.adm.data.DiveEntry;
import fr.vojtisek.adm.data.ORMLiteDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.ActivityNotFoundException;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidDiveManagerMainActivity extends OrmLiteBaseActivity<ORMLiteDBHelper> {
	
	static final int PREFERENCE_ID = 0;
	static final int CREATE_FAKE_DATA_ID = 1;
	static final int IMPORT_ALL_ID = 2;
	static final int NOTIMPLEMENTED_DIALOG_ID = 999;
	
	
	static final String DEFAULT_FILE_LOCATION = "/system/dive";
	
	
	protected static final int REQUEST_CODE_PICK_FILE_OR_DIRECTORY = 1;
	
	protected RuntimeExceptionDao<DiveEntry, Integer> dao;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        setContentView(R.layout.main);
        
        dao = getHelper().getDiveEntriesDao();
        
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
		
		
		
		// query for all of the data objects in the database
		List<DiveEntry> list = dao.queryForAll();
		// our string builder for building the content-view
		StringBuilder sb = new StringBuilder();
		builder.append("\ngot ").append(list.size()).append(" entries.\n");
		
		 
		TextView settingsTextView = (TextView) findViewById(R.id.debug_text_view);
		settingsTextView.setText(builder.toString());
    }


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, PREFERENCE_ID, 0, R.string.preferences_option).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(Menu.NONE, CREATE_FAKE_DATA_ID, 1, R.string.create_fake_data_option).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(Menu.NONE, IMPORT_ALL_ID, 1, R.string.import_data_from_file_option).setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
            case PREFERENCE_ID:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            case CREATE_FAKE_DATA_ID:
                createFakeData();
                return true;
            case IMPORT_ALL_ID:
            	importDataFromFile();
                return true;
        }
        return false;
    }
    
    public void createFakeData(){
		long millis = System.currentTimeMillis();
		// create some entries in the onCreate
		DiveEntry diveEntry = new DiveEntry(new Date(millis), "Rennes", 20, "0:30");
		dao.create(diveEntry);
        //diveEntries.add(new DiveEntry(new Date(2011,17,7), "Dinard", 25, "0:35"));
        //diveEntries.add(new DiveEntry(new Date(2008,4,15), "St Malo", 12, "0:45"));
		
    	refreshScreenData();
    }
    
    public void importDataFromFile(){
    	String fileName = DEFAULT_FILE_LOCATION;
		
    	
    	
    	
    	
		Intent intent = new Intent(FileManagerIntents.ACTION_PICK_FILE);
		
		// Construct URI from file name.
		File file = new File(fileName);
		intent.setData(Uri.fromFile(file));
		
		// Set fancy title and button (optional)
		intent.putExtra(FileManagerIntents.EXTRA_TITLE, getString(R.string.open_title));
		intent.putExtra(FileManagerIntents.EXTRA_BUTTON_TEXT, getString(R.string.open_button));
		
		try {
			startActivityForResult(intent, REQUEST_CODE_PICK_FILE_OR_DIRECTORY);
		} catch (ActivityNotFoundException e) {
			// No compatible file manager was found.
			Toast.makeText(this, R.string.no_filemanager_installed, 
					Toast.LENGTH_SHORT).show();
		}
    	
    	
    	
    	refreshScreenData();
    }
    
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case REQUEST_CODE_PICK_FILE_OR_DIRECTORY:
			Uri fileUri = data.getData();
			if (fileUri != null) {
				String filePath = fileUri.getPath();
				if (filePath != null) {
					File f = new File(filePath);
					try {
						SDM2FileLoader sdmLoader = new SDM2FileLoader(f);
						Toast.makeText(this, "Importing "+ sdmLoader.getDives().size() + " dive(s)", 
										Toast.LENGTH_SHORT).show();
						for(SDM2Dive sdmDive : sdmLoader.getDives()){
							Toast.makeText(this, sdmDive.getDate()+" " + sdmDive.getTime() + " "+sdmDive.getDepth(), 
									Toast.LENGTH_LONG).show();
						}
					} catch (SAXException e) {
						Toast.makeText(this, "Cannot load invalid file "+ filePath + e, 
								Toast.LENGTH_LONG).show();
					} catch (IOException e) {
						Toast.makeText(this, "Cannot load file "+ filePath + e, 
								Toast.LENGTH_LONG).show();
					}
					catch (ParserConfigurationException e) {
						Toast.makeText(this, "Cannot load file "+ filePath + e, 
								Toast.LENGTH_LONG).show();
					}
				}
			}
			
			break;

		default:
			break;
		}
		
	}




	public void onClickBtnDiveList(View view){
    	// open the view activity
		startActivity(new Intent(this, DiveListViewActivity.class));
    }
}