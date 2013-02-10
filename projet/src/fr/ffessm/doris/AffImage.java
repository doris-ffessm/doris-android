/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
Copyright du Code : Guillaume Moynard  ([29/05/2011]) 

Guillaume Moynard : gmo7942@gmail.com

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
* ********************************************************************
* ********************************************************************* */


package fr.ffessm.doris;

/* *********************************************************************
 * Déclaration des imports
   *********************************************************************/

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import fr.ffessm.doris.Doris;
import it.sephiroth.android.library.imagezoom.ImageViewTouch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AffImage extends Activity 
{
    private final static String TAG = "AffImage";
    private final static Boolean LOG = true;
	
    private Context appContext;
    
    private ImageViewTouch mImage;
    private TextView mText;
    private Button mBoutonImgAvant;
    private Button mBoutonImgApres;
    private Button mBoutonAffFiche;
    private Button mBoutonVersGalerie;
	
	private static String mode = null;
	private static String ficheRef= null;
	private static int ficheImgRef = 0;
	private static int orientationPrecedente = -1;
	private static String imgUrl;
	private static String imgDescription;
	
	boolean affBoutonImgAvant = false;
	boolean affBoutonImgApres = false;
	
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	
    @Override
	protected void onCreate(Bundle inSavedInstanceState)
    {
        super.onCreate(inSavedInstanceState);

        setContentView(R.layout.image);
        if (LOG) Log.d(TAG, "onCreate() - Début");
        
		appContext = getApplicationContext();
		
        mImage = (ImageViewTouch) findViewById(R.id.image);
        mText = (TextView) findViewById(R.id.text);
        mBoutonImgAvant = (Button) findViewById(R.id.bouton_avant);
        mBoutonImgApres = (Button) findViewById(R.id.bouton_apres);
        mBoutonAffFiche = (Button) findViewById(R.id.bouton_fiche);
        mBoutonVersGalerie = (Button) findViewById(R.id.bouton_versgalerie);
        
        Bundle extras = getIntent().getExtras();
		if (extras != null) {
			
			String inMode = (String) extras.get("mode");
			if (LOG) Log.v(TAG, "onCreate() - inMode : "+inMode);
			
			String inFicheRef= (String) extras.get("ref");
			if (LOG) Log.v(TAG, "onCreate() - inFicheRef : "+inFicheRef);
			

			int inFicheImgRef = (Integer) extras.get("imgRef");

			//On est dans on Create on y passe dans le cas d'ouverture de la fenêtre
			//mais aussi dans le cas d'une rotation
			//Si l'orientation n'a jamais été regardée ou qu'elle est identique à la valeur 
			//précédente, il est probable que ce soit un nouvelle affichage (on lit donc les valeurs
			//transmises. De même, si le mode ou la référence de la fiche ont changés.
			//Sinon 
	        if (LOG) Log.v(TAG, "onCreate() - getConfiguration().orientation : " + getResources().getConfiguration().orientation);
			if (orientationPrecedente == -1 || orientationPrecedente == getResources().getConfiguration().orientation 
				|| mode != inMode || ficheRef != inFicheRef) {
				mode = inMode;
				ficheRef = inFicheRef;
				ficheImgRef = inFicheImgRef;
			} 
			orientationPrecedente = getResources().getConfiguration().orientation;
			
			affichage(mode, ficheRef, ficheImgRef);
		} else {
			if (LOG) Log.e(TAG, "onCreate() - Improbable mais sait-on jamais");
		}
		
		
		/* *********************************************************************
    	 * Gestion appuis sur les boutons
    	   *********************************************************************/
        mBoutonImgAvant.setOnClickListener(
        	new OnClickListener() {
        		@Override
        		public void onClick(View inView) {
        			if (LOG) Log.d(TAG, "mBoutonImgAvant.setOnClickListener.OnClickListener().onClick() - Début");
        			
        			ficheImgRef -= 1;
        			affichage(mode, ficheRef, ficheImgRef);
        				
					if (LOG) Log.d(TAG, "mBoutonImgAvant.setOnClickListener.OnClickListener().onClick() - Fin");
	        	}
			}
        );
		
        mBoutonImgApres.setOnClickListener(
            	new OnClickListener() {
            		@Override
            		public void onClick(View inView) {
            			if (LOG) Log.d(TAG, "mBoutonImgApres.setOnClickListener.OnClickListener().onClick() - Début");
            			
            			ficheImgRef += 1;
            			affichage(mode, ficheRef, ficheImgRef);
            				
    					if (LOG) Log.d(TAG, "mBoutonImgApres.setOnClickListener.OnClickListener().onClick() - Fin");
    	        	}
    			}
            );
        
        mBoutonAffFiche.setOnClickListener(
            	new OnClickListener() {
            		@Override
            		public void onClick(View inView) {
            			if (LOG) Log.d(TAG, "mBoutonImgApres.setOnClickListener.OnClickListener().onClick() - Début");
            			
            			if (mode.equals("depuisRecherche")) {
            	                		 
	                		//Affichage Fiche Sélectionnée
	    	            	Intent explicit = new Intent();
	    	                explicit.setClass(AffImage.this, AffFiche.class);
	    	                explicit.putExtra("ref", ficheRef);
	    	                startActivity(explicit);
	           				
	    	                //On ferme par derrière l'image pour donner une meilleure impression de fluidité
	    	                finish();
	    	                
            			} else if (mode.equals("depuisFiche")) {
            				//Fermeture de la fenêtre de visualisation des images et retour à la fiche
            				finish();
            			}
            				
    					if (LOG) Log.d(TAG, "mBoutonImgApres.setOnClickListener.OnClickListener().onClick() - Fin");
    	        	}
    			}
            );
        
        
        mBoutonVersGalerie.setOnClickListener(
            	new OnClickListener() {
            		@Override
            		public void onClick(View inView) {
            			if (LOG) Log.d(TAG, "mBoutonVersGallerie.setOnClickListener.OnClickListener().onClick() - Début");
            			
            			imageVersGalerie();
            				
    					if (LOG) Log.d(TAG, "mBoutonVersGallerie.setOnClickListener.OnClickListener().onClick() - Fin");
    	        	}
    			}
            );
        
		if (LOG) Log.d(TAG, "onCreate() - Fin");
    }
    
    void affichage(String inMode, String inRef, Integer inImgRef) {
    	
    	if (LOG) Log.d(TAG, "affichage() - Début");
    	
    	if (LOG) Log.d(TAG, "affichage() - inMode : " + inMode);
    	if (LOG) Log.d(TAG, "affichage() - inRef : " + inRef);
    	if (LOG) Log.d(TAG, "affichage() - inImgRef : " + inImgRef);
					
		if (inMode.equals("depuisRecherche")) {

			imgDescription = Doris.listeFiches.get(inRef).nom;
			
			imgUrl = Doris.listeFiches.get(inRef).urlImage;
			
		} else if (inMode.equals("depuisFiche")) {

			imgDescription = Doris.listeFiches.get(inRef).nom;
			imgDescription += " - " + Doris.listeFiches.get(inRef).ficheListeImgTexte.get(inImgRef);
			
			imgUrl = Doris.listeFiches.get(inRef).ficheListeImgUrl.get(inImgRef);
			
			if (LOG) Log.d(TAG, "affichage() - ficheListeImgUrl.size() : "+Doris.listeFiches.get(inRef).ficheListeImgUrl.size());
			if (inImgRef != 0){
				affBoutonImgAvant = true;
			} else {
				affBoutonImgAvant = false;
			}
			if (inImgRef != Doris.listeFiches.get(inRef).ficheListeImgUrl.size() - 1){
				affBoutonImgApres = true;
			} else {
				affBoutonImgApres = false;
			}		
		} else if (inMode.equals("depuisHistorique")) {

			imgDescription = Doris.listeFiches.get(inRef).nom;
			
			imgUrl = Doris.listeFiches.get(inRef).urlImage;
			
		}
		
		if (LOG) Log.v(TAG, "affichage() - desc. image : "+imgDescription);
		mText.setText(imgDescription);
		
		if (LOG) Log.v(TAG, "affichage() - urlImg : "+imgUrl);
		Bitmap bitmap;
		bitmap = Outils.getImage(appContext, imgUrl, inRef,"");
		mImage.setImageBitmap(bitmap);

		//Affichage boutons Avant & Après
		if ( affBoutonImgAvant == true) {
			mBoutonImgAvant.setVisibility(View.VISIBLE);
		} else {
			mBoutonImgAvant.setVisibility(View.INVISIBLE);
		}
		
		if ( affBoutonImgApres == true) {
			mBoutonImgApres.setVisibility(View.VISIBLE);
		} else {
			mBoutonImgApres.setVisibility(View.INVISIBLE);
		}
		
		//Boutton affichage de la fiche
		mBoutonAffFiche.setText(Doris.listeFiches.get(inRef).ficheType);
		mBoutonAffFiche.setTextColor(Color.parseColor(Doris.listeFiches.get(inRef).ficheTypeCouleurTexte));
		mBoutonAffFiche.setBackgroundColor(Color.parseColor(Doris.listeFiches.get(inRef).ficheTypeCouleurFond));
		
		if (LOG) Log.d(TAG, "affichage() - Fin");
    } 

    void imageVersGalerie() {
    	
    	if (LOG) Log.d(TAG, "imageVersGalerie() - Début");
    	
		if (LOG) Log.v(TAG, "imageVersGalerie() - desc. image : "+imgDescription);
		if (LOG) Log.v(TAG, "imageVersGalerie() - urlImg : "+imgUrl);
			
		String nomImageGalerie = imgDescription.trim().replaceAll(" ", "_");

		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.YEAR))
		            + fromInt(c.get(Calendar.MONTH))
		            + fromInt(c.get(Calendar.DAY_OF_MONTH))
		            + fromInt(c.get(Calendar.HOUR_OF_DAY))
		            + fromInt(c.get(Calendar.MINUTE))
		            + fromInt(c.get(Calendar.SECOND));
		nomImageGalerie += "-" + date.toString() + ".jpg";
		if (LOG) Log.v(TAG, "imageVersGalerie() - nomImageGalerie : "+nomImageGalerie);
		
		String imgChemin = Outils.getCleImage( imgUrl, ficheRef, "");
		if (LOG) Log.v(TAG, "imageVersGalerie() - imgChemin : "+imgChemin);
		
		Bitmap bitmap = Outils.getImage(appContext, imgUrl, ficheRef, "");
		if (LOG) Log.v(TAG, "imageVersGalerie() - bitmap : "+bitmap.toString());
		
		
		File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Doris" );	
		if (LOG) Log.v(TAG, "imageVersGalerie() - storageDir : "+storageDir);

		//Création du dossier de stockage s'il n'existait pas
		if (storageDir != null) {
			if (! storageDir.mkdirs()) {
				if (! storageDir.exists()){
					// Le dossier n'a pas pu être créé
					if (LOG) Log.e(TAG, "Impossible de créer le dossier");
				} else {
					// Le dossier existait déjà
				}
			}
		}	
				
		
		FileOutputStream out = null;
		File imageFileName = new File(storageDir, nomImageGalerie);		
		try
		{
			if (LOG) Log.v(TAG, "Début copie de l'image");
			out = new FileOutputStream(imageFileName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			scanPhoto(imageFileName.toString());
			out = null;
			if (LOG) Log.v(TAG, "Fin copie de l'image");

			// On affiche que l'enregistrement a bien eu lieu
			Toast toast = Toast.makeText(appContext, getString(R.string.txt_image_msg_vers_gallerie), Toast.LENGTH_LONG);
			toast.show();
			
		} catch (Exception e)
		{
			if (LOG) Log.e(TAG, "Impossible de copier l'image");
			e.printStackTrace();
		}
	
		if (LOG) Log.d(TAG, "imageVersGalerie() - Fin");
    } 
    
    public String fromInt(int val)
    {
    	return String.valueOf(val);
    }
    
    MediaScannerConnection msConn;
    public void scanPhoto(final String imageFileName)
    {
    	msConn = new MediaScannerConnection(appContext,new MediaScannerConnectionClient()
    	{
    
    		public void onMediaScannerConnected()
		    {
			    msConn.scanFile(imageFileName, null);
			    Log.i("msClient obj  in Photo Utility","connection established");
		    }
    		
		    public void onScanCompleted(String path, Uri uri)
		    {
			    msConn.disconnect();
			    Log.i("msClient obj in Photo Utility","scan completed");
		    }
    	});
    	msConn.connect();
    } 
}
