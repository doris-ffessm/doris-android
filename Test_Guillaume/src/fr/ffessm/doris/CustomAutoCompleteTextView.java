package fr.ffessm.doris;
 
import java.util.HashMap;
 
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.AutoCompleteTextView;
 
/** Personnalisation d'AutoCompleteTextView qui retourne le texte
*  sélectionné
*/
public class CustomAutoCompleteTextView extends AutoCompleteTextView {
 
    private final static String TAG = "CustomAutoCompleteTextView";
    private final static Boolean LOG = false;
    
    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (LOG) Log.d(TAG, "CustomAutoCompleteTextView() - Début");
        
        
        if (LOG) Log.d(TAG, "CustomAutoCompleteTextView() - Fin");
    }
 
   
    @Override
    protected CharSequence convertSelectionToString(Object inSelectedItem) {
    	if (LOG) Log.d(TAG, "convertSelectionToString() - Début");
    	if (LOG) Log.v(TAG, "convertSelectionToString() - inSelectedItem : " + inSelectedItem.toString());
    	
    	HashMap<String, String> hm = (HashMap<String, String>) inSelectedItem;
    	if (LOG) Log.v(TAG, "convertSelectionToString() - hm.get(nom) : " + hm.get("nom"));
    	if (LOG) Log.v(TAG, "convertSelectionToString() - hm.get(icone) : " + hm.get("icone"));
    	
    	String choix = hm.get("nom");
    	if (LOG) Log.v(TAG, "convertSelectionToString() - choix : " + choix);
    	
        if (LOG) Log.d(TAG, "convertSelectionToString() - Fin");
        return choix;
    }
}