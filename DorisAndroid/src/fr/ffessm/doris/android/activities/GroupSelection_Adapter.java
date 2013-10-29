package fr.ffessm.doris.android.activities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.tools.OutilsGroupe;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class GroupSelection_Adapter extends BaseExpandableListAdapter {

	public static final String LOG_TAG = GroupSelection_Adapter.class.getName();
	
	protected ArrayList<Groupe> rawGroupes;
	
	private Context _context;
    private List<Groupe> groupesHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Groupe>> groupesChildMap;
    
    
    // niveau de profondeur utilisé pour le listGroup par rapport au Groupe 
    protected int listgroupLevel =1;
    
	
	public GroupSelection_Adapter(Context context, ArrayList<Groupe> rawGroupes, int listgroupLevel){
		super();
		_context = context;
		this.rawGroupes = rawGroupes;
		this.listgroupLevel = listgroupLevel;
		
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
		final String childText = ((Groupe) getChild(groupPosition, childPosition)).getNomGroupe();
		 
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.groupselection_customview_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.groupselection_customview_lblListItem);
 
        txtListChild.setText(childText);
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
		String headerTitle = ((Groupe) getGroup(groupPosition)).getNomGroupe();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.groupselection_customview_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.groupselection_customview_lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
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
