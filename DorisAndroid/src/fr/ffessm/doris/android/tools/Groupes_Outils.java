package fr.ffessm.doris.android.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.util.Log;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;

public class Groupes_Outils {

	public static final String LOG_TAG = Groupes_Outils.class.getSimpleName();
	public static Groupe getroot(List<Groupe> allGroupes){
		// trouve le groupe racine
		Groupe rootGroupe = null;
		for(Groupe groupe : allGroupes){
			if(groupe.getNomGroupe().equals("racine")){
				rootGroupe = groupe;
				break;
			}
		}
		return rootGroupe;
	}
	public static ArrayList<Groupe> getAllGroupesForLevel(ArrayList<Groupe> allGroupes, int listgroupLevel){
		// trouve le groupe racine
		Groupe rootGroupe = null;
		for(Groupe groupe : allGroupes){
			if(groupe.getNomGroupe().equals("racine")){
				rootGroupe = groupe;
				break;
			}
		}
		ArrayList<Groupe> currentLevelGroupes = new ArrayList<Groupe>();
		if(rootGroupe != null){
		
			currentLevelGroupes.add(rootGroupe);
			for (int i = 0; i < listgroupLevel; i++) {
				currentLevelGroupes = getAllGroupesForNextLevel(currentLevelGroupes);
			}
		}
		return currentLevelGroupes;
	}
	public static ArrayList<Groupe> getAllGroupesForNextLevel(ArrayList<Groupe> currentLevelGroupes){
		ArrayList<Groupe> nextLevelGroups = new ArrayList<Groupe>();
		for (Groupe groupe : currentLevelGroupes) {
			nextLevelGroups.addAll(groupe.getGroupesFils());
		}
		return nextLevelGroups;
	}
	public static List<Groupe> getAllGroupesForNextLevel(
			List<Groupe> rawGroupes, Groupe rootGroupe) {
		ArrayList<Groupe> nextLevelGroups = new ArrayList<Groupe>();
		nextLevelGroups.addAll(rootGroupe.getGroupesFils());
		return nextLevelGroups;
	}
	
	public static boolean isFichePartOfGroupe(Fiche fiche, Groupe searchedGroupe){
		boolean result = false;
		Groupe groupeFiche = fiche.getGroupe();
		if(groupeFiche == null){
			Log.w(LOG_TAG, "PB pas de groupe pour la fiche isFichePartOfGroupe("+fiche.getNomCommun()+" "+fiche.getId()+", "+searchedGroupe.getNomGroupe()+")");
			return true;
		}
		groupeFiche.setContextDB(fiche.getContextDB());
		if(groupeFiche.getId() == searchedGroupe.getId()) result= true;
		else result= isGroupePartOfGroupe(groupeFiche, searchedGroupe);
		if(BuildConfig.DEBUG) Log.d(LOG_TAG, "isFichePartOfGroupe("+fiche.getNomCommun()+", "+searchedGroupe.getNomGroupe()+") ="+result);
		return result;
	}
	public static boolean isGroupePartOfGroupe(Groupe groupe, Groupe searchedGroupe){
		Groupe groupeParent = groupe.getGroupePere();
		if(groupeParent == null) return false;
		groupeParent.setContextDB(groupe.getContextDB());
		if(groupeParent.getId() == searchedGroupe.getId()) return true;
		else return isGroupePartOfGroupe(groupeParent, searchedGroupe);
	}
	public static ArrayList<Groupe> getAllSubGroupesForGroupe( Groupe groupe){
		
		ArrayList<Groupe> subGroupes = new ArrayList<Groupe>();
		subGroupes.add(groupe);
		Collection<Groupe> directSubGroupes = groupe.getGroupesFils();
		for (Groupe subgroupe : directSubGroupes) {
			subgroupe.setContextDB(groupe.getContextDB());
			subGroupes.addAll(getAllSubGroupesForGroupe(subgroupe));
		}
		return subGroupes;
	}
}
