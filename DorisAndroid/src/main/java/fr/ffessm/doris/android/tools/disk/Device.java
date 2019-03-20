package fr.ffessm.doris.android.tools.disk;

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * Hilfsklasse zur Beschreibung eines Devices, womit MountPoints gemeint sind, also
 * unter anderem Partitionen des internen Gerätespeichers, aber auch SD-Karten oder
 * per USB ankoppelbare Geräte. Wird nur von {@link Environment2} benutzt und hat
 * daher keine public constructors. Kann aber für manche Zwecke genutzt werden,
 * daher einige public methods.
 * 
 * Die konkrete Implementierung findet seit Version 1.2 in den Klassen 
 * {@link DeviceIntern} (/data), {@link DeviceExternal} (/mnt/sdcard) und
 * {@link DeviceDiv} (weitere SD-Karten, USB-Geräte) statt.
 * 
 * @see Environment2
 * @see Size
 * @author Jörg Wirtgen (jow@ct.de)
 * @version 1.3
 * 
 */
public abstract class Device  {
	
	
	/**
	 * @return the File on the mount point
	 */
	public abstract File getMountPointFile();
	
	/**
	 * @return Size of the device
	 */
	public abstract Size getSize();
	/**
	 * @return the location of the mount point. This may be not precise on some case
	 */
	public abstract String getMountPoint();
	public abstract String getName();
	public abstract boolean isRemovable();
	public abstract boolean isEmulated();
	public abstract boolean isAvailable();
	public abstract boolean isWriteable();

	//protected void updateState() {}
	
	
	/**
	 * Analogous to Context.getXXXFilesDir, provides a data directory on
	* This unit and creates the directory if needed ..
	* @ Param ctx the Context of the app
	* @ Return a file that is usable as app data path on the
	* Device. When used on the internal or primary memory, the methods of {@ link Context} are used, 
	* When used on the
	* Secondary storage and other devices (USB) an imitation of path is used. If zero is that permission could
	* Android.permission.WRITE_EXTERNAL_STORAGE missing.
	 * @since 1.2
	 */
	public abstract File getFilesDir(Context ctx);
	public abstract File getFilesDir(Context ctx, String s);
	public abstract File getCacheDir(Context ctx);
	
	/**
	 * Liefert analog zu {@link Environment#getExternalStoragePublicDirectory(String)}
	 * ein Datenverzeichnis auf diesem Gerät zurück
	 * @param s der Name des Unterverzeichnis als String, wobei die 
	 * Environment-Konstanten wie {@link Environment#DIRECTORY_DOWNLOADS}
	 * auch funktionieren. Kann null sein, entspricht dann getMountPoint()
	 * @return ein File, das auf das angeforderte Verzeichnis zeigt. Es wird wie die 
	 * 	Environment-Methode nicht erzeugt. null, falls Device auf den internen
	 * 	Speicher (/data) zeigt.
	 * @since 1.2
	 */
	public abstract File getPublicDirectory(String s);
	
	/**
	 * Liefert analog zu {@link Environment#getExternalStorageState()} zurück,
	 * ob das Device gemountet (lesbar) und beschreibbar ist. Alternativ kann man
	 * isWriteable() und isAvailable() nutzen.
	 * 
	 * @return einen String mit Konstanten Environment.MEDIA_xxx, wobei
	 * 	der interne Speicher immer {@link Environment#MEDIA_MOUNTED} und
	 * 	die Zusatzspeicher immer das, {@link Environment#MEDIA_MOUNTED_READ_ONLY}
	 * 	oder {@link Environment#MEDIA_UNMOUNTED} liefern, keine
	 * 	detaillierteren Informationen.
	 * @since 1.2
	 */
	public abstract String getState(); 

	
	/**
	 * Helper method for emulating the getXXXDir methods of {@ link Context}, 
	 * but that is based on the mount point location. this is mainly used in situation where the native method isn't supported
	 * @ Param ctx the Context (for {@ link Context # getPackageName ()})
	 * @ Param s a string describing the pathname within the app-path
	 * @ Return a file with the desired subdirectory; if it doesn't exist, the directory is created
	 */
	protected File getFilesDirLow(Context ctx, String s) {
		if (s!=null && !s.startsWith("/")) s = "/" + s;
		File f = new File(getMountPoint() + DiskEnvironmentHelper.PATH_PREFIX + ctx.getPackageName() + s);
		if (!f.isDirectory() && isWriteable()) 
			f.mkdirs(); 
		return f;
	}


}


