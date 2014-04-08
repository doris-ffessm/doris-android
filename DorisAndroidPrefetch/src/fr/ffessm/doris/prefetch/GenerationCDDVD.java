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
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.prefetch.PrefetchDorisWebSite.ActionKind;

public class GenerationCDDVD {

	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(GenerationCDDVD.class);
	
	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;
	
	private PrefetchTools prefetchTools = new PrefetchTools();
	
	private ActionKind action;
	private boolean zipCDDVD;
	
	public List<Groupe> listeGroupes;
	
	String fichierCDLien = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CD;
	File dossierCD = new File(fichierCDLien + "/" +PrefetchConstants.DOSSIER_HTML);
	
	// Pour debbug creationCD(), transfoHtml()
	public GenerationCDDVD() {
	}
	
	
	public GenerationCDDVD(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, boolean zipCDDVD) {
		this.dbContext = dbContext;
		this.connectionSource = connectionSource;
		this.action = action;
		this.zipCDDVD = zipCDDVD;
	}

	
	public void generation() {

		String fichierLocalLien = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/";
		String fichierRefLien = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/";
		String fichierIconeRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/" + PrefetchConstants.SOUSDOSSIER_ICONES + "/";
		String fichierIconeRefRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/" + PrefetchConstants.SOUSDOSSIER_ICONES + "/";

		// Des fichiers html et icônes doivent être téléchargés spécifiquement pour le CD 
		List<Lien> liensATelecharger = getLienATelecharger();
		for (Lien lienATelecharger : liensATelecharger) {
			
			if (lienATelecharger.getLienKind() == LienKind.PAGE) {
				if( ! prefetchTools.isFileExistingPath( fichierRefLien+lienATelecharger.getFichier() ) ){
					if (prefetchTools.getFichierFromUrl(Constants.getSiteUrl()+lienATelecharger.getUrl(),
							fichierLocalLien+lienATelecharger.getFichier() ) ) {
					} else {
						log.error("Une erreur est survenue lors de la récupération du lien : "+lienATelecharger.getUrl() );
						System.exit(1);
					}
				}
			}
			
			if (lienATelecharger.getLienKind() == LienKind.ICONE) {
				if( ! prefetchTools.isFileExistingPath( fichierIconeRefRacine+lienATelecharger.getFichier() ) ){
					if (prefetchTools.getFichierFromUrl(Constants.getSiteUrl()+lienATelecharger.getUrl(),
							fichierIconeRacine+lienATelecharger.getFichier() ) ) {
					} else {
						log.error("Une erreur est survenue lors de la récupération du lien : "+lienATelecharger.getUrl() );
						System.exit(1);
					}
				}
			}
		}
	
		// Création du dossier CD et DVD
		log.debug("doMain() - Création du dossier CD");
		creationCD();

		log.debug("doMain() - transfoHtml");
		transfoHtml( PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_CD + "/" );

		//TODO : Création DVD

		
		// Zip CD si demandé puis effacement du dossier CD
		if (zipCDDVD) {
			log.debug("doMain() - zip CD");
			try { 
			    ZipOutputStream zipOS = new ZipOutputStream(
		    		new FileOutputStream(fichierCDLien + ".zip")
	    		); 

			    prefetchTools.zipDossier(fichierCDLien, zipOS); 

			    zipOS.close(); 
			} catch(Exception e) { 
				log.info("Erreur lors du ZIP du CD");
				e.printStackTrace();
				System.exit(1);
			}
			
			try {
				FileUtils.deleteDirectory(dossierCD);
				log.info("Suppression de : " + dossierCD.getAbsolutePath());
			} catch (IOException e) {
				log.info("Problème suppression de : " + dossierCD.getAbsolutePath());
				e.printStackTrace();
			}
			log.debug("doMain() - Fin zip CD");
		}
	
	}
	
	

	
	// Liste des Pages et Images à télécharger dans le cas des CD et DVD
	// REPLACE
	private List<Lien> getLienATelecharger(){
		List<Lien> lienATelecharger = new ArrayList<Lien>(0);

		lienATelecharger.add(new Lien(LienKind.PAGE, "accueil.asp","accueil.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "styles.css","styles.css"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris.asp","doris.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_faq.asp","doris_faq.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "contacts_accueil.asp","contacts_accueil.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "Copyright.asp","Copyright.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "liens.asp","liens.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "formulaire_contact.asp","formulaire_contact.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_doridiens.asp","doris_doridiens.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_genese_objectifs.asp","doris_genese_objectifs.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_chroniques_doridiennes.asp","doris_chroniques_doridiennes.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "doris_equipe.asp","doris_equipe.html"));

		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=10","fichier-10.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=1","fichier-1.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=2","fichier-2.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=3","fichier-3.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=4","fichier-4.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "fichier.asp?numero_fichier=5","fichier-5.html"));

		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=10","groupes_zone-10.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=1","groupes_zone-1.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=2","groupes_zone-2.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=3","groupes_zone-3.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=4","groupes_zone-4.html"));
		lienATelecharger.add(new Lien(LienKind.PAGE, "groupes.asp?numero_fichier=5","groupes_zone-5.html"));
		
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/favicon.ico","images_favicon.ico"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/carre.jpg","images_carre.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/pucecarre.gif","images_pucecarre.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "Images/pucecarre.gif","images_pucecarre.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/pucecarreorange.gif","images_pucecarreorange.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "Images/pucecarreorange.gif","images_pucecarreorange.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fleche_grise.gif","images_fleche_grise.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo.gif","images_logo.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo2.gif","images_logo2.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo-biologie.gif","images_logo-biologie.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo_ffessm.gif","images_logo_ffessm.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/logo-biologie-pantone.gif","images_logo-biologie-pantone.gif"));

		lienATelecharger.add(new Lien(LienKind.ICONE, "images/ligne_carre3.gif","images_ligne_carre3.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/ligne_carre4.gif","images_ligne_carre4.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/MNHN2.gif","images_MNHN2.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/Palme3.gif","images_Palme3.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/1x1.gif","images_1x1.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/10x10.gif","images_10x10.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/pucemenu.gif","images_pucemenu.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fond_bas.gif","images_fond_bas.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fonbandeau.gif","images_fonbandeau.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/icon_back.gif","images_icon_back.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/petit_gris.gif","images_petit_gris.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/f3f3f3.gif","images_f3f3f3.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/black_round_grey.gif","images_black_round_grey.gif"));		
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/newsearch.gif","images_newsearch.gif"));	
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_imp.gif","images_18_imp.gif"));	
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_fileprint.gif","images_18_fileprint.gif"));	
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_filewrite.gif","images_18_filewrite.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_fileimage.gif","images_18_fileimage.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_filetick.gif","images_18_filetick.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_mailsend.gif","images_18_mailsend.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_probe.gif","images_18_probe.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/searchdoc.gif","images_searchdoc.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/dorispetit18x18.gif","images_dorispetit18x18.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/18_faq.gif","images_18_faq.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/rightsign.jpg","images_rightsign.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/btn_next.gif","images_btn_next.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/btn_prev.gif","images_btn_prev.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/picto_regl18.gif","images_picto_regl18.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/picto_regl.gif","images_picto_regl.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/picto_dang18.gif","images_picto_dang18.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/picto_dang.gif","images_picto_dang.gif"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fiche.gif","fiche.gif"));
		
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier1puce.jpg","images_fichier1puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier2puce.jpg","images_fichier2puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier3puce.jpg","images_fichier3puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier4puce.jpg","images_fichier4puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier5puce.jpg","images_fichier5puce.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier10puce.jpg","images_fichier10puce.jpg"));

		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier1.jpg","images_fichier1.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier2.jpg","images_fichier2.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier3.jpg","images_fichier3.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier4.jpg","images_fichier4.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier5.jpg","images_fichier5.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier10.jpg","images_fichier10.jpg"));
		
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier1_gde.jpg","images_fichier1_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier2_gde.jpg","images_fichier2_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier3_gde.jpg","images_fichier3_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier4_gde.jpg","images_fichier4_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier5_gde.jpg","images_fichier5_gde.jpg"));
		lienATelecharger.add(new Lien(LienKind.ICONE, "images/fichier10_gde.jpg","images_fichier10_gde.jpg"));
		return lienATelecharger;
	}
	
	// REPLACE
	private List<Lien> getLienANettoyer(){
		List<Lien> lienANettoyer = new ArrayList<Lien>(0);
		
		// dans les href si commence par url, on replace par le nom du fichier
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=1&","listeFiches-1.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=2&","listeFiches-2.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=3&","listeFiches-3.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=4&","listeFiches-4.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=5&","listeFiches-5.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?numero_fichier=10&","listeFiches.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "nom_scientifique.asp?fichier=",""));

		lienANettoyer.add(new Lien(LienKind.PAGE, "glossaire.asp\"","listeDefinitions-a-1.html\""));
		
		lienANettoyer.add(new Lien(LienKind.PAGE, "biblio.asp\"","listeBibliographies-1.html\""));

		//TODO : Qd des espaces il faudrait les remplacer par des _
		lienANettoyer.add(new Lien(LienKind.VIGNETTE, "http://doris.ffessm.fr/gestionenligne/photos_fiche_vig/","/"));
		lienANettoyer.add(new Lien(LienKind.MED_RES, "http://doris.ffessm.fr/gestionenligne/photos_fiche_moy/","/"));
		lienANettoyer.add(new Lien(LienKind.HI_RES, "http://doris.ffessm.fr/gestionenligne/photos/","/"));
		lienANettoyer.add(new Lien(LienKind.VIGNETTE, "http://doris.ffessm.fr/gestionenligne/photos_biblio_moy/","/biblio-"));
		lienANettoyer.add(new Lien(LienKind.VIGNETTE, "http://doris.ffessm.fr/gestionenligne/photos_vig/","/"));
		
		lienANettoyer.add(new Lien(LienKind.ICONE, "gestionenligne/images_groupe/","/images_groupe_"));
		lienANettoyer.add(new Lien(LienKind.ICONE, "gestionenligne/images_sousgroupe/","/images_sousgroupe_"));
				
		lienANettoyer.add(new Lien(LienKind.PAGE, "fichier.asp","accueil.html"));
				
		lienANettoyer.add(new Lien(LienKind.PAGE, "forum_liste.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "fiche_imprime.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "fiches_liste_recherche.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "fiches_liste_proposees.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "fiches_liste_reservees.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "cle_identification0.asp","indisponible_CDDVD.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, "forum_detail.asp","indisponible_CDDVD.html"));
		
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"contacts.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"fiches_liste_recherche.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"formulaire_contact_valid.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"biblio.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "action=\"glossaire.asp\"","action=\"indisponible_CDDVD.html\""));
		lienANettoyer.add(new Lien(LienKind.TEXTE, "form.submit();",""));
		
		lienANettoyer.add(new Lien(LienKind.PAGE, "http://doris.ffessm.fr","accueil.html"));
		lienANettoyer.add(new Lien(LienKind.PAGE, " http://doris.ffessm.fr","accueil.html"));
		return lienANettoyer;
	}

	//REPLACE_ALL
	private List<Lien> getRegExpPourNettoyer(){
		List<Lien> regExpPourNettoyer = new ArrayList<Lien>(0);
		
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiche.asp\\?[^\">]*&fiche_numero=([^&]*)&[^\">]*\"","href=\"fiche-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiche2.asp\\?fiche_numero=([^&]*)&[^\">]*\"","href=\"fiche-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiche2.asp\\?fiche_numero=([^\">]*)\"","href=\"fiche-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"\\.\\./fiche2.asp\\?fiche_numero=([^\">]*)\"","href=\"fiche-$1.html\""));
		
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"contacts.asp\\?filtre=(.)","href=\"listeParticipants-$1.html"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"[^\"\\?]*formulaire_contact2.asp\\?contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"contact_fiche.asp\\?temp=0&amp;contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"contact_fiche.asp\\?temp=0&contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"contact_fiche.asp\\?contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiches_liste_photographe.asp\\?contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"fiches_liste_contact.asp\\?contact_numero=([^\">]*)\"","href=\"indisponible_CDDVD.html\""));

		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire.asp\\?filtre=(.)[^\">]*","href=\"listeDefinitions-$1-1.html"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire.asp\\?mapage=([^&\">]*)&[^\">]*filtre=(.)\"","href=\"listeDefinitions-$2-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire_detail.asp\\?glossaire_numero=([^&\">]*)&[^\">]*","href=\"definition-$1.html"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire.asp\\?page=Suivant[^\"]*","href=\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"glossaire.asp\\?page=Precedent[^\"]*","href=\""));
		
		regExpPourNettoyer.add(new Lien(LienKind.ICONE, "http://doris.ffessm.fr/gestionenligne/photos_forum_vig/[^\">]*\"","/doris_icone_doris_large.png\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "(/images_groupe_[^.]*).gif","$1.png"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "(/images_sousgroupe_[^.]*).gif","$1.png"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "(/images_sousgroupe_[^.]*).jpg","$1.png"));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "(/images_sousgroupe_[^.]*).JPG","$1.png"));
		
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "fiche_photo_liste_apercu.asp\\?fiche_numero=([^&>]*)&[^\">]*\"","fiche-$1_listePhotos.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "photo_gde_taille_fiche2.asp\\?varpositionf=[^\">]*fiche_numero = ([^&]*)&[^\">]*\"","fiche-$1_listePhotos.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "photo_gde_taille.asp\\?varposition=[^\">]*\"","indisponible_CDDVD.html\""));
		
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"biblio_fiche.asp\\?biblio_numero=([^\">]*)\"","href=\"biblio-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"biblio.asp\\?mapage=([^&>]*)&[^\">]*\"","href=\"listeBibliographies-$1.html\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"biblio.asp\\?page=Suivant[^\"]*","href=\""));
		regExpPourNettoyer.add(new Lien(LienKind.PAGE, "href=\"biblio.asp\\?page=Precedent[^\"]*","href=\""));

		regExpPourNettoyer.add(new Lien(LienKind.ICONE, "gestionenligne/diaporamaglo/([^.]*).jpg","biblio-$1.jpg"));
				
		// Si un espace dans le nom des photos alors on le remplace par un _
		//regExpPourNettoyer.add(new Lien(LienKind.PAGE, "<img src="../vignettes_fiches/ electra_posidoniae-dh02.jpg"","href=\""));

		//<img src="../vignettes_fiches/ electra_posidoniae-dh02.jpg"
		
		return regExpPourNettoyer;
	}
	
	
	private enum LienKind {
    	PAGE,
    	TEXTE,
    	ICONE,
    	VIGNETTE,
    	MED_RES,
    	HI_RES
    }
	
	
	private class Lien {
		private LienKind lienKind;
		private String url;
		private String fichierSurDisque;
		
		Lien(LienKind lienKind, String url, String fichierSurDisque){
			this.lienKind = lienKind;
			this.url = url;
			this.fichierSurDisque = fichierSurDisque;
		}
		public LienKind getLienKind() {
			return lienKind;
		}
		public String getUrl() {
			return url;
		}
		public String getFichier() {
			return fichierSurDisque;
		}
	}
	
	
	public void creationCD(){
		log.debug("creationCD() - Début");

		// Création Dossier HTML du CD
		log.info("Création Dossier HTML du CD");
		
		String fichierRefLien = PrefetchConstants.DOSSIER_RACINE + "/";
		
		File dossierRef = new File(fichierRefLien+PrefetchConstants.DOSSIER_HTML_REF);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		dossierRef = new File(fichierRefLien+PrefetchConstants.DOSSIER_HTML);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Création Dossier Icones du CD
		log.info("Création Dossier Icones du CD");
		dossierCD = new File(fichierCDLien + "/" +PrefetchConstants.SOUSDOSSIER_ICONES);
		dossierRef = new File(fichierRefLien+PrefetchConstants.DOSSIER_IMAGES_REF+"/"+PrefetchConstants.SOUSDOSSIER_ICONES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dossierRef = new File(fichierRefLien+PrefetchConstants.DOSSIER_IMAGES+"/"+PrefetchConstants.SOUSDOSSIER_ICONES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Images du Dossier ./res/images, le Doris en cas d'images non présentes et
		// celles des groupes : elles ne sont pas téléchargées pour l'appli car plus
		// jolies retouchées, on remet ici celles de l'appli.
		dossierRef = new File(PrefetchConstants.DOSSIER_RES_IMAGES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Création Dossier Vignettes du CD
		log.info("Création Dossier Vignettes du CD");
		dossierCD = new File(fichierCDLien + "/" +PrefetchConstants.SOUSDOSSIER_VIGNETTES);
		dossierRef = new File(fichierRefLien+PrefetchConstants.DOSSIER_IMAGES_REF+"/"+PrefetchConstants.SOUSDOSSIER_VIGNETTES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dossierRef = new File(fichierRefLien+PrefetchConstants.DOSSIER_IMAGES+"/"+PrefetchConstants.SOUSDOSSIER_VIGNETTES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Création Dossier Images du CD
		log.info("Création Dossier Images du CD");
		dossierCD = new File(fichierCDLien + "/" +PrefetchConstants.SOUSDOSSIER_MED_RES);
		dossierRef = new File(fichierRefLien+PrefetchConstants.DOSSIER_IMAGES_REF+"/"+PrefetchConstants.SOUSDOSSIER_MED_RES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		dossierRef = new File(fichierRefLien+PrefetchConstants.DOSSIER_IMAGES+"/"+PrefetchConstants.SOUSDOSSIER_MED_RES);
		try {
			FileUtils.copyDirectory(dossierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Copie du Fichier permettant d'aller directement sur la page d'accueil depuis la racine du CD
		log.info("Copie du Fichier : Doris_CD.html");
		dossierCD = new File(fichierCDLien);
		File fichierRef = new File(PrefetchConstants.DOSSIER_RES_HTML +"/" +"Doris_CD.html");
		try {
			FileUtils.copyFileToDirectory(fichierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Copie de la page d'erreur
		log.info("Copie du Fichier : indisponible_CDDVD.html");
		dossierCD = new File(fichierCDLien + "/" + PrefetchConstants.DOSSIER_HTML);
		fichierRef = new File(PrefetchConstants.DOSSIER_RES_HTML+"/"+"indisponible_CDDVD.html");
		try {
			FileUtils.copyFileToDirectory(fichierRef, dossierCD);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.debug("creationCD() - Fin");
	}

	
	
	public void transfoHtml(String fichierCDLien){
		log.debug("transfoHtml() - Début");

		// Modification Fichiers HTML : lien, images
		File dossierCD = new File(fichierCDLien+PrefetchConstants.DOSSIER_HTML);
		for (File fichierHtml:dossierCD.listFiles()) {
			String contenuFichier = prefetchTools.getFichierTxtFromDisk(fichierHtml);
			
			contenuFichier = contenuFichier.replace("href=\""+Constants.getSiteUrl(),"href=\"");
			
			// Le site n'est pas toujours très cohérent
			contenuFichier = contenuFichier.replace("src=\"../Images","src=\"images");
			contenuFichier = contenuFichier.replace("src=\"gestionenligne/images/icones","src=\"images" );
			
			// Si la Page contient : "recherche par Images"
			
			
			// Pour chaque Liens à télécharger définis ci-après
			for (Lien lienTelecharge : getLienATelecharger()){
				switch (lienTelecharge.getLienKind()) {
				case PAGE :
					if ( ! lienTelecharge.getUrl().contains("=")) {
						contenuFichier = contenuFichier.replace("href=\""+lienTelecharge.getUrl()+"\"","href=\""+lienTelecharge.getFichier()+"\"");
					} else {
						contenuFichier = contenuFichier.replaceAll("href=\""+Pattern.quote(lienTelecharge.getUrl())+"[^\"]*\"","href=\""+lienTelecharge.getFichier()+"\"");
					}
					break;
				case ICONE :
					contenuFichier = contenuFichier.replace("src=\""+lienTelecharge.getUrl()+"\"","src=\"../"+PrefetchConstants.SOUSDOSSIER_ICONES+"/"+lienTelecharge.getFichier()+"\"");
					contenuFichier = contenuFichier.replace("background=\""+lienTelecharge.getUrl()+"\"","background=\"../"+PrefetchConstants.SOUSDOSSIER_ICONES+"/"+lienTelecharge.getFichier()+"\"");
					break;
				case TEXTE :
				case VIGNETTE :
				case MED_RES :
				case HI_RES :
				}
			}
			// Liens vers 
			for (Lien lienANettoyer : getLienANettoyer()){
				switch (lienANettoyer.getLienKind()) {
				case PAGE :
					contenuFichier = contenuFichier.replaceAll("href=\""+Pattern.quote(lienANettoyer.getUrl())+"[^\"]*\"","href=\""+lienANettoyer.getFichier()+"\"");
					break;
				case TEXTE :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),lienANettoyer.getFichier());
					break;
				case ICONE :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),"../"+PrefetchConstants.SOUSDOSSIER_ICONES+lienANettoyer.getFichier());
					break;
				case VIGNETTE :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),"../"+PrefetchConstants.SOUSDOSSIER_VIGNETTES+lienANettoyer.getFichier());
					break;
				case MED_RES :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),"../"+PrefetchConstants.SOUSDOSSIER_MED_RES+lienANettoyer.getFichier());
					break;
				case HI_RES :
					contenuFichier = contenuFichier.replace(lienANettoyer.getUrl(),"../"+PrefetchConstants.SOUSDOSSIER_MED_RES+lienANettoyer.getFichier());
					break;
				}
			}
			
			// RegExp
			for (Lien lienRegExp : getRegExpPourNettoyer()){
				switch (lienRegExp.getLienKind()) {
				case PAGE :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),lienRegExp.getFichier());
				break;
				case ICONE :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),"../"+PrefetchConstants.SOUSDOSSIER_ICONES+lienRegExp.getFichier());
				break;
				case VIGNETTE :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),"../"+PrefetchConstants.SOUSDOSSIER_VIGNETTES+lienRegExp.getFichier());
					break;
				case MED_RES :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),"../"+PrefetchConstants.SOUSDOSSIER_MED_RES+lienRegExp.getFichier());
					break;
				case HI_RES :
					contenuFichier = contenuFichier.replaceAll(lienRegExp.getUrl(),"../"+PrefetchConstants.SOUSDOSSIER_MED_RES+lienRegExp.getFichier());
					break;
				}
			}
			
			try {
				FileUtils.write(fichierHtml, contenuFichier, "iso-8859-1");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} // Fin pour chaque fichier du dossier html

		// Les liens entre les pages de Groupes ne pouvaient être réalisé purement avec des expressions régulières
		// On re-parcourt donc l'ensemble des fichiers contenant groupe dans leur nom.
		for (File fichierHtml : dossierCD.listFiles()) {
			
			// Les pages contenant l'arborescence des Groupes : groupes_zone-11.html
			if (fichierHtml.getName().contains("groupes_zone")){
				
				String numZone = fichierHtml.getName().replaceAll("[^-]*-([0-9]*).*", "$1").trim();
				
				log.debug("transfoHtml() - groupes_zone : "+fichierHtml.getName()+" - "+numZone);
				
				String contenuFichier = prefetchTools.getFichierTxtFromDisk(fichierHtml);
		
				// Lien vers 1ère page des espèces du Groupe
				// fiches_liste.asp?groupe_numero=51 pour la zone 10
				// groupe-10-51-0-1
				contenuFichier = contenuFichier.replaceAll(
						"href=\"fiches_liste.asp\\?groupe_numero=([0-9]*)[^\">]*\"",
						"href=\"groupe-"+numZone+"-$1-0-1.html\"");

				// Lien vers 1ère page des espèces du Sous Groupe
				//fiches_liste.asp?sousgroupe_numero=73&groupe_numero=2&fichier=&numero_fichier=1 pour la zone 10
				// groupe-10-2-73-1
				contenuFichier = contenuFichier.replaceAll(
						"href=\"fiches_liste.asp\\?sousgroupe_numero=([0-9]*)&groupe_numero=([0-9]*)[^\">]*\"",
						"href=\"groupe-"+numZone+"-$2-$1-1.html\"");
				
				
				
				try {
					FileUtils.write(fichierHtml, contenuFichier, "iso-8859-1");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			// Les pages contenant les espèces du Groupe : groupe-10-2-73-1.html
			if (fichierHtml.getName().contains("groupe-")){
				log.debug("transfoHtml() - fichier groupe : "+fichierHtml.getName());
				String[] info = fichierHtml.getName().replace(".html", "").split("-");
				String numZone = info[1].trim();
				String numGroupe = info[2].trim();
				String numSousGroupe = info[3].trim();
				String numPage = info[4].trim();
				
				log.debug("transfoHtml() - groupe- : "+fichierHtml.getName()+" - "+numZone+
						" - "+numGroupe+" - "+numSousGroupe+" - "+numPage);
				
				int numPageSuivante = Integer.valueOf(numPage)+1;
				int numPagePrecedente = Integer.valueOf(numPage)-1;
				
				String contenuFichier = prefetchTools.getFichierTxtFromDisk(fichierHtml);
				//log.debug("transfoHtml() - contenuFichier : "+contenuFichier.length());
				
				// Lien vers la page de tous les groupes de la zone
				//groupes.asp?temp=0
				contenuFichier = contenuFichier.replaceAll(
						"href=\"groupes.asp\\?temp=0[^\"]*\"",
						"href=\"groupes_zone-"+numZone+".html\"");
				//log.debug("transfoHtml() - 110");
				
				// Page Suivante
				// fiches_liste.asp?fichier=&groupe_numero=49&sousgroupe_numero=&rnomscient=&rtrie=&rnomcommunfr=&page=Suivant&PageCourante=2&term=&enco=&prop=&allcheck=
				contenuFichier = contenuFichier.replaceAll(
						"href=\"fiches_liste.asp\\?fichier=[^\"]*&page=Suivant&[^\"]*\"",
						"href=\"groupe-"+numZone+"-"+numGroupe+"-"+numSousGroupe+"-"+numPageSuivante+".html\"");
				//log.debug("transfoHtml() - 120");
				
				// Précédente
				// fiches_liste.asp?&fichier=&groupe_numero=49&sousgroupe_numero=&rnomscient=&rtrie=&rnomcommunfr=&page=Precedent&PageCourante=2&term=&enco=&prop=&allcheck=
				contenuFichier = contenuFichier.replaceAll(
						"href=\"fiches_liste.asp\\?&fichier=[^\"]*&page=Precedent&[^\"]*\"",
						"href=\"groupe-"+numZone+"-"+numGroupe+"-"+numSousGroupe+"-"+numPagePrecedente+".html\"");
				//log.debug("transfoHtml() - 130");
				
				try {
					//log.debug("transfoHtml() - 210");
					FileUtils.write(fichierHtml, contenuFichier, "iso-8859-1");
					//log.debug("transfoHtml() - 220");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		log.debug("transfoHtml() - Fin");
	}
	
	
}
