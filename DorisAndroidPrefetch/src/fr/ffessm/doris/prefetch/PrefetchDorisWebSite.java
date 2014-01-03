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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
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

	// Pourrait être un jour utile, on verra
	private final static String VERSION = "alpha 3.1";
	
	// we are using the in-memory H2 database
	//private final static String DATABASE_URL = "jdbc:h2:mem:fiche";
	// we are using the created SQLite database
	private final static String DATABASE_URL = "jdbc:sqlite:run/DorisAndroid.db";

	// Dossiers liés au fonctionnement de l'appli prefetch
	private final static String DOSSIER_RACINE = "./run";
	// Ces dossiers seront renommés qd nécessaire
	private final static String DOSSIER_HTML = "html";
	
	// Inititalisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchDorisWebSite.class);
	
	// Nombre maximum de fiches traitées (--max=K permet de changer cette valeur)
	private static int nbMaxFichesTraitees = 9999;
	private static String action = "";
	private static String dossier_ref = "";
	
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
						
			// On boucle sur les initailes des gens (Cf site : doris.ffessm.fr/contacts.asp?filtre=?)
			String testPhrase;

			testPhrase="Polymastia boletiformis <../fiche2.asp?fiche_numero=449> : prolongements \nplus hauts en doigts de gant, couleur jaune orangé.\nAaptos aaptos : couleur différente (jaune brun à jaune pâle) et papilles\n moins nombreuses et plus petites. Cependant, seule une observation des\n spicules* au microscope permettra de faire la différence.";
			Outils.nettoyageTextes(testPhrase);

			testPhrase="Test : \n- Puce 1\n- Puce 2\n";
			Outils.nettoyageTextes(testPhrase);

			testPhrase="Test : \nABC\nDEF\nghi\n";
			Outils.nettoyageTextes(testPhrase);
			
			//String contenuFichierHtml = Outils.getFichier(new File(fichierLocalFiche));
			//fiche.getFicheFromHtml(contenuFichierHtml, listeGroupes, listeParticipants);
			log.debug("doMain() - Fin TEST");
		} else {

			ConnectionSource connectionSource = null;
			try {
				// create empty DB and initialize it for Android
				initializeSQLite(DATABASE_URL);
				
				// create our data-source for the database
				connectionSource = new JdbcConnectionSource(DATABASE_URL);
				// setup our database and DAOs
				setupDatabase(connectionSource);
				if (! action.equals("UPDATE")){
					databaseInitialisation(connectionSource);
				}
				
				// - - - Groupes - - -
				// Récupération de la liste des groupes sur le site de DORIS
				String listeGroupesFichier = "";
				String contenuFichierHtml = null;
				
				if (! action.equals("NODWNLD")){
					listeGroupesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeGroupes.html";
					log.info("Récup. Liste Groupes Doris : " + listeGroupesFichier);
					
					if (Outils.getFichierUrl(Constants.getGroupesUrl(), listeGroupesFichier)) {
						contenuFichierHtml = Outils.getFichier(new File(listeGroupesFichier));
						
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(0);
					}
				} else {
					// NODWNLD
					listeGroupesFichier = DOSSIER_RACINE + "/" + dossier_ref + "/listeGroupes.html";
					if (new File(listeGroupesFichier).exists()) {
						contenuFichierHtml = Outils.getFichier(new File(listeGroupesFichier));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(0);
					}
				}
				
				final List<Groupe> listeGroupes =SiteDoris.getListeGroupesFromHtml(contenuFichierHtml);
				log.debug("doMain() - listeGroupes.size : "+listeGroupes.size());
				
				TransactionManager.callInTransaction(connectionSource,
					new Callable<Void>() {
						public Void call() throws Exception {
							// Pour les groupes on efface tout et on recommence en UPDATE (+ simple)
							if (action.equals("UPDATE")){
								DeleteBuilder<Groupe, Integer> deleteBuilder = dbContext.groupeDao.deleteBuilder();
								deleteBuilder.delete();
							}
							for (Groupe groupe : listeGroupes){
								dbContext.groupeDao.create(groupe);
							}
							return null;
					    }
					});

				// - - - Fiches - - -
				// Récupération de la liste des fiches sur le site de DORIS
				
				String listeFichesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeFiches.html";
				log.info("Récup. Liste Fiches Doris : " + listeFichesFichier);
				
				if (! action.equals("NODWNLD")){
					if (Outils.getFichierUrl(Constants.getListeFichesUrl(), listeFichesFichier)) {
						contenuFichierHtml = Outils.getFichier(new File(listeFichesFichier));
						
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(0);
					}
				} else {
					// NODWNLD
					listeFichesFichier = DOSSIER_RACINE + "/" + dossier_ref + "/listeFiches.html";
					if (new File(listeFichesFichier).exists()) {
						contenuFichierHtml = Outils.getFichier(new File(listeFichesFichier));
					} else {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(0);
					}
				}
				
				List<Fiche> listFicheFromHTML = SiteDoris.getListeFichesFromHtml(contenuFichierHtml);
				log.info("Nb Fiches sur le site : "+listFicheFromHTML.size());

				if (action.equals("UPDATE")) {
					listFicheFromHTML = SiteDoris.getListeFichesUpdated(dbContext.ficheDao.queryForAll(), listFicheFromHTML);
				}
				
				final List<Fiche> listeFichesATraiter = listFicheFromHTML;
				
				
				// Suppression de l'ensemble des informations liées aux fiches à traiter dans le cas d'un UPDATE
				// Sinon ne sert à rien et fait perdre du temps
				if (action.equals("UPDATE")) {
					TransactionManager.callInTransaction(connectionSource,
							new Callable<Void>() {
								public Void call() throws Exception {
									for (Fiche fiche : listeFichesATraiter){
										DeleteBuilder<Fiche, Integer> deleteFicheBuilder = dbContext.ficheDao.deleteBuilder();
										deleteFicheBuilder.where().eq("numeroFiche", fiche.getNumeroFiche());
										deleteFicheBuilder.delete();
										
										DeleteBuilder<AutreDenomination, Integer> deleteAutreDenominationBuilder = dbContext.autreDenominationDao.deleteBuilder();
										deleteAutreDenominationBuilder.where().eq("fiche_id", fiche.getNumeroFiche());
										deleteAutreDenominationBuilder.delete();
										
										DeleteBuilder<PhotoFiche, Integer> deletePhotoFicheBuilder = dbContext.photoFicheDao.deleteBuilder();
										deletePhotoFicheBuilder.where().eq("fiche_id", fiche.getNumeroFiche());
										deletePhotoFicheBuilder.delete();
										
										DeleteBuilder<Fiches_ZonesGeographiques, Integer> deleteFicheZoneGeoBuilder = dbContext.fiches_ZonesGeographiquesDao.deleteBuilder();
										deleteFicheZoneGeoBuilder.where().eq("Fiche_id", fiche.getNumeroFiche());
										deleteFicheZoneGeoBuilder.delete();
									}
									return null;
							    }
							});
				}
				
				// Création de l'entête des fiches
				TransactionManager.callInTransaction(connectionSource,
					new Callable<Void>() {
						public Void call() throws Exception {
							for (Fiche fiche : listeFichesATraiter){
								dbContext.ficheDao.create(fiche);
							}
							return null;
					    }
					});
				
				log.debug("doMain() - listeFichesATraiter.size : "+listeFichesATraiter.size());
				
				// Fin pour la Liste des Fiches, les fiches sont traitées une fois la liste des Participants
				// et la liste des Groupes connues

				
				
				// - - - Intervenants - - -
				// On boucle sur les initiales des gens (Cf site : doris.ffessm.fr/contacts.asp?filtre=?)
				String listeFiltres;
				if (nbMaxFichesTraitees == 9999){
					listeFiltres="abcdefghijklmnopqrstuvwxyz";
				} else {
					listeFiltres="ab";
				}
				
				// Pour les participants on efface tout et on recommence en UPDATE (+ simple)
				if (action.equals("UPDATE")){
					TransactionManager.callInTransaction(connectionSource,
						new Callable<Void>() {
							public Void call() throws Exception {
								DeleteBuilder<Participant, Integer> deleteBuilder = dbContext.participantDao.deleteBuilder();
								deleteBuilder.delete();
								return null;
						    }
						});
				}
				
				for (char initiale : listeFiltres.toCharArray()){
					log.debug("doMain() - Recup Participants : "+initiale);
					
					String listeParticipantsFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeParticipants-"+initiale+".html";
					log.info("Récup. Liste des Participants : " + listeParticipantsFichier);
					
					if (! action.equals("NODWNLD")){
						if (Outils.getFichierUrl(Constants.getListeParticipantsUrl(""+initiale), listeParticipantsFichier)) {
							contenuFichierHtml = Outils.getFichier(new File(listeParticipantsFichier));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste des Participants : "+initiale);
							System.exit(0);
						}
					} else {
						// NODWNLD
						listeParticipantsFichier = DOSSIER_RACINE + "/" + dossier_ref + "/listeParticipants-"+initiale+".html";
						if (new File(listeParticipantsFichier).exists()) {
							contenuFichierHtml = Outils.getFichier(new File(listeParticipantsFichier));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste des Participants : "+initiale);
							System.exit(0);
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
				
				List<Participant> listeParticipants = new ArrayList<Participant>(0);
				listeParticipants.addAll(dbContext.participantDao.queryForAll());
				log.debug("doMain() - listeParticipants.size : "+listeParticipants.size());
				
				
				
				// - - - Glossaire - - -
				// On boucle sur les initiales des définitions (Cf site : doris.ffessm.fr/glossaire.asp?filtre=?)
				if (nbMaxFichesTraitees == 9999){
					listeFiltres="abcdefghijklmnopqrstuvwxyz";
				} else {
					listeFiltres="ab";
				}
				
				// TODO : Pour le Glossaire comme il y a bcp de pages à télécharger, il faudra faire comme les fiches
				// TODO : En attendant on efface tout
				if (action.equals("UPDATE")){
					TransactionManager.callInTransaction(connectionSource,
						new Callable<Void>() {
							public Void call() throws Exception {
								DeleteBuilder<DefinitionGlossaire, Integer> deleteBuilder = dbContext.definitionGlossaireDao.deleteBuilder();
								deleteBuilder.delete();
								return null;
						    }
						});
				}
				
				for (char initiale : listeFiltres.toCharArray()){
					log.debug("doMain() - Recup Page de définitions pour la lettre : "+initiale);
					
					int numero = 1;
					boolean continuer;
					do {
						String listeDefinitionsFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeDefinitions-"+initiale+"-"+numero+".html";
						log.info("Récup. Liste des Définitions : " + listeDefinitionsFichier);
						
						if (! action.equals("NODWNLD")){
							if (Outils.getFichierUrl(Constants.getListeDefinitionsUrl(""+initiale,""+numero ), listeDefinitionsFichier)) {
								contenuFichierHtml = Outils.getFichier(new File(listeDefinitionsFichier));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste des Définitions : "+initiale);
								System.exit(0);
							}
						} else {
							// NODWNLD
							listeDefinitionsFichier = DOSSIER_RACINE + "/" + dossier_ref + "/listeDefinitions-"+initiale+".html";
							if (new File(listeDefinitionsFichier).exists()) {
								contenuFichierHtml = Outils.getFichier(new File(listeDefinitionsFichier));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste des Définitions : "+initiale);
								System.exit(0);
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
				
				//TODO : ici il faudra traiter dans le cas des updates :
				// - calculer la liste des définitions à traiter
				// - les supprimer dans la base
				List<DefinitionGlossaire> listeDefinitionsATraiter = listeDefinitions;
				
				// Pour chaque Définitions de la liste de travail :
				// On la télécharge (et sauvegarde le fichier original)
				// On la traite
				int nbDefinitionTraitees = 0;
				for (DefinitionGlossaire definition : listeDefinitionsATraiter) {
					nbDefinitionTraitees += 1;
					
					if (  (nbDefinitionTraitees % 50) == 0) {
						if (! action.equals("NODWNLD")) {
							log.info("Définitions traitées = "+nbDefinitionTraitees+", pause de 1s...");
							Thread.sleep(1000);
						}
					}
						
					log.debug("doMain() - Traitement Définition : "+definition.getNumeroDoris());
					
					String urlDefinition =  Constants.getDefinitionUrl( ""+definition.getNumeroDoris() );
					String fichierLocalDefinition = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/definition-"+definition.getNumeroDoris()+".html";
					if (! action.equals("NODWNLD")) {
						if (Outils.getFichierUrl(urlDefinition, fichierLocalDefinition)) {
							
							contenuFichierHtml = Outils.getFichier(new File(fichierLocalDefinition));
						
						} else {
							log.error("Une erreur est survenue lors de la récupération de la définition : "+urlDefinition);
							
							continue;
						}
					} else {
						// NODWNLD
						fichierLocalDefinition = DOSSIER_RACINE + "/" + dossier_ref + "/definition-"+definition.getNumeroDoris()+".html";
						if (new File(fichierLocalDefinition).exists()) {
							contenuFichierHtml = Outils.getFichier(new File(fichierLocalDefinition));
						} else {
							log.error("La récupération de la fiche : "+urlDefinition+" a échoué.");
						}
					}
					
					definition.setContextDB(dbContext);
					definition.getDefinitionsFromHtml(contenuFichierHtml);
					dbContext.definitionGlossaireDao.update(definition);

				}

				// - - - Fiche - - -
				// Pour chaque fiche de la liste de travail :
				// On la télécharge (et sauvegarde le fichier original)
				// On la traite

				log.info("Mise à jours de "+listeFichesATraiter.size()+" fiches.");
				int nbFichesTraitees = 0;
				for (Fiche fiche : listeFichesATraiter) {
					nbFichesTraitees += 1;
					
					if (  (nbFichesTraitees % 50) == 0) {
						if (! action.equals("NODWNLD")) {
							log.info("fiche traitées = "+nbFichesTraitees+", pause de 1s...");
							Thread.sleep(1000);
						}
					}
					if (  nbFichesTraitees <= nbMaxFichesTraitees ) {
						
						log.debug("doMain() - Traitement Fiche : "+fiche.getNomCommun());
						
						String urlFiche =  Constants.getFicheFromIdUrl( fiche.getNumeroFiche() );
						String fichierLocalFiche = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/fiche-"+fiche.getNumeroFiche()+".html";
						if (! action.equals("NODWNLD")) {
							if (Outils.getFichierUrl(urlFiche, fichierLocalFiche)) {
								
								contenuFichierHtml = Outils.getFichier(new File(fichierLocalFiche));
							
							} else {
								log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
								
								urlFiche = Constants.getFicheFromNomCommunUrl(fiche.getNomCommun());
								log.error("=> Tentative sur : "+urlFiche);
								if (Outils.getFichierUrl(urlFiche, fichierLocalFiche)) {
									
									contenuFichierHtml = Outils.getFichier(new File(fichierLocalFiche));
									
								} else {
									log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
									continue;
								}
	
							}
						} else {
							// NODWNLD
							fichierLocalFiche = DOSSIER_RACINE + "/" + dossier_ref + "/fiche-"+fiche.getNumeroFiche()+".html";

							if (new File(fichierLocalFiche).exists()) {
								contenuFichierHtml = Outils.getFichier(new File(fichierLocalFiche));
							} else {
								log.error("La récupération de la fiche : "+fichierLocalFiche+" a échoué.");
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
						String contenuFichierHtmlListePhotos = null;
						if (! action.equals("NODWNLD")) {
							if (Outils.getFichierUrl(urlListePhotos, fichierLocalListePhotos)) {
								contenuFichierHtmlListePhotos = Outils.getFichier(new File(fichierLocalListePhotos));
							
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
								//continue;
							}
						} else {
							// NODWNLD
							fichierLocalListePhotos = DOSSIER_RACINE + "/" + dossier_ref + "/fiche-"+fiche.getNumeroFiche()+"_listePhotos.html";

							if (new File(fichierLocalListePhotos).exists()) {
								contenuFichierHtmlListePhotos = Outils.getFichier(new File(fichierLocalListePhotos));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
							}
						}
						if(contenuFichierHtmlListePhotos != null){
							// TODO update liste photos for fiche
							final List<PhotoFiche> listePhotoFiche = SiteDoris.getListePhotosFicheFromHtml(fiche, contenuFichierHtmlListePhotos);
							final Fiche ficheMaj = fiche;
							TransactionManager.callInTransaction(connectionSource,
								new Callable<Void>() {
									public Void call() throws Exception {
										for (PhotoFiche photoFiche : listePhotoFiche){
											photoFiche.setFiche(ficheMaj);
											dbContext.photoFicheDao.create(photoFiche);
										}
										if(listePhotoFiche.size() > 0){
											ficheMaj.setPhotoPrincipale(listePhotoFiche.get(0));
											dbContext.ficheDao.update(ficheMaj);
										}
										return null;
								    }
								});

						}
					}
					else{
						log.info("Nombre max de fiches à traiter atteint.");
						break; // ignore les fiches suivantes
					}
				}
				
				
				// mise à jour des zones geographiques
				// zone France Métropolitaine Marines
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE);
				// zone France Métropolitaine Eau douce
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE);
				// zone indo pacifique
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE);
				// zone Caraibes
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES);
				// zone atlantique nordOuest
				majZoneGeographique(connectionSource, ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST);
			
				Date date = new Date();
				SimpleDateFormat ft =  new SimpleDateFormat ("dd/MM/yyyy  HH:mm");
				dbContext.dorisDB_metadataDao.create(new DorisDB_metadata(ft.format(date),""));
				
	
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
		
		String listeFichesFichier = DOSSIER_RACINE + "/" + DOSSIER_HTML + "/listeFiches"+zoneKing.name()+".html";
		log.debug("Récup. Liste Fiches Doris Zone : " + listeFichesFichier);
		//List<Fiche> listeFiches = new ArrayList<Fiche>(0);
		String contenuFichierHtml = "";
		if (! action.equals("NODWNLD")){
			if (Outils.getFichierUrl(Constants.getListeFichesUrl(zoneKing), listeFichesFichier)) {
				contenuFichierHtml = Outils.getFichier(new File(listeFichesFichier));
				
			} else {
				log.error("Une erreur est survenue lors de la récupération de la liste des fiches de la zone ");
				return;
			}
		} else {
			// NODWNLD
			listeFichesFichier = DOSSIER_RACINE + "/" + dossier_ref + "/listeFiches"+zoneKing.name()+".html";
			if (new File(listeFichesFichier).exists()) {
				contenuFichierHtml = Outils.getFichier(new File(listeFichesFichier));
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
		
		final List<Fiche> listFicheFromHTML = SiteDoris.getListeFichesFromHtml(contenuFichierHtml);
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
			if ( arg.equals("-V") || arg.equals("--version")) {
				log.debug("checkArgs() - arg : " + arg);
				version();
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
				dossier_ref = inArgs[inArgs.length - 1].trim();
				if (dossier_ref.isEmpty()){
					log.error("Le dossier de référence n'est pas renseigné");
					System.exit(0);
				}
			} else if (arg.equals("NEWFICHES")) {
				action = arg;
			} else if (arg.equals("UPDATE")) {
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
		
		if (! action.equals("UPDATE") ) {
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
		TableUtils.createTable(connectionSource, DorisDB_metadata.class);
		
		log.debug("databaseInitialisation() - Fin");
	}


	/**
     * Afficher l'aide de l'application
     **/	
	private static void help(){
		log.debug("help() - Début");
		
		System.out.println("Récupération de la base de fiches pour DorisAndroid, Version : " + VERSION);
		System.out.println("Usage: java -jar PrefetchDorisWebSite.jar [OPTIONS] [ACTION]");
		System.out.println("");
		System.out.println("OPTIONS :");
		System.out.println("  -M, --max=K		on limite le travail au K 1ères fiches (utile en dev.)");
		System.out.println("  -V, --version     afficher la version de l'application et quitter");
		System.out.println("  -h, --help        afficher cette aide");
		System.out.println("  -d, --debug       messages detinés aux développeurs de cette application");
		System.out.println("  -v, --verbose     messages permettant de suivre l'avancé des traitements");
		System.out.println("  -s, --silence     aucune sortie, même pas les erreurs");
		System.out.println("");
		System.out.println("ACTION :");
		System.out.println("  INIT               Toutes les fiches sont retéléchargées sur doris.ffessm.fr et retraitées pour créer la base (images comprises)");
		System.out.println("  NODWNLD dossier    Pas de téléchargement, travail sur un dossier de référznce (utile en dév.)");
		System.out.println("  NEWFICHES    TODO: Ne télécharge que les nouvelles fiches");
		System.out.println("  UPDATE             En plus des nouvelles fiches, on retélécharge les fiches qui ont changées de statut");
		System.out.println("  TEST          	 Pour les développeurs");
		
		log.debug("help() - Fin");
	}
	
	/**
     * Afficher la version de l'application
     * */	
	private static void version(){
		log.debug("version() - Début");
		
		System.out.println("Version : " + VERSION);
		
		log.debug("version() - Fin");
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
		if(inAction.equals("INIT")){
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
		
		//	Vérification que le dossier Référence existe
		if(inAction.equals("NODWNLD") || inAction.equals("UPDATE") ){
			final File dossierReference = new File(DOSSIER_RACINE + "/" + dossier_ref);
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
		
		
		// Le fichier de la base de données
		if(inAction.equals("INIT") || inAction.equals("NODWNLD")){
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
	
}
