/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
Copyright du Code : Guillaume Moynard  ([29/05/2011]) 

Guillaume Moynard : gmo7942@gmail.com

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
* ********************************************************************
* ********************************************************************* */

package fr.ffessm.doris;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

	/* *********************************************************************
     * Classe de chacun des resultats trouvés durant la navigation
     ********************************************************************** */
	public class Groupe {
	    private final static String TAG = "Groupe";
		//private static String APPNOM = "Doris";
	    private final static Boolean LOG = false;
		
	    private Context appContext;
	    
		public int numGroupe;
		public String nom;
		public int profondeur;
		
		public String urlVignette;
		public int numUrlGroupe;
		public int numUrlSsGroupe;
		
		public Groupe parent;
		
		public List<Groupe> groupeListeEnfants = new ArrayList<Groupe>(20);
		
		Groupe(){
			numGroupe = -1;
			urlVignette = "";
			
			numUrlGroupe = -1;
			numUrlSsGroupe = -1;
		}
		
		Groupe (Context inContext, int inNumGroupe, int inProfondeur, String inNom){
			super();
			if (LOG) Log.d(TAG, "Groupe() - Début");
			
			if (LOG) Log.d(TAG, "Groupe() - inNumGroupe : "+inNumGroupe);
			if (LOG) Log.d(TAG, "Groupe() - inProfondeur : "+inProfondeur);
			if (LOG) Log.d(TAG, "Groupe() - inNom : "+inNom);
			
			appContext = inContext;
			
			numGroupe = inNumGroupe;
			nom = inNom;
			profondeur = inProfondeur;
			
			urlVignette = "";
			
			numUrlGroupe = -1;
			numUrlSsGroupe = -1;
			
			if (LOG) Log.d(TAG, "Groupe() - Fin");
		}

		public void addEnfant(int inNumGroupe, int inProfondeur, String inNom){
			if (LOG) Log.d(TAG, "addEnfant() - Début");
			Groupe fils = new Groupe (appContext, inNumGroupe, inProfondeur, inNom);
			fils.parent = this;
			groupeListeEnfants.add(fils);
			if (LOG) Log.d(TAG, "addEnfant() - Fin");
		}
		
		public void setUrlVignette(String inUrlVignette){
			if (LOG) Log.d(TAG, "setUrlVignette() - Début");
			if (LOG) Log.d(TAG, "setUrlVignette() - inUrlVignette : "+inUrlVignette);
			urlVignette = inUrlVignette;
			if (LOG) Log.d(TAG, "setUrlVignette() - Fin");
		}
			
		public void setNumUrlGroupe(int inNumUrlGroupe){
			if (LOG) Log.d(TAG, "setNumUrlGroupe() - Début");
			if (LOG) Log.d(TAG, "setNumUrlGroupe() - inNumUrlGroupe : "+inNumUrlGroupe);
			numUrlGroupe = inNumUrlGroupe;
			if (LOG) Log.d(TAG, "setNumUrlGroupe() - Fin");
		}
		
		public void setNumUrlSsGroupe(int inNumUrlSsGroupe){
			if (LOG) Log.d(TAG, "setNumUrlSsGroupe() - Début");
			if (LOG) Log.d(TAG, "setNumUrlSsGroupe() - inNumUrlSsGroupe : "+inNumUrlSsGroupe);
			numUrlSsGroupe = inNumUrlSsGroupe;
			if (LOG) Log.d(TAG, "setNumUrlSsGroupe() - Fin");
		}
	
		
	}