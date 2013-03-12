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
package fr.ffessm.doris.android.datamodel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;

/**
 * ORMLite Data base helper, designed to be used by android Activity
 */
public class OrmLiteDBHelper extends OrmLiteSqliteOpenHelper{
	
	public static final String TAG = "OrmLiteDBHelper";
	
	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "DorisAndroid.db";
	// any time you make changes to your database objects, you may have to increase the database version
	// Start of user code DorisAndroid
	private static final int DATABASE_VERSION = 1;
	// End of user code


	// the DAO object we use to access the diveBudies table
	private Dao<Fiche, Integer> ficheDao = null;
	private RuntimeExceptionDao<Fiche, Integer> ficheRuntimeDao = null;
	// the DAO object we use to access the diveBudies table
	private Dao<Participant, Integer> participantDao = null;
	private RuntimeExceptionDao<Participant, Integer> participantRuntimeDao = null;
	// the DAO object we use to access the diveBudies table
	private Dao<PhotoFiche, Integer> photoFicheDao = null;
	private RuntimeExceptionDao<PhotoFiche, Integer> photoFicheRuntimeDao = null;
	// the DAO object we use to access the diveBudies table
	private Dao<PhotoParticipant, Integer> photoParticipantDao = null;
	private RuntimeExceptionDao<PhotoParticipant, Integer> photoParticipantRuntimeDao = null;
	// the DAO object we use to access the diveBudies table
	private Dao<ZoneGeographique, Integer> zoneGeographiqueDao = null;
	private RuntimeExceptionDao<ZoneGeographique, Integer> zoneGeographiqueRuntimeDao = null;
	// the DAO object we use to access the diveBudies table
	private Dao<ZoneObservation, Integer> zoneObservationDao = null;
	private RuntimeExceptionDao<ZoneObservation, Integer> zoneObservationRuntimeDao = null;
	// the DAO object we use to access the diveBudies table
	private Dao<SectionFiche, Integer> sectionFicheDao = null;
	private RuntimeExceptionDao<SectionFiche, Integer> sectionFicheRuntimeDao = null;

	public OrmLiteDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(OrmLiteDBHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Fiche.class);
			TableUtils.createTable(connectionSource, Participant.class);
			TableUtils.createTable(connectionSource, PhotoFiche.class);
			TableUtils.createTable(connectionSource, PhotoParticipant.class);
			TableUtils.createTable(connectionSource, ZoneGeographique.class);
			TableUtils.createTable(connectionSource, ZoneObservation.class);
			TableUtils.createTable(connectionSource, SectionFiche.class);
		} catch (SQLException e) {
			Log.e(OrmLiteDBHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}

		// here we try inserting data in the on-create as a test
		/*RuntimeExceptionDao<DiveEntry, Integer> dao = getSimpleDataDao();
		long millis = System.currentTimeMillis();
		// create some entries in the onCreate
		DiveEntry simple = new DiveEntry(millis);
		dao.create(simple);
		simple = new DiveEntry(millis + 1);
		dao.create(simple);
		Log.i(ORMLiteDBHelper.class.getName(), "created new entries in onCreate: " + millis);
		*/
	}

	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(OrmLiteDBHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Fiche.class, true);
			TableUtils.dropTable(connectionSource, Participant.class, true);
			TableUtils.dropTable(connectionSource, PhotoFiche.class, true);
			TableUtils.dropTable(connectionSource, PhotoParticipant.class, true);
			TableUtils.dropTable(connectionSource, ZoneGeographique.class, true);
			TableUtils.dropTable(connectionSource, ZoneObservation.class, true);
			TableUtils.dropTable(connectionSource, SectionFiche.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(OrmLiteDBHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveBudy class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Fiche, Integer> getFicheDao() {
		if (ficheRuntimeDao == null) {
			ficheRuntimeDao = getRuntimeExceptionDao(Fiche.class);
		}
		return ficheRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveBudy class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<Participant, Integer> getParticipantDao() {
		if (participantRuntimeDao == null) {
			participantRuntimeDao = getRuntimeExceptionDao(Participant.class);
		}
		return participantRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveBudy class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<PhotoFiche, Integer> getPhotoFicheDao() {
		if (photoFicheRuntimeDao == null) {
			photoFicheRuntimeDao = getRuntimeExceptionDao(PhotoFiche.class);
		}
		return photoFicheRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveBudy class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<PhotoParticipant, Integer> getPhotoParticipantDao() {
		if (photoParticipantRuntimeDao == null) {
			photoParticipantRuntimeDao = getRuntimeExceptionDao(PhotoParticipant.class);
		}
		return photoParticipantRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveBudy class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<ZoneGeographique, Integer> getZoneGeographiqueDao() {
		if (zoneGeographiqueRuntimeDao == null) {
			zoneGeographiqueRuntimeDao = getRuntimeExceptionDao(ZoneGeographique.class);
		}
		return zoneGeographiqueRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveBudy class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<ZoneObservation, Integer> getZoneObservationDao() {
		if (zoneObservationRuntimeDao == null) {
			zoneObservationRuntimeDao = getRuntimeExceptionDao(ZoneObservation.class);
		}
		return zoneObservationRuntimeDao;
	}

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveBudy class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<SectionFiche, Integer> getSectionFicheDao() {
		if (sectionFicheRuntimeDao == null) {
			sectionFicheRuntimeDao = getRuntimeExceptionDao(SectionFiche.class);
		}
		return sectionFicheRuntimeDao;
	}

	

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		ficheRuntimeDao = null;
		participantRuntimeDao = null;
		photoFicheRuntimeDao = null;
		photoParticipantRuntimeDao = null;
		zoneGeographiqueRuntimeDao = null;
		zoneObservationRuntimeDao = null;
		sectionFicheRuntimeDao = null;
	}

}