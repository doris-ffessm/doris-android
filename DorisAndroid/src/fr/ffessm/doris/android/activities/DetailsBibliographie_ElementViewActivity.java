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


import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
// Start of user code protectedDetailsBibliographie_ElementViewActivity_additional_import
// End of user code

public class DetailsBibliographie_ElementViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
// Start of user code protectedDetailsBibliographie_ElementViewActivity_additional_implements
// End of user code
{
	
	protected int entreeBibliographieId;
	
	private static final String LOG_TAG = DetailsBibliographie_ElementViewActivity.class.getCanonicalName();

// Start of user code protectedDetailsBibliographie_ElementViewActivity_additional_attributes
// End of user code
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.detailsbibliographie_elementview);

		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

        entreeBibliographieId = getIntent().getExtras().getInt("entreeBibliographieId");
        
		// Start of user code protectedDetailsBibliographie_ElementViewActivity_onCreate
		// End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
	}
    
    
    private void refreshScreenData() {
    	// get our dao
    	RuntimeExceptionDao<EntreeBibliographie, Integer> entriesDao = getHelper().getEntreeBibliographieDao();
		// Start of user code protectedDetailsBibliographie_ElementViewActivity.refreshScreenData
    	EntreeBibliographie entry = entriesDao.queryForId(entreeBibliographieId);
    	entry.setContextDB(getHelper().getDorisDBHelper());

		((TextView) findViewById(R.id.detailsbibliographie_elementview_numerodoris)).setText(((Integer)entry.getNumeroDoris()).toString());					
		((TextView) findViewById(R.id.detailsbibliographie_elementview_titre)).setText(entry.getTitre());
		((TextView) findViewById(R.id.detailsbibliographie_elementview_auteurs)).setText(entry.getAuteurs());
		((TextView) findViewById(R.id.detailsbibliographie_elementview_annee)).setText(entry.getAnnee());
		((TextView) findViewById(R.id.detailsbibliographie_elementview_details)).setText(entry.getDetails());
		//((TextView) findViewById(R.id.detailsbibliographie_elementview_cleurlillustration)).setText(entry.getCleURLIllustration());
		
		String urlString = Constants.getBibliographieUrl( ""+entry.getNumeroDoris() ); 
		SpannableString richtext = new SpannableString(urlString);
		richtext.setSpan(new URLSpan(urlString), 0, urlString.length(), 0);
		TextView contenuUrl = (TextView) findViewById(R.id.detailsbibliographie_elementview_liensite);
		contenuUrl.setText(richtext);
		contenuUrl.setMovementMethod(LinkMovementMethod.getInstance());
		
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
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.detailsbibliographie_elementview_actions, menu);
		// add additional programmatic options in the menu
		//Start of user code additional onCreateOptionsMenu DetailsBibliographie_EditableElementViewActivity

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
		// behavior of option menu
        switch (item.getItemId()) {
			case R.id.detailsbibliographie_elementview_action_preference:
	        	startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
	            return true;
			//Start of user code additional menu action DetailsBibliographie_ElementViewActivity
	
			//End of user code
			default:
                return super.onOptionsItemSelected(item);
        }    	
    }

	//  ------------ dealing with Up button
	@Override
	public Intent getSupportParentActivityIntent() {
		//Start of user code getSupportParentActivityIntent DetailsBibliographie_ClassListViewActivity
		// navigates to the parent activity
		return new Intent(this, Accueil_CustomViewActivity.class);
		//End of user code
	}
	@Override
	public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
		//Start of user code onCreateSupportNavigateUpTaskStack DetailsBibliographie_ClassListViewActivity
		super.onCreateSupportNavigateUpTaskStack(builder);
		//End of user code
	}

	// Start of user code protectedDetailsBibliographie_ElementViewActivity_additional_operations
	// End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
