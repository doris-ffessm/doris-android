package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.DorisDBHelper;
import fr.ffessm.doris.android.datamodel.Fiche;
import fr.ffessm.doris.android.tools.Fiches_Outils;

public class IndexHelper {
    private static final String LOG_TAG = IndexHelper.class.getCanonicalName();

    protected DorisDBHelper _contextDB;
    protected Context context;

    protected Fiches_Outils.OrdreTriAlphabetique ordreTriAlphabetique = Fiches_Outils.OrdreTriAlphabetique.NOMCOMMUN;

    public IndexHelper(Context context, DorisDBHelper _contextDB) {
        this._contextDB = _contextDB;
        this.context = context;

        Fiches_Outils fichesOutils = new Fiches_Outils(context);
        ordreTriAlphabetique = fichesOutils.getOrdreTriAlphabetique(context);
    }

    public  HashMap<Character, Integer> getUsedAlphabetHashMap(List<Integer> filteredFicheIdList) {
        HashMap<Character, Integer> alphabetToIndex = new HashMap<>();
        Log.d(LOG_TAG, "getUsedAlphabetHashMap - début");
        int base_list_length = filteredFicheIdList.size();
        if (base_list_length < 100) {
            // the base has been filtered so return the element from the filtered one
            alphabetToIndex = new HashMap<>();


            for (int i = 0; i < base_list_length; i++) {
                Fiche entry = getFicheForId(filteredFicheIdList.get(i));
                char firstCharacter = getFirstCharForIndex(entry);
                boolean presentOrNot = alphabetToIndex.containsKey(firstCharacter);
                if (!presentOrNot) {
                    alphabetToIndex.put(firstCharacter, i);
                    //Log.d(TAG,"Character="+firstCharacter+"  position="+i);
                }
            }

        } else {
            // large list
            // use binarysearch if large list
            String alphabet_list[] = context.getResources().getStringArray(R.array.alphabet_array);
            int startSearchPos = 0;
            for (String s : alphabet_list) {
                int foundPosition = binarySearch(filteredFicheIdList, s.charAt(0), startSearchPos, base_list_length - 1);
                if (foundPosition != -1) {
                    alphabetToIndex.put(s.charAt(0), foundPosition);
                    startSearchPos = foundPosition; // mini optimisation, no need to look before for former chars
                }
            }
        }
        Log.d(LOG_TAG, "getUsedAlphabetHashMap - fin");
        return alphabetToIndex;
    }

    protected char getFirstCharForIndex(Fiche entry) {
        //Start of user code protected ListeFicheAvecFiltre_Adapter binarySearch custom
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
        //End of user code
    }

    /**
     * @param filteredFicheIdList  the list of Fiche Ids in which the search occurs
     * @param key         to be searched
     * @param startBottom initial value for bottom, default = 0
     * @param startTop    initial top value, default = array.length -1
     * @return
     */
    public int binarySearch(List<Integer> filteredFicheIdList, char key, int startBottom, int startTop) {
        int bot = startBottom;
        int top = startTop;
        int mid = startBottom;
        boolean found = false;
        while (bot <= top) {
            mid = bot + (top - bot) / 2;
            Fiche entry = getFicheForId(filteredFicheIdList.get(mid));
            char midCharacter = getFirstCharForIndex(entry);
            if (key < midCharacter) top = mid - 1;
            else if (key > midCharacter) bot = mid + 1;
            else {
                found = true;
                break;
            }
        }
        if (found) {
            // search for the first occurrence
            int best = mid;
            for (int i = mid; i > startBottom; i--) {
                Fiche entry = getFicheForId(filteredFicheIdList.get(i));
                char midCharacter = getFirstCharForIndex(entry);
                if (midCharacter == key) {
                    best = i;
                } else {
                    //previous is differents so we stop here
                    break;
                }

            }
            return best;
        } else return -1;
    }

    /**
     * USe a cache of fiche in the ApplicationContext
     * @param ficheId Id of the fiche we are looking for
     * @return the Fiche that has the given ID
     */
    public Fiche getFicheForId(Integer ficheId) {
        DorisApplicationContext appContext = DorisApplicationContext.getInstance();
        Fiche f = appContext.ficheCache.get(ficheId);
        if (f != null) return f;
        try {
            f = _contextDB.ficheDao.queryForId(ficheId);
            appContext.ficheCache.put(ficheId, f);
            if (_contextDB != null) f.setContextDB(_contextDB);
            return f;
        } catch (SQLException e1) {
            Log.e(LOG_TAG, "Cannot retreive fiche with _id = " + ficheId + " " + e1.getMessage(), e1);
            return null;
        }
    }
}
