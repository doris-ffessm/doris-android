package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.tools.ScreenTools;

public class GroupListviewAdapter extends ArrayAdapter<Groupe> {

	static String TAG="GroupListviewAdapter";
	LayoutInflater inflater;
	int imagesViewResourceId, omit;
	List<Groupe> data;
	Context context;


	public GroupListviewAdapter(Context context, int imagesViewResourceId, List<Groupe> objects) {
		super(context, imagesViewResourceId, objects);
		inflater=LayoutInflater.from(context);
		this.imagesViewResourceId=imagesViewResourceId;
		this.context=context;
		data=objects;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Groupe groupe = data.get(position);

		convertView=inflater.inflate(imagesViewResourceId, null);
		
		
		if (!(convertView instanceof ImageView))
			return null;
		ImageView imageView =(ImageView)convertView;
		imageView.setMinimumHeight((int)context.getResources().getDimension(R.dimen.indexbar_image_width));
		imageView.setMinimumWidth((int)context.getResources().getDimension(R.dimen.indexbar_image_width));

		int identifierIcone1Groupe = context.getResources().getIdentifier(groupe.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw", context.getPackageName());
		Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIcone1Groupe));
		imageView.setImageBitmap(bitmap);
		int[] colors = {Color.TRANSPARENT,groupe.getCouleurGroupe()};
		GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
		gradientDrawable.setColors(colors);
		imageView.setBackground(gradientDrawable);
		imageView.setTag(groupe);
		return convertView;
	}

	
}
