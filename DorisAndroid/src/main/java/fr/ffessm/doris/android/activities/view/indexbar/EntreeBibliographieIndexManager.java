package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.EntreeBibliographie;
import fr.ffessm.doris.android.tools.Fiches_Outils;

public class EntreeBibliographieIndexManager extends IndexManager<EntreeBibliographie, Character>{
    private static final String LOG_TAG = EntreeBibliographieIndexManager.class.getCanonicalName();

    protected DorisDBHelper _contextDB;


    public EntreeBibliographieIndexManager(Context context, DorisDBHelper _contextDB) {
        super(context, AlphabetProvider.getAlphabet(context));
        this._contextDB = _contextDB;
    }

    @Override
    public Character getIndexKeyForEntry(EntreeBibliographie entry) {
        return entry.getAuteurs().charAt(0);
    }

    @Override
    public EntreeBibliographie getItemForId(Integer itemId) {

            DorisApplicationContext appContext = DorisApplicationContext.getInstance();
        EntreeBibliographie d = appContext.bibliographieCache.get(itemId);
            if (d != null) return d;
            try {
                d = _contextDB.entreeBibliographieDao.queryForId(itemId);
                appContext.bibliographieCache.put(itemId, d);
                if (_contextDB != null) d.setContextDB(_contextDB);
                return d;
            } catch (SQLException e1) {
                Log.e(LOG_TAG, "Cannot retrieve EntreeBibliographie with _id = " + itemId + " " + e1.getMessage(), e1);
                return null;
            }
    }
}
