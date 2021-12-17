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
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.SiteDoris;
import fr.ffessm.doris.prefetch.WebSiteNotAvailableException;
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
                                    commonOutils.nettoyageTextes(
                                            jsonImage.getDataMap().getTitle().trim()
                                        ),
                                    commonOutils.nettoyageTextes(
                                            jsonImage.getDataMap().getLegend().trim()
                                        )
                );
        // récupère la liste des postfix pour les résolutions
	    String vignettePostfixCode= "";
        // test de disponibilité d'image vignette
	    if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
			    jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.IMAGE300.getPostFix()))){
		    vignettePostfixCode = Constants.ImagePostFixCode.IMAGE300.getShortCode();
	    } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
			    jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.LARGE.getPostFix()))){
		    vignettePostfixCode = Constants.ImagePostFixCode.LARGE.getShortCode();
	    } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
                jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.SpecieCard.getPostFix()))){
            vignettePostfixCode = Constants.ImagePostFixCode.SpecieCard.getShortCode();
        } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
                jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.ForumCard.getPostFix()))){
            vignettePostfixCode = Constants.ImagePostFixCode.ForumCard.getShortCode();
        } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
			    jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.MEDIUM.getPostFix()))){
		    vignettePostfixCode = Constants.ImagePostFixCode.MEDIUM.getShortCode();
	    } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
			    jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.SMALL.getPostFix()))){
		    vignettePostfixCode = Constants.ImagePostFixCode.SMALL.getShortCode();
	    }

        // test de disponibilité d'image medium
	    String mediumPostfixCode= "";
	    if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
			    jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.IMAGE600.getPostFix()))){
		    mediumPostfixCode = Constants.ImagePostFixCode.IMAGE600.getShortCode();
	    } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
			    jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.IMAGE1000.getPostFix()))){
		    mediumPostfixCode = Constants.ImagePostFixCode.IMAGE1000.getShortCode();
	    } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
                jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.IMAGE1200.getPostFix()))){
            mediumPostfixCode = Constants.ImagePostFixCode.IMAGE1200.getShortCode();
        } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
			    jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.SpecieCard.getPostFix()))){
		    mediumPostfixCode = Constants.ImagePostFixCode.SpecieCard.getShortCode();
	    } else if(SiteDoris.existsFileAtURL(Constants.SITE_RACINE_URL +
			    jsonImage.getDataMap().getImage().replace(Constants.IMAGE_BASE_URL_SUFFIXE, Constants.ImagePostFixCode.ForumCard.getPostFix()))){
		    mediumPostfixCode = Constants.ImagePostFixCode.ForumCard.getShortCode();
	    }

	    photoFiche.setImgPostfixCodes(vignettePostfixCode+"&"+mediumPostfixCode);
        log.debug("getPhotoFicheFromJSONImage - ImgPostfixCodes="+photoFiche.getImgPostfixCodes());
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

        String participantDesignation = jsonUtilisateur.getFields().getFirstName().getValue().trim() + " " + jsonUtilisateur.getFields().getLastName().getValue().trim();

        Participant utilisateur = new Participant(
                        participantDesignation.trim(),
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
    public DefinitionGlossaire getDefinitionGlossaireFromJSONTerme(Glossaire jsonTerme) throws WebSiteNotAvailableException {


        //log.debug("getDefinitionGlossaireFromJSONTerme() - listeImages : "+jsonTerme.getFields().getIllustrations().getValue());
        String listeImages = "";
        for(String possibleImageId : jsonTerme.getFields().getIllustrations().getValue().split("\\&")){
            if (! possibleImageId.isEmpty()) {
                try {
                    int imageId = Integer.parseInt(possibleImageId.split("\\|")[0]);
                    //log.debug("getDefinitionGlossaireFromJSONTerme() - imageId : "+imageId);
                    // récupère les données associées à l'image
                    Image image = dorisAPI_JSONDATABindingHelper.getImageFromImageId(imageId, 5);
                    String imageName = image.getDataMap().getImage();
                    String imageDescription = image.getDataMap().getLegend();
                    if (image != null) listeImages += imageName + "|" + commonOutils.remplacementBalises(
                                commonOutils.nettoyageBalises( imageDescription ).trim()
                            , true)
                        + ";";
                } catch (IOException io) {
                    // ignore les entrées invalides
                }
            }
        }
        //log.debug("getDefinitionGlossaireFromJSONTerme() - listeImages : "+listeImages);

        DefinitionGlossaire terme = new DefinitionGlossaire(
                Integer.parseInt(jsonTerme.getFields().getReference().getValue()),
                commonOutils.nettoyageTextes(
                        jsonTerme.getFields().getTitle().getValue()
                    ).trim(),
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonTerme.getFields().getDefinition().getValue()
                        ).trim()
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
                log.error("Statut fiche non reconnu:  suspicion pb encodage ?");
                break;
        }
        // Espèce Réglementée / Espèce "dangeureuse"
        String pictogrammes = "";
        //TODO : Vérifier fonctionnent et cohérence avec la section créée
        //log.debug("getFicheFromJSONEspece() - reglementation : "+jsonEspece.getFields().getReglementation().getValue());
        if (jsonEspece.getFields().getReglementation().getValue().replaceAll("<[^>]*>","") != "") pictogrammes += "0;";

        //TODO : Trouver un exemple pour vérifier
        //log.debug("getFicheFromJSONEspece() - danger : '"+jsonEspece.getFields().getDanger().getValue()+"'");
        if (jsonEspece.getFields().getDanger().getValue().equals("1")) pictogrammes += "1;";

        //log.debug("getFicheFromJSONEspece() - pictogrammes : "+pictogrammes);

        // Convertions des Dates
        String datePublication = "";
        if (jsonEspece.getFields().getPublicationDate().getValue() != "") datePublication = dateFormat.format(new Date((long) Integer.parseInt(jsonEspece.getFields().getPublicationDate().getValue()) * 1000) );

        String dateChantier = "";
        if (jsonEspece.getFields().getChantierDate().getValue() != "") dateChantier = dateFormat.format(new Date((long) Integer.parseInt(jsonEspece.getFields().getChantierDate().getValue()) * 1000) );


        Fiche fiche = new Fiche(
                "{{i}}"+ficheNodeId.getObjectName()+"{{/i}}"+" "+
                        commonOutils.remplacementBalises( commonOutils.nettoyageBalises( jsonEspece.getFields().getDiscoverer().getValue() ), true ),
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
        if (jsonEspece.getFields().getCleIdentification().getValue() != "") {sectionsFiche.add(new SectionFiche(10,"Critères de reconnaissance",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getCleIdentification().getValue()
                        )
                        , true)
        )); }

        // Distribution
        if (jsonEspece.getFields().getDistributionResume().getValue() != "") {sectionsFiche.add(new SectionFiche(20,"Distribution",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getDistributionResume().getValue()
                        )
                        , true)
        )); }

        // Biotope
        if (jsonEspece.getFields().getBiotop().getValue() != "") {sectionsFiche.add(new SectionFiche(30,"Biotope",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getBiotop().getValue()
                        )
                        , true)
        )); }

        // Description
        if (jsonEspece.getFields().getDescription().getValue() != "") {sectionsFiche.add(new SectionFiche(40,"Description",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getDescription().getValue()
                        )
                        , true)
        )); }

        // Espèces Ressemblantes
        if (jsonEspece.getFields().getLookLikes().getValue() != "") {sectionsFiche.add(new SectionFiche(50,"Espèces Ressemblantes",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getLookLikes().getValue()
                        )
                        , true)
        )); }

        // Alimentation
        if (jsonEspece.getFields().getAlimentation().getValue() != "") {sectionsFiche.add(new SectionFiche(110,"Alimentation",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getAlimentation().getValue()
                        )
                        , true)
        )); }


        // Reproduction - Multiplication
        if (jsonEspece.getFields().getReproduction().getValue() != "") {sectionsFiche.add(new SectionFiche(120,"Reproduction - Multiplication",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getReproduction().getValue()
                        )
                        , true)
        )); }


        // Vie associée
        if (jsonEspece.getFields().getAssociatedLife().getValue() != "") {sectionsFiche.add(new SectionFiche(130,"Vie associée",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getAssociatedLife().getValue()
                        )
                        , true)
        )); }


        // Divers biologie
        if (jsonEspece.getFields().getBioDivers().getValue() != "") {sectionsFiche.add(new SectionFiche(140,"Divers biologie",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getBioDivers().getValue()
                        )
                        , true)
        )); }

        // Informations Complémentaires
        if (jsonEspece.getFields().getComplementaryInfos().getValue() != "") {sectionsFiche.add(new SectionFiche(150,"Informations complémentaires",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getComplementaryInfos().getValue()
                        )
                        , true)
        )); }

        // Réglementation
        if (jsonEspece.getFields().getReglementation().getValue() != "") {sectionsFiche.add(new SectionFiche(160,"Réglementation",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getReglementation().getValue()
                        )
                        , true)
        )); }

        // Origine du Nom Français
        if (jsonEspece.getFields().getFrenchNameOrigin().getValue() != "") {sectionsFiche.add(new SectionFiche(210,"Origine du nom français",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getFrenchNameOrigin().getValue()
                        )
                        , true)
        )); }

        // Origine du Nom Scientifique
        if (jsonEspece.getFields().getScientificNameOrigin().getValue() != "") {sectionsFiche.add(new SectionFiche(220,"Origine du nom scientifique",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getScientificNameOrigin().getValue()
                        )
                        , true)
        )); }

        // Autres noms scientifiques parfois utilisés, mais non valides
        if (jsonEspece.getFields().getOthersNameScientific().getValue() != "") {sectionsFiche.add(new SectionFiche(230,"Autres noms scientifiques parfois utilisés, mais non valides",
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonEspece.getFields().getOthersNameScientific().getValue()
                        )
                        , true)
        )); }


        // Zone Doris
        // Lors de l'affichage des fiches, on ajoutera à "l'emplacement" 310 la ZOne Doris

        // Crédits
        // Idem en 320


        // Références Bibliographiques
        //if (jsonEspece.getFields().getBiblioRef().getValue() != "") {sectionsFiche.add(new SectionFiche(350,"Codes des Références bibliographiques",jsonEspece.getFields().getBiblioRef().getValue())); }
        if (jsonEspece.getFields().getOthersBiblioRef().getValue() != "") {sectionsFiche.add(new SectionFiche(410,"Références bibliographiques",
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

        //log.debug("getClassificationFicheFromJSONEspece - getEmbranchementTaxon() : " + jsonEspece.getFields().getEmbranchementTaxon().getValue());
        if (jsonEspece.getFields().getEmbranchementTaxon().getValue() != null) {
                classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getEmbranchementTaxon().getValue().toString()), "{{g}}Embranchement{{/g}}", "", "", ""), 2)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSousEmbranchementTaxon() : " + jsonEspece.getFields().getSousEmbranchementTaxon().getValue());
        if (jsonEspece.getFields().getSousEmbranchementTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousEmbranchementTaxon().getValue().toString()), "{{g}}Sous-embranchement{{/g}}", "", "", ""), 3)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSuperClasseTaxon() : " + jsonEspece.getFields().getSuperClasseTaxon().getValue());
        if (jsonEspece.getFields().getSuperClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSuperClasseTaxon().getValue().toString()), "{{g}}Super-classe{{/g}}", "", "", ""), 4)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getClasseTaxon() : " + jsonEspece.getFields().getClasseTaxon().getValue());
        if (jsonEspece.getFields().getClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getClasseTaxon().getValue().toString()), "{{g}}Classe{{/g}}", "", "", ""), 5)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSousClasseTaxon() : " + jsonEspece.getFields().getSousClasseTaxon().getValue());
        if (jsonEspece.getFields().getSousClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousClasseTaxon().getValue().toString()), "{{g}}Sous-classe{{/g}}", "", "", ""), 6)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSuperOrdreTaxon() : " + jsonEspece.getFields().getSuperOrdreTaxon().getValue());
        if (jsonEspece.getFields().getSuperOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSuperOrdreTaxon().getValue().toString()), "{{g}}Super-ordre{{/g}}", "", "", ""), 7)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getOrdreTaxon() : " + jsonEspece.getFields().getOrdreTaxon().getValue());
        if (jsonEspece.getFields().getOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getOrdreTaxon().getValue().toString()), "{{g}}Ordre{{/g}}", "", "", ""), 8)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSousOrdreTaxon() : " + jsonEspece.getFields().getSousOrdreTaxon().getValue());
        if (jsonEspece.getFields().getSousOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousOrdreTaxon().getValue().toString()), "{{g}}Sous-ordre{{/g}}", "", "", ""), 9)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getFamilleTaxon() : " + jsonEspece.getFields().getFamilleTaxon().getValue());
        if (jsonEspece.getFields().getFamilleTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getFamilleTaxon().getValue().toString()), "{{g}}Famille{{/g}}", "", "", ""), 10)
            );
        }

        //log.debug("getClassificationFicheFromJSONEspece - getSousFamilleTaxon() : " + jsonEspece.getFields().getSousFamilleTaxon().getValue());
        if (jsonEspece.getFields().getSousFamilleTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousFamilleTaxon().getValue().toString()), "{{g}}Sous-Famille{{/g}}", "", "", ""), 11)
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

        Classification classification = new Classification(
                jsonObjectId,
                classificationNiveau,
                jsonClassification.getDataMap().getNameLatin(),
                jsonClassification.getDataMap().getNameFrench(),
                commonOutils.remplacementBalises(
                        commonOutils.nettoyageBalises(
                                jsonClassification.getDataMap().getDescription()
                        )
                        , true)
        );
        return classification;
    }

}
