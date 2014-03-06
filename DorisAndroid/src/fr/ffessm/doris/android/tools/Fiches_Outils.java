package fr.ffessm.doris.android.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;

import fr.ffessm.doris.android.R;

public class Fiches_Outils {
	private static final String LOG_TAG = Fiches_Outils.class.getCanonicalName();
	
	private static Context context;
	
	private Param_Outils paramOutils;
	
	public Fiches_Outils(Context context) {
		Fiches_Outils.context = context;
		paramOutils = new Param_Outils(context);
	}
	
	public enum MajListeFichesType {
	    M0, M1, M2, M3
	}
	
    public enum TypeLancement_kind{
    	START,
    	MANUEL
    }
    
	public MajListeFichesType getMajListeFichesTypeZoneGeo(int inIdZoneGeo){
		switch(inIdZoneGeo){
		case 1 :
			return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_france,"M1"));
		case 2 :
			return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_eaudouce,"M1"));
		case 3 :
			return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_indopac,"M1"));
		case 4 :
			return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_caraibes,"M1"));
		case 5 :
			return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_atlantno,"M1"));
		default :
			return null;
		}
	}
	
	
	public boolean isMajListeFichesTypeOnlyP0(){
		//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
		if ( MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_france,"M1")) == MajListeFichesType.M0 
			&& MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_eaudouce,"M1")) == MajListeFichesType.M0
			&& MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_atlantno,"M1")) == MajListeFichesType.M0
			&& MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_indopac,"M1")) == MajListeFichesType.M0
			&& MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_caraibes,"M1")) == MajListeFichesType.M0
			) return true;
		return false;	
	}
	
   
	public Calendar getDerniereMajListeFichesTypeZoneGeo(int inIdZoneGeo) {
		String dernierMajDateCaract = "";
		switch(inIdZoneGeo){
		case 1:
			dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_france, "01-01-2000");
			break;
		case 2:
			dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_eaudouce, "01-01-2000");
			break;
		case 3:
			dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_indopac, "01-01-2000");
			break;
		case 4:
			dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_caraibes, "01-01-2000");
			break;
		case 5:
			dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_atlantno, "01-01-2000");
			break;
		default :
			dernierMajDateCaract = "01-01-2999";
		}
		
		Calendar dernierMajDate = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    	try {
			dernierMajDate.setTime(sdf.parse(dernierMajDateCaract));
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	return dernierMajDate;
	}
	
	public int getNbJoursDerMajListeFichesTypeZoneGeo(int inIdZoneGeo) {
		Calendar jourDerniereMaj = getDerniereMajListeFichesTypeZoneGeo(inIdZoneGeo);
		Calendar aujourdHui = Calendar.getInstance();
        aujourdHui.getTime();
         
        return ( (int) ( aujourdHui.getTimeInMillis()-jourDerniereMaj.getTimeInMillis() ) / (24 * 60 * 60 * 1000) );
        
	}

	public boolean isMajNecessaireZone(int zoneId, TypeLancement_kind typeLancement){
    	// M0 : jamais
    	// M1 : sur demande
    	// M2 : chaque semaine
    	// M3 : chaque jour

		MajListeFichesType majListeFichesType = getMajListeFichesTypeZoneGeo(zoneId);
    	
		// Si MO : jamais de Maj
    	if (majListeFichesType == MajListeFichesType.M0) return false;
		
    	// Si lancé Manuellement MAJ
    	if (typeLancement == TypeLancement_kind.MANUEL) return true; 
    	
    	int nbJours = getNbJoursDerMajListeFichesTypeZoneGeo(zoneId);
    	
		// Si M2 : chaque semaine
        if (majListeFichesType == MajListeFichesType.M2 && nbJours > 7) return true;

        // Si M3 : chaque jour
        if (majListeFichesType == MajListeFichesType.M3 && nbJours > 1) return true;
        
    	return false;
    }
	
}
