package fr.ffessm.doris.prefetch.ezpublish;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.android.sitedoris.Common_Outils;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.bibliographie.Bibliographie;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.glossaire.Glossaire;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.utilisateur.Utilisateur;


public class JsonToDB {

	public static String JSON_IMAGE_PREFIX = "var/doris/storage/images/images/";

    public static Log log = LogFactory.getLog(JsonToDB.class);

    private static Common_Outils commonOutils = new Common_Outils();
    DorisAPI_JSONDATABindingHelper dorisAPI_JSONDATABindingHelper = new DorisAPI_JSONDATABindingHelper();

    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    /* * * * * * * * * * * *
        Photos des Espèces
     * * * * * * * * * * * * */
    public PhotoFiche getPhotoFicheFromJSONImage(Image jsonImage){
        PhotoFiche photoFiche = new PhotoFiche(
                                    jsonImage.getDataMap().getImage(),
                                    commonOutils.remplacementBalises(
                                            commonOutils.nettoyageBalises(
                                                    jsonImage.getDataMap().getTitle()
                                            )
                                            , true),
                                    commonOutils.remplacementBalises(
                                        commonOutils.nettoyageBalises(
                                                jsonImage.getDataMap().getLegend()
                                        )
                                        , true)
                );
        return photoFiche;
    }

    public List<PhotoFiche> getListePhotosFicheFromJsonImages(List<Image> jsonImagesJSON) {
		
		List<PhotoFiche> listePhotosFiche = new ArrayList<PhotoFiche>(0);
		for (Image jsonImage : jsonImagesJSON) {
			listePhotosFiche.add(getPhotoFicheFromJSONImage(jsonImage));
		}
		
		return listePhotosFiche;
	}


    /* * * * * * * * * * * *
    Participants
    * * * * * * * * * * * * */
    public Participant getParticipantFromJSONUtil(int intervenantNodeId, Utilisateur jsonUtilisateur){
        //log.debug("getParticipantFromJSONUtil - Début");
        //log.debug("getParticipantFromJSONUtil - intervenantNodeId : " + intervenantNodeId);
        //log.debug("getParticipantFromJSONUtil - getReference() : " + jsonUtilisateur.getFields().getReference().getValue());
        //log.debug("getParticipantFromJSONUtil - getFirstName() : " + jsonUtilisateur.getFields().getFirstName().getValue());
        //log.debug("getParticipantFromJSONUtil - getDescription() : " + jsonUtilisateur.getFields().getDescription().getValue());
        //log.debug("getParticipantFromJSONUtil - getDescription(). nettoyée : " + commonOutils.remplacementBalises(jsonUtilisateur.getFields().getDescription().getValue(), true));

        Participant utilisateur = new Participant(
                        jsonUtilisateur.getFields().getFirstName().getValue() + " " + jsonUtilisateur.getFields().getLastName().getValue(),
                        intervenantNodeId,
                        jsonUtilisateur.getFields().getImage().getValue(),
                        jsonUtilisateur.getFields().getCorrectionMember().getValue(),
                        commonOutils.remplacementBalises(
                                commonOutils.nettoyageBalises(
                                    jsonUtilisateur.getFields().getDescription().getValue()
                                )
                            , true)
        );

        return utilisateur;
    }



    /* * * * * * * * * * * *
    Glossaire
    * * * * * * * * * * * * */
    public DefinitionGlossaire getDefinitionGlossaireFromJSONTerme(Glossaire jsonTerme){


        //log.debug("getDefinitionGlossaireFromJSONTerme() - listeImages : "+jsonTerme.getFields().getIllustrations().getValue());
        String listeImages = "";
        for(String possibleImageId : jsonTerme.getFields().getIllustrations().getValue().split("\\&")){
            if (! possibleImageId.isEmpty()) {
                try {
                    int imageId = Integer.parseInt(possibleImageId.split("\\|")[0]);
                    //log.debug("getDefinitionGlossaireFromJSONTerme() - imageId : "+imageId);
                    // récupère les données associées à l'image
                    listeImages += dorisAPI_JSONDATABindingHelper.getImageFromImageId(imageId).getDataMap().getImage() + ";";


                } catch (IOException io) {
                    // ignore les entrées invalides
                }
            }
        }
        //log.debug("getDefinitionGlossaireFromJSONTerme() - listeImages : "+listeImages);

        DefinitionGlossaire terme = new DefinitionGlossaire(
                Integer.parseInt(jsonTerme.getFields().getReference().getValue()),
                jsonTerme.getFields().getTitle().getValue(),
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonTerme.getFields().getDefinition().getValue()
                        )
                    , true),
                listeImages
                );
        return terme;
    }


    /* * * * * * * * * * * *
    Bibliographie
    * * * * * * * * * * * * */
    public EntreeBibliographie getEntreeBibliographieFromJSONTerme(Bibliographie jsonOeuvre){
        // TODO : Texte Pour recherche non renseigné
        // TODO : certains champs de la V4 non exploités
        EntreeBibliographie oeuvre = new EntreeBibliographie(
                Integer.parseInt(jsonOeuvre.getFields().getReference().getValue()),
                jsonOeuvre.getFields().getTitle().getValue(),
                jsonOeuvre.getFields().getMainAuthor().getValue()+','+jsonOeuvre.getFields().getExtraAuthors().getValue(),
                jsonOeuvre.getFields().getPublicationYear().getValue(),
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonOeuvre.getFields().getExtraInfo().getValue()
                        )
                        , true),
                jsonOeuvre.getFields().getCover().getValue(),
                (commonOutils.formatStringNormalizer(
                        jsonOeuvre.getFields().getMainAuthor().getValue()+','+jsonOeuvre.getFields().getExtraAuthors().getValue()
                                + " "
                                + jsonOeuvre.getFields().getTitle().getValue())
                ).toLowerCase(Locale.FRENCH)
        );
        return oeuvre;
    }


    /* * * * * * * * * * * *
    Fiches
    * * * * * * * * * * * * */
    public Fiche getFicheFromJSONEspece(ObjNameNodeId ficheNodeId, Espece jsonEspece){

        // Statut de la Fiche
        // Proposée ; Réservée ; Terminée ; Publiée ; A corriger
        // On convertit à la sauce de la version précédente :
        //4 : Fiche Publiée - 1, 2, 3 : En cours de Rédaction - 5 : Fiche Proposée
        log.debug("getFicheFromJSONEspece() - statut : "+jsonEspece.getFields().getState().getValue());
        int etatFiche;
        switch (jsonEspece.getFields().getState().getValue()) {
            case "Proposée" :  etatFiche = 5;
                break;
            case "Réservée" : etatFiche = 1;
                break;
            case "A corriger" : etatFiche = 2;
                break;
            case "Terminée" : etatFiche = 3;
                break;
            case "Publiée" : etatFiche = 4;
                break;
            default: etatFiche = 5;
                break;
        }
        // Espèce Réglementée / Espèce "dangeureuse"
        String pictogrammes = "";
        //TODO : Vérifier fonctionnent et cohérence avec la section créée
        //log.debug("getFicheFromJSONEspece() - reglementation : "+jsonEspece.getFields().getReglementation().getValue());
        if (jsonEspece.getFields().getReglementation().getValue().replaceAll("<[^>]*>","") != "") pictogrammes += "0;";

        //TODO : Trouver un exemple pour vérifier
        //log.debug("getFicheFromJSONEspece() - danger : "+jsonEspece.getFields().getDanger().getValue());
        if (jsonEspece.getFields().getDanger().getValue() == "1") pictogrammes += "1;";

        //log.debug("getFicheFromJSONEspece() - pictogrammes : "+pictogrammes);

        // Convertions des Dates
        String datePublication = "";
        if (jsonEspece.getFields().getPublicationDate().getValue() != "") datePublication = dateFormat.format(new Date((long) Integer.parseInt(jsonEspece.getFields().getPublicationDate().getValue()) * 1000) );

        String dateChantier = "";
        if (jsonEspece.getFields().getChantierDate().getValue() != "") dateChantier = dateFormat.format(new Date((long) Integer.parseInt(jsonEspece.getFields().getChantierDate().getValue()) * 1000) );


        Fiche fiche = new Fiche(
                "{{i}}"+ficheNodeId.getObjectName()+"{{/i}}"+" "+jsonEspece.getFields().getDiscoverer().getValue(),
                jsonEspece.getFields().getNomCommunFr().getValue(),
                Integer.parseInt(jsonEspece.getFields().getReference().getValue()),
                etatFiche,
                datePublication,
                dateChantier,
                "",
                "",
                pictogrammes
        );

        return fiche;
    }


    /* * * * * * * * * * * *
        Sections Fiche
    * * * * * * * * * * * * */
    public  List<SectionFiche> getSectionsFicheFromJSONEspece(Espece jsonEspece){

        List<SectionFiche> sectionsFiche = new ArrayList<SectionFiche>();

        // Critères de reconnaissance
        if (jsonEspece.getFields().getCleIdentification().getValue() != "") {sectionsFiche.add(new SectionFiche(110,"Critères de reconnaissance",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getCleIdentification().getValue()
                        )
                        , true)
        )); }

        // Distribution


        // Biotope
        if (jsonEspece.getFields().getBiotop().getValue() != "") {sectionsFiche.add(new SectionFiche(120,"Biotope",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getBiotop().getValue()
                        )
                        , true)
        )); }

        // Description
        if (jsonEspece.getFields().getDescription().getValue() != "") {sectionsFiche.add(new SectionFiche(130,"Description",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getDescription().getValue()
                        )
                        , true)
        )); }

        // Espèces Ressemblantes
        if (jsonEspece.getFields().getLookLikes().getValue() != "") {sectionsFiche.add(new SectionFiche(130,"Description",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getLookLikes().getValue()
                        )
                        , true)
        )); }

        // Autres noms scientifiques parfois utilisés, mais non valides
        if (jsonEspece.getFields().getOthersNameScientific().getValue() != "") {sectionsFiche.add(new SectionFiche(130,"Autres noms scientifiques parfois utilisés, mais non valides",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getOthersNameScientific().getValue()
                        )
                        , true)
        )); }
        // Zone Doris

        // Crédits
        if (jsonEspece.getFields().getDescription().getValue() != "") {sectionsFiche.add(new SectionFiche(190,"Crédits",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getDescription().getValue()
                        )
                        , true)
        )); }

        // Origine du Nom Français
        if (jsonEspece.getFields().getFrenchNameOrigin().getValue() != "") {sectionsFiche.add(new SectionFiche(300,"Origine du nom français",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getFrenchNameOrigin().getValue()
                        )
                        , true)
        )); }

        // Origine du Nom Scientifique
        if (jsonEspece.getFields().getScientificNameOrigin().getValue() != "") {sectionsFiche.add(new SectionFiche(310,"Origine du nom scientifique",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getScientificNameOrigin().getValue()
                        )
                        , true)
        )); }

        // Alimentation
        if (jsonEspece.getFields().getAlimentation().getValue() != "") {sectionsFiche.add(new SectionFiche(320,"Alimentation",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getAlimentation().getValue()
                        )
                        , true)
        )); }


        // Reproduction - Multiplication
        if (jsonEspece.getFields().getReproduction().getValue() != "") {sectionsFiche.add(new SectionFiche(330,"Reproduction - Multiplication",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getReproduction().getValue()
                        )
                        , true)
        )); }


        // Informations Complémentaires
        if (jsonEspece.getFields().getComplementaryInfos().getValue() != "") {sectionsFiche.add(new SectionFiche(340,"Informations complémentaires",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getComplementaryInfos().getValue()
                        )
                        , true)
        )); }

        // Réglementation
        if (jsonEspece.getFields().getReglementation().getValue() != "") {sectionsFiche.add(new SectionFiche(350,"Réglementation",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getReglementation().getValue()
                        )
                        , true)
        )); }

        // Références Bibliographiques
        //if (jsonEspece.getFields().getBiblioRef().getValue() != "") {sectionsFiche.add(new SectionFiche(350,"Codes des Références bibliographiques",jsonEspece.getFields().getBiblioRef().getValue())); }
        if (jsonEspece.getFields().getOthersBiblioRef().getValue() != "") {sectionsFiche.add(new SectionFiche(360,"Références bibliographiques",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getOthersBiblioRef().getValue()
                        )
                        , true)
        )); }



        return sectionsFiche;
    }


    /* * * * * * * * * * * *
       Autres dénominations Fiche
    * * * * * * * * * * * * */
    public  List<AutreDenomination> getAutresDenominationFicheFromJSONEspece(Espece jsonEspece){

        List<AutreDenomination> autresDenominations = new ArrayList<AutreDenomination>();

        // SectionFiche(int numOrdre, java.lang.String titre, java.lang.String texte)

        if (jsonEspece.getFields().getOthersNomCommunFr().getValue() != "") {
            autresDenominations.add(new AutreDenomination(
                    commonOutils.remplacementBalises(
                            commonOutils.nettoyageBalises(
                                    jsonEspece.getFields().getOthersNomCommunFr().getValue()
                            )
                            , true),
                    "FR"));
        }
        if (jsonEspece.getFields().getNomCommunInter().getValue() != "") {
            autresDenominations.add(new AutreDenomination(
                    commonOutils.remplacementBalises(
                            commonOutils.nettoyageBalises(
                                    jsonEspece.getFields().getNomCommunInter().getValue()
                            )
                            , true),
                    ""));
        }

        return autresDenominations;
    }


    /* * * * * * * * * * * *
        Classification Fiche
    * * * * * * * * * * * * */
    public  List<ClassificationFiche> getClassificationFicheFromJSONEspece(Espece jsonEspece){
        //log.debug("getClassificationFicheFromJSONEspece - Début");

        Fiche fiche = new Fiche();
        List<ClassificationFiche> classificationFiche = new ArrayList<ClassificationFiche>();

        //log.debug("getClassificationFicheFromJSONEspece - getGroup().getValue() : " + jsonEspece.getFields().getGroup().getValue());
        if (jsonEspece.getFields().getGroup().getValue() != null) {
            classificationFiche.add(
                new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getGroup().getValue()), "group", "", "", ""), 1)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getEmbranchementTaxon() : " + jsonEspece.getFields().getEmbranchementTaxon().getValue());
        if (jsonEspece.getFields().getEmbranchementTaxon().getValue() != null) {
                classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getEmbranchementTaxon().getValue().toString()), "embranchement_taxon", "", "", ""), 2)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSousEmbranchementTaxon() : " + jsonEspece.getFields().getSousEmbranchementTaxon().getValue());
        if (jsonEspece.getFields().getSousEmbranchementTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousEmbranchementTaxon().getValue().toString()), "sous_embranchement_taxon", "", "", ""), 3)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSuperClasseTaxon() : " + jsonEspece.getFields().getSuperClasseTaxon().getValue());
        if (jsonEspece.getFields().getSuperClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSuperClasseTaxon().getValue().toString()), "super_classe_taxon", "", "", ""), 4)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getClasseTaxon() : " + jsonEspece.getFields().getClasseTaxon().getValue());
        if (jsonEspece.getFields().getClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getClasseTaxon().getValue().toString()), "classe_taxon", "", "", ""), 5)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSousClasseTaxon() : " + jsonEspece.getFields().getSousClasseTaxon().getValue());
        if (jsonEspece.getFields().getSousClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousClasseTaxon().getValue().toString()), "sous_classe_taxon", "", "", ""), 6)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSuperOrdreTaxon() : " + jsonEspece.getFields().getSuperOrdreTaxon().getValue());
        if (jsonEspece.getFields().getSuperOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSuperOrdreTaxon().getValue().toString()), "super_ordre_taxon", "", "", ""), 7)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getOrdreTaxon() : " + jsonEspece.getFields().getOrdreTaxon().getValue());
        if (jsonEspece.getFields().getOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getOrdreTaxon().getValue().toString()), "ordre_taxon", "", "", ""), 8)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSousOrdreTaxon() : " + jsonEspece.getFields().getSousOrdreTaxon().getValue());
        if (jsonEspece.getFields().getSousOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousOrdreTaxon().getValue().toString()), "sous_ordre_taxon", "", "", ""), 9)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getFamilleTaxon() : " + jsonEspece.getFields().getFamilleTaxon().getValue());
        if (jsonEspece.getFields().getFamilleTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getFamilleTaxon().getValue().toString()), "famille_taxon", "", "", ""), 10)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSousFamilleTaxon() : " + jsonEspece.getFields().getSousFamilleTaxon().getValue());
        if (jsonEspece.getFields().getSousFamilleTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousFamilleTaxon().getValue().toString()), "sous_famille_taxon", "", "", ""), 11)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - classificationFiche().getNumeroDoris() : " + classificationFiche.get(0).getClassification().getNumeroDoris());
        //log.debug("getClassificationFicheFromJSONEspece - classificationFiche().getTermeFrancais() : " + classificationFiche.get(0).getClassification().getTermeFrancais());
        return classificationFiche;
    }


    /* * * * * * * * * * * *
    Classification
    * * * * * * * * * * * * */
    public Classification getClassificationFromJSONClassification(int jsonObjectId, String classificationNiveau, fr.ffessm.doris.prefetch.ezpublish.jsondata.classification.Classification jsonClassification){
        Classification classification = new Classification (
                jsonObjectId,
                classificationNiveau,
                jsonClassification.getDataMap().getNameFrench(),
                jsonClassification.getDataMap().getNameLatin(),
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonClassification.getDataMap().getDescription()
                        )
                        , true)
        );
        return classification;
    }


}
