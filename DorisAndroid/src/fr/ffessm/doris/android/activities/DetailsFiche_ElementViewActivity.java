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


import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
// Start of user code protectedDetailsFiche_ElementViewActivity_additional_import
import fr.ffessm.doris.android.BuildConfig;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ffessm.doris.android.activities.view.FoldableClickListener;
import fr.ffessm.doris.android.async.TelechargePhotosFiche_BgActivity;
import fr.ffessm.doris.android.async.TelechargePhotosFiches_BgActivity;
import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.tools.Outils;
// End of user code

public class DetailsFiche_ElementViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper>
// Start of user code protectedDetailsFiche_ElementViewActivity_additional_implements
	implements DataChangedListener
// End of user code
{
	
	protected int ficheId;
	
	private static final String LOG_TAG = DetailsFiche_ElementViewActivity.class.getCanonicalName();

// Start of user code protectedDetailsFiche_ElementViewActivity_additional_attributes
	static final int FOLD_SECTIONS_MENU_ID = 1;	
	static final int UNFOLD_SECTIONS_MENU_ID = 2;
	
    boolean isOnCreate = true;
    List<FoldableClickListener> allFoldable = new ArrayList<FoldableClickListener>();
    Handler mHandler;
    LinearLayout photoGallery;
    Collection<String> insertedPhotosFiche = new ArrayList<String>();
    boolean askedBgDownload = false;
// End of user code
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailsfiche_elementview);
        ficheId = getIntent().getExtras().getInt("ficheId");
        
		// Start of user code protectedDetailsFiche_ElementViewActivity_onCreate        
        // Defines a Handler object that's attached to the UI thread
		mHandler = new Handler(Looper.getMainLooper()) {
			/*
		     * handleMessage() defines the operations to perform when
		     * the Handler receives a new Message to process.
		     */
		    @Override
		    public void handleMessage(Message inputMessage) {
		    	
		    	if(inputMessage.obj != null ){
		    		showToast((String) inputMessage.obj);
		    	}
		    	refreshScreenData();
		    }
		
		};
		Picasso.with(this).setDebugging(BuildConfig.DEBUG);
		// End of user code
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
	}
    
    
    private void refreshScreenData() {
    	// get our dao
    	RuntimeExceptionDao<Fiche, Integer> entriesDao = getHelper().getFicheDao();
    	Fiche entry = entriesDao.queryForId(ficheId);
    	entry.setContextDB(getHelper().getDorisDBHelper());

		// Start of user code protectedDetailsFiche_ElementViewActivity.refreshScreenData
    	((TextView) findViewById(R.id.detailsfiche_elementview_nomscientifique)).setText(entry.getNomScientifique());
		((TextView) findViewById(R.id.detailsfiche_elementview_nomcommun)).setText(entry.getNomCommun());
		((TextView) findViewById(R.id.detailsfiche_elementview_numerofiche)).setText("N° "+((Integer)entry.getNumeroFiche()).toString());					
		((TextView) findViewById(R.id.detailsfiche_elementview_etatfiche)).setText(((Integer)entry.getEtatFiche()).toString());	
		
		StringBuffer sbDebugText = new StringBuffer();

		
		
		Collection<PhotoFiche> photosFiche = entry.getPhotosFiche(); 
		if(photosFiche!=null){
			sbDebugText.append("\nnbPhoto="+photosFiche.size()+"\n");
			
			photoGallery = (LinearLayout)findViewById(R.id.detailsfiche_elementview_photogallery);
			int pos = 0;
			for (PhotoFiche photoFiche : photosFiche) {
				View photoView = insertPhoto(photoFiche);
				photoView.setOnClickListener(new OnImageClickListener(this.ficheId,pos,this));
				photoGallery.addView(photoView);
				
				
				//sbDebugText.append("\nPhoto="+photoFiche.getCleURL()+"\n");
				/*if(!insertedPhotosFiche.contains(photoFiche.getCleURL())){
					View photoView = insertPhoto(photoFiche);
					if(photoView!=null){
						photoView.setOnClickListener(new OnImageClickListener(this.ficheId,pos,this));
						photoGallery.addView(photoView);
						insertedPhotosFiche.add(photoFiche.getCleURL());
					}
					else if(!askedBgDownload){
						askedBgDownload = true;
						new TelechargePhotosFiche_BgActivity(getApplicationContext(), this.getHelper(), entry, this).execute("");
						break; // les autres ne sont probablement pas là non plus, pas la peine d'essayer
					}
				}*/
				pos++;
			}
			
		}
		
		if(isOnCreate){
			// do only on first creation
			LinearLayout containerLayout =  (LinearLayout) findViewById(R.id.detailsfiche_sections_layout);
			// section Autres Dénominations
			if(entry.getAutresDenominations() != null){
				Collection<AutreDenomination> autresDenominations = entry.getAutresDenominations();
				if(autresDenominations.size() > 0){					
		            StringBuilder sbAutresDenominations = new StringBuilder();	
		            int i = 1;
					for (AutreDenomination autreDenomination : autresDenominations) {
						sbAutresDenominations.append(autreDenomination.getDenomination());
						if(autresDenominations.size() > 1 && i < autresDenominations.size()){
							sbAutresDenominations.append("\n");
						}
						i++;
					}
					addFoldableView(containerLayout, getString(R.string.detailsfiche_elementview_autresdenominations_label), sbAutresDenominations.toString());
					
				}
			}
			// section issues de la fiche
			if(entry.getContenu() != null){
				
				for (SectionFiche sectionFiche : entry.getContenu()) {
					addFoldableView(containerLayout, sectionFiche.getTitre(), sectionFiche.getTexte());
				}
			}
			
			// section "Crédits"
			StringBuilder sbCreditText = new StringBuilder();
			final String urlString = "http://doris.ffessm.fr/fiche2.asp?fiche_numero="+entry.getNumeroFiche(); 
			sbCreditText.append(urlString+"\n");
			sbCreditText.append(getString(R.string.detailsfiche_elementview_datecreation_label));
			sbCreditText.append(entry.getDateCreation()+"\n");
			sbCreditText.append(getString(R.string.detailsfiche_elementview_datemodification_label));
			sbCreditText.append(entry.getDateModification());
			SpannableString richtext = new SpannableString(sbCreditText.toString());
			//richtext.setSpan(new RelativeSizeSpan(2f), 0, urlString.length(), 0);
			richtext.setSpan(new URLSpan(urlString), 0, urlString.length(), 0);
			addFoldableView(containerLayout, getString(R.string.detailsfiche_elementview_credit_label),richtext);
			isOnCreate = false;
		}
		((TextView) findViewById(R.id.detailsfiche_elementview_debug_text)).setText(sbDebugText.toString());
		
		// End of user code
    	
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);

		//Start of user code additional onCreateOptionsMenu
        menu.add(Menu.NONE, FOLD_SECTIONS_MENU_ID, 1, R.string.fold_all_sections_menu_option).setIcon(R.drawable.ic_expand);
		menu.add(Menu.NONE, UNFOLD_SECTIONS_MENU_ID, 2, R.string.unfold_all_sections_menu_option).setIcon(R.drawable.ic_expand);

		//End of user code
        return super.onCreateOptionsMenu(menu);
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// behavior of option menu
        switch (item.getItemId()) {
        	case 777:
                    startActivity(new Intent(this, Preference_PreferenceViewActivity.class));
                    return true;
        
		//Start of user code additional menu action
        	case FOLD_SECTIONS_MENU_ID:
        		foldAll();
				break;
        	case UNFOLD_SECTIONS_MENU_ID:
        		unfoldAll();
				break;
		//End of user code
        }
        return false;
    }

	// Start of user code protectedDetailsFiche_ElementViewActivity_additional_operations
    // pour le menu sur click long
    @Override  
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {  
    	super.onCreateContextMenu(menu, v, menuInfo);  
        //menu.setHeaderTitle("Context Menu");  
        menu.add(Menu.NONE, FOLD_SECTIONS_MENU_ID, 1, R.string.fold_all_sections_menu_option).setIcon(R.drawable.ic_expand);
		menu.add(Menu.NONE, UNFOLD_SECTIONS_MENU_ID, 2, R.string.unfold_all_sections_menu_option).setIcon(R.drawable.ic_expand);

    }
    @Override  
    public boolean onContextItemSelected(MenuItem item) {  
    	switch (item.getItemId()) {    
    	case FOLD_SECTIONS_MENU_ID:
    		foldAll();
			break;
    	case UNFOLD_SECTIONS_MENU_ID:
    		unfoldAll();
			break;
	    }
	    return false;
    }
    
    protected void addFoldableView(LinearLayout containerLayout, String titre, CharSequence texte){
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.detailsfiche_elementview_foldablesection, null);
        
        TextView titreText = (TextView) convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_titre);
        titreText.setText(titre);
        
        TextView contenuText = (TextView) convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldabletext);
        contenuText.setVisibility(View.GONE); // par défault invisible
        contenuText.setText(texte, BufferType.SPANNABLE);
        // make our ClickableSpans and URLSpans work 
        contenuText.setMovementMethod(LinkMovementMethod.getInstance());
       // contenuText.
        
        ImageButton foldButton = (ImageButton)convertView.findViewById(R.id.detailsfiche_elementview_fold_unflod_section_imageButton);
        
        FoldableClickListener foldable = new FoldableClickListener(contenuText);
        allFoldable.add(foldable);
        foldButton.setOnClickListener(foldable);
        titreText.setOnClickListener(foldable);
        // enregistre pour réagir au click long
        registerForContextMenu(foldButton);
        registerForContextMenu(titreText);
        
        containerLayout.addView(convertView);
    }
    
    View insertPhoto(PhotoFiche photoFiche){
    	
    	
       LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LayoutParams(200, 200));
        layout.setGravity(Gravity.CENTER);
        
        ImageView imageView = new ImageView(getApplicationContext());	        
        imageView.setLayoutParams(new LayoutParams(200, 200));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if(Outils.isAvailableVignettePhotoFiche(this, photoFiche)){
    		try {
				Picasso.with(this).load(Outils.getVignetteFile(this, photoFiche))
					.fit()
					.centerInside()
					.into(imageView);
			} catch (IOException e) {
			}
    	}
    	else{
    		// pas préhargée en local pour l'instant, cherche sur internet
    		Picasso.with(this)
    			.load(PhotoFiche.VIGNETTE_BASE_URL+photoFiche.getCleURL())
				.placeholder(R.drawable.doris_large)  // utilisation de l'image par defaut pour commencer
				.error(R.drawable.doris_large_pas_connecte)
				.fit()
				.centerInside()
    			.into(imageView);
    	}
        
        layout.addView(imageView);
        return layout;
   
    }
    protected void foldAll(){
    	for (FoldableClickListener foldable : allFoldable) {
			foldable.fold();
		}
    }
    protected void unfoldAll(){
    	for (FoldableClickListener foldable : allFoldable) {
			foldable.unfold();
		}
    }
    
    public void dataHasChanged(String textmessage){
		Message completeMessage = mHandler.obtainMessage(1, textmessage);
        completeMessage.sendToTarget();
	}

    class OnImageClickListener implements OnClickListener {
    	 
        int _position;
        int _ficheID;
        Activity _activity;
 
        // constructor
        public OnImageClickListener(int ficheID, int position, Activity activity) {
            this._position = position;
            this._activity = activity;
            this._ficheID = ficheID;
        }
 
        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, ImagePleinEcran_CustomViewActivity.class);
            i.putExtra("position", _position);
            i.putExtra("ficheId", _ficheID);
            _activity.startActivity(i);
        }
 
    }
    
    // End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
