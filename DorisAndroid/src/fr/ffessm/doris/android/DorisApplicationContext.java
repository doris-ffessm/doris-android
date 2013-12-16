package fr.ffessm.doris.android;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.async.TelechargePhotosFiches_BgActivity;
import fr.ffessm.doris.android.datamodel.DataChangedListener;


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
	
}
