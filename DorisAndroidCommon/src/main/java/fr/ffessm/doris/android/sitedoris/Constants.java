/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
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

    /*
     * URL
     */
    public final static String SITE_RACINE_URL = "https://doris.ffessm.fr/";

    //public static final String IMAGE_BASE_URL = "https://doris.ffessm.fr/var/doris/storage/images/images";
    public static final String IMAGE_BASE_URL = "https://doris.ffessm.fr";
    public static final String PORTRAIT_BASE_URL = IMAGE_BASE_URL;
    // La photo existe toujours au format blabla.jpg, puis d'autres formats sont générés suivant le besoin,
    // nous utilisons de même ces différents formats
    // Suffixe initial
    public static final String IMAGE_BASE_URL_SUFFIXE = ".jpg";
    // La plus petite dispo.
    public static final String VIGNETTE_BASE_URL_SUFFIXE = "_small.jpg";
    // Image Intermédiaire pouvant exister sans que la plus petite soit dispo.
    public static final String PETITE_BASE_URL_SUFFIXE = "_specieHomeCard.jpg";
    // Parfois il faut prendre la "forum"
    public static final String PETITE2_BASE_URL_SUFFIXE = "_forumHomeCard.jpg";
    // Image de Taille Moyenne Fixée à 300
    public static final String MOYENNE_BASE_URL_SUFFIXE = "_image300.jpg";
    // Si 300 non dispo. on prend 600
    public static final String MOYENNE2_BASE_URL_SUFFIXE = "_image600.jpg";
    // Meilleure Qualitée (l'image Initiale)
    public static final String GRANDE_BASE_URL_SUFFIXE = ".jpg";

    public static final String ILLUSTRATION_DEFINITION_BASE_URL = IMAGE_BASE_URL + "/diaporamaglo";
    public static final String ILLUSTRATION_BIBLIO_BASE_URL = IMAGE_BASE_URL + "/photos_biblio_moy";
    /*
     * Autres Constantes
     */
    public static final String PREFIX_IMGDSK_PORTRAIT = "";
    public static final String PREFIX_IMGDSK_DEFINITION = "definition-";
    public static final String PREFIX_IMGDSK_BIBLIO = "biblio-";
    public static final String FICHE_NOMCOMMUN_VALUE_IF_EMPTY = "ã";
    public final static String ZONE_GEOGRAPHIQUE_TOUTES_ZONES_DESCR = "Toutes les zones";
    private final static String LISTE_FICHES_ZONE_URL = "nom_scientifique.asp?numero_fichier=";
    private final static String GROUPES_URL = "groupes.asp?numero_fichier=";
    private final static String SOUSGROUPE_URL = "&sousgroupe_numero=";
    private final static String GROUPE_CONTENU_URL = "fiches_liste.asp?numero_fichier=@zone&groupe_numero=@groupe&pagecourante=@page";
    private final static String FICHE_RACINE_URL_ID = "fiche2.asp?fiche_numero=";
    private final static String FICHE_RACINE_URL_NOM_COMMUN = "fiche3.asp?nomcommun=";
    private final static String PARTICIPANTS_RACINE_URL = "contacts.asp?filtre=";
    private final static String PARTICIPANT_RACINE_URL = "contact_fiche.asp?contact_numero=";
    private final static String GLOSSAIRE_RACINE_URL = "glossaire.asp?filtre=@lettre&mapage=@numero";
    private final static String DEFINITION_RACINE_URL = "glossaire_detail.asp?glossaire_numero=";
    private final static String BIBLIOGRAPHIES_RACINE_URL = "biblio.asp?mapage=@indice&PageCourante=@precedent";
    private final static String BIBLIOGRAPHIE_RACINE_URL = "biblio_fiche.asp?biblio_numero=";
    private final static String REDACTEUR_PRINCIPAL_LIB = "Rédacteur Principal";
    private final static String REDACTEUR_LIB = "Rédacteur";
    private final static String VERIFICATEUR_LIB = "Vérificateur";
    private final static String CORRECTEUR_LIB = "Correcteur";
    private final static String CORRECTEUR_SCIENTIFIQUE_LIB = "Correcteur Scientifique";
    private final static String RESPONSABLE_REGIONAL_LIB = "Responsable Régional";
    private final static String RESPONSABLE_NATIONAL_LIB = "Responsable National";
    private final static String PHOTOGRAPHE_LIB = "Photographe";
    private final static String PICTO_ESPECE_REGLEMENTEE_LIB = "Espèce réglementée";
    private final static String PICTO_ESPECE_DANGEREUSE_LIB = "Espèce dangereuse";

    public static String getSiteUrl() {
        return SITE_RACINE_URL;
    }

    public static String getListeFichesUrl(int numZone) {
        return SITE_RACINE_URL + LISTE_FICHES_ZONE_URL + numZone;
    }

    public static String getFicheFromIdUrl(int inId) {
        return SITE_RACINE_URL + FICHE_RACINE_URL_ID + inId;
    }

    public static String getFicheFromNomCommunUrl(String inNomCommun) {
        Common_Outils commonOutils = new Common_Outils();
        return SITE_RACINE_URL + FICHE_RACINE_URL_NOM_COMMUN + commonOutils.formatStringNormalizer(inNomCommun.replace(" ", "%20"));
    }

    public static String getListeParticipantsUrl(String inInitiale) {
        return SITE_RACINE_URL + PARTICIPANTS_RACINE_URL + inInitiale;
    }

    public static String getParticipantUrl(int numeroParticipantDoris) {
        return SITE_RACINE_URL + PARTICIPANT_RACINE_URL + numeroParticipantDoris;
    }

    /*
     * Accession aux Constantes
     */

    public static String getListeDefinitionsUrl(String inInitiale, String numero) {
        return SITE_RACINE_URL + GLOSSAIRE_RACINE_URL.replace("@lettre", inInitiale).replace("@numero", numero);
    }

    public static String getDefinitionUrl(String inId) {
        return SITE_RACINE_URL + DEFINITION_RACINE_URL + inId;
    }

    public static String getListeBibliographiesUrl(int numPage) {
        int pagePrecedente = numPage - 1;
        return SITE_RACINE_URL + BIBLIOGRAPHIES_RACINE_URL.replace("@indice", "" + numPage).replace("@precedent", "" + pagePrecedente);
    }

    public static String getBibliographieUrl(int inId) {
        return SITE_RACINE_URL + BIBLIOGRAPHIE_RACINE_URL + inId;
    }

    public static int getNumZoneForUrl(ZoneGeographiqueKind zoneKing) {
        switch (zoneKing) {
            case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                return 1;
            case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                return 2;
            case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                return 3;
            case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                return 4;
            case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                return 5;
            case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                return 6;
            case FAUNE_FLORE_MER_ROUGE:
                return 7;
            case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                return 8;
            case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                return 9;
            case FAUNE_FLORE_TOUTES_ZONES:
                return 10;
            default:
                return 99;
        }
    }

    public static int getNumEtatFiche(EtatFicheKind etatFicheKing) {
        switch (etatFicheKing) {
            case EN_COURS:
                return 1;
            case EN_COURS_2:
                return 2;
            case EN_COURS_3:
                return 3;
            case PUBLIEE:
                return 4;
            case PROPOSEE:
                return 5;
            default:
                return 5;
        }
    }

    public static String getGroupesZoneUrl(int zone) {
        return SITE_RACINE_URL + GROUPES_URL + zone;
    }

    public static String getGroupeContenuUrl(int zone, int numeroGroupe, int numeroSousGroupe, int page) {
        String listeGroupeUrl = SITE_RACINE_URL + GROUPE_CONTENU_URL
                .replace("@zone", "" + zone).replace("@groupe", "" + numeroGroupe).replace("@page", "" + page);
        if (numeroSousGroupe != 0) {
            listeGroupeUrl += SOUSGROUPE_URL + numeroSousGroupe;
        }
        return listeGroupeUrl;
    }

    /*
     * Gestion Zones Géographiques
     */
    //TODO : A Effacer ?
    public static ZoneGeographiqueKind getZoneGeographiqueFromId_AEFFACER(int zoneId) {
        switch (zoneId) {
            case 1:
                return ZoneGeographiqueKind.FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE;
            case 2:
                return ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE;
            case 3:
                return ZoneGeographiqueKind.FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE;
            case 4:
                return ZoneGeographiqueKind.FAUNE_FLORE_SUBAQUATIQUES_CARAIBES;
            case 5:
                return ZoneGeographiqueKind.FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST;
            case 6:
                return ZoneGeographiqueKind.FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES;
            // Aurait du être -1 (10 est la clé pour url)
            case 7:
                return ZoneGeographiqueKind.FAUNE_FLORE_MER_ROUGE;
            case 8:
                return ZoneGeographiqueKind.FAUNE_FLORE_MEDITERRANEE_FRANCAISE;
            case 9:
                return ZoneGeographiqueKind.FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE;
            case 10:
                return ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES;
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
            case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                return "Faune et flore des terres antarctiques Françaises";
            case FAUNE_FLORE_TOUTES_ZONES:
                return "Faune et flore subaquatiques de toutes les zones DORIS";
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
            case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                return "Circumpolaire, mers et côtes australes, Crozet, Kerguelen, Terre Adélie";
            case FAUNE_FLORE_TOUTES_ZONES:
                return "Toutes les espèces de toutes les zones DORIS";
            default:
                return "Toutes les espèces de toutes les zones DORIS";
        }
    }

    public static String getTitreCourtZoneGeographique(ZoneGeographiqueKind zoneKing) {
        if (zoneKing == null) return "Toutes Zones DORIS";

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
            case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                return "Antarctique";
            case FAUNE_FLORE_TOUTES_ZONES:
                return "Toutes Zones DORIS";
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
        if (typeParticipant.equalsIgnoreCase(REDACTEUR_PRINCIPAL_LIB))
            return ParticipantKind.REDACTEUR_PRINCIPAL;
        if (typeParticipant.equalsIgnoreCase(REDACTEUR_LIB)) return ParticipantKind.REDACTEUR;
        if (typeParticipant.equalsIgnoreCase(VERIFICATEUR_LIB)) return ParticipantKind.VERIFICATEUR;
        if (typeParticipant.equalsIgnoreCase(CORRECTEUR_SCIENTIFIQUE_LIB))
            return ParticipantKind.CORRECTEUR_SCIENTIFIQUE;
        if (typeParticipant.equalsIgnoreCase(CORRECTEUR_LIB)) return ParticipantKind.CORRECTEUR;
        if (typeParticipant.equalsIgnoreCase(RESPONSABLE_REGIONAL_LIB))
            return ParticipantKind.RESPONSABLE_REGIONAL;
        if (typeParticipant.equalsIgnoreCase(RESPONSABLE_NATIONAL_LIB))
            return ParticipantKind.RESPONSABLE_NATIONAL;
        if (typeParticipant.equalsIgnoreCase(PHOTOGRAPHE_LIB)) return ParticipantKind.PHOTOGRAPHE;

        return null;
    }

    public static PictoKind getTypePicto(String pictoTag) {
        pictoTag.trim();
        if (pictoTag.equalsIgnoreCase(PICTO_ESPECE_REGLEMENTEE_LIB))
            return PictoKind.PICTO_ESPECE_REGLEMENTEE;
        if (pictoTag.equalsIgnoreCase(PICTO_ESPECE_DANGEREUSE_LIB))
            return PictoKind.PICTO_ESPECE_DANGEREUSE;

        return null;
    }

    /*
     * Liste des Types de Zone géographique, Participant, etc.
     */
    public enum ZoneGeographiqueKind {
        FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE,
        FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE,
        FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE,
        FAUNE_FLORE_SUBAQUATIQUES_CARAIBES,
        FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST,
        FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES,
        FAUNE_FLORE_TOUTES_ZONES,
        FAUNE_FLORE_MER_ROUGE,
        FAUNE_FLORE_MEDITERRANEE_FRANCAISE,
        FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE
    }

    // trié de la pblus petite à la plus grande
    public enum ImagePostFixCode {
        SMALL("xs", "_small.jpg"),     // _small.jpg  quand dispo, c'est très petit
        MEDIUM("ms", "_medium.jpg"),     // _medium.jpg
        LARGE("ls", "_large.jpg"),      // _large.jpg
        IMAGE300("03", "_image300.jpg"),   // _image300.jpg    // vignette par défaut sinon large, medium, small, base
        ForumCard("fc", "_forumHomeCard.jpg"),  // _forumHomeCard.jpg
        SpecieCard("sc", "_specieHomeCard.jpg"),  // _specieHomeCard.jpg
        IMAGE600("06", "_image600.jpg"),   // _image600.jpg    // Medium par défaut, sinon, image1000, Specie, Forum,  base
        IMAGE1000("10", "_image1000.jpg"),  // _image1000.jpg
        IMAGE1200("12", "_image1200.jpg"),  // _image1200.jpg
        REFERENCE("rf", "_reference.jpg"),  // _reference.jpg
        BASE("", ".jpg");       // .jpg


        private String shortCode;
        private String postFix;

        private ImagePostFixCode(String shortcode, String postFix) {
            this.shortCode = shortcode;
            this.postFix = postFix;
        }

        public static ImagePostFixCode getEnumFromCode(String code) {
            switch (code) {
                case "xs":
                    return SMALL;
                case "ms":
                    return MEDIUM;
                case "ls":
                    return LARGE;
                case "03":
                    return IMAGE300;
                case "fc":
                    return ForumCard;
                case "sc":
                    return SpecieCard;
                case "06":
                    return IMAGE600;
                case "10":
                    return IMAGE1000;
                case "12":
                    return IMAGE1200;
                case "rf":
                    return REFERENCE;
                case "":
                    return BASE;
                default:
                    return BASE;
            }
        }

        public String getShortCode() {
            return this.shortCode;
        }

        public String getPostFix() {
            return this.postFix;
        }
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

    public enum PictoKind {
        PICTO_ESPECE_REGLEMENTEE,
        PICTO_ESPECE_DANGEREUSE
    }

    public enum EtatFicheKind {
        EN_COURS, //1
        EN_COURS_2, //2
        EN_COURS_3, //3
        PUBLIEE, //4
        PROPOSEE // 5
    }

    public enum FileHtmlKind {
        LISTE_FICHES,
        FICHE,
        LISTE_PHOTOS_FICHE,
        LISTE_BIBLIO,
        BIBLIO,
        LISTE_TERMES,
        TERME,
        LISTE_GROUPES,
        GROUPE,
        GROUPES_ZONE,
        LISTE_PARTICIPANTS,
        AUTRE
    }


}