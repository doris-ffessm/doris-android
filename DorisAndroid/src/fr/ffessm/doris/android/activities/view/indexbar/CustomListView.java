package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;

public class CustomListView extends ListView{

	static String TAG="CustomListview";
	
	protected Context _context;
	
	public CustomListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		_context = context;
	}
	
	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_context = context;
	}
	/**
	 * OnLayout() calculates the height of two Listviews.
	 */
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)	{
		
		super.onLayout(changed, left, top, right, bottom);
		Log.d(TAG, "(onLayout) Dimensions of Listview :left = " + left + " right="+right+" top="+top+" ,Height = " + bottom +" changed="+changed);
		
		/*Since this callback is called multiple times, we need to prevent its execution for multiple times.*/
		if(changed==true){
			if(_context instanceof ActivityWithIndexBar){
				Message msg = ((ActivityWithIndexBar)_context).getHandler().obtainMessage();
				msg.what = 1;
				msg.obj=bottom;								//height of the listview drawn on the screen
				((ActivityWithIndexBar)_context).getHandler().handleMessage(msg);
			}
		}
	}
}
