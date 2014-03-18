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

package fr.ffessm.doris.prefetch;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.android.sitedoris.Outils;


public class PrefetchIntervenants {


	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchIntervenants.class);
	
	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;
	
	private String action;
	private int nbMaxFichesATraiter;
	
	public List<Participant> listeParticipants = new ArrayList<Participant>(0);
	
	public PrefetchIntervenants(DorisDBHelper dbContext, ConnectionSource connectionSource, String action, int nbMaxFichesATraiter) {
		this.dbContext = dbContext;
		this.connectionSource = connectionSource;
		this.action = action;
		this.nbMaxFichesATraiter = nbMaxFichesATraiter;
	}
	
	
	
	public int prefetch() {
		// - - - Intervenants - - -
		// On boucle sur les initiales des gens (Cf site : doris.ffessm.fr/contacts.asp?filtre=?)
		// On récupère la liste des intervenants dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
		
		
		String listeFiltres;
		String contenuFichierHtml = null;
		
		try {
			
	
			if (nbMaxFichesATraiter == PrefetchConstants.nbMaxFichesTraiteesDef){
				listeFiltres="abcdefghijklmnopqrstuvwxyz";
			} else {
				listeFiltres="ab";
			}
			
			for (char initiale : listeFiltres.toCharArray()){
				log.debug("doMain() - Recup Participants : "+initiale);
				
				String listeParticipantsFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/listeParticipants-"+initiale+".html";
				log.info("Récup. Liste des Participants : " + listeParticipantsFichier);
				
				if (! action.equals("NODWNLD")){
					if (Outils.getFichierFromUrl(Constants.getListeParticipantsUrl(""+initiale), listeParticipantsFichier)) {
						contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeParticipantsFichier));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des Participants : "+initiale);
						System.exit(1);
					}
				} else {
					// NODWNLD
					listeParticipantsFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeParticipants-"+initiale+".html";
					if (new File(listeParticipantsFichier).exists()) {
						contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeParticipantsFichier));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des Participants : "+initiale);
						System.exit(1);
					}
				}
				
				final List<Participant> listeParticipantsFromHTML = SiteDoris.getListeParticipantsParInitialeFromHtml(contenuFichierHtml);
				log.info("Creation de "+listeParticipantsFromHTML.size()+" participants pour la lettre : "+initiale);
				TransactionManager.callInTransaction(connectionSource,
					new Callable<Void>() {
						public Void call() throws Exception {
							for (Participant participant : listeParticipantsFromHTML){
								if (!dbContext.participantDao.idExists(participant.getId()))
									dbContext.participantDao.create(participant);
							}
							return null;
					    }
					});
			}	
			
			
			listeParticipants.addAll(dbContext.participantDao.queryForAll());
			log.debug("doMain() - listeParticipants.size : "+listeParticipants.size());
			
			// Pas la peine de Récupérer la page de chacun des intervenants
			// Toutes les infos sont dans les listes ci dessus mais pour CDDVD
			// ça fait plus propre
			if ( action.equals("CDDVD") ) {
				String pageIntervenantRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/";
				String pageIntervenantRacineRef = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/";
				
				for (Participant participant : listeParticipants){
					
					if( ! PrefetchTools.isFileExistingPath( pageIntervenantRacineRef+"participant-"+participant.getNumeroParticipant()+".html") ){
						if ( Outils.getFichierFromUrl( Constants.getParticipantUrl(participant.getNumeroParticipant()),
								pageIntervenantRacine+"participant-"+participant.getNumeroParticipant()+".html") ) {
						} else {
							log.error("Une erreur est survenue lors de la récupération de la photo du participant : "+participant.getNom());
							//System.exit(1);
						}
					}
						
				}
			}
			
			// Téléchargement Photos Participants
			if ( action.equals("CDDVD") ) {
				String fichierImageRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/";
				String fichierImageRefRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/";
	
				for (Participant participant : listeParticipants){
	
					if ( !participant.getCleURLPhotoParticipant().isEmpty() ) {
						
						// On stocke la photo dans les Vignettes
						if( ! PrefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+"/"+participant.getPhotoNom().replace(" ", "_") ) ){
							String photoURL = URLEncoder.encode(participant.getCleURLPhotoParticipant(),"UTF-8");
							log.debug("doMain() - photoURL : "+photoURL);
							
							if (Outils.getFichierFromUrl(Constants.SITE_RACINE_URL+photoURL,
									fichierImageRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+"/"+participant.getPhotoNom().replace(" ", "_"))) {
							} else {
								log.error("Une erreur est survenue lors de la récupération de la photo du participant : "+participant.getNom());
								//System.exit(1);
							}
						}
					}
				}
			}
			
			return listeParticipants.size();
			
		} catch ( Exception e) {
			// une erreur est survenue
			log.error("Une erreur est survenue dans PrefetchIntervenants");
			return -1;
		}


	}
}
