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
import android.preference.PreferenceManager;
//Start of user code additional imports Accueil_CustomViewActivity
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import fr.ffessm.doris.android.async.InitialisationApplication_BgActivity;
import fr.ffessm.doris.android.async.TelechargeFiches_BgActivity;
import fr.ffessm.doris.android.async.TelechargePhotosFiches_BgActivity;
import fr.ffessm.doris.android.async.VerifieNouvellesFiches_BgActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.tools.Outils;
//End of user code
public class Accueil_CustomViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper>
//Start of user code additional implements Accueil_CustomViewActivity
	implements DataChangedListener
//End of user code
{
	
	//Start of user code constants Accueil_CustomViewActivity
//	static final int TELECHARGE_FICHE_MENU_ID = 1;	
	static final int TELECHARGE_PHOTO_FICHES_MENU_ID = 2;
//	static final int VERIFIE_MAJ_FICHES_MENU_ID = 3;
//	static final int VERIFIE_NOUVELLES_FICHES_MENU_ID = 4;
//	static final int RESET_DB_FROM_XML_MENU_ID = 5;
	
	Handler mHandler;
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
			PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        setContentView(R.layout.accueil_customview);
        //Start of user code onCreate Accueil_CustomViewActivity
	/*	// si pas de fiche alors il faut initialiser la base à partir du prefetched_DB
		RuntimeExceptionDao<Fiche, Integer> ficheDao = getHelper().getFicheDao();
    	if(ficheDao.countOf() == 0){
    		new InitialisationApplication_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");
    		
    		showToast("Veuillez patienter que la base de donnée s'initialise.");
		}*/
        
        // Defines a Handler object that's attached to the UI thread
        mHandler = new Handler(Looper.getMainLooper()) {
        	/*
             * handleMessage() defines the operations to perform when
             * the Handler receives a new Message to process.
             */
            @Override
            public void handleMessage(Message inputMessage) {
            	
            	if(inputMessage.obj != null ){
            		showToast((String) inputMessage.obj);
            	}
            	refreshScreenData();
            }

        };
        
		//End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
		//Start of user code onResume Accueil_CustomViewActivity
		//End of user code
	}
    //Start of user code additional code Accueil_CustomViewActivity
	public void onClickBtnListeFiches(View view){
		startActivity(new Intent(this, ListeFicheAvecFiltre_ClassListViewActivity.class));
    }
	
	/*public void reinitializeDBFromPrefetched(){
		//XMLHelper.loadDBFromXMLFile(getHelper().getDorisDBHelper(), this.getResources().openRawResource(R.raw.prefetched_db));

		new InitialisationApplication_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");
		showToast("Veuillez patienter que la base de donnée s'initialise.");
		
    }*/
	
	public void dataHasChanged(String textmessage){
		 Message completeMessage = mHandler.obtainMessage(1, textmessage);
         completeMessage.sendToTarget();
	}
	public Context getContext(){
		return this;
	}
	
	//End of user code

    /** refresh screen from data 
     */
    public void refreshScreenData() {
    	//Start of user code action when refreshing the screen Accueil_CustomViewActivity
    	StringBuffer sb = new StringBuffer();
    	RuntimeExceptionDao<Fiche, Integer> ficheDao = getHelper().getFicheDao();
    	sb.append("Nombres de fiches dans la base local: "+ficheDao.countOf());
    	RuntimeExceptionDao<PhotoFiche, Integer> photoFicheDao = getHelper().getPhotoFicheDao();
    	sb.append("\nNombres de photos référencées: "+photoFicheDao.countOf());
    	sb.append("\n\tNombres de photos téléchargées: "+Outils.getVignetteCount(this.getApplicationContext()));
    	double sizeInMiB = Outils.getVignettesDiskUsage(getApplicationContext())/(double)(1024.0*1024.0);
    	sb.append("\t("+String.format("%.2f", sizeInMiB)+" MiB)");
    	((TextView) findViewById(R.id.accueil_debug_text)).setText(sb.toString());
		//End of user code
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
		menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);

		//Start of user code additional onCreateOptionsMenu Accueil_CustomViewActivity
	//	menu.add(Menu.NONE, TELECHARGE_FICHE_MENU_ID, 1, R.string.telecharge_fiches_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(Menu.NONE, TELECHARGE_PHOTO_FICHES_MENU_ID, 2, R.string.telecharge_photofiches_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
    //    menu.add(Menu.NONE, VERIFIE_NOUVELLES_FICHES_MENU_ID, 4, R.string.verifie_nouvelles_fiches_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
    //    menu.add(Menu.NONE, RESET_DB_FROM_XML_MENU_ID, 5, R.string.reinitialise_a_partir_du_xml_menu_option).setIcon(android.R.drawable.ic_menu_preferences);
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
		
		//Start of user code additional menu action Accueil_CustomViewActivity
		/*	case TELECHARGE_FICHE_MENU_ID:
				new TelechargeFiches_BgActivity(getApplicationContext(), this.getHelper()).execute("");
				break; */
			case TELECHARGE_PHOTO_FICHES_MENU_ID:
				new TelechargePhotosFiches_BgActivity(getApplicationContext(), this.getHelper(), this).execute("");
				break;
		/*	case VERIFIE_NOUVELLES_FICHES_MENU_ID:
				new VerifieNouvellesFiches_BgActivity(getApplicationContext(), this.getHelper()).execute("");
				break;
			case RESET_DB_FROM_XML_MENU_ID:
				reinitializeDBFromPrefetched();
				break; */
		//End of user code
        }
        return false;
    }

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
