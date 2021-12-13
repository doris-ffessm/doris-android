package fr.ffessm.doris.android.tools.disk;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils.SimpleStringSplitter;

/**
 * Adaptation of the DeviceSecondaryExternal but that use some legacy mechanism 
 * for devices that doesn't respond to the ContextCompat.getExternalFilesDirs API
 * This is based on /proc/mounts and fstab file
 */
@SuppressLint("NewApi")
class DeviceSecondaryExternalLegacy extends Device {
	private String mLabel, mName;

	protected String mMountPoint;
	
	private static final String LOG_TAG = DeviceSecondaryExternalLegacy.class.getCanonicalName();
	

	public DeviceSecondaryExternalLegacy(Context context) {
		
	}
	

	DeviceSecondaryExternalLegacy(SimpleStringSplitter sp) {
		mLabel = sp.next().trim();
		mMountPoint = sp.next().trim();
	}
	
	@Override
	public boolean isAvailable() { 
		File f = getMountPointFile();		
		return f.isDirectory() && f.canRead(); 
	}

	@Override
	public boolean isWriteable() { 
		if(isAvailable()){
			return getMountPointFile().canWrite();
		}
		else{
			return false;
		}
	}

	public final String getLabel() { return mLabel; }

	public String getName() { return mName; }
	protected final void setName(String name) { mName = name; }

	@Override
	public boolean isRemovable() { return true; }

	@Override
	public File getCacheDir(Context ctx) { 
		return getFilesDirLow(ctx, "/cache");	
	}

	@Override
	public File getFilesDir(Context ctx) {
		return getFilesDirLow(ctx, "/files");		
	}
	
	@SuppressLint("NewApi")
	@Override
	public File getFilesDir(Context ctx, String s) {
	
		// as getExternalFilesDirs isn't supposed to work, emulate the access
		return getFilesDirLow(ctx, s);
	
	}

	@Override
	public File getPublicDirectory(String s) { 
		if (s!=null && !s.startsWith("/")) s = "/" + s;
		return new File(getMountPoint() + s);
	}

	@Override
	public String getState() {
		if (isAvailable())
			return isWriteable() ? Environment.MEDIA_MOUNTED : Environment.MEDIA_MOUNTED_READ_ONLY;
		else 
			return Environment.MEDIA_REMOVED;
	}

	@Override
	public boolean isEmulated() {
		return false;
	}


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

