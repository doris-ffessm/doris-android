/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.IntervenantFiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONDATABindingHelper;
import fr.ffessm.doris.prefetch.ezpublish.DorisAPI_JSONTreeHelper;
import fr.ffessm.doris.prefetch.ezpublish.JsonToDB;
import fr.ffessm.doris.prefetch.ezpublish.ObjNameNodeId;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;

public class PrefetchFiches {

	// Initialisation de la Gestion des Log 
	public static Log log = LogFactory.getLog(PrefetchFiches.class);

	private DorisDBHelper dbContext = null;
	private ConnectionSource connectionSource = null;

    private static Common_Outils commonOutils = new Common_Outils();

	private int nbMaxFichesATraiter;
    private int nbFichesParRequetes;

    public static int pauseEntreRequetes = 30000;

    public PrefetchFiches(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter, int nbFichesParRequetes) {
        this.dbContext = dbContext;
        this.connectionSource = connectionSource;

        this.nbMaxFichesATraiter = nbMaxFichesATraiter;
        this.nbFichesParRequetes = nbFichesParRequetes;
    }

    public int prefetchV4() throws Exception {
        log.debug("prefetchV4() - début");

        // - - - Fiches  - - -
        JsonToDB jsonToDB = new JsonToDB();
        DorisAPI_JSONTreeHelper dorisAPI_JSONTreeHelper = new DorisAPI_JSONTreeHelper();
        DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper();

        // TODO : Il faudrait mettre un While ici
        int nbFichesDORIS = nbMaxFichesATraiter;

        int count = 0;

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
                //log.debug("prefetchV4() - especeJSON : " + especeJSON.getFields().getNomCommunFr().getValue());

                if (especeJSON != null) {
                    Fiche espece = jsonToDB.getFicheFromJSONEspece(ficheNodeId, especeJSON);
                    String textePourRechercheRapide = espece.getNomCommun() + " " +
                            espece.getNomScientifique().replaceAll("\\{\\{[^\\{]*\\}\\}", "").replaceAll("\\([^\\(]*\\)", "");

                    // Préparation Texte pour recherche rapide
                    espece.setTextePourRechercheRapide(
                            (commonOutils.formatStringNormalizer(textePourRechercheRapide)
                            ).toLowerCase(Locale.FRENCH).trim() + "-" + ficheNodeId.getNodeId().intValue() + "-" + especeJSON.getFields().getZoneGeo().getValue()
                    );

                    // Groupe
                    if (especeJSON.getFields().getGroup().getValue() != null) {
                        Groupe groupeDoris = dbContext.groupeDao.queryForFirst(
                                dbContext.groupeDao.queryBuilder().where().eq("numeroGroupe", especeJSON.getFields().getGroup().getValue()).prepare()
                        );
                        espece.setGroupe(groupeDoris);
                    }

                    // Enregistrement dans la Base
                    final Fiche espece_final = espece;
                    TransactionManager.callInTransaction(connectionSource,
                            new Callable<Void>() {
                                public Void call() throws Exception {

                                    dbContext.ficheDao.create(espece_final);
                                    return null;
                                }
                            });


                    /* Héritée de la manière dont étaient stockées les données dans le Version 3 du Site,
                    On enregistre les différentes sections de la fiche dans l'ordre d'affichage de manière + "générique" que la verson 4 ne le fait
                     */
                    //log.debug("prefetchV4() - 'Sections' de la fiche");
                    List<SectionFiche> sectionsFiche = jsonToDB.getSectionsFicheFromJSONEspece(especeJSON);
                    for (SectionFiche sectionFiche : sectionsFiche) {

                        sectionFiche.setFiche(espece);
                        final SectionFiche sectionFiche_final = sectionFiche;
                        log.debug("prefetchV4() - sectionFiche_final.getFiche() : " + sectionFiche_final.getFiche().getNumeroFiche());

                        TransactionManager.callInTransaction(connectionSource,
                                new Callable<Void>() {
                                    public Void call() throws Exception {

                                        dbContext.sectionFicheDao.create(sectionFiche_final);

                                        return null;
                                    }
                                });

                    }

                /* Héritée de la manière dont étaient stockées les données dans le Version 3 du Site,
                    On enregistre les dénominations de la fiche dans une table dédiée
                */
                    //log.debug("prefetchV4() - Autres dénominations");
                    List<AutreDenomination> autresDenominations = jsonToDB.getAutresDenominationFicheFromJSONEspece(especeJSON);
                    for (AutreDenomination autreDenomination : autresDenominations) {
                        //log.debug("prefetchV4() - autreDenomination : "+autreDenomination.getDenomination());
                        if (!autreDenomination.getDenomination().contentEquals("<html />")) {
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
                    }


                    /* Ajout aux Classifications si pas encore dans la liste
                     */
                    //log.debug("prefetchV4() - Classification de la fiche");
                    /* Initialement on a sur la fiche que le niveau et la référence de la Classification */
                    List<ClassificationFiche> classificationsFiche = jsonToDB.getClassificationFicheFromJSONEspece(especeJSON);

                    for (ClassificationFiche classificationFiche : classificationsFiche) {
                        //log.debug("prefetchV4() - classification : " + classificationFiche.getClassification());
                        //log.debug("prefetchV4() - classification.getNiveau() : " + classificationFiche.getClassification().getNiveau());
                        //log.debug("prefetchV4() - classification.getNumeroDoris() : " + classificationFiche.getClassification().getNumeroDoris());

                        /* Si on ne trouve pas la Classification dans la Base on l'ajoute, sinon on l'utilise */
                        Classification classification = dbContext.classificationDao.queryForFirst(
                                dbContext.classificationDao.queryBuilder().where().eq("numeroDoris", classificationFiche.getClassification().getNumeroDoris()).prepare()
                        );
                        if (classification == null) {
                            //log.debug("prefetchV4() - classification par encore dans la base");

                            fr.ffessm.doris.prefetch.ezpublish.jsondata.classification.Classification classificationJSON = dorisAPI_JSONDATABindingHelper.getClassificationFieldsFromObjectId(classificationFiche.getClassification().getNumeroDoris());

                            // Parfois on n'arrive pas à la récupérer
                            if (classificationJSON != null) {
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

                        }

                        //log.debug("prefetchV4() - classification : " + classification.getNumeroDoris() + " - " + classification.getTermeFrancais());
                        /* on a ici la Classification que l'on va associer à la Fiche */
                        if (classification != null) {
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
                    }

                    /* Ajout Genre et Espèce aux Classification                  */
                    //log.debug("prefetchV4() - Ajout Genre et Espèce aux Classification");
                    Classification classificationGenre = dbContext.classificationDao.queryForFirst(
                            dbContext.classificationDao.queryBuilder().where().eq("niveau", "{{g}}Genre{{/g}}").and().eq("termeScientifique", "{{i}}" + especeJSON.getFields().getGenre().getValue() + "{{/i}})").prepare()
                    );
                    if (classificationGenre == null) {
                        classificationGenre = new Classification(0, "{{g}}Genre{{/g}}", "{{i}}" + especeJSON.getFields().getGenre().getValue() + "{{/i}}", "", "");
                        final Classification classificationGenreFinal = classificationGenre;
                        TransactionManager.callInTransaction(connectionSource,
                                new Callable<Void>() {
                                    public Void call() throws Exception {

                                        dbContext.classificationDao.create(classificationGenreFinal);

                                        return null;
                                    }
                                });
                    }
                    if (classificationGenre != null) {
                        final ClassificationFiche classification_final = new ClassificationFiche(espece, classificationGenre, 20);

                        // Enregistrement de la Classification de la Fiche
                        TransactionManager.callInTransaction(connectionSource,
                                new Callable<Void>() {
                                    public Void call() throws Exception {

                                        dbContext.classificationFicheDao.create(classification_final);

                                        return null;
                                    }
                                });
                    }

                    Classification classificationEspece = dbContext.classificationDao.queryForFirst(
                            dbContext.classificationDao.queryBuilder().where().eq("niveau", "{{g}}Espece{{/g}}").and().eq("termeScientifique", "{{i}}" + especeJSON.getFields().getEspece().getValue() + "{{/i}})").prepare()
                    );
                    if (classificationEspece == null) {
                        classificationEspece = new Classification(0, "{{g}}Espece{{/g}}", "{{i}}" + especeJSON.getFields().getEspece().getValue() + "{{/i}}", "", "");
                        final Classification classificationEspeceFinal = classificationEspece;
                        TransactionManager.callInTransaction(connectionSource,
                                new Callable<Void>() {
                                    public Void call() throws Exception {

                                        dbContext.classificationDao.create(classificationEspeceFinal);

                                        return null;
                                    }
                                });
                    }
                    if (classificationEspece != null) {
                        final ClassificationFiche classification_final = new ClassificationFiche(espece, classificationEspece, 21);

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
                    //log.debug("prefetchV4() - Doridiens ayant participés à la rédaction de la fiche :" + especeJSON.getFields().getVerificateurs().getValue());

                    for (String numeroVerificateur : especeJSON.getFields().getVerificateurs().getValue().split("-")) {
                        try {
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
                        } catch (NumberFormatException nfe) {
                            // ignore les entrées invalides
                        }
                    }
                    for (String numeroContributeur : especeJSON.getFields().getContributors().getValue().split("-")) {
                        try {
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
                        } catch (NumberFormatException nfe) {
                            // ignore les entrées invalides
                        }
                    }
                    for (String numeroCorrecteur : especeJSON.getFields().getCorrecteurs().getValue().split("-")) {
                        try {
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
                        } catch (NumberFormatException nfe) {
                            // ignore les entrées invalides
                        }
                    }
                    for (String numeroDoridien : especeJSON.getFields().getDoridiens().getValue().split("-")) {
                        try {
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
                        } catch (NumberFormatException nfe) {
                            // ignore les entrées invalides
                        }
                    }

                    /* * * * * * * * * * * *
                        Photos
                    * * * * * * * * * * * * */
                    //log.debug("prefetchV4() - imagesNodeIds = "+especeJSON.getFields().getImages().getValue());

                    List<Image> imageJSONListe = new ArrayList<Image>();

                    // itère sur les images trouvées pour cette fiche
                    for (String possibleImageId : especeJSON.getFields().getImages().getValue().split("&")) {
                        try {
                            int imageId = Integer.parseInt(possibleImageId.split("\\|")[0]);

                            //log.debug("prefetchV4() - imageId = "+possibleImageId.split("\\|")[0]);

                            // récupère les données associées à l'image
                            Image imageJSON = dorisAPI_JSONDATABindingHelper.getImageFromImageId(imageId);

                            //TODO : Traiter le cas des Videos un jour
                            if (imageJSON != null && imageJSON.getClassIdentifier().equals("image")) {
                                imageJSONListe.add(imageJSON);
                            }

                        } catch (NumberFormatException nfe) {
                            // ignore les entrées invalides
                        }
                    }

                    // recrée une entrée dans la base pour l'image
                    final List<PhotoFiche> listePhotoFiche = jsonToDB.getListePhotosFicheFromJsonImages(imageJSONListe);
                    final Fiche especeFinal = espece;
                    TransactionManager.callInTransaction(connectionSource,
                            new Callable<Void>() {
                                public Void call() throws Exception {
                                    int count = 0;
                                    for (PhotoFiche photoFiche : listePhotoFiche) {

                                        photoFiche.setFiche(especeFinal);

                                        dbContext.photoFicheDao.create(photoFiche);

                                        if (count == 0) {
                                            // met à jour l'image principale de la fiche
                                            especeFinal.setPhotoPrincipale(photoFiche);
                                            dbContext.ficheDao.update(especeFinal);
                                        }
                                        count++;
                                    }
                                    return null;
                                }
                            });

                    /* * * * * * * * * * * *
                        Zones Géographiques
                    * * * * * * * * * * * * */
                    //log.debug("prefetchV4() - Zone Géo. : "+especeJSON.getFields().getZoneGeo().getValue());

                    ZoneGeographique zoneGeographique = new ZoneGeographique();
                    for (String zoneGeoRefId : especeJSON.getFields().getZoneGeo().getValue().split("-")) {

                        try {
                            switch (Integer.parseInt(zoneGeoRefId)) {
                                // 71726 - ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE
                                case 71726:
                                    zoneGeographique.setId(1);
                                    break;
                                // On ignore pour l'instant
                                // 239910 - Méditérannée Française
                                // 239991 - Façade Atlantique Française
                                case 239910:
                                case 239991:
                                    zoneGeographique.setId(-1);
                                    break;
                                // 71728 - ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE
                                case 71728:
                                    zoneGeographique.setId(2);
                                    break;
                                // 71730 - ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE
                                case 71730:
                                    zoneGeographique.setId(3);
                                    break;
                                // 71731 - ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES
                                case 71731:
                                    zoneGeographique.setId(4);
                                    break;
                                // 135595 - ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST
                                case 135595:
                                    zoneGeographique.setId(5);
                                    break;
                                default:
                                    log.debug("prefetchV4() - Zone Géo. Inconnue : http://doris.ffessm.fr/api/ezx/v1/object/" + zoneGeoRefId);
                                    System.exit(1);
                            }

                            if (zoneGeographique.getId() >= 0) {
                                final ZoneGeographique zoneGeographique_final = zoneGeographique;
                                TransactionManager.callInTransaction(connectionSource,
                                        new Callable<Void>() {
                                            public Void call() throws Exception {
                                                dbContext.fiches_ZonesGeographiquesDao.create(new Fiches_ZonesGeographiques(zoneGeographique_final, espece_final));
                                                return null;
                                            }
                                        });
                            }
                        } catch (NumberFormatException nfe) {
                            // ignore les entrées invalides
                        }
                    }

                }
            }

            // fait une pause pour tenter d'éviter d'être banni par le site
            if(pauseEntreRequetes != 0 ) {
                log.debug("pause de "+pauseEntreRequetes+"ms...");
                Thread.sleep(pauseEntreRequetes);
            }

        }

        log.debug("prefetchV4() - fin");
        return 1;
    }

}
