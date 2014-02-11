/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2013 - FFESSM
 * Auteurs : Guillaume Mo <gmo7942@gmail.com>
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

package fr.ffessm.doris.android.sitedoris;

import java.util.ArrayList;


public class Constants {

	/*
	 * URL
	 */
    private final static String SITE_RACINE_URL = "http://doris.ffessm.fr/";
    
	public static final String IMAGE_BASE_URL = "http://doris.ffessm.fr/gestionenligne/";
	public static final String PORTRAIT_BASE_URL = IMAGE_BASE_URL+ "photos_vig";
	public static final String VIGNETTE_BASE_URL = IMAGE_BASE_URL+ "photos_fiche_vig";
	public static final String MOYENNE_BASE_URL = IMAGE_BASE_URL+ "photos_fiche_moy";
	public static final String GRANDE_BASE_URL = IMAGE_BASE_URL+ "photos";
	
    private final static String LISTE_TOUTES_FICHES_URL = "nom_scientifique.asp?numero_fichier=10";
    
    private final static String GROUPES_URL = "groupes.asp?numero_fichier=";
    private final static String SOUSGROUPE_URL = "&sousgroupe_numero=";
    private final static String GROUPE_CONTENU_URL = "fiches_liste.asp?numero_fichier=@zone&groupe_numero=@groupe&pagecourante=@page";
    
    private final static String LISTE_FICHES_FRANCE_METROPOLITAINE_URL = "nom_scientifique.asp?numero_fichier=1&fichier=Faune%20et%20flore%20marines%20de%20France%20m%E9tropolitaine";
    private final static String LISTE_FICHES_EAU_DOUCE_URL = "nom_scientifique.asp?numero_fichier=2&fichier=Faune%20et%20flore%20dulcicoles%20de%20France%20m%E9tropolitaine";
    private final static String LISTE_FICHES_CARAIBES_URL = "nom_scientifique.asp?numero_fichier=4&fichier=Faune%20et%20flore%20subaquatiques%20des%20Cara%EFbes";
    private final static String LISTE_FICHES_ATL_NORD_OUEST_URL = "nom_scientifique.asp?numero_fichier=5&fichier=Faune%20et%20flore%20marines%20et%20dulcicoles%20de%20l'Atlantique%20Nord-Ouest";
    private final static String LISTE_FICHES_INDO_PACIFIQUE_URL = "nom_scientifique.asp?numero_fichier=3&fichier=Faune%20et%20flore%20marines%20et%20dulcicoles%20de%20l'Indo-Pacifique";
    
    private final static String FICHE_RACINE_URL_ID = "fiche2.asp?fiche_numero=";
    private final static String FICHE_RACINE_URL_NOM_COMMUN = "fiche3.asp?nomcommun=";
    
    private final static String PARTICIPANTS_RACINE_URL = "contacts.asp?filtre=";
    private final static String PARTICIPANT_RACINE_URL = "contact_fiche.asp?contact_numero=";
    
    private final static String GLOSSAIRE_RACINE_URL = "glossaire.asp?filtre=@lettre&mapage=@numero";
    private final static String DEFINITION_RACINE_URL = "glossaire_detail.asp?glossaire_numero=";

    private final static String BIBLIOGRAPHIES_RACINE_URL = "biblio.asp?mapage=@indice&PageCourante=@precedent";
    private final static String BIBLIOGRAPHIE_RACINE_URL = "biblio_fiche.asp?biblio_numero=";
    
	/*
	 * Autres Constantes
	 */
  
    
	/*
	 * Liste des Types de Zone géographique, Participant, etc.
	 */
    public enum ZoneGeographiqueKind {
    	FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE,
    	FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE,
    	FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE,
    	FAUNE_FLORE_SUBAQUATIQUES_CARAIBES,
    	FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST
    }
	
    public enum ParticipantKind {
    	REDACTEUR_PRINCIPAL,
    	REDACTEUR,
    	VERIFICATEUR,
    	CORRECTEUR_SCIENTIFIQUE,
    	CORRECTEUR,
    	RESPONSABLE_REGIONAL,
    	RESPONSABLE_NATIONAL,
    	PHOTOGRAPHE
    }
    
    private final static String REDACTEUR_PRINCIPAL_LIB = "Rédacteur Principal";
	private final static String REDACTEUR_LIB = "Rédacteur";
	private final static String VERIFICATEUR_LIB = "Vérificateur";
	private final static String CORRECTEUR_LIB = "Correcteur";
	private final static String CORRECTEUR_SCIENTIFIQUE_LIB = "Correcteur Scientifique";
	private final static String RESPONSABLE_REGIONAL_LIB = "Responsable Régional";
	private final static String RESPONSABLE_NATIONAL_LIB = "Responsable National";
	private final static String PHOTOGRAPHE_LIB = "Photographe";

    public enum PictoKind {
    	PICTO_ESPECE_REGLEMENTEE,
    	PICTO_ESPECE_DANGEREUSE
    }
    private final static String PICTO_ESPECE_REGLEMENTEE_LIB = "Espèce réglementée";
	private final static String PICTO_ESPECE_DANGEREUSE_LIB = "Espèce dangereuse";

	/*
	 * Accession aux Constantes
	 */
    
	public static String getSiteUrl() {
		String listeFichesUrl = SITE_RACINE_URL;
		return listeFichesUrl;
    }
    
    public static String getListeFichesUrl() {
		String listeFichesUrl = SITE_RACINE_URL + LISTE_TOUTES_FICHES_URL;
    	return listeFichesUrl;
    }
    
    public static String getFicheFromIdUrl(int inId) {
		String ficheUrl = SITE_RACINE_URL + FICHE_RACINE_URL_ID + inId;
    	return ficheUrl;
    }
    
    public static String getFicheFromNomCommunUrl(String inNomCommun) {
		String ficheUrl = SITE_RACINE_URL + FICHE_RACINE_URL_NOM_COMMUN + Outils.formatStringNormalizer(inNomCommun.replace(" ", "%20") );
    	return ficheUrl;
    }
    
    public static String getListeParticipantsUrl(String inInitiale) {
 		String listeParticipantsUrl = SITE_RACINE_URL + PARTICIPANTS_RACINE_URL + inInitiale;
     	return listeParticipantsUrl;
    }
    public static String getParticipantUrl(String numeroParticipantDoris) {
 		String listeParticipantsUrl = SITE_RACINE_URL + PARTICIPANT_RACINE_URL + numeroParticipantDoris;
     	return listeParticipantsUrl;
    }
    
    public static String getListeDefinitionsUrl(String inInitiale, String numero) {
 		String listeDefinitionsUrl = SITE_RACINE_URL + GLOSSAIRE_RACINE_URL.replace("@lettre", inInitiale).replace("@numero", numero);
     	return listeDefinitionsUrl;
    }
    
    public static String getDefinitionUrl(String inId) {
 		String definitionsUrl = SITE_RACINE_URL + DEFINITION_RACINE_URL + inId;
     	return definitionsUrl;
    }
    
    public static String getListeBibliographiesUrl(int numPage) {
    	int pagePrecedente = numPage - 1;
 		String listeBibliographiesUrl = SITE_RACINE_URL + BIBLIOGRAPHIES_RACINE_URL.replace("@indice", ""+numPage).replace("@precedent", ""+pagePrecedente);
     	return listeBibliographiesUrl;
    }
    
    public static String getBibliographieUrl(String inId) {
 		String listeDefinitionsUrl = SITE_RACINE_URL + BIBLIOGRAPHIE_RACINE_URL + inId;
     	return listeDefinitionsUrl;
    }

    
    public static String getListeFichesUrl(ZoneGeographiqueKind zoneKing) {
    	switch (zoneKing) {
		case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
			return SITE_RACINE_URL + LISTE_FICHES_FRANCE_METROPOLITAINE_URL;
		case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
			return SITE_RACINE_URL + LISTE_FICHES_EAU_DOUCE_URL;
		case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
			return SITE_RACINE_URL + LISTE_FICHES_INDO_PACIFIQUE_URL;
		case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
			return SITE_RACINE_URL + LISTE_FICHES_CARAIBES_URL;
		case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
			return SITE_RACINE_URL + LISTE_FICHES_ATL_NORD_OUEST_URL;
		default:
			return SITE_RACINE_URL + LISTE_TOUTES_FICHES_URL;
		}
    } 
    
    public static String getGroupesUrl(int zone) {
		String listeFichesUrl = SITE_RACINE_URL + GROUPES_URL+zone;
    	return listeFichesUrl;
    }
    
    public static String getGroupeContenuUrl(int zone, int numeroGroupe, int numeroSousGroupe, int page) {
		String listeGroupeUrl = SITE_RACINE_URL + GROUPE_CONTENU_URL
			.replace("@zone", ""+zone).replace("@groupe", ""+numeroGroupe).replace("@page", ""+page);
		if (numeroSousGroupe != 0) {
			listeGroupeUrl += SOUSGROUPE_URL + numeroSousGroupe;
		}
    	return listeGroupeUrl;
    }

    
	/*
	 * Gestion Zones Géographiques
	 */
    public static ZoneGeographiqueKind getZoneGeographiqueFromId(int zoneId) {
    	switch (zoneId) {
		case 1 :
			return ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE;
		case 2 :
			return ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE;
		case 3 :
			return ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE;
		case 4 :
			return ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES;
		case 5 :
			return ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST;
		default:
			return null;
		}
    }
    public static String getTitreZoneGeographique(ZoneGeographiqueKind zoneKing) {
    	switch (zoneKing) {
		case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
			return "Faune et flore marines de France métropolitaine";
		case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
			return "Faune et flore dulcicoles de France métropolitaine";
		case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
			return "Faune et flore subaquatiques de l'Indo-Pacifique";
		case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
			return "Faune et flore subaquatiques des Caraïbes";
		case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
			return "Faune et flore subaquatiques de l'Atlantique Nord-Ouest";
		default:
			return "Faune et flore subaquatiques de toutes les zones DORIS";
		}
    }
    public static String getTexteZoneGeographique(ZoneGeographiqueKind zoneKing) {
    	switch (zoneKing) {
		case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
			return "Méditerranée, Atlantique, Manche et mer du Nord";
		case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
			return "Fleuves, rivières, lacs et étangs, ...";
		case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
			return "La Réunion, Mayotte, Nouvelle-Calédonie, Polynésie et autres";
		case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
			return "Guadeloupe, Martinique et autres";
		case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
			return "Côte est du Canada, embouchure du St Laurent, archipel de St Pierre-et-Miquelon";
		default:
			return "Faune et flore subaquatiques de toutes les zones DORIS";
		}
    }
    public static String getTitreCourtZoneGeographique(ZoneGeographiqueKind zoneKing) {
    	if (zoneKing==null) return "Toutes Zones DORIS";
    	
    	switch (zoneKing) {
		case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
			return "France - Espèces Marines";
		case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
			return "France - Espèces Dulcicoles";
		case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
			return "Indo-Pacifique";
		case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
			return "Caraïbes";
		case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
			return "Atlant. Nord-Ouest";
		default:
			return "Toutes Zones DORIS";
		}
    }
	/*
	 * Gestion Participants
	 */
    public static String getTitreParticipant(ParticipantKind participantKing) {
    	switch (participantKing) {
		case REDACTEUR_PRINCIPAL:
			return REDACTEUR_PRINCIPAL_LIB;
		case REDACTEUR:
			return REDACTEUR_LIB;
		case VERIFICATEUR:
			return VERIFICATEUR_LIB;
		case CORRECTEUR_SCIENTIFIQUE:
			return CORRECTEUR_SCIENTIFIQUE_LIB;
		case CORRECTEUR:
			return CORRECTEUR_LIB;
		case RESPONSABLE_REGIONAL:
			return RESPONSABLE_REGIONAL_LIB;
		case RESPONSABLE_NATIONAL:
			return RESPONSABLE_NATIONAL_LIB;
		case PHOTOGRAPHE:
			return PHOTOGRAPHE_LIB;
		default:
			return "Type d'intervenant inconnu";
		}
    }
    
    public static String getTitreParticipant(int participantKingInt) {
    	switch (participantKingInt) {
		case 0:
			return REDACTEUR_PRINCIPAL_LIB;
		case 1:
			return REDACTEUR_LIB;
		case 2:
			return VERIFICATEUR_LIB;
		case 3:
			return CORRECTEUR_SCIENTIFIQUE_LIB;
		case 4:
			return CORRECTEUR_LIB;
		case 5:
			return RESPONSABLE_REGIONAL_LIB;
		case 6:
			return RESPONSABLE_NATIONAL_LIB;
		case 7:
			return PHOTOGRAPHE_LIB;
		default:
			return "Type d'intervenant inconnu";
		}
    }
    
    public static ParticipantKind getTypeParticipant(String typeParticipant) {
    	typeParticipant.trim();
    	if (typeParticipant.equalsIgnoreCase(REDACTEUR_PRINCIPAL_LIB)) return ParticipantKind.REDACTEUR_PRINCIPAL;
    	if (typeParticipant.equalsIgnoreCase(REDACTEUR_LIB)) return ParticipantKind.REDACTEUR;
    	if (typeParticipant.equalsIgnoreCase(VERIFICATEUR_LIB)) return ParticipantKind.VERIFICATEUR;
    	if (typeParticipant.equalsIgnoreCase(CORRECTEUR_SCIENTIFIQUE_LIB)) return ParticipantKind.CORRECTEUR_SCIENTIFIQUE;
    	if (typeParticipant.equalsIgnoreCase(CORRECTEUR_LIB)) return ParticipantKind.CORRECTEUR;
    	if (typeParticipant.equalsIgnoreCase(RESPONSABLE_REGIONAL_LIB)) return ParticipantKind.RESPONSABLE_REGIONAL;
    	if (typeParticipant.equalsIgnoreCase(RESPONSABLE_NATIONAL_LIB)) return ParticipantKind.RESPONSABLE_NATIONAL;
    	if (typeParticipant.equalsIgnoreCase(PHOTOGRAPHE_LIB)) return ParticipantKind.PHOTOGRAPHE;
		
		return null;
    }
    
    public static PictoKind getTypePicto(String pictoTag) {
    	pictoTag.trim();
    	if (pictoTag.equalsIgnoreCase(PICTO_ESPECE_REGLEMENTEE_LIB)) return PictoKind.PICTO_ESPECE_REGLEMENTEE;
    	if (pictoTag.equalsIgnoreCase(PICTO_ESPECE_DANGEREUSE_LIB)) return PictoKind.PICTO_ESPECE_DANGEREUSE;
 	
		return null;
    }
}