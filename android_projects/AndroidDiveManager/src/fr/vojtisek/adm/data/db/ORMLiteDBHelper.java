package fr.vojtisek.adm.data.db;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import fr.vojtisek.adm.data.file.DiveSample;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */
public class ORMLiteDBHelper extends OrmLiteSqliteOpenHelper{

	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "AndroidDiveManager.db";
	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;


	// the DAO object we use to access the diveBudies table
	private Dao<DiveBudy, Integer> diveBudiesDao = null;
	private RuntimeExceptionDao<DiveBudy, Integer> diveBudiesRuntimeDao = null;
	// the DAO object we use to access the diveEntries table
	private Dao<DiveEntry, Integer> diveEntriesDao = null;
	private RuntimeExceptionDao<DiveEntry, Integer> diveEntriesRuntimeDao = null;
	// the DAO object we use to access the diveSamples table
//	private Dao<DiveBudy, Integer> diveSamplesDao = null;
//	private RuntimeExceptionDao<DiveSample, Integer> diveSamplesRuntimeDao = null;

	public ORMLiteDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		//super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(ORMLiteDBHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, DiveEntry.class);
			TableUtils.createTable(connectionSource, DiveBudy.class);
	//		TableUtils.createTable(connectionSource, DiveSample.class);
		} catch (SQLException e) {
			Log.e(ORMLiteDBHelper.class.getName(), "Can't create database", e);
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
			Log.i(ORMLiteDBHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, DiveBudy.class, true);
			TableUtils.dropTable(connectionSource, DiveEntry.class, true);
//			TableUtils.dropTable(connectionSource, DiveSample.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(ORMLiteDBHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}

	

	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveBudy class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<DiveBudy, Integer> getDiveBudiesDao() {
		if (diveBudiesRuntimeDao == null) {
			diveBudiesRuntimeDao = getRuntimeExceptionDao(DiveBudy.class);
		}
		return diveBudiesRuntimeDao;
	}
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveEntry class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
	public RuntimeExceptionDao<DiveEntry, Integer> getDiveEntriesDao() {
		if (diveEntriesRuntimeDao == null) {
			diveEntriesRuntimeDao = getRuntimeExceptionDao(DiveEntry.class);
		}
		return diveEntriesRuntimeDao;
	}
	/**
	 * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our DiveSample class. It will
	 * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
	 */
/*	public RuntimeExceptionDao<DiveSample, Integer> getDiveSamplesDao() {
		if (diveSamplesRuntimeDao == null) {
			diveSamplesRuntimeDao = getRuntimeExceptionDao(DiveSample.class);
		}
		return diveSamplesRuntimeDao;
	}
	*/

	/**
	 * Close the database connections and clear any cached DAOs.
	 */
	@Override
	public void close() {
		super.close();
		diveEntriesRuntimeDao = null;
	}
}
