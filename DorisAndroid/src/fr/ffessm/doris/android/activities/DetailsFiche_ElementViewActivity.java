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
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
// Start of user code protectedDetailsFiche_ElementViewActivity_additional_import
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ffessm.doris.android.activities.view.FoldableClickListener;
import fr.ffessm.doris.android.async.TelechargePhotosFiches_BgActivity;
import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.tools.Outils;
// End of user code

public class DetailsFiche_ElementViewActivity extends OrmLiteBaseActivity<OrmLiteDBHelper> {
	
	protected int ficheId;
	
	private static final String LOG_TAG = DetailsFiche_ElementViewActivity.class.getCanonicalName();
	
	static final int FOLD_SECTIONS_MENU_ID = 1;	
	static final int UNFOLD_SECTIONS_MENU_ID = 2;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailsfiche_elementview);
        ficheId = getIntent().getExtras().getInt("ficheId");
                
       
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		refreshScreenData();
	}
    
    boolean isOnCreate = true;
    List<FoldableClickListener> allFoldable = new ArrayList<FoldableClickListener>();
    
    // attention au risque de conserver trop de donnée si appels répété à refreshScreenData ?
    private void refreshScreenData() {
    	// get our dao
    	RuntimeExceptionDao<Fiche, Integer> entriesDao = getHelper().getFicheDao();
    	Fiche entry = entriesDao.queryForId(ficheId);
    	entry.setContextDB(getHelper().getDorisDBHelper());

		((TextView) findViewById(R.id.detailsfiche_elementview_nomscientifique)).setText(entry.getNomScientifique());
		((TextView) findViewById(R.id.detailsfiche_elementview_nomcommun)).setText(entry.getNomCommun());
		((TextView) findViewById(R.id.detailsfiche_elementview_numerofiche)).setText("N° "+((Integer)entry.getNumeroFiche()).toString());					
		((TextView) findViewById(R.id.detailsfiche_elementview_etatfiche)).setText(((Integer)entry.getEtatFiche()).toString());	
		
		// Start of user code protectedDetailsFiche_ElementViewActivity.refreshScreenData
		/*SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    	((TextView) findViewById(R.id.detail_divedate)).setText(dateFormatter.format(entry.getDate()));
		
    	((TextView) findViewById(R.id.detail_divelocation)).setText(entry.getLocation());
    	
    	((TextView) findViewById(R.id.detail_divedepth)).setText(entry.getMaxdepth().toString());
    	
    	((TextView) findViewById(R.id.detail_diveduration)).setText(entry.getDuration().toString());
    	*/	
		StringBuffer sbDebugText = new StringBuffer();
		if(entry.getPhotoPrincipale()!=null){
			sbDebugText.append("photoPrincipale="+entry.getPhotoPrincipale());
			//try {
				//Outils.getVignetteFile(getBaseContext(), entry.getPhotoPrincipale());
				ImageView ivIcon = (ImageView) findViewById(R.id.detailsfiche_elementview_icon);
		        Bitmap iconBitmap = Outils.getAvailableImagePrincipaleFiche(getBaseContext(), entry);
		        if(iconBitmap != null){
		        	ivIcon.setImageBitmap(iconBitmap);        	
		        	//ivIcon.setAdjustViewBounds(true);
		        	//ivIcon.setLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT);
		        	//ivIcon.setLayoutParams(new Gallery.LayoutParams(
		            //    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		        }
			//} catch (IOException e) {
			//	Log.e(LOG_TAG, e.getMessage(), e);
			//}
			//entry.getPhotoPrincipale().getImageVignette();
		}
		if(entry.getPhotosFiche()!=null){
			sbDebugText.append("\nnbPhoto="+entry.getPhotosFiche().size()+"\n");
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
    
	private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

	
}
