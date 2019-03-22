package fr.ffessm.doris.android.tools.disk;

import java.io.File;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import fr.ffessm.doris.android.tools.Textes_Outils;


class DeviceSecondaryExternal extends Device  {
	private static final String LOG_TAG = DeviceSecondaryExternal.class.getSimpleName();
	
	protected boolean mRemovable;
	protected boolean mEmulated = false;
	

	protected boolean mAvailable, mWriteable;
	
	protected String mMountPoint;
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	DeviceSecondaryExternal(Context context) {
		
		File[] possibleExtFilesDirs = ContextCompat.getExternalFilesDirs(context, "");
		if(possibleExtFilesDirs.length>1){
			
			// try to compute mountpoint from the common part 
			File mountPointFile = removeCommonTailPart(possibleExtFilesDirs[1], possibleExtFilesDirs[0]);
		
			mMountPoint = mountPointFile.getAbsolutePath();
	
			// determine removable and emulated mode
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
	    		setRemovable(Environment.isExternalStorageRemovable(possibleExtFilesDirs[1])); // Gingerbread weiß es genau
			}
			else {
				setRemovable(true); // Default ist, dass eine SD-Karte rausgenommen werden kann
			}
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				mEmulated = Environment.isExternalStorageEmulated(possibleExtFilesDirs[1]);
			} else {
				// utilise le nom du point d emontage pour avoir une idee s'il est emule ou pas
				mEmulated = Textes_Outils.containsIgnoreCase(mMountPoint, "emulated");
			}
		}
	}

	
	/**
	 * returns the leading part of file1, truncated by any similar part in file2
	 * @param file1
	 * @param file2
	 * @return
	 */
	protected File removeCommonTailPart(File file1, File file2){
		Log.d(LOG_TAG,"removeCommonTailPart "+file1+" "+file2 );
		if(file1 == null || file1.equals(file2)) return File.listRoots()[0];
		if( file1.getName().equals(file2.getName())){
			return removeCommonTailPart(file1.getParentFile(), file2.getParentFile());
		}
		return file1;
	}
	
	
	@Override
	public String getName() { return mRemovable ? "SD-Card" : "intern 3"; }

	@Override
	public boolean isRemovable() { return mRemovable; }

	protected final void setRemovable(boolean remove) { mRemovable = remove; }
	

	@Override
	public boolean isEmulated() { return mEmulated; }
	protected final void setEmulated(boolean emulated) { mEmulated = emulated; }

	@Override
	public boolean isAvailable() {
		String mState = getState();
		return (Environment.MEDIA_MOUNTED.equals(mState) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(mState));
	}

	@Override
	public boolean isWriteable() {
		return Environment.MEDIA_MOUNTED.equals(getState());
	}

	
	@Override
	public File getFilesDir(Context ctx) { return getFilesDir(ctx, null); }

		
	@Override
	public File getFilesDir(Context ctx, String s) { 
		File[] possibleExtFilesDirs = ContextCompat.getExternalFilesDirs(ctx, s);
		if(possibleExtFilesDirs.length>1){
			return possibleExtFilesDirs[1];
		}
		return getFilesDirLow(ctx, s);
	}

	
	@Override
	public File getCacheDir(Context ctx) { 
		File[] possibleExtCacheDirs = ContextCompat.getExternalCacheDirs(ctx);
		if(possibleExtCacheDirs.length>1){
			return possibleExtCacheDirs[1];
		}
		return getFilesDirLow(ctx, "/cache");
	}

	

	@Override
	public File getPublicDirectory(String s) { 
		return Environment.getExternalStoragePublicDirectory(s);
	}
	
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public String getState() { 
		File f = getMountPointFile();
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
			return Environment.getExternalStorageState(f);
		}
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			return Environment.getStorageState(f);
		}
		// older versions don't have a clean method, try to emulate
		if (mAvailable = f.isDirectory() && f.canRead()) {  
			mWriteable = f.canWrite();
		} else 
			mWriteable = false;
		if (mAvailable)
			return mWriteable ? Environment.MEDIA_MOUNTED : Environment.MEDIA_MOUNTED_READ_ONLY;
		else 
			return Environment.MEDIA_REMOVED; 
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

