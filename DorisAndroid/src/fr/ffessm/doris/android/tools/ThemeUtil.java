package fr.ffessm.doris.android.tools;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
}
