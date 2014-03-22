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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.prefetch.PrefetchDorisWebSite.ActionKind;


public class PrefetchGlossaire {


	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchGlossaire.class);
	
	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;
	
	private ActionKind action;
	private int nbMaxFichesATraiter;
	
	public PrefetchGlossaire(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, int nbMaxFichesATraiter) {
		this.dbContext = dbContext;
		this.connectionSource = connectionSource;
		this.action = action;
		this.nbMaxFichesATraiter = nbMaxFichesATraiter;
	}
	
	
	
	public int prefetch() {
		// - - - Glossaire - - -
		// On boucle sur les initiales des définitions (Cf site : doris.ffessm.fr/glossaire.asp?filtre=?)
		// On récupère la liste des termes dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
		
		PrefetchTools prefetchTools = new PrefetchTools();
		
		String listeFiltres;
		String contenuFichierHtml = null;
		
		try {
						
			if (nbMaxFichesATraiter == PrefetchConstants.nbMaxFichesTraiteesDef){
				listeFiltres="abcdefghijklmnopqrstuvwxyz";
			} else {
				listeFiltres="ab";
			}
			
			for (char initiale : listeFiltres.toCharArray()){
				log.debug("doMain() - Recup Page de définitions pour la lettre : "+initiale);
				
				int numero = 1;
				boolean continuer;
				do {
					String listeDefinitionsFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/listeDefinitions-"+initiale+"-"+numero+".html";
					log.info("Récup. Liste des Définitions : " + listeDefinitionsFichier);
					
					if ( action != ActionKind.NODWNLD ){
						if (prefetchTools.getFichierFromUrl(Constants.getListeDefinitionsUrl(""+initiale,""+numero ), listeDefinitionsFichier)) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeDefinitionsFichier));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste des Définitions : "+initiale+"-"+numero);
							System.exit(1);
						}
					} else {
						// NODWNLD
						listeDefinitionsFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeDefinitions-"+initiale+"-"+numero+".html";
						if (new File(listeDefinitionsFichier).exists()) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeDefinitionsFichier));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste des Définitions : "+initiale+"-"+numero);
							System.exit(1);
						}
					}
					
					continuer = SiteDoris.getContinuerListeDefinitionsParInitialeFromHtml(contenuFichierHtml);
					
					final List<DefinitionGlossaire> listeDefinitionsFromHTML = SiteDoris.getListeDefinitionsParInitialeFromHtml(contenuFichierHtml);
					log.info("Creation de "+listeDefinitionsFromHTML.size()+" définitions pour la lettre : "+initiale);
					TransactionManager.callInTransaction(connectionSource,
						new Callable<Void>() {
							public Void call() throws Exception {
								for (DefinitionGlossaire definition : listeDefinitionsFromHTML){
									if (!dbContext.definitionGlossaireDao.idExists(definition.getId()))
										dbContext.definitionGlossaireDao.create(definition);
								}
								return null;
						    }
						});
					
					numero ++;
				} while (continuer && numero < 10);
			} 
			
			List<DefinitionGlossaire> listeDefinitions = new ArrayList<DefinitionGlossaire>(0);
			listeDefinitions.addAll(dbContext.definitionGlossaireDao.queryForAll());
			log.debug("doMain() - listeDefinitions.size : "+listeDefinitions.size());
			
			// Pour chaque Définitions, on télécharge la page (si nécessaire) puis on la traite
			int nbDefinitionTelechargees = 0;
			for (DefinitionGlossaire definition : listeDefinitions) {
				log.debug("doMain() - Traitement Définition : "+definition.getNumeroDoris());
				
				String urlDefinition =  Constants.getDefinitionUrl( ""+definition.getNumeroDoris() );
				String fichierLocalDefinition = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/definition-"+definition.getNumeroDoris()+".html";
				String fichierRefDefinition = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/definition-"+definition.getNumeroDoris()+".html";
				if ( action == ActionKind.INIT ) {
					if (prefetchTools.getFichierFromUrl(urlDefinition, fichierLocalDefinition)) {
						nbDefinitionTelechargees += 1;
						contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierLocalDefinition));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la définition : "+urlDefinition);
						continue;
					}
				} else if ( action == ActionKind.UPDATE || action == ActionKind.CDDVD ) {
					if ( ! new File(fichierRefDefinition).exists() ) {
						if (prefetchTools.getFichierFromUrl(urlDefinition, fichierLocalDefinition)) {
							nbDefinitionTelechargees += 1;
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierLocalDefinition));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la définition : "+urlDefinition);
							continue;
						}
					} else {
						if (new File(fichierRefDefinition).exists()) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierRefDefinition));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la définition sur le disque : "+fichierRefDefinition+" a échoué.");
						}
					}
				} else if ( action == ActionKind.NODWNLD ) {
					if (new File(fichierRefDefinition).exists()) {
						contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(fichierRefDefinition));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la définition sur le disque : "+fichierRefDefinition+" a échoué.");
					}
				}
				
				definition.setContextDB(dbContext);
				definition.getDefinitionsFromHtml(contenuFichierHtml);
				dbContext.definitionGlossaireDao.update(definition);
				
				if ( nbDefinitionTelechargees !=0 && (nbDefinitionTelechargees % 50) == 0) {
					log.info("Définitions traitées = "+nbDefinitionTelechargees+", pause de 1s...");
					Thread.sleep(1000);
				}
				
				//Si des photos dans la définition, il faut les télécharger dans le cas CDDVD
				if (action == ActionKind.CDDVD){
					
					String fichierImageRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/";
					String fichierImageRefRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/";
					
					List<String> listeImagesDefinition = definition.getListeImagesDefinition();
					
					if (listeImagesDefinition != null) {
						for (String image : listeImagesDefinition) {
				    			
							// On stocke la photo dans les Vignettes
							if( ! prefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+"/"+Constants.PREFIX_IMGDSK_DEFINITION+image ) ){
								if (prefetchTools.getFichierFromUrl(Constants.ILLUSTRATION_DEFINITION_BASE_URL+"/"+image,
										fichierImageRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+"/"+Constants.PREFIX_IMGDSK_DEFINITION+image)) {
								} else {
									log.error("Une erreur est survenue lors de la récupération d'une photo de la définition de : "+definition.getTerme());
									//System.exit(1);
								}
							}
						}
					}
				}
			}
					
			return listeDefinitions.size();
			
		} catch ( Exception e) {
			// une erreur est survenue
			log.error("Une erreur est survenue dans PrefetchGlossaire");
			return -1;
		}


	}
}
