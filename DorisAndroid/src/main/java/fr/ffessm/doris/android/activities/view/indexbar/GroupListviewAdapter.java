package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Groupe;

public class GroupListviewAdapter extends ArrayAdapter<Groupe> {

	static String TAG="GroupListviewAdapter";
	LayoutInflater inflater;
	int textViewResourceId, omit;
	List<Groupe> data;
	Context context;


	public GroupListviewAdapter(Context context, int textViewResourceId, List<Groupe> objects) {
		super(context, textViewResourceId, objects);
		inflater=LayoutInflater.from(context);
		this.textViewResourceId=textViewResourceId;
		this.context=context;
		data=objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Groupe groupe = data.get(position);
		String character=groupe.getNomGroupe();
		if(character.equals(".") ){
			convertView=inflater.inflate(R.layout.indexbar_dot_row, null);
		}
		else
			convertView=inflater.inflate(textViewResourceId, null);
		
		
		if (!(convertView instanceof TextView))
			return null;
		TextView textview=(TextView)convertView;
		int height_of_row=context.getSharedPreferences("AndroidIndexBar", Context.MODE_PRIVATE).getInt("height", -1);

		//textview.setHeight(height_of_row);

		
		textview.setText(".");
		int[] colors = {Color.TRANSPARENT,groupe.getCouleurGroupe()};
		GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
		gradientDrawable.setColors(colors);
		textview.setBackground(gradientDrawable);
		textview.setTag(groupe);
		return convertView;
	}
	
}
