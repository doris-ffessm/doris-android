/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
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
package fr.ffessm.doris.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import fr.ffessm.doris.android.async.TelechargePhotosAsync_BgActivity;
import fr.ffessm.doris.android.async.VerifieMAJFiche_BgActivity;
import fr.ffessm.doris.android.async.VerifieMAJFiches_BgActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.tools.App_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;


/** Classe globale pour accéder aux informations générales de l'application */
public class DorisApplicationContext {

	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getSimpleName();
	
	/** singleton intance */
	private static DorisApplicationContext instance = null;
	
	/** singleton contructor */
	private DorisApplicationContext(){
		
	}
	
	public static DorisApplicationContext getInstance(){
		if(instance == null) instance = new DorisApplicationContext();
		return instance;
	}
	
	// used to get a pointer on running background activities, usefull when on onCreate onDestroy in case of configuration changes like rotation
	public TelechargePhotosAsync_BgActivity telechargePhotosFiches_BgActivity = null;
	public VerifieMAJFiche_BgActivity verifieMAJFiche_BgActivity = null;
	public VerifieMAJFiches_BgActivity verifieMAJFiches_BgActivity = null;
	
	/** listener that have registered for being notified of data changes */
	private ArrayList<DataChangedListener>  dataChangeListeners = new ArrayList<DataChangedListener>();
	
	public List<DataChangedListener> getDataChangeListeners(){
		synchronized(dataChangeListeners){
			return new ArrayList<DataChangedListener>(dataChangeListeners);
		}
	}
	public void addDataChangeListeners(DataChangedListener listener){
		synchronized(dataChangeListeners){
			dataChangeListeners.add(listener);
		}
	}
	public void removeDataChangeListeners(DataChangedListener listener){
		synchronized(dataChangeListeners){
			dataChangeListeners.remove(listener);
		}
	}
	/**
	 * Averti les listener que les données ont changées
	 */
	public void notifyDataHasChanged(String message){
		for (DataChangedListener listener :  getDataChangeListeners()) {
			listener.dataHasChanged(message);
		}
	}
	
	
	/**
	 * Gère les valeur par défaut en fonction du type d'appareil sur lequel l'application est installée.
	 * Une fois exécuté, le valeur sont stockée et ne sont plus modifiées par ce traitement afin de conserver les choix utilisateur.
	 * @param context
	 */
	public void ensureDefaultPreferencesInitialization(Context context){
		
		
		final Param_Outils paramOutils = new Param_Outils(context);
		
		int screenlayout_size = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		int screenlayout_density= context.getResources().getDisplayMetrics().densityDpi;
		Log.d(LOG_TAG, "ensureDefaultPreferencesInitialization "+screenlayout_density+" "+screenlayout_size);
		// Taille par défaut des images téléchargées en mode on-line
		String qualitePhotoSgtring = paramOutils.getParamString(R.string.pref_key_mode_connecte_qualite_photo,"");
		if(qualitePhotoSgtring.equals("")){
			SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
			// adapte la taille par défaut des images téléchargée (mode online) en fonction de la taille de l'écran et la densité 
			if(screenlayout_size >= Configuration.SCREENLAYOUT_SIZE_LARGE || screenlayout_density>=DisplayMetrics.DENSITY_HIGH){
				Log.d(LOG_TAG, "ensureDefaultPreferencesInitialization HI_RES");
		    	// LARGE ou XLARGE -> par defaut HI_RES
				ed.putString(context.getString(R.string.pref_key_mode_connecte_qualite_photo), "HI_RES");
			}
			else{
				Log.d(LOG_TAG, "ensureDefaultPreferencesInitialization MED_RES");
				// par defaut MED_RES
				ed.putString(context.getString(R.string.pref_key_mode_connecte_qualite_photo), "MED_RES");				
			}
			ed.commit();
		}
		// TODO fatoriser ce code qui semble redondant plutot
		// Taille des icones des listes
		String iconSizeString = paramOutils.getParamString(R.string.pref_key_list_icon_size, "");
		if(iconSizeString.equals("")){
			SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
			// adapte la taille par défaut des icones en fonction de la densité et la taille de l'écran 
			switch(screenlayout_density)
			{
			case DisplayMetrics.DENSITY_LOW:
				switch(screenlayout_size) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			    	ed.putString(context.getString(R.string.pref_key_list_icon_size), "96");
					break;
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	ed.putString(context.getString(R.string.pref_key_list_icon_size), "64");
					break;
				default: 
					ed.putString(context.getString(R.string.pref_key_list_icon_size), "48");
				}
			    break;
			case DisplayMetrics.DENSITY_MEDIUM:
				switch(screenlayout_size) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			    	ed.putString(context.getString(R.string.pref_key_list_icon_size), "128");
					break;
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	ed.putString(context.getString(R.string.pref_key_list_icon_size), "96");
					break;
				default: 
					ed.putString(context.getString(R.string.pref_key_list_icon_size), "64");
				}
			    break;
			case DisplayMetrics.DENSITY_HIGH:
				switch(screenlayout_size) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			    	ed.putString(context.getString(R.string.pref_key_list_icon_size), "192");
					break;
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	ed.putString(context.getString(R.string.pref_key_list_icon_size), "128");
					break;
				default: 
					ed.putString(context.getString(R.string.pref_key_list_icon_size), "96");
				}
			    break;
			case DisplayMetrics.DENSITY_XHIGH:
				switch(screenlayout_size) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	ed.putString(context.getString(R.string.pref_key_list_icon_size), "192");
					break;
				default: 
					ed.putString(context.getString(R.string.pref_key_list_icon_size), "128");
				}
			    break;
			case DisplayMetrics.DENSITY_XXHIGH:
				ed.putString(context.getString(R.string.pref_key_list_icon_size), "192");
			    break;
			default:
				ed.putString(context.getString(R.string.pref_key_list_icon_size), "64");
			}
			ed.commit();
		}
		// Taille des icones des page accueil
		String acceuilIconSizeString = paramOutils.getParamString(R.string.pref_key_accueil_icon_size, "");
		if(acceuilIconSizeString.equals("")){
			SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
			// adapte la taille par défaut des icones en fonction de la densité et la taille de l'écran 
			switch(screenlayout_density)
			{
			case DisplayMetrics.DENSITY_LOW:
				switch(screenlayout_size) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			    	ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "96");
					break;
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "64");
					break;
				default: 
					ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "48");
				}
			    break;
			case DisplayMetrics.DENSITY_MEDIUM:
				switch(screenlayout_size) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			    	ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "96");
					break;
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "64");
					break;
				default: 
					ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "48");
				}
			    break;
			case DisplayMetrics.DENSITY_HIGH:
				switch(screenlayout_size) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			    	ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "128");
					break;
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "96");
					break;
				default: 
					ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "64");
				}
			    break;
			case DisplayMetrics.DENSITY_XHIGH:
				switch(screenlayout_size) {
				case Configuration.SCREENLAYOUT_SIZE_XLARGE:
			    case Configuration.SCREENLAYOUT_SIZE_LARGE:
			    	ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "128");
					break;
				default: 
					ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "96");
				}
			    break;
			case DisplayMetrics.DENSITY_XXHIGH:
				ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "128");
			    break;
			default:
				ed.putString(context.getString(R.string.pref_key_accueil_icon_size), "64");
			}
			ed.commit();
		}
		
	}
	
}
