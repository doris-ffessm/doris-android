package fr.ffessm.doris.prefetch.ezpublish;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.datamodel.AutreDenomination;
import fr.ffessm.doris.android.datamodel.Classification;
import fr.ffessm.doris.android.datamodel.ClassificationFiche;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.android.datamodel.SectionFiche;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.bibliographie.Bibliographie;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.espece.Espece;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.glossaire.Glossaire;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.utilisateur.Utilisateur;


public class JsonToDB {

	public static String JSON_IMAGE_PREFIX = "var/doris/storage/images/images/";

    public static Log log = LogFactory.getLog(JsonToDB.class);
    /* * * * * * * * * * * *
        Photos des Espèces
     * * * * * * * * * * * * */
    public PhotoFiche getPhotoFicheFromJSONImage(Image jsonImage){
        PhotoFiche photoFiche = new PhotoFiche(jsonImage.getDataMap().getImage(),jsonImage.getDataMap().getTitle(), jsonImage.getDataMap().getLegend());
        return photoFiche;
    }

    public List<PhotoFiche> getListePhotosFicheFromJsonImages(List<Image> jsonImages) {
		
		List<PhotoFiche> listePhotosFiche = new ArrayList<PhotoFiche>(0);
		for (Image jsonImage : jsonImages) {
			listePhotosFiche.add(getPhotoFicheFromJSONImage(jsonImage));
		}
		
		return listePhotosFiche;
	}


    /* * * * * * * * * * * *
    Participants
    * * * * * * * * * * * * */
    public Participant getParticipantFromJSONTerme(Utilisateur jsonUtilisateur){
        Participant utilisateur = new Participant(
                        jsonUtilisateur.getFields().getFirstName().getValue() + " " + jsonUtilisateur.getFields().getLastName().getValue(),
                        Integer.parseInt(jsonUtilisateur.getFields().getReference().getValue()),
                        jsonUtilisateur.getFields().getImage().getValue(),
                        jsonUtilisateur.getFields().getCorrectionMember().getValue(),
                        jsonUtilisateur.getFields().getDescription().getValue()
        );
        return utilisateur;
    }


    /* * * * * * * * * * * *
    Glossaire
    * * * * * * * * * * * * */
    public DefinitionGlossaire getDefinitionGlossaireFromJSONTerme(Glossaire jsonTerme){
        DefinitionGlossaire terme = new DefinitionGlossaire(
                Integer.parseInt(jsonTerme.getFields().getReference().getValue()),
                jsonTerme.getFields().getTitle().getValue(),
                jsonTerme.getFields().getDefinition().getValue(),
                jsonTerme.getFields().getIllustrations().getValue()
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
                jsonOeuvre.getFields().getExtraInfo().getValue(),
                jsonOeuvre.getFields().getCover().getValue(),
                ""
        );
        return oeuvre;
    }


    /* * * * * * * * * * * *
    Fiches
    * * * * * * * * * * * * */
    public Fiche getFicheFromJSONEspece(ObjNameNodeId ficheNodeId, Espece jsonEspece){

        //
        String pictogrammes = "";
        if (jsonEspece.getFields().getReglementation().getValue() == "1") pictogrammes += "0;";
        if (jsonEspece.getFields().getDanger().getValue() == "1") pictogrammes += "1;";

        Fiche fiche = new Fiche(
                "{{i}}"+ficheNodeId.getObjectName()+"{{/i}}"+" "+jsonEspece.getFields().getDiscoverer().getValue(),
                jsonEspece.getFields().getNomCommunFr().getValue(),
                Integer.parseInt(jsonEspece.getFields().getReference().getValue()),
                1,
                jsonEspece.getFields().getPublicationDate().getValue(),
                jsonEspece.getFields().getChantierDate().getValue(),
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
        if (jsonEspece.getFields().getCleIdentification().getValue() != "") {sectionsFiche.add(new SectionFiche(110,"Critères de reconnaissance",jsonEspece.getFields().getCleIdentification().getValue())); }

        // Distribution


        // Biotope
        if (jsonEspece.getFields().getBiotop().getValue() != "") {sectionsFiche.add(new SectionFiche(120,"Biotope",jsonEspece.getFields().getBiotop().getValue())); }

        // Description
        if (jsonEspece.getFields().getDescription().getValue() != "") {sectionsFiche.add(new SectionFiche(130,"Description",jsonEspece.getFields().getDescription().getValue())); }

        // Espèces Ressemblantes
        if (jsonEspece.getFields().getLookLikes().getValue() != "") {sectionsFiche.add(new SectionFiche(130,"Description",jsonEspece.getFields().getLookLikes().getValue())); }

        // Autres noms scientifiques parfois utilisés, mais non valides
        if (jsonEspece.getFields().getOthersNameScientific().getValue() != "") {sectionsFiche.add(new SectionFiche(130,"Autres noms scientifiques parfois utilisés, mais non valides",jsonEspece.getFields().getOthersNameScientific().getValue())); }
        // Zone Doris

        // Crédits
        if (jsonEspece.getFields().getDescription().getValue() != "") {sectionsFiche.add(new SectionFiche(190,"Crédits",jsonEspece.getFields().getDescription().getValue())); }

        // Origine du Nom Français
        if (jsonEspece.getFields().getFrenchNameOrigin().getValue() != "") {sectionsFiche.add(new SectionFiche(300,"Origine du nom français",jsonEspece.getFields().getFrenchNameOrigin().getValue())); }

        // Origine du Nom Scientifique
        if (jsonEspece.getFields().getScientificNameOrigin().getValue() != "") {sectionsFiche.add(new SectionFiche(310,"Origine du nom scientifique",jsonEspece.getFields().getScientificNameOrigin().getValue())); }

        // Alimentation
        if (jsonEspece.getFields().getAlimentation().getValue() != "") {sectionsFiche.add(new SectionFiche(320,"Alimentation",jsonEspece.getFields().getAlimentation().getValue())); }


        // Reproduction - Multiplication
        if (jsonEspece.getFields().getReproduction().getValue() != "") {sectionsFiche.add(new SectionFiche(330,"Reproduction - Multiplication",jsonEspece.getFields().getReproduction().getValue())); }


        // Informations Complémentaires
        if (jsonEspece.getFields().getComplementaryInfos().getValue() != "") {sectionsFiche.add(new SectionFiche(340,"Informations complémentaires",jsonEspece.getFields().getComplementaryInfos().getValue())); }

        // Références Bibliographiques
        //if (jsonEspece.getFields().getBiblioRef().getValue() != "") {sectionsFiche.add(new SectionFiche(350,"Codes des Références bibliographiques",jsonEspece.getFields().getBiblioRef().getValue())); }
        if (jsonEspece.getFields().getOthersBiblioRef().getValue() != "") {sectionsFiche.add(new SectionFiche(350,"Références bibliographiques",jsonEspece.getFields().getOthersBiblioRef().getValue())); }



        return sectionsFiche;
    }


    /* * * * * * * * * * * *
       Autres dénominations Fiche
    * * * * * * * * * * * * */
    public  List<AutreDenomination> getAutresDenominationFicheFromJSONEspece(Espece jsonEspece){

        List<AutreDenomination> autresDenominations = new ArrayList<AutreDenomination>();

        // SectionFiche(int numOrdre, java.lang.String titre, java.lang.String texte)

        if (jsonEspece.getFields().getOthersNomCommunFr().getValue() != "") { autresDenominations.add(new AutreDenomination(jsonEspece.getFields().getOthersNomCommunFr().getValue(), "FR")); }
        if (jsonEspece.getFields().getNomCommunInter().getValue() != "") { autresDenominations.add(new AutreDenomination(jsonEspece.getFields().getNomCommunInter().getValue(), "")); }

        return autresDenominations;
    }


    /* * * * * * * * * * * *
        Classification Fiche
    * * * * * * * * * * * * */
    public  List<ClassificationFiche> getClassificationFicheFromJSONEspece(Espece jsonEspece){
        log.debug("getClassificationFicheFromJSONEspece - Début");

        Fiche fiche = new Fiche();
        List<ClassificationFiche> classificationFiche = new ArrayList<ClassificationFiche>();

        log.debug("getClassificationFicheFromJSONEspece - getGroup().getValue() : " + jsonEspece.getFields().getGroup().getValue());
        if (jsonEspece.getFields().getGroup().getValue() != null) {
            classificationFiche.add(
                new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getGroup().getValue()), "group", "", "", ""), 1)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getEmbranchementTaxon() : " + jsonEspece.getFields().getEmbranchementTaxon().getValue());
        if (jsonEspece.getFields().getEmbranchementTaxon().getValue() != null) {
                classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getEmbranchementTaxon().getValue().toString()), "embranchement_taxon", "", "", ""), 2)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getSousEmbranchementTaxon() : " + jsonEspece.getFields().getSousEmbranchementTaxon().getValue());
        if (jsonEspece.getFields().getSousEmbranchementTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousEmbranchementTaxon().getValue().toString()), "sous_embranchement_taxon", "", "", ""), 3)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getSuperClasseTaxon() : " + jsonEspece.getFields().getSuperClasseTaxon().getValue());
        if (jsonEspece.getFields().getSuperClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSuperClasseTaxon().getValue().toString()), "super_classe_taxon", "", "", ""), 4)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getClasseTaxon() : " + jsonEspece.getFields().getClasseTaxon().getValue());
        if (jsonEspece.getFields().getClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getClasseTaxon().getValue().toString()), "classe_taxon", "", "", ""), 5)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getSousClasseTaxon() : " + jsonEspece.getFields().getSousClasseTaxon().getValue());
        if (jsonEspece.getFields().getSousClasseTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousClasseTaxon().getValue().toString()), "sous_classe_taxon", "", "", ""), 6)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getSuperOrdreTaxon() : " + jsonEspece.getFields().getSuperOrdreTaxon().getValue());
        if (jsonEspece.getFields().getSuperOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSuperOrdreTaxon().getValue().toString()), "super_ordre_taxon", "", "", ""), 7)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getOrdreTaxon() : " + jsonEspece.getFields().getOrdreTaxon().getValue());
        if (jsonEspece.getFields().getOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getOrdreTaxon().getValue().toString()), "ordre_taxon", "", "", ""), 8)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getSousOrdreTaxon() : " + jsonEspece.getFields().getSousOrdreTaxon().getValue());
        if (jsonEspece.getFields().getSousOrdreTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousOrdreTaxon().getValue().toString()), "sous_ordre_taxon", "", "", ""), 9)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getFamilleTaxon() : " + jsonEspece.getFields().getFamilleTaxon().getValue());
        if (jsonEspece.getFields().getFamilleTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getFamilleTaxon().getValue().toString()), "famille_taxon", "", "", ""), 10)
            );
        }

        log.debug("getClassificationFicheFromJSONEspece - getSousFamilleTaxon() : " + jsonEspece.getFields().getSousFamilleTaxon().getValue());
        if (jsonEspece.getFields().getSousFamilleTaxon().getValue() != null) {
            classificationFiche.add(
                    new ClassificationFiche(new Classification(Integer.parseInt(jsonEspece.getFields().getSousFamilleTaxon().getValue().toString()), "sous_famille_taxon", "", "", ""), 11)
            );
        }

/*
                 group":{"type":"ezobjectrelation","identifier":"group","value":"48883","id":947887,"classattribute_id":479}," +
                ""embranchement_taxon":{"type":"ezobjectrelation","identifier":"embranchement_taxon","value":"1081"," ""id":947888,"classattribute_id":480},
                "sous_embranchement_taxon":{"type":"ezobjectrelation","identifier":"sous_embranchement_taxon","""value":null,"id":947889,"classattribute_id":481}," +
                ""super_classe_taxon":{"type":"ezobjectrelation","identifier":"super_classe_taxon","value":null,"id":947890,"classattribute_id":482}," +
                ""classe_taxon":{"type":"ezobjectrelation","identifier":"classe_taxon","value":"1177","id":947891,"classattribute_id":483}," +
                ""sous_classe_taxon":{"type":"ezobjectrelation","identifier":"sous_classe_taxon","value":"1225","id":947892,"classattribute_id":484},
                "super_ordre_taxon":{"type":"ezobjectrelation","identifier":"super_ordre_taxon","value":null,"id":947893,"classattribute_id":485}," +
                ""ordre_taxon":{"type":"ezobjectrelation","identifier":"ordre_taxon","value":"1226","id":947894,"classattribute_id":486},
                "sous_ordre_taxon":{"type":"ezobjectrelation","identifier":"sous_ordre_taxon","value":"1561","id":947895,"classattribute_id":487}," +
                ""famille_taxon":{"type":"ezobjectrelation","identifier":"famille_taxon","value":"1563","id":947896,"classattribute_id":488}," +
                ""sous_famille_taxon":{"type":"ezobjectrelation","identifier":"sous_famille_taxon","value":null,"id":947897,"classattribute_id":489}," +
                ""genre":{"type":"ezstring","identifier":"genre","value":"Eunicella","id":947899,"classattribute_id":546},"espece":{"type":"ezstring"," +
                ""identifier":"espece","value":"verrucosa","id":947900,"classattribute_id":538}
*/

        log.debug("getClassificationFicheFromJSONEspece - classificationFiche().getId() : " + classificationFiche.get(0).getClassification().getId());
        log.debug("getClassificationFicheFromJSONEspece - classificationFiche().getTermeFrancais() : " + classificationFiche.get(0).getClassification().getTermeFrancais());
        return classificationFiche;
    }


    /* * * * * * * * * * * *
    Classification
    * * * * * * * * * * * * */
    public Classification getClassificationFromJSONClassification(fr.ffessm.doris.prefetch.ezpublish.jsondata.classification.Classification jsonClassification){
        Classification classification = new Classification (
                jsonClassification.getClassIdentifier(),
                jsonClassification.getDataMap().getNameFrench(),
                jsonClassification.getDataMap().getNameLatin(),
                jsonClassification.getDataMap().getDescription()
        );
        return classification;
    }


}
