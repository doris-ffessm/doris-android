package fr.ffessm.doris.prefetch;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.support.ConnectionSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.IntervenantFiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.datamodel.ZoneGeographique;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.prefetch.ezpublish.DorisOAuth2ClientCredentials;
import fr.ffessm.doris.prefetch.ezpublish.ObjNameNodeId;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;

public class PrefetchFiches extends AbstractNodePrefetch<Fiche, Espece, Dao<Fiche, Integer>> {

    // Initialisation de la Gestion des Log
    public static Log log = LogFactory.getLog(PrefetchFiches.class);

    private static Common_Outils commonOutils = new Common_Outils();

    public PrefetchFiches(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter) {
        super(dbContext, connectionSource, nbMaxFichesATraiter);
    }

    public PrefetchFiches(DorisDBHelper dbContext, ConnectionSource connectionSource, int nbMaxFichesATraiter, int nbFichesParRequetes) {
        super(dbContext, connectionSource, nbMaxFichesATraiter, nbFichesParRequetes);
    }

    @Override
    Fiche getNewDBObjectInstance() {
        return new Fiche();
    }

    @Override
    List<ObjNameNodeId> getNodeIdsFromWeb(int nbLimitRequest, int offset) throws IOException, WebSiteNotAvailableException {
        return dorisAPI_JSONTreeHelper.getFichesNodeIds(nbLimitRequest, offset);
    }

    @Override
    int getNbNodeIdsFromWeb() throws IOException, WebSiteNotAvailableException {
        return dorisAPI_JSONTreeHelper.getNbFichesNodeIds();
    }


    @Override
    Espece getJsonObjectFromWeb(int id) throws IOException, WebSiteNotAvailableException {
        return dorisAPI_JSONDATABindingHelper.getEspeceFieldsFromNodeId(id);
    }

    @Override
    Fiche getDBObjectFromJSONObject(ObjNameNodeId objNameNodeId, Espece espece) {

        Fiche fiche = jsonToDB.getFicheFromJSONEspece(objNameNodeId, espece);

        // complement entry data and cross references
        // Texte pour recherche rapide
        String textePourRechercheRapide = fiche.getNomCommun() + " " +
                fiche.getNomScientifique().replaceAll("\\{\\{[^\\{]*\\}\\}", "").replaceAll("\\([^\\(]*\\)", "");
        fiche.setTextePourRechercheRapide(
                (commonOutils.formatStringNormalizer(textePourRechercheRapide)
                ).toLowerCase(Locale.FRENCH).trim() + "-" + objNameNodeId.getNodeId().intValue() + "-" + espece.getFields().getZoneGeo().getValue()
        );
        // Groupe
        if (espece.getFields().getGroup().getValue() != null) {
            try {
                Groupe groupeDoris = dbContext.groupeDao.queryForFirst(
                        dbContext.groupeDao.queryBuilder().where().eq("numeroGroupe", espece.getFields().getGroup().getValue()).prepare()
                );
                fiche.setGroupe(groupeDoris);
            } catch (SQLException throwables) {
                log.error(String.format("Cannot set group %s due to exception %s", espece.getFields().getGroup().getValue(), throwables.getMessage()), throwables);
            }
        }

        return fiche;
    }

    @Override
    protected void postNodeCreation(ObjNameNodeId objNameNodeId, Fiche ficheDB, Espece especeJSON) throws SQLException, WebSiteNotAvailableException {
        super.postNodeCreation(objNameNodeId, ficheDB, especeJSON);
        // add/update data of the other tables related to the Fiche

        try {
            removeSectionFicheForFiche(ficheDB, especeJSON);
            updateSectionFicheForFiche(ficheDB, especeJSON);
            removeAutreDenominationForFiche(ficheDB, especeJSON);
            updateAutreDenominationForFiche(ficheDB, especeJSON);
            removeClassificationForFiche(ficheDB, especeJSON);
            updateClassificationForFiche(ficheDB, especeJSON);
            // TODO remove previous updateGenreForFiche ?
            updateGenreForFiche(ficheDB, especeJSON);
            removeParticipantForFiche(ficheDB,especeJSON);
            updateParticipantForFiche(ficheDB, especeJSON);
            removePhotoForFiche(ficheDB, especeJSON);
            updatePhotoForFiche(ficheDB, especeJSON);
            removeZoneGeographiqueForFiche(ficheDB, especeJSON);
            updateZoneGeographiqueForFiche(ficheDB, especeJSON);
        } catch (Exception e) {
            // revert element that have been partially added if the Fiche isn't fully retrieved (web connection error)
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        getDao().delete(ficheDB);
                        return null;
                    });
            removeSectionFicheForFiche(ficheDB, especeJSON);
            removeAutreDenominationForFiche(ficheDB, especeJSON);
            removeClassificationForFiche(ficheDB, especeJSON);
            // TODO remove previous updateGenreForFiche ?
            removeParticipantForFiche(ficheDB, especeJSON);
            removePhotoForFiche(ficheDB, especeJSON);
            removeZoneGeographiqueForFiche(ficheDB, especeJSON);
            String uri = DorisOAuth2ClientCredentials.getServerNodeUrlTousLesChamps( objNameNodeId.getNodeId().toString() );
            log.debug("Faulty specie uri "+uri);
            throw e;
        }
    }

    protected void removeSectionFicheForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        // remove previous SectionFiche if any
        SectionFiche sectionFicheQuery = new SectionFiche();
        sectionFicheQuery.setFiche(ficheDB);
        List<SectionFiche> previousRelatedSectionFiche = dbContext.sectionFicheDao.queryForMatching(sectionFicheQuery);
        if (!previousRelatedSectionFiche.isEmpty()) {
            log.debug(String.format("delete %d previous SectionFiche related to this Fiche", previousRelatedSectionFiche.size()));

            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.sectionFicheDao.delete(previousRelatedSectionFiche);
                        return null;
                    });
        }
    }
    protected void updateSectionFicheForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        // Create SectionFiche related to this Fiche
        List<SectionFiche> sectionsFiche = jsonToDB.getSectionsFicheFromJSONEspece(especeJSON);
        log.debug(String.format("add %d SectionFiche related to this Fiche", sectionsFiche.size()));
        for (SectionFiche sectionFiche : sectionsFiche) {

            sectionFiche.setFiche(ficheDB);
            final SectionFiche sectionFiche_final = sectionFiche;
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.sectionFicheDao.create(sectionFiche_final);
                        return null;
                    });
        }
    }

    protected void removeAutreDenominationForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        // remove previous SectionFiche if any
        AutreDenomination query = new AutreDenomination();
        query.setFiche(ficheDB);
        List<AutreDenomination> previousRelatedToFiche = dbContext.autreDenominationDao.queryForMatching(query);
        if(!previousRelatedToFiche.isEmpty()){
            log.debug(String.format("delete %d previous AutreDenomination related to this Fiche",previousRelatedToFiche.size()));
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.autreDenominationDao.delete(previousRelatedToFiche);
                        return null;
                    });
        }
    }

    protected void updateAutreDenominationForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        // Create AutreDenomination related to this Fiche
        List<AutreDenomination> autresDenominations = jsonToDB.getAutresDenominationFicheFromJSONEspece(especeJSON);
        log.debug(String.format("add %d AutreDenomination related to this Fiche", autresDenominations.size()));
        for (AutreDenomination autreDenomination : autresDenominations) {
            //log.debug("prefetchV4() - autreDenomination : "+autreDenomination.getDenomination());
            if (!autreDenomination.getDenomination().contentEquals("<html />")) {
                String textePourRechercheRapide = ficheDB.getTextePourRechercheRapide();
                textePourRechercheRapide += " " + autreDenomination.getDenomination().replaceAll("\\([^\\(]*\\)", "");

                autreDenomination.setFiche(ficheDB);
                final AutreDenomination autreDenomination_final = autreDenomination;

                TransactionManager.callInTransaction(connectionSource,
                        (Callable<Void>) () -> {

                            dbContext.autreDenominationDao.create(autreDenomination_final);

                            return null;
                        });
            }
        }
        // update Fiche because textePourRechercheRapide has changed)
        try {
            Fiche ficheQuery = new Fiche();
            ficheQuery.setNumeroFiche(ficheDB.getNumeroFiche());
            ficheDB.setId(dbContext.ficheDao.queryForMatching(ficheQuery).get(0).getId());
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.ficheDao.createOrUpdate(ficheDB);
                        return null;
                    });
        } catch (Exception e) {
            log.warn(String.format("Failed to update Fiche %d with new textePourRechercheRapide", ficheDB.getNumeroFiche()), e);
        }
    }


    protected void removeClassificationForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        // remove previous ClassificationFiche if any
        ClassificationFiche query = new ClassificationFiche();
        query.setFiche(ficheDB);
        List<ClassificationFiche> previousRelatedToFiche = dbContext.classificationFicheDao.queryForMatching(query);
        if(!previousRelatedToFiche.isEmpty()){
            log.debug(String.format("delete %d previous ClassificationFiche related to this Fiche",previousRelatedToFiche.size()));
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.classificationFicheDao.delete(previousRelatedToFiche);
                        return null;
                    });
        }
    }


    /**
     *  Ajout aux Classifications si pas encore dans la liste
     */
    protected void updateClassificationForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        /* Initialement on a sur la fiche que le niveau et la référence de la Classification */
        List<ClassificationFiche> classificationsFiche = jsonToDB.getClassificationFicheFromJSONEspece(especeJSON);

        log.debug(String.format("add %d ClassificationFiche related to this Fiche",classificationsFiche.size()));
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

                fr.ffessm.doris.prefetch.ezpublish.jsondata.classification.Classification classificationJSON = null;
                try {
                    classificationJSON = dorisAPI_JSONDATABindingHelper.getClassificationFieldsFromObjectId(classificationFiche.getClassification().getNumeroDoris());
                } catch (IOException | WebSiteNotAvailableException e) {
                    String uri = DorisOAuth2ClientCredentials.getServerObjectUrlTousLesChamps( String.valueOf(classificationFiche.getClassification().getNumeroDoris()) );
                    log.warn(String.format("Failed to retrieve classification fields %d %s \n\tfor fiche %d - %s",
                            classificationFiche.getClassification().getNumeroDoris(), uri,
                            ficheDB.getWebNodeId(),
                            DorisOAuth2ClientCredentials.SPECIES_NODE_URL), e);
                }

                // Parfois on n'arrive pas à la récupérer
                if (classificationJSON != null) {
                    classification = jsonToDB.getClassificationFromJSONClassification(
                            classificationFiche.getClassification().getNumeroDoris(), classificationFiche.getClassification().getNiveau(), classificationJSON);

                    //log.debug(String.format("add new Classification %d %s related to one of the ClassificationFiche of this Fiche",
                    //        classification.getNumeroDoris(),
                    //        classification.getTermeFrancais()));
                    final Classification classificationFinal = classification;
                    TransactionManager.callInTransaction(connectionSource,
                            (Callable<Void>) () -> {
                                dbContext.classificationDao.create(classificationFinal);
                                return null;
                            });

                }

            }

            //log.debug("prefetchV4() - classification : " + classification.getNumeroDoris() + " - " + classification.getTermeFrancais());
            /* on a ici la Classification que l'on va associer à la Fiche */
            if (classification != null) {
                final ClassificationFiche classification_final = new ClassificationFiche(ficheDB, classification, classificationFiche.getNumOrdre());

                // Enregistrement de la Classification de la Fiche
                TransactionManager.callInTransaction(connectionSource,
                        (Callable<Void>) () -> {
                            dbContext.classificationFicheDao.create(classification_final);
                            return null;
                        });
            }
        }
    }

    /* Ajout Genre et Espèce aux Classification                  */
    protected void updateGenreForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        log.debug("Adding ClassificationFiche(s) about Genre/Espece");
        int nbGenreEspece = 0;
        Classification classificationGenre = dbContext.classificationDao.queryForFirst(
                dbContext.classificationDao.queryBuilder().where().eq("niveau", "{{g}}Genre{{/g}}").and().eq("termeScientifique", "{{i}}" + especeJSON.getFields().getGenre().getValue() + "{{/i}})").prepare()
        );
        if (classificationGenre == null) {
            nbGenreEspece++;
            classificationGenre = new Classification(0, "{{g}}Genre{{/g}}", "{{i}}" + especeJSON.getFields().getGenre().getValue() + "{{/i}}", "", "");
            final Classification classificationGenreFinal = classificationGenre;
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.classificationDao.create(classificationGenreFinal);
                        return null;
                    });
        }

        if (classificationGenre != null) {
            nbGenreEspece++;
            final ClassificationFiche classification_final = new ClassificationFiche(ficheDB, classificationGenre, 20);

            // Enregistrement de la Classification de la Fiche
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.classificationFicheDao.create(classification_final);
                        return null;
                    });
        }

        Classification classificationEspece = dbContext.classificationDao.queryForFirst(
                dbContext.classificationDao.queryBuilder().where().eq("niveau", "{{g}}Espece{{/g}}").and().eq("termeScientifique", "{{i}}" + especeJSON.getFields().getEspece().getValue() + "{{/i}})").prepare()
        );
        if (classificationEspece == null) {
            nbGenreEspece++;
            classificationEspece = new Classification(0, "{{g}}Espece{{/g}}", "{{i}}" + especeJSON.getFields().getEspece().getValue() + "{{/i}}", "", "");
            final Classification classificationEspeceFinal = classificationEspece;
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.classificationDao.create(classificationEspeceFinal);
                        return null;
                    });
        }
        if (classificationEspece != null) {
            nbGenreEspece++;
            final ClassificationFiche classification_final = new ClassificationFiche(ficheDB, classificationEspece, 21);

            // Enregistrement de la Classification de la Fiche
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.classificationFicheDao.create(classification_final);
                        return null;
                    });
        }
        log.info(String.format("added %d ClassificationFiche(s) about Genre/Espece", nbGenreEspece));
    }


    protected void removeParticipantForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        // remove previous IntervenantFiche if any
        IntervenantFiche query = new IntervenantFiche();
        query.setFiche(ficheDB);
        List<IntervenantFiche> previousRelatedToFiche = dbContext.intervenantFicheDao.queryForMatching(query);
        if(!previousRelatedToFiche.isEmpty()){
            log.info(String.format("delete %d previous IntervenantFiche related to this Fiche",previousRelatedToFiche.size()));
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.intervenantFicheDao.delete(previousRelatedToFiche);
                        return null;
                    });
        }
    }

    /**
     * Doridiens ayant participés à la rédaction de la fiche
     */
    protected void updateParticipantForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        int nbIntervenants = 0;
        Object numeroAuteurPrincipal = especeJSON.getFields().getPrincipalWriter().getValue();
        if(numeroAuteurPrincipal != null && numeroAuteurPrincipal instanceof String && numeroAuteurPrincipal != ""){
            try {
                final Participant doridien = dbContext.participantDao.queryForFirst(
                        dbContext.participantDao.queryBuilder().where().eq("numeroParticipant", numeroAuteurPrincipal).prepare()
                );
                if(doridien != null) {
                    //log.debug(String.format("add IntervenantFiche %s as REDACTEUR_PRINCIPAL", doridien.getNom()));
                    nbIntervenants++;
                    TransactionManager.callInTransaction(connectionSource,
                            (Callable<Void>) () -> {
                                dbContext.intervenantFicheDao.create(new IntervenantFiche(ficheDB, doridien, Constants.ParticipantKind.REDACTEUR_PRINCIPAL.ordinal()));
                                return null;
                            });
                }
            } catch (NumberFormatException nfe) {
                // ignore les entrées invalides
            }
        }

        for (String numeroVerificateur : especeJSON.getFields().getVerificateurs().getValue().split("-")) {
            try {
                if (numeroVerificateur != null && numeroVerificateur != "") {
                    final Participant doridien = dbContext.participantDao.queryForFirst(
                            dbContext.participantDao.queryBuilder().where().eq("numeroParticipant", numeroVerificateur).prepare()
                    );
                    if(doridien != null) {
                        //log.debug(String.format("add IntervenantFiche %s as VERIFICATEUR", doridien.getNom()));
                        nbIntervenants++;
                        TransactionManager.callInTransaction(connectionSource,
                                (Callable<Void>) () -> {
                                    dbContext.intervenantFicheDao.create(new IntervenantFiche(ficheDB, doridien, Constants.ParticipantKind.VERIFICATEUR.ordinal()));
                                    return null;
                                });
                    }
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
                    if(doridien != null) {
                        //log.debug(String.format("add IntervenantFiche %s as REDACTEUR", doridien.getNom()));
                        nbIntervenants++;
                        TransactionManager.callInTransaction(connectionSource,
                                (Callable<Void>) () -> {
                                    dbContext.intervenantFicheDao.create(new IntervenantFiche(ficheDB, doridien, Constants.ParticipantKind.REDACTEUR.ordinal()));
                                    return null;
                                });
                    }
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
                    if(doridien != null) {
                        //log.debug(String.format("add IntervenantFiche %s as CORRECTEUR", doridien.getNom()));
                        nbIntervenants++;
                        TransactionManager.callInTransaction(connectionSource,
                                (Callable<Void>) () -> {
                                    dbContext.intervenantFicheDao.create(new IntervenantFiche(ficheDB, doridien, Constants.ParticipantKind.CORRECTEUR.ordinal()));
                                    return null;
                                });
                    }
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
                    if(doridien != null) {
                        //log.debug(String.format("add IntervenantFiche %s as RESPONSABLE_REGIONAL", doridien.getNom()));
                        nbIntervenants++;
                        TransactionManager.callInTransaction(connectionSource,
                                (Callable<Void>) () -> {
                                    dbContext.intervenantFicheDao.create(new IntervenantFiche(ficheDB, doridien, Constants.ParticipantKind.RESPONSABLE_REGIONAL.ordinal()));
                                    return null;
                                });
                    }
                }
            } catch (NumberFormatException nfe) {
                // ignore les entrées invalides
            }
        }
        log.info(String.format("Added %d intervenant(s) on this fiche",nbIntervenants));
    }
    protected void removePhotoForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {

        // remove previous PhotoFiche if any
        PhotoFiche query = new PhotoFiche();
        query.setFiche(ficheDB);
        List<PhotoFiche> previousRelatedToFiche = dbContext.photoFicheDao.queryForMatching(query);
        if(!previousRelatedToFiche.isEmpty()){
            log.info(String.format("delete %d previous PhotoFiche related to this Fiche",previousRelatedToFiche.size()));
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.photoFicheDao.delete(previousRelatedToFiche);
                        return null;
                    });
        }
    }
    protected void updatePhotoForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException, WebSiteNotAvailableException {

        List<Image> imageJSONListe = new ArrayList<Image>();

        // itère sur les images trouvées pour cette fiche
        for (String possibleImageId : especeJSON.getFields().getImages().getValue().split("&")) {
            try {
                int imageId = Integer.parseInt(possibleImageId.split("\\|")[0]);

                // récupère les données associées à l'image
                Image imageJSON = dorisAPI_JSONDATABindingHelper.getImageFromImageId(imageId);

                //TODO : Traiter le cas des Videos un jour
                if (imageJSON != null && imageJSON.getClassIdentifier().equals("image")) {
                    imageJSONListe.add(imageJSON);
                }

            } catch (NumberFormatException | IOException e) {
                log.warn(String.format("Failed retrieving image information for %s", possibleImageId),e);
            }
        }

        // recrée une entrée dans la base pour l'image
        final List<PhotoFiche> listePhotoFiche = jsonToDB.getListePhotosFicheFromJsonImages(imageJSONListe);
        final Fiche especeFinal = ficheDB;
        log.info(String.format("add %d PhotoFiche related to this Fiche",listePhotoFiche.size()));
        TransactionManager.callInTransaction(connectionSource,
                (Callable<Void>) () -> {
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
                });

    }
    protected void removeZoneGeographiqueForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {
        // remove previous PhotoFiche if any
        Fiches_ZonesGeographiques query = new Fiches_ZonesGeographiques();
        query.setFiche(ficheDB);
        List<Fiches_ZonesGeographiques> previousRelatedToFiche = dbContext.fiches_ZonesGeographiquesDao.queryForMatching(query);
        if(!previousRelatedToFiche.isEmpty()){
            log.info(String.format("delete %d previous Fiches_ZonesGeographiques related to this Fiche",previousRelatedToFiche.size()));
            TransactionManager.callInTransaction(connectionSource,
                    (Callable<Void>) () -> {
                        dbContext.fiches_ZonesGeographiquesDao.delete(previousRelatedToFiche);
                        return null;
                    });
        }
    }
    protected void updateZoneGeographiqueForFiche(Fiche ficheDB, Espece especeJSON) throws SQLException {

        ZoneGeographique zoneGeographique ;
        log.info(String.format("add %d Fiches_ZonesGeographiques related to this Fiche",especeJSON.getFields().getZoneGeo().getValue().split("-").length));
        for (String zoneGeoRefId : especeJSON.getFields().getZoneGeo().getValue().split("-")) {

            try {
                zoneGeographique = dbContext.zoneGeographiqueDao.queryForFirst(
                    dbContext.zoneGeographiqueDao.queryBuilder().where().eq("idDoris", Integer.parseInt(zoneGeoRefId)).prepare());
                if(zoneGeographique == null) {
                        log.error(String.format("Zone Géo. Inconnue %s  dans fiche %s/%d" ,
                                zoneGeoRefId,
                                DorisOAuth2ClientCredentials.SPECIES_NODE_URL,
                                ficheDB.getWebNodeId()));
                        log.error(String.format("fiche n° %d de %s %s" ,
                                ficheDB.getNumeroFiche(),
                                especeJSON.getFields().getEspece().getValue(),
                                especeJSON.getFields().getNomCommunFr().getValue()
                                ));

                        System.exit(1);
                }

                if (zoneGeographique.getId() >= 0) {
                    final ZoneGeographique zoneGeographique_final = zoneGeographique;
                    TransactionManager.callInTransaction(connectionSource,
                            (Callable<Void>) () -> {
                                dbContext.fiches_ZonesGeographiquesDao.create(new Fiches_ZonesGeographiques(zoneGeographique_final, ficheDB));
                                return null;
                            });
                }
            } catch (NumberFormatException nfe) {
                // ignore les entrées invalides
            }
        }
    }

    @Override
    Dao<Fiche, Integer> getDao() {
        return dbContext.ficheDao;
    }
}
