package fr.ffessm.doris.android.tools;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

public class Disque_Outils {
	private static final String LOG_TAG = Disque_Outils.class.getCanonicalName();
		
	private Context context;
	
	public Disque_Outils(Context context){
		this.context = context;
	}

	public long getDiskUsage(File inImageFolder){
		DisqueUsage_Outils du = new DisqueUsage_Outils();
    	du.accept(inImageFolder);
    	return du.getSize();
	}
	public String getHumanDiskUsage(long inSize){
		String sizeTexte = "";
		// octet => ko
		inSize = inSize/1024;
        if ( inSize < 1024 ) {
        	sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Ko";
        } else {
        	inSize = inSize / 1024;
        	if ( inSize < 1024 ) {
        		sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Mo";
        	} else {
        		inSize = inSize / 1024;
        		sizeTexte = String.valueOf(Math.round(inSize)) + "\u00A0Go";
        	}
        }
    	return sizeTexte;
	}


	
    public int clearFolder(File inFolder, int inNbJours){
		int deletedFiles = 0;
	    if (inFolder!= null && inFolder.isDirectory()) {
	    	Log.d(LOG_TAG, "clearFolder() - inFolder : "+inFolder);
	        try {
	            for (File child:inFolder.listFiles()) {

	                //first delete subdirectories recursively
	                if (child.isDirectory()) {
	                    deletedFiles += clearFolder(child, inNbJours);
	                }

	                //then delete the files and subdirectories in this dir
	                //only empty directories can be deleted, so subdirs have been done first
	                if (child.lastModified() < new Date().getTime() - inNbJours * DateUtils.DAY_IN_MILLIS) {
	                    if (child.delete()) {
	                        deletedFiles++;
	                    }
	                }
	            }
	        }
	        catch(Exception e) {
	        	Log.e(LOG_TAG, String.format("Failed to clean the folder, error %s", e.getMessage()));
	        }
	    }
	    Log.d(LOG_TAG, "clearFolder() - Fichiers effacés : "+deletedFiles);
	    return deletedFiles;
	}
 
    public class DisqueUsage_Outils implements FileFilter {
    	public DisqueUsage_Outils() {
    	};

    	private long size = 0;

    	public boolean accept(File file) {
    		if (file.isFile())
    			size += file.length();
    		else
    			file.listFiles(this);
    		return false;
    	}

    	public long getSize() {
    		return size;
    	}
    }

}
