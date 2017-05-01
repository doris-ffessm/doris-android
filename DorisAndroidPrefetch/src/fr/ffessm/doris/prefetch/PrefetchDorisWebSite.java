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

package fr.ffessm.doris.prefetch;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import com.google.api.client.auth.oauth2.Credential;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.DataBase_Outils;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPIConnexionHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisOAuth2ClientCredentials;
import fr.ffessm.doris.prefetch.ezpublish.JsonToDB;
import fr.ffessm.doris.prefetch.ezpublish.ObjNameNodeId;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;

public class PrefetchDorisWebSite {

	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchDorisWebSite.class);
	
	// Nombre maximum de fiches traitées (--max=K permet de changer cette valeur)
	private int nbMaxFichesATraiter = PrefetchConstants.nbMaxFichesTraiteesDef;
    private int nbFichesParRequetes = 50;

	private ActionKind action;

	public enum ActionKind {
		INIT,
		TEST,
		DB_TO_ANDROID,
        V4_TO_ANDROID,
        TEST_CONNECTION_V4,
		DB_IMAGE_UPGRADE,
		ERASE_ALL
	}
	
	ConnectionSource connectionSource = null;
	DorisDBHelper dbContext = null;
	DataBase_Outils outilsBase = null;

	public static void main(String[] args) throws Exception {

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

		} else if ( action == ActionKind.DB_TO_ANDROID ) {
			
			dbToAndroidAction();

		} else if ( action == ActionKind.V4_TO_ANDROID ) {

            dbV4ToAndroidAction();

        } else if ( action == ActionKind.TEST_CONNECTION_V4 ) {

            testConnection();

        } else if ( action == ActionKind.DB_IMAGE_UPGRADE ) {

            dbImageV4UpgradeAction();

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

		// - - - Base de Données - - -
		PrefetchDBTools prefetchDBTools = new PrefetchDBTools();
		prefetchDBTools.initializeSQLite(PrefetchConstants.DATABASE_URL);
		connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL);
		dbContext = prefetchDBTools.setupDatabase(connectionSource);
		prefetchDBTools.databaseInitialisation(connectionSource);
		outilsBase = new DataBase_Outils(dbContext);

        /*
		PrefetchGlossaire glossaire = new PrefetchGlossaire(dbContext, connectionSource, ActionKind.INIT, nbMaxFichesATraiter);
		if ( glossaire.prefetch() == -1 ) {
			log.debug("doMain() - Erreur Glossaire" );
			System.exit(1);
		}
		*/
		log.debug("doMain() - Fin TEST");
	}

    private void testConnection() throws Exception {
        log.debug("testConnection() - Début");

        Credential credent = DorisAPIConnexionHelper.authorizeViaWebPage(DorisOAuth2ClientCredentials.getUserId());

        DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper = new DorisAPI_JSONTreeHelper(credent);
        DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper(credent);

        log.debug("testConnection() - credent : " + credent.toString());
        log.debug("testConnection() - credent : " + credent.getAccessToken().toString());

        log.debug("testConnection() - Fin");
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

    private void dbV4ToAndroidAction() throws Exception{
        log.debug("dbV4ToAndroidAction() - Début Création de la Base pour Doris V4");

        // turn our static method into an instance of Main
        //if (testDev == true) BasicConfigurator.configure();

        // Vérification, Création, Sauvegarde des dossiers de travail
        renommageDossiers(action);
        creationDossiers(action);

        // - - - Base de Données - - -
        PrefetchDBTools prefetchDBTools = new PrefetchDBTools();

        // create empty DB and initialize it for Android
        prefetchDBTools.initializeSQLite(PrefetchConstants.DATABASE_URL);

        // create our data-source for the database
        connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL);

        // setup our database and DAOs
        dbContext = prefetchDBTools.setupDatabase(connectionSource);

        prefetchDBTools.databaseInitialisation(connectionSource);

        try {
            // - - - Groupes - - -
            // Récupération de la liste des groupes sur le site de DORIS
            log.debug("dbV4ToAndroidAction() - - - Groupes - - -");
            int nbMaxGroupesATraiter = 2000;
            if (nbMaxGroupesATraiter > nbMaxFichesATraiter ) nbMaxGroupesATraiter = nbMaxFichesATraiter;
            PrefetchGroupes groupes = new PrefetchGroupes(dbContext, connectionSource, nbMaxGroupesATraiter, nbFichesParRequetes);
            if ( groupes.prefetchV4() == -1 ) {
                log.debug("doMain() - Erreur Groupes");
                System.exit(1);
            }

            // - - - Participants - - -
            log.debug("dbV4ToAndroidAction() - - - Participants - - -");
            int nbMaxParticipantsATraiter = 2000;
            if (nbMaxParticipantsATraiter > nbMaxFichesATraiter ) nbMaxParticipantsATraiter = nbMaxFichesATraiter;
            PrefetchIntervenants intervenants = new PrefetchIntervenants(dbContext, connectionSource, nbMaxParticipantsATraiter, nbFichesParRequetes);
            if ( intervenants.prefetchV4() == -1 ) {
                log.debug("doMain() - Erreur Intervenants" );
                System.exit(1);
            }

            // - - - Glossaire - - -
            log.debug("dbV4ToAndroidAction() - - - Glossaire - - -");
            int nbMaxTermesATraiter = 2000;
            if (nbMaxTermesATraiter > nbMaxFichesATraiter ) nbMaxTermesATraiter = nbMaxFichesATraiter;
            PrefetchGlossaire glossaire = new PrefetchGlossaire(dbContext, connectionSource, nbMaxTermesATraiter, nbFichesParRequetes);
            if ( glossaire.prefetchV4() == -1 ) {
                log.debug("doMain() - Erreur Glossaire" );
                System.exit(1);
            }

            // - - - Bibliographie - - -
            log.debug("dbV4ToAndroidAction() - - - Bibliographie - - -");
			int nbMaxTitresATraiter = 2000;
            if (nbMaxTitresATraiter > nbMaxFichesATraiter ) nbMaxTitresATraiter = nbMaxFichesATraiter;
            PrefetchBibliographies bibliographies = new PrefetchBibliographies(dbContext, connectionSource, nbMaxTitresATraiter, nbFichesParRequetes);
            if ( bibliographies.prefetchV4() == -1 ) {
                log.debug("doMain() - Erreur Bibliographies" );
                System.exit(1);
            }

            // - - - Mise à jour des zones géographiques - - -
            log.debug("dbV4ToAndroidAction() - - - Mise à jour des zones géographiques - - -");
            PrefetchZonesGeographiques zonesGeographiques = new PrefetchZonesGeographiques(dbContext, connectionSource, nbMaxFichesATraiter);
            if ( zonesGeographiques.prefetchV4() == -1 ) {
                log.debug("doMain() - Erreur Mise à jour des zones géographiques" );
                System.exit(1);
            }

            // - - - Liste des Fiches - - -
            log.debug("dbV4ToAndroidAction() - - - Liste des Fiches - - -");
            int nbMaxEspecesATraiter = 6000;
            if (nbMaxEspecesATraiter > nbMaxFichesATraiter ) nbMaxEspecesATraiter = nbMaxFichesATraiter;
            PrefetchFiches listeFiches = new PrefetchFiches(dbContext, connectionSource, nbMaxEspecesATraiter, nbFichesParRequetes);
            if ( listeFiches.prefetchV4() == -1 ) {
                log.debug("doMain() - Erreur Liste des Fiches" );
                System.exit(1);
            }

            // - - - Enregistrement Date génération Base - - -
            log.debug("dbV4ToAndroidAction() - - - Enregistrement Date génération Base - - -");
            Date date = new Date();
            SimpleDateFormat ft =  new SimpleDateFormat ("dd/MM/yyyy  HH:mm", Locale.US);
            dbContext.dorisDB_metadataDao.create(new DorisDB_metadata(ft.format(date),""));


        } finally {
            // destroy the data source which should close underlying connections
            log.debug("doMain() - Fermeture Base");
            if (connectionSource != null) {
                connectionSource.close();
            }
        }



        log.debug("dbV4ToAndroidAction() - Fin Création de la Base pour Doris V4");
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

				List<ObjNameNodeId> nodeIds = dorisAPI_JSONTreeHelper.getFichesNodeIds(nbFichesParRequetes, nbFichesParRequetes * i);
			
			
				for (ObjNameNodeId especeNodeId : nodeIds) {
					count++;
					if( count > nbMaxFichesATraiter ){
						log.debug("doMain() - nbMaxFichesATraiter atteint");
						i=9999;
						break;
					}
					 
					// Référence de l'Espèce dans le message JSON 
					Espece especeJSON = dorisAPI_JSONDATABindingHelper.getEspeceFieldsFromNodeId(especeNodeId.getNodeId());
					if (especeJSON != null) {
                        String especeJSONReferenceId = especeJSON.getFields().getReference().getValue();

                        log.debug(" nodeId=" + especeNodeId + ", dorisId=" + especeJSONReferenceId + ", imagesNodeIds=" + especeJSON.getFields().getImages().getValue());

                        List<Image> imageData = new ArrayList<Image>();


                        // itère sur les images trouvées pour cette fiche
                        for (String possibleImageId : especeJSON.getFields().getImages().getValue().split("\\|")) {
                            try {
                                int imageId = Integer.parseInt(possibleImageId.replaceAll("&", ""));
                                // récupère les données associées à l'image
                                Image image = dorisAPI_JSONDATABindingHelper.getImageFromImageId(imageId);
                                if (image != null) imageData.add(image);

                            } catch (NumberFormatException nfe) {
                                // ignore les entrées invalides
                            }
                        }

                        log.debug(" ficheDao.queryBuilder() =" + dbContext.ficheDao.queryBuilder().where().eq("numeroFiche", especeJSONReferenceId).getStatement());

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
                                            for (PhotoFiche photoFiche : listePhotoFiche) {

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

                            log.error("! ! ! Fiche non trouvée : " + especeJSONReferenceId + " ! ! ! !");

                        }
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
				
		log.debug("dbImageV4UpgradeAction() - Début upgrade images pour Doris V4");
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
		System.out.println("  -h, --help        Afficher cette aide");
		System.out.println("  -d, --debug       Messages detinés aux développeurs de cette application");
		System.out.println("  -v, --verbose     Messages permettant de suivre l'avancé des traitements");
		System.out.println("  -s, --silence     Aucune sortie, même pas les erreurs");
		System.out.println("");
		System.out.println("ACTION :");
		System.out.println("  INIT              Toutes les fiches sont retéléchargées sur doris.ffessm.fr et retraitées pour créer la base (images comprises)");
		System.out.println("  TEST          	Pour les développeurs");
		System.out.println("  DB_TO_ANDROID     Déplace la base du Prefetch vers DorisAndroid");
        System.out.println("  V4_TO_ANDROID     Création de la base de données pour l'appli. Android à partir du site Doris V4");
		System.out.println("  DB_IMAGE_UPGRADE  remplace les images de la base courante par celles du nouveau site Doris V4");
		System.out.println("  ERASE_ALL         Efface tout le contenu de run");
		log.debug("help() - Fin");
	}

	private void renommageDossiers(ActionKind inAction) {
		log.debug("renommageDossiers() - Début");
		
		log.debug("renommageDossiers() - Action : " + inAction.toString());
		
		log.debug("renommageDossiers() - Dossier racine : " + PrefetchConstants.DOSSIER_RACINE);
		log.debug("renommageDossiers() - Dossier de la base : " + PrefetchConstants.DOSSIER_DATABASE);
		log.debug("renommageDossiers() - Fichier de la Base : " + PrefetchConstants.DATABASE_URL);
		
		// Calcul suffixe de renommage
		Date maintenant = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss", Locale.US);
		String suffixe = sdf.format(maintenant);
		
		// Si le dossier principal de travail ( ./run )  n'existe pas, on le créé
		File dossierRacine = new File(PrefetchConstants.DOSSIER_RACINE);
		if ( dossierRacine.exists() ) {

            // Le fichier de la base de données
            if(inAction == ActionKind.INIT || inAction == ActionKind.V4_TO_ANDROID) {

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

	}

}
