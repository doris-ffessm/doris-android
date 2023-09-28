/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
 * Copyright (c) 2012-2017 - FFESSM
 * Auteurs : Guillaume Moynard <gmo7942@gmail.com>
 *           Didier Vojtisek <dvojtise@gmail.com>
 * *********************************************************************

Ce logiciel est un programme informatique servant à afficher de manière 
ergonomique sur un terminal Android les fiches du site : doris.ffessm.fr. 

Les images, logos et textes restent la propriété de leurs auteurs, cf. : 
doris.ffessm.fr.

Ce logiciel est régi par la licence CeCILL-B soumise au droit français et
respectant les principes de diffusion des logiciels libres. Vous pouvez
utiliser, modifier et/ou redistribuer ce programme sous les conditions
de la licence CeCILL-B telle que diffusée par le CEA, le CNRS et l'INRIA 
sur le site "http://www.cecill.info".

En contrepartie de l'accessibilité au code source et des droits de copie,
de modification et de redistribution accordés par cette licence, il n'est
offert aux utilisateurs qu'une garantie limitée.  Pour les mêmes raisons,
seule une responsabilité restreinte pèse sur l'auteur du programme,  le
titulaire des droits patrimoniaux et les concédants successifs.

A cet égard  l'attention de l'utilisateur est attirée sur les risques
associés au chargement,  à l'utilisation,  à la modification et/ou au
développement et à la reproduction du logiciel par l'utilisateur étant 
donné sa spécificité de logiciel libre, qui peut le rendre complexe à 
manipuler et qui le réserve donc à des développeurs et des professionnels
avertis possédant  des  connaissances  informatiques approfondies.  Les
utilisateurs sont donc invités à charger  et  tester  l'adéquation  du
logiciel à leurs besoins dans des conditions permettant d'assurer la
sécurité de leurs systèmes et ou de leurs données et, plus généralement, 
à l'utiliser et l'exploiter dans les mêmes conditions de sécurité. 

Le fait que vous puissiez accéder à cet en-tête signifie que vous avez 
pris connaissance de la licence CeCILL-B, et que vous en avez accepté les
termes.
* ********************************************************************* */
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

public class AlphabetIndexBarHandler extends Handler {

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
	public AlphabetIndexBarHandler(ActivityWithIndexBar indexbar) {
		this.indexbar=indexbar;
	}
	
	public void handleMessage(Message msg) {
		if (msg.what == 1) {
			//Log.d(TAG,"IndexBarHandler called. Custom Listview is created. Now initializing Alphabet IndexBar Scroller Listview.");
			/*Before creating the Index Bar Listview we need to determine the number of alphabets 
			 * to be shown in this listview.
			 */

			height_of_listview	=	(Integer)msg.obj;			//Height of the ListView in pixels
			
			//View view=indexbar.findViewById(R.id.alphabet_row_layout);
			View view=indexbar.getAlphabetRowView();
			
			if(view !=null && view instanceof TextView){
				 TextView row=(TextView)view;
				 height_of_alphabet_row=row.getHeight();			//Height of the row.
				 indexbar.getSharedPreferences("AndroidIndexBar", Context.MODE_PRIVATE).edit().putInt("height", height_of_alphabet_row).apply();
				 view.setVisibility(View.GONE);
			}
			else
				return;
			//Log.d(TAG,"Height of List= "+height_of_listview+"    height of one row="+height_of_alphabet_row);
			number_of_characters_shown=height_of_listview/height_of_alphabet_row; 				//Number of Characters to be shown
			number_of_characters_omit=27-number_of_characters_shown;							//Number of Characters to be omited	
			//Log.d(TAG,"Number of Characters="+number_of_characters_shown+"  omits="+number_of_characters_omit);
			
			
			//Alphabets listview is populated
			// TODO triuver un moyen indenpendant de la vue
			
			//ListView alphabets_listview=(ListView)indexbar.findViewById(R.id.listeficheavecfiltre_listView_alphabets);
			ListView alphabets_listview=indexbar.getAlphabetListView();
			/*Populating the Alphabet List*/			
			ArrayList<String> alphabets_list=new ArrayList<String>();
			String alphabet_array[]    =	indexbar.getResources().getStringArray(R.array.alphabet_array);
			for(int i=0; i<27 ; i++){
				alphabets_list.add(alphabet_array[i]);
			}
			/*Main Task*/	
			prepareArray(alphabets_list);
			
			ArrayAdapter<String> alphabets_adapter=	new AlphabetListviewAdapter(indexbar.getBaseContext(),R.layout.indexbar_alphabet_row,alphabets_list, number_of_characters_omit);
			alphabets_listview.setAdapter(alphabets_adapter);
			alphabets_listview.setOnItemClickListener((OnItemClickListener) indexbar);
		}
	}

	private void prepareArray(ArrayList<String> alphabets_list) {
		
		height_of_one_dot=convertDipToPx(9, indexbar.getBaseContext());
		//Log.d(TAG,"Height of one dot i.e. 9dp= "+height_of_one_dot+" px");  //9dp=13px
		
				
		if(number_of_characters_omit==1 || number_of_characters_omit==2 ){
			//adjusted the height of the row
		}
		if(number_of_characters_omit>2 && number_of_characters_omit<=4){					//Two groups shown. For 3,4 characters remove 4,5 characters
		
			removeAlphabet(alphabets_list, 6, 2);		//F,G
			alphabets_list.add(6, ".");	
			
			removeAlphabet(alphabets_list, 18, 2);		//S,T
			
			if(number_of_characters_omit==4)
				alphabets_list.remove(18);				//U
			alphabets_list.add(18, ".");
			
		}
		if(number_of_characters_omit>4 && number_of_characters_omit<=6){  			//5,6
			
			int total_height_dots=height_of_one_dot*3;					
			float num_eliminate=(float)total_height_dots/height_of_alphabet_row;
			int eliminate=(int)Math.round(num_eliminate+0.5f );
			Log.d(TAG,"For 3 dots, eliminate= "+eliminate+" more characters"+"  (num_eliminate="+num_eliminate+")");
			
			if(eliminate==0)	//there would be atleast one omitance. If there is any error, it is explicitly done.
				eliminate=1;
			
			if((number_of_characters_omit==5 && eliminate==2) || (number_of_characters_omit==6 && eliminate==1))  {
				
			}
			removeAlphabet(alphabets_list, 6, 2);		//F,G
			alphabets_list.add(6, ".");	

			alphabets_list.remove(14);  				//O
			if(number_of_characters_omit==6)
				alphabets_list.remove(14);				//P			
			alphabets_list.add(14, ".");
			
			if(number_of_characters_omit==5){
				removeAlphabet(alphabets_list, 23, 2);	//X,Y
				alphabets_list.add(23, ".");
			}else{
				removeAlphabet(alphabets_list, 22, 2);
				alphabets_list.add(22, ".");
			}
				
			if(eliminate==1)
				alphabets_list.remove(15);		//P  / Q
			else if(eliminate==2){
				if(number_of_characters_omit==5){
					removeAlphabet(alphabets_list, 15, 2);		//P,Q
				}
				else{
					alphabets_list.remove(7);		//H
					alphabets_list.remove(14);		//Q 
				}
			}
			else{
				if(number_of_characters_omit==5){
					alphabets_list.remove(7);		//H
					removeAlphabet(alphabets_list, 14, 2);		//P,Q
				}
				else{
					alphabets_list.remove(7);		//H
					alphabets_list.remove(14);		//Q
					alphabets_list.remove(19);		//W
				}
			}
		}
		else if(number_of_characters_omit>6 && number_of_characters_omit<=8){
			
			int total_height_dots=height_of_one_dot*4;					
			float num_eliminate=(float)total_height_dots/height_of_alphabet_row;
			int eliminate=(int)Math.round(num_eliminate+0.5f );
			//Log.d(TAG,"For 4 dots, eliminate= "+eliminate+" rows"+"  (num_eliminate="+num_eliminate+")");
			
			if(eliminate==0)	//there would be atleast one omitance. If there is any error, it is explicitly done.
				eliminate=1;
			if( (number_of_characters_omit==7 && eliminate==2) || (number_of_characters_omit==8 && eliminate==1)){
					
				removeAlphabet(alphabets_list, 3, 3);	//C,D,E
				alphabets_list.add(3,".");		
				
				removeAlphabet(alphabets_list, 8, 2);	//J,K
				alphabets_list.add(8,".");		
				
				removeAlphabet(alphabets_list, 14, 2);	//Q,R
				alphabets_list.add(14,".");		
				
				removeAlphabet(alphabets_list, 20, 2);	//X,Y
				alphabets_list.add(20,".");	
			}
			else if( (number_of_characters_omit==7 && eliminate==3) || (number_of_characters_omit==8 && eliminate==2)){
				removeAlphabet(alphabets_list, 3, 3);		//C,D,E
				alphabets_list.add(3,".");		
				
				removeAlphabet(alphabets_list, 7, 3);		//I,J,K
				alphabets_list.add(7,".");		
				
				removeAlphabet(alphabets_list, 13, 2);		//Q,R		
				alphabets_list.add(13,".");		
				
				removeAlphabet(alphabets_list, 19, 2);		//X,Y		
				alphabets_list.add(19,".");	
			}
			else if( (number_of_characters_omit==7 && eliminate==4) || (number_of_characters_omit==8 && eliminate==3)){
				removeAlphabet(alphabets_list, 3, 3);		//C,D,E		
				alphabets_list.add(3,".");		
				
				removeAlphabet(alphabets_list, 7, 3);		//I,J,K
				alphabets_list.add(7,".");		
				
				removeAlphabet(alphabets_list, 12, 3);		//P,Q,R		
				alphabets_list.add(12,".");		
				
				removeAlphabet(alphabets_list, 18, 2);		//X,Y
				alphabets_list.add(18,".");	
			}
			else if( number_of_characters_omit==7 && eliminate==1){
				
				removeAlphabet(alphabets_list, 4, 2);	//D,E		
				alphabets_list.add(4,".");		
				
				removeAlphabet(alphabets_list, 9, 2);	//J,K		
				alphabets_list.add(9,".");
				
				removeAlphabet(alphabets_list, 15, 2);	//Q,R		
				alphabets_list.add(15,".");
				
				removeAlphabet(alphabets_list, 21, 2);	//X,Y		
				alphabets_list.add(21,".");	
			}
			else if( number_of_characters_omit==8 && eliminate==4){
						
				removeAlphabet(alphabets_list, 3, 3);	//C,D,E		
				alphabets_list.add(3,".");		

				removeAlphabet(alphabets_list, 7, 3);	//I,J,K	
				alphabets_list.add(7,".");	
				
				removeAlphabet(alphabets_list, 12, 3);		//P,Q,R
				alphabets_list.add(12,".");		
				
				removeAlphabet(alphabets_list, 17, 3);		//W,X,Y		
				alphabets_list.add(17,".");	
			}
		}
		else if(number_of_characters_omit>8 && number_of_characters_omit<=10){
				removeAlphabet(alphabets_list, 3, 4);	//C,D,E,F		
				alphabets_list.add(3,".");		

				removeAlphabet(alphabets_list, 6, 3);	//I,J,K	
				alphabets_list.add(6,".");	
				
				removeAlphabet(alphabets_list, 11, 3);		//P,Q,R
				alphabets_list.add(11,".");		
				
				removeAlphabet(alphabets_list, 16, 3);		//V,W,X,Y		
				alphabets_list.add(16,".");
		}
		else if(number_of_characters_omit>10 && number_of_characters_omit<=12){
			removeAlphabet(alphabets_list, 3, 4);	//C,D,E,F		
			alphabets_list.add(3,".");		

			removeAlphabet(alphabets_list, 6, 4);	//I,J,K,L	
			alphabets_list.add(6,".");	
			
			removeAlphabet(alphabets_list, 9, 4);	//O,P,Q,R 	
			alphabets_list.add(9,".");		
			
			removeAlphabet(alphabets_list, 12, 4);		//V,W,X,Y		
			alphabets_list.add(12,".");
		}
		else if(number_of_characters_omit>12 && number_of_characters_omit<=14){
			removeAlphabet(alphabets_list, 3, 4);	//C,D,E,F		
			alphabets_list.add(3,".");		

			removeAlphabet(alphabets_list, 6, 4);	//I,J,K,L	
			alphabets_list.add(6,".");	
			
			removeAlphabet(alphabets_list, 9, 4);	//O,P,Q,R 	
			alphabets_list.add(9,".");		
			
			removeAlphabet(alphabets_list, 12, 4);		//V,W,X,Y		
			alphabets_list.add(12,".");
		}
		else if(number_of_characters_omit>14 && number_of_characters_omit<=16){
			removeAlphabet(alphabets_list, 2, 5);	//B,C,D,E,F		
			alphabets_list.add(2,".");		

			removeAlphabet(alphabets_list, 5, 4);	//I,J,K,L	
			alphabets_list.add(5,".");	
			
			removeAlphabet(alphabets_list, 8, 4);	//O,P,Q,R 	
			alphabets_list.add(8,".");		
			
			removeAlphabet(alphabets_list, 11, 5);		//U,V,W,X,Y		
			alphabets_list.add(11,".");
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
