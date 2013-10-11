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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;


/* *********************************************************************
 * Outils
 ********************************************************************** */
public class Outils {
    
	// Inititalisation de la Gestion des Log
	public static Log log = LogFactory.getLog(Outils.class);
    
    public static boolean getFichierUrl(String inUrl, String inFichierRetour) {
    	log.debug("getFichierUrl()- Début");
    	log.debug("getFichierUrl()- url : " + inUrl);
    	log.debug("getFichierUrl()- Fichier Retourné : " + inFichierRetour);
    	
    	InputStream flux = null;
        FileOutputStream fichierUrl = null;

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
            	fichierUrl.close();
                flux.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                log.error("Erreur lors de l'écriture du fichier : " + inFichierRetour);
                return false;
            }
        }
    	
    	
    	log.debug("getFichierUrl()- Fin");
    	return true;
    }
    
	public static String getFichier(File inFichier) {
    	log.debug("getFichier()- Début");
    	log.debug("getFichier()- htmlFiche : " + inFichier);
    	
    	FileInputStream objFile = null;
		try {
			objFile = new FileInputStream(inFichier);
			
			InputStreamReader objReader = new InputStreamReader(objFile, "iso-8859-1");
			BufferedReader objBufferReader = new BufferedReader(objReader);
			StringBuffer objBuffer = new StringBuffer();
			String strLine;
			try {
				while ((strLine = objBufferReader.readLine()) != null) {
					objBuffer.append(strLine);
					objBuffer.append("\n");
				}
				try {
					objFile.close();
					
					log.debug("getFichier()- Fin");
			    	return (objBuffer.toString());
			    	
				} catch (IOException e) {

					e.printStackTrace();
					
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		log.error("Erreur lors de la lecture du fichier : " + inFichier);
     	log.debug("getFichier()- Fin");
		return null;
	}

	
	/* *********************************************************************
     * ciblePage permet de supprimer tout le superflu de la page HTML
     ********************************************************************* */
    public static String ciblePage(String inCodeHtml, String inTypePage) throws IOException{
    	log.debug("ciblePage() - Début");
    	log.debug("ciblePage() - codeHtml : " + inCodeHtml.substring(0, Math.min(50, inCodeHtml.length())));
    	log.debug("ciblePage() - inTypePage : " + inTypePage);
    			
    	String pageANettoyer = inCodeHtml;
    	String typePage = inTypePage;
    	
    	// Suppression des sauts de lignes
    	pageANettoyer.replaceAll("/r/n","");
    	log.debug("ciblePage() - pageANettoyer 10 : " + pageANettoyer.substring(0, Math.min(100, pageANettoyer.length())));
    	
    	// Suppression des espaces inutiles (entre les ><)
    	pageANettoyer.replaceAll(">\\s*<","><");
    	log.debug("ciblePage() - pageANettoyer 20 : " + pageANettoyer.substring(0, Math.min(100, pageANettoyer.length())));
    	
		Source source=new Source(pageANettoyer);
		source.fullSequentialParse();
		Element tableResultats = null;
		
		if (typePage == "RESULTATS" || typePage == "FICHE"){
			// Récupération de la Table des Résultats
			List<? extends Element> listeElementsTable=source.getAllElements(HTMLElementName.TABLE);
			for (Element elementTable : listeElementsTable) {
				log.debug("ciblePage() - elementTable : " + elementTable.toString().substring(0, Math.min(100, elementTable.toString().length())));
				
				List<? extends Attribute> listeAttributs=elementTable.getAttributes();
				for (Attribute attribut : listeAttributs) {
					if (attribut.getName().toLowerCase().equals("width") &  attribut.getValue().equals("820")) {
						log.debug("ciblePage() - Table Trouvée : " + attribut.getName() + " = " +  attribut.getValue());
						tableResultats = elementTable;
						break;
					}
				}
				if (tableResultats != null) break;
			}
		}

		if (typePage == "RECHERCHE"){
			// Récupération de la Table des Résultats
			Element elementTitreGrandsGroupes = source.getFirstElementByClass("titre3");
			log.debug("ciblePage() - titre3 : "+ elementTitreGrandsGroupes.toString().substring(0, Math.min(20,elementTitreGrandsGroupes.toString().length())));
			
			Element elementTable = elementTitreGrandsGroupes.getParentElement().getParentElement().getParentElement();
			log.debug("ciblePage() - table : "+elementTable.toString().substring(0, Math.min(20,elementTable.toString().length())));

			tableResultats = elementTable;

		}
 
		if ( tableResultats != null ){
			log.debug("ciblePage() - longueur tableResultats : "+tableResultats.toString().length());
			log.debug("ciblePage() - tableResultats : "+tableResultats.toString().substring(0, Math.min(50, tableResultats.toString().length())));
			log.debug("ciblePage() - tableResultats : ...");
			log.debug("ciblePage() - tableResultats : "+tableResultats.toString().substring(Math.max(0, tableResultats.toString().length()-50), tableResultats.toString().length() ));
			log.debug("ciblePage() - Fin");
		
			return tableResultats.toString();
		
		} else {
			log.warn("ciblePage() - tableResultats == null");
			log.debug("ciblePage() - Fin");
			
			return null;
		}
    }

	
    public static String nettoyageCaracteres(String texteANettoye) {
    	log.debug("nettoyageCaracteres() - Début");
    	log.debug("nettoyageCaracteres() - texteANettoye : " + texteANettoye);
		String texteNettoye = texteANettoye;
		
		//if (LOG) Log.v(TAG, "nettoyageCaracteres() - texteNettoye : " + texteNettoye.charAt(7) + " - " + texteNettoye.codePointAt(7));
		
		// A priori seuls les caratères de table Unicode "Latin étendu A" nécessite une transcodif.
		// en français seul oe
		
		//œ oe 
		texteNettoye = texteNettoye.replaceAll("\u009C", "\u0153");
		//Œ OE
		texteNettoye = texteNettoye.replaceAll("\u008C", "\u0152");
				
		log.debug("nettoyageCaracteres() - texteNettoye : " + texteNettoye);
		
		log.debug("nettoyageCaracteres() - Fin");
		return texteNettoye;
	}

    /*
    static void enregistreXML(Boolean nouveauFichier, File fichierXML, org.jdom2.Element arbreXMLaAjouter)
    {
    	log.debug("enregistreXML() - Début");
    	log.debug("enregistreXML() - nouveauFichier : " + nouveauFichier);
    	log.debug("enregistreXML() - fichierXML : " + fichierXML);
    	log.debug("enregistreXML() - arbreXMLaAjouter : " + arbreXMLaAjouter);
    	
    	org.jdom2.Element racineFichier = null;
    	org.jdom2.Document document = null;
    	
    	if (fichierXML.exists() && !nouveauFichier){
			log.debug("enregistreXML() - Le fichier : " + fichierXML + " existe, l'arbre est donc lu.");

			org.jdom2.input.SAXBuilder sxb = new org.jdom2.input.SAXBuilder();
			try
			{
				document = sxb.build(fichierXML);
				racineFichier = document.getRootElement();
				racineFichier.addContent(arbreXMLaAjouter);
			}
			catch(Exception e){
				e.printStackTrace();
			}

		} else {
			log.debug("enregistreXML() - Le fichier : " + fichierXML + " n'existe pas, l'arbre est donc créé.");
			
			try{
				
				
				//Si nouveauFichier = Faux et qu'il n'est pas déjà créé, ce doit être pour faire de
				// l'append donc on crée un cadre
				if (!nouveauFichier) {
					racineFichier = new org.jdom2.Element("BaseDoris");
					
					SimpleDateFormat formatDate = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss" );
					racineFichier.setAttribute("DateCreation", formatDate.format(new Date()) );
					racineFichier.setAttribute("UrlRacineSite", Extraction.racineSite );
					
					racineFichier.addContent(arbreXMLaAjouter);
					
				} else {
					
					// Sinon on complète juste le xml avec la date du jour
					SimpleDateFormat formatDate = new SimpleDateFormat ("dd/MM/yyyy HH:mm:ss" );
					arbreXMLaAjouter.setAttribute("DateCreation", formatDate.format(new Date()) );
					arbreXMLaAjouter.setAttribute("UrlRacineSite", Extraction.racineSite );
					
					racineFichier = arbreXMLaAjouter;
				}
				document = new org.jdom2.Document(racineFichier);
			}catch (Throwable e) {
				e.printStackTrace();
			} 
		}
    	
    	try
    	{
    		org.jdom2.output.XMLOutputter sortie = new org.jdom2.output.XMLOutputter(org.jdom2.output.Format.getPrettyFormat());
    		sortie.output(document, new FileOutputStream(fichierXML));
    	}
		catch (java.io.IOException e){
			e.printStackTrace();
		}
    	log.debug("enregistreXML() - Fin");
    }
    
    static void enregistreHTML(File fichierHTML, String codeHtml)
    {
    	log.debug("enregistreHTML() - Début");
    	log.debug("enregistreHTML() - fichierDestination : " + fichierHTML);
    	log.debug("enregistreHTML() - codeHtml.length() : " + codeHtml.length());
     	
    	if (! fichierHTML.exists()){
			log.error("enregistreHTML() - Le fichier de destination : " + fichierHTML + " n'existe pas !");

		} else {
			
			String fichierHtmlContenu = getFichier(fichierHTML);
			log.debug("enregistreHTML() - fichierHtmlContenu.length() : " + fichierHtmlContenu.length());
			
			if (!fichierHtmlContenu.contains("<!--REPERE-->")){
				log.error("enregistreHTML() - Le fichier de destination est mal formé !");

			} else {
				codeHtml = codeHtml + "<!--REPERE-->";
				log.debug("enregistreHTML() - codeHtml.length() : " + codeHtml.length());
		     					
				fichierHtmlContenu = fichierHtmlContenu.replaceAll("<!--REPERE-->", codeHtml);
				log.debug("enregistreHTML() - fichierHtmlContenu.length() : " + fichierHtmlContenu.length());
		     	

				Writer out;
				try {
					out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fichierHTML), "ISO-8859-1"));
					try {
						out.write(fichierHtmlContenu);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						out.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
								
			}
			
		}
    	
    	log.debug("enregistreHTML() - Fin");
    }
    */
    
    /*
    static String xmlToTableauFichesHtml(File fichierXML)
    {
    	log.debug("xmlToTableauFichesHtml() - Début");
    	log.debug("xmlToTableauFichesHtml() - fichierXML : " + fichierXML);
     	
    	StringBuffer tableauHtml = new StringBuffer();
    	
    	org.jdom2.Document document = null;

		org.jdom2.input.SAXBuilder sxb = new org.jdom2.input.SAXBuilder();
		try
		{
			document = sxb.build(fichierXML);
			
			Iterator Fiche = document.getRootElement().getChildren("Fiche").iterator();

	        while (Fiche.hasNext()) {
	        	//org.jdom2.Element elemFiche = (org.jdom2.Element) Fiche.next();

	        	tableauHtml.append( xmlFicheToHtml ( (org.jdom2.Element) Fiche.next() ) );

	        }
       
	        
		}
		catch(Exception e){
			e.printStackTrace();
		}

		
		log.debug("tableauHtml = " + tableauHtml.toString());
		log.debug("xmlToTableauFichesHtml() - Fin");
    	return tableauHtml.toString();
    }
    */
    /*
    static String xmlFicheToHtml(org.jdom2.Element elemFiche)
    {
    	log.debug("xmlFicheToHtml() - Début");
    	log.debug("xmlFicheToHtml() - elemFiche : " + elemFiche);
     	
    	StringBuffer tableauHtml = new StringBuffer();
    	


    	org.jdom2.Element elemEntete = elemFiche.getChild("Entete");
    	log.debug("Fiche - Ref. = " + elemEntete.getAttribute("Reference").getValue());
		
    	tableauHtml.append("<tr bgcolor=\"#ffffff\" onMouseOver=\"this.bgColor='#F3F3F3';\" onMouseOut=\"this.bgColor='#ffffff';\" style=\"height: 150px;\">\n");
    	tableauHtml.append("<td width=\"5\" valign=\"middle\" class=\"listeHorsLigne\" bgcolor=\"#cccccc\">&nbsp;</td>\n");
		tableauHtml.append("<td valign=\"top\" class=\"listeHorsLigne\">\n");
    	
        tableauHtml.append("<a href=\"fiche" + elemEntete.getAttribute("Reference").getValue() + ".html\">\n");
        tableauHtml.append(elemEntete.getAttribute("Reference").getValue() + "&nbsp;-&nbsp;");
        tableauHtml.append("<em>" + elemEntete.getChild("NomScientifique").getValue() + "</em>&nbsp;-&nbsp;");
        tableauHtml.append(elemEntete.getChild("NomFrancais").getValue() + "<br/><br/>\n");
        
        
        org.jdom2.Element elemAutresDenominations = elemEntete.getChild("AutresDenominations"); 
        if (elemAutresDenominations != null) {
            Iterator Denominations = elemAutresDenominations.getChildren("Denominations").iterator();
            
            while (Denominations.hasNext()) {
	        	org.jdom2.Element elemDenominations = (org.jdom2.Element) Denominations.next();
	        	
	        	tableauHtml.append(elemDenominations.getValue() + "<br/><br/>\n");
            }
        }
        tableauHtml.append("</a></td>\n");
        
        tableauHtml.append("<td  valign=\"top\" width=\"118\" class=\"listeHorsLigne\">\n");
        tableauHtml.append("<a href=\"fiche" + elemEntete.getAttribute("Reference").getValue() + ".html\">\n");
        tableauHtml.append("<font color=\"#999999\"><em>\n");
        
        String typeFiche = elemEntete.getAttribute("TypeFiche").getValue();
        if (typeFiche.equals("F")){
        	tableauHtml.append("Fiche publi&eacute;e");
        } else if (typeFiche.equals("FR")){
        	tableauHtml.append("Fiche en cours de r&eacute;daction");
        } else if (typeFiche.equals("FP")){
        	tableauHtml.append("Fiche propos&eacute;e");
        }
        tableauHtml.append("</em></font><br/>\n");
        tableauHtml.append( elemEntete.getChild("Region").getValue() );
        tableauHtml.append("</a></td>\n");
        
        tableauHtml.append("<td class=\"listeHorsLigne\" width=\"118\" valign=\"top\" bgcolor=\"#F3F3F3\">");
        tableauHtml.append("<a href=\"fiche" + elemEntete.getAttribute("Reference").getValue() + ".html\">\n");
        
        tableauHtml.append("<img border=\"0\" height=\"100\" style=\"padding-top: 5px;\" ");
        tableauHtml.append("src=\"" + elemEntete.getChild("Images").getChild("Image").getAttribute("UrlVignette").getValue() + "\"/>");
        tableauHtml.append("</a></td></tr>\n");
      
	        

		
		log.debug("tableauHtml = " + tableauHtml.toString());
		log.debug("xmlToTableauFichesHtml() - Fin");
    	return tableauHtml.toString();
    }
    */
}