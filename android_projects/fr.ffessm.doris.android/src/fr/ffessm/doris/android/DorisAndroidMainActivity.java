package fr.ffessm.doris.android;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.activities.MainPreferencesActivity;
import fr.ffessm.doris.android.activities.ParticipantListViewActivity;
import fr.ffessm.doris.android.activities.async.DownloadDorisDataTask;
import fr.ffessm.doris.android.datamodel.Card;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class DorisAndroidMainActivity extends OrmLiteBaseActivity<OrmLiteDBHelper> {
	
	static final int PREFERENCE_ID = 0;
	static final int SYNC_DATA_ID = 1;
	static final int CREATE_FAKE_DATA_ID = 888;
	
	
	protected RuntimeExceptionDao<Participant, Integer> participantDao;
	protected RuntimeExceptionDao<Card, Integer> cardDao;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.mainpreferences, false);
        
        setContentView(R.layout.main);
        
        participantDao = getHelper().getParticipantDao();
        cardDao = getHelper().getCardDao();
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		refreshScreenData();
	}
    
    public void onClickBtnCardList(View view){
    	// open the view activity
		//startActivity(new Intent(this, CardListViewActivity.class));
    }
    public void onClickBtnParticipantList(View view){
    	// open the view activity
		startActivity(new Intent(this, ParticipantListViewActivity.class));
    }
    
    
    /**
     * Refresh data on this view
     */
    protected void refreshScreenData(){
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, PREFERENCE_ID, 0, R.string.mainpreferences_menu_title).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(Menu.NONE, SYNC_DATA_ID, 1, R.string.sync_data_option).setIcon(android.R.drawable.ic_menu_preferences);
        menu.add(Menu.NONE, CREATE_FAKE_DATA_ID, 2, R.string.create_fake_data_option).setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
            case PREFERENCE_ID:
                startActivity(new Intent(this, MainPreferencesActivity.class));
                return true;
            case CREATE_FAKE_DATA_ID:
                createFakeData();
                return true;
            case SYNC_DATA_ID:
            	syncData();
                return true;
            
        }
        return false;
    }
    
    
    public void createFakeData(){
		long millis = System.currentTimeMillis();
		Participant participant = new Participant("Célina Vojtisek ("+millis+")", "", ((Long)millis).intValue());
		participantDao.create(participant);
		// create some entries in the onCreate
		//DiveEntry diveEntry = new DiveEntry(new Date(millis), "Rennes", 20, 30);
		//dao.create(diveEntry);
        //diveEntries.add(new DiveEntry(new Date(2011,17,7), "Dinard", 25, "0:35"));
        //diveEntries.add(new DiveEntry(new Date(2008,4,15), "St Malo", 12, "0:45"));
		
    	refreshScreenData();
    }
    
    public void syncData(){
		//long millis = System.currentTimeMillis();
		// create some entries in the onCreate
		//DiveEntry diveEntry = new DiveEntry(new Date(millis), "Rennes", 20, 30);
		//dao.create(diveEntry);
        //diveEntries.add(new DiveEntry(new Date(2011,17,7), "Dinard", 25, "0:35"));
        //diveEntries.add(new DiveEntry(new Date(2008,4,15), "St Malo", 12, "0:45"));
		
    	new DownloadDorisDataTask(getApplicationContext()).execute("");
    	
    	refreshScreenData();
    }
}