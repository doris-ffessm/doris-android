/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2014 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Groupe;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

//Start of user code protected additional GroupeSelection_Adapter imports
// additional imports

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Groupes_Outils;
//End of user code

public class GroupeSelection_Adapter extends BaseAdapter  {
	
	private Context context;

	/**
     * dbHelper used to autorefresh values and doing queries
     * must be set other wise most getter will return proxy that will need to be refreshed
	 */
	protected DorisDBHelper _contextDB = null;

	private static final String LOG_TAG = GroupeSelection_Adapter.class.getCanonicalName();

    private List<Groupe> groupeList;
    public List<Groupe> filteredGroupeList;
	SharedPreferences prefs;
	//Start of user code protected additional GroupeSelection_Adapter attributes
	boolean depuisAccueil = false;
	
	public GroupeSelection_Adapter(Context context, DorisDBHelper contextDB, boolean depuisAccueil) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		updateList();
		
		this.depuisAccueil = depuisAccueil;
	}
	
	// additional attributes
	// niveau de profondeur utilisé pour le listGroup par rapport au Groupe 
    public Groupe currentRootGroupe;
	
	/*protected void buildTreeForRoot(Groupe rootGroupe){
		this.currentRootGroupe = rootGroupe;
		
		// trouve les groups de Groupe pour le niveau requis
		groupesHeader = OutilsGroupe.getAllGroupesForNextLevel(rawGroupes, rootGroupe);
		Log.d(LOG_TAG, "Created groupesHeader.size="+groupesHeader.size());
		// crée les fils pour ces groups de Groupe
		groupesChildMap = new HashMap<String, List<Groupe>>();
		for (Groupe groupe : groupesHeader) {
			List<Groupe> childs = new ArrayList<Groupe>();
			childs.addAll(groupe.getGroupesFils());
			groupesChildMap.put(groupe.getNomGroupe(), childs);
			Log.d(LOG_TAG, "     Created childs.size="+childs.size());
		}
		notifyDataSetChanged();
		refreshNavigation();
	}*/
	/*protected void refreshNavigation(){
		LinearLayout navigationLayout = (LinearLayout)context.findViewById(R.id.groupselection_customview_navigation);
    	
    	navigationLayout.removeAllViews();
    	addBackToParentGroupButton(navigationLayout, currentRootGroupe.getGroupePere());
    	if(currentRootGroupe.getId() ==1){
			// ajout du nouveau bouton standard
			ImageView rootImage = new ImageView(_context);
			navigationLayout.addView(rootImage);
			rootImage.setImageResource(R.drawable.arbre_phylogenetique_gris);
			rootImage.setPadding(5, 5, 5, 5);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rootImage.getLayoutParams();
			layoutParams.leftMargin =2;
			layoutParams.rightMargin = 2;
			rootImage.setLayoutParams(layoutParams);
			rootImage.setMaxHeight(30);
			
		}
		else{
	    	TextView groupeNavigationText = new TextView(context);
	    	groupeNavigationText.setText(currentRootGroupe.getNomGroupe());
	    	navigationLayout.addView(groupeNavigationText);
		}
	}*/
	//End of user code

	public GroupeSelection_Adapter(Context context, DorisDBHelper contextDB) {
		super();
		this.context = context;
		this._contextDB = contextDB;
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
		updateList();
	}
	
	protected void updateList(){
		// Start of user code protected GroupeSelection_Adapter updateList
		// TODO find a way to query in a lazier way
		try{
			if(groupeList == null){
				this.groupeList = _contextDB.groupeDao.queryForAll();
				for (Groupe groupe : groupeList) {
		        	groupe.setContextDB(_contextDB);
				}
			}
			if(currentRootGroupe == null)
				currentRootGroupe = Groupes_Outils.getroot(groupeList);
			buildTreeForRoot(currentRootGroupe);
			
		} catch (java.sql.SQLException e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		// End of user code
	}

	@Override
	public int getCount() {
		return filteredGroupeList.size();
	}

	@Override
	public Object getItem(int position) {
		return filteredGroupeList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup viewGroup) {
		
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.groupeselection_listviewrow, null);
        }
		final Groupe entry = filteredGroupeList.get(position);
		if(_contextDB != null) entry.setContextDB(_contextDB); 		
       
		// set data in the row 
		TextView tvLabel = (TextView) convertView.findViewById(R.id.groupeselection_listviewrow_label);
        StringBuilder labelSB = new StringBuilder();
		labelSB.append(entry.getNomGroupe());
		labelSB.append(" ");
        tvLabel.setText(labelSB.toString());

        TextView tvDetails = (TextView) convertView.findViewById(R.id.groupeselection_listviewrow_details);
		StringBuilder detailsSB = new StringBuilder();
		detailsSB.append(entry.getDescriptionGroupe().toString());
		detailsSB.append(" ");
        tvDetails.setText(detailsSB.toString());
		
        // assign the entry to the row in order to ease GUI interactions
        LinearLayout llRow = (LinearLayout)convertView.findViewById(R.id.groupeselection_listviewrow);
        llRow.setTag(entry);
        
		// Start of user code protected additional GroupeSelection_Adapter getView code
		//	additional code
        
        ImageView ivIcon = (ImageView) convertView.findViewById(R.id.groupeselection_listviewrow_icon);
        ivIcon.setBackgroundResource(R.drawable.groupe_icon_background);
        if(entry.getCleURLImage() != null && !entry.getCleURLImage().isEmpty()){
        	//Picasso.with(context).load(Constants.getSiteUrl()+entry.getCleURLImage()).placeholder(R.drawable.app_ic_launcher).into(ivIcon);
         	int identifierIconeGroupe = context.getResources().getIdentifier(entry.getImageNameOnDisk().replaceAll("\\.[^\\.]*$", ""), "raw",  context.getPackageName());
        	//if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getView() - identifierIconeGroupe : "+identifierIconeGroupe); 
        	
        	Bitmap bitmap = BitmapFactory.decodeStream(context.getResources().openRawResource(identifierIconeGroupe));
        	ivIcon.setImageBitmap(bitmap);
        }
        else{
        	// remet image de base
        	ivIcon.setImageResource(R.drawable.app_ic_launcher);
        }
        
        ImageButton selectbutton = (ImageButton) convertView
                .findViewById(R.id.groupeselection_btnSelect);
        selectbutton.setFocusable(false);
        selectbutton.setClickable(true);
        selectbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Filtre espèces : "+entry.getNomGroupe(), Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
				ed.putInt(context.getString(R.string.pref_key_filtre_groupe), entry.getId());
		        ed.commit();
		        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "onClick() - depuisAccueil : " + depuisAccueil);
		        if (!depuisAccueil) {
		            ((GroupeSelection_ClassListViewActivity)context).finish();
		        } else {
		        	Intent toListeFiche_View = new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class);
		        	toListeFiche_View.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        	context.getApplicationContext().startActivity(toListeFiche_View);
		        }
			}
		});
        
        
        // ajout de l'image "expand" si contient des sous groupes
        ImageView ivHasChildGroup = (ImageView) convertView.findViewById(R.id.groupeselection_btnHasChildGroup);
        
        if(entry.getGroupesFils().size() > 0){
        	ivHasChildGroup.setVisibility(View.VISIBLE);
        }
        else {
        	ivHasChildGroup.setVisibility(View.GONE);
        }
		// End of user code

        return convertView;

	}

	
	//Start of user code protected additional GroupeSelection_Adapter methods
	// additional methods
	public Groupe getGroupeFromPosition(int position) {
		return  filteredGroupeList.get(position);
		
	}
	protected void refreshNavigation(){
		LinearLayout navigationLayout = (LinearLayout)((GroupeSelection_ClassListViewActivity)context).findViewById(R.id.groupselection_listview_navigation);
    	
    	navigationLayout.removeAllViews();
    	addBackToParentGroupButton(navigationLayout, currentRootGroupe.getGroupePere());
    	if(currentRootGroupe.getId() == 1){
			// ajout du nouveau bouton standard
			ImageView rootImage = new ImageView(context);
			navigationLayout.addView(rootImage);
			rootImage.setImageResource(R.drawable.ic_action_arbre_phylogenetique);
			rootImage.setPadding(5, 5, 5, 5);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rootImage.getLayoutParams();
			layoutParams.leftMargin = 2;
			layoutParams.rightMargin = 2;
			rootImage.setLayoutParams(layoutParams);
			rootImage.setMaxHeight(30);
			
		}
		else{
	    	TextView groupeNavigationText = new TextView(context);
	    	groupeNavigationText.setText(currentRootGroupe.getNomGroupe());
	    	groupeNavigationText.setPadding(2, 5, 5, 2);
	    	navigationLayout.addView(groupeNavigationText);
		}
	}
	protected void addBackToParentGroupButton(LinearLayout navigationLayout, final Groupe parent){
		if(parent == null) return;
		if(parent.getContextDB() == null) parent.setContextDB(_contextDB);
		addBackToParentGroupButton(navigationLayout, parent.getGroupePere());
		
		
		if(parent.getId() == 1){
			// ajout du nouveau bouton standard
			ImageButton backToParentButton = new ImageButton(context);
			navigationLayout.addView(backToParentButton);
			backToParentButton.setImageResource(R.drawable.ic_action_arbre_phylogenetique);
			backToParentButton.setBackgroundResource(R.drawable.button_selected_background);
			backToParentButton.setPadding(5, 5, 5, 5);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) backToParentButton.getLayoutParams();
			layoutParams.leftMargin =2;
			layoutParams.rightMargin = 2;
			backToParentButton.setLayoutParams(layoutParams);
			backToParentButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					buildTreeForRoot(parent);
				}
			});
		}
		else{
			// ajout du nouveau bouton
			Button backToParentButton = new Button(context);
			navigationLayout.addView(backToParentButton);
			Log.w(LOG_TAG,"addBackToParentGroupButton parent="+parent);
			Log.d(LOG_TAG,"addBackToParentGroupButton parent.getNomGroupe="+parent.getNomGroupe());
			backToParentButton.setText(parent.getNomGroupe().trim());
			backToParentButton.setBackgroundResource(R.drawable.button_selected_background);
			backToParentButton.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
			backToParentButton.setPadding(5, 5, 5, 0);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) backToParentButton.getLayoutParams();
			layoutParams.leftMargin =2;
			layoutParams.rightMargin = 2;
			layoutParams.bottomMargin = 5;
			backToParentButton.setLayoutParams(layoutParams);
			backToParentButton.setHeight(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_action_arbre_phylogenetique).getHeight()+10);
			backToParentButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					buildTreeForRoot(parent);
				}
			});
		}
	}
	public void buildTreeForRoot(Groupe rootGroupe){
		this.currentRootGroupe = rootGroupe;
		
		List<Groupe> nextLevelGroupes  = Groupes_Outils.getAllGroupesForNextLevel(this.groupeList, currentRootGroupe);
		if(nextLevelGroupes.size() > 0)
			this.filteredGroupeList  = nextLevelGroupes;
		notifyDataSetChanged();
		refreshNavigation();
	}
	//End of user code
}
