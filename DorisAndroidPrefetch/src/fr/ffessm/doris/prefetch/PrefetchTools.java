/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
 * Auteurs : Guillaume Mo <gmo7942@gmail.com>
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

package fr.ffessm.doris.prefetch;

import java.io.File;
import java.io.FileInputStream;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PrefetchTools {
	
	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchTools.class);
	
	
	// Vérifie que le fichier existe
	public static boolean isFileExistingPath(String fichierPath){
		File fichier = new File(fichierPath);
		if (!fichier.exists()) {
			return false;
		} else {
			if (fichier.isDirectory()) {
				return false;
			}
		}
		return true;
	}
	
	// Permet de Zipper un Dossier
	// Très inspiré de http://www.devx.com/tips/Tip/14049
	public static void zipDossier(String dossierAZipper, ZipOutputStream zipOS) 
	{ 
	    try {
	    	
           File dossier = new File(dossierAZipper); 
           String[] contenuDossier = dossier.list(); 

           // ???
           byte[] readBuffer = new byte[2156]; 
           int bytesIn = 0;
           
           // Parcours du contenu
           for ( int i=0; i < contenuDossier.length; i++ ) {
        	   File fichier = new File(dossier, contenuDossier[i]); 
        	   
        	   // Si c'est un Dossier => récurrence
        	   if ( fichier.isDirectory() ) { 
        		   String sousDossier = fichier.getPath(); 
        		   zipDossier(sousDossier, zipOS); 
        		   continue; 
        	   } 
        	   
        	   // Écriture du fichier dans le ZIP
        	   FileInputStream fileIS = new FileInputStream(fichier); 
        	   ZipEntry zipEntry = new ZipEntry( fichier.getPath() ); 

        	   zipOS.putNextEntry(zipEntry);
        	   
        	   while((bytesIn = fileIS.read(readBuffer)) != -1) { 
        		   zipOS.write(readBuffer, 0, bytesIn); 
        	   }
        	   
        	   fileIS.close(); 
           }
           
		} catch(Exception e) { 
			log.info("Erreur lors du ZIP du dossier : "+dossierAZipper);
			e.printStackTrace();
		} 
	}
}

