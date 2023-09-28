package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.tools.Groupes_Outils;

public class GroupeListProvider {
    private static final String LOG_TAG = FicheGroupeIndexManager.class.getCanonicalName();
    public static List<Groupe> getAllGroupeList(DorisDBHelper contextDB){
        List<Groupe> allGroups = Groupes_Outils.getAllGroupes(contextDB);
        return allGroups;
    }
    public static List<Integer> getAllGroupeIdList(DorisDBHelper contextDB){
        List<Groupe> allGroups = getAllGroupeList(contextDB);
        List<Integer> ids = new ArrayList<>();
        for (Groupe g : allGroups) {
            ids.add(g.getId());
        }
        return ids;
    }

    public static List<Groupe> getFilteredGroupeList(DorisDBHelper contextDB, int filteredGroupeId){
        List<Groupe> groups;
        if (filteredGroupeId == 1) { // not filtered
            groups = Groupes_Outils.getAllGroupes(contextDB);
        } else {
            Groupe searchedGroupe = null;
            try {
                searchedGroupe = contextDB.groupeDao.queryForId(filteredGroupeId);
                searchedGroupe.setContextDB(contextDB);
                return Groupes_Outils.getAllSubGroupesForGroupe(searchedGroupe);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                Log.e(LOG_TAG, "Cannot retrieve groupe with _id = " + filteredGroupeId + " " + throwables.getMessage(), throwables);
                groups = Groupes_Outils.getAllGroupes(contextDB);
            }
        }
        return groups;
    }
    public static List<Integer> getFilteredGroupeIdList(DorisDBHelper contextDB, int filteredGroupeId){
        List<Groupe> filteredGroups = getFilteredGroupeList(contextDB, filteredGroupeId);
        List<Integer> ids = new ArrayList<>();
        for (Groupe g : filteredGroups) {
            ids.add(g.getId());
        }
        return ids;
    }
}
