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
package fr.ffessm.doris.android.tools;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.text.StrBuilder;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.associations.Fiches_ZonesGeographiques;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.sitedoris.Constants.ZoneGeographiqueKind;

public class Fiches_Outils {
    private static final String LOG_TAG = Fiches_Outils.class.getCanonicalName();

    private Context context;

    private Param_Outils paramOutils;
    private OrmLiteDBHelper ormLiteDBHelper;

    private List<Integer> ficheIdList;
    private List<Integer> filteredFicheIdList;

    public Fiches_Outils(Context context) {

        this.context = context;
        paramOutils = new Param_Outils(context);

        ormLiteDBHelper = new OrmLiteDBHelper(context);

        ficheIdList = new ArrayList<Integer>();
        filteredFicheIdList = new ArrayList<Integer>();
    }

    public enum MajListeFichesType {
        M0, M1, M2, M3
    }

    public enum TypeLancement_kind {
        START,
        MANUEL
    }

    public enum OrdreTri {NOMCOMMUN, NOMSCIENTIFIQUE}

    ;

    public int getNbFichesZoneGeo(ZoneGeographiqueKind inZoneGeo) {

        int nbFiches = 0;

        if (inZoneGeo == ZoneGeographiqueKind.FAUNE_FLORE_TOUTES_ZONES) {
            RuntimeExceptionDao<Fiche, Integer> entriesDao = ormLiteDBHelper.getFicheDao();
            nbFiches = (int) entriesDao.countOf();

        } else {
            RuntimeExceptionDao<Fiches_ZonesGeographiques, Integer> entriesDao = ormLiteDBHelper.getFiches_ZonesGeographiquesDao();
            nbFiches = (int) entriesDao.queryForEq(
                    Fiches_ZonesGeographiques.ZONEGEOGRAPHIQUE_ID_FIELD_NAME,
                    Constants.getNumZoneForUrl(inZoneGeo)).size();
        }

        ormLiteDBHelper.getFicheDao().clearObjectCache();
        return nbFiches;
    }

    public int getNbFichesPublished() {

        int nbFiches = 0;

        RuntimeExceptionDao<Fiche, Integer> entriesDao = ormLiteDBHelper.getFicheDao();
        nbFiches = (int) entriesDao.queryForEq(
                Fiche.XML_ATT_ETATFICHE,
                Constants.getNumEtatFiche(Constants.EtatFicheKind.PUBLIEE)).size();

        ormLiteDBHelper.getFicheDao().clearObjectCache();
        return nbFiches;
    }

    public int getNbFichesProposed() {

        int nbFiches = 0;

        RuntimeExceptionDao<Fiche, Integer> entriesDao = ormLiteDBHelper.getFicheDao();
        nbFiches = (int) entriesDao.queryForEq(
                Fiche.XML_ATT_ETATFICHE,
                Constants.getNumEtatFiche(Constants.EtatFicheKind.PROPOSEE)).size();

        ormLiteDBHelper.getFicheDao().clearObjectCache();
        return nbFiches;
    }

    public List<Integer> getListeIdFiches() {
        return ficheIdList;
    }

    public List<Integer> getListeIdFichesFiltrees() {
        return filteredFicheIdList;
    }

    public List<Integer> getListeIdFichesFiltrees(Context context, DorisDBHelper contextDB, int filteredZoneGeoId, int filteredGroupeId) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        ficheIdList.clear();
        filteredFicheIdList.clear();
        ArrayList<Integer> acceptedGroupeId = new ArrayList<Integer>();

        String ordreTri = prefs.getString(context.getString(R.string.pref_key_accueil_fiches_ordre), context.getString(R.string.accueil_fiches_ordre_default));
        String orderByClause = "";
        if (ordreTri.equals("Commun")) orderByClause = " ORDER BY Fiche.textePourRechercheRapide";
        if (ordreTri.equals("Scientifique")) orderByClause = " ORDER BY Fiche.nomScientifique";


        String filtreEtat = prefs.getString(context.getString(R.string.pref_key_accueil_etat_fiches_affiche), "toutes");
        String whereSuffixClauseWHERE = "";
        String whereSuffixClauseAND = "";
        //Fiches Publiées + En cours de Rédaction
        if (filtreEtat.equals("pr")) {
            whereSuffixClauseWHERE = " WHERE Fiche.etatFiche <> 5";
            whereSuffixClauseAND = " AND Fiche.etatFiche <> 5";
        }
        //Fiches Publiées seulement
        if (filtreEtat.equals("p")) {
            whereSuffixClauseWHERE = " WHERE Fiche.etatFiche = 4";
            whereSuffixClauseAND = " AND Fiche.etatFiche = 4";
        }


        try {
            Log.d(LOG_TAG, "filteredZoneGeoId -  " + filteredZoneGeoId);

            if (filteredZoneGeoId == -1) {
                Log.d(LOG_TAG, "_contextDB.ficheDao.queryForAll() - début " + contextDB.ficheDao.countOf());
                //this.ficheList = _contextDB.ficheDao.queryForAll();
                ficheIdList = new ArrayList<Integer>((int) contextDB.ficheDao.countOf());


                // récupère les id seulement des fiches
                GenericRawResults<String[]> rawResults =
                        contextDB.ficheDao.queryRaw("SELECT _id FROM fiche" + whereSuffixClauseWHERE + orderByClause);
                for (String[] resultColumns : rawResults) {
                    String iDString = resultColumns[0];
                    ficheIdList.add(Integer.parseInt(iDString));
                }

                Log.d(LOG_TAG, "_contextDB.ficheDao.queryForAll() - fin");
            } else {
                final String queryFichesForZone = "SELECT Fiche_id FROM fiches_ZonesGeographiques , Fiche WHERE ZoneGeographique_id=" + filteredZoneGeoId + whereSuffixClauseAND + " AND fiches_ZonesGeographiques.Fiche_id = Fiche._id" + orderByClause;
                Log.d(LOG_TAG, "queryFichesForZone - début - " + queryFichesForZone);
                GenericRawResults<String[]> rawResults =
                        contextDB.ficheDao.queryRaw(queryFichesForZone);
                ficheIdList = new ArrayList<Integer>();
                for (String[] resultColumns : rawResults) {
                    String iDString = resultColumns[0];
                    ficheIdList.add(Integer.parseInt(iDString));
                }
                Log.d(LOG_TAG, "queryFichesForZone - fin");
            }

        } catch (java.sql.SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }


        try {
            // si filtre espèce actif, récupérer la liste des fiches pour les groupes accepté, puis faire un diff avec le filtre précédent
            Log.d(LOG_TAG, "filteredGroupeId -  " + filteredGroupeId);

            if (filteredGroupeId != 1) {
                // récupère la liste des groupes acceptés
                Groupe searchedGroupe = contextDB.groupeDao.queryForId(filteredGroupeId);
                //Log.d(LOG_TAG, "filter _contextDB="+_contextDB);
                searchedGroupe.setContextDB(contextDB);
                acceptedGroupeId = new ArrayList<Integer>();
                for (Groupe groupe : Groupes_Outils.getAllSubGroupesForGroupe(searchedGroupe)) {
                    acceptedGroupeId.add(groupe.getId());
                }
                StrBuilder queryIdForGroupes = new StrBuilder("SELECT _id FROM fiche WHERE groupe_id IN (");
                queryIdForGroupes.appendWithSeparators(acceptedGroupeId, ", ");
                queryIdForGroupes.append(")" + whereSuffixClauseAND + orderByClause + ";");
                Log.d(LOG_TAG, "queryIdForGroupes = " + queryIdForGroupes);
                GenericRawResults<String[]> rawResults =
                        contextDB.ficheDao.queryRaw(queryIdForGroupes.toString());
                Set<Integer> ficheIdForGroupeList = new HashSet<Integer>();
                for (String[] resultColumns : rawResults) {
                    String iDString = resultColumns[0];
                    ficheIdForGroupeList.add(Integer.parseInt(iDString));
                }
                // conserve uniquement les id des fiches qui sont dans les 2 filtres
                List<Integer> groupFilteredFicheIdList = new ArrayList<Integer>();
                for (Integer i : ficheIdList) {
                    if (ficheIdForGroupeList.contains(i)) groupFilteredFicheIdList.add(i);
                }
                // remplace la liste par celle filtrée pour les groupes aussi
                ficheIdList = groupFilteredFicheIdList;
            }
            filteredFicheIdList = ficheIdList;


        } catch (java.sql.SQLException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return filteredFicheIdList;
    }

    public MajListeFichesType getMajListeFichesTypeZoneGeo(ZoneGeographiqueKind inZoneGeo) {
        switch (inZoneGeo) {
            case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_france, "M1"));
            case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_eaudouce, "M1"));
            case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_indopac, "M1"));
            case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_caraibes, "M1"));
            case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                return MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_atlantno, "M1"));
            default:
                return null;
        }
    }

    public boolean isMajListeFichesTypeOnlyP0() {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getPrecharMode() - Début" );
        if (MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_france, "M1")) == MajListeFichesType.M0
                && MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_eaudouce, "M1")) == MajListeFichesType.M0
                && MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_atlantno, "M1")) == MajListeFichesType.M0
                && MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_indopac, "M1")) == MajListeFichesType.M0
                && MajListeFichesType.valueOf(paramOutils.getParamString(R.string.pref_key_maj_liste_fiches_region_caraibes, "M1")) == MajListeFichesType.M0
        ) return true;
        return false;
    }

    public Calendar getDerniereMajListeFichesTypeZoneGeo(ZoneGeographiqueKind inZoneGeo) {
        String dernierMajDateCaract = "";
        switch (inZoneGeo) {
            case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_france, "01-01-2000");
                break;
            case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_eaudouce, "01-01-2000");
                break;
            case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_indopac, "01-01-2000");
                break;
            case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_caraibes, "01-01-2000");
                break;
            case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                dernierMajDateCaract = paramOutils.getParamString(R.string.pref_key_datedermaj_fiches_atlantno, "01-01-2000");
                break;
            default:
                dernierMajDateCaract = "01-01-2999";
        }

        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "getDerniereMajListeFichesTypeZoneGeo() - dernierMajDateCaract : "+dernierMajDateCaract );

        Calendar dernierMajDate = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        try {
            dernierMajDate.setTime(sdf.parse(dernierMajDateCaract));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dernierMajDate;
    }

    public boolean setDateMajListeFichesTypeZoneGeo(ZoneGeographiqueKind inZoneGeo) {
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setDateMajListeFichesTypeZoneGeo() - Début" );
        Calendar aujourdHui = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        String dateMajCaract = sdf.format(aujourdHui.getTime());
        //if (BuildConfig.DEBUG) Log.d(LOG_TAG, "setDateMajListeFichesTypeZoneGeo() - dateMajCaract : "+dateMajCaract );

        switch (inZoneGeo) {
            case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                paramOutils.setParamString(R.string.pref_key_datedermaj_fiches_france, dateMajCaract);
                return true;
            case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                paramOutils.setParamString(R.string.pref_key_datedermaj_fiches_eaudouce, dateMajCaract);
                return true;
            case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                paramOutils.setParamString(R.string.pref_key_datedermaj_fiches_indopac, dateMajCaract);
                return true;
            case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                paramOutils.setParamString(R.string.pref_key_datedermaj_fiches_caraibes, dateMajCaract);
                return true;
            case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                paramOutils.setParamString(R.string.pref_key_datedermaj_fiches_atlantno, dateMajCaract);
                return true;
            default:
                return false;
        }

    }

    public int getNbJoursDerMajListeFichesTypeZoneGeo(ZoneGeographiqueKind inZoneGeo) {
        Calendar jourDerniereMaj = getDerniereMajListeFichesTypeZoneGeo(inZoneGeo);
        Calendar aujourdHui = Calendar.getInstance();
        aujourdHui.getTime();

        return ((int) (aujourdHui.getTimeInMillis() - jourDerniereMaj.getTimeInMillis()) / (24 * 60 * 60 * 1000));

    }

    public boolean isMajNecessaireZone(ZoneGeographiqueKind zoneKind, TypeLancement_kind typeLancement) {
        // M0 : jamais
        // M1 : sur demande
        // M2 : chaque semaine
        // M3 : chaque jour

        MajListeFichesType majListeFichesType = getMajListeFichesTypeZoneGeo(zoneKind);

        // Si MO : jamais de Maj
        if (majListeFichesType == MajListeFichesType.M0) return false;

        // Si lancé Manuellement MAJ
        if (typeLancement == TypeLancement_kind.MANUEL) return true;

        int nbJours = getNbJoursDerMajListeFichesTypeZoneGeo(zoneKind);

        // Si M2 : chaque semaine
        if (majListeFichesType == MajListeFichesType.M2 && nbJours > 7) return true;

        // Si M3 : chaque jour
        if (majListeFichesType == MajListeFichesType.M3 && nbJours > 1) return true;

        return false;
    }

    public String getZoneIcone(ZoneGeographiqueKind inZoneGeo) {
        switch (inZoneGeo) {
            case FAUNE_FLORE_TOUTES_ZONES:
                return context.getString(R.string.icone_touteszones);
            case FAUNE_FLORE_MARINES_FRANCE_METROPOLITAINE:
                return context.getString(R.string.icone_france);
            case FAUNE_FLORE_DULCICOLES_FRANCE_METROPOLITAINE:
                return context.getString(R.string.icone_eaudouce);
            case FAUNE_FLORE_MARINES_DULCICOLES_INDO_PACIFIQUE:
                return context.getString(R.string.icone_indopac);
            case FAUNE_FLORE_SUBAQUATIQUES_CARAIBES:
                return context.getString(R.string.icone_caraibes);
            case FAUNE_FLORE_DULCICOLES_ATLANTIQUE_NORD_OUEST:
                return context.getString(R.string.icone_atlantno);
            case FAUNE_FLORE_TERRES_ANTARCTIQUES_FRANCAISES:
                return context.getString(R.string.icone_antarctique);
            case FAUNE_FLORE_MER_ROUGE:
                return context.getString(R.string.icone_merrouge);
            case FAUNE_FLORE_MEDITERRANEE_FRANCAISE:
                return context.getString(R.string.icone_mediter);
            case FAUNE_FLORE_FACADE_ATLANTIQUE_FRANCAISE:
                return context.getString(R.string.icone_atlantne);
            case FAUNE_FLORE_GUYANNE:
                return context.getString(R.string.icone_guyanne);
            case FAUNE_FLORE_HABITAT:
                return context.getString(R.string.icone_habitat);
            default:
                return "";
        }
    }

    public int getZoneIconeId(ZoneGeographiqueKind inZoneGeo) {
        return context.getResources().getIdentifier(getZoneIcone(inZoneGeo), null, context.getPackageName());
    }

    public Fiche getFicheForId(Integer ficheId) {
        try {
            return ormLiteDBHelper.getDorisDBHelper().ficheDao.queryForId(ficheId);
        } catch (SQLException e1) {
            Log.e(LOG_TAG, "Cannot retreive fiche with _id = " + ficheId + " " + e1.getMessage(), e1);
            return null;
        }
    }

    public OrdreTri getOrdreTri(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String ordreTri = prefs.getString(context.getString(R.string.pref_key_accueil_fiches_ordre), "Commun");
        if (ordreTri.equals("Commun")) return OrdreTri.NOMCOMMUN;
        else return OrdreTri.NOMSCIENTIFIQUE;
    }


}
