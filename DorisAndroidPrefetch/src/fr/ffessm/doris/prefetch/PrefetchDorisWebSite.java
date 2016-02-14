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
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.DataBase_Outils;
import fr.ffessm.doris.android.sitedoris.ErrorCollector;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
import fr.ffessm.doris.prefetch.ezpublish.JsonToDB;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;

public class PrefetchDorisWebSite {

	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchDorisWebSite.class);
	
	// Nombre maximum de fiches traitées (--max=K permet de changer cette valeur)
	private int nbMaxFichesATraiter = PrefetchConstants.nbMaxFichesTraiteesDef;
	private ActionKind action;
	private boolean zipCDDVD = false;
	
	public enum ActionKind {
		INIT,
		UPDATE,
		NODWNLD,
		CDDVD_MED,
		CDDVD_HI,
		TEST,
		DB_TO_ANDROID,
		DB_IMAGE_UPGRADE,
		DWNLD_TO_REF,
		ERASE_BUT_REF,
		ERASE_ALL
	}
	
	ConnectionSource connectionSource = null;
	DorisDBHelper dbContext = null;
	DataBase_Outils outilsBase = null;
	PrefetchDBTools prefetchDBTools = null;
	
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
		log.info("Nb. Fiches Max : " + nbMaxFichesATraiter);
				
		
		// - - - - - - - - - - - -
		// - - - Test  - - - - - -
		if ( action == ActionKind.TEST ) {
			
			testAction();
			
		} else if(action == ActionKind.INIT || action == ActionKind.UPDATE || action == ActionKind.NODWNLD
				|| action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ) {
			
			cdDVDAction();		
			
		} else if ( action == ActionKind.DB_TO_ANDROID ) {
			
			dbToAndroidAction();
		
		} else if ( action == ActionKind.DB_IMAGE_UPGRADE ) {
			
			dbImageV4UpgradeAction();
			
		} else if ( action == ActionKind.DWNLD_TO_REF ) {

			downloadToRefAction();
			
		} else if ( action == ActionKind.ERASE_BUT_REF ) {
			
			eraseButRefAction();
			
		} else if ( action == ActionKind.ERASE_ALL ) {
			
			eraseAllAction();
		}

		
		log.debug("doMain() - Fin");
	}


	private void testAction() throws Exception{
		log.debug("doMain() - Début TEST");
		
		// Vérification, Création, Sauvegarde des dossiers de travail
		renommageDossiers(ActionKind.INIT);
		creationDossiers(ActionKind.INIT);
		creationDossiersRef(ActionKind.INIT);
		
		// - - - Base de Données - - -
		PrefetchDBTools prefetchDBTools = new PrefetchDBTools();
		prefetchDBTools.initializeSQLite(PrefetchConstants.DATABASE_URL);
		connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL);
		dbContext = prefetchDBTools.setupDatabase(connectionSource);
		prefetchDBTools.databaseInitialisation(connectionSource);
		outilsBase = new DataBase_Outils(dbContext);
					
		PrefetchGlossaire glossaire = new PrefetchGlossaire(dbContext, connectionSource, ActionKind.INIT, nbMaxFichesATraiter);
		if ( glossaire.prefetch() == -1 ) {
			log.debug("doMain() - Erreur Glossaire" );
			System.exit(1);
		}
		
		log.debug("doMain() - Fin TEST");
	}
	
	
	private void cdDVDAction()  throws Exception{
		
		// enable collection of Doris web site errors in an juit xml file
		ErrorCollector.getInstance().collectErrors = true;
				
		// Vérification, Création, Sauvegarde des dossiers de travail
		renommageDossiers(action);
		creationDossiers(action);
		creationDossiersRef(action);
		
		// - - - Base de Données - - -
		PrefetchDBTools prefetchDBTools = new PrefetchDBTools();
		
		// create empty DB and initialize it for Android
		prefetchDBTools.initializeSQLite(PrefetchConstants.DATABASE_URL);
		
		// create our data-source for the database
		connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL);
		
		// setup our database and DAOs
		dbContext = prefetchDBTools.setupDatabase(connectionSource);
		
		prefetchDBTools.databaseInitialisation(connectionSource);
		
		outilsBase = new DataBase_Outils(dbContext);
		
		
		try {
			// - - - Groupes - - -
			// Récupération de la liste des groupes sur le site de DORIS
			// En UPDATE et CDDVD on re-télécharge que la liste

			PrefetchGroupes groupes = new PrefetchGroupes(dbContext, connectionSource, action, nbMaxFichesATraiter);
			if ( groupes.prefetch() == -1 ) {
				log.debug("doMain() - Erreur Groupes" );
				System.exit(1);
			}

			ErrorCollector.getInstance().dumpErrorsAsJUnitFile(PrefetchConstants.DOSSIER_TESTS + "/dorisSite_groupes_testsuites.xml");
			log.debug("doMain() - debbug" );
			
			// - - - Intervenants - - -
			// On boucle sur les initiales des gens (Cf site : doris.ffessm.fr/contacts.asp?filtre=?)
			// On récupère la liste des intervenants dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD

			PrefetchIntervenants intervenants = new PrefetchIntervenants(dbContext, connectionSource, action, nbMaxFichesATraiter);
			if ( intervenants.prefetch() == -1 ) {
				log.debug("doMain() - Erreur Intervenants" );
				System.exit(1);
			}

			ErrorCollector.getInstance().dumpErrorsAsJUnitFile(PrefetchConstants.DOSSIER_TESTS + "/dorisSite_intervenants_testsuites.xml");
			
			// - - - Glossaire - - -
			// On boucle sur les initiales des définitions (Cf site : doris.ffessm.fr/glossaire.asp?filtre=?)
			// On récupère la liste des termes dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD

			PrefetchGlossaire glossaire = new PrefetchGlossaire(dbContext, connectionSource, action, nbMaxFichesATraiter);
			if ( glossaire.prefetch() == -1 ) {
				log.debug("doMain() - Erreur Glossaire" );
				System.exit(1);
			}

			ErrorCollector.getInstance().dumpErrorsAsJUnitFile(PrefetchConstants.DOSSIER_TESTS + "/dorisSite_glossaire_testsuites.xml");
			// - - - Bibliographie - - -
			// On boucle sur la page des Fiches tant que l'on trouve dans la page courante (n)
			//biblio.asp?mapage=(n+1)&PageCourante=n
			// On récupère les Bibliographies dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
			
			PrefetchBibliographies bibliographies = new PrefetchBibliographies(dbContext, connectionSource, action, nbMaxFichesATraiter);
			if ( bibliographies.prefetch() == -1 ) {
				log.debug("doMain() - Erreur Bibliographies" );
				System.exit(1);
			}

			ErrorCollector.getInstance().dumpErrorsAsJUnitFile(PrefetchConstants.DOSSIER_TESTS + "/dorisSite_biblio_testsuites.xml");
			// - - - Liste des Fiches - - -
			// Récupération de la liste des fiches sur le site de DORIS
			// Elles sont récupérées dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD

			PrefetchFiches listeFiches = new PrefetchFiches(dbContext, connectionSource, action, nbMaxFichesATraiter,
					groupes.listeGroupes, intervenants.listeParticipants);
			if ( listeFiches.prefetch() == -1 ) {
				log.debug("doMain() - Erreur Liste des Fiches" );
				System.exit(1);
			}

			ErrorCollector.getInstance().dumpErrorsAsJUnitFile(PrefetchConstants.DOSSIER_TESTS + "/dorisSite_fiches_testsuites.xml");

			// - - - Mise à jour des zones géographiques - - -
			PrefetchZonesGeographiques zonesGeographiques = new PrefetchZonesGeographiques(dbContext, connectionSource, action, nbMaxFichesATraiter);
			if ( zonesGeographiques.prefetch() == -1 ) {
				log.debug("doMain() - Erreur Mise à jour des zones géographiques" );
				System.exit(1);
			}

			ErrorCollector.getInstance().dumpErrorsAsJUnitFile(PrefetchConstants.DOSSIER_TESTS + "/dorisSite_zonesgeo_testsuites.xml");
			
			// - - - Enregistrement Date génération Base - - -
			Date date = new Date();
			SimpleDateFormat ft =  new SimpleDateFormat ("dd/MM/yyyy  HH:mm", Locale.US);
			dbContext.dorisDB_metadataDao.create(new DorisDB_metadata(ft.format(date),""));
			
			
			// - - - Génération CD et DVD  - - - 
			if ( action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI) {
				GenerationCDDVD generationCDDVD = new GenerationCDDVD(dbContext, connectionSource, action, zipCDDVD);
				generationCDDVD.generation();
			}
			
			
		} finally {
			// destroy the data source which should close underlying connections
			log.debug("doMain() - Fermeture Base");
			if (connectionSource != null) {
				connectionSource.close();
			}
		}
	}
	
	private void dbToAndroidAction(){
		log.debug("doMain() - Début Déplacement Base");
		
		// Consiste au déplacement du fichier de la base du run vers assets
		String dataBaseRunString = PrefetchConstants.DATABASE_URL.substring(PrefetchConstants.DATABASE_URL.lastIndexOf(":")+1, PrefetchConstants.DATABASE_URL.length() );
		log.debug("dataBase : " + dataBaseRunString);
		
		String dataBaseName = dataBaseRunString.substring(dataBaseRunString.lastIndexOf("/")+1, dataBaseRunString.length() );
		log.debug("dataBaseName : " + dataBaseName);
		
		File dataBaseRunFile = new File(dataBaseRunString);
		File dataBaseAndroidFile = new File("../DorisAndroid/assets/"+dataBaseName);
		
		if ( dataBaseRunFile.exists() ){
			if ( dataBaseAndroidFile.exists() ){
				try {
					FileUtils.forceDelete(dataBaseAndroidFile);
					log.info("Suppression du fichier précédent : " + dataBaseAndroidFile.getAbsolutePath());
				} catch (IOException e) {
					log.info("Problème suppression du fichier précédent : " + dataBaseAndroidFile.getAbsolutePath());
					e.printStackTrace();
				}
			}
			
			try {
				// Ne pas faire un moveTo car il faut que les 2 fichiers soient sur le même disque
				// ça n'est pas le cas si on utilise un tmpfs pour run/database
				FileUtils.moveFile(dataBaseRunFile, dataBaseAndroidFile);
				log.info("Déplacement du fichier de la base vers : " + dataBaseAndroidFile.getAbsolutePath());
			} catch (IOException e) {
				log.error("Echec Déplacement du fichier de la base de : " + dataBaseRunFile.getAbsolutePath());
				log.error("vers : " + dataBaseAndroidFile.getAbsolutePath());
				e.printStackTrace();
			}
		} else {
			log.error("Le fichier de la Base n'existe pas ou plus dans le Prefetch");
			System.exit(1);
		}
		
		log.debug("doMain() - Fin Déplacement Base");
	}
	
	
	private void dbImageV4UpgradeAction() throws Exception{
		log.debug("dbImageV4UpgradeAction() - Début upgrade images pour Doris V4");
		
		JsonToDB jsonToDB = new JsonToDB();

		/*
		Credential credent = DorisAPIConnexionHelper
				.authorizeViaWebPage(DorisOAuth2ClientCredentials.getUserId());
		
		DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper = new DorisAPI_JSONTreeHelper(credent);
		DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper(credent);
		*/
		DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper = new DorisAPI_JSONTreeHelper();
		DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper();
		
		// copie ancienne base pour travailler dessus
		String dataBaseName = PrefetchConstants.DATABASE_URL.substring(PrefetchConstants.DATABASE_URL.lastIndexOf(":")+1, PrefetchConstants.DATABASE_URL.lastIndexOf(".") );
		log.debug("dbImageV4UpgradeAction() - dataBaseName : " + dataBaseName);
		File fichierDB= new File(dataBaseName+".db");
		if (fichierDB.exists()){
			File fichierDBNew = new File(dataBaseName+"_for_V4.db");
			FileUtils.copyFile(fichierDB, fichierDBNew);
			
		}
		
		// - - - Base de Données - - -
		PrefetchDBTools prefetchDBTools = new PrefetchDBTools();
		// create our data-source for the database
		connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL.replaceAll("DorisAndroid.db", "DorisAndroid_for_V4.db"));
		
		// setup our database and DAOs
		dbContext = prefetchDBTools.setupDatabase(connectionSource);
			
		try{
			// remove all previous photos
			log.debug("doMain() - Remove all previous photos");
			
			TableUtils.clearTable(connectionSource, PhotoFiche.class);
			
		
			// récupère tous les nodeIds des fiches connues de Doris V4
			log.debug("doMain() - Récupère tous les nodeIds des fiches connues de Doris V4");
			
			int nbFichesDORIS = 3700;
			int nbFichesParRequetes = 50;

			int count = 0;
			
			for(int i=0; i < (nbFichesDORIS / nbFichesParRequetes); i++){

				List<Integer> nodeIds = dorisAPI_JSONTreeHelper.getFichesNodeIds(nbFichesParRequetes, nbFichesParRequetes * i);
			
			
				for (Integer especeNodeId : nodeIds) {
					count++;
					if( count > nbMaxFichesATraiter ){
						log.debug("doMain() - nbMaxFichesATraiter atteint");
						i=9999;
						break;
					}
					 
					// Référence de l'Espèce dans le message JSON 
					Espece especeJSON = dorisAPI_JSONDATABindingHelper.getEspeceFieldsFromNodeId(especeNodeId);
					String especeJSONReferenceId = especeJSON.getFields().getReference().getValue();
					
					log.debug(" nodeId="+especeNodeId+", dorisId="+especeJSONReferenceId +", imagesNodeIds="+especeJSON.getFields().getImages().getValue());
									
					List<Image> imageData = new ArrayList<Image>();
					
					
					// itère sur les images trouvées pour cette fiche
					for(String possibleImageId : especeJSON.getFields().getImages().getValue().split("\\|")){
						try{
							int imageId = Integer.parseInt(possibleImageId.replaceAll("&", ""));
							// récupère les données associées à l'image
							imageData.add(dorisAPI_JSONDATABindingHelper.getImageFromImageId(imageId));	
	
						} catch ( NumberFormatException nfe){
							// ignore les entrées invalides
						}
					}
					
					log.debug(" ficheDao.queryBuilder() ="+dbContext.ficheDao.queryBuilder().where().eq("numeroFiche", especeJSONReferenceId).getStatement() );
					
					final Fiche fiche = dbContext.ficheDao.queryForFirst(
							dbContext.ficheDao.queryBuilder().where().eq("numeroFiche", especeJSONReferenceId).prepare()
						);
					
					if (fiche != null) {
						
						fiche.setContextDB(dbContext);
					
						// recrée une entrée dans la base pour l'image
						final List<PhotoFiche> listePhotoFiche = jsonToDB.getListePhotosFicheFromJsonImages(imageData);
						TransactionManager.callInTransaction(connectionSource,
							new Callable<Void>() {
								public Void call() throws Exception {
									int count = 0;
									for (PhotoFiche photoFiche : listePhotoFiche){
										
										photoFiche.setFiche(fiche);
										
										dbContext.photoFicheDao.create(photoFiche);
										
										if (count == 0) {
											// met à jour l'image principale de la fiche
											fiche.setPhotoPrincipale(photoFiche);
											dbContext.ficheDao.update(fiche);
										}
										count++;
									}
									return null;
							    }
							});
					
					} else {
						
						log.error("! ! ! Fiche non trouvée : "+especeJSONReferenceId+" ! ! ! !");
						
					}
	
				}
			}
		
		} finally {
			// destroy the data source which should close underlying connections
			log.debug("doMain() - Fermeture Base");
			if (connectionSource != null) {
				connectionSource.close();
			}
		}
				
		log.debug("doMain() - Fin upgrade images pour Doris V4");
	}
	
	private void downloadToRefAction() {
		log.debug("doMain() - Début Déplacement Fichiers vers Ref");
		
		// Consiste au déplacement des fichiers de html vers html_ref
		// et ceux de images vers images_ref

		// On commence par vérifier que les dossiers ref existent
		creationDossiersRef(action);

		// html -> html_ref
		File dossierHtml = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML);
		File dossierHtmlRef = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF);
		try {
			FileUtils.copyDirectory(dossierHtml, dossierHtmlRef);
			FileUtils.deleteDirectory(dossierHtml);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// images -> images_ref
		File dossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES);
		if (dossierImages.exists()) {
			File dossierImagesRef = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF);
			try {
				FileUtils.copyDirectory(dossierImages, dossierImagesRef);
				FileUtils.deleteDirectory(dossierImages);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		log.debug("doMain() - Fin Déplacement Fichiers vers Ref");
	}
	
	private void eraseButRefAction() {
		log.debug("doMain() - Début Effacement Fichiers autres que Ref");
		
		// Effacement de tous les fichiers de run sauf image_ref et html_ref
		
		// Le chemin vers un dossier peut être écrit de façon différente
		// Comparer 2 dossiers (File) ne fonctionne pas forcément, nous nous basons donc sur 
		// sur la fin du chemin : pas top mais ça fonctionne
		File dossierRun = new File(PrefetchConstants.DOSSIER_RACINE);
		log.debug("dossierRun : " + dossierRun.getAbsolutePath());
		
		if ( dossierRun.exists() ){
			
			for (File dossierFils : dossierRun.listFiles()) {
				
				if ( dossierFils.isDirectory()
						&& ! dossierFils.toString().matches(".*"+PrefetchConstants.DOSSIER_HTML_REF)
						&& ! dossierFils.toString().matches(".*"+PrefetchConstants.DOSSIER_IMAGES_REF) ) {
			
					try {
						// TODO : si dossier de la base dans tmpfs ne marche pas (évidement ?)
						FileUtils.deleteDirectory(dossierFils);
						log.info("Suppression de : " + dossierFils.getAbsolutePath());
					} catch (IOException e) {
						log.info("Problème suppression de : " + dossierFils.getAbsolutePath());
						e.printStackTrace();
					}
					
				}
			}
		} else {
			// Ne devrait jamais arriver
			log.error("Le dossier run n'existe pas !");
			System.exit(1);
		}
		
		log.debug("doMain() - Fin Effacement Fichiers autres que Ref");
	}
	
	private void eraseAllAction() {
		log.debug("doMain() - Début Effacement tous Dossiers");
		
		// Effacement de tous les fichiers de run
		// à reserver à la mise à jour complète de la base : tâche "mensuelle"
		
		File dossierRun = new File(PrefetchConstants.DOSSIER_RACINE);
		log.debug("dossierRun : " + dossierRun.getAbsolutePath());
		
		if ( dossierRun.exists() ){
			
			try {
				FileUtils.cleanDirectory(dossierRun);
				log.info("Suppression du contenu de : " + dossierRun.getAbsolutePath());
			} catch (IOException e) {
				log.info("Problème suppression du contenu de : " + dossierRun.getAbsolutePath());
				e.printStackTrace();
			}
			
		} else {
			// Ne devrait jamais arriver
			log.error("Le dossier run n'existe pas !");
			System.exit(1);
		}
		
		log.debug("doMain() - Fin Effacement tous Dossiers");
	}
	/**
	 * Vérification des arguments passés à l'application
	 * 
	 *  @param args
	 */
	private ActionKind checkArgs(String[] inArgs){
			
		// Si Aucun Argument, on affiche l'aide et on termine
		log.debug("checkArgs() - nb args : " + inArgs.length);
		if (inArgs.length < 1) {
			help();
			log.error("Le programme ne peut être lancé sans arguments.");
			System.exit(1);
		}
		
		// On commence par regarder si un des paramètres est un paramètre optionnel prioritaire
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
		
		
		// Vérification que le dernier argument est une des actions prévues
		ActionKind action = null;
		log.debug("checkArgs() - argument action");
		for (String arg : inArgs) {
			for (ActionKind actionKind : ActionKind.values()) {
				// TODO : ActionKind.valueOf(*) ?
				if (arg.equals(actionKind.toString())) {
					action = actionKind;
				}
			}
		}
		log.debug("checkArgs() - action : "+action);
		
		if (action == null) {
			help();
			String listeArgs = "";
			for (String arg : inArgs) {
				listeArgs += arg + " ";
			}
			log.error("arguments : " + listeArgs);
			log.error("Action non prévue");
			System.exit(1);
		}
		
		
		
		// Paramètres autres :
		//   - qui permet de limiter le nombre de fiches à traiter
		//   - qui permet de Zipper le CD à l'issue de sa génération
		log.debug("checkArgs() - max ? ");
		for (String arg : inArgs) {
			
			// Permet de limiter le nombre de fiches à traiter (utile en Dev.)
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
					 nbMaxFichesATraiter = Integer.parseInt(nbFichesStr);
					 log.debug("checkArgs() - nbMaxFichesTraitees : " + nbMaxFichesATraiter);
			    } catch(NumberFormatException e) { 
			    	help();
					log.error("Argument -M ou --max mal utilisé : " + arg);
			    	System.exit(1);
			    }
			}
			
			// Permet de Zipper le CD à l'issue de sa génération
			if ( arg.startsWith("-Z") || arg.startsWith("--zip")) {
				log.debug("checkArgs() - arg : " + arg);
				if (action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI) {
					zipCDDVD = true;
				} else {
					help();
					log.error("Argument -Z ou --zip réservé au Mode : CDDVD");
			    	System.exit(1);
			    }
			}
		}

		

		return action;

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
		System.out.println("  -M, --max=K		On limite le travail au K 1ères fiches (utile en dev.)");
		System.out.println("  -Z, --zip         Option réservée au Mode CDDVD, elle permet de ZIPPER le dossier généré");
		System.out.println("  -h, --help        Afficher cette aide");
		System.out.println("  -d, --debug       Messages detinés aux développeurs de cette application");
		System.out.println("  -v, --verbose     Messages permettant de suivre l'avancé des traitements");
		System.out.println("  -s, --silence     Aucune sortie, même pas les erreurs");
		System.out.println("");
		System.out.println("ACTION :");
		System.out.println("  INIT              Toutes les fiches sont retéléchargées sur doris.ffessm.fr et retraitées pour créer la base (images comprises)");
		System.out.println("  NODWNLD dossier   Pas de téléchargement, travail sur un dossier de référznce (utile en dév.)");
		System.out.println("  UPDATE            En plus des nouvelles fiches, définitions et intervenants, on retélécharge les fiches qui ont changées de statut");
		System.out.println("  CDDVD_MED        	Comme UPDATE + Permet de télécharger les photos manquantes et de créer un dossier de la taille d'environ un CD (images en qualité inter.) dans lequel il est possible de naviguer sans connection internet");
		System.out.println("  CDDVD_HI        	Comme UPDATE + Permet de télécharger les photos manquantes et de créer un dossier avec toutes les images disponibles dans lequel il est possible de naviguer sans connection internet (peut servir de sauvegarde du site)");
		System.out.println("  TEST          	Pour les développeurs");
		System.out.println("  DB_TO_ANDROID     Déplace la base du Prefetch vers DorisAndroid");
		System.out.println("  DB_IMAGE_UPGRADE  remplace les images de la base courante par celles du nouveau site Doris V4");
		System.out.println("  DWNLD_TO_REF      Déplace fichiers de html vers html_ref et ceux de images vers images_ref");
		System.out.println("  ERASE_ALL         Efface tout le contenu de run");
		System.out.println("  ERASE_BUT_REF     Efface le contenu de run sauf html_ref et images_ref");
		log.debug("help() - Fin");
	}
	

	private void renommageDossiers(ActionKind inAction) {
		log.debug("renommageDossiers() - Début");
		
		log.debug("renommageDossiers() - Action : " + inAction.toString());
		
		log.debug("renommageDossiers() - Dossier racine : " + PrefetchConstants.DOSSIER_RACINE);
		log.debug("renommageDossiers() - Dossier html : " + PrefetchConstants.DOSSIER_HTML);
		log.debug("renommageDossiers() - Dossier de la base : " + PrefetchConstants.DOSSIER_DATABASE);
		log.debug("renommageDossiers() - Fichier de la Base : " + PrefetchConstants.DATABASE_URL);
		
		// Calcul suffixe de renommage
		Date maintenant = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss", Locale.US);
		String suffixe = sdf.format(maintenant);
		
		// Si le dossier principal de travail ( ./run )  n'existe pas, on le créé
		File dossierRacine = new File(PrefetchConstants.DOSSIER_RACINE);
		if ( dossierRacine.exists() ) {
	
			// Si les dossiers download (html et img) et résultats existent déjà, ils sont renommés
			// avant d'être recréé vide
	
			// Le dossier des fichiers html téléchargés
			if(inAction == ActionKind.INIT || inAction == ActionKind.UPDATE 
					|| inAction == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ){
				File dossierHtml = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML);
				if (dossierHtml.exists()){
					File dossierHtmlNew = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML +"_"+ suffixe);
					if(dossierHtml.renameTo(dossierHtmlNew)){
						log.info("Sauvegarde du dossier download : " + dossierHtmlNew.getAbsolutePath());
					}else{
						log.error("Echec renommage du dossier download : " + dossierHtml.getAbsolutePath());
						System.exit(1);
					}
				}
			}
			
			// Le dossier des images téléchargées
			if( inAction == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ){
				File dossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES);
				if (dossierImages.exists()){
					File dossierImagesNew = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES +"_"+ suffixe);
					if(dossierImages.renameTo(dossierImagesNew)){
						log.info("Sauvegarde du dossier download : " + dossierImagesNew.getAbsolutePath());
					}else{
						log.error("Echec renommage du dossier download : " + dossierImagesNew.getAbsolutePath());
						System.exit(1);
					}
				}
			}
			
			// Le dossier du CD ou DVD
			if ( inAction == ActionKind.CDDVD_MED ) {
				
				File dossierCD = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CDDVD_MED);
				if (dossierCD.exists()){
					File dossierCDNew = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CDDVD_MED +"_"+ suffixe);
					if(dossierCD.renameTo(dossierCDNew)){
						log.info("Sauvegarde du dossier CD : " + dossierCDNew.getAbsolutePath());
					}else{
						log.error("Echec renommage du dossier CD : " + dossierCDNew.getAbsolutePath());
						System.exit(1);
					}
				}
			}
			
			if ( inAction == ActionKind.CDDVD_HI ) {
				File dossierDVD = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CDDVD_HI);
				if (dossierDVD.exists()){
					File dossierDVDNew = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CDDVD_HI +"_"+ suffixe);
					if(dossierDVD.renameTo(dossierDVDNew)){
						log.info("Sauvegarde du dossier DVD : " + dossierDVDNew.getAbsolutePath());
					}else{
						log.error("Echec renommage du dossier DVD : " + dossierDVDNew.getAbsolutePath());
						System.exit(1);
					}
				}

			}
			
			// Le fichier de la base de données
			if(inAction == ActionKind.INIT || inAction == ActionKind.UPDATE || inAction == ActionKind.NODWNLD
					|| inAction == ActionKind.CDDVD_MED || inAction == ActionKind.CDDVD_HI ) {
	
				String dataBaseName = PrefetchConstants.DATABASE_URL.substring(PrefetchConstants.DATABASE_URL.lastIndexOf(":")+1, PrefetchConstants.DATABASE_URL.lastIndexOf(".") );
				log.debug("dataBaseName : " + dataBaseName);
				File fichierDB= new File(dataBaseName+".db");
				if (fichierDB.exists()){
					File fichierDBNew = new File(dataBaseName+"_"+suffixe+".db");
					if(fichierDB.renameTo(fichierDBNew)){
						log.info("Sauvegarde du fichier de la base : " + fichierDB.getAbsolutePath());
					}else{
						log.error("Echec renommage du fichier de la base en : " + fichierDBNew.getAbsolutePath());
						System.exit(1);
					}
				}
			}
		}
		log.debug("renommageDossiers() - Fin");
	}


	private void creationDossiers(ActionKind inAction) {
		log.debug("creationDossiers() - Début");
		
		log.debug("creationDossiers() - Action : " + inAction.toString());
		
		log.debug("creationDossiers() - Dossier racine : " + PrefetchConstants.DOSSIER_RACINE);
		log.debug("creationDossiers() - Dossier html : " + PrefetchConstants.DOSSIER_HTML);
		log.debug("creationDossiers() - Dossier de la base : " + PrefetchConstants.DOSSIER_DATABASE);
		log.debug("creationDossiers() - Fichier de la Base : " + PrefetchConstants.DATABASE_URL);
				
		// Si le dossier principal de travail ( ./run/ )  n'existe pas, on le crée
		File dossierRacine = new File(PrefetchConstants.DOSSIER_RACINE);
		if (dossierRacine.mkdirs()) {
			log.info("Création du dossier : " + dossierRacine.getAbsolutePath());
		}
		// idem dossier de la Base ( ./run/database/ )
		File dossierDB = new File(PrefetchConstants.DOSSIER_DATABASE);
		if (dossierDB.mkdirs()) {
			log.info("Création du dossier : " + dossierDB.getAbsolutePath());
		}
		File dossierTests = new File(PrefetchConstants.DOSSIER_TESTS);
		if (dossierTests.mkdirs()) {
			log.info("Création du dossier : " + dossierTests.getAbsolutePath());
		}

		// Le dossier des fichiers html téléchargés ( ./run/html/ )
		if(inAction == ActionKind.INIT || inAction == ActionKind.UPDATE  || inAction == ActionKind.CDDVD_MED
				|| inAction == ActionKind.CDDVD_HI ){
			File dossierHtml = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML);

			if (dossierHtml.mkdir()) {
				log.info("Création du dossier download : " + dossierHtml.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier download : " + dossierHtml.getAbsolutePath());
				System.exit(1);
			}
		}
		
		// Le dossier des images téléchargées ( ./run/images/ )
		if( inAction == ActionKind.CDDVD_MED || inAction == ActionKind.CDDVD_HI ){
			File dossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES);

			if (dossierImages.mkdir()) {
				log.info("Création du dossier download : " + dossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier download : " + dossierImages.getAbsolutePath());
				System.exit(1);
			}
			
			File sousDossierImages;
			
			// ( ./run/images/icones/ )
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_ICONES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du sous-dossier download : " + sousDossierImages.getAbsolutePath());
				System.exit(1);
			}
			
			// ( ./run/images/icones/vignettes_fiches/ )
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_VIGNETTES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(1);
			}
			
			// ( ./run/images/icones/medium_res_images_fiches/ )
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_MED_RES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(1);
			}
			
			// ( ./run/images/icones/hi_res_images_fiches/ )
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_HI_RES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(1);
			}
		}
		
		// Le dossier du CD ou DVD ( ./run/cd/ )
		if( inAction == ActionKind.CDDVD_MED ){
			
			File dossierCD = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CDDVD_MED);

			if (dossierCD.mkdir()) {
				log.info("Création du dossier CD : " + dossierCD.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier CD : " + dossierCD.getAbsolutePath());
				System.exit(1);
			}
		}
		
		if( inAction == ActionKind.CDDVD_HI ){

			File dossierDVD = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CDDVD_HI);

			if (dossierDVD.mkdir()) {
				log.info("Création du dossier DVD : " + dossierDVD.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier DVD : " + dossierDVD.getAbsolutePath());
				System.exit(1);
			}

		}
	}
	
	
	private void creationDossiersRef(ActionKind inAction) {
		log.debug("creationDossiersRef() - Début");
		
		log.debug("creationDossiersRef() - Action : " + inAction.toString());
		
		log.debug("creationDossiersRef() - Dossier racine : " + PrefetchConstants.DOSSIER_RACINE);
		log.debug("creationDossiersRef() - Dossier html : " + PrefetchConstants.DOSSIER_HTML_REF);
		
		//	Vérification que le dossier html Référence existe ( ./run/html_ref/ )
		if( inAction == ActionKind.NODWNLD || inAction == ActionKind.UPDATE || inAction == ActionKind.CDDVD_MED
				|| inAction == ActionKind.CDDVD_HI || inAction == ActionKind.DWNLD_TO_REF ){
			File dossierReference = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF);
			if (dossierReference.mkdir()) {
				log.info("Création du dossier : " + dossierReference.getAbsolutePath());
			}
		}
		
		//	Vérification que les dossiers Images Référence existe
		if( inAction == ActionKind.CDDVD_MED || inAction == ActionKind.CDDVD_HI || inAction == ActionKind.DWNLD_TO_REF ){
			
			// ( ./run/images_ref/ )
			File dossierImagesReference = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF);
			if (dossierImagesReference.mkdir()) {
				log.info("Création du dossier : " + dossierImagesReference.getAbsolutePath());
			}
			
			File sousDossierImages;
			
			// ( ./run/images_ref/icones/ )
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/" + PrefetchConstants.SOUSDOSSIER_ICONES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier : " + sousDossierImages.getAbsolutePath());
			}
			
			// ( ./run/images_ref/vignettes_fiches/ )
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/" + PrefetchConstants.SOUSDOSSIER_VIGNETTES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier : " + sousDossierImages.getAbsolutePath());
			}
			
			// ( ./run/images_ref/medium_res_images_fiches/ )
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/" + PrefetchConstants.SOUSDOSSIER_MED_RES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier : " + sousDossierImages.getAbsolutePath());
			}
			
			// ( ./run/images_ref/hi_res_images_fiches/ )
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/" + PrefetchConstants.SOUSDOSSIER_HI_RES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier : " + sousDossierImages.getAbsolutePath());
			}
			
		}
	
		
		log.debug("creationDossiersRef() - Fin");
	}

}
