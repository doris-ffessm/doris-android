/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2015 - FFESSM
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
import java.util.Date;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import fr.ffessm.doris.android.tools.disk.Device;
import fr.ffessm.doris.android.tools.disk.DiskEnvironmentHelper;

public class Disque_Outils {
	private static final String LOG_TAG = Disque_Outils.class.getSimpleName();
		
	// type pour le choix de l'emplacement des photos
	public enum ImageLocation {
		// Mémoire Interne où sont installées les Applications
		// Certains appareils n'ont que cet emplacement mémoire
		APP_INTERNAL,
		// Mémoire Interne dédiée aux données des applications (n'existe pas toujours)
		// Surtout même qd elle n'existe pas physiquement elle existe logiquement,
		// il faut donc les différencier ... voir identifiantPartition() plus bas
		PRIMARY,
		// Carte Mémoire amovible
		SECONDARY
	}
	
	private Context context;
	
	private boolean isPrimaryExternalStorageExist = false;
	private boolean isSecondaryExternalStorageExist = false;
	
	public Disque_Outils(Context context){
		this.context = context;
		
		refresh();
	}
	
    public void refresh(){
    	isPrimaryExternalStorageExist = !DiskEnvironmentHelper.getPrimaryExternalStorage().isEmulated();
    	
    	try{
    		isSecondaryExternalStorageExist = DiskEnvironmentHelper.isSecondaryExternalStorageAvailable(context);
    	} catch (Exception e){
    		Log.e(LOG_TAG, String.format("Problème avec isSecondaryExternalStorageExist : error %s", e.getMessage()));
    	}	
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

	public int nbFileInFolder(File inFolder){
		//Log.d(LOG_TAG, "nbFileInFolder() - inFolder : "+inFolder+" - length : "+inFolder.list().length);
		
		int nbFiles = 0;
		
		//TODO : Crado mais "temporaire"
		// c'est utile pour la phase transitoire
		// Si on a beaucoup d'images dans le dossier c'est sans doute que les images sont téléchargées 
		// à l'ancienne manière.
		// Si on a moins de 62 (26 * 2 + 10) fichiers on peut supposer que ce sont des dossiers.
		// De toutes les façons ça ira vite avec si peu de fichiers
		try {
			
			if (inFolder.list().length > 100) return (int) inFolder.list().length;
		
		} catch(Exception e) {
        	Log.e(LOG_TAG, String.format("Le dossier n'existe pas : error %s", e.getMessage()));
		}
		
		for (File child:inFolder.listFiles()) {
			try {
			
				if (child.isDirectory()) {
					if (child.exists()) nbFiles += child.list().length;
	            } else {
	            	nbFiles++;
	            }
				
			} catch(Exception e) {
	        	Log.e(LOG_TAG, String.format("Le dossier n'existe pas : error %s", e.getMessage()));
			}
		}
		
		return nbFiles;
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

    
    public boolean isStorageExist(ImageLocation imageLocation){
    	switch (imageLocation) {
		case APP_INTERNAL :
			return true;
		case PRIMARY :
			return isPrimaryExternalStorageExist;
		case SECONDARY :
			return isSecondaryExternalStorageExist;
		default:
			return false;
    	}
    	
    }
    
    
    // Fonction qui permet d'obtenir une clé unique permettant de distinguer les 2 partitions à un moment donné
    
    // Qd il n'y a qu'une partition interne, Android en affiche qd même une seconde, il n'est donc pas facile
    // de savoir si nous sommes dans le cas de 2 réellement différentes ou non

    // Permet d'obtenir une clé unique permettant de distinguer 2 partitions à un moment donné
    // En fait, dans le cas où il n'y a pas physiquement de Carte SD Interne, Android en a une logique qu'il faut ignorée  
    // la concaténation de la taille de la partition et de la place utilisée en octet doit être à peu près sûr
    public String identifiantPartition(Device device){
    	
    	Log.d(LOG_TAG, "identifiantPartition() - getName : "+device.getName());
    	Log.d(LOG_TAG, "identifiantPartition() - getMountPoint : "+device.getMountPoint());
    	Log.d(LOG_TAG, "identifiantPartition() - isRemovable : "+device.isRemovable());
    	Log.d(LOG_TAG, "identifiantPartition() - taille disque  : "+device.getSize().first+" - place occupée : "+device.getSize().second);

    	// bizarrement device.getSize().first semble parfois changer pour le même device
    	// En comparant en méga (1024*1024=1048576), c'est OK, du coup on regarde aussi la place dispo.
    	// Les 2 appels à cette fonction se succédant très rapidement, 10 Mo n'ont pas pu avoir été écrit
    	
    	int tailleDisque = Math.round(device.getSize().first / 1048576);
    	int tailleDisqueDispo = Math.round(device.getSize().second / 10485760);
    	//Log.d(LOG_TAG, "identifiantPartition() - taille disque  : "+tailleDisque+" Mo - place occupée : "+tailleDisqueDispo+" *10 Mo");

    	return tailleDisque+"-"+tailleDisqueDispo+"-"+device.isRemovable();
    }
    
    
}
