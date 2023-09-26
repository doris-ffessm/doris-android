package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;

import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.tools.Fiches_Outils;

public class FicheIndexManager extends IndexManager<Fiche>{
    private static final String LOG_TAG = FicheIndexManager.class.getCanonicalName();

    protected DorisDBHelper _contextDB;

    protected Fiches_Outils.OrdreTriAlphabetique ordreTriAlphabetique = Fiches_Outils.OrdreTriAlphabetique.NOMCOMMUN;

    public FicheIndexManager(Context context, DorisDBHelper _contextDB) {
        super(context);
        this._contextDB = _contextDB;

        Fiches_Outils fichesOutils = new Fiches_Outils(context);
        ordreTriAlphabetique = fichesOutils.getOrdreTriAlphabetique(context);
    }

    @Override
    public char getFirstCharForIndex(Fiche entry) {
        String nom;
        switch (ordreTriAlphabetique) {
            case NOMSCIENTIFIQUE:
                nom = entry.getNomScientifique().replaceFirst("\\{\\{i}}", "");
                break;
            case NOMCOMMUN:
            default:
                nom = entry.getNomCommunNeverEmpty();
                break;
        }
        if (nom.length() == 0) return '#';
        return nom.charAt(0);
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
