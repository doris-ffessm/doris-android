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
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.SqlExceptionUtil;
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
import fr.ffessm.doris.android.sitedoris.DataBase_Outils;
import fr.ffessm.doris.prefetch.PrefetchConstants;

public class PrefetchDorisWebSite {

	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchDorisWebSite.class);
	
	// Nombre maximum de fiches traitées (--max=K permet de changer cette valeur)
	private static int nbMaxFichesATraiter = PrefetchConstants.nbMaxFichesTraiteesDef;
	public static ActionKind action;
	
	public enum ActionKind {
		INIT,
		UPDATE,
		NODWNLD,
		CDDVD,
		TEST,
		PROMOTE_AS_REF,
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
		
		// Vérification, Création, Sauvegarde des dossiers de travail
		checkDossiers(action);
		
		// - - - - - - - - - - - -
		// - - - Test  - - - - - -
		if ( action == ActionKind.TEST ) {
			log.debug("doMain() - Début TEST");
			
			GenerationCDDVD generationCDDVD = new GenerationCDDVD();
			generationCDDVD.creationCD();
			generationCDDVD.transfoHtml();

			log.debug("doMain() - Fin TEST");
			
		} else if(action == ActionKind.INIT || action == ActionKind.UPDATE || action == ActionKind.NODWNLD
				|| action == ActionKind.CDDVD ) {
			
			// - - - Base de Données - - -
			// create empty DB and initialize it for Android
			PrefetchDBTools.initializeSQLite(PrefetchConstants.DATABASE_URL);
			
			// create our data-source for the database
			connectionSource = new JdbcConnectionSource(PrefetchConstants.DATABASE_URL);
			
			// setup our database and DAOs
			dbContext = PrefetchDBTools.setupDatabase(connectionSource);
			
			PrefetchDBTools.databaseInitialisation(connectionSource);
			
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
				
				
				// - - - Intervenants - - -
				// On boucle sur les initiales des gens (Cf site : doris.ffessm.fr/contacts.asp?filtre=?)
				// On récupère la liste des intervenants dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
	
				PrefetchIntervenants intervenants = new PrefetchIntervenants(dbContext, connectionSource, action, nbMaxFichesATraiter);
				if ( intervenants.prefetch() == -1 ) {
					log.debug("doMain() - Erreur Intervenants" );
					System.exit(1);
				}
				
				
				// - - - Glossaire - - -
				// On boucle sur les initiales des définitions (Cf site : doris.ffessm.fr/glossaire.asp?filtre=?)
				// On récupère la liste des termes dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
	
				PrefetchGlossaire glossaire = new PrefetchGlossaire(dbContext, connectionSource, action, nbMaxFichesATraiter);
				if ( glossaire.prefetch() == -1 ) {
					log.debug("doMain() - Erreur Glossaire" );
					System.exit(1);
				}
				
				// - - - Bibliographie - - -
				// On boucle sur la page des Fiches tant que l'on trouve dans la page courante (n)
				//biblio.asp?mapage=(n+1)&PageCourante=n
				// On récupère les Bibliographies dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
				
				PrefetchBibliographies bibliographies = new PrefetchBibliographies(dbContext, connectionSource, action, nbMaxFichesATraiter);
				if ( bibliographies.prefetch() == -1 ) {
					log.debug("doMain() - Erreur Bibliographies" );
					System.exit(1);
				}
	
				// - - - Liste des Fiches - - -
				// Récupération de la liste des fiches sur le site de DORIS
				// Elles sont récupérées dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
	
				PrefetchFiches listeFiches = new PrefetchFiches(dbContext, connectionSource, action, nbMaxFichesATraiter,
						groupes.listeGroupes, intervenants.listeParticipants);
				if ( listeFiches.prefetch() == -1 ) {
					log.debug("doMain() - Erreur Liste des Fiches" );
					System.exit(1);
				}
	
	
				// - - - Mise à jour des zones géographiques - - -
				
				PrefetchZonesGeographiques zonesGeographiques = new PrefetchZonesGeographiques(dbContext, connectionSource, action, nbMaxFichesATraiter);
				if ( zonesGeographiques.prefetch() == -1 ) {
					log.debug("doMain() - Erreur Mise à jour des zones géographiques" );
					System.exit(1);
				}
				
				
				// - - - Enregistrement Date génération Base - - -
				Date date = new Date();
				SimpleDateFormat ft =  new SimpleDateFormat ("dd/MM/yyyy  HH:mm");
				dbContext.dorisDB_metadataDao.create(new DorisDB_metadata(ft.format(date),""));
				
				
				// - - - Génération CD et DVD  - - - 
				if ( action == ActionKind.CDDVD ) {
					GenerationCDDVD generationCDDVD = new GenerationCDDVD(dbContext, connectionSource, action, nbMaxFichesATraiter);
					generationCDDVD.generation();
				}

				
			} finally {
				// destroy the data source which should close underlying connections
				log.debug("doMain() - Fermeture Base");
				if (connectionSource != null) {
					connectionSource.close();
				}
			}
		// Fin de <> TEST
		} else if ( action == ActionKind.PROMOTE_AS_REF ) {
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
			
		} else if ( action == ActionKind.ERASE_BUT_REF ) {
			
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
			
			
		} else if ( action == ActionKind.ERASE_ALL ) {
			
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
			
			
			
		}

		
		log.debug("doMain() - Fin");
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
					 nbMaxFichesATraiter = Integer.parseInt(nbFichesStr);
					 log.debug("checkArgs() - nbMaxFichesTraitees : " + nbMaxFichesATraiter);
			    } catch(NumberFormatException e) { 
			    	help();
					log.error("Argument -M ou --max mal utilisé : " + arg);
			    	System.exit(1);
			    }
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
		System.out.println("  -M, --max=K		on limite le travail au K 1ères fiches (utile en dev.)");
		System.out.println("  -h, --help        afficher cette aide");
		System.out.println("  -d, --debug       messages detinés aux développeurs de cette application");
		System.out.println("  -v, --verbose     messages permettant de suivre l'avancé des traitements");
		System.out.println("  -s, --silence     aucune sortie, même pas les erreurs");
		System.out.println("");
		System.out.println("ACTION :");
		System.out.println("  INIT              Toutes les fiches sont retéléchargées sur doris.ffessm.fr et retraitées pour créer la base (images comprises)");
		System.out.println("  NODWNLD dossier   Pas de téléchargement, travail sur un dossier de référznce (utile en dév.)");
		System.out.println("  UPDATE            En plus des nouvelles fiches, définitions et intervenants, on retélécharge les fiches qui ont changées de statut");
		System.out.println("  CDDVD          	Comme UPDATE + Permet de télécharger les photos manquantes et de créer un dossier de la taille d'un CD (images en qualité inter.) et un de la taille d'un DVD (images en qualité max.)");
		System.out.println("  TEST          	Pour les développeurs");
		System.out.println("  PROMOTE_AS_REF    Déplace la base du Prefetch vers DorisAndroid");
		System.out.println("  ERASE_ALL         Efface tout le contenu de run");
		System.out.println("  ERASE_BUT_REF     Efface le contenu de run sauf html_ref et images_ref");
		log.debug("help() - Fin");
	}
	
	/**
	 * Création, Sauvegarde des dossiers de travail de l'application
	 * Selon l'action choisie
	 * 
	 * @param action
	 */
	private void checkDossiers(ActionKind inAction) {
		log.debug("checkDossiers() - Début");
		
		log.debug("checkDossiers() - Action : " + inAction.toString());
		
		log.debug("checkDossiers() - Dossier de base : " + PrefetchConstants.DOSSIER_RACINE);
		log.debug("checkDossiers() - Dossier html : " + PrefetchConstants.DOSSIER_HTML);
		log.debug("checkDossiers() - Dossier de la base : " + PrefetchConstants.DOSSIER_DATABASE);
		log.debug("checkDossiers() - Fichier de la Base : " + PrefetchConstants.DATABASE_URL);
		
		// Si le dossier principal de travail n'existe pas, on le créé
		File dossierBase = new File(PrefetchConstants.DOSSIER_RACINE);
		if (dossierBase.mkdirs()) {
			log.info("Création du dossier : " + dossierBase.getAbsolutePath());
		} 
		File dossierDB = new File(PrefetchConstants.DOSSIER_DATABASE);
		if (dossierDB.mkdirs()) {
			log.info("Création du dossier : " + dossierDB.getAbsolutePath());
		}

		// Si les dossiers download (html et img) et résultats existent déjà, ils sont renommés
		// avant d'être recréé vide
		Date maintenant = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
		String suffixe = sdf.format(maintenant);
		
		// Le dossier des fichiers html téléchargés
		if(inAction == ActionKind.INIT || inAction == ActionKind.UPDATE  || inAction == ActionKind.CDDVD ){
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
			if (dossierHtml.mkdir()) {
				log.info("Création du dossier download : " + dossierHtml.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier download : " + dossierHtml.getAbsolutePath());
				System.exit(1);
			}
		}
		
		// Le dossier des images téléchargées
		if( inAction == ActionKind.CDDVD ){
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
			if (dossierImages.mkdir()) {
				log.info("Création du dossier download : " + dossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier download : " + dossierImages.getAbsolutePath());
				System.exit(1);
			}
			File sousDossierImages;
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_ICONES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du sous-dossier download : " + sousDossierImages.getAbsolutePath());
				System.exit(1);
			}
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_VIGNETTES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(1);
			}
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_MED_RES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(1);
			}
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_HI_RES);
			if (sousDossierImages.mkdir()) {
				log.info("Création du dossier download : " + sousDossierImages.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier sous-download : " + sousDossierImages.getAbsolutePath());
				System.exit(1);
			}
		}
		
		// Le dossier du CD ou DVD
		if( inAction == ActionKind.CDDVD ){
			File dossierCD = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CD);
			if (dossierCD.exists()){
				File dossierCDNew = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CD +"_"+ suffixe);
				if(dossierCD.renameTo(dossierCDNew)){
					log.info("Sauvegarde du dossier CD : " + dossierCDNew.getAbsolutePath());
				}else{
					log.error("Echec renommage du dossier CD : " + dossierCDNew.getAbsolutePath());
					System.exit(1);
				}
			}
			if (dossierCD.mkdir()) {
				log.info("Création du dossier CD : " + dossierCD.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier CD : " + dossierCD.getAbsolutePath());
				System.exit(1);
			}
			File dossierDVD = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_DVD);
			if (dossierDVD.exists()){
				File dossierDVDNew = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_DVD +"_"+ suffixe);
				if(dossierDVD.renameTo(dossierDVDNew)){
					log.info("Sauvegarde du dossier DVD : " + dossierDVDNew.getAbsolutePath());
				}else{
					log.error("Echec renommage du dossier DVD : " + dossierDVDNew.getAbsolutePath());
					System.exit(1);
				}
			}
			if (dossierDVD.mkdir()) {
				log.info("Création du dossier DVD : " + dossierDVD.getAbsolutePath());
			} else {
				log.error("Echec de la Création du dossier DVD : " + dossierDVD.getAbsolutePath());
				System.exit(1);
			}
		}
		
		//	Vérification que le dossier html Référence existe
		if( inAction == ActionKind.NODWNLD || inAction == ActionKind.UPDATE  || inAction == ActionKind.CDDVD ){
			final File dossierReference = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF);
			if (!dossierReference.exists()){
				log.error("Le dossier Référence : " + dossierReference.getAbsolutePath() + "n'a pas été créé.");
				System.exit(1);
			} else {
				if (!dossierReference.isDirectory()){
					log.error("Le dossier Référence : " + dossierReference.getAbsolutePath() + "n'a pas été créé.");
					System.exit(1);
				}
			}
		}
		
		//	Vérification que les dossiers Images Référence existe
		if( inAction == ActionKind.CDDVD ){
			File dossierReference = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF);
			if (!dossierReference.exists()){
				log.error("Le dossier Référence des images : " + dossierReference.getAbsolutePath() + " n'a pas été créé.");
				System.exit(1);
			} else {
				if (!dossierReference.isDirectory()){
					log.error("Le dossier Référence des images : " + dossierReference.getAbsolutePath() + " n'a pas été créé.");
					System.exit(1);
				}
			}
			File sousDossierImages;
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_ICONES);
			if (!sousDossierImages.exists()){
				log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
				System.exit(1);
			} else {
				if (!sousDossierImages.isDirectory()){
					log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
					System.exit(1);
				}
			}
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_VIGNETTES);
			if (!sousDossierImages.exists()){
				log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
				System.exit(1);
			} else {
				if (!sousDossierImages.isDirectory()){
					log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
					System.exit(1);
				}
			}
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_MED_RES);
			if (!sousDossierImages.exists()){
				log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
				System.exit(1);
			} else {
				if (!sousDossierImages.isDirectory()){
					log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
					System.exit(1);
				}
			}
			sousDossierImages = new File(PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_HI_RES);
			if (!sousDossierImages.exists()){
				log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
				System.exit(1);
			} else {
				if (!sousDossierImages.isDirectory()){
					log.error("Le dossier Référence des images : " + sousDossierImages.getAbsolutePath() + " n'a pas été créé.");
					System.exit(1);
				}
			}
		}
	
		// Le fichier de la base de données
		if(inAction == ActionKind.INIT || inAction == ActionKind.UPDATE || inAction == ActionKind.NODWNLD  || inAction == ActionKind.CDDVD ) {

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
		
		log.debug("checkDossiers() - Fin");
	}



}
