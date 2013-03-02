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


import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Fiche;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

public class ListeFicheAvecFiltreAdapter extends BaseAdapter{
	
private Context context;

    private List<Fiche> ficheList;

	public ListeFicheAvecFiltreAdapter(Context context, RuntimeExceptionDao<Fiche, Integer> entriesDao) {
		super();
		this.context = context;
		// TODO find a way to query in a lazy way
		this.ficheList = entriesDao.queryForAll();
	}

	@Override
	public int getCount() {
		return ficheList.size();
	}

	@Override
	public Object getItem(int position) {
		return ficheList.get(position);

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		Fiche entry = ficheList.get(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listeficheavecfiltre_listviewrow, null);
        }
       
		// set data in the row 
		TextView tvLabel = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
				labelSB.append(entry.getImageVignette().toString());
			
			labelSB.append(" ");
        tvLabel.setText(labelSB.toString());

        TextView tvDetails = (TextView) convertView.findViewById(R.id.listeficheavecfiltre_listviewrow_details);
		StringBuilder detailsSB = new StringBuilder();
		detailsSB.append(entry.getNom().toString());
		detailsSB.append(" ");
        tvDetails.setText(detailsSB.toString());
		
        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.listeficheavecfiltre_listviewrow);
        llRow.setTag(entry);
        
        return convertView;

	}

	
}
