
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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.ffessm.doris.android.R;

public class MainPreferencesActivity  extends android.preference.PreferenceActivity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.mainpreferences); 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		//Start of user code preference specific menu definition
        // menu.add(Menu.NONE, 0, 0, "Back to main menu");
		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

		//Start of user code preference specific menu action
        /* switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, AndroidDiveManagerMainActivity.class));
                return true;
        } */
		//End of user code
        return false;
    }
}
