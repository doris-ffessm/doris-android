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


public class Constants {

    private final static String SITE_RACINE_URL = "http://doris.ffessm.fr/";
    private final static String LISTE_TOUTES_FICHES_URL = "nom_scientifique.asp?numero_fichier=10";
    private final static String GROUPES_URL = "groupes.asp?numero_fichier=10";
    private final static String LISTE_FICHES_FRANCE_METROPOLITAINE_URL = "nom_scientifique.asp?numero_fichier=1&fichier=Faune%20et%20flore%20marines%20de%20France%20m%E9tropolitaine";
    private final static String LISTE_FICHES_EAU_DOUCE_URL = "nom_scientifique.asp?numero_fichier=2&fichier=Faune%20et%20flore%20dulcicoles%20de%20France%20m%E9tropolitaine";
    private final static String LISTE_FICHES_CARAIBES_URL = "nom_scientifique.asp?numero_fichier=4&fichier=Faune%20et%20flore%20subaquatiques%20des%20Cara%EFbes";
    private final static String LISTE_FICHES_ATL_NORD_OUEST_URL = "nom_scientifique.asp?numero_fichier=5&fichier=Faune%20et%20flore%20marines%20et%20dulcicoles%20de%20l'Atlantique%20Nord-Ouest";
    private final static String LISTE_FICHES_INDO_PACIFIQUE_URL = "nom_scientifique.asp?numero_fichier=3&fichier=Faune%20et%20flore%20marines%20et%20dulcicoles%20de%20l'Indo-Pacifique";
    private final static String PARTICIPANTS_RACINE_URL = "contacts.asp?filtre=";
    
    public enum ZoneGeographiqueKind {
    	FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE,
    	FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE,
    	FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE,
    	FAUNE_FLORE_SUBAQUATIQUES_CARAIBES,
    	FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST
    }
	private final static String FICHIER_DORIS_XML = "prefetchedDorisDB.xml";
	
    public static String getSiteUrl() {
		String listeFichesUrl = SITE_RACINE_URL;
		return listeFichesUrl;
    }
    
    public static String getListeFichesUrl() {
		String listeFichesUrl = SITE_RACINE_URL + LISTE_TOUTES_FICHES_URL;
    	return listeFichesUrl;
    }
    
    public static String getListeParticipantsUrl(String inInitiale) {
 		String listeParticipantsUrl = SITE_RACINE_URL + PARTICIPANTS_RACINE_URL + inInitiale;
     	return listeParticipantsUrl;
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
			return "La Réunion, Mayotte, Nouvelle-Calédonie, Polynésie et autres...";
		case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
			return "Guadeloupe, Martinique et autress";
		case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
			return "Côte est du Canada, embouchure du St Laurent, archipel de St Pierre-et-Miquelon";
		default:
			return "Faune et flore subaquatiques de toutes les zones DORIS";
		}
    }
    
    public static String getGroupesUrl() {
		String listeFichesUrl = SITE_RACINE_URL + GROUPES_URL;
    	return listeFichesUrl;
    }
    
    public static String getFichierDorisXML() {
		String refFichierDorisXML = FICHIER_DORIS_XML;
    	return refFichierDorisXML;
    }
	
}