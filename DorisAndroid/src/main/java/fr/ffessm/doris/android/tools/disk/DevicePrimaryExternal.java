package fr.ffessm.doris.android.tools.disk;

import java.io.File;

import fr.ffessm.doris.android.tools.Textes_Outils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

/**
 * Beschreibt die (primäre) SD-Karte in {@link Device}-Form. Interessant für 
 * API7 ist, dass die dort in {@link Context} nicht vorhandenen Methoden wie 
 * {@link Context#getExternalFilesDir(String)} emuliert werden.
 * 
 * TODO Die Erkennung, ob es sich um ein Gerät mit fester SD-Karte handelt,
 * findet nicht hier statt. {@link #isRemovable()} liefert also TRUE, zumindest
 * solange in {@link Environment2#rescanDevices()} kein Hack durchgeführt
 * wird.
 * 
 * @author Jockel
 * 
 * @version 1.2 - API7 wird unterstützt
 *
 */
class DevicePrimaryExternal extends Device {
	private boolean mRemovable;
	private boolean mEmulated = false;
	//private String mState;
	
	private String mMountPoint;
	
	/**
	 * liest Parameter aus {@link Environment#getExternalStorageDirectory()},
	 * also i.Allg. /mnt/sdcard, bei API7 /sdcard. Das Device wird als 
	 * Removable gekennzeichnet, was der Normalfall ist. Eine 
	 * Erkennung, ob es sich um z.B. ein Nexus mit fester SD-Karte
	 * handelt, findet hier nicht statt.
	 * 
	 * @return this für Verkettungen wie {@code return new Device().initFromExternalStorageDirectory() } 
	 */
	@SuppressLint("NewApi") 
	DevicePrimaryExternal() {
		File f = Environment.getExternalStorageDirectory();
		mMountPoint = f.getAbsolutePath();

		setRemovable(Environment.isExternalStorageRemovable()); // Gingerbread weiß es genau

		mEmulated = Environment.isExternalStorageEmulated();

	}

	
	
	
	
	@Override
	public String getName() { return mRemovable ? "SD-Card" : "intern 2"; }

	@Override
	public boolean isRemovable() { return mRemovable; }

	protected final void setRemovable(boolean remove) { mRemovable = remove; }
	

	@Override
	public boolean isEmulated() { return mEmulated; }
	protected final void setEmulated(boolean emulated) { mEmulated = emulated; }

	@Override
	public boolean isAvailable() {
		String mState = Environment.getExternalStorageState();
		return (Environment.MEDIA_MOUNTED.equals(mState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(mState));
	}

	@Override
	public boolean isWriteable() {
		String mState = Environment.getExternalStorageState();
		return Environment.MEDIA_MOUNTED.equals(mState);
	}

	
	@Override
	public File getFilesDir(Context ctx) { return getFilesDir(ctx, null); }

	
	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
	public File getFilesDir(Context ctx, String s) { 
		return ctx.getExternalFilesDir(s);
	}

	
	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
	public File getCacheDir(Context ctx) { 
		return ctx.getExternalCacheDir();
	}

	
	@TargetApi(Build.VERSION_CODES.FROYO)
	@Override
	public File getPublicDirectory(String s) { 
		return Environment.getExternalStoragePublicDirectory(s);

	}

	
	@Override
	public String getState() { return Environment.getExternalStorageState(); }


	@Override
	public File getMountPointFile() {
		return new File(getMountPoint()); 
	}

	@Override
	public Size getSize() {
		return Size.getSpace(getMountPointFile());
	}

	@Override
	public String getMountPoint() {
		return mMountPoint;
	}

}

