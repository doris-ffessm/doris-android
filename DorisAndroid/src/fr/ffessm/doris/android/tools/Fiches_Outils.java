package fr.ffessm.doris.android.tools;

import android.content.Context;

import fr.ffessm.doris.android.R;

public class Fiches_Outils {
	private static final String LOG_TAG = Fiches_Outils.class.getCanonicalName();
	private static Context context;
	
	public Fiches_Outils(Context context) {
		Fiches_Outils.context = context;
	}
	
	public enum MajListeFichesType {
	    M0, M1, M2, M3
	}
	
	
	public MajListeFichesType getMajListeFichesTypeZoneGeo(Context inContext, int inIdZoneGeo){
		switch(inIdZoneGeo){
		case 1 :
			return MajListeFichesType.valueOf(Outils.getParamString(inContext, R.string.pref_key_maj_liste_fiches_region_france,"M1"));
		case 2 :
			return MajListeFichesType.valueOf(Outils.getParamString(inContext, R.string.pref_key_maj_liste_fiches_region_eaudouce,"M1"));
		case 3 :
			return MajListeFichesType.valueOf(Outils.getParamString(inContext, R.string.pref_key_maj_liste_fiches_region_indopac,"M1"));
		case 4 :
			return MajListeFichesType.valueOf(Outils.getParamString(inContext, R.string.pref_key_maj_liste_fiches_region_caraibes,"M1"));
		case 5 :
			return MajListeFichesType.valueOf(Outils.getParamString(inContext, R.string.pref_key_maj_liste_fiches_region_atlantno,"M1"));
		default :
			return null;
		}
	}
	
	
	public boolean isMajListeFichesTypeOnlyP0(){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
		if ( MajListeFichesType.valueOf(Outils.getParamString(context, R.string.pref_key_maj_liste_fiches_region_france,"M1")) == MajListeFichesType.M0 
			&& MajListeFichesType.valueOf(Outils.getParamString(context, R.string.pref_key_maj_liste_fiches_region_eaudouce,"M1")) == MajListeFichesType.M0
			&& MajListeFichesType.valueOf(Outils.getParamString(context, R.string.pref_key_maj_liste_fiches_region_atlantno,"M1")) == MajListeFichesType.M0
			&& MajListeFichesType.valueOf(Outils.getParamString(context, R.string.pref_key_maj_liste_fiches_region_indopac,"M1")) == MajListeFichesType.M0
			&& MajListeFichesType.valueOf(Outils.getParamString(context, R.string.pref_key_maj_liste_fiches_region_caraibes,"M1")) == MajListeFichesType.M0
			) return true;
		return false;	
	}
	
   
}
