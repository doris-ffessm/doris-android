/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2014 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
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

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import fr.ffessm.doris.android.R;

//Start of user code Preference preference activity additional imports
import java.io.File;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask.Status;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.widget.Toast;
import android.util.Log;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.async.VerifieMAJFiche_BgActivity;
import fr.ffessm.doris.android.async.VerifieMAJFiches_BgActivity;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.App_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;

//End of user code

public class Preference_PreferenceViewActivity  extends android.preference.PreferenceActivity {

	
	//Start of user code Preference preference activity additional attributes
	private static final String LOG_TAG = App_Outils.class.getCanonicalName();
	final Context context = this;
	
    final Param_Outils paramOutils = new Param_Outils(context);
    final Photos_Outils photosOutils = new Photos_Outils(context);
    Disque_Outils disqueOutils = new Disque_Outils(context);
	//End of user code

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference); 
		//Start of user code Preference preference activity additional onCreate
        
        // Si téléchargements en tâche de fond, il est arrêté
        TelechargePhotosAsync_BgActivity telechargePhotosFiches_BgActivity = DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity;		    	
    	if(telechargePhotosFiches_BgActivity != null && telechargePhotosFiches_BgActivity.getStatus() == Status.RUNNING) {
    		Toast.makeText(this, R.string.bg_notifToast_arretTelecharg, Toast.LENGTH_LONG).show();
    		DorisApplicationContext.getInstance().telechargePhotosFiches_BgActivity.cancel(true);
    	}
    	
    	VerifieMAJFiches_BgActivity verifieMAJFiches_BgAct = DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity;
    	if(verifieMAJFiches_BgAct != null && verifieMAJFiches_BgAct.getStatus() == Status.RUNNING){ 
    		Toast.makeText(this, R.string.bg_notifToast_arretTelecharg, Toast.LENGTH_LONG).show();
    		DorisApplicationContext.getInstance().verifieMAJFiches_BgActivity.cancel(true);
    	}
    	
    	VerifieMAJFiche_BgActivity verifieMAJFiche_BgAct = DorisApplicationContext.getInstance().verifieMAJFiche_BgActivity;
    	if(verifieMAJFiche_BgAct != null && verifieMAJFiche_BgAct.getStatus() == Status.RUNNING){ 		
    		Toast.makeText(this, R.string.bg_notifToast_arretTelecharg, Toast.LENGTH_LONG).show();
    		DorisApplicationContext.getInstance().verifieMAJFiche_BgActivity.cancel(true);
    	}
    	
        /* Permet d'afficher directement une sous-partie des préférences
        *  Utile depuis Aide ou EtatHorsLigne, etc.
        */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
	        String typeParam = bundle.getString("type_parametre");
	        String param = bundle.getString("parametre");
	        
	        if (typeParam != null) {
	        	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - typeParam : "+typeParam);
	        	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - param : "+param);
	        	PreferenceScreen preferenceScreen = (PreferenceScreen) findPreference(typeParam);
        		if (param != null) {
		        	int pos = findPreference(param).getOrder();
		        	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onCreate() - pos "+param+" : "+pos);
		        	preferenceScreen.onItemClick( null, null, pos, 0 ); 
        		}
	        }
        }


        final Preference btnVideVig = (Preference)getPreferenceManager().findPreference("btn_reset_vig");
        if(btnVideVig != null) {
	        btnVideVig.setSummary(getVigSummary());
        	btnVideVig.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 @Override
                 public boolean onPreferenceClick(Preference arg0) {
                	 
                	 AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(context);
                	 alertDialogbD.setMessage(context.getString(R.string.mode_precharg_reset_confirmation));
                	 alertDialogbD.setCancelable(true);
                	 
                	 // On vide le dossier si validé
                	 alertDialogbD.setPositiveButton(context.getString(R.string.btn_yes),
            			 new DialogInterface.OnClickListener() {
	                         public void onClick(DialogInterface dialog, int id) {
	                        	 Log.d(LOG_TAG, "onCreate() - onPreferenceClick() : btn_reset_vig");
	                        	 disqueOutils.clearFolder(photosOutils.getImageFolderVignette(), 0);

	                        	 paramOutils.setParamInt(R.string.pref_key_nbphotos_recues_vignettes, photosOutils.getImageCount(ImageType.VIGNETTE));
	                        	 paramOutils.setParamLong(R.string.pref_key_size_folder_vignettes, photosOutils.getPhotoDiskUsage(ImageType.VIGNETTE));
	                        	 btnVideVig.setSummary(getVigSummary());	
	                         }
                     	});
                	 // Abandon donc Rien à Faire
                	 alertDialogbD.setNegativeButton(context.getString(R.string.btn_annul),
                         new DialogInterface.OnClickListener() {
                         	public void onClick(DialogInterface dialog, int id) {
                         		dialog.cancel();
                         	}
                     	});

                     AlertDialog alertDialog = alertDialogbD.create();
                     alertDialog.show();

                	 return true;
                 }
             }); 
         }


        final Preference btnVideMedRes = (Preference)getPreferenceManager().findPreference("btn_reset_med_res");      
        if(btnVideMedRes != null) {
	        btnVideMedRes.setSummary(getMedResSummary());

        	btnVideMedRes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 @Override
                 public boolean onPreferenceClick(Preference arg0) {
                	 
                	 AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(context);
                	 alertDialogbD.setMessage(context.getString(R.string.mode_precharg_reset_confirmation));
                	 alertDialogbD.setCancelable(true);
                	 
                	 // On vide le dossier si validé
                	 alertDialogbD.setPositiveButton(context.getString(R.string.btn_yes),
            			 new DialogInterface.OnClickListener() {
	                         public void onClick(DialogInterface dialog, int id) {
	                        	 Log.d(LOG_TAG, "onCreate() - onPreferenceClick() : btn_reset_med_res");
	                        	 disqueOutils.clearFolder(photosOutils.getImageFolderMedRes(), 0);

	                        	 paramOutils.setParamInt(R.string.pref_key_nbphotos_recues_med_res, photosOutils.getImageCount(ImageType.MED_RES));
	                        	 paramOutils.setParamLong(R.string.pref_key_size_folder_med_res, photosOutils.getPhotoDiskUsage(ImageType.MED_RES));
	                        	 btnVideMedRes.setSummary(getMedResSummary());
	                         }
                     	});
                	 // Abandon donc Rien à Faire
                	 alertDialogbD.setNegativeButton(context.getString(R.string.btn_annul),
                         new DialogInterface.OnClickListener() {
                         	public void onClick(DialogInterface dialog, int id) {
                         		dialog.cancel();
                         	}
                     	});

                     AlertDialog alertDialog = alertDialogbD.create();
                     alertDialog.show();
                	 
                	 return true;
                 }
             });     
         }
        

        final Preference btnVideHiRes = (Preference)getPreferenceManager().findPreference("btn_reset_hi_res");      
        if(btnVideHiRes != null) {
        	btnVideHiRes.setSummary(getHiResSummary());
        
        	btnVideHiRes.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 @Override
                 public boolean onPreferenceClick(Preference arg0) {
                	 
                	 AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(context);
                	 alertDialogbD.setMessage(context.getString(R.string.mode_precharg_reset_confirmation));
                	 alertDialogbD.setCancelable(true);
                	 
                	 // On vide le dossier si validé
                	 alertDialogbD.setPositiveButton(context.getString(R.string.btn_yes),
            			 new DialogInterface.OnClickListener() {
	                         public void onClick(DialogInterface dialog, int id) {
	                        	 Log.d(LOG_TAG, "onCreate() - onPreferenceClick() : btn_reset_hi_res");
	                        	 disqueOutils.clearFolder(photosOutils.getImageFolderHiRes(), 0);

	                        	 paramOutils.setParamInt(R.string.pref_key_nbphotos_recues_hi_res, photosOutils.getImageCount(ImageType.HI_RES));
	                        	 paramOutils.setParamLong(R.string.pref_key_size_folder_hi_res, photosOutils.getPhotoDiskUsage(ImageType.HI_RES));
	                        	 btnVideHiRes.setSummary(getHiResSummary());
	                         }
                     	});
                	 // Abandon donc Rien à Faire
                	 alertDialogbD.setNegativeButton(context.getString(R.string.btn_annul),
                         new DialogInterface.OnClickListener() {
                         	public void onClick(DialogInterface dialog, int id) {
                         		dialog.cancel();
                         	}
                     	});

                     AlertDialog alertDialog = alertDialogbD.create();
                     alertDialog.show();
                	 
                	 return true;
                 }
             });     
         }


        final Preference btnVideCache = (Preference)getPreferenceManager().findPreference("btn_reset_cache");      
        if(btnVideCache != null) {
        	
	        btnVideCache.setSummary(getCacheSummary());

        	btnVideCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                 @Override
                 public boolean onPreferenceClick(Preference arg0) {
                	 
                	 AlertDialog.Builder alertDialogbD = new AlertDialog.Builder(context);
                	 alertDialogbD.setMessage(context.getString(R.string.mode_precharg_reset_confirmation));
                	 alertDialogbD.setCancelable(true);
                	 
                	 // On vide le cache si validé
                	 alertDialogbD.setPositiveButton(context.getString(R.string.btn_yes),
            			 new DialogInterface.OnClickListener() {
	                         public void onClick(DialogInterface dialog, int id) {
                        	 try {
	                     	        File dir = context.getCacheDir();
	                     	        if (dir != null && dir.isDirectory()) {
	                     	            deleteDir(dir);
	                     	        }
	                     	    } catch (Exception e) {}
	                     	 
	                        	 btnVideCache.setSummary(getCacheSummary());
                        	 }
                     	});
                	 // Abandon donc Rien à Faire
                	 alertDialogbD.setNegativeButton(context.getString(R.string.btn_annul),
                         new DialogInterface.OnClickListener() {
                         	public void onClick(DialogInterface dialog, int id) {
                         		dialog.cancel();
                         	}
                     	});

                     AlertDialog alertDialog = alertDialogbD.create();
                     alertDialog.show();

                     return true;
                 }
             });     
         }
        

		//End of user code
    }

    @Override
	protected void onResume() {
		super.onResume(); 
		//Start of user code Preference preference activity additional onResume
		//End of user code
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
    	String message = ""+item.getItemId()+" - "+item.getGroupId()+" - "+item.toString();
    	if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onOptionsItemSelected() - menu : "+message);  
    	Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    	
        /* switch (item.getItemId()) {
            case 0:
                startActivity(new Intent(this, AndroidDiveManagerMainActivity.class));
                return true;
        } */
		//End of user code
        return false;
    }

	
	//Start of user code Preference preference activity additional operations
    
    private String getVigSummary() {
    	String txt = context.getString(R.string.mode_precharg_reset_vig_summary);
    	txt = txt.replace("@nbPh", ""+paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_vignettes, 0)) ;
    	txt = txt.replace("@size", ""+disqueOutils.getHumanDiskUsage(paramOutils.getParamLong(R.string.pref_key_size_folder_vignettes, 0L ) ) ) ;
    	return txt;
    }
    private String getMedResSummary() {
        String txt = getApplicationContext().getString(R.string.mode_precharg_reset_med_res_summary); 
        txt = txt.replace("@nbPh", ""+paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_med_res, 0)) ;
        txt = txt.replace("@size", ""+disqueOutils.getHumanDiskUsage(paramOutils.getParamLong(R.string.pref_key_size_folder_med_res, 0L ) ) ) ;
    	return txt;
    }
    private String getHiResSummary() {
        String txt = getApplicationContext().getString(R.string.mode_precharg_reset_hi_res_summary); 
        txt = txt.replace("@nbPh", ""+paramOutils.getParamInt(R.string.pref_key_nbphotos_recues_hi_res, 0)) ;
        txt = txt.replace("@size", ""+disqueOutils.getHumanDiskUsage(paramOutils.getParamLong(R.string.pref_key_size_folder_hi_res, 0L ) ) ) ;
    	return txt;
    }
    private String getCacheSummary() {
    	int nbFichiersDansCache = 0;
    	for (File child:getApplicationContext().getCacheDir().listFiles()) {
     		if (child.getName().equals("picasso-cache") ) {
     			nbFichiersDansCache = child.listFiles().length;
  	     		break;
     		}
     	}
    	String txt = getApplicationContext().getString(R.string.mode_precharg_reset_cache_summary);
    	// La division par 2 est très sale mais c'est bien le plus rapide :-)
    	// En le dossier est bizarrement structuré avec des dossiers renommés, mais en gros il y en a 2 par fichiers en cache
     	txt = txt.replace("@nbPh", ""+ Math.round(nbFichiersDansCache/2) );
     	txt = txt.replace("@size", ""+disqueOutils.getHumanDiskUsage(disqueOutils.getDiskUsage(getApplicationContext().getCacheDir() ) ) ) ;
     	return txt;
    }
    
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    
	//End of user code
}
