package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.datamodel.Groupe;
import fr.ffessm.doris.android.tools.Fiches_Outils;

public class GroupeIndexManager extends IndexManager<Groupe, Integer> {
    private static final String LOG_TAG = GroupeIndexManager.class.getCanonicalName();

    protected DorisDBHelper _contextDB;


    public GroupeIndexManager(Context context, DorisDBHelper _contextDB, Integer groupFilter) {
        super(context, GroupeListProvider.getFilteredGroupeIdList(_contextDB, groupFilter));
        this._contextDB = _contextDB;
    }


    @Override
    public Integer getIndexKeyForEntry(Groupe entry) {
        return entry.getId();
    }

    @Override
    public Groupe getItemForId(Integer itemId) {
            DorisApplicationContext appContext = DorisApplicationContext.getInstance();
            Groupe f = appContext.groupeCache.get(itemId);
            if (f != null) return f;
            try {
                f = _contextDB.groupeDao.queryForId(itemId);
                appContext.groupeCache.put(itemId, f);
                if (_contextDB != null) f.setContextDB(_contextDB);
                return f;
            } catch (SQLException e1) {
                Log.e(LOG_TAG, "Cannot retrieve groupe with _id = " + itemId + " " + e1.getMessage(), e1);
                return null;
            }
    }
}
