package fr.ffessm.doris.android.activities.view.indexbar;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fr.ffessm.doris.android.R;

public class AlphabetProvider {
    public static List<Character> getAlphabet(Context context){
        String[]  l  = context.getResources().getStringArray(R.array.alphabet_array);
        List<Character> lc = new ArrayList<Character>();
        for (int i = 0; i < l.length; i++) {
            lc.add(l[i].charAt(0));
        }
        return lc;
    }
}
