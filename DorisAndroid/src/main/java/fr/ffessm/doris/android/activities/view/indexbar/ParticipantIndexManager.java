package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.tools.Fiches_Outils;

public class ParticipantIndexManager extends IndexManager<Participant, Character>{
    private static final String LOG_TAG = ParticipantIndexManager.class.getCanonicalName();

    protected DorisDBHelper _contextDB;

    protected Fiches_Outils.OrdreTriAlphabetique ordreTriAlphabetique = Fiches_Outils.OrdreTriAlphabetique.NOMCOMMUN;

    public ParticipantIndexManager(Context context, DorisDBHelper _contextDB) {

        super(context, AlphabetProvider.getAlphabet(context));
        this._contextDB = _contextDB;

        Fiches_Outils fichesOutils = new Fiches_Outils(context);
        ordreTriAlphabetique = fichesOutils.getOrdreTriAlphabetique(context);
    }

    @Override
    public Character getIndexKeyForEntry(Participant entry) {
        return entry.getNom().trim().charAt(0); // il y a un blanc au début, devrait être nettoyé dans le prefecth
    }

    @Override
    public Participant getItemForId(Integer itemId) {

            DorisApplicationContext appContext = DorisApplicationContext.getInstance();
            Participant d = appContext.participantCache.get(itemId);
            if (d != null) return d;
            try {
                d = _contextDB.participantDao.queryForId(itemId);
                appContext.participantCache.put(itemId, d);
                if (_contextDB != null) d.setContextDB(_contextDB);
                return d;
            } catch (SQLException e1) {
                Log.e(LOG_TAG, "Cannot retrieve DefinitionGlossaire with _id = " + itemId + " " + e1.getMessage(), e1);
                return null;
            }
    }
}
