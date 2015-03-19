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

package fr.ffessm.doris.prefetch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ffessm.doris.android.sitedoris.Constants.FileHtmlKind;

public class PrefetchTools {
	
	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchTools.class);
	
	// Constructeur
	public PrefetchTools(){
		
	}
	
	// Vérifie que le fichier existe
	public boolean isFileExistingPath(String fichierPath){
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

	
    public boolean getFichierFromUrl(String inUrl, String inFichierRetour) {
    	//log.debug("getFichierUrl()- Début");
    	//log.debug("getFichierUrl()- url : " + inUrl);
    	//log.debug("getFichierUrl()- Fichier à Retourner : " + inFichierRetour);
    	
    	InputStream flux = null;
        FileOutputStream fichierUrl = null;

        // TODO : TENTATIVE DE RALLENTISSEMENT EXAGERE POUR VOIR SI SERVEUR SUPPORTE
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try
        {
            URL url = new URL(inUrl);
            URLConnection connection = url.openConnection();
            int fileLength = connection.getContentLength();

            if (fileLength == -1)
            {
                log.error("URL Invalide : " + inUrl);
                return false;
            }

            flux = connection.getInputStream();
            fichierUrl = new FileOutputStream(inFichierRetour);
            byte[] buffer = new byte[1024];
            int read;

            while ((read = flux.read(buffer)) > 0)
            	fichierUrl.write(buffer, 0, read);
            fichierUrl.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            log.error("Erreur lors du téléchargement du fichier : " + inUrl);
            return false;
        }
        finally
        {
            try
            {  	
            	if(fichierUrl!=null) fichierUrl.close();            	
            	if(flux!=null)  flux.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                log.error("Erreur lors de l'écriture du fichier : " + inFichierRetour);
                return false;
            }
        }
    	
    	//log.debug("getFichierUrl()- Fin");
    	return true;
    }


	
	public String getFichierTxtFromDisk(File inFichier, FileHtmlKind fileKind) {
    	//log.debug("getFichierTxtFromDisk()- Début");
    	//log.debug("getFichierTxtFromDisk()- Fichier : " + inFichier);
		
		try {

			//Top d'après JavaDoc : BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			//InputStreamReader objReader = new InputStreamReader(objFile, "iso-8859-1");
			//BufferedReader objBufferReader = new BufferedReader(objReader);
			BufferedReader objBufferReader = new BufferedReader(
					new InputStreamReader(
							new FileInputStream(inFichier), "iso-8859-1"));
			
			//log.debug("getFichierTxtFromDisk()- 020");
			
			StringBuilder objBuilder = new StringBuilder();
			
			try {
				//log.debug("getFichierTxtFromDisk()- 030");
				String strLine;
				while ((strLine = objBufferReader.readLine()) != null) {
					switch (fileKind) {
					case LISTE_FICHES:
						// Supprimer dès ici toutes les lignes <TD ne nous servant pas permet de gagner
						// bcp de place en mémoire, le poids des fichiers html est en effet divisé par 2 
						if (
								!( strLine.trim().startsWith("<td width=") )
								|| ( strLine.trim().startsWith("<td width=\"75%\"") )
							){
								objBuilder.append(strLine.trim());
								objBuilder.append("\n");
						}
						break;
					default:
						objBuilder.append(strLine.trim());
						objBuilder.append("\n");
					}
				}
				objBufferReader.close();
					
				//log.debug("getFichierTxtFromDisk()- objBuffer.length : "+objBuilder.toString().length());
		    	//log.debug("getFichierTxtFromDisk()- Fin");
		    	return (objBuilder.toString());
			    	

			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		log.error("Erreur lors de la lecture du fichier : " + inFichier);
     	//log.debug("getFichierTxtFromDisk()- Fin");
		return null;
	}
	
	
	// Permet de Zipper un Dossier
	// Très inspiré de http://www.devx.com/tips/Tip/14049
	public void zipDossier(String dossierAZipper, ZipOutputStream zipOS) 
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

