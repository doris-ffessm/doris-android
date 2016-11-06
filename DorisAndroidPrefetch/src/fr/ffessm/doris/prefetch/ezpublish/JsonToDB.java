package fr.ffessm.doris.prefetch.ezpublish;

import java.util.ArrayList;
import java.util.List;

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
    Fiche
    * * * * * * * * * * * * */
    public  List<SectionFiche> getSectionsFicheFromJSONEspece(Espece jsonEspece){

        List<SectionFiche> sectionsFiche = new ArrayList<SectionFiche>();

        // SectionFiche(int numOrdre, java.lang.String titre, java.lang.String texte)

        // Autres Dénominations
        String autresDenomination = "";
        if (jsonEspece.getFields().getOthersNomCommunFr().getValue() != "") { autresDenomination += jsonEspece.getFields().getOthersNomCommunFr().getValue(); }
        if (jsonEspece.getFields().getNomCommunInter().getValue() != "") { autresDenomination += jsonEspece.getFields().getNomCommunInter().getValue(); }

        if (autresDenomination != "") {sectionsFiche.add(new SectionFiche(100,"Autres dénominations",autresDenomination)); }

        // Groupe Phylogénétique



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



        /*Fiche fiche = new Fiche(
                ficheNodeId.getObjectName(),
                jsonEspece.getFields().getNomCommunFr().getValue(),
                Integer.parseInt(jsonEspece.getFields().getReference().getValue()),
                1,
                jsonEspece.getFields().getPublicationDate().getValue(),
                jsonEspece.getFields().getChantierDate().getValue(),
                "",
                "",
                ""
        );
*/
        /* Reste : \\java.lang.String numerofichesLiees,
                java.lang.String textePourRechercheRapide,
                java.lang.String pictogrammes
                */



        /*
        SectionFiche contenu = new SectionFiche(100+positionSectionDansFiche, dernierTitreSection, texte);
							contenu.setFiche(this);
							_contextDB.sectionFicheDao.create(contenu);

         */
        return sectionsFiche;
    }

    /* * * * * * * * * * * *
    Sections Fiche
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

        /* Fiche(
            java.lang.String nomScientifique,
            java.lang.String nomCommun,
            int numeroFiche,
            int etatFiche,
            java.lang.String dateCreation,
            java.lang.String dateModification,
            java.lang.String numerofichesLiees,
            java.lang.String textePourRechercheRapide,
            java.lang.String pictogrammes)
                */



        /*
        SectionFiche contenu = new SectionFiche(100+positionSectionDansFiche, dernierTitreSection, texte);
							contenu.setFiche(this);
							_contextDB.sectionFicheDao.create(contenu);

         */
        return fiche;
    }
}
