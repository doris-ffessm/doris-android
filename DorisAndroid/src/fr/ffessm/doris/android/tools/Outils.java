package fr.ffessm.doris.android.tools;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;

public class Outils {
	private static final String LOG_TAG = Outils.class.getCanonicalName();
	
	private Context context;
	
	public Outils(Context context){
		this.context = context;
	}
	
	public String getAppVersion() {
		try	{
        	PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo( context.getPackageName(), 0);
            if (BuildConfig.DEBUG) Log.v(LOG_TAG, "getAppVersion() - appVersionName : "+pi.versionName);
            return pi.versionName;
     	} catch(Exception e) {
    		if (BuildConfig.DEBUG) Log.e(LOG_TAG, "getAppVersion() - erreur : ");
    		e.printStackTrace();
    		return "";
    	}
	}
	
	
    // TODO : En attendant d'obtenir la nouvelle version de Common
	public String getZoneIcone(int inId) {
	   	switch (inId) {
	   	case -1:
    		return context.getString(R.string.icone_touteszones);
    	case 1:
    		return context.getString(R.string.icone_france);
		case 2:
			return context.getString(R.string.icone_eaudouce);
		case 3:
			return context.getString(R.string.icone_indopac);
		case 4:
			return context.getString(R.string.icone_caraibes);
		case 5:
			return context.getString(R.string.icone_atlantno);
		default:
			return "";
		}
	}
    
}
