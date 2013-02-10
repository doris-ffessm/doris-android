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

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import net.htmlparser.jericho.Attribute;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.util.Log;

/* *********************************************************************
 * Classe permettant de récupérer les résultats à partir d'un mot 
 ********************************************************************** */
public class RechercheDansLeCache {
    private final static String TAG = "RechercheDansLeCache";
    private final static Boolean LOG = false;
    
    private Context appContext;
	public File dossierCache;
	public String pageNettoyee;
	public Integer nbResultats;
	
	public static List<String> listeFichesTrouvees = new ArrayList<String>(20);
		    
	RechercheDansLeCache(){
		if (LOG) Log.d(TAG, "RechercheDansLeCache() - Début");
    	
    	if (LOG) Log.d(TAG, "RechercheDansLeCache() - Fin");
	}
	
	public void setContext(Context inContext){
		if (LOG) Log.d(TAG, "setContext()- Début");
		
		appContext = inContext;
		
		if (LOG) Log.d(TAG, "setContext()- Fin");
	}
	
    public Integer getNbResultats() throws IOException{
    	if (LOG) Log.d(TAG, "getNbResultats() - Début");
    	
    	nbResultats = 0;
    	
    	dossierCache = appContext.getCacheDir();
		if (LOG) Log.v(TAG, "getNbResultats() - dossierCache : "+dossierCache.toString());
		
		if (dossierCache!= null && dossierCache.isDirectory()) {
			
	        try {
	            for (File child:dossierCache.listFiles()) {
	            	if (LOG) Log.v(TAG, "getNbResultats() - child : "+child.getName());
	            	 if (child.isFile()) {
	            	  if (child.getName().startsWith("-Fiche£",3)) {
	            		  if (LOG) Log.d(TAG, "getNbResultats() - fiche : "+child.getName());
	            		  nbResultats ++;
	            	  }
	            	 }
	            }
	        } catch(Exception e) {
	            Log.e(TAG, String.format("Erreur lors de la lecture du cache, erreur %s", e.getMessage()));
	        }
		}

		if (LOG) Log.d(TAG, "getNbResultats() - nbResultats : "+nbResultats);
		if (LOG) Log.d(TAG, "getNbResultats() - Fin");
    	return nbResultats;
    }
    
    /* *********************************************************************
     * Recherche des Fiches dans une pages de résultats
     ********************************************************************** */
    public List<String> getFiches() {
    	if (LOG) Log.d(TAG, "getFiches() - Début");
    	    	
    	try {
            //Parcours de l'ensemble des fiches
    		for (File childFiche:dossierCache.listFiles()) {
            	if (LOG) Log.v(TAG, "getFiches() - child : "+childFiche.getName());
            	 if (childFiche.isFile()) {
            	  if (childFiche.getName().startsWith("-Fiche£",3)) {
            		  if (LOG) Log.d(TAG, "getFiches() - fiche : "+childFiche.getName());
            		 
						Fiche fiche = new Fiche(appContext);
						
						String[] tempFichier = childFiche.getName().split("£");
						if (LOG) Log.v(TAG, "getFiches() - tempFichier[0] : "+tempFichier[0]);
						if (LOG) Log.v(TAG, "getFiches() - tempFichier[1] : "+tempFichier[1]);
						if (LOG) Log.v(TAG, "getFiches() - tempFichier[2] : "+tempFichier[2]);
						
						fiche.ref = tempFichier[1];
						fiche.nom = tempFichier[2];
						//Pas très juste mais permet d'afficher un résultat
						fiche.nom_scient = tempFichier[2];
						fiche.urlFiche = appContext.getString(R.string.cst_urlFiche_racine)+tempFichier[1];
						
						tempFichier = null;
						if (LOG) Log.d(TAG, "getFiches() - fiche.ref : " + fiche.ref);
						
						// Recherche de l'image de la fiche dans le cache
						for (File childImage:dossierCache.listFiles()) {
			            	if (LOG) Log.v(TAG, "getFiches() - child : "+childImage.getName());
			            	 if (childImage.isFile()) {
			            	  if (childImage.getName().startsWith("-Image£",3)) {
			            		  if (LOG) Log.v(TAG, "getFiches() - fiche : "+childImage.getName());
						
			            		  String[] tempFichier2 = childImage.getName().split("£");
			            		  if (LOG) Log.v(TAG, "getFiches() - tempFichier2[0] : "+tempFichier2[0]);
			            		  if (LOG) Log.v(TAG, "getFiches() - tempFichier2[1] : "+tempFichier2[1]);
			            		  if (LOG) Log.v(TAG, "getFiches() - tempFichier2[3] : "+tempFichier2[3]);
						
			            		  if (tempFichier2[1].equals(fiche.ref) && tempFichier2[3].equals("photos_fiche_vig")) {
			            			  fiche.urlVignette = tempFichier2[3] + "/" + tempFichier2[4];
			            			  if (LOG) Log.v(TAG, "getFiches() - fiche.urlVignette : "+fiche.urlVignette);
			            			  
			            			  fiche.urlImage = "http://doris.ffessm.fr/gestionenligne/photos/" + tempFichier2[4];
			            			  if (LOG) Log.v(TAG, "getFiches() - fiche.urlImage : "+fiche.urlImage);

			            			  break;
			            		  }
			            		  
			            	  }
			            	 }
						}
						//Si fiche.ref == null c'est qu'il y a eu un souci
						if ( fiche.ref != null ){
						
							//si la fiche n'a jamais été créée on l'ajoute à notre Liste de Fiches 
							if (Doris.listeFiches.get(fiche.ref) == null){
								
								if (LOG) Log.v(TAG, "getFiches() - Doris.listeFiches.put()");
								Doris.listeFiches.put(fiche.ref, fiche);
							
							//Si la fiche existe mais pas son entête alors on complète la fiche existente
							} else if (Doris.listeFiches.get(fiche.ref).getEnteteExistence() == false){
								
								if (LOG) Log.v(TAG, "getFiches() - Doris.listeFiches.get().setEnteteFiche()");
								Doris.listeFiches.get(fiche.ref).setEnteteFicheFromFiche(fiche);
							
							}
							
							listeFichesTrouvees.add(fiche.ref);
							
						} else {
							
							if (LOG) Log.e(TAG, "getFiches() - fiche.ref == null => souci sur la page html");
					
						}
						
						fiche = null;
				}
			}
		}
            

		} catch(Exception e) {
		  Log.e(TAG, String.format("Erreur lors de la lecture du cache, erreur %s", e.getMessage()));
		}    
		if (LOG) Log.d(TAG, "getFiches() - Fin");
		return listeFichesTrouvees;
    }
    
	
    
    public boolean getPageSuite(String inCodeHtml) throws IOException{
    	if (LOG) Log.d(TAG, "Recherche.resultGetNbResultats() - Début");
    	
		boolean pageSuivanteTrouvee;
		
		pageSuivanteTrouvee = inCodeHtml.contains("page=Suivant&PageCourante=");
		
		if (LOG) Log.d(TAG, "Recherche.resultGetNbResultats() - pageSuivanteTrouvee : "+pageSuivanteTrouvee);
		if (LOG) Log.d(TAG, "Recherche.resultGetNbResultats() - Fin");
    	return pageSuivanteTrouvee;
    }
     
    
    
}