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
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
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

import fr.ffessm.doris.android.activities.view.FoldableClickListener;
import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.DataChangedListener;
import fr.ffessm.doris.android.datamodel.Groupe;
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
	final Activity activity = this;
	
	protected int ficheNumero;
	
	static final int FOLD_SECTIONS_MENU_ID = 1;	
	static final int UNFOLD_SECTIONS_MENU_ID = 2;
	static final int GLOSSAIRE_MENU_ID = 3;
	
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
	    
    	((TextView) findViewById(R.id.detailsfiche_elementview_nomscientifique)).setText( Outils.textToSpannableStringDoris(context, entry.getNomScientifique()) );
		((TextView) findViewById(R.id.detailsfiche_elementview_nomcommun)).setText(entry.getNomCommun().replaceAll("\\{\\{[^\\}]*\\}\\}", ""));
		((TextView) findViewById(R.id.detailsfiche_elementview_numerofiche)).setText("N° "+((Integer)entry.getNumeroFiche()).toString());					
		((TextView) findViewById(R.id.detailsfiche_elementview_etatfiche)).setText(((Integer)entry.getEtatFiche()).toString());	
		
		
		TextView btnEtatFiche = (TextView)  findViewById(R.id.detailsfiche_elementview_etatfiche);
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
		
		ImageView picoEspeceReglementee = (ImageView)  findViewById(R.id.detailsfiche_elementview_picto_reglementee);
		ImageView picoEspeceDanger = (ImageView)  findViewById(R.id.detailsfiche_elementview_picto_dangereuse);
		if (entry.getPictogrammes().contains(Constants.PictoKind.PICTO_ESPECE_REGLEMENTEE.ordinal()+";")) {
			picoEspeceReglementee.setVisibility(View.VISIBLE);
			picoEspeceReglementee.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, R.string.picto_espece_reglementee_label, Toast.LENGTH_LONG).show();
				}
			});
		}
		if (entry.getPictogrammes().contains(Constants.PictoKind.PICTO_ESPECE_DANGEREUSE.ordinal()+";")) {
			picoEspeceDanger.setVisibility(View.VISIBLE);
			picoEspeceDanger.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, R.string.picto_espece_en_danger_label, Toast.LENGTH_LONG).show();
				}
			});
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
				photoView.setPadding(0, 0, 2, 0);
				photoGallery.addView(photoView);
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
					addFoldableTextView(containerLayout, getString(R.string.detailsfiche_elementview_autresdenominations_label), sbAutresDenominations.toString());
					
				}
			}
			// Section Groupe Phylogénique
			if(entry.getGroupe().getNumeroGroupe() != 0){
				addFoldableGroupeView(containerLayout, getString(R.string.detailsfiche_elementview_groupes_label), entry.getGroupe());
			}
			
			// sections issues de la fiche
			if(entry.getContenu() != null){
				
				for (SectionFiche sectionFiche : entry.getContenu()) {
					//Log.d(LOG_TAG, "refreshScreenData() - titre : "+sectionFiche.getTitre());
					//Log.d(LOG_TAG, "refreshScreenData() - texte : "+sectionFiche.getTexte());
					addFoldableTextView(containerLayout, sectionFiche.getTitre(), sectionFiche.getTexte());
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
					addFoldableTextView(containerLayout, getString(R.string.detailsfiche_elementview_zonesgeo_label), sbZonesGeographiques.toString());
				} else {
					addFoldableTextView(containerLayout, getString(R.string.detailsfiche_elementview_zonesgeo_label), getString(R.string.detailsfiche_elementview_zonesgeo_aucune_label));
				}
			}
			
			// section "Crédits"
			StringBuilder sbCreditText = new StringBuilder();
			final String urlString = Constants.getFicheFromIdUrl( entry.getNumeroFiche() ); 
			sbCreditText.append("{{A:"+urlString+"}}");
			sbCreditText.append(urlString);
			sbCreditText.append("{{/A}}");
			
			sbCreditText.append("\n"+getString(R.string.detailsfiche_elementview_datecreation_label));
			sbCreditText.append(entry.getDateCreation());
			
			if (!entry.getDateModification().isEmpty()) {
				sbCreditText.append("\n"+getString(R.string.detailsfiche_elementview_datemodification_label));
				sbCreditText.append(entry.getDateModification());
			}
			
			for (IntervenantFiche intervenant : entry.getIntervenants()) {
				intervenant.setContextDB(getHelper().getDorisDBHelper());
				//sbCreditText.append("\n"+intervenant.getId());
				sbCreditText.append("\n"+Constants.getTitreParticipant(intervenant.getRoleIntervenant() )+" : " );				
				
				intervenant.setContextDB(getHelper().getDorisDBHelper());
				Participant participant = intervenant.getParticipant();
				participant.setContextDB(getHelper().getDorisDBHelper());
				
				sbCreditText.append("{{P:"+participant.getId()+"}}");
				sbCreditText.append(participant.getNom());
				sbCreditText.append("{{/P}}");
				
			}
			
			SpannableString richtext = Outils.textToSpannableStringDoris(context, sbCreditText.toString());
			//richtext.setSpan(new RelativeSizeSpan(2f), 0, urlString.length(), 0);
			//richtext.setSpan(new URLSpan(urlString), 0, urlString.length(), 0);
			
			addFoldableTextView(containerLayout, getString(R.string.detailsfiche_elementview_credit_label),richtext);
			
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
       
		//Start of user code additional onCreateOptionsMenu DetailsFiche_EditableElementViewActivity
        menu.add(Menu.NONE, FOLD_SECTIONS_MENU_ID, 1, R.string.fold_all_sections_menu_option).setIcon(R.drawable.app_expander_ic_maximized);
		menu.add(Menu.NONE, UNFOLD_SECTIONS_MENU_ID, 2, R.string.unfold_all_sections_menu_option).setIcon(R.drawable.app_expander_ic_minimized);
		menu.add(Menu.NONE, GLOSSAIRE_MENU_ID, 0, R.string.accueil_glossaire_text).setIcon(R.drawable.app_glossaire);
		
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
        
		//Start of user code additional menu actionDetailsFiche_ElementViewActivity
        	case FOLD_SECTIONS_MENU_ID:
        		foldAll();
				break;
        	case UNFOLD_SECTIONS_MENU_ID:
        		unfoldAll();
				break;
        	case GLOSSAIRE_MENU_ID:
        		Intent toDefinitionlView = new Intent(context, Glossaire_ClassListViewActivity.class);
            	context.startActivity(toDefinitionlView);
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
        menu.add(Menu.NONE, FOLD_SECTIONS_MENU_ID, 1, R.string.fold_all_sections_menu_option).setIcon(R.drawable.app_ic_expand_close);
		menu.add(Menu.NONE, UNFOLD_SECTIONS_MENU_ID, 2, R.string.unfold_all_sections_menu_option).setIcon(R.drawable.app_ic_expand_open);

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
    
    protected void addFoldableTextView(LinearLayout containerLayout, String titre, CharSequence texte){
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
        SpannableString richtext = Outils.textToSpannableStringDoris(context, texte);
        
        
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
    protected void addFoldableGroupeView(LinearLayout containerLayout, String titre, final Groupe groupe){
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = inflater.inflate(R.layout.detailsfiche_elementview_foldablesection_2icones, null);
        
        TextView titreText = (TextView) convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_titre);
        titreText.setText(titre);
        
        RelativeLayout sectionIcones = (RelativeLayout) convertView.findViewById(R.id.detailsfiche_elementview_fold_unflod_section_icones);
        
        if (!Outils.getParamBoolean(this.getApplicationContext(), R.string.pref_key_fiche_aff_details_pardefaut, false)){
        	sectionIcones.setVisibility(View.GONE); // par défaut invisible
        } else {
        	sectionIcones.setVisibility(View.VISIBLE);
        }
        
        final Groupe groupePere = getHelper().getGroupeDao().queryForId(groupe.getGroupePere().getId());
 
        ImageButton icone1 = (ImageButton) convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldableicone1);
        TextView texte1 = (TextView) convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldabletexte1);
        ImageButton icone2 = (ImageButton) convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldableicone2);
        TextView texte2 = (TextView) convertView.findViewById(R.id.detailsfiche_elementview_foldablesection_foldabletexte2);
        
        if (groupe.getNumeroSousGroupe() == 0){
        	int identifierIcone1Groupe = context.getResources().getIdentifier(groupe.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw",  context.getPackageName());
        	Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIcone1Groupe));
        	icone1.setImageBitmap(bitmap);
        	icone1.setBackgroundResource(R.drawable.groupe_icon_background);
        	texte1.setText(groupe.getNomGroupe());
        	icone1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
					ed.putInt(context.getString(R.string.pref_key_filtre_groupe), groupe.getId());
			        ed.commit();
			        startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
				}
			});
        	icone2.setVisibility(View.GONE);
        	texte2.setVisibility(View.GONE);
        } else {
        	int identifierIcone1Groupe = context.getResources().getIdentifier(groupePere.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw",  context.getPackageName());
        	Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIcone1Groupe));
        	icone1.setImageBitmap(bitmap);
        	icone1.setBackgroundResource(R.drawable.groupe_icon_background);
        	texte1.setText(groupePere.getNomGroupe());
        	icone1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
					ed.putInt(context.getString(R.string.pref_key_filtre_groupe), groupePere.getId());
			        ed.commit();
			        startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
				}
			});
        	
           	int identifierIcone2Groupe = context.getResources().getIdentifier(groupe.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw",  context.getPackageName());
           	bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIcone2Groupe));
        	icone2.setImageBitmap(bitmap);
        	icone2.setBackgroundResource(R.drawable.groupe_icon_background);
        	texte2.setText(groupe.getNomGroupe());
        	icone2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
					ed.putInt(context.getString(R.string.pref_key_filtre_groupe), groupe.getId());
			        ed.commit();
			        startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
				}
			});
        }
        
        ImageButton foldButton = (ImageButton) convertView.findViewById(R.id.detailsfiche_elementview_fold_unflod_section_imageButton);
        
        FoldableClickListener foldable = new FoldableClickListener(sectionIcones, foldButton);
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
				.placeholder(R.drawable.doris_icone_doris_large)  // utilisation de l'image par defaut pour commencer
				.error(R.drawable.doris_icone_doris_large_pas_connecte)
				.fit()
				.centerInside()
    			.into(imageView);
    	}
        
        layout.addView(imageView);
        return layout;
   
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
    
    
    
    // End of user code

	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
