package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.AbstractWebNodeObject;


/**
 * Helper class to manage Index
 * @param <ItemType>
 */

public abstract class IndexManager<ItemType extends AbstractWebNodeObject, IndexKeyType extends Comparable> {
    private static final String LOG_TAG = IndexManager.class.getCanonicalName();
    protected Context context;

    List<IndexKeyType> indexList;

    public IndexManager(Context context, List<IndexKeyType> indexList) {
        this.context = context;
        this.indexList = indexList;
    }


    public  HashMap<IndexKeyType, Integer> getUsedIndexHashMapFromItems(List<ItemType> filteredItemList) {
        HashMap<IndexKeyType, Integer> alphabetToIndex = new HashMap<>();
        Log.d(LOG_TAG, "getUsedAlphabetHashMap - début");
        int base_list_length = filteredItemList.size();
        if (base_list_length < 100) {
            // the base has been filtered so return the element from the filtered one
            alphabetToIndex = new HashMap<>();


            for (int i = 0; i < base_list_length; i++) {
                ItemType entry = filteredItemList.get(i);
                IndexKeyType firstCharacter = getIndexKeyForEntry(entry);
                boolean presentOrNot = alphabetToIndex.containsKey(firstCharacter);
                if (!presentOrNot) {
                    alphabetToIndex.put(firstCharacter, i);
                    //Log.d(TAG,"Character="+firstCharacter+"  position="+i);
                }
            }

        } else {
            // large list
            // use binarysearch if large list
            //String alphabet_list[] = context.getResources().getStringArray(R.array.alphabet_array);
            int startSearchPos = 0;
            for (int i = 0; i < indexList.size(); i++) {
                int foundPosition = binarySearch(filteredItemList, indexList.get(0), startSearchPos, base_list_length - 1);
                if (foundPosition != -1) {
                    alphabetToIndex.put(indexList.get(i), foundPosition);
                    startSearchPos = foundPosition; // mini optimisation, no need to look before for former chars
                }
            }
        }
        Log.d(LOG_TAG, "getUsedAlphabetHashMap - fin");
        return alphabetToIndex;

    }
    public  HashMap<IndexKeyType, Integer> getUsedIndexHashMapItemIds(List<Integer> filteredFicheIdList) {
        HashMap<IndexKeyType, Integer> alphabetToIndex = new HashMap<>();
        Log.d(LOG_TAG, "getUsedAlphabetHashMap - début");
        int base_list_length = filteredFicheIdList.size();
        if (base_list_length < 100) {
            // the base has been filtered so return the element from the filtered one
            alphabetToIndex = new HashMap<>();


            for (int i = 0; i < base_list_length; i++) {
                ItemType entry = getItemForId(filteredFicheIdList.get(i));
                IndexKeyType firstCharacter = getIndexKeyForEntry(entry);
                boolean presentOrNot = alphabetToIndex.containsKey(firstCharacter);
                if (!presentOrNot) {
                    alphabetToIndex.put(firstCharacter, i);
                    //Log.d(TAG,"Character="+firstCharacter+"  position="+i);
                }
            }

        } else {
            // large list
            // use binarysearch if large list
            //String alphabet_list[] = context.getResources().getStringArray(R.array.alphabet_array);
            int startSearchPos = 0;
            for (IndexKeyType s : this.indexList) {
                int foundPosition = binarySearchId(filteredFicheIdList, s, startSearchPos, base_list_length - 1);
                if (foundPosition != -1) {
                    alphabetToIndex.put(s, foundPosition);
                    startSearchPos = foundPosition; // mini optimisation, no need to look before for former chars
                }
            }
        }
        Log.d(LOG_TAG, "getUsedAlphabetHashMap - fin");
        return alphabetToIndex;
    }

    public abstract IndexKeyType getIndexKeyForEntry(ItemType entry);

    /**
     * @param filteredFicheIdList  the list of Fiche Ids in which the search occurs
     * @param key         to be searched
     * @param startBottom initial value for bottom, default = 0
     * @param startTop    initial top value, default = array.length -1
     * @return
     */
    public int binarySearchId(List<Integer> filteredFicheIdList, IndexKeyType key, int startBottom, int startTop) {
        int bot = startBottom;
        int top = startTop;
        int mid = startBottom;
        boolean found = false;
        while (bot <= top) {
            mid = bot + (top - bot) / 2;
            ItemType entry = getItemForId(filteredFicheIdList.get(mid));
            IndexKeyType midCharacter = getIndexKeyForEntry(entry);
            if (key.compareTo(midCharacter) < 0) top = mid - 1;
            else if (key.compareTo(midCharacter) > 0) bot = mid + 1;
            else {
                found = true;
                break;
            }
        }
        if (found) {
            // search for the first occurrence
            int best = mid;
            for (int i = mid; i > startBottom; i--) {
                ItemType entry = getItemForId(filteredFicheIdList.get(i));
                IndexKeyType midCharacter = getIndexKeyForEntry(entry);
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

    public int binarySearch(List<ItemType> filteredItemList, IndexKeyType key, int startBottom, int startTop) {
        int bot = startBottom;
        int top = startTop;
        int mid = startBottom;
        boolean found = false;
        while (bot <= top) {
            mid = bot + (top - bot) / 2;
            ItemType entry = filteredItemList.get(mid);
            IndexKeyType midCharacter = getIndexKeyForEntry(entry);
            if (key.compareTo(midCharacter) < 0 ) top = mid - 1;
            else if (key.compareTo(midCharacter) > 0) bot = mid + 1;
            else {
                found = true;
                break;
            }
            ;
        }
        if (found) {
            // search for the first occurence
            int best = mid;
            for (int i = mid; i > startBottom; i--) {
                ItemType entry = filteredItemList.get(i);
                IndexKeyType midCharacter = getIndexKeyForEntry(entry);
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
     * @param itemId Id of the fiche we are looking for
     * @return the Fiche that has the given ID
     */
    public abstract ItemType getItemForId(Integer itemId);
}
