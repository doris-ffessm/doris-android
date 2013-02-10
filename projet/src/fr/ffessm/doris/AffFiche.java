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
import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;

import fr.ffessm.doris.R;
import fr.ffessm.doris.DetailsFicheAdapter.FicheAdapterListener;

public class AffFiche extends Activity implements AdapterView.OnItemLongClickListener, FicheAdapterListener, OnItemClickListener {
    
	private static String TAG = "AffFiche";
	private static boolean LOG = true;
	
    public Context appContext;
     
    private String refFiche;

    Thread thread;
    Handler handler;
    private ProgressDialog dialogPatience;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        if (LOG) Log.d(TAG, "onCreate() - Début");
         
        appContext = getApplicationContext();
        
 		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (LOG) Log.d(TAG, "onCreate() - extras : "+extras.toString());
			if (LOG) Log.d(TAG, "onCreate() - extras : "+extras.size());
			refFiche = (String) extras.get("ref");

			if (LOG) Log.d(TAG, "onCreate() - inRef : "+refFiche);
			
			if (LOG) Log.v(TAG, "onCreate() - nom : "+Doris.listeFiches.get(refFiche).nom);
			if (LOG) Log.v(TAG, "onCreate() - urlImage : "+Doris.listeFiches.get(refFiche).urlImage);

			if (LOG) Log.v(TAG, "onCreate() - Détails ? : "+Doris.listeFiches.get(refFiche).ficheListeDetails.size());
			
			if (Doris.listeFiches.get(refFiche).ficheListeDetails.size() == 0) {
				if (LOG) Log.v(TAG, "onCreate() - Récupération Détails");
				// Les Détails de cet objet doivent être téléchargés puis trouvés
				//Pour Faire patienter
				dialogPatience = ProgressDialog.show(AffFiche.this, "", getString(R.string.txt_patienceChargement), true);
		    	handler = new Handler(){
		    		public void handleMessage(Message msg) {
		    			if (LOG) Log.v(TAG, "onCreate() - msg.what : " + msg.what);
		    			switch(msg.what) {
						case 0:
							if (LOG) Log.v(TAG, "onCreate() - 0");
							dialogPatience.cancel();
							affichage();
							break;
						case 1:
							if (LOG) Log.v(TAG, "onCreate() - 1");
							dialogPatience.setMessage(getString(R.string.txt_patienceTelechargement));
							break;
						case 2:
							if (LOG) Log.v(TAG, "onCreate() - 2");
							dialogPatience.setMessage(getString(R.string.txt_patienceTraitement));
							break;
		    			}
		    		}
		    	};
	  	
		    	
		    	thread = new Thread(){
		    		public void run(){
		    			handler.sendMessage(handler.obtainMessage(1,null));
						try {
							if (LOG) Log.v(TAG, "onCreate() - 100");
							Doris.listeFiches.get(refFiche).getHtmlFiche();
							
							if (LOG) Log.v(TAG, "onCreate() - 110");
							handler.sendMessage(handler.obtainMessage(2,null));
							
							if (LOG) Log.v(TAG, "onCreate() - 120");
							Doris.listeFiches.get(refFiche).getFiche();
							
							if (LOG) Log.v(TAG, "onCreate() - 130");
							
						} catch (IOException e) {
							if (LOG) Log.e(TAG, "onCreate() - erreur : " + e);
						}
						handler.sendMessage(handler.obtainMessage(0,null));
		    		}
		    	};
		    	thread.start();	
			} else {
				// Les Détails de cet objet avaient déjà été construit
				if (LOG) Log.d(TAG, "onCreate() - Affichage Détails sans téléchargement");
				affichage();
			}
		} else {
			if (LOG) Log.e(TAG, "onCreate() - Improbable mais sait-on jamais");
		}
    
        if (LOG) Log.d(TAG, "onCreate() - Fin");
    }
    
	public void affichage (){
		if (LOG) Log.d(TAG, "affichage() - Début");
		
		setContentView(R.layout.fiche);
		DetailsFicheAdapter listeDetailsAdapter = new DetailsFicheAdapter(appContext, Doris.listeFiches.get(refFiche) );
		listeDetailsAdapter.addListener(this);
        
        ListView listeDetails = (ListView) findViewById(R.id.ListView01);
        listeDetails.setAdapter(listeDetailsAdapter);
        
        // Gallerie Haute des Photos
        Gallery gallerie = (Gallery) findViewById(R.id.Gallery1);
        gallerie.setSpacing(4);
        gallerie.setAdapter(new ImageAdapter(this));

        // Test pour aligner (à l'ouverture de la fiche) la 1ère image sur la gauche
        // de la Gallerie
        //gallerie.setGravity(Gravity.);
        
        gallerie.setOnItemClickListener(this);
        
        gallerie.setOnItemLongClickListener(this);
        
        if (LOG) Log.d(TAG, "affichage() - Fin");
	}
	
    public class ImageAdapter extends BaseAdapter {
    	Context localContext;
        
        public ImageAdapter(Context inContext) {
        	if (LOG) Log.d(TAG, "ImageAdapter() - Début");
        	localContext = inContext;
        	if (LOG) Log.d(TAG, "ImageAdapter() - Fin");
        }

        public int getCount() {
        	if (LOG) Log.d(TAG, "getCount() - Début");
        	
        	if (LOG) Log.d(TAG, "getCount() - Fin");
            return Doris.listeFiches.get(refFiche).ficheListeImgUrl.size();
        }

        public Object getItem(int inPosition) {
        	if (LOG) Log.d(TAG, "getItem() - Début");
        	if (LOG) Log.d(TAG, "getItem() - inPosition" + inPosition);
        	
        	if (LOG) Log.d(TAG, "getItem() - Fin");
            return inPosition;
        }

        public long getItemId(int inPosition) {
        	if (LOG) Log.d(TAG, "getItemId() - Début");
        	if (LOG) Log.d(TAG, "getItemId() - inPosition" + inPosition);
        	
        	if (LOG) Log.d(TAG, "getItemId() - Fin");
            return inPosition;
        }

        public View getView(int inPosition, View inConvertView, ViewGroup inParent) {
        	if (LOG) Log.d(TAG, "getView() - Début");
        	if (LOG) Log.d(TAG, "getView() - inPosition" + inPosition);
            ImageView imagView = new ImageView(localContext);

            imagView.setImageBitmap(Outils.getImage(localContext, Doris.listeFiches.get(refFiche).ficheListeImgVigUrl.get(inPosition),refFiche,""));
            imagView.setAdjustViewBounds(true);
            imagView.setLayoutParams(new Gallery.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        	if (LOG) Log.d(TAG, "getView() - Fin");
            return imagView;
        }
   }

    //Ne fait rien, sinon prévenir qu'il faut faire un clic long sur la photo pour l'afficher en grand
    @Override
	public void onItemClick(AdapterView inParent, View inView, int inPosition, long inId) {
		if (LOG) Log.d(TAG, "gallerie.setOnClickListener.OnClickListener().onClick() - Début");
		
		Toast toast = Toast.makeText(appContext, getString(R.string.txt_ClicCourtSurGalleriePhotos), Toast.LENGTH_SHORT);
		toast.show();
			
		if (LOG) Log.d(TAG, "gallerie.setOnClickListener.OnClickListener().onClick() - Fin");
	}
		
 	
    // Affichage de la Fiche du Lien sélectionné
	@Override
	public boolean onItemLongClick(AdapterView inParent, View inView, int inPosition, long inId) {
    	if (LOG) Log.d(TAG, "onItemLongClick() - Début");
    	if (LOG) Log.d(TAG, "onItemLongClick() - refFiche : "+refFiche);
    	if (LOG) Log.d(TAG, "onItemLongClick() - position : "+inPosition);

        Intent explicit = new Intent();
        explicit.setClass(AffFiche.this, AffImage.class);
        explicit.putExtra("mode", "depuisFiche");
        explicit.putExtra("ref", refFiche);
        explicit.putExtra("imgRef", inPosition);

        startActivity(explicit);
        
        if (LOG) Log.d(TAG, "onItemLongClick() - Fin");
        return true;
	}
	
}

