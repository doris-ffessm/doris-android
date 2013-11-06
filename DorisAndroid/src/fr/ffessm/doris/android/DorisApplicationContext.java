package fr.ffessm.doris.android;

import fr.ffessm.doris.android.async.TelechargePhotosFiches_BgActivity;


/** Classe globale pour accéder aux informations générales de l'application */
public class DorisApplicationContext {

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
	public TelechargePhotosFiches_BgActivity telechargePhotosFiches_BgActivity = null;
	
}
