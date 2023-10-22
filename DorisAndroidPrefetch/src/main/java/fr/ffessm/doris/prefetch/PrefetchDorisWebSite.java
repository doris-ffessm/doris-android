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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Level;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.DataBase_Outils;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPIConnexionHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPIHTTPHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
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

    private boolean copyBase = false;
	
	ConnectionSource connectionSource = null;
	DorisDBHelper dbContext = null;

	public static void main(String[] args) {
		try {
			new PrefetchDorisWebSite().doMain(args);
		} catch (Exception e) {
			log.error( e.getMessage() , e);
			System.exit(1);
		}
		
	}

	private void doMain(String[] args) throws Exception {
		log.debug("doMain() - Début");

		Options options = createOptions();

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);

			// manage options
			if (cmd.hasOption("debug")) {
				org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
				logger.setLevel(Level.DEBUG);
			}
			if (cmd.hasOption("quiet")) {
				org.apache.log4j.Logger logger = org.apache.log4j.Logger.getRootLogger();
				logger.setLevel(Level.WARN);
			}
			this.copyBase = cmd.hasOption("copyBase");

			if(cmd.hasOption("M")) {
				String nbFichesStr = cmd.getOptionValue("M");
				try {
					nbMaxFichesATraiter = Integer.parseInt(nbFichesStr);
					log.debug("checkArgs() - nbMaxFichesTraitees : " + nbMaxFichesATraiter);
				} catch(NumberFormatException e) {
					//help();
					log.error("incorrect value for option -M : " + nbFichesStr+ " expecting an integer");
					System.exit(-1);
				}
			}

			// start concrete commands

			// token management (interactive or via option
			if(!cmd.hasOption("noFetch")) {
				if ( !(cmd.hasOption("interactive") || cmd.hasOption("token"))) {
					System.out.println("Missing method to obtain token : interactive or accessToken options, this is required to fetch data from doris.ffessm.fr");
					help(options);
					System.exit(-1);
				}
			} else {
				log.info("NoFetch enabled");
			}

			if(cmd.hasOption("interactive") && cmd.hasOption("token")) {
				System.out.println("interactive or accessToken options must not be used together");
				help(options);
				System.exit(-1);
			}
			if(cmd.hasOption("interactive")){
				getInteractiveAccessToken();
			}
			if(cmd.hasOption("token")){
				DorisOAuth2ClientCredentials.API_ACCESSTOKEN = cmd.getOptionValue("token");
			}

			// main fetch action
			if(! cmd.hasOption("noFetch")) {
				webToDBAction();
			}




		} catch (UnrecognizedOptionException | MissingOptionException parseException) {
			System.out.println(parseException.getMessage());
			help(options);

			log.info("Nb Call to DorisAPIHTTPHelper: "+DorisAPIHTTPHelper.getNbHttpCall());
			log.debug("doMain() - Fin");
			System.exit(-1);
		}
		// old way
	/*	action = checkArgs(args);
		log.info("action : " + action);
		log.info("Nb. Fiches Max : " + nbMaxFichesATraiter);
				
		
		// - - - - - - - - - - - -
		// - - - Test  - - - - - -
		if ( action == ActionKind.TEST ) {
			
			testAction();

		} else if ( action == ActionKind.DB_TO_ANDROID ) {
			
			dbToAndroidAction();

		} else if ( action == ActionKind.WEB_TO_DB) {

            webToDBAction();

        } else if ( action == ActionKind.TEST_CONNECTION_V4 ) {

            testConnection();

        } else if ( action == ActionKind.DB_IMAGE_UPGRADE ) {

            dbImageV4UpgradeAction();

		} else if (action == ActionKind.TEST_COLLECT_GROUP) {
			testCollectGroupAction();
		} else if ( action == ActionKind.ERASE_ALL ) {
			
			eraseAllAction();
		}
*/
		log.info("Nb Call to DorisAPIHTTPHelper: "+DorisAPIHTTPHelper.getNbHttpCall());
		log.debug("doMain() - Fin");
	}

	private Options createOptions() {
		// create Options object
		Options options = new Options();

		options.addOption(new Option("help", "print this message"));
		options.addOption(new Option("debug", false, "print debugging information"));
		options.addOption(new Option("quiet", false, "be extra quiet"));
		options.addOption(new Option( "noFetch", false, "Do not fetch data from doris.ffessm.fr, useful combined with interactive option in order to get an accessToken"));
		options.addOption(new Option( "i","interactive", false, "prompt for user action through a web page to connect to the site and get an accessToken"));
		options.addOption(new Option("d","delete-previous", false, "delete previous DB and workon a fresh data base"));


		options.addOption(new Option("copyBase", "save existing base and work on a fresh empty base"));
		Option maxFiche    = Option.builder("M")
				.longOpt("max")
				.argName("nbFiches")
				.required(false)
				.hasArg()
				.desc("max number of Fiche that will be retrieved on this call")
				.build();
		options.addOption(maxFiche);

		Option accessToken    = Option.builder("t")
				.longOpt("token")
				.argName("accessToken")
				.required(false)
				.hasArg()
				.desc("Use access token from a previous authentication")
				.build();
		options.addOption(accessToken);


		Option userId    = Option.builder("u")
				.longOpt("user")
				.argName("userID")
				.required(true)
				.hasArg()
				.desc("UserId (email) for the doris.ffessm.fr web site")
				.build();
		options.addOption(userId);


		return options;
	}
/*
	private void testAction() throws Exception{
		log.debug("doMain() - Début TEST");
		
		// Vérification, Création, Sauvegarde des dossiers de travail
		//renommageDossiers(ActionKind.WEB_TO_DB);
		//creationDossiers(ActionKind.WEB_TO_DB);

		// - - - Base de Données - - -
		PrefetchDBTools prefetchDBTools = new PrefetchDBTools();
		prefetchDBTools.initializeSQLite(PrefetchConstants.DATABASE_URL);
		connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL);
		dbContext = prefetchDBTools.setupDatabase(connectionSource);
		prefetchDBTools.databaseInitialisation(connectionSource);
		outilsBase = new DataBase_Outils(dbContext);

*/

        /*
		PrefetchGlossaire glossaire = new PrefetchGlossaire(dbContext, connectionSource, ActionKind.INIT, nbMaxFichesATraiter);
		if ( glossaire.prefetch() == -1 ) {
			log.debug("doMain() - Erreur Glossaire" );
			System.exit(1);
		}
		*/
/*
		// - - - Liste des Fiches - - -
		log.debug("dbV4ToAndroidAction() - - - Liste des Fiches - - -");
		int nbMaxEspecesATraiter = 100000;
		if (nbMaxEspecesATraiter > nbMaxFichesATraiter ) nbMaxEspecesATraiter = nbMaxFichesATraiter;
		PrefetchFiches listeFiches = new PrefetchFiches(dbContext, connectionSource, nbMaxEspecesATraiter, nbFichesParRequetes);
		if ( listeFiches.prefetch() == -1 ) {
			log.debug("doMain() - Erreur Liste des Fiches" );
			throw new RuntimeException("Erreur Liste des Fiches");
		}

		log.debug("doMain() - Fin TEST");
	}
*/
    private void getInteractiveAccessToken() throws Exception {
        log.debug("getAccessToken() - Début");

        Credential credent = DorisAPIConnexionHelper.authorizeViaWebPage(DorisOAuth2ClientCredentials.getUserId());

        log.info("credent : " + credent.toString());
        log.info("accessToken : " + credent.getAccessToken());
        DorisOAuth2ClientCredentials.API_ACCESSTOKEN = credent.getAccessToken();

        log.debug("getAccessToken() - Fin");
    }

	private void dbToAndroidAction(){
		log.debug("doMain() - Début Déplacement Base");

		// Consiste au déplacement du fichier de la base du run vers assets
		String dataBaseRunString = PrefetchConstants.DATABASE_URL.substring(PrefetchConstants.DATABASE_URL.lastIndexOf(":")+1);
		log.debug("dataBase : " + dataBaseRunString);

		String dataBaseName = dataBaseRunString.substring(dataBaseRunString.lastIndexOf("/")+1);
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
			throw new RuntimeException("Invalid base");
		}

		log.debug("doMain() - Fin Déplacement Base");
	}

    private void webToDBAction() throws Exception{
        log.debug("webToDBAction() - Début Création/update de la base à partir de doris.ffessm.fr");

        // turn our static method into an instance of Main
        //if (testDev == true) BasicConfigurator.configure();

        // Vérification, Création, Sauvegarde des dossiers de travail
		if(this.copyBase) {
			renommageDossiers();
		}

		creationDossiers();

		// - - - Base de Données - - -
		PrefetchDBTools prefetchDBTools = new PrefetchDBTools();
		String dataBaseFileName = PrefetchConstants.DATABASE_URL.substring(PrefetchConstants.DATABASE_URL.lastIndexOf(":")+1, PrefetchConstants.DATABASE_URL.lastIndexOf(".") );
		File dbFile = new File(dataBaseFileName+".db");
		if(dbFile.exists()) {
			log.info("Open existing DB "+PrefetchConstants.DATABASE_URL);
			// create our data-source for the database
			connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL);

			// setup our database and DAOs
			dbContext = prefetchDBTools.setupDatabase(connectionSource);
		} else {
			log.info("Create new DB "+PrefetchConstants.DATABASE_URL);

			// create empty DB and initialize it for Android
			prefetchDBTools.initializeSQLite(PrefetchConstants.DATABASE_URL);

			// create our data-source for the database
			connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL);

			// setup our database and DAOs
			dbContext = prefetchDBTools.setupDatabase(connectionSource);

			prefetchDBTools.databaseInitialisation(connectionSource);
		}
        try {
            // - - - Groupes - - -
            // Récupération de la liste des groupes sur le site de DORIS
            log.info("webToDBAction() - - - Groupes - - -");
            int nbMaxGroupesATraiter = 100000;
            // if (nbMaxGroupesATraiter > nbMaxFichesATraiter ) nbMaxGroupesATraiter = nbMaxFichesATraiter;

            PrefetchGroupes groupes = new PrefetchGroupes(dbContext, connectionSource, nbMaxGroupesATraiter, nbFichesParRequetes);
            if ( groupes.prefetchFromModalDialog() == -1 ) {
                log.debug("Erreur Groupes");
				throw new RuntimeException("Error in PrefetchGroupes");
            }

            // - - - Participants - - -
            log.info("webToDBAction() - - - Participants - - -");
            int nbMaxParticipantsATraiter = 100000;
            // if (nbMaxParticipantsATraiter > nbMaxFichesATraiter ) nbMaxParticipantsATraiter = nbMaxFichesATraiter;
            PrefetchIntervenants intervenants = new PrefetchIntervenants(dbContext, connectionSource, nbMaxParticipantsATraiter, nbFichesParRequetes);
            if ( intervenants.prefetch() == -1 ) {
				throw new RuntimeException("Error in PrefetchIntervenants");
            }

            // - - - Glossaire - - -
            log.info("webToDBAction() - - - Glossaire - - -");
            int nbMaxTermesATraiter = 100000;
            // if (nbMaxTermesATraiter > nbMaxFichesATraiter ) nbMaxTermesATraiter = nbMaxFichesATraiter;
            PrefetchGlossaire glossaire = new PrefetchGlossaire(dbContext, connectionSource, nbMaxTermesATraiter, nbFichesParRequetes);
            if ( glossaire.prefetch() == -1 ) {
				throw new RuntimeException("Error in Glossaire");
            }

            // - - - Bibliographie - - -
            log.info("webToDBAction() - - - Bibliographie - - -");
			int nbMaxTitresATraiter = 100000;
            if (nbMaxTitresATraiter > nbMaxFichesATraiter ) nbMaxTitresATraiter = nbMaxFichesATraiter;
            PrefetchBibliographies bibliographies = new PrefetchBibliographies(dbContext, connectionSource, nbMaxTitresATraiter, nbFichesParRequetes);
            if ( bibliographies.prefetch() == -1 ) {
				throw new RuntimeException("Error in PrefetchBibliographies");
            }

            // - - - Mise à jour des zones géographiques - - -
            log.info("webToDBAction() - - - Mise à jour des zones géographiques - - -");
            PrefetchZonesGeographiques zonesGeographiques = new PrefetchZonesGeographiques(dbContext, connectionSource, nbMaxFichesATraiter);
            if ( zonesGeographiques.prefetchV4() == -1 ) {
				throw new RuntimeException("Error in PrefetchZonesGeographiques ");
            }

            // - - - Liste des Fiches - - -
            log.info("webToDBAction() - - - Liste des Fiches - - -");
            int nbMaxEspecesATraiter = 100000;
            if (nbMaxEspecesATraiter > nbMaxFichesATraiter ) nbMaxEspecesATraiter = nbMaxFichesATraiter;
            PrefetchFiches listeFiches = new PrefetchFiches(dbContext, connectionSource, nbMaxEspecesATraiter, nbFichesParRequetes);
            if ( listeFiches.prefetch() == -1 ) {
				throw new RuntimeException("Error in PrefetchFiches ");
            }

            // - - - Enregistrement Date génération Base - - -
            log.debug(" - - - Enregistrement Date génération Base - - -");
            // delete old entries if any
			List<DorisDB_metadata> metadataList = dbContext.dorisDB_metadataDao.queryForAll();
			for (DorisDB_metadata metadata : metadataList ) {
				dbContext.dorisDB_metadataDao.delete(metadata);
			}
            Date date = new Date();
            SimpleDateFormat ft =  new SimpleDateFormat ("dd/MM/yyyy  HH:mm", Locale.US);
            dbContext.dorisDB_metadataDao.create(new DorisDB_metadata(ft.format(date),""));


        } finally {
            // destroy the data source which should close underlying connections
            log.debug("Fermeture Base");
            if (connectionSource != null) {
                connectionSource.close();
            }
        }



        log.debug("webToDBAction() - Fin ");
    }



	/**
     * Display help
     **/	
	private static void help(Options options){

		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar PrefetchDorisWebSite.jar", "header", options, "footer", true);

	}

	private void renommageDossiers() {
		log.debug("renommageDossiers() - Début");
		
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

			String dataBaseName = PrefetchConstants.DATABASE_URL.substring(PrefetchConstants.DATABASE_URL.lastIndexOf(":")+1, PrefetchConstants.DATABASE_URL.lastIndexOf(".") );
			log.debug("dataBaseName : " + dataBaseName);
			File fichierDB= new File(dataBaseName+".db");
			if (fichierDB.exists()){
				File fichierDBNew = new File(dataBaseName+"_"+suffixe+".db");
				if(fichierDB.renameTo(fichierDBNew)){
					log.info("Sauvegarde du fichier de la base : " + fichierDB.getAbsolutePath());
				}else{
					throw new RuntimeException("Echec renommage du fichier de la base en : " + fichierDBNew.getAbsolutePath());
				}
			}
		}
		log.debug("renommageDossiers() - Fin");
	}

	private void creationDossiers() {
		log.debug("creationDossiers() - Début");

		// Si le dossier principal de travail ( ./run/ )  n'existe pas, on le crée
		File dossierRacine = new File(PrefetchConstants.DOSSIER_RACINE);
		if (dossierRacine.mkdirs()) {
			log.info("Création du dossier : " + dossierRacine.getAbsolutePath());
		} else {
			log.debug("Dossier racine : " + PrefetchConstants.DOSSIER_RACINE);
		}
		// idem dossier de la Base ( ./run/database/ )
		File dossierDB = new File(PrefetchConstants.DOSSIER_DATABASE);
		if (dossierDB.mkdirs()) {
			log.info("Création du dossier : " + dossierDB.getAbsolutePath());
		} else {
			log.debug("Dossier datbase : " + PrefetchConstants.DOSSIER_DATABASE);
		}
		File dossierTests = new File(PrefetchConstants.DOSSIER_TESTS);
		if (dossierTests.mkdirs()) {
			log.info("Création du dossier : " + dossierTests.getAbsolutePath());
		} else {
			log.debug("Fichier de la Base : " + PrefetchConstants.DATABASE_URL);
		}

	}

}
