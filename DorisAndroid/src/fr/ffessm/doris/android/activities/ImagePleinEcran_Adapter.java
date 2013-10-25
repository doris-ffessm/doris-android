package fr.ffessm.doris.android.activities;

import java.util.ArrayList;
import java.util.Collection;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.tools.Outils;

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

public class ImagePleinEcran_Adapter extends PagerAdapter {

	
	private Activity _activity;
    private ArrayList<PhotoFiche> _PhotoFicheLists;
    private LayoutInflater inflater;
 
    // constructor
    public ImagePleinEcran_Adapter(Activity activity,
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
        ImageView imgDisplay;
        Button btnClose;
  
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.imagepleinecran_image, container,
                false);
  
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imagepleinecran_image_imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.imagepleinecran_image_btnClose);
         
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888; // TODO regarder si on peux exploiter cette option pour optimiser
        Bitmap bitmap = Outils.getAvailableImagePhotoFiche(_activity, _PhotoFicheLists.get(position));
        //Bitmap bitmap = BitmapFactory.decodeFile(_PhotoFicheLists.get(position), options);
        imgDisplay.setImageBitmap(bitmap);
         
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
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }

}
