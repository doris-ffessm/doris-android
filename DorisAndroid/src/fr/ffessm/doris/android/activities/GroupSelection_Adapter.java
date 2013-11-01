package fr.ffessm.doris.android.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.tools.OutilsGroupe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GroupSelection_Adapter extends BaseExpandableListAdapter {

	public static final String LOG_TAG = GroupSelection_Adapter.class.getName();
	
	protected ArrayList<Groupe> rawGroupes;
	
	private GroupSelection_CustomViewActivity _context;
    private List<Groupe> groupesHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Groupe>> groupesChildMap;
    
    
    // niveau de profondeur utilisé pour le listGroup par rapport au Groupe 
    public Groupe currentRootGroupe;
    
	
	/*public GroupSelection_Adapter(Context context, ArrayList<Groupe> rawGroupes, int listgroupLevel){
		super();
		_context = context;
		this.rawGroupes = rawGroupes;
		this.rootGroupe = listgroupLevel;
		
		// trouve les groups de Groupe pour le niveau requis
		groupesHeader = OutilsGroupe.getAllGroupesForLevel(rawGroupes, listgroupLevel);
		Log.d(LOG_TAG, "Created groupesHeader.size="+groupesHeader.size());
		// crée les fils pour ces groups de Groupe
		groupesChildMap = new HashMap<String, List<Groupe>>();
		for (Groupe groupe : groupesHeader) {
			List<Groupe> childs = new ArrayList<Groupe>();
			childs.addAll(groupe.getGroupesFils());
			groupesChildMap.put(groupe.getNomGroupe(), childs);
			Log.d(LOG_TAG, "     Created childs.size="+childs.size());
		}
	}*/
	
	public GroupSelection_Adapter(GroupSelection_CustomViewActivity context, ArrayList<Groupe> rawGroupes, Groupe rootGroupe){
		super();
		_context = context;
		this.rawGroupes = rawGroupes;
		buildTreeForRoot(rootGroupe);
		
	}
	
	
	protected void buildTreeForRoot(Groupe rootGroupe){
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
	}
	
	protected void refreshNavigation(){
		LinearLayout navigationLayout = (LinearLayout)_context.findViewById(R.id.groupselection_customview_navigation);
    	
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
	    	TextView groupeNavigationText = new TextView(_context);
	    	groupeNavigationText.setText(currentRootGroupe.getNomGroupe());
	    	navigationLayout.addView(groupeNavigationText);
		}
	}
	
	protected void addBackToParentGroupButton(LinearLayout navigationLayout, final Groupe parent){
		if(parent == null) return;
		addBackToParentGroupButton(navigationLayout, parent.getGroupePere());
		
		
		if(parent.getId() ==1){
			// ajout du nouveau bouton standard
			ImageButton backToParentButton = new ImageButton(_context);
			navigationLayout.addView(backToParentButton);
			backToParentButton.setImageResource(R.drawable.arbre_phylogenetique_gris);
			backToParentButton.setBackgroundResource(R.drawable.button_background);
			backToParentButton.setPadding(5, 5, 5, 5);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) backToParentButton.getLayoutParams();
			layoutParams.leftMargin =2;
			layoutParams.rightMargin = 2;
			backToParentButton.setLayoutParams(layoutParams);
			backToParentButton.setMaxHeight(30);
			backToParentButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					buildTreeForRoot(parent);
				}
			});
		}
		else{
			// ajout du nouveau bouton standard
			Button backToParentButton = new Button(_context);
			navigationLayout.addView(backToParentButton);
			backToParentButton.setText(parent.getNomGroupe().trim());
			backToParentButton.setBackgroundResource(R.drawable.button_background);
			backToParentButton.setTextColor(_context.getResources().getColor(android.R.color.darker_gray));
			backToParentButton.setPadding(5, 5, 5, 5);
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) backToParentButton.getLayoutParams();
			layoutParams.leftMargin =2;
			layoutParams.rightMargin = 2;
			backToParentButton.setLayoutParams(layoutParams);
			backToParentButton.setHeight(30);
			backToParentButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					buildTreeForRoot(parent);
				}
			});
		}
	}
	
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return groupesChildMap.get(groupesHeader.get(groupPosition).getNomGroupe()).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final Groupe childGroupe = (Groupe) getChild(groupPosition, childPosition);
		final String childText = childGroupe.getNomGroupe();
		 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.groupselection_customview_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.groupselection_customview_lblListItem);
 
        txtListChild.setText(childText);
        
        ImageButton moreButton = (ImageButton)convertView
                .findViewById(R.id.groupselection_customview_lblListItem_moreBtn);
        // n'affiche la possibilité de faire un focus que s'il y a de sous groupe
        if(childGroupe.getGroupesFils().isEmpty()){
            moreButton.setVisibility(View.GONE);
        }
        else{
        moreButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				buildTreeForRoot(childGroupe);
			}});
        }
        
        ImageButton selectbutton = (ImageButton) convertView
                .findViewById(R.id.groupselection_customview_lblListItem_selectBtn);
        selectbutton.setFocusable(false);
        selectbutton.setClickable(true);
        selectbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(_context, "Filtre espèces : "+childGroupe.getNomGroupe(), Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(_context).edit();
				ed.putInt(_context.getString(R.string.pref_key_filtre_groupe), childGroupe.getId());
		        ed.commit();
				_context.finish();
			}
		});
        return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		Log.d(LOG_TAG, "getChildrenCount groupPosition="+groupPosition);
		return groupesChildMap.get(groupesHeader.get(groupPosition).getNomGroupe()).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupesHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupesHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		Log.d(LOG_TAG, "getGroupView groupPosition="+groupPosition);
		final Groupe groupe = (Groupe) getGroup(groupPosition);
		String headerTitle = groupe.getNomGroupe();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.groupselection_customview_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.groupselection_customview_lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        // n'affiche la possibilité de faire un focus que s'il y a de sous groupe
        ImageButton morebutton = (ImageButton) convertView
                .findViewById(R.id.groupselection_customview_lblListGroup_moreBtn);
        morebutton.setFocusable(false);
        if(groupe.getGroupesFils().isEmpty()){
            morebutton.setVisibility(View.GONE);
        }
        else{
            morebutton.setClickable(true);
	        morebutton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					buildTreeForRoot(groupe);
				}
			});
        }
        ImageButton selectbutton = (ImageButton) convertView
                .findViewById(R.id.groupselection_customview_lblListGroup_selectBtn);
        selectbutton.setFocusable(false);
        selectbutton.setClickable(true);
        selectbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(_context, "Filtre espèces : "+groupe.getNomGroupe(), Toast.LENGTH_SHORT).show();
				SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(_context).edit();
				ed.putInt(_context.getString(R.string.pref_key_filtre_groupe), groupe.getId());
		        ed.commit();
				_context.finish();
			}
		});
        return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	

}
