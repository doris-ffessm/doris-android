/*******************************************************************************
 * Copyright (c) 2012 Vojtisek.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Didier Vojtisek - initial API and implementation
 *******************************************************************************/
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
	}

}
