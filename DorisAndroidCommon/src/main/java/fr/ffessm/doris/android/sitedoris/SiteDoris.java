/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
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

package fr.ffessm.doris.android.sitedoris;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.Participant;

public class SiteDoris {

	// Initialisation de la Gestion des Log
	public static Log log = LogFactory.getLog(SiteDoris.class);
	
	// Constructeur
    public SiteDoris(){
    	
    }

	public Groupe getGroupeFromListeGroupes(List<Groupe> listeGroupes, int numGroupe, int numSousGroupe){
    	//log.trace("getGroupeFromListeGroupes() - Début");
    	//log.debug("getGroupeFromListeGroupes() - numGroupe : "+numGroupe);
    	//log.debug("getGroupeFromListeGroupes() - numSousGroupe : "+numSousGroupe);
    	
    	// Contrôle basique des entrées
    	if (numGroupe == 0) {
    		log.error("getGroupeFromListeGroupes() - refGroupe ne peut être égal à 0");
    		return null;
    	}
    	
    	for (Groupe groupe : listeGroupes) {
    	
    		if ( groupe.getNumeroGroupe() == numGroupe && ( numSousGroupe == 0 || groupe.getNumeroSousGroupe() == numSousGroupe) ) {
    			//log.debug("getGroupeFromListeGroupes() - Groupe Trouvé : "+groupe.getId()+" - "+groupe.getNomGroupe());
    			//log.trace("getGroupeFromListeGroupes() - Fin");
    			return groupe;
    		}
    		
    	}
    	
    	//log.debug("getGroupeFromListeGroupes() - Fin (sans avoir trouvé de groupe correspondant)");
		return null;
    }

    public HashSet<FicheLight> getListeFichesUpdated(HashSet<FicheLight> inListeFichesRef, HashSet<FicheLight> inListeFichesSite) {
    	//log.debug("getListeFichesUpdated()- Début");
    	//log.debug("getListeFichesUpdated()- Liste Base : "+inListeFichesRef.size());
    	//log.debug("getListeFichesUpdated()- Liste Site : "+inListeFichesSite.size());
    	
    	HashSet<FicheLight> listeFichesUpdated = new HashSet<FicheLight>(0);
     	
    	HashSet<String> listeClesRef = new HashSet<String>(inListeFichesRef.size());
    
    	// Chargement de la liste des clés de comparaisons
    	Iterator<FicheLight> iFicheRef = inListeFichesRef.iterator();
    	while (iFicheRef.hasNext()) {
    		FicheLight ficheRef = iFicheRef.next();
    		//log.info("getListeFichesUpdated() - ficheRef.getCleCompareUpdate() : "+ficheRef.getCleCompareUpdate());
    		
    		listeClesRef.add( ficheRef.getCleCompareUpdate() );
    	}
    	
    	// Pour chaque Fiche du site, on regarde si la clé existe dans la liste ci dessus
    	Iterator<FicheLight> iFicheSite = inListeFichesSite.iterator();
    	while (iFicheSite.hasNext()) {
    		FicheLight ficheSite = iFicheSite.next();
    		//log.info("getListeFichesUpdated() - ficheSite.getCleCompareUpdate() : "+ficheSite.getCleCompareUpdate());
    		
    		if ( ! listeClesRef.contains(ficheSite.getCleCompareUpdate()) ){
    			listeFichesUpdated.add(ficheSite);
    		}
    		
    	}
    	//log.debug("getListeFichesUpdated()- Liste Site Updated : "+listeFichesUpdated.size());
    	
		//log.debug("getListeFichesUpdated()- Fin");
		return listeFichesUpdated;
    }
    
    public Participant getParticipantFromListeParticipants(List<Participant> listeParticipants, int numParticipant){
    	//log.trace("getParticipantFromListeParticipants() - Début");
    	//log.debug("getParticipantFromListeParticipants()) - numParticipant : "+numParticipant);
    	
    	// Contrôle basique des entrées
    	if (numParticipant == 0) {
    		log.error("getParticipantFromListeParticipants() - numParticipant ne peut être égal à 0");
    		return null;
    	}
    	
    	for (Participant participant : listeParticipants) {
    	
    		if ( participant.getNumeroParticipant() == numParticipant) {
    			//log.debug("getParticipantFromListeParticipants() - Participant Trouvé : "+participant.getId());
    			//log.trace("getParticipantFromListeParticipants() - Fin");
    			return participant;
    		}
    		
    	}
    	
    	//log.debug("getParticipantFromListeParticipants() - Fin (sans avoir trouvé de Participant correspondant)");
		return null;
    }

	public static boolean existsFileAtURL(String httpURL){
		try {
			HttpURLConnection.setFollowRedirects(false);
			// note : you may also need
			//        HttpURLConnection.setInstanceFollowRedirects(false)
			HttpURLConnection con =
					(HttpURLConnection) new URL(httpURL).openConnection();
			con.setRequestMethod("HEAD");
			return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
