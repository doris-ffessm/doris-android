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
