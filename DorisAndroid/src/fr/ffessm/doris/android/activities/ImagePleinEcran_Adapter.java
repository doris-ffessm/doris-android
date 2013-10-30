package fr.ffessm.doris.android.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.squareup.picasso.Picasso;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.ScreenTools;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ImagePleinEcran_Adapter extends PagerAdapter {

	
	private ImagePleinEcran_CustomViewActivity _activity;
    private ArrayList<PhotoFiche> _PhotoFicheLists;
    private LayoutInflater inflater;
 
    // constructor
    public ImagePleinEcran_Adapter(ImagePleinEcran_CustomViewActivity activity,
            ArrayList<PhotoFiche> photoFicheLists) {
        this._activity = activity;
        this._PhotoFicheLists = photoFicheLists;
    }
	
	@Override
	public int getCount() {
		return this._PhotoFicheLists.size();
	}

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
     
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
    	fr.ffessm.doris.android.tools.TouchImageView imgDisplay;
        Button btnClose;
  
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.imagepleinecran_image, container,
                false);
  
        imgDisplay = (fr.ffessm.doris.android.tools.TouchImageView) viewLayout.findViewById(R.id.imagepleinecran_image_imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.imagepleinecran_image_btnClose);
        
        int hauteur = ScreenTools.getScreenHeight(_activity);
        int largeur = ScreenTools.getScreenWidth(_activity);
        PhotoFiche photoFiche = _PhotoFicheLists.get(position);
        if(Outils.isAvailableHiResPhotoFiche(_activity,photoFiche)){
    		try {
				Picasso.with(_activity)
					.load(Outils.getHiResFile(_activity, photoFiche))
					.placeholder(R.drawable.doris_large)  // utilisation de l'image par defaut pour commencer
					.resize(largeur, hauteur)
					.centerInside()
					.into(imgDisplay);
			} catch (IOException e) {
			}
    	}
    	else{
    		if(Outils.isAvailableMedResPhotoFiche(_activity,photoFiche)){
        		try {
    				Picasso.with(_activity)
    					.load(Outils.getMedResFile(_activity, photoFiche))
    					.placeholder(R.drawable.doris_large)  // utilisation de l'image par defaut pour commencer
    					.into(imgDisplay);
    			} catch (IOException e) {
    			}
        	}
    		else{
	    		// pas préchargée en local pour l'instant, cherche sur internet
	    		Picasso.with(_activity)
	    			.load(PhotoFiche.MOYENNE_BASE_URL+photoFiche.getCleURL())
					.placeholder(R.drawable.doris_large)  // utilisation de l'image par defaut pour commencer
					.error(R.drawable.doris_large_pas_connecte)
					.resize(largeur, hauteur)
					.centerInside()
	    			.into(imgDisplay);
    		}
    	}
        
        imgDisplay.setOnClickListener(new PhotoClickListener(photoFiche));
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {           
            @Override
            public void onClick(View v) {
            	_activity.finish();
            }
        });
  
        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    class PhotoClickListener implements View.OnClickListener{
    	PhotoFiche photoFiche;
    	public PhotoClickListener(PhotoFiche photoFiche){
    		this.photoFiche = photoFiche;
    	}	
    	@Override
        public void onClick(View v) {
    		Toast.makeText(_activity, photoFiche.getDescription(), Toast.LENGTH_LONG).show();
        }
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }

}
