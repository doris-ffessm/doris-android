package fr.vojtisek.adm;

import java.text.SimpleDateFormat;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.vojtisek.adm.data.db.DiveEntry;
import fr.vojtisek.adm.data.db.ORMLiteDBHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;

public class DiveDetailViewActivity extends OrmLiteBaseActivity<ORMLiteDBHelper> {

	protected int currentDiveId;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.divedetail);
        currentDiveId = getIntent().getExtras().getInt("diveId");
        //findViewById(R.id.divedate);
        
        
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		
		refreshScreenData();
	}
    
    
    private void refreshScreenData() {
    	// get our dao
    	RuntimeExceptionDao<DiveEntry, Integer> diveEntriesDao = getHelper().getDiveEntriesDao();
    	DiveEntry entry = diveEntriesDao.queryForId(currentDiveId);
    	
    	
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    	((TextView) findViewById(R.id.detail_divedate)).setText(dateFormatter.format(entry.getDate()));
		
    	((TextView) findViewById(R.id.detail_divelocation)).setText(entry.getLocation());
    	
    	((TextView) findViewById(R.id.detail_divedepth)).setText(entry.getMaxdepth().toString());
    	
    	((TextView) findViewById(R.id.detail_diveduration)).setText(entry.getDuration().toString());
    	
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		menu.add(Menu.NONE, 0, 0, R.string.edit_menu).setIcon(android.R.drawable.ic_menu_edit);
        menu.add(Menu.NONE, 1, 0, R.string.preferences_option).setIcon(android.R.drawable.ic_menu_preferences);
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
        case 0:
        	Intent toDetailView = new Intent(this, EditableDiveDetailViewActivity.class);
            Bundle b = new Bundle();
            b.putInt("diveId", currentDiveId);
    		toDetailView.putExtras(b);
            startActivity(toDetailView);
            return true;
        case 1:
            startActivity(new Intent(this, PreferencesActivity.class));
            return true;
        }
        return false;
    }
    
}
