/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
 * Auteurs : Guillaume Mo <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */

package fr.ffessm.doris.android.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.squareup.picasso.Picasso;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.view.ChainedLoadImageViewCallback;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Outils;
import fr.ffessm.doris.android.tools.ScreenTools;
import fr.ffessm.doris.android.tools.Outils.ImageType;
import fr.ffessm.doris.android.tools.Outils.PrecharMode;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
    	ImageView btnClose;
        Button imgTitre;
        
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.imagepleinecran_image, container,
                false);
  
        imgDisplay = (fr.ffessm.doris.android.tools.TouchImageView) viewLayout.findViewById(R.id.imagepleinecran_image_imgDisplay);
        btnClose = (ImageView) viewLayout.findViewById(R.id.imagepleinecran_image_btnClose);
        imgTitre = (Button) viewLayout.findViewById(R.id.imagepleinecran_image_titre);
        
        int hauteur = ScreenTools.getScreenHeight(_activity);
        int largeur = ScreenTools.getScreenWidth(_activity);
        final PhotoFiche photoFiche = _PhotoFicheLists.get(position);
        if(Outils.isAvailablePhoto(_activity, photoFiche.getCleURL(), ImageType.HI_RES)){
    		try {
				Picasso.with(_activity)
					.load(Outils.getPhotoFile(_activity, photoFiche.getCleURL(), ImageType.HI_RES))
					.placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par defaut pour commencer
					.resize(largeur, hauteur)
					.centerInside()
					.into(imgDisplay);
			} catch (IOException e) {
			}
    	}
    	else{
    		if(Outils.isAvailablePhoto(_activity,photoFiche.getCleURL(), ImageType.MED_RES)){
        		try {
    				Picasso.with(_activity)
    					.load(Outils.getPhotoFile(_activity, photoFiche.getCleURL(), ImageType.MED_RES))
    					.placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par defaut pour commencer
    					.into(imgDisplay);
    			} catch (IOException e) {
    			}
        	}
    		else{
	    		// pas préchargée en local pour l'instant, cherche sur internet
    			String dossier_photo;
    			switch(Outils.ImageType.valueOf(Outils.getParamString(_activity, R.string.pref_key_mode_connecte_qualite_photo,""))){
    			case MED_RES :
    				dossier_photo = Constants.MOYENNE_BASE_URL;
    				break;
    			case HI_RES :
    				dossier_photo = Constants.GRANDE_BASE_URL;
    				break;
    			default:
    				dossier_photo = Constants.MOYENNE_BASE_URL;
    			}
    			
    			ChainedLoadImageViewCallback chainedLoadImageViewCallback = new ChainedLoadImageViewCallback(_activity, imgDisplay, 
    					dossier_photo+photoFiche.getCleURL(),largeur, hauteur); // vrai chargement de l'image dansle callback
    			if(Outils.isAvailablePhoto(_activity, photoFiche.getCleURL(), ImageType.VIGNETTE)){
    				try {
						Picasso.with(_activity)
							.load(Outils.getPhotoFile(_activity, photoFiche.getCleURL(), ImageType.VIGNETTE)) // charge d'abord la vignette depuis le disque
							.placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par defaut pour commencer
							.error(R.drawable.doris_icone_doris_large_pas_connecte)
							.resize(largeur, hauteur)
							.centerInside()
							.into(imgDisplay,chainedLoadImageViewCallback);
					} catch (IOException e) {
					}
    			}
    			else{
		    		Picasso.with(_activity)
		    			.load(Constants.VIGNETTE_BASE_URL+photoFiche.getCleURL()) // charge d'abord la vignette depuis internet (mais elle est probablement déjà dans le cache)
						.placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par defaut pour commencer
						.error(R.drawable.doris_icone_doris_large_pas_connecte)
						.resize(largeur, hauteur)
						.centerInside()
		    			.into(imgDisplay,chainedLoadImageViewCallback);
    			}
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
  
        
        // Affichage Titre & Description de l'image
        String titre = photoFiche.getTitre();
        int longMax = Integer.parseInt(Outils.getParamString(_activity, R.string.imagepleinecran_titre_longmax,"25"));
        // on termine par un espace insécable puis "..."
        if (titre.length() > longMax) titre = titre.substring(0, longMax)+"\u00A0\u2026";
        if (titre.isEmpty()) titre = "Image sans titre";
        imgTitre.setText(titre);
    	
        imgTitre.setOnClickListener(new View.OnClickListener() {           
        	@Override
            public void onClick(View v) {
        		showDescription(photoFiche);
            }
        });
        
        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    class PhotoClickListener implements View.OnClickListener{
    	PhotoFiche photoFiche;
    	private long lastTouchTime = -1;
    	
    	public PhotoClickListener(PhotoFiche photoFiche){
    		this.photoFiche = photoFiche;
    	}
    	
    	@Override
        public void onClick(View v) {
    		long thisTime = System.currentTimeMillis();
    		if (thisTime - lastTouchTime < 250) {
    			// Double Click on affiche la Description de l'image
    			
        		String texteAff = "Double Click - j'aurais aimé zommer x2 (comme Google Photo)";
        		Toast.makeText(_activity, texteAff, Toast.LENGTH_LONG).show();
        		
        		lastTouchTime = -1;
    		} else if (lastTouchTime != -1){
    			// Double Click Lent on affiche la Description de l'image
    			showDescription(photoFiche);
    			
    			lastTouchTime = -1;
    		} else {
    			lastTouchTime = thisTime;
    		}
        }
    	
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }
    
	private void showDescription(PhotoFiche photoFiche) {
		String titre = photoFiche.getTitre();
		String description = photoFiche.getDescription();
		String texteAff = description;
		if (titre.length() > Integer.parseInt(Outils.getParamString(_activity, R.string.imagepleinecran_titre_longmax,"25")))
			texteAff = titre + System.getProperty("line.separator") + description;
		if (texteAff.isEmpty()) texteAff = "Image sans description";
		
		Toast.makeText(_activity, texteAff, Toast.LENGTH_LONG).show();
    }

}
