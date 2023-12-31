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

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;

public class Groupes_Outils {

    public static final String LOG_TAG = Groupes_Outils.class.getSimpleName();

    public static List<Groupe> getAllGroupes(DorisDBHelper _contextDB) {

        List<Groupe> groupeList;

        try {
            groupeList = _contextDB.groupeDao.queryForAll();
            for (Groupe groupe : groupeList) {
                groupe.setContextDB(_contextDB);
            }
            return groupeList;
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return new ArrayList<>();
        }

    }

    public static Groupe getGroupeFromId(List<Groupe> allGroupes, int _id) {
        Groupe groupeFromId = null;
        for (Groupe groupe : allGroupes) {
            if (groupe.getId() == _id) {
                groupeFromId = groupe;
                break;
            }
        }
        return groupeFromId;
    }

    public static Groupe getGroupeRoot(DorisDBHelper contextDB) {
        return getGroupeRoot(getAllGroupes(contextDB));
    }

    public static Groupe getGroupeRoot(List<Groupe> allGroupes) {
        // trouve le groupe racine
        Groupe rootGroupe = null;
        for (Groupe groupe : allGroupes) {
            if (groupe.getNomGroupe().equals("racine")) {
                rootGroupe = groupe;
                break;
            }
        }
        return rootGroupe;
    }

    public static ArrayList<Groupe> getAllGroupesEnfantsJusquAuNiveau(List<Groupe> allGroupes, ArrayList<Groupe> groupesListeIn, int niveauFeuilles) {
        if (BuildConfig.DEBUG)
            Log.d(LOG_TAG, "getAllGroupesEnfantsJusquAuNiveau - groupesListeIn.size() : " + groupesListeIn.size());
        if (BuildConfig.DEBUG)
            Log.d(LOG_TAG, "getAllGroupesEnfantsJusquAuNiveau - niveauFeuilles : " + niveauFeuilles);

        ArrayList<Groupe> groupesListeOut = new ArrayList<>();

        // Trouve le groupe racine si aucun groupe passé
        if (groupesListeIn.size() == 0) {
            for (Groupe groupe : allGroupes) {
                if (groupe.getNomGroupe().equals("racine")) {
                    groupesListeIn.add(groupe);
                    break;
                }
            }
        }

        // Recherche Groupes Enfants
        for (Groupe groupeIn : groupesListeIn) {
            if (BuildConfig.DEBUG)
                Log.d(LOG_TAG, "getAllGroupesEnfantsJusquAuNiveau - groupeIn : " + groupeIn.getNomGroupe());

            Collection<Groupe> groupesFils = groupeIn.getGroupesFils();

            // Si pas de fils alors on garde le père
            if (!groupesFils.isEmpty()) {
                groupesListeOut.addAll(groupesFils);
            } else {
                groupesListeOut.add(groupeIn);
            }
        }

        //Tant que pas atteint le niveau demandé on continue pas récurrence
        if (niveauFeuilles != 1) {
            return getAllGroupesEnfantsJusquAuNiveau(allGroupes, groupesListeOut, niveauFeuilles - 1);
        }

        return groupesListeOut;
    }

    public static ArrayList<Groupe> getAllGroupesForNextLevel(ArrayList<Groupe> currentLevelGroupes) {
        ArrayList<Groupe> nextLevelGroups = new ArrayList<>();
        for (Groupe groupe : currentLevelGroupes) {
            nextLevelGroups.addAll(groupe.getGroupesFils());
        }
        return nextLevelGroups;
    }

    public static List<Groupe> getAllGroupesForNextLevel(List<Groupe> rawGroupes, Groupe rootGroupe) {
        ArrayList<Groupe> nextLevelGroups = new ArrayList<>(rootGroupe.getGroupesFils());
        return nextLevelGroups;
    }

    public static boolean isFichePartOfGroupe(Fiche fiche, Groupe searchedGroupe) {
        boolean result;
        Groupe groupeFiche = fiche.getGroupe();
        if (groupeFiche == null) {
            Log.w(LOG_TAG, "PB pas de groupe pour la fiche isFichePartOfGroupe(" + fiche.getNomCommunNeverEmpty() + " " + fiche.getId() + ", " + searchedGroupe.getNomGroupe() + ")");
            return true;
        }
        groupeFiche.setContextDB(fiche.getContextDB());
        if (groupeFiche.getId() == searchedGroupe.getId()) result = true;
        else result = isGroupePartOfGroupe(groupeFiche, searchedGroupe);
        if (BuildConfig.DEBUG)
            Log.d(LOG_TAG, "isFichePartOfGroupe(" + fiche.getNomCommunNeverEmpty() + ", " + searchedGroupe.getNomGroupe() + ") =" + result);
        return result;
    }

    public static boolean isGroupePartOfGroupe(Groupe groupe, Groupe searchedGroupe) {
        Groupe groupeParent = groupe.getGroupePere();
        if (groupeParent == null) return false;
        groupeParent.setContextDB(groupe.getContextDB());
        if (groupeParent.getId() == searchedGroupe.getId()) return true;
        else return isGroupePartOfGroupe(groupeParent, searchedGroupe);
    }

    public static ArrayList<Groupe> getAllSubGroupesForGroupe(Groupe groupe) {

        ArrayList<Groupe> subGroupes = new ArrayList<>();
        subGroupes.add(groupe);
        Collection<Groupe> directSubGroupes = groupe.getGroupesFils();
        for (Groupe subgroupe : directSubGroupes) {
            subgroupe.setContextDB(groupe.getContextDB());
            subGroupes.addAll(getAllSubGroupesForGroupe(subgroupe));
        }
        return subGroupes;
    }


    public static int getTailleGroupeFiltre(Context context, DorisDBHelper contextDB, int filteredZoneGeoId, int filteredGroupeId) {

        Fiches_Outils fichesOutils = new Fiches_Outils(context);

        return fichesOutils.getListeIdFichesFiltrees(context, contextDB, filteredZoneGeoId, filteredGroupeId).size();
    }

    /**
     * Checks that the 2 group list are the same, use groupeId to compare
     *
     * @param groupListA first group list to compare
     * @param groupListB second group list to compare
     * @return true if both are equivalent
     */
    public static boolean areEquivalentGroupLists(List<Groupe> groupListA, List<Groupe> groupListB) {
        boolean result = false;
        List<Integer> groupIdListA = new ArrayList<>();
        List<Integer> groupIdListB = new ArrayList<>();
        for (Groupe gA : groupListA) {
            groupIdListA.add(gA.getId());
        }
        for (Groupe gB : groupListB) {
            groupIdListB.add(gB.getId());
        }
        return groupIdListA.containsAll(groupIdListB) && groupIdListB.containsAll(groupIdListA);
    }
}
