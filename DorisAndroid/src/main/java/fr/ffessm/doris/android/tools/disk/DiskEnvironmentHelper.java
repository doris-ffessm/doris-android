package fr.ffessm.doris.android.tools.disk;
import java.io.File;
import java.io.IOException;
import java.util.List;

import android.content.Context;

import androidx.core.content.ContextCompat;

import android.text.TextUtils.SimpleStringSplitter;
import android.util.Log;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.tools.disk.StorageHelper.StorageVolume;

/**
 * HelperClass that abstract the various Disk situations used in the project
 * Ie. Internal disk, primary Internal SD, secondary External SD
 * On some device, the primary internal SD is emulated and located on the internal disk.
 * @author dvojtise
 *
 */

public class DiskEnvironmentHelper {
	private static final String LOG_TAG = DiskEnvironmentHelper.class.getSimpleName();
	private static final boolean DEBUG = true;
	
	private static Device mInternal = new DeviceIntern();
	private static Device mPrimary = new DevicePrimaryExternal();
		
	private static Device mSecondary;
	
	public final static String PATH_PREFIX = "/Android/data/";
	

	public static Device getInternalStorage() {
		return mInternal;
	}
	
	public static Device getPrimaryExternalStorage() {
		return mPrimary;
	}
		
	public static Device getSecondaryExternalStorage(Context context) throws NoSecondaryStorageException {
		
		if (existSecondaryExternal(context)) {
			if(mSecondary == null) mSecondary = new DeviceSecondaryExternal(context);
		}
		else {
			if (existSecondaryExternalLegacy(context)) {
				if(mSecondary == null) mSecondary = createSecondaryExternalLegacy();
			} else {
				throw new NoSecondaryStorageException();
			}
		}
		return mSecondary;
		
	}
		
	public static boolean isSecondaryExternalStorageAvailable(Context context) {
		if(existSecondaryExternal(context) || existSecondaryExternalLegacy(context)) {
			try {
				return getSecondaryExternalStorage(context).isAvailable();
			} catch (NoSecondaryStorageException e) {
				return false;
			} catch (Exception e) {
				// something wrong happenned, let's consider that this disk doesn't exist
				Log.e(LOG_TAG, "pb dans la detection des disques:" + e.getMessage(), e);
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	/**
	 * Helpers for building the correct instance of SecondaryExternal
	 * @param context
	 * @return
	 */
	private static boolean existSecondaryExternal(Context context){
		File[] possibleExtFilesDirs = ContextCompat.getExternalFilesDirs(context, "");
		return possibleExtFilesDirs.length>1;
	}
	private static boolean existSecondaryExternalLegacy(Context context){
		List<StorageVolume> listStorages = StorageHelper.getStorages(false);
		for(StorageVolume storage : listStorages){
			//Log.d(LOG_TAG, "existSecondaryExternalLegacy() - StorageVolume "+storage.toString());
			if(storage.isRemovable() && !storage.isEmulated() && (storage.getType()!= StorageVolume.Type.USB)){
				// ignore if this is the primary sdCard
				try {
					if(!storage.file.getCanonicalPath().equals(getPrimaryExternalStorage().getMountPoint())){
						return true;
					}
				} catch (IOException e) {
					Log.e(LOG_TAG, e.getMessage(), e);
				}
			}
		}
		return false;
	}
	
	private static DeviceSecondaryExternalLegacy createSecondaryExternalLegacy(){
		List<StorageVolume> listStorages = StorageHelper.getStorages(false);
		for(StorageVolume storage : listStorages){
			//Log.d(LOG_TAG, "createSecondaryExternalLegacy() - StorageVolume "+storage.toString());
			if(storage.isRemovable() && !storage.isEmulated() && (storage.getType()!= StorageVolume.Type.USB)){				
			    try {
			    	if(!storage.file.getCanonicalPath().equals(getPrimaryExternalStorage().getMountPoint())){
			    		SimpleStringSplitter sp = new SimpleStringSplitter(' ');
			    		sp.setString(storage.fileSystem+" "+storage.file.getCanonicalPath());
									
			    		//Log.d(LOG_TAG, "createSecondaryExternalLegacy() - trying new storage "+storage.fileSystem+" "+storage.file.getCanonicalPath()+" "+storage.device);
					 
			    		return new DeviceSecondaryExternalLegacy(sp);
			    	}
					 
			    } catch (IOException e) {
					Log.e(LOG_TAG, e.getMessage(), e);
				}
			}
		}
		return null;
	}
//	private static boolean scanVold(String name) {
//	String s, f;
//	boolean prefixScan = true; // sdcard-Prefixes
//	SimpleStringSplitter sp = new SimpleStringSplitter(' ');
//	     try {
//	     BufferedReader buf = new BufferedReader(new FileReader(Environment.getRootDirectory().getAbsolutePath()+"/etc/"+name), 2048);
//	     s = buf.readLine();
//	     while (s!=null) {
//	     sp.setString(s.trim());
//	     f = sp.next(); // dev_mount oder anderes
//	         if ("dev_mount".equals(f)) {
//	         DeviceDiv d = new DeviceDiv(sp);
//	        
//	         if (TextUtils.equals(mPrimary.getMountPoint(), d.getMountPoint())) {
//	         // ein wenig Spezialkrams über /mnt/sdcard herausfinden
//	        
//	         // wenn die Gingerbread-Funktion isExternalStorageRemovable nicht da ist, diesen Hinweis nutzen
//	         if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD)
//	         mPrimary.setRemovable(true);
//	         // Then the default entry is removable
//	         // Actually this is not enough here, because the vold entries for the primary SD card are much more complex,
//	         // Is often because of what non-removable. But auszuwerden all these proprietary clothes
//	         // Would be far too complex. A workable compromise seems to be just from 2.3 to
//	         // To leave isExternalStorageRemovable what is already set up in Device (). In the
//	         // So far emerged devices with 2.2 again, the reference in vold seems to klappen.vccfg
//
//	         // For example Galaxy Note hangs "encryptable_nonremovable" to
//	         while (sp.hasNext()) {
//	         f = sp.next();
//	         if (f.contains("nonremovable")) {
//	         mPrimary.setRemovable(false);
//	         Log.w(LOG_TAG, "isExternStorageRemovable overwrite ('nonremovable') auf false");
//	         }
//	         }
//	         prefixScan = false;
//	         } else
//	         // nur in Liste aufnehmen, falls nicht Dupe von /mnt/sdcard
//	         mDeviceList.add(d);
//	        
//	         } else if (prefixScan) {
//	         // Weitere Untersuchungen nur, wenn noch vor sdcard-Eintrag
//	         // etwas unsauber, da es eigentlich in {} vorkommen muss, was ich hier nicht überprüfe
//	        
//	         if ("discard".equals(f)) {
//	         // manche (Galaxy Note) schreiben "discard=disable" vor den sdcard-Eintrag.
//	         sp.next(); // "="
//	         f = sp.next();
//	         if ("disable".equals(f)) {
//	         mPrimary.setRemovable(false);
//	         Log.w(LOG_TAG, "isExternStorageRemovable overwrite ('discard=disable') auf false");
//	         } else if ("enable".equals(f)) {
//	         // ha, denkste... bisher habe ich den Eintrag nur bei zwei Handys gefunden, (Galaxy Note, Galaxy Mini 2), und
//	         // da stimmte er *nicht*, sondern die Karten waren nicht herausnehmbar.
//	         // mPrimary.mRemovable = true;
//	         Log.w(LOG_TAG, "isExternStorageRemovable overwrite overwrite ('discard=enable'), bleibt auf "+mPrimary.isRemovable());
//	         } else
//	         Log.w(LOG_TAG, "disable-Eintrag unverständlich: "+f);
//	         }
//	        
//	         }
//	     s = buf.readLine();
//	     }
//	     buf.close();
//	     Log.v(LOG_TAG, name+" gelesen; Geräte gefunden: "+mDeviceList.size());
//	     return true;
//	     } catch (Exception e) {
//	     Log.e(LOG_TAG, "kann "+name+" nicht lesen: "+e.getMessage());
//	     return false;
//	     }
//	}
}
