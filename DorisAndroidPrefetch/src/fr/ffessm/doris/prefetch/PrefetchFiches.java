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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.IntervenantFiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.Constants.FileHtmlKind;
import fr.ffessm.doris.android.sitedoris.ErrorCollector;
import fr.ffessm.doris.android.sitedoris.FicheLight;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;
import fr.ffessm.doris.prefetch.PrefetchDorisWebSite.ActionKind;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
import fr.ffessm.doris.prefetch.ezpublish.JsonToDB;
import fr.ffessm.doris.prefetch.ezpublish.ObjNameNodeId;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;


public class PrefetchFiches {


	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchFiches.class);

	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;

    private static Common_Outils commonOutils = new Common_Outils();

	private ActionKind action;
	private int nbMaxFichesATraiter;
    private int nbFichesParRequetes;

	private List<Groupe> listeGroupes;
	private List<Participant> listeParticipants;


    private HashMap<Integer, Classification> listeClassification = new HashMap<>();

	private Fiche ficheMaj;
	private List<PhotoFiche> listePhotoFiche;

    public PrefetchFiches(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, int nbMaxFichesATraiter, List<Groupe> listeGroupes, List<Participant> listeParticipants) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;

        this.action = action;
        this.nbMaxFichesATraiter = nbMaxFichesATraiter;

        this.listeGroupes = listeGroupes;
        this.listeParticipants = listeParticipants;
    }

    public PrefetchFiches(DorisDBHelper dbContext, ConnectionSource connectionSource, ActionKind action, int nbMaxFichesATraiter, int nbFichesParRequetes, List<Groupe> listeGroupes, List<Participant> listeParticipants) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;

        this.action = action;
        this.nbMaxFichesATraiter = nbMaxFichesATraiter;
        this.nbFichesParRequetes = nbFichesParRequetes;

        this.listeGroupes = listeGroupes;
        this.listeParticipants = listeParticipants;
    }


    public int prefetchV4() throws Exception {
        log.debug("prefetchV4() - début");

        // - - - Fiches  - - -
        JsonToDB jsonToDB = new JsonToDB();
        DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper = new DorisAPI_JSONTreeHelper();
        DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper();

        // TODO : Il faudrait mettre un While ici
        int nbFichesDORIS = 99;

        int count = 0;

        /* On va mettre la */

        for (int i = 0; i < (nbFichesDORIS / nbFichesParRequetes); i++) {

            List<ObjNameNodeId> nodesIds = dorisAPI_JSONTreeHelper.getFichesNodeIds(nbFichesParRequetes, nbFichesParRequetes * i);

            for (ObjNameNodeId ficheNodeId : nodesIds) {
                count++;
                if (count > nbMaxFichesATraiter) {
                    log.debug("prefetchV4() - nbMaxFichesATraiter atteint");
                    i = 9999;
                    break;
                }

                // Référence de l'Espèce dans le message JSON
                Espece especeJSON = dorisAPI_JSONDATABindingHelper.getEspeceFieldsFromNodeId(ficheNodeId.getNodeId().intValue());
                log.debug("prefetchV4() - especeJSON : " + especeJSON.getFields().getNomCommunFr().getValue());

                Fiche espece = jsonToDB.getFicheFromJSONEspece(ficheNodeId, especeJSON);
                String textePourRechercheRapide = espece.getNomCommun() + " " +
                        espece.getNomScientifique().replaceAll("\\{\\{[^\\{]*\\}\\}", "").replaceAll("\\([^\\(]*\\)", "");

                /* Héritée de la manière dont étaient stockées les données dans le Version 3 du Site,
                On enregistre les différentes sections de la fiche dans l'ordre d'affichage de manière + "générique" que la verson 4 ne le fait
                 */
                log.debug("prefetchV4() - 'Sections' de la fiche");
                List<SectionFiche> sectionsFiche = jsonToDB.getSectionsFicheFromJSONEspece(especeJSON);
                for (SectionFiche sectionFiche : sectionsFiche) {

                    sectionFiche.setFiche(espece);
                    final SectionFiche sectionFiche_final = sectionFiche;

                    TransactionManager.callInTransaction(connectionSource,
                            new Callable<Void>() {
                                public Void call() throws Exception {

                                    dbContext.sectionFicheDao.create(sectionFiche_final);

                                    return null;
                                }
                            });

                }
             /*
                SectionFiche contenu = new SectionFiche(100+positionSectionDansFiche, dernierTitreSection, texte);
							contenu.setFiche(this);
							_contextDB.sectionFicheDao.create(contenu);
            */


            /* Héritée de la manière dont étaient stockées les données dans le Version 3 du Site,
                On enregistre les dénominations de la fiche dans une table dédiée
            */
                log.debug("prefetchV4() - Autres dénominations");
                List<AutreDenomination> autresDenominations = jsonToDB.getAutresDenominationFicheFromJSONEspece(especeJSON);
                for (AutreDenomination autreDenomination : autresDenominations) {
                    textePourRechercheRapide += " " + autreDenomination.getDenomination().replaceAll("\\([^\\(]*\\)", "");

                    autreDenomination.setFiche(espece);
                    final AutreDenomination autreDenomination_final = autreDenomination;

                    TransactionManager.callInTransaction(connectionSource,
                            new Callable<Void>() {
                                public Void call() throws Exception {

                                    dbContext.autreDenominationDao.create(autreDenomination_final);

                                    return null;
                                }
                            });

                }

                // Préparation Texte pour recherche rapide
                espece.setTextePourRechercheRapide(
                    (commonOutils.formatStringNormalizer(textePourRechercheRapide)
                    ).toLowerCase(Locale.FRENCH)
                );

                // Enregistrement dans la Base
                final Fiche espece_final = espece;
                TransactionManager.callInTransaction(connectionSource,
                        new Callable<Void>() {
                            public Void call() throws Exception {

                                dbContext.ficheDao.create(espece_final);
                                return null;
                            }
                        });

                /* Ajout aux Classifications si pas encore dans la liste
                 */
                log.debug("prefetchV4() - Classification de la fiche");
                /* Initialement on a sur la fiche que le niveau et la référence de la Classification */
                List<ClassificationFiche> classificationsFiche = jsonToDB.getClassificationFicheFromJSONEspece(especeJSON);

                for (ClassificationFiche classificationFiche : classificationsFiche) {
                    log.debug("prefetchV4() - classification : " + classificationFiche.getClassification());
                    log.debug("prefetchV4() - classification.getNiveau() : " + classificationFiche.getClassification().getNiveau());
                    log.debug("prefetchV4() - classification.getNumeroDoris() : " + classificationFiche.getClassification().getNumeroDoris());

                    /* Si on ne trouve pas la Classification dans la Base on l'ajoute, sinon on l'utilise */
                    Classification classification = dbContext.classificationDao.queryForFirst(
                            dbContext.classificationDao.queryBuilder().where().eq("numeroDoris", classificationFiche.getClassification().getNumeroDoris()).prepare()
                    );
                    if (classification == null) {
                        log.debug("prefetchV4() - classification par encore dans la base");

                        fr.ffessm.doris.prefetch.ezpublish.jsondata.classification.Classification classificationJSON = dorisAPI_JSONDATABindingHelper.getClassificationFieldsFromObjectId(classificationFiche.getClassification().getNumeroDoris());
                        classification = jsonToDB.getClassificationFromJSONClassification(
                                classificationFiche.getClassification().getNumeroDoris(), classificationFiche.getClassification().getNiveau(), classificationJSON);

                        final Classification classificationFinal = classification;
                        TransactionManager.callInTransaction(connectionSource,
                                new Callable<Void>() {
                                    public Void call() throws Exception {

                                        dbContext.classificationDao.create(classificationFinal);

                                        return null;
                                    }
                                });
                    }

                    log.debug("prefetchV4() - classification : " + classification.getNumeroDoris() + " - " + classification.getTermeFrancais());
                    /* on a ici la Classification que l'on va associer à la Fiche */

                    final ClassificationFiche classification_final = new ClassificationFiche(espece, classification, classificationFiche.getNumOrdre());

                    // Enregistrement de la Classification de la Fiche
                    TransactionManager.callInTransaction(connectionSource,
                            new Callable<Void>() {
                                public Void call() throws Exception {

                                    dbContext.classificationFicheDao.create(classification_final);

                                    return null;
                                }
                            });
                }

                /* * * * * * * * * * * *
                    Doridiens ayant participés à la rédaction de la fiche
                * * * * * * * * * * * * */
                log.debug("prefetchV4() - Doridiens ayant participés à la rédaction de la fiche");

                for(String numeroVerificateur : especeJSON.getFields().getVerificateurs().getValue().split("-")){
                    try{
                        if (numeroVerificateur != null && numeroVerificateur != "") {
                            final Participant doridien = dbContext.participantDao.queryForFirst(
                                    dbContext.participantDao.queryBuilder().where().eq("numeroParticipant", numeroVerificateur).prepare()
                            );

                            TransactionManager.callInTransaction(connectionSource,
                                    new Callable<Void>() {
                                        public Void call() throws Exception {
                                            dbContext.intervenantFicheDao.create(new IntervenantFiche(espece_final, doridien, Constants.ParticipantKind.VERIFICATEUR.ordinal()));
                                            return null;
                                        }
                                    });
                        }
                    } catch ( NumberFormatException nfe){
                        // ignore les entrées invalides
                    }
                }
                for(String numeroContributeur : especeJSON.getFields().getContributors().getValue().split("-")){
                    try{
                        if (numeroContributeur != null && numeroContributeur != "") {
                            final Participant doridien = dbContext.participantDao.queryForFirst(
                                    dbContext.participantDao.queryBuilder().where().eq("numeroParticipant", numeroContributeur).prepare()
                            );

                            TransactionManager.callInTransaction(connectionSource,
                                    new Callable<Void>() {
                                        public Void call() throws Exception {
                                            dbContext.intervenantFicheDao.create(new IntervenantFiche(espece_final, doridien, Constants.ParticipantKind.REDACTEUR.ordinal()));
                                            return null;
                                        }
                                    });
                        }
                    } catch ( NumberFormatException nfe){
                        // ignore les entrées invalides
                    }
                }
                for(String numeroCorrecteur : especeJSON.getFields().getCorrecteurs().getValue().split("-")){
                    try{
                        if (numeroCorrecteur != null && numeroCorrecteur != "") {
                            final Participant doridien = dbContext.participantDao.queryForFirst(
                                    dbContext.participantDao.queryBuilder().where().eq("numeroParticipant", numeroCorrecteur).prepare()
                            );

                            TransactionManager.callInTransaction(connectionSource,
                                    new Callable<Void>() {
                                        public Void call() throws Exception {
                                            dbContext.intervenantFicheDao.create(new IntervenantFiche(espece_final, doridien, Constants.ParticipantKind.CORRECTEUR.ordinal()));
                                            return null;
                                        }
                                    });
                        }
                    } catch ( NumberFormatException nfe){
                        // ignore les entrées invalides
                    }
                }
                for(String numeroDoridien : especeJSON.getFields().getDoridiens().getValue().split("-")){
                    try{
                        if (numeroDoridien != null && numeroDoridien != "") {
                            final Participant doridien = dbContext.participantDao.queryForFirst(
                                    dbContext.participantDao.queryBuilder().where().eq("numeroParticipant", numeroDoridien).prepare()
                            );

                            TransactionManager.callInTransaction(connectionSource,
                                    new Callable<Void>() {
                                        public Void call() throws Exception {
                                            dbContext.intervenantFicheDao.create(new IntervenantFiche(espece_final, doridien, Constants.ParticipantKind.RESPONSABLE_REGIONAL.ordinal()));
                                            return null;
                                        }
                                    });
                        }
                    } catch ( NumberFormatException nfe){
                        // ignore les entrées invalides
                    }
                }
            }

        }

        log.debug("prefetchV4() - fin");
        return 1;
    }


	public int prefetch() {
		// - - - Liste des Fiches - - -
		// Récupération de la liste des fiches sur le site de DORIS
		// Elles sont récupérées dans tous les cas sauf NODOWNLOAD, i.e. : INIT, UPDATE, CDDVD
		
		PrefetchTools prefetchTools = new PrefetchTools();
		SiteDoris siteDoris = new SiteDoris();
		
		String contenuFichierHtml = null;
		
		try {
			
			String listeFichesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/listeFiches.html";
			log.info("Récup. Liste Fiches Doris : " + listeFichesFichier);
			
			if ( action != ActionKind.NODWNLD ){
				String listeToutesFiches = Constants.getListeFichesUrl(Constants.getNumZoneForUrl(ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES)); 
				if (prefetchTools.getFichierFromUrl(listeToutesFiches, listeFichesFichier)) {
					contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeFichesFichier), FileHtmlKind.LISTE_FICHES);
				} else {
					log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
					System.exit(1);
				}
			} else {
				// NODWNLD
				listeFichesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeFiches.html";
				if (new File(listeFichesFichier).exists()) {
					contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(new File(listeFichesFichier), FileHtmlKind.LISTE_FICHES);
				} else {
					log.error("Une erreur est survenue lors de la récupération de la liste des fiches");
					System.exit(0);
				}
			}
			
			HashSet<FicheLight> listeFichesSite = siteDoris.getListeFichesFromHtml(contenuFichierHtml);
			log.info("Nb Fiches sur le site : "+listeFichesSite.size());
	
			// Récupération de la liste des fiches dans le dossier de référence
			// Si NODWNLD la liste sera utilisée pour faire le traitement
			// Si UPDATED ou CDDVD, elle permettra de déduire les fiches à télécharger de nouveau : les fiches ayant changées de statut
			HashSet<FicheLight> listFichesFromRef = new HashSet<FicheLight>(0);
			if ( action != ActionKind.INIT ){
				listeFichesFichier = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/listeFiches.html";
				if (new File(listeFichesFichier).exists()) {
					listFichesFromRef = siteDoris.getListeFichesFromHtml( prefetchTools.getFichierTxtFromDisk(new File(listeFichesFichier), FileHtmlKind.LISTE_FICHES) );
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
			listeFichesSite = null;
			
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
			if ( action == ActionKind.UPDATE || action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI) {
				listFichesModif = siteDoris.getListeFichesUpdated(listFichesFromRef, listeFichesTravail);
			}
			listFichesFromRef = null;
			
			int nbFichesTraitees = 0;
			String urlFiche =  "";
			String fichierSiteFicheUrl = "";
			String fichierRefFicheUrl = "";
			File fichierSiteFiche = null;
			File fichierRefFiche = null;
			
			Fiche fiche = null;
			
			String urlListePhotos = "";
			String fichierSiteListePhotosUrl = "";
			String fichierRefListePhotosUrl = "";
			File fichierSiteListePhotos = null;
			File fichierRefListePhotos = null;
			String contenuFichierHtmlListePhotos = null;
			
			String fichierImageRacine = "";
			String fichierImageRefRacine = "";

			for (FicheLight ficheLight : listeFichesTravail) {
				if (  nbFichesTraitees <= nbMaxFichesATraiter ) {
					log.debug("doMain() - Traitement Fiche : "+ficheLight.getNomCommun());
					
					String errorGroup = "fiches.fiche_"+ficheLight.getNumeroFiche();
					ErrorCollector.getInstance().addGroup(errorGroup);
					
					urlFiche =  Constants.getFicheFromIdUrl( ficheLight.getNumeroFiche() );
					fichierSiteFicheUrl = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/fiche-"+ficheLight.getNumeroFiche()+".html";
					fichierRefFicheUrl = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/fiche-"+ficheLight.getNumeroFiche()+".html";
					fichierSiteFiche = new File(fichierSiteFicheUrl);
					fichierRefFiche = new File(fichierRefFicheUrl);
					
					if ( action == ActionKind.INIT ) {
						if (prefetchTools.getFichierFromUrl(urlFiche, fichierSiteFicheUrl)) {
							nbFichesTraitees += 1;
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(fichierSiteFiche, FileHtmlKind.FICHE);
						} else {
							log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
							ErrorCollector.getInstance().addError(errorGroup, "fiche introuvable", "Une erreur est survenue lors de la récupération de la fiche (getFichierFromUrl) depuis l'url :" +urlFiche+ " , "+ficheLight.getNomCommun()+ " , fiche id="+ficheLight.getNumeroFiche());
							// Solution de contournement désespérée 
							urlFiche = Constants.getFicheFromNomCommunUrl(ficheLight.getNomCommun());
							log.error("=> Tentative sur : "+urlFiche);
							if (prefetchTools.getFichierFromUrl(urlFiche, fichierSiteFicheUrl)) {
								nbFichesTraitees += 1;
								contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(fichierSiteFiche, FileHtmlKind.FICHE);
							} else {
								log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
								ErrorCollector.getInstance().addError(errorGroup, "fiche VRAIMENT introuvable", "Une erreur est survenue lors de la récupération de la fiche (getFichierFromUrl) depuis l'url :" +urlFiche+ " , "+ficheLight.getNomCommun()+ " , fiche id="+ficheLight.getNumeroFiche());
								
								continue;
							}
						}
					} else if ( action == ActionKind.UPDATE || action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ) {
	
						if ( fichierRefFiche.exists() && !listFichesModif.contains(ficheLight)) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(fichierRefFiche, FileHtmlKind.FICHE);
							nbFichesTraitees += 1;
						} else {
							if (prefetchTools.getFichierFromUrl(urlFiche, fichierSiteFicheUrl)) {
								nbFichesTraitees += 1;
								contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(fichierSiteFiche, FileHtmlKind.FICHE);
							} else {
								log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
								ErrorCollector.getInstance().addError(errorGroup, "fiche introuvable", "Une erreur est survenue lors de la récupération de la fiche (getFichierFromUrl) depuis l'url :" +urlFiche+ " , "+ficheLight.getNomCommun()+ " , fiche id="+ficheLight.getNumeroFiche());
								// Solution de contournement désespérée 
								urlFiche = Constants.getFicheFromNomCommunUrl(ficheLight.getNomCommun());
								log.error("=> Tentative sur : "+urlFiche);
								if (prefetchTools.getFichierFromUrl(urlFiche, fichierSiteFicheUrl)) {
									nbFichesTraitees += 1;
									contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(fichierSiteFiche, FileHtmlKind.FICHE);
								} else {
									log.error("Une erreur est survenue lors de la récupération de la fiche : "+urlFiche);
									ErrorCollector.getInstance().addError(errorGroup, "fiche VRAIMENT introuvable", "Une erreur est survenue lors de la récupération de la fiche (getFichierFromUrl) depuis l'url :" +urlFiche+ " , "+ficheLight.getNomCommun()+ " , fiche id="+ficheLight.getNumeroFiche());
									continue;
								}
							}
						}
					} else if ( action == ActionKind.NODWNLD ) {
						if (fichierRefFiche.exists()) {
							contenuFichierHtml = prefetchTools.getFichierTxtFromDisk(fichierRefFiche, FileHtmlKind.FICHE);
							nbFichesTraitees += 1;
						} else {
							log.error("La récupération de la fiche sur le disque : "+fichierRefFicheUrl+" a échoué.");
						}
					}

					fiche = dbContext.ficheDao.queryForFirst(
							dbContext.ficheDao.queryBuilder().where().eq("numeroFiche", ficheLight.getNumeroFiche()).prepare()
						);
					fiche.setContextDB(dbContext);
					fiche.getFicheFromHtml(contenuFichierHtml, listeGroupes, listeParticipants);
					ficheMaj = fiche;
					TransactionManager.callInTransaction(connectionSource,
							new Callable<Void>() {
								public Void call() throws Exception {
									dbContext.ficheDao.update(ficheMaj);
									return null;
							    }
							});
					
					// mise à jour des champs inverse
					//dbContext.ficheDao.refresh(fiche);
					
					log.info("doMain() - Info Fiche {");
					log.info("doMain() -      - ref : "+fiche.getNumeroFiche());
					log.info("doMain() -      - nom : "+fiche.getNomCommunNeverEmpty());
					log.info("doMain() -      - etat : "+fiche.getEtatFiche());
					log.info("doMain() - }");
					
					urlListePhotos = "http://doris.ffessm.fr/fiche_photo_liste_apercu.asp?fiche_numero="+fiche.getNumeroFiche();
					fichierSiteListePhotosUrl = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML + "/fiche-"+fiche.getNumeroFiche()+"_listePhotos.html";
					fichierRefListePhotosUrl = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_HTML_REF + "/fiche-"+fiche.getNumeroFiche()+"_listePhotos.html";
					fichierSiteListePhotos = new File(fichierSiteListePhotosUrl);
					fichierRefListePhotos = new File(fichierRefListePhotosUrl);
					
					contenuFichierHtmlListePhotos = null;
					
					if ( action == ActionKind.INIT ) {
						if (prefetchTools.getFichierFromUrl(urlListePhotos, fichierSiteListePhotosUrl)) {
							contenuFichierHtmlListePhotos = prefetchTools.getFichierTxtFromDisk(fichierSiteListePhotos, FileHtmlKind.FICHE);
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
							ErrorCollector.getInstance().addError(errorGroup, "liste photo introuvable" ,"Une erreur est survenue lors de la récupération de la liste de photo pour la fiche "+fiche.getNumeroFiche()+" - "+fiche.getNomCommunNeverEmpty()+" url de la liste: " +urlListePhotos);
							continue;
						}
					} else if ( action == ActionKind.UPDATE || action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ) {
						if (fichierRefListePhotos.exists() && !listFichesModif.contains(fiche)) {
							contenuFichierHtmlListePhotos = prefetchTools.getFichierTxtFromDisk(fichierRefListePhotos, FileHtmlKind.FICHE);
						} else {
							if (prefetchTools.getFichierFromUrl(urlListePhotos, fichierSiteListePhotosUrl)) {
								contenuFichierHtmlListePhotos = prefetchTools.getFichierTxtFromDisk(fichierSiteListePhotos, FileHtmlKind.FICHE);
							
							} else {
								log.warn("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
								continue;
							}
						}
					} else if ( action == ActionKind.NODWNLD ){
						if (fichierRefListePhotos.exists()) {
							contenuFichierHtmlListePhotos = prefetchTools.getFichierTxtFromDisk(fichierRefListePhotos, FileHtmlKind.FICHE);
						} else {
							log.error("Une erreur est survenue lors de la récupération de la liste de photo pour la fiche : "+urlListePhotos);
							
						}
					}
					
					if(contenuFichierHtmlListePhotos != null) {
						
						listePhotoFiche = siteDoris.getListePhotosFicheFromHtml(fiche, contenuFichierHtmlListePhotos);
						
						// Maj Base de données
						ficheMaj = fiche;
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
						/*
						if ( action == ActionKind.CDDVD_MED || action == ActionKind.CDDVD_HI ) {

							fichierImageRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES + "/";
							fichierImageRefRacine = PrefetchConstants.DOSSIER_RACINE + "/" + PrefetchConstants.DOSSIER_IMAGES_REF + "/";

							for (PhotoFiche photoFiche : listePhotoFiche){

								if ( !photoFiche.getCleURL().isEmpty() ) {
									// Vignettes
									if( ! prefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+"/"+photoFiche.getCleURL().replace(" ", "_") ) ){
										if (prefetchTools.getFichierFromUrl(Constants.VIGNETTE_BASE_URL+"/"+photoFiche.getCleURL().replace(" ", "%20"), fichierImageRacine+PrefetchConstants.SOUSDOSSIER_VIGNETTES+"/"+photoFiche.getCleURL().replace(" ", "_"))) {
										} else {
											log.warn("Erreur sur une image 'Photos Vig' ; fiche : "+ficheLight.getNumeroFiche()+"-"+ficheLight.getNomCommun()+" ; url image : "+photoFiche.getCleURL());

											ErrorCollector.getInstance().addError(errorGroup, "Erreur sur une image 'Photos Vig' ; fiche : "+ficheLight.getNumeroFiche()+"-"+ficheLight.getNomCommun()+" ; url image : "+photoFiche.getCleURL());
											//System.exit(1);
										}
									}
									// Qualité Intermédiaire
									if( ! prefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_MED_RES+"/"+photoFiche.getCleURL().replace(" ", "_") ) ){
										if (prefetchTools.getFichierFromUrl(Constants.MOYENNE_BASE_URL+"/"+photoFiche.getCleURL().replace(" ", "%20"), fichierImageRacine+PrefetchConstants.SOUSDOSSIER_MED_RES+"/"+photoFiche.getCleURL().replace(" ", "_"))) {
										} else {
											log.warn("Erreur sur une image 'Photos Moy' ; fiche : "+ficheLight.getNumeroFiche()+"-"+ficheLight.getNomCommun()+" ; url image : "+photoFiche.getCleURL());
											ErrorCollector.getInstance().addError(errorGroup, "Erreur sur une image 'Photos Moy' ; fiche : "+ficheLight.getNumeroFiche()+"-"+ficheLight.getNomCommun()+" ; url image : "+photoFiche.getCleURL());
											//System.exit(1);
										}
									}
									// Haute Qualité seulement si "DVD"
									if ( action == ActionKind.CDDVD_HI ) {
										if( ! prefetchTools.isFileExistingPath( fichierImageRefRacine+PrefetchConstants.SOUSDOSSIER_HI_RES+"/"+photoFiche.getCleURL().replace(" ", "_") ) ){
											if (prefetchTools.getFichierFromUrl(Constants.GRANDE_BASE_URL+"/"+photoFiche.getCleURL().replace(" ", "%20"), fichierImageRacine+PrefetchConstants.SOUSDOSSIER_HI_RES+"/"+photoFiche.getCleURL().replace(" ", "_"))) {
											} else {
												log.warn("Erreur sur une image 'Photos' ; fiche : "+ficheLight.getNumeroFiche()+"-"+ficheLight.getNomCommun()+" ; url image : "+photoFiche.getCleURL());
												ErrorCollector.getInstance().addError(errorGroup, "Erreur sur une image 'Photos' ; fiche : "+ficheLight.getNumeroFiche()+"-"+ficheLight.getNomCommun()+" ; url image : "+photoFiche.getCleURL());
												//System.exit(1);
											}
										}
									}
								}
							}
						}*/
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
			log.error(e);
			return -1;
		}


	}
}
