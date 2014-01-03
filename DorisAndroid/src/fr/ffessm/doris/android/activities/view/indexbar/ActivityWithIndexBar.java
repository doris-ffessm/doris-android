package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.view.View;
import android.widget.ListView;

public interface ActivityWithIndexBar  {
	
	public Handler getHandler();
	
	public Context getBaseContext();

	public Resources getResources();

	public SharedPreferences getSharedPreferences(String string, int modePrivate);

	public ListView getAlphabetListView();
	
	public View getAlphabetRowView();
	
	public void populateIndexBarHashMap();

}
