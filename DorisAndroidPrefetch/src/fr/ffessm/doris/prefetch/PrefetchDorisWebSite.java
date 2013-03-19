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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

//import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.htmlparser.jericho.Element;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.ZoneObservation;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesObservations;
import fr.ffessm.doris.android.datamodel.associations.Fiches_verificateurs_Participants;
import fr.ffessm.doris.android.datamodel.xml.XMLHelper;


public class PrefetchDorisWebSite {

	// Pourrait être un jour utile, on verra
	private final static String VERSION = "0.01";
	
	// we are using the in-memory H2 database
	private final static String DATABASE_URL = "jdbc:h2:mem:fiche";

	// Dossiers liés au fonctionnement de l'appli prefetch
	private final static String DOSSIER_BASE = "./run";
	// Ces dossiers seront renommés qd nécessaire
	private final static String DOSSIER_HTML = "html";
	private final static String DOSSIER_IMG = "img";
	private final static String DOSSIER_RESULTATS = "result";
	
	// Inititalisation de la Gestion des Log {Log(0) permet de forcer le mode debbug}
	private final static String LOGTAG = "PrefetchDoris";
	public static Log trace = new Log();
	public org.apache.commons.logging.Log log = LogFactory.getLog(XMLHelper.class);
	//public static Log trace = new Log(0);
	
	// Nombre maximum de fiches traitées (--max=K permet de changer cette valeur)
	private static int nbMaxFichesTraitees = 9999;
	
	// Balises et Attributs des fichiers XML
	public static final String XML_BASE = "Doris";
	public static final String XML_FICHES = "Fiches";
	public static final String XML_ATT_DATE_CREAT = "DateCreation";
	public static final String XML_ATT_SITE_URL = "UrlRacineSite";
	
	DorisDBHelper dbContext = null;

	public static void main(String[] args) throws Exception {
		
		// turn our static method into an instance of Main
		new PrefetchDorisWebSite().doMain(args);
		
	}

	private void doMain(String[] args) throws Exception {
		log.info("Test log Didier");
		// Vérification et lecture des arguments
		trace.log(trace.LOG_DEBUG, LOGTAG, "doMain() : Vérification et lecture des arguments");
		String action = checkArgs(args);
		trace.log(trace.LOG_VERBOSE, LOGTAG, "action : " + action);
		trace.log(trace.LOG_VERBOSE, LOGTAG, "Nb. Fiches Max : " + nbMaxFichesTraitees);
		
		// Vérification, Création, Sauvegarde des dossiers de travails
		checkDossiers(action);
		
		// Récupération de la liste des fiches sur le site de DORIS
		String listeFichesFichier = DOSSIER_BASE + "/" + DOSSIER_HTML + "/listeFiches.html";
		trace.log(trace.LOG_VERBOSE, LOGTAG, "Récup. Liste Fiches Doris : " + listeFichesFichier);
		
		List<Fiche> listeFichesTravail = new ArrayList<Fiche>(1);
		
		if (! action.equals("NODWNLD")){
			if (Outils.getFichierUrl(TempCommon.getListeFichesUrl(), listeFichesFichier)) {
				String contenuFichierHtml = Outils.getFichier(new File(listeFichesFichier));
				
				listeFichesTravail = TempCommon.getListeFiches(contenuFichierHtml, nbMaxFichesTraitees);
			} else {
				trace.log(trace.LOG_ERROR, LOGTAG, "Une erreur est survenue lors de la récupération de la liste des fiches");
				System.exit(0);
			}
		} else {
			String contenuFichierHtml = Outils.getFichier(new File(listeFichesFichier));
			
			listeFichesTravail = TempCommon.getListeFiches(contenuFichierHtml, nbMaxFichesTraitees);
		}
		// TODO : Il faudra ici, pour les modes où on complète la base, reconstruire
		// la table des fiches déjà connues
		
		
		// Pour chaque fiche de la liste de travail :
		// TODO : on vérifie qu'il faut la traiter
		// On la télécharge (et sauvegarde le fichier original)
		// On la traite
		for (Fiche fiche : listeFichesTravail) {
			// TODO : Ne pas traiter dans certain cas
			
			trace.log(trace.LOG_DEBUG, LOGTAG, "doMain() - Fiche : "+fiche.getNomCommun());
			
			String urlFiche = "http://doris.ffessm.fr/fiche2.asp?fiche_numero="+fiche.getNumeroFiche();
			String fichierLocalFiche = DOSSIER_BASE + "/" + DOSSIER_HTML + "/fiche"+fiche.getNumeroFiche()+".html";
			if (! action.equals("NODWNLD")) {
				if (Outils.getFichierUrl(urlFiche, fichierLocalFiche)) {
					String contenuFichierHtml = Outils.getFichier(new File(fichierLocalFiche));
				
					fiche.getFiche(contenuFichierHtml);
				} else {
					trace.log(trace.LOG_ERROR, LOGTAG, "Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
				}
			} else {
				String contenuFichierHtml = Outils.getFichier(new File(fichierLocalFiche));
				
				fiche.getFiche(contenuFichierHtml);
			}
		}
		
		// Ecriture des fiches dans le fichier xml final
		ConnectionSource connectionSource = null;
		try {
			// create our data-source for the database
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			// setup our database and DAOs
			setupDatabase(connectionSource);
			// read and write some data
			readWriteData(listeFichesTravail);

		} finally {
			// destroy the data source which should close underlying connections
			if (connectionSource != null) {
				connectionSource.close();
			}
		}
	}

	/**
	 * Vérification des arguments passés à l'application
	 * 
	 *  @param args
	 */
	private String checkArgs(String[] inArgs){
			
		// Si Aucun Argument, on affiche l'aide et on termine
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - nb args : " + inArgs.length);
		if (inArgs.length < 1) {
			help();
			trace.log(trace.LOG_ERROR, LOGTAG, "Le programme ne peut être lancé sans arguments.");
			System.exit(0);
		}
		
		// On commence par regarder si un des paramètres est un paramétre optionnel prioritaire
		// verbose, debug ou silence
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - debug, verbose ou silence ? ");
		for (String arg : inArgs) {
			trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - arg : " + arg);
			
			if ( arg.equals("-d") || arg.equals("--debug") ) {
				trace.set_niveauTrace(trace.LOG_DEBUG);
			}
			if ( arg.equals("-v") || arg.equals("--verbose")) {
				trace.set_niveauTrace(trace.LOG_VERBOSE);
			}
			if ( arg.equals("-s") || arg.equals("--silence")) {
				trace.set_niveauTrace(trace.LOG_SILENCE);
			}
		}
		
		// Si Aide ou Version alors affichage puis on termine
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - help ou version ? ");
		for (String arg : inArgs) {
			
			if ( arg.equals("-h") || arg.equals("--help")) {
				trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - arg : " + arg);
				help();
				System.exit(0);
			}
			if ( arg.equals("-V") || arg.equals("--version")) {
				trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - arg : " + arg);
				version();
				System.exit(0);
			}
		}
		
		// paramètre qui permet de limiter le nombre de fiches à traiter
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - max ? ");
		for (String arg : inArgs) {
			if ( arg.startsWith("-M") || arg.startsWith("--max=")) {
				trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - arg : " + arg);
				String nbFichesStr = null;
				if ( arg.startsWith("-M")) {
					nbFichesStr =  arg.substring(3);
				}
				if ( arg.startsWith("--max=")) {
					nbFichesStr =  arg.substring(6);
				}
				try { 
					 nbMaxFichesTraitees = Integer.parseInt(nbFichesStr);
					 trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - nbMaxFichesTraitees : " + nbMaxFichesTraitees);
			    } catch(NumberFormatException e) { 
			    	help();
					trace.log(trace.LOG_ERROR, LOGTAG, "Argument -M ou --max mal utilisé : " + arg);
			    	System.exit(0);
			    }
			}
		}

			
			
		// Vérification que le dernier argument est une des actions prévues
		String action = inArgs[inArgs.length - 1];
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - argument action : " + action);
		
		if (action.equals("INIT")) {
		} else if (action.equals("NODWNLD")) {
		} else if (action.equals("NEWFICHES")) {
		} else if (action.equals("UPDATE")) {
		} else if (action.equals("INITSSIMG")) {
		} else {
			help();
			String listeArgs = "";
			for (String arg : inArgs) {
				listeArgs += arg + " ";
			}
			trace.log(trace.LOG_ERROR, LOGTAG, "arguments : " + listeArgs);
			trace.log(trace.LOG_ERROR, LOGTAG, "Action non prévue");
			System.exit(0);
		}
		return action;
	}
	
	/**
	 * Setup our database and DAOs
	 */
	private void setupDatabase(ConnectionSource connectionSource)
			throws Exception {

		dbContext = new DorisDBHelper();
		dbContext.ficheDao = DaoManager.createDao(connectionSource, Fiche.class);
		dbContext.photoFicheDao = DaoManager.createDao(connectionSource, PhotoFiche.class);
		dbContext.participantDao = DaoManager.createDao(connectionSource, Participant.class);
		dbContext.zoneGeographiqueDao = DaoManager.createDao(connectionSource, ZoneGeographique.class);
		dbContext.zoneObservationDao = DaoManager.createDao(connectionSource, ZoneObservation.class);
		dbContext.sectionFicheDao = DaoManager.createDao(connectionSource, SectionFiche.class);
		dbContext.autreDenominationDao = DaoManager.createDao(connectionSource, AutreDenomination.class);
		dbContext.fiches_verificateurs_ParticipantsDao = DaoManager.createDao(connectionSource, Fiches_verificateurs_Participants.class);
		dbContext.fiches_ZonesGeographiquesDao = DaoManager.createDao(connectionSource, Fiches_ZonesGeographiques.class);
		dbContext.fiches_ZonesObservationsDao = DaoManager.createDao(connectionSource, Fiches_ZonesObservations.class);
		
		
		// if you need to create the table
		TableUtils.createTable(connectionSource, Fiche.class);
		TableUtils.createTable(connectionSource, Participant.class);
		TableUtils.createTable(connectionSource, PhotoFiche.class);
		TableUtils.createTable(connectionSource, ZoneGeographique.class);
		TableUtils.createTable(connectionSource, ZoneObservation.class);
		TableUtils.createTable(connectionSource, SectionFiche.class);
		TableUtils.createTable(connectionSource, AutreDenomination.class);
		TableUtils.createTable(connectionSource, Fiches_verificateurs_Participants.class);
		TableUtils.createTable(connectionSource, Fiches_ZonesGeographiques.class);
		TableUtils.createTable(connectionSource, Fiches_ZonesObservations.class);
	}

	/**
	 * Read and write some example data.
	 */
	private void readWriteData(List<Fiche> inListeFiches) throws Exception {
		// create an instance of Fiche

		/* Fiche fiche1 = new Fiche("Amphiprion bicinctus",
				"Poisson-clown à deux bandes", 1001, "");
		Fiche fiche2 = new Fiche("Palaemon elegans Rathke",
				"Petite crevette rose", 337, "");
		
		// persist the fiches object to the database
		dbContext.ficheDao.create(fiche1);
		dbContext.ficheDao.create(fiche2);
		
		SectionFiche s1 = new SectionFiche("Titre S1","hqdshgh ojfjdj");
		//fiche1.getContenu().add(s1);
		//s1.setFiche(fiche1);
		dbContext.sectionFicheDao.create(s1);
		
		int id = fiche1.getId();

		Participant p1 = new Participant("Didier");
		Participant p2 = new Participant("Guillaume");
		
		dbContext.participantDao.create(p1);
		dbContext.participantDao.create(p2);
		
		Fiches_verificateurs_Participants verification1 =  new Fiches_verificateurs_Participants(fiche1,p1);
		Fiches_verificateurs_Participants verification2 =  new Fiches_verificateurs_Participants(fiche1,p2);
		Fiches_verificateurs_Participants verification3 =  new Fiches_verificateurs_Participants(fiche2,p1);
		
		dbContext.fiches_verificateurs_ParticipantsDao.create(verification1);
		dbContext.fiches_verificateurs_ParticipantsDao.create(verification2);
		dbContext.fiches_verificateurs_ParticipantsDao.create(verification3);
		*/
		
		
		// assign a password
		//fiche1.setId(1001);
		// update the database after changing the object
		//ficheDao.update(fiche1);

		// query for all items in the database
		/*List<Fiche> fiches = dbContext.ficheDao.queryForAll();
		Fiche fiche3 = fiches.get(0);
		
		
		System.out.println("nb verificateur fiche 0: " + fiche3.lookupVerificateurs(dbContext).size());
*/
		for (Fiche fiche : inListeFiches){
			dbContext.ficheDao.create(fiche);
		}
		
		/*
		File f = new File("./prefetchedDorisDB.xml");
		sauveXML(f, fiches);
		*/
		
		// test de la nouvelle fonctions XMLHelper
		String fichierXML = DOSSIER_BASE + "/" + DOSSIER_RESULTATS + "/" + "prefetchedDorisDB.xml";
		XMLHelper.saveDBToFile(new File(fichierXML), dbContext);
	}

	/**
	 * Sauves toutes les fiches dans un fichier
	 * 
	 * @param fiches
	 */
	public void sauveXML(File file, List<Fiche> fiches) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write("<");
			out.write(XML_BASE);
			out.write(" ");
			out.write(XML_ATT_DATE_CREAT);
			out.write("=\"");
			SimpleDateFormat formatDate = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss" );
			out.write(formatDate.format(new Date()));
			out.write("\" ");
			out.write(XML_ATT_SITE_URL);
			out.write("=\"");
			out.write(TempCommon.getSiteUrl());
			out.write("\"");
			out.write(">\n");
			out.write("<");
			out.write(XML_FICHES);
			out.write(">\n");
			for (Fiche fiche : fiches) {
				out.write(fiche.toXML("",dbContext)+"\n");
				//out.write(fiche.toXML()+"\n");
			}
			out.write("</");
			out.write(XML_FICHES);
			out.write(">\n");
			out.write("</");
			out.write(XML_BASE);
			out.write(">\n");
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
     * Afficher l'aide de l'application
     **/	
	private static void help(){
		trace.log(trace.LOG_DEBUG, LOGTAG, "help() - Début");
		
		System.out.println("Récupération de la base de fiches pour DorisAndroid, Version : " + VERSION);
		System.out.println("Usage: java -jar PrefetchDorisWebSite.jar [OPTIONS] [ACTION]");
		System.out.println("");
		System.out.println("OPTIONS :");
		System.out.println("  -M, --max=K      on limite le travail au K 1ères fiches (utile en dev.)");
		System.out.println("  -V, --version     afficher la version de l'application et quitter");
		System.out.println("  -h, --help        afficher cette aide");
		System.out.println("  -d, --debug       messages detinés aux développeurs de cette application");
		System.out.println("  -v, --verbose     messages permettant de suivre l'avancé des traitements");
		System.out.println("  -s, --silence     aucune sortie, même pas les erreurs");
		System.out.println("");
		System.out.println("ACTION :");
		System.out.println("  INIT               Toutes les fiches sont retéléchargées sur doris.ffessm.fr et retraitées pour créer la base (images comprises)");
		System.out.println("  NODWNLD	         Pas de téléchargement, travail sur ce qui est dispo. uniquement (utile en dev.)");
		System.out.println("  NEWFICHES          Ne télécharge que les nouvelles fiches");
		System.out.println("  UPDATE             En plus des nouvelles fiches, on retélécharge les fiches qui ont changées de statut");
		System.out.println("  INITSSIMG          Comme INIT sauf que l'on ne retélécharge pas une image déjà connue");
		
		trace.log(trace.LOG_DEBUG, LOGTAG, "help() - Fin");
	}
	
	/**
     * Afficher la version de l'application
     * */	
	private static void version(){
		trace.log(trace.LOG_DEBUG, LOGTAG, "version() - Début");
		
		System.out.println("Version : " + VERSION);
		
		trace.log(trace.LOG_DEBUG, LOGTAG, "version() - Fin");
	}

	/**
	 * Création, Sauvegarde des dossiers de travail de l'application
	 * Selon l'action choisie
	 * 
	 * @param action
	 */
	public void checkDossiers(String inAction) {
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkDossiers() - Début");
		
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkDossiers() - Action : " + inAction);
		
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkDossiers() - Dossier de base : " + DOSSIER_BASE);
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkDossiers() - Dossier html : " + DOSSIER_HTML);
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkDossiers() - Dossier Resultats : " + DOSSIER_RESULTATS);
		
		// Si le dossier principal de travail n'existe pas, on le créé
		File dossierBase = new File(DOSSIER_BASE);
		if (dossierBase.mkdirs()) {
			trace.log(trace.LOG_VERBOSE, LOGTAG, "Création du dossier : " + dossierBase.getAbsolutePath());
		} else {
			trace.log(trace.LOG_ERROR, LOGTAG, "Echec de la Création du dossier : " + dossierBase.getAbsolutePath());
		}
		
		// Si les dossiers download (html et img) et résultats existent déjà, ils sont renommés
		// avant d'être recréé vide
		// TODO : dans le cas NODWNLD il faudrait vérifier dans les dossiers existent
		Date maintenant = new Date(); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMddhhmmss");
		String suffixe = sdf.format(maintenant);
		
		// Le dossier des fichiers html téléchargés
		if(inAction.equals("INIT")){
			File dossierHtml = new File(DOSSIER_BASE + "/" + DOSSIER_HTML);
			if (dossierHtml.exists()){
				File dossierHtmlNew = new File(DOSSIER_BASE + "/" + DOSSIER_HTML + suffixe);
				if(dossierHtml.renameTo(dossierHtmlNew)){
					trace.log(trace.LOG_VERBOSE, LOGTAG, "Sauvegarde du dossier download : " + dossierHtmlNew.getAbsolutePath());
				}else{
					trace.log(trace.LOG_ERROR, LOGTAG, "Echec renommage du dossier download : " + dossierHtml.getAbsolutePath());
					System.exit(0);
				}
			}
			if (dossierHtml.mkdir()) {
				trace.log(trace.LOG_VERBOSE, LOGTAG, "Création du dossier download : " + dossierHtml.getAbsolutePath());
			} else {
				trace.log(trace.LOG_ERROR, LOGTAG, "Echec de la Création du dossier download : " + dossierHtml.getAbsolutePath());
				System.exit(0);
			}
		}
		
		// Le dossier des fichiers image téléchargés
		if(inAction.equals("INIT") || inAction.equals("INITSSIMG")){
			File dossierImg = new File(DOSSIER_BASE + "/" + DOSSIER_IMG);
			if (dossierImg.exists()){
				File dossierImgNew = new File(DOSSIER_BASE + "/" + DOSSIER_IMG + suffixe);
				if(dossierImg.renameTo(dossierImgNew)){
					trace.log(trace.LOG_VERBOSE, LOGTAG, "Sauvegarde du dossier download : " + dossierImgNew.getAbsolutePath());
				}else{
					trace.log(trace.LOG_ERROR, LOGTAG, "Echec renommage du dossier download : " + dossierImg.getAbsolutePath());
					System.exit(0);
				}
			}
			if (dossierImg.mkdir()) {
				trace.log(trace.LOG_VERBOSE, LOGTAG, "Création du dossier download : " + dossierImg.getAbsolutePath());
			} else {
				trace.log(trace.LOG_ERROR, LOGTAG, "Echec de la Création du dossier download : " + dossierImg.getAbsolutePath());
				System.exit(0);
			}
		}
				
		// Le dossier des résultats
		// TODO : Il faudra être capable de lire le fichier précédement généré
		if( inAction.equals("INIT") || inAction.equals("NODWNLD") ){
			File dossierResultats = new File(DOSSIER_BASE + "/" + DOSSIER_RESULTATS);
			if (dossierResultats.exists()){
				File dossierResultatsNew = new File(DOSSIER_BASE + "/" + DOSSIER_RESULTATS + suffixe);
				if(dossierResultats.renameTo(dossierResultatsNew)){
					trace.log(trace.LOG_VERBOSE, LOGTAG, "Sauvegarde du dossier résultats : " + dossierResultatsNew.getAbsolutePath());
				}else{
					trace.log(trace.LOG_ERROR, LOGTAG, "Echec renommage du dossier résultats : " + dossierResultats.getAbsolutePath());
					System.exit(0);
				}
			}
			if (dossierResultats.mkdir()) {
				trace.log(trace.LOG_VERBOSE, LOGTAG, "Création du dossier résultats : " + dossierResultats.getAbsolutePath());
			} else {
				trace.log(trace.LOG_ERROR, LOGTAG, "Echec de la Création du dossier résultats : " + dossierResultats.getAbsolutePath());
				System.exit(0);
			}
		}
		
		
		
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkDossiers() - Fin");
	}
	
	
}
