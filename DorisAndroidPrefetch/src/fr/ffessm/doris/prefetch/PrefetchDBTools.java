/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.misc.SqlExceptionUtil;
import com.j256.ormlite.stmt.StatementBuilder.StatementType;
import com.j256.ormlite.support.CompiledStatement;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
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

public class PrefetchDBTools {
	
	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchFiches.class);
	
	// Constructeur
	public PrefetchDBTools(){
		
	}
	
	public void initializeSQLite(String url) throws ClassNotFoundException, SQLException{
		
		Class.forName("org.sqlite.JDBC");		
		Connection c = DriverManager.getConnection(url);
		log.debug("Opened database successfully");
		
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
	
	/**
	 * Setup our database and DAOs
	 */
	public DorisDBHelper setupDatabase(ConnectionSource connectionSource)
			throws Exception {
		log.debug("setupDatabase() - Début");
		
		DorisDBHelper dbContext = null;
		
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
		dbContext.classificationDao = DaoManager.createDao(connectionSource, Classification.class);
		dbContext.classificationFicheDao = DaoManager.createDao(connectionSource, ClassificationFiche.class);
		
		//dbContext.fiches_verificateurs_ParticipantsDao = DaoManager.createDao(connectionSource, Fiches_verificateurs_Participants.class);
		dbContext.fiches_ZonesGeographiquesDao = DaoManager.createDao(connectionSource, Fiches_ZonesGeographiques.class);
		//dbContext.fiches_DefinitionsGlossaireDao = DaoManager.createDao(connectionSource, Fiches_DefinitionsGlossaire.class);
		//dbContext.fiches_ZonesObservationsDao = DaoManager.createDao(connectionSource, Fiches_ZonesObservations.class);
		dbContext.dorisDB_metadataDao = DaoManager.createDao(connectionSource, DorisDB_metadata.class);
		
		return dbContext;
	}
		
	/**
	 * Création des Tables
	 */
	public void databaseInitialisation(ConnectionSource connectionSource)
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
		TableUtils.createTable(connectionSource, Classification.class);
		TableUtils.createTable(connectionSource, ClassificationFiche.class);
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
	
}