package fr.ffessm.doris.prefetch.ezpublish;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.bibliographie.Bibliographie;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.glossaire.Glossaire;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;

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

}
