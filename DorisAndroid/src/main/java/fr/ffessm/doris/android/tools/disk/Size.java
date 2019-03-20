package fr.ffessm.doris.android.tools.disk;

import java.io.File;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.StatFs;
import android.util.Pair;

/**
 * Mini class for determining a partition size that even with old Android versions
  * Collapses and from Gingerbread uses the File methods. It uses a {@ link Pair} to
  * Storing the information, wherein:
  * <ul>
  * <li> First: free space (free), determined by getUsableSpace
  * <li> Secondary: Total memory (size), determined by getTotalSpace
  * </ Ul>
  * The class constructor is private, is created an object using the static
  * Function getSpace (File). 
 */
public class Size extends Pair<Long,Long> {
	
	private Size(long free, long size) { super(free, size); }

	/**
	 * Try to guess how big the storage medium, on which the Size object
	 * Lies. Only provides a meaningful value when on the storage medium except
	 * The Size object either no or only little other partitions reside.
	 *
	 * @ Return the next higher power of two on the determined size (secondary)
	 * So bsp. 16 GB for 14 GB. If second is 0, returns 1.
	 */
	public long guessSize() {
		if (second==0) return 0;
		long g;
		if (second>1024*1024*1024) g = 1024*1024*1024;
		else if (second>1024*1024) g = 1024*1024;
		else g = 1;
		while (second>g) g *= 2;
		return g;
	}

	
	/**
	 * Get the size and free space of the given {@ link File}. from
	 * Android 2.3 to be the File methods {@ link File # getUsableSpace ()} and
	 * {@ Link File # getTotalSpace ()} used in older versions of Android, a
	 * Helper {@ link} statfs generated.
	 *
	 * @ Param f the directory whose size is to be determined
	 * @ Return a {@ link Pair}, contains the size and free space of the partition
	 * The f shows or (0.0) if there was an error or f is zero.
	 */
	@SuppressLint("NewApi")
	public static Size getSpace(File f) {
		if (f!=null) try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				// Gingerbread hat Größe/freier Platz im File
				return new Size(f.getUsableSpace(), f.getTotalSpace());
			} else {
				// vor Gingerbread muss der StatFs-Krams ran; wichtig ist die long-Wandlung
				StatFs fs = new StatFs(f.getAbsolutePath());
				return new Size((long)fs.getAvailableBlocks()*fs.getBlockSize(), (long)fs.getBlockCount()*fs.getBlockSize());
			}
		} catch (Exception e) { }
		return new Size((long)0, (long)0);
	}
}