/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2015 - FFESSM
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
package fr.ffessm.doris.android.tools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.Log;
import fr.ffessm.doris.android.R;

public class ThemeUtil {
	
	private static final String LOG_TAG = ThemeUtil.class.getSimpleName(); 
	
	/**
	 * Changes the theme of the activity, the activity is restarted (must not be called in onCreate()) 
	 * @param activity
	 * @param theme
	 */
	public static void updateActivityTheme(Activity activity)
	{
		activity.finish();
		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	public static void onActivityCreateSetTheme(Activity activity)
	{
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
    	
    	String theme = prefs.getString(activity.getString(R.string.pref_key_theme), "DorisAndroid");
    	if(theme.equals("DorisAndroid")){
    		Log.d(LOG_TAG, theme);
    		activity.setTheme(R.style.Theme_AppDorisAndroid);
    	}
    	else if(theme.equals("PureBlack")) {
    		Log.d(LOG_TAG, "theme PureBlack ="+ theme);
    		Log.d(LOG_TAG, theme);
    		activity.setTheme(R.style.Theme_AppPureBlack);
    	}
    	else if(theme.equals("Holo")) {
    		Log.d(LOG_TAG, "theme Holo ="+ theme);
    		activity.setTheme(R.style.Theme_AppHolo);
    	}
    	else if(theme.equals("HoloLight")) {
    		Log.d(LOG_TAG, "theme HoloLight ="+ theme);
    		activity.setTheme(R.style.Theme_AppHoloLight);
    	}
	}
	
	// Permet d'obtenir id de l'image pour setImageResource (différente selon les thèmes)
	public static int attrToResId(Activity activity, int attr) {
		
		TypedArray a = activity.getTheme().obtainStyledAttributes(new int[] { attr });
		//Log.d(LOG_TAG, "attrToResId() - a.getResourceId(0, 0) ="+ a.getResourceId(0, 0));
		return a.getResourceId(0, 0);
	}
}
