package fr.ffessm.doris.android.activities.view.indexbar;

import java.util.ArrayList;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import fr.ffessm.doris.android.R;

public class AlphabetListviewAdapter extends ArrayAdapter<String> {

	static String TAG="AlphabetListviewAdapter";
	LayoutInflater inflater;
	int textViewResourceId, omit;
	ArrayList<String> data;
	Context context;
	
	
	public AlphabetListviewAdapter(Context context, int textViewResourceId, ArrayList<String> objects, int omit) {
		super(context, textViewResourceId, objects);
		inflater=LayoutInflater.from(context);
		this.textViewResourceId=textViewResourceId;
		this.context=context;
		data=objects;
		this.omit=omit;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		String character=data.get(position);
		if(character.equals(".") ){
			convertView=inflater.inflate(R.layout.indexbar_dot_row, null);
		}
		else
			convertView=inflater.inflate(textViewResourceId, null);
		
		
		if (!(convertView instanceof TextView))
			return null;
		TextView textview=(TextView)convertView;
		int reduce_height_by_pixels=1;
		int height_of_row=context.getSharedPreferences("AndroidIndexBar", Context.MODE_PRIVATE).getInt("height", -1);
		
		if(omit==1 ||omit==2 ){		//Adjusting the height of the row
		
			if(height_of_row<=27)
				reduce_height_by_pixels=1;
			
			else if(height_of_row <= 52)
				reduce_height_by_pixels=2;
			
			else if(height_of_row <= 78) 
				reduce_height_by_pixels=3;
			
			else if(height_of_row <= 104) 
				reduce_height_by_pixels=4;
			
			else if(height_of_row <= 130) 
				reduce_height_by_pixels=5;
			/*Test if more if blocks need to be added */
			
			height_of_row=height_of_row-reduce_height_by_pixels;
			textview.setHeight(height_of_row);
			// TODO :Log.d(TAG,"Since there are 1 or 2 characters to be missed, the height of each row is reduced by " +reduce_height_by_pixels+ "  pixel(s)");
		}
		
		textview.setText(character);
		return convertView;
	}
	
}
