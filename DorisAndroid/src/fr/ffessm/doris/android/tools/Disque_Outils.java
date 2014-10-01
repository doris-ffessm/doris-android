/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2014 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */
package fr.ffessm.doris.android.tools;

import java.io.File;
import java.io.FileFilter;
import java.util.Date;

import fr.ffessm.doris.android.tools.disk.Device;
import fr.ffessm.doris.android.tools.disk.DiskEnvironment;
import fr.ffessm.doris.android.tools.disk.NoSecondaryStorageException;

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
		Log.d(LOG_TAG, "Disque_Outils - getDiskUsage()");
		DisqueUsage_Outils du = new DisqueUsage_Outils();
    	du.accept(inImageFolder);
    	Log.d(LOG_TAG, "Disque_Outils - du.getSize() = "+du.getSize() );
    	return du.getSize();
	}
	
	public long getDiskUsage(File inImageFolder, boolean pipot){

		File[] files = inImageFolder.listFiles();
		  int count = 0;
		  for (File f : files)
		    if (f.isDirectory())
		      count += getDiskUsage(f);
		    else
		      count++;

		  return count;
	}
	
	public String getHumanDiskUsage(long inSize){
		String sizeTexte = "";
		// octet => ko
		inSize = inSize/1024;
        if ( inSize < 1024 ) {
        	sizeTexte = "" + inSize + "\u00A0Ko";
        } else {
        	inSize = inSize / 1024;
        	if ( inSize < 1024 ) {
        		sizeTexte = "" + inSize + "\u00A0Mo";
        	} else {
        		//Pour les Go il faut un peu plus de précision que les entiers
        		float sizeGo = (float)inSize / 1024;
        		sizeTexte = "" + (Math.round(sizeGo * 10.0) / 10.0) + "\u00A0Go";
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
    
    public int getPrimaryExternalStorageNbFiles(String fileStr){
    	File file = DiskEnvironment.getPrimaryExternalStorage().getFilesDir(context, fileStr);
    	if (file.isDirectory()) {
    		return file.list().length;
    	}
    	return 0;
    }
    
    public int getSecondaryExternalStorageNbFiles(String fileStr){
    	try {
	    	File file = DiskEnvironment.getSecondaryExternalStorage().getFilesDir(context, fileStr);
	    	if (file.isDirectory()) {
	    		return file.list().length;
	    	}
		} catch (NoSecondaryStorageException e) {
			Log.e(LOG_TAG, "Erreur détermination SecondaryStorage");
    		return 0;
		}
    	return 0;
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

    // Fonction qui permet d'obtenir une clé unique permettant de distinguer les 2 partitions à un moment donné
    
    // Qd il n'y a qu'une partition interne, Android en affiche qd même une seconde, il n'est donc pas facile
    // de savoir si nous sommes dans le cas de 2 réellement différentes ou non

    // Permet d'obtenir une clé unique permettant de distinguer 2 partitions à un moment donné
    // En fait, dans le cas où il n'y a pas physiquement de Carte SD Interne, Android en a une logique qu'il faut ignorée  
    // la concaténation de la taille de la partition et de la place utilisée en octet doit être à peu près sûr
    public String identifiantPartition(Device device){
    	
    	//Log.d(LOG_TAG, "identifiantPartition() - getMountPoint : "+device.getMountPoint());
    	//Log.d(LOG_TAG, "identifiantPartition() - isRemovable : "+device.isRemovable());
    	//Log.d(LOG_TAG, "identifiantPartition() - taille disque  : "+device.getSize().first+" - place occupée : "+device.getSize().second);

    	// bizarrement device.getSize().first semble parfois changer pour le même device
    	// En comparant en méga (1024*1024=1048576), c'est OK, du coup on regarde aussi la place dispo.
    	// Les 2 appels à cette fonction se succédant très rapidement, 10 Mo n'ont pas pu avoir été écrit
    	int tailleDisque = Math.round(device.getSize().first / 1048576);
    	int tailleDisqueDispo = Math.round(device.getSize().second / 10485760);
    	//Log.d(LOG_TAG, "identifiantPartition() - taille disque  : "+tailleDisque+" Mo - place occupée : "+tailleDisqueDispo+" *10 Mo");

    	return tailleDisque+"-"+tailleDisqueDispo+"-"+device.isRemovable();
    }
    
    
}
