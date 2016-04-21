package fr.ffessm.doris.prefetch.ezpublish;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.datamodel.PhotoFiche;
import fr.ffessm.doris.prefetch.ezpublish.jsondata.image.Image;

public class JsonToDB {

	public static String JSON_IMAGE_PREFIX = "var/doris/storage/images/images/";
	
	public PhotoFiche getPhotoFicheFromJSONImage(Image jsonImage){
		PhotoFiche photoFiche = new PhotoFiche(
								jsonImage.getDataMap().getImage(),
								jsonImage.getDataMap().getTitle(),
								jsonImage.getDataMap().getLegend()
							);
		return photoFiche;
	}
	
	
	public List<PhotoFiche> getListePhotosFicheFromJsonImages(List<Image> jsonImages) {
		
		List<PhotoFiche> listePhotosFiche = new ArrayList<PhotoFiche>(0);
		for (Image jsonImage : jsonImages) {
			listePhotosFiche.add(getPhotoFicheFromJSONImage(jsonImage));
		}
		
		return listePhotosFiche;
	}
}
