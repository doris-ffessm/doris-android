package fr.ffessm.doris.android.activities.view;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.sitedoris.Constants;

import android.content.Context;
import android.widget.ImageView;

public class ChainedLoadImageViewCallback implements Callback{
	
	ImageView targetImageView;
	Context context; 
	String imageUrl;
	int width;
	int height;
	
	public ChainedLoadImageViewCallback(Context context, ImageView targetImageView, String imageUrl, int width, int height){
		this.targetImageView = targetImageView;
		this.context = context;
		this.imageUrl = imageUrl;
		this.width = width;
		this.height = height;
	}

	@Override
	public void onError() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess() {
		// Call the second image using the first as placeholder
		Picasso.with(context)
		.load(imageUrl)
		.placeholder(targetImageView.getDrawable())  
		.resize(width, height)
		.centerInside()
		.error(R.drawable.doris_icone_doris_large_pas_connecte)
		.into(targetImageView);
	}

}
