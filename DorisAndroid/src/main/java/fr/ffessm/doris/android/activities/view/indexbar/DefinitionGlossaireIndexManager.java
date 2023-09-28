package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.tools.Fiches_Outils;

public class DefinitionGlossaireIndexManager extends IndexManager<DefinitionGlossaire, Character>{
    private static final String LOG_TAG = DefinitionGlossaireIndexManager.class.getCanonicalName();

    protected DorisDBHelper _contextDB;

    protected Fiches_Outils.OrdreTriAlphabetique ordreTriAlphabetique = Fiches_Outils.OrdreTriAlphabetique.NOMCOMMUN;

    public DefinitionGlossaireIndexManager(Context context, DorisDBHelper _contextDB) {
        super(context, AlphabetProvider.getAlphabet(context));
        this._contextDB = _contextDB;

        Fiches_Outils fichesOutils = new Fiches_Outils(context);
        ordreTriAlphabetique = fichesOutils.getOrdreTriAlphabetique(context);
    }

    @Override
    public Character getIndexKeyForEntry(DefinitionGlossaire entry) {
        return entry.getTerme().charAt(0);
    }

    @Override
    public DefinitionGlossaire getItemForId(Integer itemId) {

            DorisApplicationContext appContext = DorisApplicationContext.getInstance();
            DefinitionGlossaire d = appContext.glossaireCache.get(itemId);
            if (d != null) return d;
            try {
                d = _contextDB.definitionGlossaireDao.queryForId(itemId);
                appContext.glossaireCache.put(itemId, d);
                if (_contextDB != null) d.setContextDB(_contextDB);
                return d;
            } catch (SQLException e1) {
                Log.e(LOG_TAG, "Cannot retrieve DefinitionGlossaire with _id = " + itemId + " " + e1.getMessage(), e1);
                return null;
            }
    }
}
