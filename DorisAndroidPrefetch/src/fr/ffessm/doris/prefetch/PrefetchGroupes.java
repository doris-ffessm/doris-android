/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2015 - FFESSM
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

package fr.ffessm.doris.prefetch;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.android.sitedoris.Constants.FileHtmlKind;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.prefetch.PrefetchDorisWebSite.ActionKind;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;


public class PrefetchGroupes {


	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchGroupes.class);
	
	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;
	
	private ActionKind action;
	private int nbMaxFichesATraiter;
	
	public List<Groupe> listeGroupes;
	private Groupe groupeMaj;
	
	public PrefetchGroupes(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, int nbMaxFichesATraiter) {
		this.dbContext = dbContext;
		this.connectionSource = connectionSource;
		this.action = action;
		this.nbMaxFichesATraiter = nbMaxFichesATraiter;
	}
	
	
	
	public int prefetch() {
		// - - - Groupes - - -
		// Récupération de la liste des groupes sur le site de DORIS
		// En UPDATE et CDDVD on re-télécharge la liste
		
		PrefetchTools prefetchTools = new PrefetchTools();	
		SiteDoris siteDoris = new SiteDoris();
		
		String listeGroupesFichier = "";
		String contenuFichierHtml = null;
			
		try {
			
			if ( action != ActionKind.NODWNLD ){
				listeGroupesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/listeGroupes.html";
				log.info("Récup. Liste Groupes Doris : " + listeGroupesFichier);
				
				if (prefetchTools.getFichierFromUrl(Constants.getGroupesZoneUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES)), listeGroupesFichier)) {
					contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeGroupesFichier), FileHtmlKind.LISTE_GROUPES);
					
				} else {
					log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
					System.exit(1);
				}
			} else {
				// NODWNLD
				listeGroupesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeGroupes.html";
				if (new File(listeGroupesFichier).exists()) {
					contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeGroupesFichier), FileHtmlKind.LISTE_GROUPES);
				} else {
					log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
					System.exit(1);
				}
			}
			
			listeGroupes = siteDoris.getListeGroupesFromHtml(contenuFichierHtml);
			log.debug("doMain() - listeGroupes.size : "+listeGroupes.size());
			
			TransactionManager.callInTransaction(connectionSource,
				new Callable<Void>() {
					public Void call() throws Exception {
						for (Groupe groupe : listeGroupes){
							dbContext.groupeDao.create(groupe);
						}
						return null;
				    }
				});

			for (Groupe groupe : listeGroupes){
				log.info("Groupe : " + groupe.getNomGroupe());
				if (groupe.getNumeroGroupe() != 0 && (nbMaxFichesATraiter == PrefetchConstants.nbMaxFichesTraiteesDef || groupe.getNumeroGroupe() <= 10) ) {
					String fichierLocalContenuGroupe = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/groupe-10-"+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe()+"-1.html";
					String fichierRefContenuGroupe = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/groupe-10-"+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe()+"-1.html";
					
					if ( action != ActionKind.NODWNLD && action != ActionKind.CDDVD_MED
							|| action == ActionKind.CDDVD_HI && action != ActionKind.UPDATE){
						
						if (prefetchTools.getFichierFromUrl(Constants.getGroupeContenuUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES),
								groupe.getNumeroGroupe(), groupe.getNumeroSousGroupe(), 1), fichierLocalContenuGroupe)) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierLocalContenuGroupe), FileHtmlKind.GROUPE);
						} else {
							log.error("Une erreur est survenue lors du téléchargement du groupe : "+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe());
							System.exit(1);
						}
						
					} else if (action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI
							|| action == ActionKind.UPDATE) {
						
						// UPDATE ou CDDVD
						if ( prefetchTools.isFileExistingPath( fichierRefContenuGroupe ) ) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierRefContenuGroupe), FileHtmlKind.GROUPE);
						} else if (prefetchTools.getFichierFromUrl(Constants.getGroupeContenuUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES),
								groupe.getNumeroGroupe(), groupe.getNumeroSousGroupe(), 1), fichierLocalContenuGroupe)) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierLocalContenuGroupe), FileHtmlKind.GROUPE);
						} else {
							log.error("Une erreur est survenue lors du téléchargement du groupe : "+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe());
							System.exit(1);
						}
						
					} else {
						// NODWNLD
						if ( prefetchTools.isFileExistingPath( fichierRefContenuGroupe ) ) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierRefContenuGroupe), FileHtmlKind.GROUPE);
						} else {
							log.error("Une erreur est survenue lors de la récupération du groupe : "+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe());
							System.exit(1);
						}
					}
										
					groupe.setContextDB(dbContext);
					groupe.descriptionDetailleeFromHtml(contenuFichierHtml);
					groupeMaj = groupe;
					TransactionManager.callInTransaction(connectionSource,
						new Callable<Void>() {
							public Void call() throws Exception {
								dbContext.groupeDao.update(groupeMaj);
								return null;
						    }
						});
					
				}
			}
			// Téléchargement des pages de Groupes
			if ( action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ){

				List<ZoneGeographiqueKind> listZone = Arrays.asList(ZoneGeographiqueKind.values());
				for (ZoneGeographiqueKind zone : listZone ) {
					
					int zoneId = Constants.getNumZoneForUrl(zone);
					String fichierGroupes = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/groupes_zone-"+zoneId+".html";

					if (prefetchTools.getFichierFromUrl(Constants.getGroupesZoneUrl(zoneId), fichierGroupes)) {
						contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierGroupes), FileHtmlKind.GROUPES_ZONE);
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des Groupes : " + zone.toString());
						System.exit(1);
					}
					
					final List<Groupe> listeGroupesZone = siteDoris.getListeGroupesFromHtml(contenuFichierHtml);
					log.debug("doMain() - listeGroupesZone.size : "+listeGroupesZone.size());
					
					for (Groupe groupe : listeGroupesZone) {
						
						if (groupe.getNumeroGroupe() != 0  && (nbMaxFichesATraiter == PrefetchConstants.nbMaxFichesTraiteesDef || groupe.getNumeroGroupe() <= 10) ) {
							int pageCourante = 1;
							boolean testContinu = false;
							
							do {
								log.debug("doMain() - page Groupe : "+zoneId+" - "+groupe.getNumeroGroupe()+" - "+groupe.getNumeroSousGroupe()+" - "+pageCourante);

								String fichierPageGroupe = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/groupe-"+zoneId+"-"+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe()+"-"+pageCourante+".html";

								if (prefetchTools.getFichierFromUrl(Constants.getGroupeContenuUrl(zoneId, groupe.getNumeroGroupe(), groupe.getNumeroSousGroupe(), pageCourante), fichierPageGroupe)) {
									contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierPageGroupe), FileHtmlKind.GROUPE);
								} else {
									log.error("Une erreur est survenue lors de la récupération de la page des groupes : " + fichierPageGroupe);
									System.exit(1);
								}

								pageCourante ++;
								testContinu = siteDoris.getContinuerContenuGroupeFromHtml(contenuFichierHtml);
							
							} while ( testContinu );
							
						}
					}
				}
			}

			return listeGroupes.size();
			
		} catch ( Exception e) {
			// une erreur est survenue
			log.error("Une erreur est survenue dans PrefetchGroupes");
			log.error(e);
			return -1;
		}

	}
	
	public int prefetchV4(DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper) {
		// - - - Groupes - - -
		// Récupération de la liste des groupes sur le site de DORIS
		// En UPDATE et CDDVD on re-télécharge la liste
		log.debug("prefetchV4()");
		try {
			List<Integer> nodeIds = dorisAPI_JSONTreeHelper.getGroupesNodeIds();
			
			return nodeIds.size();
		} catch ( Exception e) {
			// une erreur est survenue
			log.error("Une erreur est survenue dans PrefetchGroupes");
			log.error(e);
			return -1;
		}
	}
	
}
