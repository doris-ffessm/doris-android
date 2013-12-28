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
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import fr.ffessm.doris.android.activities.view.FoldableClickListener;
import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.IntervenantFiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.sitedoris.Constants;
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
	
	final Context context = this;  
	
	protected int ficheNumero;
	
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

        ficheNumero = getIntent().getExtras().getInt("ficheNumero");
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
		
		// info de debug de Picasso
		if (Outils.getParamBoolean(this.getApplicationContext(), R.string.pref_key_affichage_debug, false)){
			Picasso.with(this).setDebugging(BuildConfig.DEBUG);
		}
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
		// Start of user code protectedDetailsFiche_ElementViewActivity.refreshScreenData
    	Fiche entry = null;
    	if (ficheId != 0) entry = entriesDao.queryForId(ficheId);
    	else if (ficheNumero != 0) entry = entriesDao.queryForEq("numeroFiche", ficheNumero).get(0);
	    entry.setContextDB(getHelper().getDorisDBHelper());
	    
    	((TextView) findViewById(R.id.detailsfiche_elementview_nomscientifique)).setText(entry.getNomScientifique());
		((TextView) findViewById(R.id.detailsfiche_elementview_nomcommun)).setText(entry.getNomCommun());
		((TextView) findViewById(R.id.detailsfiche_elementview_numerofiche)).setText("N° "+((Integer)entry.getNumeroFiche()).toString());					
		((TextView) findViewById(R.id.detailsfiche_elementview_etatfiche)).setText(((Integer)entry.getEtatFiche()).toString());	
		TextView btnEtatFiche = (TextView)  findViewById(R.id.detailsfiche_elementview_etatfiche);
		final DetailsFiche_ElementViewActivity context = this;
        
		//1-Fiche en cours de rédaction;2-Fiche en cours de rédaction;3-Fiche en cours de rédaction;4-Fiche Publiée;5-Fiche proposée
		switch(entry.getEtatFiche()){
        case 1 : case 2 : case 3 :
        	btnEtatFiche.setVisibility(View.VISIBLE);
        	btnEtatFiche.setText(" R ");
        	btnEtatFiche.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, R.string.ficheredaction_explications, Toast.LENGTH_LONG).show();
				}
			});
        	break;
        case 5:
        	btnEtatFiche.setVisibility(View.VISIBLE);
        	btnEtatFiche.setText(" P ");
        	btnEtatFiche.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, R.string.ficheproposee_explications, Toast.LENGTH_LONG).show();
				}
			});
        	break;
        case 4:
        	btnEtatFiche.setVisibility(View.GONE);
        	break;
        default:
        	btnEtatFiche.setVisibility(View.VISIBLE);
        	btnEtatFiche.setText(" "+entry.getEtatFiche()+" ");
        }
		
		
		
		StringBuffer sbDebugText = new StringBuffer();

		
		
		Collection<PhotoFiche> photosFiche = entry.getPhotosFiche(); 
		if(photosFiche!=null && isOnCreate){
			//sbDebugText.append("\nnbPhoto="+photosFiche.size()+"\n");
			
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
					//Log.d(LOG_TAG, "refreshScreenData() - titre : "+sectionFiche.getTitre());
					//Log.d(LOG_TAG, "refreshScreenData() - texte : "+sectionFiche.getTexte());
					addFoldableView(containerLayout, sectionFiche.getTitre(), sectionFiche.getTexte());
				}
			}
			
			// Zones Géographiques
			List<ZoneGeographique> zonesGeographiques= entry.getZonesGeographiques();
			if(zonesGeographiques!= null){			
				StringBuilder sbZonesGeographiques = new StringBuilder();	
	            int i = 1;
				for (ZoneGeographique zoneGeographique : zonesGeographiques) {
					sbZonesGeographiques.append(zoneGeographique.getNom());
					if(zonesGeographiques.size() > 1 && i < zonesGeographiques.size()){
						sbZonesGeographiques.append("\n");
					}
					i++;
				}
				if (sbZonesGeographiques.toString().length() != 0) {
					addFoldableView(containerLayout, getString(R.string.detailsfiche_elementview_zonesgeo_label), sbZonesGeographiques.toString());
				} else {
					addFoldableView(containerLayout, getString(R.string.detailsfiche_elementview_zonesgeo_label), getString(R.string.detailsfiche_elementview_zonesgeo_aucune_label));
				}
			}
			
			// section "Crédits"
			StringBuilder sbCreditText = new StringBuilder();
			final String urlString = "http://doris.ffessm.fr/fiche2.asp?fiche_numero="+entry.getNumeroFiche(); 
			sbCreditText.append(urlString);
			
			sbCreditText.append("\n"+getString(R.string.detailsfiche_elementview_datecreation_label));
			sbCreditText.append(entry.getDateCreation());
			
			if (!entry.getDateModification().isEmpty()) {
				sbCreditText.append("\n"+getString(R.string.detailsfiche_elementview_datemodification_label));
				sbCreditText.append(entry.getDateModification());
			}
			
			for (IntervenantFiche intervenant : entry.getIntervenants()) {
				intervenant.setContextDB(getHelper().getDorisDBHelper());
				//sbCreditText.append("\n"+intervenant.getId());
				sbCreditText.append("\n"+Constants.getTitreParticipant(intervenant.getRoleIntervenant() ) );				
				
				intervenant.setContextDB(getHelper().getDorisDBHelper());
				Participant participant = intervenant.getParticipant();
				participant.setContextDB(getHelper().getDorisDBHelper());
				
				//sbCreditText.append(" - "+participant.getId());
				sbCreditText.append(" : "+participant.getNom());
			}
			
			SpannableString richtext = new SpannableString(sbCreditText.toString());
			//richtext.setSpan(new RelativeSizeSpan(2f), 0, urlString.length(), 0);
			richtext.setSpan(new URLSpan(urlString), 0, urlString.length(), 0);
			
			addFoldableView(containerLayout, getString(R.string.detailsfiche_elementview_credit_label),richtext);
			
			isOnCreate = false;
		}
		
		
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    	// Debug
    	// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		if (Outils.getParamBoolean(this.getApplicationContext(), R.string.pref_key_affichage_debug, false)){
			
			((TextView) findViewById(R.id.detailsfiche_elementview_debug_text)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.detailsfiche_elementview_debug_text)).setText(sbDebugText.toString());
	
		} else {
			((TextView) findViewById(R.id.detailsfiche_elementview_debug_text)).setVisibility(View.GONE);
		}
		// End of user code
    	
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		// add options in the menu
        menu.add(Menu.NONE, 777, 0, R.string.preference_menu_title).setIcon(android.R.drawable.ic_menu_preferences);

		//Start of user code additional onCreateOptionsMenu
        menu.add(Menu.NONE, FOLD_SECTIONS_MENU_ID, 1, R.string.fold_all_sections_menu_option).setIcon(R.drawable.expander_ic_maximized);
		menu.add(Menu.NONE, UNFOLD_SECTIONS_MENU_ID, 2, R.string.unfold_all_sections_menu_option).setIcon(R.drawable.expander_ic_minimized);

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
        menu.add(Menu.NONE, FOLD_SECTIONS_MENU_ID, 1, R.string.fold_all_sections_menu_option).setIcon(R.drawable.ic_expand_close);
		menu.add(Menu.NONE, UNFOLD_SECTIONS_MENU_ID, 2, R.string.unfold_all_sections_menu_option).setIcon(R.drawable.ic_expand_open);

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
        
        if (!Outils.getParamBoolean(this.getApplicationContext(), R.string.pref_key_fiche_aff_details_pardefaut, false)){
        	contenuText.setVisibility(View.GONE); // par défault invisible
        } else {
        	contenuText.setVisibility(View.VISIBLE);
        }
        
        // Si le texte contient {{999}} alors on remplace par (Fiche) est on met un lien vers la fiche sur (Fiche)
        SpannableString richtext = textToSpannableStringDoris(texte);
        
        
        //Log.d(LOG_TAG, "addFoldableView() - richtext : "+richtext);
                
        contenuText.setText(richtext, BufferType.SPANNABLE);
        // make our ClickableSpans and URLSpans work 
        contenuText.setMovementMethod(LinkMovementMethod.getInstance());
        
        ImageButton foldButton = (ImageButton) convertView.findViewById(R.id.detailsfiche_elementview_fold_unflod_section_imageButton);
        
        FoldableClickListener foldable = new FoldableClickListener(contenuText, foldButton);
        allFoldable.add(foldable);
        foldButton.setOnClickListener(foldable);
        
        LinearLayout titreLinearLayout = (LinearLayout) convertView.findViewById(R.id.detailsfiche_elementview_fold_unflod_section_linearlayout);
        titreLinearLayout.setOnClickListener(foldable);
        // enregistre pour réagir au click long
        registerForContextMenu(foldButton);
        registerForContextMenu(titreLinearLayout);
        
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
    		// pas préchargée en local pour l'instant, cherche sur internet
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
        	Log.d(LOG_TAG, "onClick() - v : "+v.toString());
        	Log.d(LOG_TAG, "onClick() - _position : "+_position+" - _ficheID : "+_ficheID);
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, ImagePleinEcran_CustomViewActivity.class);
            i.putExtra("position", _position);
            i.putExtra("ficheId", _ficheID);
            _activity.startActivity(i);
        }
 
    }
    
    public final SpannableString textToSpannableStringDoris(CharSequence texte) {
	    Log.d(LOG_TAG, "textToSpannableStringDoris() - texte : "+texte);
	    
	    SpannableString richtext = new SpannableString("");
	    
	    if ( !texte.toString().replaceAll("\\s", "").matches(".*\\{\\{[^\\}]*\\}\\}.*")) {
	    	Log.d(LOG_TAG, "textToSpannableStringDoris() - Aucun bloc {{*}}");
	    	return new SpannableString(texte);
	    	
	    } else {
	    	Log.d(LOG_TAG, "textToSpannableStringDoris() - Traitement blocs {{*}}");
	    	
	    	// TODO : doit être améliorable mais je n'arrive pas à manipuler directement SpannableString
	    	// donc pas de concat, pas de regexp.
	        List<TextSpan> listeFicheNumero = new ArrayList<TextSpan>();
	        
	        String texteInter = texte.toString();
	        StringBuilder texteFinal = new StringBuilder();
	        int iTmp = 0;
	        while (texteInter.contains("{{") && iTmp < 10 ) {
	        	iTmp ++;
	        	
	        	int posDepTexteInter = texteInter.indexOf("{{");
	        	int posFinTexteInter = texteInter.indexOf("}}");
	        	
	        	String balise = texteInter.substring(posDepTexteInter+2, posFinTexteInter);
	        	
	        	Log.d(LOG_TAG, "textToSpannableStringDoris() - texteInter : "+texteInter
	        			+ " - " + posDepTexteInter + "-" + posFinTexteInter + " -> " + balise);
	        	
	        	if (balise.equals("i")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		posDepTexteInter = texteInter.indexOf("{{/i}}");
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		Log.d(LOG_TAG, "textToSpannableStringDoris() - texteFinal : "+texteFinal);
	        	
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.ITALIQUE,posDepTexteFinal,posFinTexteFinal));
	        		
	        		texteInter = texteInter.substring(posDepTexteInter+6, texteInter.length());
	        	}
	        	else if (balise.equals("b")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posDepTexteFinal = texteFinal.length();
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        		
	        		posDepTexteInter = texteInter.indexOf("{{/b}}");
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) );
	        		int posFinTexteFinal = texteFinal.length();
	        		Log.d(LOG_TAG, "textToSpannableStringDoris() - texteFinal : "+texteFinal);
	        	
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.GRAS,posDepTexteFinal,posFinTexteFinal));
	        		
	        		texteInter = texteInter.substring(posDepTexteInter+6, texteInter.length());
	        	}
	        	else if (balise.equals("n")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) + "\n");
	        			        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	}
	        	else if (balise.matches("[0-9]*")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) + "(Fiche)");
	        		
	        		int posDepTexteFinal = texteFinal.length() - 7;
	        		int posFinTexteFinal = texteFinal.length();
	        		Log.d(LOG_TAG, "textToSpannableStringDoris() - texteFinal : "+texteFinal);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.FICHE,posDepTexteFinal,posFinTexteFinal,
	        				balise));
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	}
	        	else if (balise.startsWith("D:")){
	        		texteFinal.append( texteInter.substring(0, posDepTexteInter) + "(*)");
	        		
	        		int posDepTexteFinal = texteFinal.length() - 3;
	        		int posFinTexteFinal = texteFinal.length();
	        		Log.d(LOG_TAG, "textToSpannableStringDoris() - texteFinal : "+texteFinal);
	        		
	        		listeFicheNumero.add(new TextSpan(TextSpan.SpanType.DEFINITION,posDepTexteFinal,posFinTexteFinal,
	        				balise.substring(2, balise.length())));
	        		
	        		texteInter = texteInter.substring(posFinTexteInter+2, texteInter.length());
	        	}
	        } // fin du While
	        
	        texteFinal.append(texteInter);
	        Log.d(LOG_TAG, "textToSpannableStringDoris() - texteFinal après while : "+texteFinal);
	        Log.d(LOG_TAG, "textToSpannableStringDoris() - longueur : "+texteFinal.length());
	        
	        richtext = new SpannableString(texteFinal);
	        
	        for (final TextSpan ts : listeFicheNumero) {
	        	Log.d(LOG_TAG, "textToSpannableStringDoris() - ts : "+ts.spanType.name()+" - "+ts.info);
	        	
	        	if ( ts.spanType == TextSpan.SpanType.ITALIQUE ) {
	        		richtext.setSpan(new StyleSpan(Typeface.ITALIC), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.GRAS ) {
	        		richtext.setSpan(new StyleSpan(Typeface.BOLD), ts.positionDebut, ts.positionFin, 0);
	        		richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_gras))), ts.positionDebut, ts.positionFin, 0);
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.FICHE ) {

	        		ClickableSpan clickableSpan = new ClickableSpan() {  
			            @Override  
			            public void onClick(View view) {  
			                //Toast.makeText(context, "Test (Fiche Liée) : "+ficheId, Toast.LENGTH_LONG).show();
			            	Intent toDetailView = new Intent(context, DetailsFiche_ElementViewActivity.class);
			                Bundle b = new Bundle();
			                b.putInt("ficheNumero", Integer.valueOf(ts.info) );
			                
			                RuntimeExceptionDao<Fiche, Integer> entriesDao = getHelper().getFicheDao();
			                b.putInt("ficheId", entriesDao.queryForEq("numeroFiche", Integer.valueOf(ts.info)).get(0).getId() );
			                
			        		toDetailView.putExtras(b);
			                startActivity(toDetailView);
			            }  
			        };
			     	Log.d(LOG_TAG, "addFoldableView() - SpannableString : "+ts.positionDebut + " - " + ts.positionFin);
			    	
					richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
					richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_lienfiche))), ts.positionDebut, ts.positionFin, 0);  
	        	}
	        	else if ( ts.spanType == TextSpan.SpanType.DEFINITION) {

	        		ClickableSpan clickableSpan = new ClickableSpan() {  
			            @Override  
			            public void onClick(View view) {  
			                Toast.makeText(context, "Test Définition : "+ts.info, Toast.LENGTH_LONG).show();
			            	/*
			                Intent toDetailView = new Intent(context, DetailsFiche_ElementViewActivity.class);
			                Bundle b = new Bundle();
			                b.putInt("ficheNumero", Integer.valueOf(ts.info) );
			                
			                RuntimeExceptionDao<Fiche, Integer> entriesDao = getHelper().getFicheDao();
			                b.putInt("ficheId", entriesDao.queryForEq("numeroFiche", Integer.valueOf(ts.info)).get(0).getId() );
			                
			        		toDetailView.putExtras(b);
			                startActivity(toDetailView);
			                */
			            }  
			        };
			     	Log.d(LOG_TAG, "addFoldableView() - SpannableString : "+ts.positionDebut + " - " + ts.positionFin);
			    	
					richtext.setSpan(clickableSpan, ts.positionDebut, ts.positionFin, 0);
					richtext.setSpan(new ForegroundColorSpan(Color.parseColor(context.getString(R.string.detailsfiche_elementview_couleur_liendefinition))), ts.positionDebut, ts.positionFin, 0);
	        	}
	        }
	        
	        return richtext;
	    }

    }
    
    public static class TextSpan {
    	
    	public enum SpanType {
    	    FICHE, ITALIQUE, GRAS, SAUTDELIGNE, DEFINITION
    	} 
    	
    	SpanType spanType = null;
    	int positionDebut;
    	int positionFin;
    	String info = ""; 
    	
    	public TextSpan(SpanType spanType, int positionDebut, int positionFin) {
    		this.spanType = spanType;
    		this.positionDebut = positionDebut;
    		this.positionFin = positionFin;
    	}
    	
    	public TextSpan(SpanType spanType, int positionDebut, int positionFin, String info) {
    		this.spanType = spanType;
    		this.positionDebut = positionDebut;
    		this.positionFin = positionFin;
    		this.info = info;
    	}
    }
    
    // End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
