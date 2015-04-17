package fr.ffessm.doris.android.tools.disk;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;
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
		
		if (!DeviceSecondaryExternal.existSecondaryExternal(context)) throw new NoSecondaryStorageException();
		if(mSecondary == null) mSecondary = new DeviceSecondaryExternal(context);
		return mSecondary;
	}
		
	public static boolean isSecondaryExternalStorageAvailable(Context context) {
		if(!DeviceSecondaryExternal.existSecondaryExternal(context)) return false;
		try {
			return getSecondaryExternalStorage(context).isAvailable();
		} catch (NoSecondaryStorageException e) {
			return false;
		}
	}
}
