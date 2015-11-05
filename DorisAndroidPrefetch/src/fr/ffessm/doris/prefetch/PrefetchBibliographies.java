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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.Constants.FileHtmlKind;
import fr.ffessm.doris.android.sitedoris.ErrorCollector;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.prefetch.PrefetchDorisWebSite.ActionKind;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;


public class PrefetchBibliographies {


	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchBibliographies.class);
	
	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;

	private ActionKind action;
	private int nbMaxFichesATraiter;
	
	public PrefetchBibliographies(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, int nbMaxFichesATraiter) {
		this.dbContext = dbContext;
		this.connectionSource = connectionSource;
		this.action = action;
		this.nbMaxFichesATraiter = nbMaxFichesATraiter;
	}
	
	
	
	public int prefetch() {
		// - - - Bibliographie - - -
		// On boucle sur la page des Fiches tant que l'on trouve dans la page courante (n)
		//biblio.asp?mapage=(n+1)&PageCourante=n
		// On récupère les Bibliographies dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
		
		PrefetchTools prefetchTools = new PrefetchTools();
		SiteDoris siteDoris = new SiteDoris();
		
		String contenuFichierHtml = null;
		
		try {
		
			int pageCourante = 1;
			boolean testContinu = false;
			
			do {
				log.debug("doMain() - pageCourante Bibliographie : "+pageCourante);
				ErrorCollector.getInstance().addGroup("biblio.page_biblio_"+pageCourante);
				
				String listeBibliographies = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/listeBibliographies-"+pageCourante+".html";
				log.info("Récup. Liste des Bibliographies : " + listeBibliographies);
				
				if ( action != ActionKind.NODWNLD){
					if (prefetchTools.getFichierFromUrl(Constants.getListeBibliographiesUrl(pageCourante), listeBibliographies)) {
						contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeBibliographies), FileHtmlKind.LISTE_BIBLIO);
					} else {
						ErrorCollector.getInstance().addError("Biblio "+pageCourante, "probleme lien page "+pageCourante, "Une erreur est survenue lors de la récupération de la liste des Bibliographies sur le lien :"+Constants.getListeBibliographiesUrl(pageCourante));
						log.error("Une erreur est survenue lors de la récupération de la liste des Bibliographies : " + listeBibliographies);
						System.exit(1);
					}
				} else {
					// NODWNLD
					listeBibliographies = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeBibliographies-"+pageCourante+".html";
					if (new File(listeBibliographies).exists()) {
						contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeBibliographies), FileHtmlKind.LISTE_BIBLIO);
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des Bibliographies : " + listeBibliographies);
						System.exit(1);
					}
				}
				
				final List<EntreeBibliographie> listeBiblioFromHTML = siteDoris.getListeBiblioFromHtml(contenuFichierHtml);
				//log.info("Creation de "+listeParticipantsFromHTML.size()+" participants pour la lettre : "+initiale);
				TransactionManager.callInTransaction(connectionSource,
						new Callable<Void>() {
							public Void call() throws Exception {
								for (EntreeBibliographie entreeBiblio : listeBiblioFromHTML){
									if (!dbContext.entreeBibliographieDao.idExists(entreeBiblio.getId()))
										dbContext.entreeBibliographieDao.create(entreeBiblio);
								}
								return null;
						    }
						});
				 
				pageCourante ++;
				testContinu = contenuFichierHtml.contains("biblio.asp?mapage="+pageCourante+"&");
			} while ( testContinu && (nbMaxFichesATraiter == PrefetchConstants.nbMaxFichesTraiteesDef || pageCourante <= 10) );
			
			List<EntreeBibliographie> listeEntreesBiblio = new ArrayList<EntreeBibliographie>(0);
			listeEntreesBiblio.addAll(dbContext.entreeBibliographieDao.queryForAll());
			log.debug("doMain() - listeEntreesBiblio.size : "+listeEntreesBiblio.size());
			
			// Téléchargement de la page de l'entrée bibliographique
			log.debug("doMain() - Téléchargement de la page de l'entrée bibliographique");
			int nbBiblioTelechargees = 0;
			
			for (EntreeBibliographie biblio : listeEntreesBiblio){
				if (nbMaxFichesATraiter == PrefetchConstants.nbMaxFichesTraiteesDef || nbBiblioTelechargees <= 10) {

					String urlBiblio = Constants.getBibliographieUrl(biblio.getNumeroDoris());
					String fichierBiblio = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/" + "biblio-" + biblio.getNumeroDoris()+".html";
					String fichierBiblioRef = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/" + "biblio-" + biblio.getNumeroDoris()+".html";
					
					if ( action == ActionKind.INIT ) {
						if( ! prefetchTools.isFileExistingPath( fichierBiblioRef ) ){
							if ( prefetchTools.getFichierFromUrl( urlBiblio, fichierBiblio ) ) {
								nbBiblioTelechargees += 1;
								contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierBiblio), FileHtmlKind.BIBLIO);
							} else {
								log.error("Une erreur est survenue lors de la récupération de la Biblio : "+urlBiblio);
								continue;
							}
						}
					} else if ( action == ActionKind.UPDATE || action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ) {
						if ( ! new File(fichierBiblioRef).exists() ) {
							if (prefetchTools.getFichierFromUrl(urlBiblio, fichierBiblio)) {
								nbBiblioTelechargees += 1;
								contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierBiblio), FileHtmlKind.BIBLIO);
							} else {
								log.error("Une erreur est survenue lors de la récupération de la Biblio : "+urlBiblio);
								continue;
							}
						} else {
							if (new File(fichierBiblioRef).exists()) {
								contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierBiblioRef), FileHtmlKind.BIBLIO);
							} else {
								log.error("Une erreur est survenue lors de la récupération de la Biblio sur le disque : "+fichierBiblioRef+" a échoué.");
							}
						}
					} else if ( action == ActionKind.NODWNLD ) {
						if (new File(fichierBiblioRef).exists()) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierBiblioRef), FileHtmlKind.BIBLIO);
						} else {
							log.error("Une erreur est survenue lors de la récupération de la Biblio sur le disque : "+fichierBiblioRef+" a échoué.");
						}
					}


					// Téléchargement Vérification existence de l'image / Photos Bibliographie
					if (contenuFichierHtml.contains(biblio.getNumeroDoris()+".jpg")) {
						// Maj Base de données
						final EntreeBibliographie biblioMaj = biblio;
						TransactionManager.callInTransaction(connectionSource,
							new Callable<Void>() {
								public Void call() throws Exception {

									biblioMaj.setCleURLIllustration("gestionenligne/photos_biblio_moy/" + biblioMaj.getNumeroDoris() + ".jpg");
									dbContext.entreeBibliographieDao.update(biblioMaj);
									return null;
							    }
							});
					
						if ( action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI  ) {
							String fichierImageRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/";
							String fichierImageRefRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/";
							
							// On stocke la photo dans les Vignettes
							if( ! prefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+"/"+Constants.PREFIX_IMGDSK_BIBLIO+biblio.getNumeroDoris()+".jpg" ) ){
								if (prefetchTools.getFichierFromUrl(Constants.ILLUSTRATION_BIBLIO_BASE_URL+"/"+biblio.getNumeroDoris()+".jpg",
										fichierImageRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+"/"+Constants.PREFIX_IMGDSK_BIBLIO+biblio.getNumeroDoris()+".jpg" )) {
								} else {
									log.warn("Une erreur est survenue lors de la récupération de la photo de l'entrée Biblio. : "+biblio.getTitre() + ", il est probable qu'il n'y ait pas d'illustration.");
								}
							}
						}
					}
				}
			}
					
			return listeEntreesBiblio.size();
			
		} catch ( Exception e) {
			// une erreur est survenue
			log.error("Une erreur est survenue dans PrefetchBibliographies");
			log.error(e);
			return -1;
		}
	}
	
	
	public int prefetchV4(DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper, DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper) {
		log.debug("prefetchV4()");
		try {
			List<Integer> nodeIds = dorisAPI_JSONTreeHelper.getBibliographieNodeIds(20);
			
			log.debug("nodeIds.size() : "+nodeIds.size());
			
			EntreeBibliographie entreeBibliographie = new EntreeBibliographie();
			
			for (Integer nodeId : nodeIds){
				log.debug("nodeId : "+nodeId);
				
				dorisAPI_JSONDATABindingHelper.getEntreeBibliographieFromEntreeBibliographieId(nodeId);
				
				if (!dbContext.entreeBibliographieDao.idExists(entreeBibliographie.getId()))
				{
					log.debug("création enregistrement");
					dbContext.entreeBibliographieDao.create(entreeBibliographie);
				}
				
			}
			
			
			return nodeIds.size();
		} catch ( Exception e) {
			// une erreur est survenue
			log.error("Une erreur est survenue dans PrefetchGlossaire");
			log.error(e);
			return -1;
		}
	}

}
