/*******************************************************************************
 * Copyright (c) 2012 Vojtisek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Didier Vojtisek - initial API and implementation
 *******************************************************************************/
package fr.ffessm.doris.android.activities;


import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;


public class ParticipantDetailViewElementViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper>{
	
	protected int participantId;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.participantdetailview_elementview);
        participantId = getIntent().getExtras().getInt("participantId");
        //findViewById(R.id.divedate);
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
	}
    
    
    private void refreshScreenData() {
    	// get our dao
    	RuntimeExceptionDao<Participant, Integer> entriesDao = getHelper().getParticipantDao();
    	Participant entry = entriesDao.queryForId(participantId);
    	
		((TextView) findViewById(R.id.participantdetailview_elementview_name)).setText(entry.getName());
		((TextView) findViewById(R.id.participantdetailview_elementview_iddoris)).setText(((Integer)entry.getIdDoris()).toString());					
    	/*SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    	((TextView) findViewById(R.id.detail_divedate)).setText(dateFormatter.format(entry.getDate()));
		
    	((TextView) findViewById(R.id.detail_divelocation)).setText(entry.getLocation());
    	
    	((TextView) findViewById(R.id.detail_divedepth)).setText(entry.getMaxdepth().toString());
    	
    	((TextView) findViewById(R.id.detail_diveduration)).setText(entry.getDuration().toString());
    	*/
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, 777, 0, R.string.mainpreferences_menu_title).setIcon(android.R.drawable.ic_menu_preferences);
        
		//Start of user code additional onCreateOptionsMenu

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
        	case 777:
                    startActivity(new Intent(this, MainPreferencesActivity.class));
                    return true;
        
		//Start of user code additional menu action

		//End of user code
        }
        return false;
    }

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
