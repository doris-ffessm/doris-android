package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.tools.Fiches_Outils;

public class FicheGroupeIndexManager extends IndexManager<Fiche, Integer> {
    private static final String LOG_TAG = FicheGroupeIndexManager.class.getCanonicalName();

    protected DorisDBHelper _contextDB;

    public FicheGroupeIndexManager(Context context, DorisDBHelper _contextDB, Integer groupFilter) {
        super(context, GroupeListProvider.getFilteredGroupeIdList(_contextDB, groupFilter));
        this._contextDB = _contextDB;
    }

    @Override
    public Integer getIndexKeyForEntry(Fiche entry) {
        return entry.getGroupe().getId();
    }

    @Override
    public Fiche getItemForId(Integer itemId) {
            DorisApplicationContext appContext = DorisApplicationContext.getInstance();
            Fiche f = appContext.ficheCache.get(itemId);
            if (f != null) return f;
            try {
                f = _contextDB.ficheDao.queryForId(itemId);
                appContext.ficheCache.put(itemId, f);
                if (_contextDB != null) f.setContextDB(_contextDB);
                return f;
            } catch (SQLException e1) {
                Log.e(LOG_TAG, "Cannot retrieve fiche with _id = " + itemId + " " + e1.getMessage(), e1);
                return null;
            }
    }
}
