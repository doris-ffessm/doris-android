/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
 * Auteurs : Guillaume Mo <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */
package fr.ffessm.doris.android.activities;


import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
// Start of user code protectedDetailsParticipant_ElementViewActivity_additional_import
// End of user code

public class DetailsParticipant_ElementViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper>
// Start of user code protectedDetailsParticipant_ElementViewActivity_additional_implements
// End of user code
{
	
	protected int participantId;
	
	
	private static final String LOG_TAG = DetailsParticipant_ElementViewActivity.class.getCanonicalName();

// Start of user code protectedDetailsParticipant_ElementViewActivity_additional_attributes
	protected int participantNumeroDoris;
// End of user code
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailsparticipant_elementview);
        participantId = getIntent().getExtras().getInt("participantId");
        
		// Start of user code protectedDetailsParticipant_ElementViewActivity_onCreate
		// End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
	}
    
    
    private void refreshScreenData() {
    	// get our dao
    	RuntimeExceptionDao<Participant, Integer> entriesDao = getHelper().getParticipantDao();
		// Start of user code protectedDetailsParticipant_ElementViewActivity.refreshScreenData
    	Participant entry = entriesDao.queryForId(participantId);
    	entry.setContextDB(getHelper().getDorisDBHelper());

		((TextView) findViewById(R.id.detailsparticipant_elementview_nom)).setText(entry.getNom());
		participantNumeroDoris = entry.getNumeroParticipant();
		((TextView) findViewById(R.id.detailsparticipant_elementview_numeroparticipant)).setText(((Integer)participantNumeroDoris).toString());					
		
		
		/*SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    	((TextView) findViewById(R.id.detail_divedate)).setText(dateFormatter.format(entry.getDate()));
		
    	((TextView) findViewById(R.id.detail_divelocation)).setText(entry.getLocation());
    	
    	((TextView) findViewById(R.id.detail_divedepth)).setText(entry.getMaxdepth().toString());
    	
    	((TextView) findViewById(R.id.detail_diveduration)).setText(entry.getDuration().toString());
    	*/	
		// End of user code
    	
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);

		//Start of user code additional onCreateOptionsMenu

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
        	case 777:
                    startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
                    return true;
        
		//Start of user code additional menu action

		//End of user code
        }
        return false;
    }

	// Start of user code protectedDetailsParticipant_ElementViewActivity_additional_operations
    
    public void onClickBioComplete(View view){
		String url = Constants.getParticipantUrl(((Integer)participantNumeroDoris).toString());
		if (!url.isEmpty()) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			startActivity(intent);
		}
    }
	// End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
