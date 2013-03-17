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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;


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

	// Inititalisation de la Gestion des Log {Log(0) permet de forcer le mode debbug}
	private final static String LOGTAG = "PrefetchDoris";
	public static Log trace = new Log();
	//public static Log trace = new Log(0);
	
	//Nombre maximum de fiches traitées (--max=K permet de changer cette valeur)
	private static int nbMaxFichesTraitees = 9999;
	
	DorisDBHelper dbContext = null;

	public static void main(String[] args) throws Exception {
		
		// turn our static method into an instance of Main
		new PrefetchDorisWebSite().doMain(args);
		
	}

	private void doMain(String[] args) throws Exception {
		
		//Vérification et lecture des arguments
		trace.log(trace.LOG_DEBUG, LOGTAG, "doMain() : Vérification et lecture des arguments");
		String action = checkArgs(args);
		trace.log(trace.LOG_VERBOSE, LOGTAG, "action : " + action);
		trace.log(trace.LOG_VERBOSE, LOGTAG, "Nb. Fiches Max : " + nbMaxFichesTraitees);
		
		ConnectionSource connectionSource = null;
		try {
			// create our data-source for the database
			connectionSource = new JdbcConnectionSource(DATABASE_URL);
			// setup our database and DAOs
			setupDatabase(connectionSource);
			// read and write some data
			readWriteData();

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
	private String checkArgs(String[] args){
			
		// Si Aucun Argument, on affiche l'aide et on termine
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - nb args : " + args.length);
		if (args.length < 1) {
			help();
			trace.log(trace.LOG_ERROR, LOGTAG, "Le programme ne peut être lancé sans arguments.");
			System.exit(0);
		}
		
		// On commence par regarder si un des paramètres est un paramétre optionnel prioritaire
		// verbose, debug ou silence
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - debug, verbose ou silence ? ");
		for (String arg : args) {
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
		for (String arg : args) {
			
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
		for (String arg : args) {
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
		String action = args[args.length - 1];
		trace.log(trace.LOG_DEBUG, LOGTAG, "checkArgs() - argument action : " + action);

		if (action.equals("INIT")) {
			
		} else if (action.equals("NEWFICHES")) {
			
		} else if (action.equals("UPDATE")) {
			
		} else if (action.equals("INITSSIMG")) {
			
		} else {
			help();
			String listeArgs = "";
			for (String arg : args) {
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
		TableUtils.createTable(connectionSource, Fiches_verificateurs_Participants.class);
		TableUtils.createTable(connectionSource, Fiches_ZonesGeographiques.class);
		TableUtils.createTable(connectionSource, Fiches_ZonesObservations.class);
	}

	/**
	 * Read and write some example data.
	 */
	private void readWriteData() throws Exception {
		// create an instance of Fiche

		Fiche fiche1 = new Fiche("Amphiprion bicinctus",
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
		
		
		
		// assign a password
		//fiche1.setId(1001);
		// update the database after changing the object
		//ficheDao.update(fiche1);

		// query for all items in the database
		List<Fiche> fiches = dbContext.ficheDao.queryForAll();
		Fiche fiche3 = fiches.get(0);
		
		
		System.out.println("nb verificateur fiche 0: " + fiche3.lookupVerificateurs(dbContext).size());

		File f = new File("./prefetchedDorisDB.xml");
		sauveXML(f, fiches);
		
		
		// test de la nouvelle fonctions XMLHelper
		XMLHelper.saveDBToFile(new File("./prefetchedDorisDB2.xml"), dbContext);
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
			out.write("<Doris>\n");
			out.write("<Fiches>\n");
			for (Fiche fiche : fiches) {
				out.write(fiche.toXML("",dbContext)+"\n");
				//out.write(fiche.toXML()+"\n");
			}
			out.write("</Fiches>\n");
			out.write("</Doris>\n");
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

}
