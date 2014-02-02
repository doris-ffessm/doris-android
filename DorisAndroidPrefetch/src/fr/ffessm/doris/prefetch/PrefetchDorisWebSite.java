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
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.commons.io.FileUtils;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.IntervenantFiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.ZoneObservation;
import fr.ffessm.doris.android.datamodel.associations.Fiches_DefinitionsGlossaire;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesObservations;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.android.sitedoris.Outils;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;


public class PrefetchDorisWebSite {

	// we are using the in-memory H2 database
	//private final static String DATABASE_URL = "jdbc:h2:mem:fiche";
	// we are using the created SQLite database
	private final static String DATABASE_URL = "jdbc:sqlite:run/database/DorisAndroid.db";

	// Dossiers liés au fonctionnement de l'appli prefetch
	private final static String DOSSIER_RACINE = "./run";
	private final static String DOSSIER_RES_HTML = "./res/html";
	
	// Ces dossiers seront renommés qd nécessaire
	private final static String DOSSIER_HTML = "html";
	private final static String DOSSIER_HTML_REF = "html_ref";
	private final static String DOSSIER_IMAGES = "images";
	private final static String DOSSIER_IMAGES_REF = "images_ref";
	private final static String SOUSDOSSIER_ICONES = "icones";
	private final static String SOUSDOSSIER_VIGNETTES = "vignettes_fiches";
	private final static String SOUSDOSSIER_MED_RES = "medium_res_images_fiches";
	private final static String SOUSDOSSIER_HI_RES = "hi_res_images_fiches";
	
	private final static String DOSSIER_CD = "cd";
	private final static String DOSSIER_DVD = "dvd";
	
	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchDorisWebSite.class);
	
	// Nombre maximum de fiches traitées (--max=K permet de changer cette valeur)
	private static int nbMaxFichesTraitees = 9999;
	private static String action = "";
	
	
	// Balises et Attributs des fichiers XML
	public static final String XML_BASE = "Doris";
	public static final String XML_FICHES = "Fiches";
	public static final String XML_GROUPES = "Groupes";
	public static final String XML_ATT_DATE_CREAT = "DateCreation";
	public static final String XML_ATT_SITE_URL = "UrlRacineSite";
	
	DorisDBHelper dbContext = null;

	public static void main(String[] args) throws Exception {
		
		// turn our static method into an instance of Main
		new PrefetchDorisWebSite().doMain(args);
		
	}

	private void doMain(String[] args) throws Exception {
		log.debug("doMain() - Début");
		
		// Vérification et lecture des arguments
		log.debug("doMain() : Vérification et lecture des arguments");
		action = checkArgs(args);
		log.info("action : " + action);
		log.info("Nb. Fiches Max : " + nbMaxFichesTraitees);
		
		// Vérification, Création, Sauvegarde des dossiers de travails
		checkDossiers(action);
		
		if (action.equals("TEST")) {
			log.debug("doMain() - Début TEST");
			

			

			log.debug("doMain() - Fin TEST");
		} else {

			ConnectionSource connectionSource = null;
			try {
				// - - - Base de Données - - -
				// create empty DB and initialize it for Android
				initializeSQLite(DATABASE_URL);
				
				// create our data-source for the database
				connectionSource = new JdbcConnectionSource(DATABASE_URL);
				// setup our database and DAOs
				setupDatabase(connectionSource);
				databaseInitialisation(connectionSource);
				
				// - - - Groupes - - -
				// Récupération de la liste des groupes sur le site de DORIS
				// En UPDATE et CDDVD on re-télécharge la liste
				String listeGroupesFichier = "";
				String contenuFichierHtml = null;
				
				if (! action.equals("NODWNLD")){
					listeGroupesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeGroupes.html";
					log.info("Récup. Liste Groupes Doris : " + listeGroupesFichier);
					
					if (Outils.getFichierFromUrl(Constants.getGroupesUrl(), listeGroupesFichier)) {
						contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeGroupesFichier));
						
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(0);
					}
				} else {
					// NODWNLD
					listeGroupesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/listeGroupes.html";
					if (new File(listeGroupesFichier).exists()) {
						contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeGroupesFichier));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(0);
					}
				}
				
				final List<Groupe> listeGroupes = SiteDoris.getListeGroupesFromHtml(contenuFichierHtml);
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

				// Téléchargement des pages de Groupes et des Icônes
				if ( action.equals("CDDVD")){
					String fichierIconeRacine = DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_ICONES + "/";
					String fichierIconeRefRacine = DOSSIER_RACINE + "/" + DOSSIER_IMAGES_REF + "/" + SOUSDOSSIER_ICONES + "/";

					for (Groupe groupe : listeGroupes){
						log.info("Groupe : " + groupe.getNomGroupe());
						if (groupe.getNumeroGroupe() != 0) {
							String fichierLocalGroupe = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/groupe-"+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe()+".html";
							String fichierRefGroupe = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/groupe-"+groupe.getNumeroGroupe()+"-"+groupe.getNumeroSousGroupe()+".html";
	
							if( ! isFileExistingPath( fichierRefGroupe ) ){
								if (Outils.getFichierFromUrl(Constants.getGroupeUrl(groupe.getNumeroGroupe(), groupe.getNumeroSousGroupe()), fichierLocalGroupe)) {
								} else {
									log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
									System.exit(0);
								}
							}
						}
						log.info("Groupe : " + groupe.getNomGroupe()+" - "+groupe.getCleURLImage());
						if ( !groupe.getCleURLImage().isEmpty() ) {
							if( ! isFileExistingPath( fichierIconeRefRacine+groupe.getImageNameOnDisk() ) ){
								if (Outils.getFichierFromUrl(Constants.getSiteUrl() + groupe.getCleURLImage(), fichierIconeRacine + groupe.getImageNameOnDisk())) {
								} else {
									log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
									System.exit(0);
								}
							}
						}
					}
				}

				// - - - Intervenants - - -
				// On boucle sur les initiales des gens (Cf site : doris.ffessm.fr/contacts.asp?filtre=?)
				// On récupère la liste des intervants dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
				String listeFiltres;
				if (nbMaxFichesTraitees == 9999){
					listeFiltres="abcdefghijklmnopqrstuvwxyz";
				} else {
					listeFiltres="ab";
				}
				
				for (char initiale : listeFiltres.toCharArray()){
					log.debug("doMain() - Recup Participants : "+initiale);
					
					String listeParticipantsFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeParticipants-"+initiale+".html";
					log.info("Récup. Liste des Participants : " + listeParticipantsFichier);
					
					if (! action.equals("NODWNLD")){
						if (Outils.getFichierFromUrl(Constants.getListeParticipantsUrl(""+initiale), listeParticipantsFichier)) {
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeParticipantsFichier));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste des Participants : "+initiale);
							System.exit(0);
						}
					} else {
						// NODWNLD
						listeParticipantsFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/listeParticipants-"+initiale+".html";
						if (new File(listeParticipantsFichier).exists()) {
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeParticipantsFichier));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste des Participants : "+initiale);
							System.exit(0);
						}
					}
					
					final HashSet<Participant> listeParticipantsFromHTML = SiteDoris.getListeParticipantsParInitialeFromHtml(contenuFichierHtml);
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
				
				HashSet<Participant> listeParticipants = new HashSet<Participant>(0);
				listeParticipants.addAll(dbContext.participantDao.queryForAll());
				log.debug("doMain() - listeParticipants.size : "+listeParticipants.size());
				
				//Pas la peine de Récupérer la page de chacun des intervenants
				// Toutes les infos sont dans les listes ci dessus

				
				// - - - Glossaire - - -
				// On boucle sur les initiales des définitions (Cf site : doris.ffessm.fr/glossaire.asp?filtre=?)
				// On récupère la liste des termes dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
				if (nbMaxFichesTraitees == 9999){
					listeFiltres="abcdefghijklmnopqrstuvwxyz";
				} else {
					listeFiltres="ab";
				}
				
				for (char initiale : listeFiltres.toCharArray()){
					log.debug("doMain() - Recup Page de définitions pour la lettre : "+initiale);
					
					int numero = 1;
					boolean continuer;
					do {
						String listeDefinitionsFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeDefinitions-"+initiale+"-"+numero+".html";
						log.info("Récup. Liste des Définitions : " + listeDefinitionsFichier);
						
						if (! action.equals("NODWNLD")){
							if (Outils.getFichierFromUrl(Constants.getListeDefinitionsUrl(""+initiale,""+numero ), listeDefinitionsFichier)) {
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeDefinitionsFichier));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste des Définitions : "+initiale+"-"+numero);
								System.exit(0);
							}
						} else {
							// NODWNLD
							listeDefinitionsFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/listeDefinitions-"+initiale+"-"+numero+".html";
							if (new File(listeDefinitionsFichier).exists()) {
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeDefinitionsFichier));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste des Définitions : "+initiale+"-"+numero);
								System.exit(0);
							}
						}
						
						continuer = SiteDoris.getContinuerListeDefinitionsParInitialeFromHtml(contenuFichierHtml);
						
						final HashSet<DefinitionGlossaire> listeDefinitionsFromHTML = SiteDoris.getListeDefinitionsParInitialeFromHtml(contenuFichierHtml);
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
					String fichierLocalDefinition = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/definition-"+definition.getNumeroDoris()+".html";
					String fichierRefDefinition = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/definition-"+definition.getNumeroDoris()+".html";
					if ( action.equals("INIT") ) {
						if (Outils.getFichierFromUrl(urlDefinition, fichierLocalDefinition)) {
							nbDefinitionTelechargees += 1;
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalDefinition));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la définition : "+urlDefinition);
							continue;
						}
					} else if ( action.equals("UPDATE") || action.equals("CDDVD") ) {
						if ( ! new File(fichierRefDefinition).exists() ) {
							if (Outils.getFichierFromUrl(urlDefinition, fichierLocalDefinition)) {
								nbDefinitionTelechargees += 1;
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalDefinition));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la définition : "+urlDefinition);
								continue;
							}
						} else {
							if (new File(fichierRefDefinition).exists()) {
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierRefDefinition));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la définition sur le disque : "+fichierRefDefinition+" a échoué.");
							}
						}
					} else if ( action.equals("NODWNLD") ) {
						if (new File(fichierRefDefinition).exists()) {
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierRefDefinition));
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
				}
			
				
				// - - - Bibliographie - - -
				// On boucle sur la page des Fiches tant que l'on trouve dans la page courante (n)
				//biblio.asp?mapage=(n+1)&PageCourante=n
				// On récupère les Bibliographies dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
				int pageCourante = 1;
				boolean testContinu = false;
				
				do {
					log.debug("doMain() - pageCourante Bibliographie : "+pageCourante);
					
					String listeBibliographies = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeBibliographies-"+pageCourante+".html";
					log.info("Récup. Liste des Bibliographies : " + listeBibliographies);
					
					if (! action.equals("NODWNLD")){
						if (Outils.getFichierFromUrl(Constants.getListeBibliographiesUrl(pageCourante), listeBibliographies)) {
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeBibliographies));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste des Bibliographies : " + listeBibliographies);
							System.exit(0);
						}
					} else {
						// NODWNLD
						listeBibliographies = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/listeBibliographies-"+pageCourante+".html";
						if (new File(listeBibliographies).exists()) {
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeBibliographies));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste des Bibliographies : " + listeBibliographies);
							System.exit(0);
						}
					}
					
					final HashSet<EntreeBibliographie> listeBiblioFromHTML = SiteDoris.getListeBiblioFromHtml(contenuFichierHtml);
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
				} while ( testContinu );
				/* TODO : Il y a souvent une illustration dans la page de l'entrée bibliographique
				 * La façon de faire ressemblerait bcp à la façon de faire des définitions
				HashSet<Participant> listeParticipants = new HashSet<Participant>(0);
				listeParticipants.addAll(dbContext.participantDao.queryForAll());
				log.debug("doMain() - listeParticipants.size : "+listeParticipants.size());
				*/
				//Pas la peine de Récupérer la page de chacun des intervenants
				// Toutes les infos sont dans les listes ci dessus
				
				
				// - - - Liste des Fiches - - -
				// Récupération de la liste des fiches sur le site de DORIS
				// Elles sont récupérées dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
				String listeFichesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeFiches.html";
				log.info("Récup. Liste Fiches Doris : " + listeFichesFichier);
				
				if (! action.equals("NODWNLD")){
					if (Outils.getFichierFromUrl(Constants.getListeFichesUrl(), listeFichesFichier)) {
						contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeFichesFichier));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(0);
					}
				}
				HashSet<Fiche> listeFichesSite = SiteDoris.getListeFichesFromHtml(contenuFichierHtml);
				log.info("Nb Fiches sur le site : "+listeFichesSite.size());

				// Récupération de la liste des fiches dans le dossier de référence
				// Si NODWNLD la liste sera utilisée pour faire le traitement
				// Si UPDATED ou CDDVD, elle permettra de déduire les fiches à télécharger de nouveau : les fiches ayant changées de statut

				if (! action.equals("INIT")){
					listeFichesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/listeFiches.html";
					if (new File(listeFichesFichier).exists()) {
						contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeFichesFichier));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(0);
					}
				}
				HashSet<Fiche> listFichesFromRef = SiteDoris.getListeFichesFromHtml(contenuFichierHtml);;
				log.info("Nb Fiches dans le dossier de référence : "+listFichesFromRef.size());
				
				// Création de l'entête des fiches
				final HashSet<Fiche> listeFichesTravail;
				if (! action.equals("NODWNLD")) {
					listeFichesTravail = (HashSet<Fiche>) listeFichesSite.clone();
				} else {
					listeFichesTravail = (HashSet<Fiche>) listFichesFromRef.clone();
				}
				TransactionManager.callInTransaction(connectionSource,
					new Callable<Void>() {
						public Void call() throws Exception {
							for (Fiche fiche : listeFichesTravail){
								dbContext.ficheDao.create(fiche);
							}
							return null;
					    }
					});

				// - - - Fiche - - -
				// Pour chaque fiche, on télécharge la page (si nécessaire) puis on la traite
				log.info("Mise à jours de "+listeFichesSite.size()+" fiches.");
				HashSet<Fiche> listFichesModif = null;
				if ( action.equals("UPDATE") || action.equals("CDDVD") ) {
					listFichesModif = SiteDoris.getListeFichesUpdated(listFichesFromRef, listeFichesSite);
				}
				
				int nbFichesTraitees = 0;
				for (Fiche fiche : listeFichesSite) {
					if (  nbFichesTraitees <= nbMaxFichesTraitees ) {
						log.debug("doMain() - Traitement Fiche : "+fiche.getNomCommun());
						
						String urlFiche =  Constants.getFicheFromIdUrl( fiche.getNumeroFiche() );
						String fichierLocalFiche = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/fiche-"+fiche.getNumeroFiche()+".html";
						String fichierRefFiche = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/fiche-"+fiche.getNumeroFiche()+".html";
						
						if ( action.equals("INIT") ) {
							if (Outils.getFichierFromUrl(urlFiche, fichierLocalFiche)) {
								nbFichesTraitees += 1;
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalFiche));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
								// Solution de contournement désespérée 
								urlFiche = Constants.getFicheFromNomCommunUrl(fiche.getNomCommun());
								log.error("=> Tentative sur : "+urlFiche);
								if (Outils.getFichierFromUrl(urlFiche, fichierLocalFiche)) {
									nbFichesTraitees += 1;
									contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalFiche));
								} else {
									log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
									continue;
								}
							}
						} else if ( action.equals("UPDATE") || action.equals("CDDVD") ) {
							if (new File(fichierRefFiche).exists() && !listFichesModif.contains(fiche)) {
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierRefFiche));
								nbFichesTraitees += 1;
							} else {
								if (Outils.getFichierFromUrl(urlFiche, fichierLocalFiche)) {
									nbFichesTraitees += 1;
									contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalFiche));
								} else {
									log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
									// Solution de contournement désespérée 
									urlFiche = Constants.getFicheFromNomCommunUrl(fiche.getNomCommun());
									log.error("=> Tentative sur : "+urlFiche);
									if (Outils.getFichierFromUrl(urlFiche, fichierLocalFiche)) {
										nbFichesTraitees += 1;
										contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalFiche));
									} else {
										log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
										continue;
									}
								}
							}
						} else if ( action.equals("NODWNLD") ) {
							if (new File(fichierRefFiche).exists()) {
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierRefFiche));
								nbFichesTraitees += 1;
							} else {
								log.error("La récupération de la fiche sur le disque : "+fichierRefFiche+" a échoué.");
							}
						}
						
						fiche.setContextDB(dbContext);
						fiche.getFicheFromHtml(contenuFichierHtml, listeGroupes, listeParticipants);
						dbContext.ficheDao.update(fiche);
						
						// mise à jour des champs inverse
						//dbContext.ficheDao.refresh(fiche);
						
						log.info("doMain() - Info Fiche {");
						log.info("doMain() -      - ref : "+fiche.getNumeroFiche());
						log.info("doMain() -      - nom : "+fiche.getNomCommun());
						log.info("doMain() -      - etat : "+fiche.getEtatFiche());
						log.info("doMain() - }");
						
						String urlListePhotos = "http://doris.ffessm.fr/fiche_photo_liste_apercu.asp?fiche_numero="+fiche.getNumeroFiche();
						String fichierLocalListePhotos = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/fiche-"+fiche.getNumeroFiche()+"_listePhotos.html";
						String fichierRefListePhotos = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/fiche-"+fiche.getNumeroFiche()+"_listePhotos.html";
						String contenuFichierHtmlListePhotos = null;
						if ( action.equals("INIT")) {
							if (Outils.getFichierFromUrl(urlListePhotos, fichierLocalListePhotos)) {
								contenuFichierHtmlListePhotos = Outils.getFichierTxtFromDisk(new File(fichierLocalListePhotos));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
								continue;
							}
						} else if ( action.equals("UPDATE") || action.equals("CDDVD") ) {
							if (new File(fichierRefListePhotos).exists() && !listFichesModif.contains(fiche)) {
								contenuFichierHtmlListePhotos = Outils.getFichierTxtFromDisk(new File(fichierRefListePhotos));
							} else {
								if (Outils.getFichierFromUrl(urlListePhotos, fichierLocalListePhotos)) {
									contenuFichierHtmlListePhotos = Outils.getFichierTxtFromDisk(new File(fichierLocalListePhotos));
								
								} else {
									log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
									continue;
								}
							}
						} else if ( action.equals("NODWNLD") ){
							if (new File(fichierRefListePhotos).exists()) {
								contenuFichierHtmlListePhotos = Outils.getFichierTxtFromDisk(new File(fichierRefListePhotos));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
							}
						}
						if(contenuFichierHtmlListePhotos != null){
							
							final List<PhotoFiche> listePhotoFiche = SiteDoris.getListePhotosFicheFromHtml(fiche, contenuFichierHtmlListePhotos);
							
							// Maj Base de données
							final Fiche ficheMaj = fiche;
							TransactionManager.callInTransaction(connectionSource,
								new Callable<Void>() {
									public Void call() throws Exception {
										for (PhotoFiche photoFiche : listePhotoFiche){
											photoFiche.setFiche(ficheMaj);
											dbContext.photoFicheDao.create(photoFiche);
											
											if (photoFiche.estPhotoPrincipale) {
												ficheMaj.setPhotoPrincipale(photoFiche);
												dbContext.ficheDao.update(ficheMaj);
											}
										}
										return null;
								    }
								});
							
							// Téléchargement Photos
							if ( action.equals("CDDVD") ) {
								String fichierImageRacine = DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/";
								String fichierImageRefRacine = DOSSIER_RACINE + "/" + DOSSIER_IMAGES_REF + "/";

								for (PhotoFiche photoFiche : listePhotoFiche){
	
									if ( !photoFiche.getCleURL().isEmpty() ) {
										// Vignettes
										if( ! isFileExistingPath( fichierImageRefRacine+SOUSDOSSIER_VIGNETTES+photoFiche.getCleURL() ) ){
											if (Outils.getFichierFromUrl(Constants.VIGNETTE_BASE_URL+photoFiche.getCleURL(), fichierImageRacine+SOUSDOSSIER_VIGNETTES+photoFiche.getCleURL())) {
											} else {
												log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
												//System.exit(0);
											}
										}
										// Qualité Intermédiaire
										if( ! isFileExistingPath( fichierImageRefRacine+SOUSDOSSIER_MED_RES+photoFiche.getCleURL() ) ){
											if (Outils.getFichierFromUrl(Constants.MOYENNE_BASE_URL+photoFiche.getCleURL(), fichierImageRacine+SOUSDOSSIER_MED_RES+photoFiche.getCleURL())) {
											} else {
												log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
												//System.exit(0);
											}
										}
										// Haute Qualité 
										if( ! isFileExistingPath( fichierImageRefRacine+SOUSDOSSIER_HI_RES+photoFiche.getCleURL() ) ){
											if (Outils.getFichierFromUrl(Constants.GRANDE_BASE_URL+photoFiche.getCleURL(), fichierImageRacine+SOUSDOSSIER_HI_RES+photoFiche.getCleURL())) {
											} else {
												log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
												//System.exit(0);
											}
										}
									}
								}
							}
						}
						
					}
					else {
						log.info("Nombre max de fiches à traiter atteint.");
						break; // ignore les fiches suivantes
					}
					
					if ( nbFichesTraitees != 0 && (nbFichesTraitees % 50) == 0) {
						log.info("fiche traitées = "+nbFichesTraitees+", pause de 1s...");
						Thread.sleep(1000);
					}
				}
				
				
				// mise à jour des zones géographiques
				// zone France Métropolitaine Marines
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE);
				// zone France Métropolitaine Eau douce
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE);
				// zone indo pacifique
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE);
				// zone Caraïbes
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES);
				// zone atlantique nordOuest
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST);
			
				Date date = new Date();
				SimpleDateFormat ft =  new SimpleDateFormat ("dd/MM/yyyy  HH:mm");
				dbContext.dorisDB_metadataDao.create(new DorisDB_metadata(ft.format(date),""));
				
				
				// Téléchargement Pages et Images exclusives CD et DVD
				if ( action.equals("CDDVD") ) {
					String fichierLocalLien = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/";
					String fichierRefLien = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/";
					String fichierIconeRacine = DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_ICONES + "/";
					String fichierIconeRefRacine = DOSSIER_RACINE + "/" + DOSSIER_IMAGES_REF + "/" + SOUSDOSSIER_ICONES + "/";

					List<Lien> liensATelecharger = getLienATelecharger();
					for (Lien lienATelecharger : liensATelecharger) {
						
						if (lienATelecharger.getLienKind() == LienKind.PAGE) {
							if( ! isFileExistingPath( fichierRefLien+lienATelecharger.getFichier() ) ){
								if (Outils.getFichierFromUrl(Constants.getSiteUrl()+lienATelecharger.getUrl(),
										fichierLocalLien+lienATelecharger.getFichier() ) ) {
								} else {
									log.error("Une erreur est survenue lors de la récupération du lien : "+lienATelecharger.getUrl() );
									System.exit(0);
								}
							}
						}
						
						if (lienATelecharger.getLienKind() == LienKind.ICONE) {
							if( ! isFileExistingPath( fichierIconeRefRacine+lienATelecharger.getFichier() ) ){
								if (Outils.getFichierFromUrl(Constants.getSiteUrl()+lienATelecharger.getUrl(),
										fichierIconeRacine+lienATelecharger.getFichier() ) ) {
								} else {
									log.error("Une erreur est survenue lors de la récupération du lien : "+lienATelecharger.getUrl() );
									System.exit(0);
								}
							}
						}
					}
					
				}
				
				// Création du dossier CD et DVD
				if ( action.equals("CDDVD") ) {
					creationCD();
					transfoHtml();
				}
				
			} finally {
				// destroy the data source which should close underlying connections
				if (connectionSource != null) {
					connectionSource.close();
				}
			}
			
			
		} // Fin de <> TEST
		log.debug("doMain() - Fin");
	}

	private void majZoneGeographique(ConnectionSource connectionSource, ZoneGeographiqueKind zoneKing){
		log.debug("majZoneGeographique() - Début");
		
		String listeFichesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeFiches-"+(zoneKing.ordinal()+1)+".html";
		log.debug("Récup. Liste Fiches Doris Zone : " + listeFichesFichier);
		//List<Fiche> listeFiches = new ArrayList<Fiche>(0);
		String contenuFichierHtml = "";
		if (! action.equals("NODWNLD")){
			if (Outils.getFichierFromUrl(Constants.getListeFichesUrl(zoneKing), listeFichesFichier)) {
				contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeFichesFichier));
				
			} else {
				log.error("Une erreur est survenue lors de la récupération de la liste des fiches de la zone ");
				return;
			}
		} else {
			// NODWNLD
			listeFichesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML_REF + "/listeFiches-"+(zoneKing.ordinal()+1)+".html";
			if (new File(listeFichesFichier).exists()) {
				contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeFichesFichier));
			} else {
				log.error("Une erreur est survenue lors de la récupération de la liste des fiches de la zone ");
				return;
			}
		}
		
		final ZoneGeographique zoneGeographique = new ZoneGeographique(Constants.getTitreZoneGeographique(zoneKing), 
																 Constants.getTexteZoneGeographique(zoneKing));
		try {
			dbContext.zoneGeographiqueDao.create(zoneGeographique);
		} catch (SQLException e) {
			log.error("impossible de créer la zone dans la base", e);
		}
		
		final HashSet<Fiche> listFicheFromHTML = SiteDoris.getListeFichesFromHtml(contenuFichierHtml);
		log.info("Création des "+listFicheFromHTML.size()+" associations pour la Zone : " + listeFichesFichier);
		
		try {
			TransactionManager.callInTransaction(connectionSource,
					new Callable<Void>() {
						public Void call() throws Exception { 

							for (Fiche fiche : listFicheFromHTML) {
								Fiche fichesDeLaBase = queryFicheByNumeroFiche(fiche.getNumeroFiche());
								fichesDeLaBase.setContextDB(dbContext);
								fichesDeLaBase.addZoneGeographique(zoneGeographique);
							}
							
							return null;
					    }
					});

		} catch (SQLException e) {
			log.error("impossible d'associer Fiches et Zone Géographique", e);
		}
		log.debug("majZoneGeographique() - Fin");
	}
	
	
	/**
	 * Vérification des arguments passés à l'application
	 * 
	 *  @param args
	 */
	private String checkArgs(String[] inArgs){
			
		// Si Aucun Argument, on affiche l'aide et on termine
		log.debug("checkArgs() - nb args : " + inArgs.length);
		if (inArgs.length < 1) {
			help();
			log.error("Le programme ne peut être lancé sans arguments.");
			System.exit(0);
		}
		
		// On commence par regarder si un des paramètres est un paramétre optionnel prioritaire
		// verbose, debug ou silence
		log.debug("checkArgs() - debug, verbose ou silence ? ");
		for (String arg : inArgs) {
			log.debug("checkArgs() - arg : " + arg);
			
			if ( arg.equals("-d") || arg.equals("--debug") ) {
				org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
		    	logger.setLevel(Level.DEBUG);
			}
			if ( arg.equals("-v") || arg.equals("--verbose")) {
				org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
		    	logger.setLevel(Level.INFO);
			}
			if ( arg.equals("-s") || arg.equals("--silence")) {
				org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
		    	logger.setLevel(Level.OFF);
			}
		}
		
		// Si Aide ou Version alors affichage puis on termine
		log.debug("checkArgs() - help ou version ? ");
		for (String arg : inArgs) {
			
			if ( arg.equals("-h") || arg.equals("--help")) {
				log.debug("checkArgs() - arg : " + arg);
				help();
				System.exit(0);
			}
		}
		
		// paramètre qui permet de limiter le nombre de fiches à traiter
		log.debug("checkArgs() - max ? ");
		for (String arg : inArgs) {
			if ( arg.startsWith("-M") || arg.startsWith("--max=")) {
				log.debug("checkArgs() - arg : " + arg);
				String nbFichesStr = null;
				if ( arg.startsWith("-M")) {
					nbFichesStr =  arg.substring(3);
				}
				if ( arg.startsWith("--max=")) {
					nbFichesStr =  arg.substring(6);
				}
				try { 
					 nbMaxFichesTraitees = Integer.parseInt(nbFichesStr);
					 log.debug("checkArgs() - nbMaxFichesTraitees : " + nbMaxFichesTraitees);
			    } catch(NumberFormatException e) { 
			    	help();
					log.error("Argument -M ou --max mal utilisé : " + arg);
			    	System.exit(0);
			    }
			}
		}

		
		// Vérification que le dernier argument est une des actions prévues
		String action = "";
		log.debug("checkArgs() - argument action");
		for (String arg : inArgs) {
			if (arg.equals("INIT")) {
				action = arg;
			} else if (arg.startsWith("NODWNLD")) {
				action = "NODWNLD";
			} else if (arg.equals("NEWFICHES")) {
				action = arg;
			} else if (arg.equals("UPDATE")) {
				action = arg;
			} else if (arg.equals("CDDVD")) {
				action = arg;
			} else if (arg.equals("TEST")) {
				action = arg;
			}
		}
		if (action == "") {
			help();
			String listeArgs = "";
			for (String arg : inArgs) {
				listeArgs += arg + " ";
			}
			log.error("arguments : " + listeArgs);
			log.error("Action non prévue");
			System.exit(0);
		}
		return action;

	}
	
	private void initializeSQLite(String url) throws ClassNotFoundException, SQLException{
		
		Class.forName("org.sqlite.JDBC");
		Connection c = DriverManager.getConnection(url);
		log.debug("Opened database successfully");
		
		if (! action.equals("CDDVD") ) {
			Statement  stmt = c.createStatement();
			String sql = "CREATE TABLE \"android_metadata\" (\"locale\" TEXT DEFAULT 'en_US')"; 
			stmt.executeUpdate(sql);
			stmt.close();
			
			stmt = c.createStatement();
			sql = "    INSERT INTO \"android_metadata\" VALUES ('en_US')"; 
			stmt.executeUpdate(sql);
			stmt.close();
			c.close();
		}
	}
	
	/**
	 * Setup our database and DAOs
	 */
	private void setupDatabase(ConnectionSource connectionSource)
			throws Exception {
		log.debug("setupDatabase() - Début");
		
		dbContext = new DorisDBHelper();
		dbContext.ficheDao = DaoManager.createDao(connectionSource, Fiche.class);
		dbContext.groupeDao = DaoManager.createDao(connectionSource, Groupe.class);
		
		dbContext.photoFicheDao = DaoManager.createDao(connectionSource, PhotoFiche.class);
		dbContext.participantDao = DaoManager.createDao(connectionSource, Participant.class);
		dbContext.intervenantFicheDao = DaoManager.createDao(connectionSource, IntervenantFiche.class);
		dbContext.zoneGeographiqueDao = DaoManager.createDao(connectionSource, ZoneGeographique.class);
		dbContext.zoneObservationDao = DaoManager.createDao(connectionSource, ZoneObservation.class);
		dbContext.sectionFicheDao = DaoManager.createDao(connectionSource, SectionFiche.class);
		dbContext.autreDenominationDao = DaoManager.createDao(connectionSource, AutreDenomination.class);
		dbContext.definitionGlossaireDao = DaoManager.createDao(connectionSource, DefinitionGlossaire.class);
		dbContext.entreeBibliographieDao = DaoManager.createDao(connectionSource, EntreeBibliographie.class);
		
		//dbContext.fiches_verificateurs_ParticipantsDao = DaoManager.createDao(connectionSource, Fiches_verificateurs_Participants.class);
		dbContext.fiches_ZonesGeographiquesDao = DaoManager.createDao(connectionSource, Fiches_ZonesGeographiques.class);
		dbContext.fiches_DefinitionsGlossaireDao = DaoManager.createDao(connectionSource, Fiches_DefinitionsGlossaire.class);
		//dbContext.fiches_ZonesObservationsDao = DaoManager.createDao(connectionSource, Fiches_ZonesObservations.class);
		dbContext.dorisDB_metadataDao = DaoManager.createDao(connectionSource, DorisDB_metadata.class);
	}
		
	/**
	 * Création des Tables
	 */
	private void databaseInitialisation(ConnectionSource connectionSource)
			throws Exception {
		log.debug("databaseInitialisation() - Début");	
		
		// if you need to create the table
		TableUtils.createTable(connectionSource, Fiche.class);
		TableUtils.createTable(connectionSource, Groupe.class);
		TableUtils.createTable(connectionSource, Participant.class);
		TableUtils.createTable(connectionSource, IntervenantFiche.class);
		TableUtils.createTable(connectionSource, PhotoFiche.class);
		TableUtils.createTable(connectionSource, ZoneGeographique.class);
		TableUtils.createTable(connectionSource, ZoneObservation.class);
		TableUtils.createTable(connectionSource, SectionFiche.class);
		TableUtils.createTable(connectionSource, AutreDenomination.class);
		TableUtils.createTable(connectionSource, DefinitionGlossaire.class);
		TableUtils.createTable(connectionSource, Fiches_ZonesGeographiques.class);
		TableUtils.createTable(connectionSource, Fiches_ZonesObservations.class);
		TableUtils.createTable(connectionSource, Fiches_DefinitionsGlossaire.class);
		TableUtils.createTable(connectionSource, EntreeBibliographie.class);
		TableUtils.createTable(connectionSource, DorisDB_metadata.class);
		
		
		// TODO : Reprendre par Didier plus proprement
		// CREATE INDEX fiches_ZoneGeographiques_Id ON fiches_ZonesGeographiques(ZoneGeographique_id ASC);
		// CREATE INDEX photoFiche_I_ficheId ON photoFiche(fiche_id ASC);
		
		DatabaseConnection connection = connectionSource.getReadWriteConnection();
		List<String> statements = new ArrayList<String>();
		final FieldType[] noFieldTypes = new FieldType[0];
		Boolean ignoreErrors = false;
		Boolean returnsNegative  = false;
		Boolean expectingZero  = false;
		statements.add("CREATE INDEX fiches_ZoneGeographiques_I_id ON fiches_ZonesGeographiques(ZoneGeographique_id ASC)");
		statements.add("CREATE INDEX photoFiche_I_ficheId ON photoFiche(fiche_id ASC)");
		

		for (String statement : statements) {
			int rowC = 0;
			CompiledStatement compiledStmt = null;
			try {
				compiledStmt = connection.compileStatement(statement, StatementType.EXECUTE, noFieldTypes);
				rowC = compiledStmt.runExecute();
				log.info("executed {} table statement changed {} rows: {}" + rowC + " - " + statement);

			} catch (SQLException e) {
				if (ignoreErrors) {
					log.info("ignoring {} error '{}' for statement: {}" + rowC + " - " + statement);
				} else {
					throw SqlExceptionUtil.create("SQL statement failed: " + statement, e);
				}
			} finally {
				if (compiledStmt != null) {
					compiledStmt.close();
				}
			}
			// sanity check
			if (rowC < 0) {
				if (!returnsNegative) {
					throw new SQLException("SQL statement " + statement + " updated " + rowC
							+ " rows, we were expecting >= 0");
				}
			} else if (rowC > 0 && expectingZero) {
				throw new SQLException("SQL statement updated " + rowC + " rows, we were expecting == 0: " + statement);
			}
		}
		// Fin Création index
		
		log.debug("databaseInitialisation() - Fin");
	}


	/**
     * Afficher l'aide de l'application
     **/	
	private static void help(){
		log.debug("help() - Début");
		
		System.out.println("Récupération de la base de fiches pour DorisAndroid");
		System.out.println("Usage: java -jar PrefetchDorisWebSite.jar [OPTIONS] [ACTION]");
		System.out.println("");
		System.out.println("OPTIONS :");
		System.out.println("  -M, --max=K		on limite le travail au K 1ères fiches (utile en dev.)");
		System.out.println("  -h, --help        afficher cette aide");
		System.out.println("  -d, --debug       messages detinés aux développeurs de cette application");
		System.out.println("  -v, --verbose     messages permettant de suivre l'avancé des traitements");
		System.out.println("  -s, --silence     aucune sortie, même pas les erreurs");
		System.out.println("");
		System.out.println("ACTION :");
		System.out.println("  INIT               Toutes les fiches sont retéléchargées sur doris.ffessm.fr et retraitées pour créer la base (images comprises)");
		System.out.println("  NODWNLD dossier    Pas de téléchargement, travail sur un dossier de référznce (utile en dév.)");
		System.out.println("  NEWFICHES    TODO: Ne télécharge que les nouvelles fiches");
		System.out.println("  UPDATE       TODO: En plus des nouvelles fiches, définitions et intervenants, on retélécharge les fiches qui ont changées de statut");
		System.out.println("  CDDVD          	 Comme UPDATE + Permet de télécharger les photos manquantes et de créer un dossier de la taille d'un CD (images en qualité inter.) et un de la taille d'un DVD (images en qualité max.)");
		System.out.println("  TEST          	 Pour les développeurs");
		
		log.debug("help() - Fin");
	}
	
	/**
	 * Création, Sauvegarde des dossiers de travail de l'application
	 * Selon l'action choisie
	 * 
	 * @param action
	 */
	public void checkDossiers(String inAction) {
		log.debug("checkDossiers() - Début");
		
		log.debug("checkDossiers() - Action : " + inAction);
		
		log.debug("checkDossiers() - Dossier de base : " + DOSSIER_RACINE);
		log.debug("checkDossiers() - Dossier html : " + DOSSIER_HTML);
		log.debug("checkDossiers() - Fichier de la Base : " + DATABASE_URL);
		
		// Si le dossier principal de travail n'existe pas, on le créé
		File dossierBase = new File(DOSSIER_RACINE);
		if (dossierBase.mkdirs()) {
			log.info("Création du dossier : " + dossierBase.getAbsolutePath());
		} 

		// Si les dossiers download (html et img) et résultats existent déjà, ils sont renommés
		// avant d'être recréé vide
		Date maintenant = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
		String suffixe = sdf.format(maintenant);
		
		// Le dossier des fichiers html téléchargés
		if(inAction.equals("INIT") || inAction.equals("UPDATE")  || inAction.equals("CDDVD") ){
			File dossierHtml = new File(DOSSIER_RACINE + "/" + DOSSIER_HTML);
			if (dossierHtml.exists()){
				File dossierHtmlNew = new File(DOSSIER_RACINE + "/" + DOSSIER_HTML +"_"+ suffixe);
				if(dossierHtml.renameTo(dossierHtmlNew)){
					log.info("Sauvegarde du dossier download : " + dossierHtmlNew.getAbsolutePath());
				}else{
					log.error("Echec renommage du dossier download : " + dossierHtml.getAbsolutePath());
					System.exit(0);
				}
			}
			if (dossierHtml.mkdir()) {
				log.info("Création du dossier download : " + dossierHtml.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier download : " + dossierHtml.getAbsolutePath());
				System.exit(0);
			}
		}
		
		// Le dossier des images téléchargées
		if( inAction.equals("CDDVD") ){
			File dossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES);
			if (dossierImages.exists()){
				File dossierImagesNew = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES +"_"+ suffixe);
				if(dossierImages.renameTo(dossierImagesNew)){
					log.info("Sauvegarde du dossier download : " + dossierImagesNew.getAbsolutePath());
				}else{
					log.error("Echec renommage du dossier download : " + dossierImagesNew.getAbsolutePath());
					System.exit(0);
				}
			}
			if (dossierImages.mkdir()) {
				log.info("Création du dossier download : " + dossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier download : " + dossierImages.getAbsolutePath());
				System.exit(0);
			}
			File sousDossierImages;
			sousDossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_ICONES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du sous-dossier download : " + sousDossierImages.getAbsolutePath());
				System.exit(0);
			}
			sousDossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_VIGNETTES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(0);
			}
			sousDossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_MED_RES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(0);
			}
			sousDossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_HI_RES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(0);
			}
		}
		
		// Le dossier du CD ou DVD
		if( inAction.equals("CDDVD") ){
			File dossierCD = new File(DOSSIER_RACINE + "/" + DOSSIER_CD);
			if (dossierCD.exists()){
				File dossierCDNew = new File(DOSSIER_RACINE + "/" + DOSSIER_CD +"_"+ suffixe);
				if(dossierCD.renameTo(dossierCDNew)){
					log.info("Sauvegarde du dossier CD : " + dossierCDNew.getAbsolutePath());
				}else{
					log.error("Echec renommage du dossier CD : " + dossierCDNew.getAbsolutePath());
					System.exit(0);
				}
			}
			if (dossierCD.mkdir()) {
				log.info("Création du dossier CD : " + dossierCD.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier CD : " + dossierCD.getAbsolutePath());
				System.exit(0);
			}
			File dossierDVD = new File(DOSSIER_RACINE + "/" + DOSSIER_DVD);
			if (dossierDVD.exists()){
				File dossierDVDNew = new File(DOSSIER_RACINE + "/" + DOSSIER_DVD +"_"+ suffixe);
				if(dossierDVD.renameTo(dossierDVDNew)){
					log.info("Sauvegarde du dossier DVD : " + dossierDVDNew.getAbsolutePath());
				}else{
					log.error("Echec renommage du dossier DVD : " + dossierDVDNew.getAbsolutePath());
					System.exit(0);
				}
			}
			if (dossierDVD.mkdir()) {
				log.info("Création du dossier DVD : " + dossierDVD.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier DVD : " + dossierDVD.getAbsolutePath());
				System.exit(0);
			}
		}
		
		//	Vérification que le dossier Html Référence existe
		if(inAction.equals("NODWNLD") || inAction.equals("UPDATE")  || inAction.equals("CDDVD") ){
			final File dossierReference = new File(DOSSIER_RACINE + "/" + DOSSIER_HTML_REF);
			if (!dossierReference.exists()){
				log.error("Le dossier Référence : " + dossierReference.getAbsolutePath() + "n'a pas été créé.");
				System.exit(0);
			} else {
				if (!dossierReference.isDirectory()){
					log.error("Le dossier Référence : " + dossierReference.getAbsolutePath() + "n'a pas été créé.");
					System.exit(0);
				}
			}
		}
		
		//	Vérification que le dossier Html Référence existe
		if(inAction.equals("CDDVD") ){
			File dossierReference = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES_REF);
			if (!dossierReference.exists()){
				log.error("Le dossier Référence des images : " + dossierReference.getAbsolutePath() + " n'a pas été créé.");
				System.exit(0);
			} else {
				if (!dossierReference.isDirectory()){
					log.error("Le dossier Référence des images : " + dossierReference.getAbsolutePath() + " n'a pas été créé.");
					System.exit(0);
				}
			}
			File sousDossierImages;
			sousDossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_ICONES);
			if (!sousDossierImages.exists()){
				log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
				System.exit(0);
			} else {
				if (!sousDossierImages.isDirectory()){
					log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
					System.exit(0);
				}
			}
			sousDossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_VIGNETTES);
			if (!sousDossierImages.exists()){
				log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
				System.exit(0);
			} else {
				if (!sousDossierImages.isDirectory()){
					log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
					System.exit(0);
				}
			}
			sousDossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_MED_RES);
			if (!sousDossierImages.exists()){
				log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
				System.exit(0);
			} else {
				if (!sousDossierImages.isDirectory()){
					log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
					System.exit(0);
				}
			}
			sousDossierImages = new File(DOSSIER_RACINE + "/" + DOSSIER_IMAGES + "/" + SOUSDOSSIER_HI_RES);
			if (!sousDossierImages.exists()){
				log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
				System.exit(0);
			} else {
				if (!sousDossierImages.isDirectory()){
					log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
					System.exit(0);
				}
			}
		}
	
		// Le fichier de la base de données
		String dataBaseName = DATABASE_URL.substring(DATABASE_URL.lastIndexOf(":")+1, DATABASE_URL.lastIndexOf(".") );
		log.debug("dataBaseName : " + dataBaseName);
		File fichierDB= new File(dataBaseName+".db");
		if (fichierDB.exists()){
			File fichierDBNew = new File(dataBaseName+"_"+suffixe+".db");
			if(fichierDB.renameTo(fichierDBNew)){
				log.info("Sauvegarde du fichier de la base : " + fichierDB.getAbsolutePath());
			}else{
				log.error("Echec renommage du fichier de la base en : " + fichierDBNew.getAbsolutePath());
				System.exit(0);
			}

		}
		
		log.debug("checkDossiers() - Fin");
	}

	// Retrouve la fiche dans la base
	public Fiche queryFicheByNumeroFiche(int inNumeroFiche) {
		try {
			Fiche queryFiche = new Fiche();
			queryFiche.setNumeroFiche(inNumeroFiche);
			List<Fiche> fichesDeLaBase = dbContext.ficheDao.queryForMatching(queryFiche);
			if(fichesDeLaBase.size() != 1){
				log.debug("La fiche n°"+queryFiche.getNumeroFiche()+ " n'existe pas dans la base");
				return null;
			}
			return fichesDeLaBase.get(0);
		} catch (SQLException e) {
			log.error("erreur pendant la requete sur la fiche "+inNumeroFiche+ " dans la base", e);
		}
		return null;
	}
	
	// Vérifie que le fichier existe
	public boolean isFileExistingPath(String fichierPath){
		File fichier = new File(fichierPath);
		if (!fichier.exists()){
			return false;
		} else {
			if (fichier.isDirectory()){
				return false;
			}
		}
		return true;
	}
	
	// Liste des Pages et Images à télécharger dans le cas des CD et DVD
	// REPLACE
	public List<Lien> getLienATelecharger(){
		List<Lien> lienATelecharger = new ArrayList<Lien>(0);

		lienATelecharger.add(new Lien(LienKind.PAGE, "accueil.asp","accueil.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "styles.css","styles.css"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris.asp","doris.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_faq.asp","doris_faq.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "contacts_accueil.asp","contacts_accueil.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "Copyright.asp","Copyright.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "liens.asp","liens.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "formulaire_contact.asp","formulaire_contact.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_doridiens.asp","doris_doridiens.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_genese_objectifs.asp","doris_genese_objectifs.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_chroniques_doridiennes.asp","doris_genese_objectifs.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_equipe.asp","doris_equipe.html"));

		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=10","fichier_10.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=1","fichier_1.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=2","fichier_2.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=3","fichier_3.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=4","fichier_4.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=5","fichier_5.html"));

		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=10","groupes_zone_10.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=1","groupes_zone_1.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=2","groupes_zone_2.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=3","groupes_zone_3.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=4","groupes_zone_4.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=5","groupes_zone_5.html"));
		
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/favicon.ico","images_favicon.ico"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/carre.jpg","images_carre.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/pucecarre.gif","images_pucecarre.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "Images/pucecarre.gif","images_pucecarre.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/pucecarreorange.gif","images_pucecarreorange.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "Images/pucecarreorange.gif","images_pucecarreorange.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fleche_grise.gif","images_fleche_grise.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo.gif","images_logo.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo2.gif","images_logo2.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo-biologie.gif","images_logo-biologie.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo_ffessm.gif","images_logo_ffessm.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo-biologie-pantone.gif","images_logo-biologie-pantone.gif"));

		lienATelecharger.add(new Lien(LienKind.ICONE, "images/ligne_carre3.gif","images_ligne_carre3.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/ligne_carre4.gif","images_ligne_carre4.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/MNHN2.gif","images_MNHN2.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/Palme3.gif","images_Palme3.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/1x1.gif","images_1x1.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/10x10.gif","images_10x10.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/pucemenu.gif","images_pucemenu.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fond_bas.gif","images_fond_bas.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fonbandeau.gif","images_fonbandeau.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/icon_back.gif","images_icon_back.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/petit_gris.gif","images_petit_gris.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/f3f3f3.gif","images_f3f3f3.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/black_round_grey.gif","images_black_round_grey.gif"));		
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/newsearch.gif","images_newsearch.gif"));	
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_imp.gif","images_18_imp.gif"));	
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_fileprint.gif","images_18_fileprint.gif"));	
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_filewrite.gif","images_18_filewrite.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_fileimage.gif","images_18_fileimage.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_filetick.gif","images_18_filetick.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_mailsend.gif","images_18_mailsend.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_probe.gif","images_18_probe.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/searchdoc.gif","images_searchdoc.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/dorispetit18x18.gif","images_dorispetit18x18.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_faq.gif","images_18_faq.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/rightsign.jpg","images_rightsign.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/btn_next.gif","images_btn_next.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/btn_prev.gif","images_btn_prev.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/picto_regl18.gif","images_picto_regl18.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/picto_regl.gif","images_picto_regl.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/picto_dang18.gif","images_picto_dang18.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/picto_dang.gif","images_picto_dang.gif"));
		
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier1puce.jpg","images_fichier1puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier2puce.jpg","images_fichier2puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier3puce.jpg","images_fichier3puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier4puce.jpg","images_fichier4puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier5puce.jpg","images_fichier5puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier10puce.jpg","images_fichier10puce.jpg"));

		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier1.jpg","images_fichier1.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier2.jpg","images_fichier2.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier3.jpg","images_fichier3.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier4.jpg","images_fichier4.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier5.jpg","images_fichier5.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier10.jpg","images_fichier10.jpg"));
		
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier1_gde.jpg","images_fichier1_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier2_gde.jpg","images_fichier2_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier3_gde.jpg","images_fichier3_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier4_gde.jpg","images_fichier4_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier5_gde.jpg","images_fichier5_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier10_gde.jpg","images_fichier10_gde.jpg"));
		return lienATelecharger;
	}
	
	// REPLACE
	public List<Lien> getLienANettoyer(){
		List<Lien> lienANettoyer = new ArrayList<Lien>(0);
		
		// dans les href si commence par url, on replace par le nom du fichier
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=1&","listeFiches-1.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=2&","listeFiches-2.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=3&","listeFiches-3.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=4&","listeFiches-4.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=5&","listeFiches-5.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=10&","listeFiches.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?fichier=",""));

		lienANettoyer.add(new Lien(LienKind.PAGE, "glossaire.asp\"","listeDefinitions-a-1.html\""));
		
		lienANettoyer.add(new Lien(LienKind.PAGE, "biblio.asp\"","listeBibliographies-1.html\""));

		//TODO : Qd des espaces il faudrait les remplacer par des _
		lienANettoyer.add(new Lien(LienKind.VIGNETTE, "http://doris.ffessm.fr/gestionenligne/photos_fiche_vig/","/"));
		lienANettoyer.add(new Lien(LienKind.MED_RES, "http://doris.ffessm.fr/gestionenligne/photos_fiche_moy/","/"));
		lienANettoyer.add(new Lien(LienKind.HI_RES, "http://doris.ffessm.fr/gestionenligne/photos/","/"));
		
		lienANettoyer.add(new Lien(LienKind.ICONE, "gestionenligne/images_groupe/","/images_groupe_"));
		lienANettoyer.add(new Lien(LienKind.ICONE, "gestionenligne/images_sousgroupe/","/images_sousgroupe_"));
				
		lienANettoyer.add(new Lien(LienKind.PAGE, "fichier.asp","accueil.html"));
				
		lienANettoyer.add(new Lien(LienKind.PAGE, "forum_liste.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "fiche_imprime.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "fiches_liste_recherche.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "fiches_liste_proposees.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "fiches_liste_reservees.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "cle_identification0.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "forum_detail.asp","indisponible_CDDVD.html"));
		
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"contacts.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"fiches_liste_recherche.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"formulaire_contact_valid.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"biblio.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"glossaire.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "form.submit();",""));
		
		lienANettoyer.add(new Lien(LienKind.PAGE, "http://doris.ffessm.fr","accueil.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, " http://doris.ffessm.fr","accueil.html"));
		return lienANettoyer;
	}

	//REPLACE_ALL
	public List<Lien> getRegExpPourNettoyer(){
		List<Lien> regExpPourNettoyer = new ArrayList<Lien>(0);
		
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiche.asp\\?[^&\">]*&fiche_numero=([^&]*)&[^\">]*\"","href=\"fiche-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiche2.asp\\?fiche_numero=([^&]*)&[^\">]*\"","href=\"fiche-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiche2.asp\\?fiche_numero=([^\">]*)\"","href=\"fiche-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"\\.\\./fiche2.asp\\?fiche_numero=([^\">]*)\"","href=\"fiche-$1.html\""));
		
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"contacts.asp\\?filtre=(.)","href=\"listeParticipants-$1.html"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"[^\"\\?]*formulaire_contact2.asp\\?contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"contact_fiche.asp\\?temp=0&amp;contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"contact_fiche.asp\\?temp=0&contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"contact_fiche.asp\\?contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiches_liste_photographe.asp\\?contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiches_liste_contact.asp\\?contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));

		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire.asp\\?filtre=(.)[^\">]*","href=\"listeDefinitions-$1-1.html"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire.asp\\?mapage=([^&\">]*)&[^\">]*filtre=(.)\"","href=\"listeDefinitions-$2-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire_detail.asp\\?glossaire_numero=([^&\">]*)&[^\">]*","href=\"definition-$1.html"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire.asp\\?page=Suivant[^\"]*","href=\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire.asp\\?page=Precedent[^\"]*","href=\""));
		
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiches_liste.asp\\?groupe_numero=[^\">]*\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiches_liste.asp\\?sousgroupe_numero=[^\">]*\"","href=\"indisponible_CDDVD.html\""));
				
		regExpPourNettoyer.add(new Lien(LienKind.ICONE, "http://doris.ffessm.fr/gestionenligne/photos_forum_vig/[^\">]*\"","/doris_icone_doris_large.png\""));

		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "fiche_photo_liste_apercu.asp\\?fiche_numero=([^&>]*)&[^\">]*\"","fiche-$1_listePhotos.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "photo_gde_taille_fiche2.asp\\?varpositionf=[^\">]*fiche_numero = ([^&]*)&[^\">]*\"","fiche-$1_listePhotos.html\""));
		
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"biblio_fiche.asp\\?biblio_numero=[^\">]*\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"biblio.asp\\?mapage=([^&>]*)&[^\">]*\"","href=\"listeBibliographies-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"biblio.asp\\?page=Suivant[^\"]*","href=\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"biblio.asp\\?page=Precedent[^\"]*","href=\""));


		return regExpPourNettoyer;
	}
	
	public enum LienKind {
    	PAGE,
    	TEXTE,
    	ICONE,
    	VIGNETTE,
    	MED_RES,
    	HI_RES
    }
	private class Lien {
		LienKind lienKind;
		String url;
		String fichierSurDisque;
		
		Lien(LienKind lienKind, String url, String fichierSurDisque){
			this.lienKind = lienKind;
			this.url = url;
			this.fichierSurDisque = fichierSurDisque;
		}
		public LienKind getLienKind() {
			return lienKind;
		}
		public String getUrl() {
			return url;
		}
		public String getFichier() {
			return fichierSurDisque;
		}
	}
	private void creationCD(){
		log.debug("creationCD() - Début");
		String fichierCDLien = DOSSIER_RACINE + "/" + DOSSIER_CD + "/";
		String fichierRefLien = DOSSIER_RACINE + "/";
		
		// Création Dossiers du CD
		log.info("Création Dossier HTML du CD");
		File dossierCD = new File(fichierCDLien+DOSSIER_HTML);
		File dossierRef = new File(fichierRefLien+DOSSIER_HTML_REF);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dossierRef = new File(fichierRefLien+DOSSIER_HTML);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("Création Dossier Icones du CD");
		dossierCD = new File(fichierCDLien+SOUSDOSSIER_ICONES);
		dossierRef = new File(fichierRefLien+DOSSIER_IMAGES_REF+"/"+SOUSDOSSIER_ICONES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dossierRef = new File(fichierRefLien+DOSSIER_IMAGES+"/"+SOUSDOSSIER_ICONES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("Création Dossier Vignettes du CD");
		dossierCD = new File(fichierCDLien+SOUSDOSSIER_VIGNETTES);
		dossierRef = new File(fichierRefLien+DOSSIER_IMAGES_REF+"/"+SOUSDOSSIER_VIGNETTES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dossierRef = new File(fichierRefLien+DOSSIER_IMAGES+"/"+SOUSDOSSIER_VIGNETTES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("Création Dossier Images du CD");
		dossierCD = new File(fichierCDLien+SOUSDOSSIER_MED_RES);
		dossierRef = new File(fichierRefLien+DOSSIER_IMAGES_REF+"/"+SOUSDOSSIER_MED_RES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dossierRef = new File(fichierRefLien+DOSSIER_IMAGES+"/"+SOUSDOSSIER_MED_RES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Copie du Fichier permettant d'aller directement sur la page d'accueil depuis la racine du CD
		log.info("Copie du Fichier : Doris_CD.html");
		dossierCD = new File(fichierCDLien);
		File fichierRef = new File(DOSSIER_RES_HTML+"/"+"Doris_CD.html");
		try {
			FileUtils.copyFileToDirectory(fichierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.debug("creationCD() - Fin");
	}

	private void transfoHtml(){
		log.debug("transfoHtml() - Début");
		String fichierCDLien = DOSSIER_RACINE + "/" + DOSSIER_CD + "/";

		// Modification Fichiers HTML : lien, images
		File dossierCD = new File(fichierCDLien+DOSSIER_HTML);
		for (File fichierHtml:dossierCD.listFiles()) {
			String contenuFichier = Outils.getFichierTxtFromDisk(fichierHtml);
			contenuFichier = contenuFichier.replace("href=\""+Constants.getSiteUrl(),"href=\"");
			
			// Le site n'est pas toujours très cohérent
			contenuFichier = contenuFichier.replace("src=\"../Images","src=\"images");
			contenuFichier = contenuFichier.replace("src=\"gestionenligne/images/icones","src=\"images" );
			
			
			// Pour chaque Liens à télécharger définis ci-après
			for (Lien lienTelecharge : getLienATelecharger()){
				switch (lienTelecharge.getLienKind()) {
				case PAGE :
					if ( ! lienTelecharge.getUrl().contains("=")) {
						contenuFichier = contenuFichier.replace("href=\""+lienTelecharge.getUrl()+"\"","href=\""+lienTelecharge.getFichier()+"\"");
					} else {
						contenuFichier = contenuFichier.replaceAll("href=\""+Pattern.quote(lienTelecharge.getUrl())+"[^\"]*\"","href=\""+lienTelecharge.getFichier()+"\"");
					}
					break;
				case ICONE :
					contenuFichier = contenuFichier.replace("src=\""+lienTelecharge.getUrl()+"\"","src=\"../"+SOUSDOSSIER_ICONES+"/"+lienTelecharge.getFichier()+"\"");
					contenuFichier = contenuFichier.replace("background=\""+lienTelecharge.getUrl()+"\"","background=\"../"+SOUSDOSSIER_ICONES+"/"+lienTelecharge.getFichier()+"\"");
					break;
				case TEXTE :
				case VIGNETTE :
				case MED_RES :
				case HI_RES :
				}
			}
			// Liens vers 
			for (Lien lienANettoyer : getLienANettoyer()){
				switch (lienANettoyer.getLienKind()) {
				case PAGE :
					contenuFichier = contenuFichier.replaceAll("href=\""+Pattern.quote(lienANettoyer.getUrl())+"[^\"]*\"","href=\""+lienANettoyer.getFichier()+"\"");
					break;
				case TEXTE :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),lienANettoyer.getFichier());
					break;
				case ICONE :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),"../"+SOUSDOSSIER_ICONES+lienANettoyer.getFichier());
					break;
				case VIGNETTE :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),"../"+SOUSDOSSIER_VIGNETTES+lienANettoyer.getFichier());
					break;
				case MED_RES :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),"../"+SOUSDOSSIER_MED_RES+lienANettoyer.getFichier());
					break;
				case HI_RES :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),"../"+SOUSDOSSIER_MED_RES+lienANettoyer.getFichier());
					break;
				}
			}
			
			// RegExp
			for (Lien lienRegExp : getRegExpPourNettoyer()){
				switch (lienRegExp.getLienKind()) {
				case PAGE :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.url,lienRegExp.getFichier());
				break;
				case ICONE :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),"../"+SOUSDOSSIER_ICONES+lienRegExp.getFichier());
				break;
				case VIGNETTE :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),"../"+SOUSDOSSIER_VIGNETTES+lienRegExp.getFichier());
					break;
				case MED_RES :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),"../"+SOUSDOSSIER_MED_RES+lienRegExp.getFichier());
					break;
				case HI_RES :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),"../"+SOUSDOSSIER_MED_RES+lienRegExp.getFichier());
					break;
				}
			}
			
			try {
				FileUtils.write(fichierHtml, contenuFichier, "iso-8859-1");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
		log.debug("transfoHtml() - Fin");
	}
}
