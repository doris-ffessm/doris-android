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
import fr.ffessm.doris.android.datamodel.associations.Fiches_verificateurs_Participants;

public class PrefetchDorisWebSite {

	// we are using the in-memory H2 database
	private final static String DATABASE_URL = "jdbc:h2:mem:fiche";

	DorisDBHelper dbContext = null;

	public static void main(String[] args) throws Exception {
		// turn our static method into an instance of Main
		new PrefetchDorisWebSite().doMain(args);
	}

	private void doMain(String[] args) throws Exception {
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
	 * Setup our database and DAOs
	 */
	private void setupDatabase(ConnectionSource connectionSource)
			throws Exception {

		dbContext = new DorisDBHelper();
		dbContext.ficheDao = DaoManager.createDao(connectionSource, Fiche.class);
		dbContext.participantDao = DaoManager.createDao(connectionSource, Participant.class);
		dbContext.fiches_verificateurs_ParticipantsDao = DaoManager.createDao(connectionSource, Fiches_verificateurs_Participants.class);

		// if you need to create the table
		TableUtils.createTable(connectionSource, Fiche.class);
		TableUtils.createTable(connectionSource, Participant.class);
		TableUtils.createTable(connectionSource, Fiches_verificateurs_Participants.class);
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
				out.write(fiche.toXML()+"\n");
			}
			out.write("</Fiches>\n");
			out.write("</Doris>\n");
			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

}
