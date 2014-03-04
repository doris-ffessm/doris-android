package fr.ffessm.doris.android.tools;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Param_Outils {
	private final String LOG_TAG = Param_Outils.class.getCanonicalName();
	
	private Context context;
	
	public Param_Outils(Context context){
		this.context = context;
	}


	/* Lecture Paramètres */
	
	public String getStringKeyParam(int inParam) {
		return context.getResources().getResourceEntryName(inParam);
	}
	public String getStringNameParam(int inParam) {
		return context.getString(inParam);
	}
	public boolean getParamBoolean(int inParam, boolean inValDef) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(context.getString(inParam), inValDef);
	}
	public String getParamString(int inParam, String inValDef) {
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamString() - param : " + inParam + "-" + context.getString(inParam) );
		return PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(inParam), inValDef);
	}
	public long getParamLong(int inParam, Long inValDef) {
		if (PreferenceManager.getDefaultSharedPreferences(context).contains(context.getString(inParam)) ) {
			return PreferenceManager.getDefaultSharedPreferences(context).getLong(context.getString(inParam), inValDef);
		} else {
			return inValDef;
		}
	}
	public int getParamInt(int inParam, int inValDef) {
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamInt() - param : " + inParam );
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getParamInt() - param : " + context.getString(inParam) );
		return PreferenceManager.getDefaultSharedPreferences(context).getInt(context.getString(inParam), inValDef);
	}
	
	
	/* Enregistrement paramètres */
	
	public void setParamString(int inParam, String inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor prefEdit = preferences.edit();
	    //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setParamString() - param : " + context.getString(inParam) );
	    //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setParamString() - getStringKeyParam : " + Outils.getStringKeyParam(context,inParam) );
	    prefEdit.putString(context.getString(inParam), inVal);
		prefEdit.commit();
	}
	public void setParamInt(int inParam, int inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putInt(context.getString(inParam), inVal);
		prefEdit.commit();
	}
	public void setParamBoolean(int inParam, Boolean inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putBoolean(context.getString(inParam), inVal);
		prefEdit.commit();
	}
	
	public void setParamLong(int inParam, long inVal) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
	    SharedPreferences.Editor prefEdit = preferences.edit();  
		prefEdit.putLong(context.getString(inParam), inVal);
		prefEdit.commit();
	}	

    
}
