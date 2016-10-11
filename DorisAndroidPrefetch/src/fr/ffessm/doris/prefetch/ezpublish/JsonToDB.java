package fr.ffessm.doris.prefetch.ezpublish;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
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
    public Fiche getFicheFromJSONTerme(Espece jsonEspece){

        Fiche fiche = new Fiche(
                jsonEspece.getFields().getNomCommunInter().getValue(),
                jsonEspece.getFields().getNomCommunFr().getValue(),
                Integer.parseInt(jsonEspece.getFields().getReference().getValue()),
                1,
                jsonEspece.getFields().getPublicationDate().getValue(),
                jsonEspece.getFields().getChantierDate().getValue(),
                "",
                "",
                ""
        );

        /* Reste : \\java.lang.String numerofichesLiees,
                java.lang.String textePourRechercheRapide,
                java.lang.String pictogrammes
                */
        return fiche;
    }

}
