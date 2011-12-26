package fr.vojtisek.adm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.vojtisek.adm.data.DiveEntry;
import fr.vojtisek.adm.data.ORMLiteDBHelper;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DiveListViewActivity extends OrmLiteBaseActivity<ORMLiteDBHelper> implements OnItemClickListener{

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.divelist_listview);

        ListView list = (ListView) findViewById(R.id.divelist_listview);
        list.setClickable(true);

        /*final List<DiveEntry> diveEntries = new ArrayList<DiveEntry>();
        diveEntries.add(new DiveEntry(new Date(2011,12,1), "St Malo", 20, "0:30"));
        diveEntries.add(new DiveEntry(new Date(2011,17,7), "Dinard", 25, "0:35"));
        diveEntries.add(new DiveEntry(new Date(2008,4,15), "St Malo", 12, "0:45"));
*/
        DiveEntryAdapter adapter = new DiveEntryAdapter(this, getHelper().getDiveEntriesDao());

        
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// get our dao
		//RuntimeExceptionDao<DiveEntry, Integer> simpleDao = getHelper().getDiveEntriesDao();
		// query for all of the data objects in the database
		//List<DiveEntry> diveEntries = simpleDao.queryForAll();
	} 
	
	
	public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
		//showToast(view.toString() + ", "+ view.getId());
		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        //tvLabel.setText(dateFormatter.format(entry.getDate()));
        showToast(dateFormatter.format(((DiveEntry)view.getTag()).getDate()));
        Intent toDetailView = new Intent(this, DiveDetailViewActivity.class);
        Bundle b = new Bundle();
        b.putInt("diveId", ((DiveEntry)view.getTag()).getId());
		toDetailView.putExtras(b);
        startActivity(toDetailView);
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
	
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    
	/*@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String item = (String) getListAdapter().getItem(position);
		Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	}*/
}
