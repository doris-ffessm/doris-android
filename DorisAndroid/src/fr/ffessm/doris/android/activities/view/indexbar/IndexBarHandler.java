package fr.ffessm.doris.android.activities.view.indexbar;

import java.util.ArrayList;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import fr.ffessm.doris.android.R;

public class IndexBarHandler extends Handler {

	ActivityWithIndexBar indexbar;
	static String TAG="IndexBarHandler";
	int height_of_listview=0;
	int height_of_alphabet_row=0;
	int number_of_characters_shown=0;
	int number_of_characters_omit=0;
	int height_of_one_dot=0;
	
	/**
	 * Constructor of IndexBarHandler class which reserves the instance reference of its UI class
	 * @param indexbar
	 */
	public IndexBarHandler(ActivityWithIndexBar indexbar) {
		this.indexbar=indexbar;
	}
	
	public void handleMessage(Message msg) {
		if (msg.what == 1) {
			Log.d(TAG,"IndexBarHandler called. Custom Listview is created. Now initializing Alphabet IndexBar Scroller Listview.");
			/*Before creating the Index Bar Listview we need to determine the number of alphabets 
			 * to be shown in this listview.
			 */

			height_of_listview	=	(Integer)msg.obj;			//Height of the ListView in pixels
			
			//View view=indexbar.findViewById(R.id.alphabet_row_layout);
			View view=indexbar.getAlphabetRowView();
			
			if(view !=null && view instanceof TextView){
				 TextView row=(TextView)view;
				 height_of_alphabet_row=row.getHeight();			//Height of the row.
				 indexbar.getSharedPreferences("AndroidIndexBar", Context.MODE_PRIVATE).edit().putInt("height", height_of_alphabet_row).commit();
				 view.setVisibility(View.GONE);
			}
			else
				return;
			Log.d(TAG,"Height of List= "+height_of_listview+"    height of one row="+height_of_alphabet_row);
			number_of_characters_shown=height_of_listview/height_of_alphabet_row; 				//Number of Characters to be shown
			number_of_characters_omit=26-number_of_characters_shown;							//Number of Characters to be omited	
			Log.d(TAG,"Number of Characters="+number_of_characters_shown+"  omits="+number_of_characters_omit);
			
			
			//Alphabets listview is populated
			// TODO triuver un moyen indenpendant de la vue
			
			//ListView alphabets_listview=(ListView)indexbar.findViewById(R.id.listeficheavecfiltre_listView_alphabets);
			ListView alphabets_listview=indexbar.getAlphabetListView();
			/*Populating the Alphabet List*/			
			ArrayList<String> alphabets_list=new ArrayList<String>();
			String alphabet_array[]    =	indexbar.getResources().getStringArray(R.array.alphabtes_array);
			for(int i=0; i<26 ; i++){
				alphabets_list.add(alphabet_array[i]);
			}
			/*Main Task*/	
			prepareArray(alphabets_list);
			
			ArrayAdapter<String> alphabets_adapter=	new AlphabetListviewAdapter(indexbar.getBaseContext(),R.layout.indexbar_alphabtes_row,alphabets_list, number_of_characters_omit);
			alphabets_listview.setAdapter(alphabets_adapter);
			alphabets_listview.setOnItemClickListener((OnItemClickListener) indexbar);
		}
	}

	private void prepareArray(ArrayList<String> alphabets_list) {
		
		height_of_one_dot=convertDipToPx(9, indexbar.getBaseContext());
		Log.d(TAG,"Height of one dot i.e. 9dp= "+height_of_one_dot+" px");  //9dp=13px
		
				
		if(number_of_characters_omit==1 || number_of_characters_omit==2 ){
			//adjusted the height of the row
		}
		if(number_of_characters_omit<=4){					//Two groups shown. For 3,4 characters remove 4,5 characters
		
			removeAlphabet(alphabets_list, 5, 2);		//F,G
			alphabets_list.add(5, ".");	
			
			removeAlphabet(alphabets_list, 17, 2);		//S,T
			
			if(number_of_characters_omit==4)
				alphabets_list.remove(17);				//U
			alphabets_list.add(17, ".");
			
		}
		if(number_of_characters_omit>4 & number_of_characters_omit<=6){  			//5,6
			
			int total_height_dots=height_of_one_dot*3;					
			float num_eliminate=(float)total_height_dots/height_of_alphabet_row;
			int eliminate=(int)Math.round(num_eliminate+0.5f );
			Log.d(TAG,"For 3 dots, eliminate= "+eliminate+" more characters"+"  (num_eliminate="+num_eliminate+")");
			
			if(eliminate==0)	//there would be atleast one omitance. If there is any error, it is explicitly done.
				eliminate=1;
			
			if((number_of_characters_omit==5 && eliminate==2) || (number_of_characters_omit==6 && eliminate==1))  {
				
			}
			removeAlphabet(alphabets_list, 5, 2);		//F,G
			alphabets_list.add(5, ".");	

			alphabets_list.remove(13);  				//O
			if(number_of_characters_omit==6)
				alphabets_list.remove(13);				//P			
			alphabets_list.add(13, ".");
			
			if(number_of_characters_omit==5){
				removeAlphabet(alphabets_list, 22, 2);	//X,Y
				alphabets_list.add(22, ".");
			}else{
				removeAlphabet(alphabets_list, 21, 2);
				alphabets_list.add(21, ".");
			}
				
			if(eliminate==1)
				alphabets_list.remove(14);		//P  / Q
			else if(eliminate==2){
				if(number_of_characters_omit==5){
					removeAlphabet(alphabets_list, 14, 2);		//P,Q
				}
				else{
					alphabets_list.remove(6);		//H
					alphabets_list.remove(13);		//Q 
				}
			}
			else{
				if(number_of_characters_omit==5){
					alphabets_list.remove(6);		//H
					removeAlphabet(alphabets_list, 13, 2);		//P,Q
				}
				else{
					alphabets_list.remove(6);		//H
					alphabets_list.remove(13);		//Q
					alphabets_list.remove(18);		//W
				}
			}
		}
		else if(number_of_characters_omit>6 & number_of_characters_omit<=8){
			
			int total_height_dots=height_of_one_dot*4;					
			float num_eliminate=(float)total_height_dots/height_of_alphabet_row;
			int eliminate=(int)Math.round(num_eliminate+0.5f );
			Log.d(TAG,"For 4 dots, eliminate= "+eliminate+" rows"+"  (num_eliminate="+num_eliminate+")");
			
			if(eliminate==0)	//there would be atleast one omitance. If there is any error, it is explicitly done.
				eliminate=1;
			if( (number_of_characters_omit==7 && eliminate==2) || (number_of_characters_omit==8 && eliminate==1)){
					
				removeAlphabet(alphabets_list, 2, 3);	//C,D,E
				alphabets_list.add(2,".");		
				
				removeAlphabet(alphabets_list, 7, 2);	//J,K
				alphabets_list.add(7,".");		
				
				removeAlphabet(alphabets_list, 13, 2);	//Q,R
				alphabets_list.add(13,".");		
				
				removeAlphabet(alphabets_list, 19, 2);	//X,Y
				alphabets_list.add(19,".");	
			}
			else if( (number_of_characters_omit==7 && eliminate==3) || (number_of_characters_omit==8 && eliminate==2)){
				removeAlphabet(alphabets_list, 2, 3);		//C,D,E
				alphabets_list.add(2,".");		
				
				removeAlphabet(alphabets_list, 6, 3);		//I,J,K
				alphabets_list.add(6,".");		
				
				removeAlphabet(alphabets_list, 12, 2);		//Q,R		
				alphabets_list.add(12,".");		
				
				removeAlphabet(alphabets_list, 18, 2);		//X,Y		
				alphabets_list.add(18,".");	
			}
			else if( (number_of_characters_omit==7 && eliminate==4) || (number_of_characters_omit==8 && eliminate==3)){
				removeAlphabet(alphabets_list, 2, 3);		//C,D,E		
				alphabets_list.add(2,".");		
				
				removeAlphabet(alphabets_list, 6, 3);		//I,J,K
				alphabets_list.add(6,".");		
				
				removeAlphabet(alphabets_list, 11, 3);		//P,Q,R		
				alphabets_list.add(11,".");		
				
				removeAlphabet(alphabets_list, 17, 2);		//X,Y
				alphabets_list.add(17,".");	
			}
			else if( number_of_characters_omit==7 && eliminate==1){
				
				removeAlphabet(alphabets_list, 3, 2);	//D,E		
				alphabets_list.add(3,".");		
				
				removeAlphabet(alphabets_list, 8, 2);	//J,K		
				alphabets_list.add(8,".");
				
				removeAlphabet(alphabets_list, 14, 2);	//Q,R		
				alphabets_list.add(14,".");
				
				removeAlphabet(alphabets_list, 20, 2);	//X,Y		
				alphabets_list.add(20,".");	
			}
			else if( number_of_characters_omit==8 && eliminate==4){
						
				removeAlphabet(alphabets_list, 2, 3);	//C,D,E		
				alphabets_list.add(2,".");		

				removeAlphabet(alphabets_list, 6, 3);	//I,J,K	
				alphabets_list.add(6,".");	
				
				removeAlphabet(alphabets_list, 11, 3);		
				alphabets_list.add(11,".");		
				
				removeAlphabet(alphabets_list, 16, 3);		//W,X,Y		
				alphabets_list.add(16,".");	
			}
		}
	
	}
	
	public int convertDipToPx(int dp, Context context){	//10dp=15px
		Resources r = context.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
		return (int)px;
	}
	/**
	 * This method eliminates the character on remove_position from ArrayList list for iterations number of times.
	 * @param list
	 * @param remove_position
	 * @param iterations
	 */
	private void removeAlphabet(ArrayList<String>list, int remove_position, int iterations){
		if(list==null || remove_position>list.size()  || iterations==0)
			return;
		
		for (int i=0; i<iterations; i++){
			list.remove(remove_position);
		}
	}
	
}
