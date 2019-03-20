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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;

public class SiteDoris {

	// Initialisation de la Gestion des Log
	public static Log log = LogFactory.getLog(SiteDoris.class);
	
	// Constructeur
    public SiteDoris(){
    	
    }
        
	public HashSet<FicheLight> getListeFichesFromHtml(String inCodePageHtml) {
    	//log.info("getListeFichesFromHtml()- Début");
		//log.debug("getListeFichesFromHtml()- inCodePageHtml.length() : " + inCodePageHtml.length());
		Common_Outils commonOutils = new Common_Outils();
		
    	HashSet<FicheLight> listeFiches = new HashSet<FicheLight>(0);
    	
    	// Dans les version suivante de Jericho le niveau de trace a changé
    	//Config.LoggerProvider = LoggerProvider.DISABLED;
    	
    	Source source=new Source(commonOutils.remplacementBalises(commonOutils.nettoyageBalises(inCodePageHtml),false ) );

    	source.fullSequentialParse();
    	//log.debug("getListeFichesFromHtml()- source.length() : " + source.length());
    	//log.debug("getListeFichesFromHtml()- source : " + source.toString().substring(0, Math.min(100, source.toString().length())));
    	
    	Element elementTableracine;
    	try{
    		elementTableracine=source.getFirstElementByClass("titre_page").getParentElement().getParentElement();
    	}
    	catch ( NullPointerException e){
    		log.error("Probleme lors de la récupération de la liste des fiches, peut etre du a une connexion web defectueuse ou a un changement sur le site web Doris",  e);
    		return listeFiches;
    	}
    	//log.debug("getListeFichesFromHtml()- elementTableracine.length() : " + elementTableracine.length());
    	//log.debug("getListeFichesFromHtml()- elementTableracine : " + elementTableracine.toString().substring(0, Math.min(100, elementTableracine.toString().length())));

    	List<? extends Element> listeElementsTD = elementTableracine.getAllElements(HTMLElementName.TD);
    	//log.debug("getListeFichesFromHtml() - listeElementsTD.size() : " + listeElementsTD.size());
		
    	for (Element elementTD : listeElementsTD) {
    		//log.debug("getListeFichesFromHtml() - elementTD.length() : " + elementTD.length());
    		//log.debug("getListeFichesFromHtml()- elementTD : " + elementTD.toString().substring(0, Math.min(100, elementTD.toString().length())));
    		
			if (elementTD.getAttributeValue("width") != null){
    			if (elementTD.getAttributeValue("width").toString().equals("75%")) {
    				//log.debug("getListeFichesFromHtml() - elementTD : "+elementTD.getRenderer());
    				Element elementTDA = elementTD.getFirstElement(HTMLElementName.A);
    				
    				//String contenu = elementTDA.getRenderer().toString();
    				//log.debug("getListeFichesFromHtml() - contenu : "+contenu);
    				
    				String ficheNomScientifique = elementTDA.getRenderer().toString().replaceAll("([^-]*)-(.*)", "$1").trim();
    				String ficheNomCommun = elementTDA.getRenderer().toString().replaceAll("([^-]*)-(.*)", "$2").trim();
    				int ficheId = Integer.parseInt(elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", "").replaceAll("&.*", ""));
    				int ficheEtat = Integer.parseInt(elementTDA.getAttributeValue("href").replaceAll(".*fiche_etat=", "").replaceAll("&.*", ""));
    				
    				//log.debug("getListeFichesFromHtml() - fiche : "+ficheId+" - "+ficheNomScientifique+" - "+ficheNomCommun + " - Etat : " + ficheEtat);
    				
    				FicheLight fiche = new FicheLight(ficheId, ficheEtat, ficheNomScientifique, ficheNomCommun);
      				
    				listeFiches.add(fiche);
    			}
			}
			
		}
    	
    	source = null;
    	listeElementsTD = null;
		//log.info("getListeFichesFromHtml()- Fin");
		return listeFiches;
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

}
