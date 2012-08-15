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


import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;


public class CardListViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper> implements OnItemClickListener{
	

	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.cardlistview_listview);

		ListView list = (ListView) findViewById(R.id.cardlistview_listview);
        list.setClickable(true);
        CardListViewAdapter adapter = new CardListViewAdapter(this, getHelper().getCardDao());

        
        list.setOnItemClickListener(this);

        list.setAdapter(adapter);
	}
	


	public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
		showToast(view.toString() + ", "+ view.getId());
		/*SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        //tvLabel.setText(dateFormatter.format(entry.getDate()));
        showToast(dateFormatter.format(((DiveEntry)view.getTag()).getDate()));
        Intent toDetailView = new Intent(this, DiveDetailViewActivity.class);
        Bundle b = new Bundle();
        b.putInt("diveId", ((DiveEntry)view.getTag()).getId());
		toDetailView.putExtras(b);
        startActivity(toDetailView);*/
    }
	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
