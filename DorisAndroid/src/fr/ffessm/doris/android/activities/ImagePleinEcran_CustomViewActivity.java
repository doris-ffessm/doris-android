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


import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.tools.ThemeUtil;
import fr.vojtisek.genandroid.genandroidlib.activities.OrmLiteActionBarActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.j256.ormlite.dao.RuntimeExceptionDao;

//Start of user code additional imports ImagePleinEcran_CustomViewActivity

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Textes_Outils;
//End of user code
public class ImagePleinEcran_CustomViewActivity extends OrmLiteActionBarActivity<OrmLiteDBHelper>
//Start of user code additional implements ImagePleinEcran_CustomViewActivity
//End of user code
{
	
	//Start of user code constants ImagePleinEcran_CustomViewActivity
	private static final String LOG_TAG = ImagePleinEcran_CustomViewActivity.class.getSimpleName();
	
	protected ImagePleinEcran_Adapter adapter;
	protected ViewPager viewPager;
	int ficheId;
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		ThemeUtil.onActivityCreateSetTheme(this);
        setContentView(R.layout.imagepleinecran_customview);
		ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);

        //Start of user code onCreate ImagePleinEcran_CustomViewActivity
        
        viewPager = (ViewPager) findViewById(R.id.imagepleinecran_pager);
        
        Intent i = getIntent();
        // récupération de la position dans du click dans la vue précédente
		int position = i.getIntExtra("position", 0);
		// récupération info qui permettra de retrouver la liste des images à afficher
		ficheId = i.getIntExtra("ficheId", 0);

		
		//TODO calcul de la liste d'images à partir de l'Id de la fiche  (pour plus tard, la même chose mais pour les photos principales d'un groupe ?) 
		//ArrayList<String> imagePaths;
		
		RuntimeExceptionDao<Fiche, Integer> entriesDao = getHelper().getFicheDao();
    	Fiche entry = entriesDao.queryForId(ficheId);
    	entry.setContextDB(getHelper().getDorisDBHelper());
    	//Collection<PhotoFiche> photosFiche = entry.getPhotosFiche(); 
    	ArrayList<PhotoFiche> photosFicheArrayList = new ArrayList<PhotoFiche>(entry.getPhotosFiche());
		
    	actionBar.setTitle(entry.getNomCommun().replaceAll("\\{\\{[^\\}]*\\}\\}", ""));
    	Textes_Outils textesOutils = new Textes_Outils(this);
		actionBar.setSubtitle(textesOutils.textToSpannableStringDoris(entry.getNomScientifique()));
    	
        // Image adapter
        adapter = new ImagePleinEcran_Adapter(ImagePleinEcran_CustomViewActivity.this, photosFicheArrayList);
        
        viewPager.setAdapter(adapter);
        
        // affiche l'image sélectionnée en premier
     	viewPager.setCurrentItem(position);
     	
     	// info de debug de Picasso
     	Param_Outils paramOutils = new Param_Outils(this.getApplicationContext());
     	if (paramOutils.getParamBoolean(R.string.pref_key_affichage_debug, false)){
     		Picasso.with(this).setDebugging(BuildConfig.DEBUG);
     	}
		//End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
		//Start of user code onResume ImagePleinEcran_CustomViewActivity
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    	if(prefs.getBoolean(getString(R.string.pref_key_imagepleinecran_aff_zoomcontrol), false) !=  affZoomControl){
    		affZoomControl = prefs.getBoolean(getString(R.string.pref_key_imagepleinecran_aff_zoomcontrol), false);
    		ViewPager view = (ViewPager) findViewById(R.id.imagepleinecran_pager);
    		view.invalidate();
    		// ne fonctionne pas :-(
    	}   
		//End of user code
	}
    //Start of user code additional code ImagePleinEcran_CustomViewActivity
	
    boolean affZoomControl = false;
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	
    	Log.d(LOG_TAG, "onDestroy()");
    	Log.d(LOG_TAG, "onDestroy() - isFinishing() : "+isFinishing());


    }
    	
	//End of user code

    /** refresh screen from data 
     */
    public void refreshScreenData() {
    	//Start of user code action when refreshing the screen ImagePleinEcran_CustomViewActivity
		//End of user code
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.imagepleinecran_customview_actions, menu);
		// add additional programmatic options in the menu
		//Start of user code additional onCreateOptionsMenu ImagePleinEcran_CustomViewActivity

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
			case R.id.imagepleinecran_customview_action_preference:
	        	startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
	            return true;
			//Start of user code additional menu action ImagePleinEcran_CustomViewActivity

		    //End of user code
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
	        	TaskStackBuilder.create(this)
	                // Add all of this activity's parents to the back stack
	                .addNextIntentWithParentStack(getSupportParentActivityIntent())
	                // Navigate up to the closest parent
	                .startActivities();
	            return true;
			default:
                return super.onOptionsItemSelected(item);
        }
    }

	//  ------------ dealing with Up button
	@Override
	public Intent getSupportParentActivityIntent() {
		//Start of user code getSupportParentActivityIntent ImagePleinEcran_CustomViewActivity
		// navigates to the parent activity
		Intent toDetailView = new Intent(this, DetailsFiche_ElementViewActivity.class);
        Bundle b = new Bundle();
        b.putInt("ficheId", ficheId);
		toDetailView.putExtras(b);
		return toDetailView;
		//End of user code
	}
	@Override
	public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
		//Start of user code onCreateSupportNavigateUpTaskStack ImagePleinEcran_CustomViewActivity
		super.onCreateSupportNavigateUpTaskStack(builder);
		//End of user code
	}
	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
