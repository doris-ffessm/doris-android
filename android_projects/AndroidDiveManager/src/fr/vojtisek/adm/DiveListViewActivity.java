package fr.vojtisek.adm;

import java.util.ArrayList;
import java.util.List;

import fr.vojtisek.adm.data.DiveEntry;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class DiveListViewActivity extends Activity {

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.divelist_listview);

        ListView list = (ListView) findViewById(R.id.divelist_listview);
        list.setClickable(true);

        final List<DiveEntry> diveEntries = new ArrayList<DiveEntry>();
        diveEntries.add(new DiveEntry("05/09/2011", "St Malo", 20, "0:30"));
        diveEntries.add(new DiveEntry("03/10/2011", "Dinard", 25, "0:35"));
        diveEntries.add(new DiveEntry("22/09/2008", "St Malo", 12, "0:45"));

        DiveEntryAdapter adapter = new DiveEntryAdapter(this, diveEntries);

        
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
                System.out.println("sadsfsf");
               showToast(diveEntries.get(position).getDate());
            }
        });

        list.setAdapter(adapter);

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
