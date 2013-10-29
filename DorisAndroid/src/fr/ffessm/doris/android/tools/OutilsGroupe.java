package fr.ffessm.doris.android.tools;

import java.util.ArrayList;

import fr.ffessm.doris.android.datamodel.Groupe;

public class OutilsGroupe {

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
}
