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
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.FicheLight;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.android.sitedoris.Outils;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.prefetch.PrefetchDorisWebSite.ActionKind;


public class PrefetchFiches {


	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchFiches.class);
	
	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;
	
	private ActionKind action;
	private int nbMaxFichesATraiter;
	
	private List<Groupe> listeGroupes;
	private List<Participant> listeParticipants;

	public PrefetchFiches(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, int nbMaxFichesATraiter,
			List<Groupe> listeGroupes, List<Participant> listeParticipants) {
		this.dbContext = dbContext;
		this.connectionSource = connectionSource;
		this.action = action;
		this.nbMaxFichesATraiter = nbMaxFichesATraiter;
		
		this.listeGroupes = listeGroupes;
		this.listeParticipants = listeParticipants;
	}
	
	
	
	public int prefetch() {
		// - - - Liste des Fiches - - -
		// Récupération de la liste des fiches sur le site de DORIS
		// Elles sont récupérées dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD

		
		String contenuFichierHtml = null;
		
		try {
			
			String listeFichesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/listeFiches.html";
			log.info("Récup. Liste Fiches Doris : " + listeFichesFichier);
			
			if ( action != ActionKind.NODWNLD ){
				String listeToutesFiches = Constants.getListeFichesUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES)); 
				if (Outils.getFichierFromUrl(listeToutesFiches, listeFichesFichier)) {
					contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeFichesFichier));
				} else {
					log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
					System.exit(1);
				}
			} else {
				// NODWNLD
				listeFichesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeFiches.html";
				if (new File(listeFichesFichier).exists()) {
					contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeFichesFichier));
				} else {
					log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
					System.exit(0);
				}
			}
			HashSet<FicheLight> listeFichesSite = SiteDoris.getListeFichesFromHtml(contenuFichierHtml);
			log.info("Nb Fiches sur le site : "+listeFichesSite.size());

			// Récupération de la liste des fiches dans le dossier de référence
			// Si NODWNLD la liste sera utilisée pour faire le traitement
			// Si UPDATED ou CDDVD, elle permettra de déduire les fiches à télécharger de nouveau : les fiches ayant changées de statut
			HashSet<FicheLight> listFichesFromRef = new HashSet<FicheLight>(0);
			if ( action != ActionKind.INIT ){
				listeFichesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeFiches.html";
				if (new File(listeFichesFichier).exists()) {
					contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(listeFichesFichier));
					listFichesFromRef = SiteDoris.getListeFichesFromHtml(contenuFichierHtml);
				} else {
					// Si en Mode NODWLD alors le fichier doit être dispo.
					if (action == ActionKind.NODWNLD) {
						log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
						System.exit(1);
					} else {
						// Sinon Liste Ref = Liste du Site puisque non dispo
						listFichesFromRef = listeFichesSite;
					}
				}
			}
			
			log.info("Nb Fiches dans le dossier de référence : "+listFichesFromRef.size());
			
			// Création de l'entête des fiches
			final HashSet<FicheLight> listeFichesTravail;
			if ( action != ActionKind.NODWNLD ) {
				listeFichesTravail = (HashSet<FicheLight>) listeFichesSite.clone();
			} else {
				listeFichesTravail = (HashSet<FicheLight>) listFichesFromRef.clone();
			}
			TransactionManager.callInTransaction(connectionSource,
				new Callable<Void>() {
					public Void call() throws Exception {
						for (FicheLight ficheLight : listeFichesTravail){
							dbContext.ficheDao.create(new Fiche(ficheLight));
						}
						return null;
				    }
				});

			
			// - - - Fiche - - -
			// Pour chaque fiche, on télécharge la page (si nécessaire) puis on la traite

			log.info("Mise à jours de "+listeFichesTravail.size()+" fiches.");
			HashSet<FicheLight> listFichesModif = null;
			if ( action == ActionKind.UPDATE || action == ActionKind.CDDVD ) {
				listFichesModif = SiteDoris.getListeFichesUpdated(listFichesFromRef, listeFichesTravail);
			}
			
			int nbFichesTraitees = 0;
			for (FicheLight ficheLight : listeFichesTravail) {
				if (  nbFichesTraitees <= nbMaxFichesATraiter ) {
					log.debug("doMain() - Traitement Fiche : "+ficheLight.getNomCommun());
					
					String urlFiche =  Constants.getFicheFromIdUrl( ficheLight.getNumeroFiche() );
					String fichierLocalFiche = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/fiche-"+ficheLight.getNumeroFiche()+".html";
					String fichierRefFiche = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/fiche-"+ficheLight.getNumeroFiche()+".html";
					
					if ( action == ActionKind.INIT ) {
						if (Outils.getFichierFromUrl(urlFiche, fichierLocalFiche)) {
							nbFichesTraitees += 1;
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalFiche));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
							// Solution de contournement désespérée 
							urlFiche = Constants.getFicheFromNomCommunUrl(ficheLight.getNomCommun());
							log.error("=> Tentative sur : "+urlFiche);
							if (Outils.getFichierFromUrl(urlFiche, fichierLocalFiche)) {
								nbFichesTraitees += 1;
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalFiche));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
								continue;
							}
						}
					} else if ( action == ActionKind.UPDATE || action == ActionKind.CDDVD ) {
						if (new File(fichierRefFiche).exists() && !listFichesModif.contains(ficheLight)) {
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierRefFiche));
							nbFichesTraitees += 1;
						} else {
							if (Outils.getFichierFromUrl(urlFiche, fichierLocalFiche)) {
								nbFichesTraitees += 1;
								contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalFiche));
							} else {
								log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
								// Solution de contournement désespérée 
								urlFiche = Constants.getFicheFromNomCommunUrl(ficheLight.getNomCommun());
								log.error("=> Tentative sur : "+urlFiche);
								if (Outils.getFichierFromUrl(urlFiche, fichierLocalFiche)) {
									nbFichesTraitees += 1;
									contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierLocalFiche));
								} else {
									log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
									continue;
								}
							}
						}
					} else if ( action == ActionKind.NODWNLD ) {
						if (new File(fichierRefFiche).exists()) {
							contenuFichierHtml = Outils.getFichierTxtFromDisk(new File(fichierRefFiche));
							nbFichesTraitees += 1;
						} else {
							log.error("La récupération de la fiche sur le disque : "+fichierRefFiche+" a échoué.");
						}
					}
					//Fiche fiche = new Fiche(ficheLight);
					Fiche fiche = dbContext.ficheDao.queryForFirst(
							dbContext.ficheDao.queryBuilder().where().eq("numeroFiche", ficheLight.getNumeroFiche()).prepare()
						);
					fiche.setContextDB(dbContext);
					fiche.getFicheFromHtml(contenuFichierHtml, listeGroupes, listeParticipants);
					dbContext.ficheDao.update(fiche);
					
					// mise à jour des champs inverse
					//dbContext.ficheDao.refresh(fiche);
					
					log.info("doMain() - Info Fiche {");
					log.info("doMain() -      - ref : "+fiche.getNumeroFiche());
					log.info("doMain() -      - nom : "+fiche.getNomCommun());
					log.info("doMain() -      - etat : "+fiche.getEtatFiche());
					log.info("doMain() - }");
					
					String urlListePhotos = "http://doris.ffessm.fr/fiche_photo_liste_apercu.asp?fiche_numero="+fiche.getNumeroFiche();
					String fichierLocalListePhotos = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/fiche-"+fiche.getNumeroFiche()+"_listePhotos.html";
					String fichierRefListePhotos = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/fiche-"+fiche.getNumeroFiche()+"_listePhotos.html";
					String contenuFichierHtmlListePhotos = null;
					if ( action == ActionKind.INIT ) {
						if (Outils.getFichierFromUrl(urlListePhotos, fichierLocalListePhotos)) {
							contenuFichierHtmlListePhotos = Outils.getFichierTxtFromDisk(new File(fichierLocalListePhotos));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
							continue;
						}
					} else if ( action == ActionKind.UPDATE || action == ActionKind.CDDVD ) {
						if (new File(fichierRefListePhotos).exists() && !listFichesModif.contains(fiche)) {
							contenuFichierHtmlListePhotos = Outils.getFichierTxtFromDisk(new File(fichierRefListePhotos));
						} else {
							if (Outils.getFichierFromUrl(urlListePhotos, fichierLocalListePhotos)) {
								contenuFichierHtmlListePhotos = Outils.getFichierTxtFromDisk(new File(fichierLocalListePhotos));
							
							} else {
								log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
								continue;
							}
						}
					} else if ( action == ActionKind.NODWNLD ){
						if (new File(fichierRefListePhotos).exists()) {
							contenuFichierHtmlListePhotos = Outils.getFichierTxtFromDisk(new File(fichierRefListePhotos));
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
						}
					}
					
					if(contenuFichierHtmlListePhotos != null) {
						
						final List<PhotoFiche> listePhotoFiche = SiteDoris.getListePhotosFicheFromHtml(fiche, contenuFichierHtmlListePhotos);
						
						// Maj Base de données
						final Fiche ficheMaj = fiche;
						TransactionManager.callInTransaction(connectionSource,
							new Callable<Void>() {
								public Void call() throws Exception {
									for (PhotoFiche photoFiche : listePhotoFiche){
										photoFiche.setFiche(ficheMaj);
										dbContext.photoFicheDao.create(photoFiche);
										
										if (photoFiche.estPhotoPrincipale) {
											ficheMaj.setPhotoPrincipale(photoFiche);
											dbContext.ficheDao.update(ficheMaj);
										}
									}
									return null;
							    }
							});
						
						// Téléchargement Photos
						if ( action == ActionKind.CDDVD ) {
							String fichierImageRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/";
							String fichierImageRefRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/";

							for (PhotoFiche photoFiche : listePhotoFiche){

								if ( !photoFiche.getCleURL().isEmpty() ) {
									// Vignettes
									if( ! PrefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+photoFiche.getCleURL().replace(" ", "_") ) ){
										if (Outils.getFichierFromUrl(Constants.VIGNETTE_BASE_URL+photoFiche.getCleURL().replace(" ", "%20"), fichierImageRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+photoFiche.getCleURL().replace(" ", "_"))) {
										} else {
											log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
											//System.exit(1);
										}
									}
									// Qualité Intermédiaire
									if( ! PrefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_MED_RES+photoFiche.getCleURL().replace(" ", "_") ) ){
										if (Outils.getFichierFromUrl(Constants.MOYENNE_BASE_URL+photoFiche.getCleURL().replace(" ", "%20"), fichierImageRacine+PrefetchConstants.SOUSDOSSIER_MED_RES+photoFiche.getCleURL().replace(" ", "_"))) {
										} else {
											log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
											//System.exit(1);
										}
									}
									// Haute Qualité 
									if( ! PrefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_HI_RES+photoFiche.getCleURL().replace(" ", "_") ) ){
										if (Outils.getFichierFromUrl(Constants.GRANDE_BASE_URL+photoFiche.getCleURL().replace(" ", "%20"), fichierImageRacine+PrefetchConstants.SOUSDOSSIER_HI_RES+photoFiche.getCleURL().replace(" ", "_"))) {
										} else {
											log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
											//System.exit(1);
										}
									}
								}
							}
						}
					}
					
				}
				else {
					log.info("Nombre max de fiches à traiter atteint.");
					break; // ignore les fiches suivantes
				}
				
				if ( nbFichesTraitees != 0 && (nbFichesTraitees % 500) == 0) {
					log.info("fiche traitées = "+nbFichesTraitees+", pause de 1s...");
					Thread.sleep(1000);
				}
			}
			
			
			
			return listeFichesTravail.size();
			
		} catch ( Exception e) {
			// une erreur est survenue
			log.error("Une erreur est survenue dans PrefetchFiches");
			return -1;
		}


	}
}
