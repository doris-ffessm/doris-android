package fr.ffessm.doris.android.tools.disk;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.Log;

/**
 * Ein {@link Device}, das ein speziell gemountetes Gerät beschreibt, z.B.
 * die Secondary-SDs vieler moderner Geräte und die USB-Geräte bzw.
 * Kartenleser. Erkennt die Pfade aus vold.fstab (siehe {@link Environment2}
 * und emuliert die getXXXDir-Methoden, die sonst {@link Context} hat.
 * 
 * @author Jockel
 *
 */
@SuppressLint("NewApi")
class DeviceDiv extends Device {
	private String mLabel, mName;
	private boolean mAvailable, mWriteable;
	private static final String LOG_TAG = DeviceDiv.class.getCanonicalName();
	

	/**
	 * Constructor, der eine Zeile aus vold.fstab bekommt (dev_mount schon weggelesen)
	 * @param sp der StringSplitter, aus dem die Zeile gelesen wird, wobei
	 * 		"dev_mount" schon weggelesen sein muss.
	 * @return this für Verkettungen wie {@code return new Device().initFrom...() } 
	 */
	DeviceDiv(SimpleStringSplitter sp) {
		mLabel = sp.next().trim();
		mMountPoint = sp.next().trim();
		updateState();
	}
	
	@Override
	public boolean isAvailable() { return mAvailable; }

	@Override
	public boolean isWriteable() { return mWriteable; }

	@Override
	protected void updateState() {
		File f = new File(mMountPoint);		
		setName(f.getName()); // letzter Teil des Pfads

		Log.d(LOG_TAG, "updateState() - mMountPoint : "+mMountPoint);
		Log.d(LOG_TAG, "updateState() - name : "+f.getName());
		Log.d(LOG_TAG, "updateState() - f.isDirectory() : "+f.isDirectory());
		Log.d(LOG_TAG, "updateState() - f.canRead() : "+f.canRead());
		
		
		if (mAvailable = f.isDirectory() && f.canRead()) { // ohne canRead() klappts z.B. beim Note2 nicht
			mSize = Size.getSpace(f); 
			mWriteable = f.canWrite();
			// Korrektur, falls in /mnt/sdcard gemountet (z.B. Samsung)
			if (mMountPoint.startsWith(DiskEnvironment.mPrimary.mMountPoint) && mSize.equals(DiskEnvironment.mPrimary.mSize)) 
				mAvailable = mWriteable = false;
		} else 
			mWriteable = false;
		
	}

	public final String getLabel() { return mLabel; }

	public String getName() { return mName; }
	protected final void setName(String name) { mName = name; }

	@Override
	public boolean isRemovable() { return true; }

	@Override
	public File getCacheDir(Context ctx) { 
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			File[] files =ctx.getExternalCacheDirs();
			if(files.length > 1){
				// first entry is supposed to be the same as  ctx.getExternalFilesDir(type)
				// so let''s get the second one
				return files[1];
			}
			else{
				// we have a problem there, we haven't found a valid folder, this method will return a folder that is probably not writable
				return getFilesDirLow(ctx, "/cache");
			}
		}
		else {
			// if not KITKAT emulate the access 
			return getFilesDirLow(ctx, "/cache");
		}
	}

	@Override
	public File getFilesDir(Context ctx) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			return getFilesDir(ctx, "/files"); 
		}
		else{
			return getFilesDirLow(ctx, "/files");
		}
	}
	
	@SuppressLint("NewApi")
	@Override
	public File getFilesDir(Context ctx, String s) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			File[] files =ctx.getExternalFilesDirs(s);
			for(File file:files){
				Log.d(LOG_TAG, "getFilesDir() - files : "+file);
			}
			if(files.length > 1){
				// first entry is supposed to be the same as  ctx.getExternalFilesDir(type)
				// so let''s get the second one
				return files[1];
			}
			else{
				// we have a problem there, we haven't found a valid folder, this method will return a folder that is probably not writable
				return getFilesDirLow(ctx, s);
			}
		}
		else {
			// if not KITKAT emulate the access 
			return getFilesDirLow(ctx, s);
		}
	}

	@Override
	public File getPublicDirectory(String s) { 
		if (s!=null && !s.startsWith("/")) s = "/" + s;
		return new File(getMountPoint() + s);
	}

	@Override
	public String getState() {
		if (mAvailable)
			return mWriteable ? Environment.MEDIA_MOUNTED : Environment.MEDIA_MOUNTED_READ_ONLY;
		else 
			return Environment.MEDIA_REMOVED;
	}

	@Override
	public boolean isEmulated() {
		return false;
	}

}

