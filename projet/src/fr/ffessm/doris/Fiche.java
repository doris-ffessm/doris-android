/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
Copyright du Code : Guillaume Moynard  ([29/05/2011]) 

Guillaume Moynard : gmo7942@gmail.com

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
* ********************************************************************
* ********************************************************************* */

package fr.ffessm.doris;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import android.content.Context;
import android.util.Log;

/* *********************************************************************
 * Classe de chacun des resultats trouvés 
 ********************************************************************** */
public class Fiche
{
    private final static String TAG = "Fiche";
    private final static Boolean LOG = false;
	   
    private Context appContext;
    
	public String ref;
	public String nom = "";
	public String nom_scient = "";
	public String htmlEntete = "";
	public String urlVignette = "";
	public String urlImage = "";
	public String urlFiche = "";
	public String htmlFiche = "";
	
	public String ficheType = "";
	public String ficheTypeCouleurFond = "#000000";
	public String ficheTypeCouleurTexte = "#FFFFFF";
	public String ficheNomLatin = "";
	public String ficheRegion = "";
	public String ficheNomFrancais = "";
	public String ficheNomCommun = "";
	public String ficheNomInter = "";
	
	public List<Detail> ficheListeDetails = new ArrayList<Detail>(20);
	
	public List<String> ficheListeImgUrl = new ArrayList<String>(20);
	public List<String> ficheListeImgVigUrl = new ArrayList<String>(20);
	public List<String> ficheListeImgTexte = new ArrayList<String>(20);
	
	public List<String> ficheListeLiensUrl = new ArrayList<String>(20);
	public List<String> ficheListeLiensTexte = new ArrayList<String>(20);
	public boolean ficheListeLiensAffiche = false;
	
	Fiche (Context inContext){
		if (LOG) Log.d(TAG, "Fiche() - Début");
		if (LOG) Log.d(TAG, "Fiche() - inContext : " + inContext.toString());
		
		appContext = inContext;
		
		if (LOG) Log.d(TAG, "Fiche() - Fin");
	}

	Fiche (Context inContext, String inRef){
		if (LOG) Log.d(TAG, "Fiche() - Début");
		if (LOG) Log.d(TAG, "Fiche() - inContext : " + inContext.toString());
		if (LOG) Log.d(TAG, "Fiche() - inRef : " + inRef);
		
		appContext = inContext;
		
		ref = inRef;
		if (LOG) Log.v(TAG, "Fiche() - ref : " + ref);
		
		urlFiche = appContext.getString(R.string.cst_urlFiche_racine)+ref;
		if (LOG) Log.v(TAG, "Fiche() - urlFiche : "+urlFiche);
		
		if (LOG) Log.d(TAG, "Fiche() - Fin");
	}
	
	
	//Initialisation de l'entête de la fiche à partir du code HTML d'une recherche
	public void setEnteteFiche(String inCodeHtml){
		if (LOG) Log.d(TAG, "setEnteteFiche(String) - Début");
		
		htmlEntete = inCodeHtml;
		if (LOG) Log.v(TAG, "setEnteteFiche(String) - "+this.htmlEntete.substring(0, 100));
		
		Source source=new Source(htmlEntete);
		
		//Parcourt du code html pour récupérer les fiches affichées
		List<? extends Element> listeElements=source.getAllElements();
		for (Element element : listeElements) {
			if (LOG) Log.v(TAG, "setEnteteFiche(String) - 00100 - "+element.toString().substring(0, Math.min(element.toString().length(), 100)));
			
			List<? extends Attribute> listeAttributs=element.getAttributes();
			for (Attribute attribut : listeAttributs) {
				if (LOG) Log.v(TAG, "setEnteteFiche(String) - 00200 - "+attribut.getName()+"="+attribut.getValue());
				
				//Récupération du numéro de la fiche
				if (element.getName() == HTMLElementName.A){
					if (LOG) Log.v(TAG, "setEnteteFiche(String) - 00300 - A");
					
					if (attribut.getName().toLowerCase().equals("href")) {
						if (LOG) Log.v(TAG, "setEnteteFiche(String) - 00310 - href="+attribut.getValue());
						
						// 1ère partie du test pour l'acces résultat par la recherche
						// 2ème partie du test pour les résultats après navigation dans l'arbre
						if ( attribut.getValue().startsWith("fiche3.asp") || attribut.getValue().startsWith("fiche.asp") ) {
							
							ref = attribut.getValue().replaceAll(".*fiche_numero=", "");
							if (LOG) Log.v(TAG, "setEnteteFiche(String) - ref 1 : "+ref);
							
							ref = ref.replaceAll("&origine=.*", "");
							if (LOG) Log.v(TAG, "setEnteteFiche(String) - ref 2 : "+ref);
							
							urlFiche = "http://doris.ffessm.fr/fiche.asp?origine=accueil&fiche_numero="+ref;
							if (LOG) Log.d(TAG, "setEnteteFiche(String) - urlFiche : "+urlFiche);
							
							break;
						}
					}
				}
				
				//Récupération du Type de la Fiche
				if (element.getName() == HTMLElementName.TR){
					if (LOG) Log.v(TAG, "setEnteteFiche(String) - 00320 - TR");
					if (attribut.getName().toLowerCase().equals("bgcolor")) {
												
						if ( attribut.getValue().equals("#ababab") ) {
							//Fiche Complète
							ficheType = "F";
						} else if ( attribut.getValue().equals("#FAEDC0") ) {
							//Fiche Proposée
							ficheType = "FP";
						} else if ( attribut.getValue().equals("#D2E8FF") ) {
							//Fiche en cours de rédaction
							ficheType = "FR";
						} else {
							//Type Inconnu
							ficheType = "I";
						}
						if (LOG) Log.d(TAG, "setEnteteFiche(String) - ficheType : "+ficheType);
						
						//Détermination des couleurs liées au type de la fiche
						setTypeFicheCouleurs(ficheType);
						break;
					}
				}
				
				// Récupération de l'url des Images
				if (element.getName() == HTMLElementName.IMG){
					if (LOG) Log.v(TAG, "setEnteteFiche(String) - 00330 - IMG");
					if (attribut.getName().toLowerCase().equals("src")) {
						
						if ( attribut.getValue().contains("gestionenligne/photos_fiche_vig/") ) {
							
							String refImg = attribut.getValue().replaceAll(".*gestionenligne/photos_fiche_vig/", "");
							if (LOG) Log.d(TAG, "setEnteteFiche(String) - refImg : "+refImg);
						
							urlVignette = appContext.getString(R.string.cst_urlVignette_racine)+refImg;
							if (LOG) Log.d(TAG, "setEnteteFiche(String) - urlVignette : "+urlVignette);
							
							urlImage = appContext.getString(R.string.cst_urlImage_racine)+refImg;
							if (LOG) Log.d(TAG, "setEnteteFiche(String) - urlImage : "+urlImage);
							break;
						}
					}
				}
				
				//Récupération du Nom Vernaculaire
				if (element.getName() == HTMLElementName.TD){
					if (LOG) Log.v(TAG, "setEnteteFiche(String) - 00350 - TD");
					if (attribut.getName().toLowerCase().equals("class")) {
						
						if ( attribut.getValue().equals("gris_gras") ) {
							
							nom = Outils.nettoyageCaracteres(element.getContent().toString());
							if (LOG) Log.d(TAG, "setEnteteFiche(String) - nom : "+nom);
							break;
						}
					}
				}
			}
			
			// Récupération du Nom Latin
			if (element.getName() == HTMLElementName.EM){
				if (LOG) Log.v(TAG, "setEnteteFiche(String) - 00380 - EM");
				nom_scient = Outils.nettoyageCaracteres(element.getContent().toString());
				if (LOG) Log.d(TAG, "setEnteteFiche(String) - nom_scient. : "+nom_scient);
				//break;
			}
		}
		
		
		//En Téléchargeant ici les images des vignettes, l'interface parait plus fluide ensuite
		if ( urlVignette != "") {
			if (LOG) Log.v(TAG, "setEnteteFiche() - Recup Vignette - urlVignette : " + urlVignette);
			Outils.getImage(appContext, urlVignette, ref, "");
		}
		
		if (LOG) Log.d(TAG, "setEnteteFiche(String) - Fin");
	}

	
	// Permet de savoir si l'entête a déjà été créée
	public boolean getEnteteExistence(){
		if (LOG) Log.d(TAG, "getEnteteExistence() - Début");
		boolean existe;
		
		if (htmlEntete.length() != 0 ){
			existe = true;
		}else{
			existe = false;
		}
		if (LOG) Log.d(TAG, "getEnteteExistence() - Fin");
		return existe;
	}
	
	
	//Initialisation de l'entête à partir d'une autre fiche
	public void setEnteteFicheFromFiche(Fiche inFiche){
		if (LOG) Log.d(TAG, "setEnteteFicheFromFiche(Fiche) - Début");
		if (LOG) Log.d(TAG, "setEnteteFicheFromFiche(Fiche) - inFiche.ref : " + inFiche.ref);
		
		this.htmlEntete = inFiche.htmlEntete;
							
		this.nom_scient  = inFiche.nom_scient;

		this.urlVignette = inFiche.urlVignette;
		
		this.urlImage = inFiche.urlImage;
		
		this.nom = inFiche.nom;

		if (LOG) Log.d(TAG, "setEnteteFicheFromFiche(Fiche) - Fin");
	}
	
	
	public void getHtmlFiche() throws IOException{
    	if (LOG) Log.d(TAG, "getHtmlFiche()- Début");
    	String url = urlFiche;
    	String cle_fichier_cache = "020-Fiche£" + ref + "£" + nom;
    	
    	if (LOG) Log.v(TAG, "getHtmlFiche()- url = " + urlFiche);
    	if (LOG) Log.v(TAG, "getHtmlFiche()- cle_fichier_cache = " + cle_fichier_cache);
    	
    	htmlFiche = Outils.getHtml(appContext, url, cle_fichier_cache);
    	
    	if (LOG) Log.d(TAG, "getHtmlFiche()- Fin");
	}
	
	// Permet de savoir si la Fiche a déjà été créée
	public boolean getFicheExistence(){
		if (LOG) Log.d(TAG, "getFicheExistence() - Début");
		boolean existe;
		
		if (htmlFiche.length() != 0 ){
			existe = true;
		}else{
			existe = false;
		}
		if (LOG) Log.d(TAG, "getFicheExistence() - Fin");
		return existe;
	}
	
	public void getFiche() throws IOException{
    	if (LOG) Log.d(TAG, "getFiche()- Début");

    	int i;
    	String listeLienRencontre = "";
    	
    	htmlFiche = htmlFiche.replace("&nbsp;</td>", "</td>");
    	htmlFiche = Outils.ciblePage(htmlFiche, "FICHE");
    	
    	htmlFiche = htmlFiche.replace("<strong>", "");
    	htmlFiche = htmlFiche.replace("</strong>", "");
    	htmlFiche = htmlFiche.replace("<em>", "");
    	htmlFiche = htmlFiche.replace("</em>", "");
    	htmlFiche = htmlFiche.replace("<br>", "");
    	htmlFiche = htmlFiche.replace("<br/>", "");
    	
    	if (LOG) Log.v(TAG, "getFiche() - ficheType : " + ficheType);
    	if (LOG) Log.v(TAG, "getFiche() - htmlFiche : " + htmlFiche.substring(0, 200));
    	
    	
    	//Dans le cas des liens depuis une autre fiche, suivant la recherche effectuée et 
    	// l'ordre de visite, ficheType peut être vide (si jamais été visité ni trouvé dans recherche)
    	//On le calcul donc dans ce cas
    	if (ficheType == "") {
    		if (htmlFiche.contains("Fiche en cours de rédaction")) {
    			ficheType = "FR";
    		} else if (htmlFiche.contains("Fiche proposée")) {
    			ficheType = "FP";
    		} else {
    			ficheType = "F";
    		}
    		if (LOG) Log.v(TAG, "getFiche() - ficheType (2) : " + ficheType);
    		
    		//Détermination des couleurs liées au type de la fiche
			setTypeFicheCouleurs(ficheType);
    	}
 
		
		String lTitreFicheType = "";
		if (ficheType == "F"){
			lTitreFicheType += " " + appContext.getString(R.string.txt_ficheType_F);
		} else if (ficheType == "FR") {
			lTitreFicheType += " " + appContext.getString(R.string.txt_ficheType_FR);
		}else if (ficheType == "FP") {
			lTitreFicheType += " " + appContext.getString(R.string.txt_ficheType_FP);
		}
		
		// Utilisation du parser Jericho
		Source source=new Source(htmlFiche);
		
		//Necessaire pour trouver ensuite les pères
		source.fullSequentialParse();
		
		//Centrage sur la TABLE qui contient tout le texte et les images
		Element ElementTable;
		List<? extends Element> listeElementsTable_TABLE;
		int num_table = 0;
		
		// Lecture des informations pour une fiche complète
		if ( ficheType == "F") {
			
			//Recup de l'ensemble des lignes (TR) de la TABLE
			// La 1ère contient l'entête
			// La 2ème contient la description
			// La 4ème contient la classification et la suite
			
			ElementTable=source.getFirstElementByClass("trait_cadregris").getFirstElement();
			if (LOG) Log.d(TAG, "getFiche() - ElementTable : " + ElementTable.toString().substring(0, Math.min(ElementTable.toString().length(),200)));
			
			listeElementsTable_TABLE = ElementTable.getFirstElement(HTMLElementName.TABLE).getChildElements();
			if (LOG) Log.v(TAG, "getFiche() - listeElementsTable_TABLE.size : " + listeElementsTable_TABLE.size());

			for (Element elementTable_TABLE : listeElementsTable_TABLE) {
				num_table++;
				if (LOG) Log.v(TAG, "getFiche() - ligneTable_TR :" + num_table);
				if (LOG) Log.v(TAG, "getFiche() - elementTable_TR : " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),100)));
				switch(num_table) {
				//Entête de la Fiche
				case 1 :
					//Recup du TD qui contient les infos Haut Gauche
					List<? extends Element> listeElementsHG_TD = elementTable_TABLE.getAllElementsByClass("code_fiche");
					
					for (Element element : listeElementsHG_TD) {
						List<? extends Element> listeElementsHG_TR = element.getAllElements(HTMLElementName.TR);
						i = 0;
						for (Element elementTR : listeElementsHG_TR) {
							i++;
							if (i == 2) {
								if (LOG) Log.v(TAG, "getFiche() - ficheNomLatin : " + elementTR.getRenderer().toString());
								ficheNomLatin = Outils.nettoyageCaracteres(elementTR.getRenderer().toString().trim());
							}
							if (i == 3) {
								if (LOG) Log.v(TAG, "getFiche() - ficheRegion : " + elementTR.getRenderer().toString());
								ficheRegion = elementTR.getRenderer().toString().trim();
							}
							if (i == 5) {
								if (LOG) Log.v(TAG, "getFiche() - ficheNomFrancais : " + elementTR.getRenderer().toString());
								ficheNomFrancais = Outils.nettoyageCaracteres(elementTR.getRenderer().toString().trim());
							}
						}
					}
					
					ficheListeDetails.add(new Detail(ficheNomFrancais + lTitreFicheType,ficheNomLatin + "\n" + ficheRegion,true));
					break;
				//Description
				case 2 :
					
					//Le grand pere du 1er TD de class Normal est le TBODY des Détails
					Element ElementsMG_normal=elementTable_TABLE.getFirstElementByClass("normal");
					if (LOG) Log.v(TAG, "getFiche() - ElementsMG_rubrique : " + ElementsMG_normal.toString().substring(0, Math.min(ElementsMG_normal.toString().length(),20)));
					Element ElementsMG=ElementsMG_normal.getParentElement().getParentElement();
					List<? extends Element> listeElementsMG_TD = ElementsMG.getAllElements(HTMLElementName.TD);
					String autresDenominations = "";
					boolean autresDenominationsFlag = true;
					String rubrique = "";
					String contenu = "";
	
					for (Element elementTD : listeElementsMG_TD) {
						if (LOG) Log.v(TAG, "getFiche() - listeElementsMG_TD : " + elementTD.toString().substring(0, Math.min(elementTD.toString().length(),50)));
						List<? extends Attribute> listeAttributs=elementTD.getAttributes();
						for (Attribute attribut : listeAttributs) {
							
							if (attribut.getName().equals("class") && attribut.getValue().equals("rubrique")) {
								//Si c'est la 1ère fois que l'on passe alors on enregistre les autres dénomination et 
								// l'international dans un nouveau détail
								if (rubrique.equals("") && !autresDenominations.equals("") && autresDenominationsFlag) {
									ficheListeDetails.add(new Detail(appContext.getString(R.string.txt_ficheDetailAutresDenominations),autresDenominations,false));
									autresDenominationsFlag = false;
								}
								rubrique = elementTD.getRenderer().toString().trim();
							}
							
							if (attribut.getName().equals("class") && attribut.getValue().equals("normal") ) {
								if (LOG) Log.v(TAG, "getFiche() - rubrique : " + rubrique);
								if (rubrique.equals("")) {
									autresDenominations  = elementTD.getRenderer().toString().trim();
									
									// suppression des sauts de ligne
									autresDenominations = autresDenominations.replaceAll("\r\n", " ").replaceAll("\n", " ");
									if (LOG) Log.v(TAG, "getFiche() - autresDenominations(1) : " + autresDenominations);
	
									// suppression des blancs multiples
									autresDenominations = autresDenominations.replaceAll("\\s{2,}"," ");
									if (LOG) Log.v(TAG, "getFiche() - autresDenominations(2) : " + autresDenominations);
									
									// permet d'enlever les Liens et de les remplacer par (*)
									autresDenominations = autresDenominations.replaceAll("<[^>]*>", "(*)").trim();
										
								} else {
									contenu = elementTD.getRenderer().toString();
									if (LOG) Log.v(TAG, "getFiche() - contenu(initial) : " + contenu);
									
									// suppression des sauts de ligne
									contenu = contenu.replaceAll("\r\n", " ").replaceAll("\n", " ");
									if (LOG) Log.v(TAG, "getFiche() - contenu(1) : " + contenu);
	
									// suppression des blancs multiples
									contenu = contenu.replaceAll("\\s{2,}"," ");
									if (LOG) Log.v(TAG, "getFiche() - contenu(2) : " + contenu);
									
									// permet d'enlever les Liens et de les remplacer par (*)
									contenu = contenu.replaceAll("<[^>]*>", "(*)").trim();
	
									if (LOG) Log.v(TAG, "getFiche() - contenu(après nettoyage) : " + contenu);
									ficheListeDetails.add(new Detail(rubrique,contenu,false));
								}
							}
						}
						
						// Création de la liste des Liens (url vers d'autres fiches)
						
						for (Element elementTDA : elementTD.getAllElements(HTMLElementName.A)) {
							
							if (LOG) Log.v(TAG, "getFiche() - A : " + elementTDA.getRenderer().toString().trim() + " - lien : " + elementTDA.getAttributeValue("href"));
							
							if (elementTDA.getAttributeValue("href").startsWith("../") || elementTDA.getAttributeValue("href").startsWith("http://doris.ffessm.fr") ) {
							
								if (elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", "") != "" && elementTDA.getRenderer().toString().trim() != "") {
								
									String tempLien = elementTDA.getRenderer().toString().trim();
									if (LOG) Log.v(TAG, "getFiche() - listeLienRencontre : " + listeLienRencontre );
									
									if (!listeLienRencontre.contains(tempLien + "£")) {
										listeLienRencontre += tempLien + "£";
										ficheListeLiensUrl.add(elementTDA.getAttributeValue("href").replaceAll(".*fiche_numero=", ""));
										ficheListeLiensTexte.add(tempLien);
									}
								}
							}
						}
					}
					
					//Recup du TD qui contient les infos DROITE (images et qui a fait la fiche)
					//Recup du TR dont le 3ème TD fils contient les infos DROITE (images et qui a fait la fiche)
					
					List<? extends Element> listeElements1 = null;
					listeElements1 = elementTable_TABLE.getAllElementsByClass("trait_cadregris");
					if (LOG) Log.v(TAG, "getFiche() -  element : " + " - " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),30)));
					
					for (Element element : listeElements1) {
						if (LOG) Log.v(TAG, "getFiche() - vignette 1: " + element.toString().substring(0, Math.min(100,element.toString().toString().length())));
						
						Element element2 = element.getFirstElement(HTMLElementName.A);
						
						if (element2 != null)
						{
							element2 = element2.getFirstElement(HTMLElementName.IMG);
							if (LOG) Log.v(TAG, "getFiche() - vignette 2: " + element2.toString().substring(0, Math.min(100,element2.toString().toString().length())));
							
							List<? extends Attribute> listeAttributs=element2.getAttributes();
							for (Attribute attribut : listeAttributs) {
								if (attribut.getName().equalsIgnoreCase("src") && attribut.getValue().contains("gestionenligne")){
									
									String urlImageDansFiche = attribut.getValue();
									if (LOG) Log.v(TAG, "getFiche() - urlImageDansFiche : " + urlImageDansFiche);
									
									//Les remplacements si dessous permettent de "calculer" simplement la référence de la grande image
									ficheListeImgUrl.add(urlImageDansFiche.replace("/photos_fiche_moy/","/photos/").replace("/photos_fiche_vig/","/photos/"));
									//ou de la vignette
									ficheListeImgVigUrl.add(urlImageDansFiche.replace("/photos_fiche_moy/","/photos_fiche_vig/"));
									
									//En Téléchargeant ici les images des vignettes, l'interface parait plus fluide ensuite
									if (LOG) Log.v(TAG, "getFiche() - ref : " + ref);
									Outils.getImage(appContext, urlImageDansFiche, ref, "");
									
									break;
								}
								
							}
						}
						
						//Recup Texte
						element2 = element.getFirstElementByClass("normal2");
						if (element2 != null)
						{
							if (LOG) Log.v(TAG, "getFiche() - Texte : " + element2.getRenderer().toString());
							ficheListeImgTexte.add(element2.getRenderer().toString());
						}
					}					
					break;
					
				//Ligne blanche => rien à faire mais à garder pour ne pas passer dans le default
				case 3 :
					break;
					
				//la classification et la suite
				case 4 :
					// To Do
					break;
				default :
					break;
				}
			}
		}
		
		
		//Lecture des informations pour une fiche proposée et pour le début d'une fiche en cours de rédaction
		if ( ficheType == "FP" || ficheType == "FR") {
			ElementTable=source.getFirstElementByClass("trait_cadregris");
			if (LOG) Log.d(TAG, "getFiche() - ElementTable : " + ElementTable.toString().substring(0, Math.min(ElementTable.toString().length(),200)));

			listeElementsTable_TABLE = ElementTable.getAllElements(HTMLElementName.TABLE);
			if (LOG) Log.v(TAG, "getFiche() - listeElementsTable_TABLE.size : " + listeElementsTable_TABLE.size());

			for (Element elementTable_TABLE : listeElementsTable_TABLE) {
				num_table++;
				if (LOG) Log.v(TAG, "getFiche() - num_table :" + num_table);
				if (LOG) Log.v(TAG, "getFiche() - elementTable_TABLE.length() : " + elementTable_TABLE.toString().length());
				if (LOG) Log.v(TAG, "getFiche() - elementTable_TABLE : " + elementTable_TABLE.toString().substring(0, Math.min(elementTable_TABLE.toString().length(),100)));

				String largeurTable = elementTable_TABLE.getAttributeValue("width");
				if (LOG) Log.v(TAG, "getFiche() - largeurTable :" + largeurTable);
				
				//Entête de la Fiche
				if (num_table== 2) {
			
					Element ElementInfosGauche = elementTable_TABLE.getFirstElementByClass("code_fiche").getFirstElement();
					
					try	{
						Element ElementNomLatin = ElementInfosGauche.getFirstElementByClass("texte_bandeau").getFirstElement();
						if (LOG) Log.v(TAG, "getFiche() - ElementNomLatin : " + ElementNomLatin.getRenderer().toString().trim());
						ficheNomLatin = ElementNomLatin.getRenderer().toString().trim();
					} catch (Exception e) {
		        		if (LOG) Log.e(TAG, "getFiche() - le nom latin n'est pas toujours renseigné");
		        	}
					
					try	{
						Element ElementDistribution = ElementInfosGauche.getFirstElementByClass("normal").getFirstElement();
						if (LOG) Log.v(TAG, "getFiche() - ElementDistribution : " + ElementDistribution.getRenderer().toString().trim());
						ficheRegion = ElementDistribution.getRenderer().toString().trim();
					} catch (Exception e) {
		        		if (LOG) Log.e(TAG, "getFiche() - la Distribution n'est pas toujours renseignée");
		        	}
					
					try	{
						Element ElementNomCommun = ElementInfosGauche.getFirstElementByClass("titre2").getFirstElement();
						if (LOG) Log.v(TAG, "getFiche() - ElementNomCommun : " + ElementNomCommun.getRenderer().toString().trim());
						ficheNomFrancais = ElementNomCommun.getRenderer().toString().trim();
					} catch (Exception e) {
		        		if (LOG) Log.e(TAG, "getFiche() - le nom français n'est pas toujours renseigné");
		        	}
					
					ficheListeDetails.add(new Detail(ficheNomFrancais + lTitreFicheType,ficheNomLatin + "\n" + ficheRegion,true));
			
				}
					
				if (largeurTable!=null && largeurTable.equals("372")) {
					if (LOG) Log.v(TAG, "getFiche() - Recup. Images");
					
					//Recup du TR dont le 3ème TD fils contient les infos DROITE (images et qui a fait la fiche)
					List<? extends Element> ListeelementTable_IMG = elementTable_TABLE.getAllElements(HTMLElementName.IMG);
					
					for (Element elementTableImg : ListeelementTable_IMG) {
						List<? extends Attribute> listeAttributsTableImg=elementTableImg.getAttributes();
						for (Attribute attribut : listeAttributsTableImg) {
							if (attribut.getName().equalsIgnoreCase("src") && attribut.getValue().contains("gestionenligne")){
								
								String urlImageDansFiche = attribut.getValue();
								if (LOG) Log.v(TAG, "getFiche() - urlImageDansFiche : " + urlImageDansFiche);
								
								//Les remplacements si dessous permettent de "calculer" simplement la référence de la grande image
								ficheListeImgUrl.add(urlImageDansFiche.replace("/photos_fiche_moy/","/photos/").replace("/photos_fiche_vig/","/photos/"));
								//ou de la vignette
								ficheListeImgVigUrl.add(urlImageDansFiche.replace("/photos_fiche_moy/","/photos_fiche_vig/"));
								
								//En Téléchargeant ici les images des vignettes, l'interface parait plus fluide ensuite
								if (LOG) Log.v(TAG, "getFiche() - ref : " + ref);
								Outils.getImage(appContext, urlImageDansFiche,ref, "");
								
								break;
							}
							
						}
					}
						
					//Recup Texte
					Element element2 = elementTable_TABLE.getFirstElementByClass("normal2");
					if (element2 != null)
					{
						if (LOG) Log.v(TAG, "getFiche() - Texte : " + element2.getRenderer().toString());
						ficheListeImgTexte.add(element2.getRenderer().toString());
					}
					break;
				
				}
			}
		}
		
		listeElementsTable_TABLE = null;
		
    	if (LOG) Log.d(TAG, "getFiche() - Fin");
	}
	
	/* Gestion de la couleur du texte et du fond de l'étiquette placée sur la photo */
	public void setTypeFicheCouleurs(String inTypeFiche){
		if (LOG) Log.d(TAG, "setTypeFicheCouleurs() - Début");
		if (LOG) Log.d(TAG, "setTypeFicheCouleurs() - inTypeFiche : "+inTypeFiche);
					
			if ( inTypeFiche == "F" ) {
				//Fiche Complète
				ficheTypeCouleurTexte = "#F6F6F6";
				ficheTypeCouleurFond = "#ababab";
				
			} else if ( inTypeFiche == "FP" ) {
				//Fiche Proposée
				ficheTypeCouleurTexte="#999999";
				ficheTypeCouleurFond = "#FAEDC0";
				
			} else if ( inTypeFiche == "FR" ) {
				//Fiche en cours de rédaction
				ficheTypeCouleurTexte="#999999";
				ficheTypeCouleurFond = "#D2E8FF";
				
			} else {
				//Cas du type non prévu
				ficheTypeCouleurTexte="#FF0000";
				ficheTypeCouleurFond="#000000";
			}
		
		if (LOG) Log.d(TAG, "setTypeFicheCouleurs() - Fin");
	}
	
	public class Detail {

		public String titre;
		public String contenu;
		public boolean affiche;
		
		public Detail(String inTitre, String inContenu, boolean inAffiche) {
			if (LOG) Log.d(TAG, "Detail() - Début");
			if (LOG) Log.d(TAG, "Detail() - Titre : " + inTitre);
			if (LOG) Log.d(TAG, "Detail() - Contenu : " + inContenu);
			if (LOG) Log.d(TAG, "Detail() - Affiche : " + inAffiche);
			titre = inTitre;
			contenu = inContenu;
			affiche = inAffiche;
			if (LOG) Log.d(TAG, "Detail() - Fin");
		}
	}

}