/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
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
