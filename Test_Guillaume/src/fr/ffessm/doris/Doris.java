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

/* *********************************************************************
 * Déclaration des imports
   *********************************************************************/
import fr.ffessm.doris.Doris;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
	
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;

import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.Window;

import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.GridView;
import android.widget.Toast;
	
/* *********************************************************************
 * Début du Programme
   *********************************************************************/
public class Doris extends Activity {
	/* *********************************************************************
	 * Définition Paramètres
	   *********************************************************************/
    
    private final static String TAG = "Doris";
    private final static Boolean LOG = true;
    
    private final static int MODE_INIT = 0;
    private final static int MODE_RESULTATS = 1;
    private final static int MODE_RESULTATS_SUIVANT = 2;
    private final static int MODE_RECHERCHE_GUIDEE = 3;
    private final static int MODE_HISTORIQUE = 4;
    
    private final static String PREFS_NAME = "DorisParam";
	/* *********************************************************************
	 * 
	   *********************************************************************/
    Thread thread;
    Handler handler;
    
    private GridView GVResultat;
    private CustomAutoCompleteTextView ETRecherche;
    private Button BTEnvoyer;
    private ProgressDialog dialogPatience;
    
    private static int mode;
    private boolean pageSuite = false;
    private Integer numPageSuite = 0;
	
    private Context appContext;
    private SharedPreferences preferences;
    private String appVersionName = "";
    private Integer affResultatsTypeLib = 0;
    
    //Ensemble des Fiches crées identifiées par le Num de fiches HTML
    public static HashMap<String, Fiche> listeFiches = new HashMap<String, Fiche>();

    //On crée toujours 1 objet de recherche, ainsi on garde en mémoire les résultats
    // pour y revenir plus rapidement
    private RechercheParNom rechercheParNom = new RechercheParNom();
    private RechercheGuidee rechercheGuidee = new RechercheGuidee();
    private RechercheDansLeCache rechercheDansLeCache = new RechercheDansLeCache();
    
    //Liste des Fiches Affichées, liste de Num de Fiches HTML
    public static List<String> listeFichesAff = new ArrayList<String>(20);
	
    //Liste des propositions disponibles pour aider la saisie / la recherche
    public static List<HashMap<String,String>> listePropositions = new ArrayList<HashMap<String,String>>();
    public String[] from = { "icone","nom"};
    public int[] to = { R.id.aide_icone_type,R.id.aide_txt};
    
    //Grand Groupe Racine pour la recherche par Groupes
    public static Groupe groupeAff = new Groupe();

	private String racineSite;
	private String txtRecherche;
	private String urlRecherche;
	private Integer nbResultats = 0;
	// permet de se positionner sur le début des ajouts qd on a fait suite des résultats
	private Integer tailleListeAvantAjout = 0;
	private Boolean auMoins1FicheDansLeCache = false;
	
	/* *********************************************************************
	 * Démarrage de l'application
	   *********************************************************************/
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	if (LOG) Log.d(TAG, "onCreate() - Début");
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
                
        // Contexte de l application
        if (LOG) Log.d(TAG, "onCreate() - mode : "+mode);
        if (LOG) Log.d(TAG, "onCreate() - groupeAff : "+groupeAff.groupeListeEnfants.size());
        if (LOG) Log.d(TAG, "onCreate() - listeFichesAff : "+listeFichesAff.size());
        
        appContext = getApplicationContext();
        racineSite = appContext.getString(R.string.cst_racineSite);
        
        // Récupération Identifiant : EditText, GridView, Boutton
        ETRecherche = (CustomAutoCompleteTextView) findViewById(R.id.etIDRecherche);
        GVResultat = (GridView) findViewById(R.id.gvIDResultat);
        BTEnvoyer = (Button) findViewById(R.id.btIDEnvoyer);
        
        //Création de l'objet Préférences pour son utilisation ensuite
        preferences = appContext.getSharedPreferences(PREFS_NAME, 0);
        
        // Préparation à l'aide à la saisie (liste de propositions apparaissant lors de la saisie)
        miseAJourListeNoms();
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), listePropositions, R.layout.autocomplete_layout, from, to);
        ETRecherche.setAdapter(adapter);
        
        if (!Outils.isOnline(appContext)){
        	affichageMessage(getString(R.string.txt_pas_internet));
        }
        
        // Après une rotation, l'activity est relancée mais avec des données
        // On les affiche afin de ne pas perdre de temps
        if ( (mode == MODE_RESULTATS_SUIVANT || mode == MODE_RESULTATS) && listeFichesAff.size() != 0 ){
        	if (LOG) Log.v(TAG, "onCreate() - Affichage resultats après rotation");
        	GVResultat.setAdapter(new ImageAdapter(Doris.this, "Resultats"));
	        GVResultat.setLongClickable(true);
    		 
	        //Cacher le clavier
    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    		imm.hideSoftInputFromWindow(ETRecherche.getWindowToken(), 0);
    		
        } else if ( mode == MODE_RECHERCHE_GUIDEE && groupeAff.groupeListeEnfants.size() != 0){
        	if (LOG) Log.v(TAG, "onCreate() - recherche");
        	GVResultat.setAdapter(new ImageAdapter(Doris.this, "Recherche_Guidee"));
	        GVResultat.setLongClickable(true);
	        
	        
        } else {
        	GVResultat.setAdapter(new ImageAdapter(this));
        }
        
        //Lors du 1er démarrage de l'application dans la version actuelle,
        //on affiche la boite d'A Propos
        String VersionAffichageAPropos = preferences.getString("AProposVersionAffichage", "jamais");
        if (LOG) Log.v(TAG, "onCreate() - VersionAffichageAPropos : "+VersionAffichageAPropos);
    	
        //Récupération du numéro de Version de DORIS
    	try	{
        	PackageManager pm = getPackageManager();
            PackageInfo pi = pm.getPackageInfo( getPackageName(), 0);
            appVersionName = pi.versionName;
     	} catch(Exception e) {
    		if (LOG) Log.e(TAG, "onOptionsItemSelected() - R.id.apropos");
    		e.printStackTrace();
    	}
        if (LOG) Log.v(TAG, "onCreate() - appVersionName : "+appVersionName);

        if (!VersionAffichageAPropos.equals(appVersionName)) {
        	if (LOG) Log.v(TAG, "onCreate() - Affichage A Propos");
            affichageMessageHTML(appContext.getString(R.string.txt_apropos).replace("@version", appVersionName), "file:///android_res/raw/apropos.html");
            
            SharedPreferences.Editor prefEdit = preferences.edit();  
        	prefEdit.putString("AProposVersionAffichage", appVersionName);  
        	prefEdit.commit();
        }
        
        /* *********************************************************************
    	 * 
    	   *********************************************************************/
        ETRecherche.setOnTouchListener(
        		new OnTouchListener() {
					@Override
					public boolean onTouch(View v,
							android.view.MotionEvent event) {
            			if (LOG) Log.d(TAG, "ETRecherche.setOnTouchListener.OnTouchListener().onTouch() - Début");
                    	
                    	miseAJourListeNoms();
                    	
                    	SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), listePropositions, R.layout.autocomplete_layout, from, to);
                        
                        ETRecherche.setAdapter(adapter);
                        
    					if (LOG) Log.d(TAG, "ETRecherche.setOnTouchListener.OnTouchListener().onTouch() - Fin");

						return false;
					}
    			}
        		
		);
        
        /* *********************************************************************
    	 * Gestion appuis sur les boutons
    	   *********************************************************************/
        BTEnvoyer.setOnClickListener(
        	new OnClickListener() {
        		@Override
        		public void onClick(View inView) {
        			if (LOG) Log.d(TAG, "BTEnvoyer.setOnClickListener.OnClickListener().onClick() - Début");
        			
                	lancementRecherche();
                	
					if (LOG) Log.d(TAG, "BTEnvoyer.setOnClickListener.OnClickListener().onClick() - Fin");
	        	}
			}
        );
        
        GVResultat.setOnItemClickListener(
        		new OnItemClickListener() 
        {
            public void onItemClick(AdapterView inParent,View inView, int inPosition, long inId) 
            {
            	if (LOG) Log.d(TAG, "GVResultat.setOnItemClickListener.onItemClick() - Début");
            	if (LOG) Log.d(TAG, "GVResultat.setOnItemClickListener.onItemClick() - mode : "+mode);
            	if (LOG) Log.d(TAG, "GVResultat.setOnItemClickListener.onItemClick() - position : "+inPosition);
            	if (LOG) Log.d(TAG, "GVResultat.setOnItemClickListener.onItemClick() - listeFichesAff.size() : "+listeFichesAff.size());
            	if (LOG) Log.d(TAG, "GVResultat.setOnItemClickListener.onItemClick() - groupeAff.groupeListeEnfants.size() : "+groupeAff.groupeListeEnfants.size());
            	
            	if (mode == MODE_INIT){
            		//Chargement de la liste des Groupes
            		if (inPosition == 0){
            			if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Chargement de la liste des Groupes");
            			reset();
             			getGrilleRecherche(0, 0);
            		}
            		
            		//Chargement de la liste des Fiches en cache
            		if (inPosition == 1 && auMoins1FicheDansLeCache == true){
            			if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Chargement de l'historique des Fiches visitées");
            			reset();
            			getGrilleHistorique();
            		}
            		
            		//Site Web 1
            		if ( (inPosition == 1 && auMoins1FicheDansLeCache == false)
            				|| (inPosition == 2 && auMoins1FicheDansLeCache == true)){
            			if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Lancement Navigateur sur Site 1");
            			Intent intent = new Intent(Intent.ACTION_VIEW);
    					intent.setData(Uri.parse(getString(R.string.cst_sitewebaccueil1_url)));
    					startActivity(intent);
            		}
            		//Site Web 2
            		if ( (inPosition == 2 && auMoins1FicheDansLeCache == false)
            				|| (inPosition == 3 && auMoins1FicheDansLeCache == true)){
            			if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Lancement Navigateur sur Site 2");
            			Intent intent = new Intent(Intent.ACTION_VIEW);
    					intent.setData(Uri.parse(getString(R.string.cst_sitewebaccueil2_url)));
    					startActivity(intent);
            		}
            	}
            	
            	if ((mode == MODE_RESULTATS || mode == MODE_RESULTATS_SUIVANT)) {
	            	if ( inPosition < listeFichesAff.size()){
	            		//Affichage de l'image
	            		if (LOG) Log.v(TAG, "GVResultat.setOnItemClickListener.onItemClick() - Affichage Image");
		                String refFiche = listeFichesAff.get(inPosition);
	            		
	            		Intent explicit = new Intent();
		                explicit.setClass(Doris.this, AffImage.class);
		                explicit.putExtra("mode", "depuisRecherche");
		                explicit.putExtra("ref", refFiche);
		                explicit.putExtra("imgRef", 0);
		                
		                startActivity(explicit);
		                
	            	} else if (mode == MODE_RESULTATS_SUIVANT && inPosition == listeFichesAff.size()) {
						//Affichage Message : faire appui long pour obtenir plus de résultat
	            		if (LOG) Log.v(TAG, "GVResultat.setOnItemClickListener.onItemClick() - Faire Clic long pour obtenir plus de résultats");
	            		Toast toast = Toast.makeText(appContext, getString(R.string.txt_ClicCourtSurResultatsSuivants), Toast.LENGTH_LONG);
						toast.show();
	            	} else {
	            		reset();
	            	}
	            	return;
            	}
            	
            	if (mode == MODE_RECHERCHE_GUIDEE){
            		if ( inPosition != groupeAff.groupeListeEnfants.size()){
					
						if (groupeAff.groupeListeEnfants.get(inPosition).groupeListeEnfants.size() != 0){
			      			//Affichage des groupes enfants
		            		if (LOG) Log.v(TAG, "GVResultat.setOnItemClickListener.onItemClick() - Affichage des groupes " + groupeAff.groupeListeEnfants.get(inPosition).groupeListeEnfants.size() + "enfants");
		  
							groupeAff = groupeAff.groupeListeEnfants.get(inPosition);
							GVResultat.setAdapter(new ImageAdapter(Doris.this, "Recherche_Guidee"));
							
						} else {
							//Affichage des Entêtes du Sous-Sous...Groupe sélectioné
		            		if (LOG) Log.v(TAG, "GVResultat.setOnItemClickListener.onItemClick() - Affichage des entêtes de " + groupeAff.groupeListeEnfants.get(inPosition).nom);
		  
							txtRecherche = groupeAff.groupeListeEnfants.get(inPosition).nom;
							urlRecherche = racineSite + "/fiches_liste.asp?groupe_numero=" + groupeAff.groupeListeEnfants.get(inPosition).numUrlGroupe;
							
							if ( groupeAff.groupeListeEnfants.get(inPosition).numUrlSsGroupe != -1 ){
								urlRecherche = urlRecherche + "&sousgroupe_numero=" + groupeAff.groupeListeEnfants.get(inPosition).numUrlSsGroupe;
							}

							if (LOG) Log.d(TAG, "GVResultat.setOnItemClickListener.onItemClick() - urlRecherche : "+urlRecherche);
							tailleListeAvantAjout = 0;
							getGrilleResultats(false);
						
						}
						
            		} else {
            			//Retour au groupe Parent
	            		if (LOG) Log.v(TAG, "GVResultat.setOnItemClickListener.onItemClick() - groupeAff.profondeur : " + groupeAff.profondeur);
	            		if (groupeAff.profondeur == 0){
	            			reset();
	            		} else {
	            			if (LOG) Log.v(TAG, "GVResultat.setOnItemClickListener.onItemClick() - Retour au groupe Parent : " + groupeAff.parent);
	            			groupeAff = groupeAff.parent;
	            			GVResultat.setAdapter(new ImageAdapter(Doris.this, "Recherche_Guidee"));
	            		}
						
            		}
    		                
            		//Le titre de la Fenêtre sera le Nom du Groupe courant
					if (groupeAff.nom != null) setTitle(groupeAff.nom);
					// Si on est sur le Groupe père on remet le Titre de la Fenêtre à DORIS
					else setTitle(getString(R.string.app_name));
					return;
            	}

        		if (mode == MODE_HISTORIQUE ) {
	            	if ( inPosition < listeFichesAff.size()){
	            		//Affichage de l'image
	            		if (LOG) Log.v(TAG, "GVResultat.setOnItemClickListener.onItemClick() - Affichage Image du Cache");
		                String refFiche = listeFichesAff.get(inPosition);
	            		
	            		Intent explicit = new Intent();
		                explicit.setClass(Doris.this, AffImage.class);
		                explicit.putExtra("mode", "depuisHistorique");
		                explicit.putExtra("ref", refFiche);
		                explicit.putExtra("imgRef", 0);
		                
		                startActivity(explicit);
	            	} else {
	            		reset();
	            	}
        		}
            	
                if (LOG) Log.d(TAG, "GVResultat.setOnItemClickListener.onItemClick() - Fin");
            }
        });
        
        GVResultat.setOnItemLongClickListener(
        		new OnItemLongClickListener() 
        {
			@Override
            public boolean onItemLongClick(AdapterView inParent,View inView, int inPosition, long inId) 
            {
            	if (LOG) Log.d(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Début");
            	if (LOG) Log.d(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - mode : " + mode);
            	if (LOG) Log.d(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - inPosition : " + inPosition);
            	
            	
            	if (mode == MODE_INIT){
            		
            	}
            	
            	if ((mode == MODE_RESULTATS || mode == MODE_RESULTATS_SUIVANT)) {
                	if (inPosition != listeFichesAff.size()){
                		if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Affichage Fiche");
                		
                		String refFicheAff = listeFichesAff.get(inPosition);
                		if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - refFicheAff : "+refFicheAff);

                		
                		//Affichage Fiche Sélectionnée
    	            	Intent explicit = new Intent();
    	                explicit.setClass(Doris.this, AffFiche.class);
    	                explicit.putExtra("ref", refFicheAff);
    	                startActivity(explicit);
    	                
                	} else {
                		// Chargement résultats suivants
                		if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Chargement Résultats suivants");
                		
                		tailleListeAvantAjout = listeFichesAff.size();
                		if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - tailleListeAvantAjout : " + tailleListeAvantAjout);
                		
                		getGrilleResultats(true);
                		
	            	}
            	}
            	
            	if ((mode == MODE_HISTORIQUE)) {
                	if (inPosition != listeFichesAff.size()){
                		if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Affichage Fiche depuis le cache");
                		
                		String refFicheAff = listeFichesAff.get(inPosition);
                		if (LOG) Log.v(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - refFicheAff : "+refFicheAff);

                		
                		//Affichage Fiche Sélectionnée
    	            	Intent explicit = new Intent();
    	                explicit.setClass(Doris.this, AffFiche.class);
    	                explicit.putExtra("ref", refFicheAff);
    	                startActivity(explicit);
                	}
            	}
            	
                if (LOG) Log.d(TAG, "GVResultat.setOnItemLongClickListener.onItemClick() - Fin");
                return true;
            }

        });
        
        //Capture des évènements de saisies du clavier logiciel
    	ETRecherche.setOnKeyListener(new EditText.OnKeyListener() {
    	    public boolean onKey(View inView, int inKeyCode, KeyEvent inKeyEvent) {
    	    	if (LOG) Log.d(TAG, "ETRecherche.setOnKeyListener() - Début");

    	    	if (LOG) Log.v(TAG, "ETRecherche.setOnKeyListener() - inKeyCode : " + inKeyCode);
    	    	if (LOG) Log.v(TAG, "ETRecherche.setOnKeyListener() - inKeyEvent : " + inKeyEvent);
    	    	
    	    	//Lors de l'appui
    	    	if (inKeyEvent.getAction() == KeyEvent.ACTION_DOWN) {
	    	    	// Si on valide on lance la recherche
	    	    	if ( inKeyCode == KeyEvent.KEYCODE_ENTER ) {
	    	    		
	    	    		lancementRecherche();
	    	    		
	    	    	}
    	    	}
    	    	
    	    	if (LOG) Log.d(TAG, "ETRecherche.setOnKeyListener() - Fin");
                return false;
            }
        });
        	
        if (LOG) Log.d(TAG, "onCreate() - Fin");
    }
    
	/* *********************************************************************
	 * On va commencer à interagir avec l'application
	   *********************************************************************/
    @Override
    public void onResume() {
    	if (LOG) Log.d(TAG, "onResume() - Début");
    	super.onResume();
    	
        if (LOG) Log.d(TAG, "onResume() - Fin");
    }
    
    public class ImageAdapter extends BaseAdapter {
		Context localContext;
		String localAction;
		
        public ImageAdapter(Context inContext) {
        	if (LOG) Log.d(TAG, "ImageAdapter.ImageAdapter(Context) - Début");
        	localContext = inContext;
        	localAction = "";
			if (LOG) Log.d(TAG, "ImageAdapter.ImageAdapter(Context) - Fin");
     	}

		public ImageAdapter(Context inContext, String inAction) {
			if (LOG) Log.d(TAG, "ImageAdapter.ImageAdapter(Context, inAction) - Début");
			if (LOG) Log.d(TAG, "ImageAdapter.ImageAdapter(Context, inAction) - inAction : "+inAction);
			localContext = inContext;
			localAction = inAction;
			if (LOG) Log.d(TAG, "ImageAdapter.ImageAdapter(Context, inAction) - Fin");
		}

		@Override
		public int getCount() {
			if (LOG) Log.d(TAG, "ImageAdapter.getCount() - Début");
			if (LOG) Log.d(TAG, "ImageAdapter.getCount() - localAction : " + localAction);
			if (LOG) Log.d(TAG, "ImageAdapter.getCount() - groupeAff.numGroupe : " + groupeAff.numGroupe);
			
			Integer num = 0;
			// Si on est à la racine : on a jamais construit l'arbre de recherche
			// alors on affiche le bouton de construction de l'arbre
			// Le bouton suivant est l'acces au cache (s'il y a au moins une fiche)
			// Les 2 suivantes permettent de promouvoir 2 sites web
			if (localAction == "" && groupeAff.numGroupe == -1) {
				mode = MODE_INIT;
				num = 3;
				
				//Si au moins une fiche dans le cache alors on affiche le bouton permettant 
				//de naviquer dans l'historique
				auMoins1FicheDansLeCache = Outils.isAumoins1FicheDansLeCache(appContext);
				if (auMoins1FicheDansLeCache == true) {
					num++;
					rechercheDansLeCache.init(appContext);
				}
				
				
			}
			// Affichage de la matrice des résultats
			// + le bouton de retour à la racine
			// + le bouton d'affichage des résultats suivants (si nécessaire)
			if (localAction == "Resultats") {
				mode = MODE_RESULTATS;
				if (LOG) Log.v(TAG, "ImageAdapter.getCount() - listeFichesAff.size() : " + listeFichesAff.size());
				
				num = listeFichesAff.size();
				// Ajout d'une icone s'il y a une page de résultat suivante
				if (pageSuite) {
					mode = MODE_RESULTATS_SUIVANT;
					num++;
				}
				//Incrément pour ajouter le bouton de retour à la racine
				num++;
								
			}
			// Affichage de la matrice des groupes de navigation
			if (localAction == "Recherche_Guidee" || ( localAction == "" && groupeAff.numGroupe != -1 ) ) {
				mode = MODE_RECHERCHE_GUIDEE;

				if (LOG) Log.v(TAG, "ImageAdapter.getCount() - groupeAff.groupeListeEnfants.size() : " + groupeAff.groupeListeEnfants.size());
				num = groupeAff.groupeListeEnfants.size();
				
				//la dernière icône est le retour au groupe père de l'actuel
				num++;
			}
			// Affichage de la matrice des résultats du cache
			// + le bouton de retour à la racine
			if (localAction == "Historique") {
				mode = MODE_HISTORIQUE;
				
				if (LOG) Log.v(TAG, "ImageAdapter.getCount() - listeFichesAff.size() : " + listeFichesAff.size());
				num = listeFichesAff.size();
				
				//Incrément pour ajouter le bouton de retour à la racine
				num++;
			}
			
			// Choix du type de Libellé affiché dans les résultats 
	        affResultatsTypeLib = preferences.getInt("aff_res_type_libelle", 0);
	        if (LOG) Log.v(TAG, "onCreate() - Type Libellé (0-Fr., 1-Scient. : "+affResultatsTypeLib);

			if (LOG) Log.d(TAG, "ImageAdapter.getCount() - mode : "+mode);
			if (LOG) Log.d(TAG, "ImageAdapter.getCount() - num : "+num);
			if (LOG) Log.d(TAG, "ImageAdapter.getCount() - Fin");
			return num;
		}
 
		@Override
		public View getView(int inPosition, View convertView, ViewGroup parent) {
			if (LOG) Log.d(TAG, "ImageAdapter.getView() - Début");
			if (LOG) Log.d(TAG, "ImageAdapter.getView() - mode : "+mode);
			if (LOG) Log.d(TAG, "ImageAdapter.getView() - position : "+inPosition+" listeFiches.size() : "+listeFichesAff.size());
			if (LOG) Log.d(TAG, "ImageAdapter.getView() - parent : "+parent.toString());
			
			View view;

			if (convertView == null) {
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - convertView == null");
				LayoutInflater li = getLayoutInflater();
				if ( mode != MODE_RECHERCHE_GUIDEE){
					view = li.inflate(R.layout.lien_fiche, null);
				}else{
					view = li.inflate(R.layout.lien_groupe, null);
				}
			}
			else
			{
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - convertView != null");
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - convertView.getId() : "+convertView.getId());
				view = convertView;
			}
			
			TextView tv_libelle;
			TextView tv_type;
			ImageView iv_icone;
			
			// On affiche le bouton de recherche guidée
			if (mode == MODE_INIT && inPosition == 0) {

				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Interface Démarrage");
				tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
				
				String libelle = getString(R.string.txt_rechercheGuidee);
				
				int filtre_zone_geo = preferences.getInt("filtre_zone_geo", 0);
				if (LOG) Log.v(TAG, "getPageGrandsGroupes() - filtre_zone_geo : "+filtre_zone_geo);
				if (filtre_zone_geo != 0){
					String[] zone_geo = getResources().getStringArray(R.array.res_liste_config_restriction_zone_geo);
					libelle += " (" + zone_geo[filtre_zone_geo] + ")";
				}
				if (LOG) Log.v(TAG, "getPageGrandsGroupes() - libelle : "+libelle);
				
				tv_libelle.setText(libelle);
				
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				iv_icone.setImageResource(R.drawable.magic_tophat);
				
				tv_type = (TextView)view.findViewById(R.id.lienF_typeFiche);
				tv_type.setText("");
			}
			
			// On affiche le bouton de navigation dans l'historique
			if (mode == MODE_INIT && inPosition == 1 && auMoins1FicheDansLeCache == true) {

				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Navigation dans l'Historique");
				tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
				tv_libelle.setText(getString(R.string.txt_navigationHistorique));
				
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				iv_icone.setImageResource(android.R.drawable.ic_menu_recent_history);
				
				tv_type = (TextView)view.findViewById(R.id.lienF_typeFiche);
				tv_type.setText("");
			}
			
			// Bouton du Site Web 1 de la page d'Accueil 
			if (mode == MODE_INIT &&
					( ( inPosition == 1 && auMoins1FicheDansLeCache == false ) 
					|| ( inPosition == 2 && auMoins1FicheDansLeCache == true ) ) ) {

				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Bouton SIte Web 1");
				tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
				tv_libelle.setText(getString(R.string.cst_sitewebaccueil1_nom));
				
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				iv_icone.setImageResource(R.drawable.sitewebaccueil1);
				
				tv_type = (TextView)view.findViewById(R.id.lienF_typeFiche);
				tv_type.setText("");
			}
			
			// Bouton du Site Web 2 de la page d'Accueil 
			if (mode == MODE_INIT &&
					( ( inPosition == 2 && auMoins1FicheDansLeCache == false ) 
					|| ( inPosition == 3 && auMoins1FicheDansLeCache == true ) ) ) {

				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Bouton SIte Web 2");
				tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
				tv_libelle.setText(getString(R.string.cst_sitewebaccueil2_nom));
				
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				iv_icone.setImageResource(R.drawable.sitewebaccueil2);
				
				tv_type = (TextView)view.findViewById(R.id.lienF_typeFiche);
				tv_type.setText("");
			}

			// Affichage des vignettes de résultats
			if ( (mode == MODE_RESULTATS || mode == MODE_RESULTATS_SUIVANT || mode == MODE_HISTORIQUE) && inPosition < listeFichesAff.size() ) {
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Insert Vignettes");
				
				if ( affResultatsTypeLib == 0 ) {
					if (LOG) Log.v(TAG, "ImageAdapter.getView() - nom("+inPosition+") : "+listeFiches.get(listeFichesAff.get(inPosition)).nom);
					tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
					tv_libelle.setText(listeFiches.get(listeFichesAff.get(inPosition)).nom);
				}
				if ( affResultatsTypeLib == 1 ) {
					if (LOG) Log.v(TAG, "ImageAdapter.getView() - nom("+inPosition+") : "+listeFiches.get(listeFichesAff.get(inPosition)).nom_scient);
					tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
					tv_libelle.setText(listeFiches.get(listeFichesAff.get(inPosition)).nom_scient);
				}
				
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - ref : "+listeFiches.get(listeFichesAff.get(inPosition)).ref);
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - urlVignette : "+listeFiches.get(listeFichesAff.get(inPosition)).urlVignette);
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				if ( !listeFiches.get(listeFichesAff.get(inPosition)).urlVignette.equals("") ) {
					//On passe le nom de la fiche afin de la sauvegarder dans le cache, dans le nom du fichier
					iv_icone.setImageBitmap(Outils.getImage(localContext,listeFiches.get(listeFichesAff.get(inPosition)).urlVignette, listeFiches.get(listeFichesAff.get(inPosition)).ref, ""));
				} else {
					iv_icone.setImageResource(R.drawable.doris);
				}
				
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - ficheType("+inPosition+") : "+listeFiches.get(listeFichesAff.get(inPosition)).ficheType);
				tv_type = (TextView)view.findViewById(R.id.lienF_typeFiche);
				tv_type.setText(listeFiches.get(listeFichesAff.get(inPosition)).ficheType);
				tv_type.setTextColor(Color.parseColor(listeFiches.get(listeFichesAff.get(inPosition)).ficheTypeCouleurTexte));
				tv_type.setBackgroundColor(Color.parseColor(listeFiches.get(listeFichesAff.get(inPosition)).ficheTypeCouleurFond));
			}
			
			// Affichage du bouton suite résultats
			if (mode == MODE_RESULTATS_SUIVANT && inPosition == listeFichesAff.size()) {
				// Transformer cela un jour en appel acceder page suivante
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Page Suivante");
				tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
				tv_libelle.setText("Suite");
				
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				iv_icone.setImageResource(R.drawable.media_seek_forward_8);
				
				tv_type = (TextView)view.findViewById(R.id.lienF_typeFiche);
				tv_type.setText("");
			}
			
			// Affichage de vignette retour à l'écran de démmarage
			if ( ( mode == MODE_RESULTATS && inPosition == listeFichesAff.size() )
					|| ( mode == MODE_RESULTATS_SUIVANT && inPosition == listeFichesAff.size()+1 ) 
					|| ( mode == MODE_HISTORIQUE && inPosition == listeFichesAff.size() )){
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Insert Groupe");
				tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
				tv_libelle.setText(appContext.getString(R.string.txt_effacerResultats));
				
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				iv_icone.setImageResource(R.drawable.media_eject_200_200);
				
				tv_type = (TextView)view.findViewById(R.id.lienF_typeFiche);
				tv_type.setText("");
			}
			
			// Affichage des vignettes des groupes
			if (mode == MODE_RECHERCHE_GUIDEE && inPosition != groupeAff.groupeListeEnfants.size()) {
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Insert Groupe");
				
				tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
				tv_libelle.setText(groupeAff.groupeListeEnfants.get(inPosition).nom);
				
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				iv_icone.setImageBitmap(Outils.getImage(localContext,groupeAff.groupeListeEnfants.get(inPosition).urlVignette,"",""));
			}
			
			// Affichage de vignette groupe père dans la navigation par Groupes
			if ( mode == MODE_RECHERCHE_GUIDEE && inPosition == groupeAff.groupeListeEnfants.size() ) {
				if (LOG) Log.v(TAG, "ImageAdapter.getView() - Insert Retour Groupe Père");
				
				tv_libelle = (TextView)view.findViewById(R.id.lienF_text);
				
				//Pour le 1er niveau on propose de retourner à l'accueil
				if (groupeAff.profondeur == 0) {
					tv_libelle.setText(appContext.getString(R.string.txt_retourAccueil));
				} else {
					tv_libelle.setText(appContext.getString(R.string.txt_rechercheGroupPere));
				}
				iv_icone = (ImageView)view.findViewById(R.id.lienF_image);
				iv_icone.setImageResource(R.drawable.media_eject_200_200);
				
			}
			if (LOG) Log.d(TAG, "ImageAdapter.getView() - Fin");
			return view;
    	}

		@Override
		public Object getItem(int inPosition) {
			if (LOG) Log.d(TAG, "ImageAdapter.getItem() - Début");
			if (LOG) Log.d(TAG, "ImageAdapter.getItem() - position : "+inPosition);

			if (LOG) Log.d(TAG, "ImageAdapter.getItem() - Fin");
			return inPosition;
		}

		@Override
		public long getItemId(int inPosition) {
			if (LOG) Log.d(TAG, "ImageAdapter.getItemId() - Début");
			if (LOG) Log.d(TAG, "ImageAdapter.getItemId() - position : "+inPosition);
			
			if (LOG) Log.d(TAG, "ImageAdapter.getItemId() - Fin");
			return inPosition;
		}
		

	}
    
    
    
    /* *********************************************************************
     * Menu
     ********************************************************************** */
    public boolean onCreateOptionsMenu(Menu inMenu) {
    	if (LOG) Log.d(TAG, "onCreateOptionsMenu() - Début");
    	
        //Création d'un MenuInflater qui va permettre d'instancier un Menu XML en un objet Menu
        MenuInflater inflater = getMenuInflater();
        //Instanciation du menu XML spécifier en un objet Menu
        inflater.inflate(R.menu.menu, inMenu);
  
        if (LOG) Log.d(TAG, "onCreateOptionsMenu() - Fin");
        return true;
     }
 
    //Méthode qui se déclenchera au clic sur un item du Menu
    public boolean onOptionsItemSelected(MenuItem inItem) {
    	if (LOG) Log.d(TAG, "onOptionsItemSelected() - Début");
    	if (LOG) Log.d(TAG, "onOptionsItemSelected() - item.getItemId() = " + inItem.getItemId());
    	
	     //On regarde quel item a été cliqué grace à son id et on déclenche une action
	     switch (inItem.getItemId()) {
	        case R.id.option:
	        	if (LOG) Log.d(TAG, "onOptionsItemSelected() - R.id.option");
                
	        	Intent explicit = new Intent();
                explicit.setClass(Doris.this, Configuration.class);

                startActivity(explicit);
                
                if (LOG) Log.d(TAG, "onOptionsItemSelected() - R.id.option - Fin");
	            return true;
	            
	        case R.id.reset:    
	        	if (LOG) Log.d(TAG, "onOptionsItemSelected() - R.id.reset");
	        	reset();
	        	if (LOG) Log.d(TAG, "onOptionsItemSelected() - R.id.reset - Fin");
	        	return true;
	        	
	        case R.id.apropos:
	        	if (LOG) Log.d(TAG, "onOptionsItemSelected() - R.id.apropos");
	        	
	            affichageMessageHTML(appContext.getString(R.string.txt_apropos).replace("@version", appVersionName), "file:///android_res/raw/apropos.html");
	        	
	        	if (LOG) Log.d(TAG, "onOptionsItemSelected() - R.id.apropos - Fin");
	            return true;
         }
	     	
	     if (LOG) Log.d(TAG, "onOptionsItemSelected() - Fin");
	     return false;
 	}
    
	/* *********************************************************************
     * Recherche à partir de la saisie
     ********************************************************************** */
    public void lancementRecherche() {
		if (LOG) Log.d(TAG, "lancementRecherche() - Début");
		//Nettoyage de la liste de résultats précédente
		reset();
		tailleListeAvantAjout = 0;
		
		//On récupère ce qui a été entré dans EditText
		txtRecherche = ETRecherche.getText().toString().trim();
		if (LOG) Log.d(TAG, "lancementRecherche() - txtRecherche : "+txtRecherche);
		
		//Un filtre sur le type de fiche a-t-il été paramétré ?
		int filtre_type_fiche = preferences.getInt("filtre_type_fiche", 0);
		if (LOG) Log.v(TAG, "lancementRecherche() - filtre_type_fiche : "+filtre_type_fiche);
		
		//On vérifie qu'il n'y a pas de souci dans la zone de recherche
		if (txtRecherche.equals("")) {
			Toast toast = Toast.makeText(appContext, getString(R.string.txt_aucune_saisie), Toast.LENGTH_LONG);
			toast.show();
		} else {
			switch (filtre_type_fiche) {
			case 1 :
				urlRecherche = racineSite + "/fiches_liste_recherche.asp?nomcommunte=1&allcheck=1&checkvar=1&nomcommun=" + txtRecherche.replaceAll(" ","+");
				break;
			default:
				urlRecherche = racineSite + "/fiches_liste_recherche.asp?nomcommun=" + txtRecherche.replaceAll(" ","+");
			}
			if (LOG) Log.d(TAG, "lancementRecherche() - urlRecherche : "+urlRecherche);
			
			getGrilleResultats(false);
			mode = MODE_RECHERCHE_GUIDEE;
		}
    	
    	if (LOG) Log.d(TAG, "lancementRecherche() - Fin");
	}
    
	/* *********************************************************************
     * fonction permettant la création ou mise à jour de la liste de mots proposables lors de la saisie d'une recherche
     ********************************************************************** */
    public void miseAJourListeNoms() {
		if (LOG) Log.d(TAG, "miseAJourListeNoms() - Début");
		
		listePropositions.clear();
		
		File cacheDir = appContext.getCacheDir();
		
		if (cacheDir.exists()) {
			if (LOG) Log.v(TAG, "miseAJourListeNoms() - cacheDir : " + cacheDir.getName());
			
			File[] files = cacheDir.listFiles();
	        if (files != null) {
	
		        for (File fichier : files) {
		            
		        	if (LOG) Log.v(TAG, "miseAJourListeNoms() - fichier : " + fichier.getName());
		        	
		        	String[] tempFichier = fichier.getName().split("£");
		        	if (LOG) Log.v(TAG, "miseAJourListeNoms() - tempFichier[0] : " + tempFichier[0]);
		        	if (LOG) Log.v(TAG, "miseAJourListeNoms() - tempFichier.length : " + tempFichier.length);

		        	String tempNom = null;
		        	String tempIcone = null;
		        	if (tempFichier[0].equals("010-RechercheParNom") && tempFichier.length > 1){
		        		tempNom = tempFichier[1];
		        		tempIcone = Integer.toString(android.R.drawable.ic_menu_recent_history);
		        	}
		        	if (tempFichier[0].equals("020-Fiche")  && tempFichier.length > 2){
		        		tempNom = tempFichier[2];
		        		tempIcone = Integer.toString(android.R.drawable.ic_menu_search);
		        	}
		        	if (tempFichier[0].equals("030-Image")  && tempFichier.length > 3){
		        		tempNom = tempFichier[3];
		        		tempIcone = Integer.toString(android.R.drawable.ic_menu_search);
		        	}
		        	if ( tempNom != null ){
		        		
		        		if (LOG) Log.v(TAG, "miseAJourListeNoms() - tempNom : " + tempNom);
			        	if (LOG) Log.v(TAG, "miseAJourListeNoms() - tempIcone : " + tempIcone);

			        	tempNom = tempNom.toLowerCase();
			        	if (LOG) Log.v(TAG, "miseAJourListeNoms() - tempNom minuscule : " + tempNom);
			        	
			        	if ( tempNom != "" && !listePropositions.contains(tempNom)) {
				        	HashMap<String, String> hm = new HashMap<String,String>();
				            hm.put("nom",tempNom);
				            hm.put("icone", tempIcone);
		
				            listePropositions.add(hm);
			        	}
		        	}
		        	
		        }	
		        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), listePropositions, R.layout.autocomplete_layout, from, to);
		        
	        }

		}
		
		if (LOG) Log.d(TAG, "miseAJourListeNoms() - Fin");
    }
    
	/* *********************************************************************
     * fonction permettant d'afficher des messages d'avertissement ou l'Apropos
     ********************************************************************** */
	public void affichageMessage(String inPhrase) {
		if (LOG) Log.d(TAG, "affichageMessage() - Début");
		
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

    	LayoutInflater inflater = (LayoutInflater) appContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.message,
    	                               (ViewGroup) findViewById(R.id.layout_root));

    	TextView text = (TextView) layout.findViewById(R.id.text);
    	text.setText(inPhrase);
    	ImageView image = (ImageView) layout.findViewById(R.id.image);
    	image.setImageResource(R.drawable.doris);

    	alertDialog.setTitle(R.string.app_name);
    	alertDialog.setView(layout);
    	alertDialog.show();
    	
    	if (LOG) Log.d(TAG, "affichageMessage() - Fin");
	}

	/* *********************************************************************
     * fonction permettant d'afficher des messages de l'Apropos
     ********************************************************************** */
	public void affichageMessageHTML(String inTitre, String inURL) {
		if (LOG) Log.d(TAG, "affichageMessageHTML() - Début");
		if (LOG) Log.d(TAG, "affichageMessageHTML() - inTitre : " + inTitre);
		if (LOG) Log.d(TAG, "affichageMessageHTML() - inURL : " + inURL);
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

    	LayoutInflater inflater = (LayoutInflater) appContext.getSystemService(LAYOUT_INFLATER_SERVICE);
    	View layout = inflater.inflate(R.layout.message_html,
    	                               (ViewGroup) findViewById(R.id.layout_root));

    	TextView text = (TextView) layout.findViewById(R.id.text);
    	text.setText(inTitre);
    	
    	WebView pageWeb = (WebView) layout.findViewById(R.id.webView);
    	pageWeb.setWebViewClient(new WebViewClient() {  
    	    @Override  
    	    public boolean shouldOverrideUrlLoading(WebView inView, String inUrl)  
    	    {  
    	    	
    	    	if (inUrl.startsWith("http")){
	    	    	if (LOG) Log.d(TAG, "affichageMessageHTML() - Lancement navigateur Android Défaut");
	        		
	    	    	Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(inUrl));
					startActivity(intent);
	
					return true;
    	    	} else {
    	    		return true;
    	    	}
    	    }  
    	});  
    	
    	pageWeb.loadUrl(inURL);

    	alertDialog.setView(layout);
    	alertDialog.show();
    	
    	if (LOG) Log.d(TAG, "affichageMessageHTML() - Fin");
	}
	
	/* *********************************************************************
     * Affiche page des résulats
     ********************************************************************** */	
	
	public void getGrilleResultats(boolean inSuivant){
		if (LOG) Log.d(TAG, "getGrilleResultats() - Début");
		
		final boolean suivant = inSuivant;
		
        if (!Outils.isOnline(appContext)){
        	affichageMessage(getString(R.string.txt_pas_internet));
        } else {
        	if (LOG) Log.v(TAG, "getGrilleResultats() - Accès Internet OK");
        	
			//Pour Faire patienter
			dialogPatience = ProgressDialog.show(Doris.this, "", getString(R.string.txt_patienceChargement), true);
	    	handler = new Handler(){
	    		public void handleMessage(Message msg) {
	    			if (LOG) Log.d(TAG, "getGrilleResultats() - handleMessage() - msg.what : " + msg.what);
	    			
	    			switch(msg.what) {
					case 0:
						if (LOG) Log.v(TAG, "getGrilleResultats() - handleMessage() - 0");
						dialogPatience.cancel();
						
						setTitle(txtRecherche+" : "+nbResultats+getString(R.string.txt_resultatsTitre_fin));
			            GVResultat.setAdapter(new ImageAdapter(Doris.this, "Resultats"));
			            
			            GVResultat.setLongClickable(true);
			            
			    		// Cacher le clavier si résultat
			    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    		imm.hideSoftInputFromWindow(ETRecherche.getWindowToken(), 0);

			    		if (LOG) Log.v(TAG, "getGrilleResultats() - handleMessage() - tailleListeAvantAjout : " + tailleListeAvantAjout);
			    		GVResultat.setSelection(tailleListeAvantAjout);
			    		
						break;
					case 1:
						if (LOG) Log.v(TAG, "getGrilleResultats() - handleMessage() - 1");
						dialogPatience.setMessage(getString(R.string.txt_patienceTelechargement));
						break;
					case 2:
						if (LOG) Log.v(TAG, "getGrilleResultats() - handleMessage() - 2");
						dialogPatience.setMessage(getString(R.string.txt_patienceTraitement));
						break;
					case 3:
						if (LOG) Log.v(TAG, "getGrilleResultats() - handleMessage() - 3");
						dialogPatience.setMessage(getString(R.string.txt_patienceRecupEntete));
						break;
	    			}
	    		}
	    	};
	    	
	    	thread = new Thread(){
	    		public void run(){
			
					try {
						if (LOG) Log.d(TAG, "getGrilleResultats() - Thread().run() - Début");
						
						handler.sendMessage(handler.obtainMessage(1,null));
						//on récupère le code HTML associé à l'URL que l'on a indiqué dans l'EditText
						if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - rechercheParNom.getPage");
						rechercheParNom.getPage(appContext, urlRecherche, txtRecherche, suivant, numPageSuite); 
							
						handler.sendMessage(handler.obtainMessage(2,null));
						if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - rechercheParNom.pageNettoyee");
						rechercheParNom.pageNettoyee = Outils.ciblePage(rechercheParNom.pageRecup, "RESULTATS");
						
			    		// Affichage des Résultats
						if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - nbResultats");
						nbResultats = rechercheParNom.getNbResultats(rechercheParNom.pageNettoyee);
						if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - nbResultats = " + nbResultats.toString());
						
			    		if (nbResultats != 0) {
			    			if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - handler.sendMessage 3");
			    			handler.sendMessage(handler.obtainMessage(3,null));
				    		
			    			// Existe-il une page suivante
			    			if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - pageSuite");
			    			pageSuite = rechercheParNom.getPageSuite(rechercheParNom.pageNettoyee);
			    			if (pageSuite) numPageSuite++;
			    			
			    			// Création de chacunes des Fiches de la Page courante
			    			if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - listeFichesAff");
			    			listeFichesAff = rechercheParNom.getFiches(rechercheParNom.pageNettoyee);
			    			
			    			if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - listeFiches.size() : " + listeFichesAff.size());
				    		if (LOG) Log.v(TAG, "getGrilleResultats() - Thread().run() - listeFiches : " + listeFichesAff.toString());
				            
			    		}
			    		handler.sendMessage(handler.obtainMessage(0,null));
			    		
			    	} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	    		}
	    	};
	    	thread.start();
	    	
        }
        if (LOG) Log.d(TAG, "getGrilleResultats() - Fin");
	}

	
	/* *********************************************************************
     * Aide à la recherche par navigation
     ********************************************************************** */	
	public void getGrilleRecherche(int inGroupe, int inSousGroupe){
		if (LOG) Log.d(TAG, "getGrilleRecherche() - Début");
				
        if (!Outils.isOnline(appContext)){
        	affichageMessage(getString(R.string.txt_pas_internet));
        } else {
			
			//Pour Faire patienter
			dialogPatience = ProgressDialog.show(Doris.this, "", getString(R.string.txt_patienceChargement), true);
	    	handler = new Handler(){
	    		public void handleMessage(Message msg) {
	    			if (LOG) Log.v(TAG, "getGrilleRecherche() - handleMessage(msg) : "+msg.what);
	    			switch(msg.what) {
					case 0:
						if (LOG) Log.v(TAG, "getGrilleRecherche() - handleMessage() - 0");
						dialogPatience.cancel();
						
						// On remet le Titre de la Fenêtre à DORIS
						setTitle(getString(R.string.app_name));
						
			            GVResultat.setAdapter(new ImageAdapter(Doris.this, "Recherche_Guidee"));
			            GVResultat.setLongClickable(true);
			            
			    		// Cacher le clavier si résultat
			    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			    		imm.hideSoftInputFromWindow(ETRecherche.getWindowToken(), 0);

						break;
					case 1:
						if (LOG) Log.v(TAG, "getGrilleRecherche() - handleMessage() - 1");
						dialogPatience.setMessage(getString(R.string.txt_patienceTelechargement));
						break;
					case 2:
						if (LOG) Log.v(TAG, "getGrilleRecherche() - handleMessage() - 2");
						dialogPatience.setMessage(getString(R.string.txt_patienceTraitement));
						break;
					case 3:
						if (LOG) Log.v(TAG, "getGrilleRecherche() - handleMessage() - 3");
						dialogPatience.setMessage(getString(R.string.txt_patienceRecupGroupe));
						break;
	    			}
	    		}
	    	};
	    	
	    	thread = new Thread(){
	    		public void run(){
					if (LOG) Log.d(TAG, "getGrilleRecherche() - Thread().run()");			
					try {
						//Si la recherche guidée existe déjà, pas besoin de la recréée
						if (LOG) Log.d(TAG, "getGrilleRecherche() - rechercheGuidee.filtre_zone_geo : "+rechercheGuidee.filtre_zone_geo);
						if ( rechercheGuidee.filtre_zone_geo == -1 || rechercheGuidee.filtre_zone_geo != rechercheGuidee.get_filtre_zone_geo(preferences)) {
							
							handler.sendMessage(handler.obtainMessage(1,null));
							
							rechercheGuidee.getPageGrandsGroupes(appContext, preferences);  

		        			//String codeHtml = Outils.getHtml(appContext, url, "recherche-racine");
		
							handler.sendMessage(handler.obtainMessage(2,null));
							
							rechercheGuidee.pageNettoyee = Outils.ciblePage(rechercheGuidee.pageRecup, "RECHERCHE");
							
							handler.sendMessage(handler.obtainMessage(3,null));
							
							rechercheGuidee.getGroupes(appContext);  
						}		
			    		handler.sendMessage(handler.obtainMessage(0,null));
			    		
			    		groupeAff = rechercheGuidee.groupe;
						
			    	} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
	    		}
	    	};
	    	thread.start();
	    	
        }
        if (LOG) Log.d(TAG, "getGrilleRecherche() - Fin");
	}	
	
	
	
	/* *********************************************************************
     * Affiche page des résulats
     ********************************************************************** */	
	
	public void getGrilleHistorique(){
		if (LOG) Log.d(TAG, "getGrilleHistorique() - Début");
				     	
		//Pour Faire patienter
		dialogPatience = ProgressDialog.show(Doris.this, "",getString(R.string.txt_patienceChargement), true);
    	handler = new Handler(){
    		public void handleMessage(Message msg) {
    			if (LOG) Log.d(TAG, "getGrilleHistorique() - handleMessage() - msg.what : " + msg.what);
    			
    			switch(msg.what) {
				case 0:
					if (LOG) Log.v(TAG, "getGrilleHistorique() - handleMessage() - 0");
					dialogPatience.cancel();
					
					setTitle(getString(R.string.txt_historiqueTitre_debut)+nbResultats+getString(R.string.txt_resultatsTitre_fin));
		            GVResultat.setAdapter(new ImageAdapter(Doris.this, "Historique"));
		            
		            GVResultat.setLongClickable(true);
		            
		    		// Cacher le clavier 
		    		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		    		imm.hideSoftInputFromWindow(ETRecherche.getWindowToken(), 0);

		    		if (LOG) Log.v(TAG, "getGrilleHistorique() - handleMessage() - tailleListeAvantAjout : " + tailleListeAvantAjout);
		    		GVResultat.setSelection(tailleListeAvantAjout);
		    		
					break;
				case 1:
					if (LOG) Log.v(TAG, "getGrilleHistorique() - handleMessage() - 1");
					dialogPatience.setMessage(getString(R.string.txt_patienceTraitement));
					break;
				case 2:
					if (LOG) Log.v(TAG, "getGrilleHistorique() - handleMessage() - 2");
					dialogPatience.setMessage(getString(R.string.txt_patienceRecupEntete));
					break;
    			}
    		}
    	};
    	
    	thread = new Thread(){
    		public void run(){
		
				try {
					if (LOG) Log.d(TAG, "getGrilleHistorique() - Thread().run() - Début");
					
					handler.sendMessage(handler.obtainMessage(1,null));					
					
		    		// Calcul du Nombre de Fiches disponibles dans le cache
					if (LOG) Log.v(TAG, "getGrilleHistorique() - Thread().run() - nbResultats");
					nbResultats = rechercheDansLeCache.getNbResultats();
					if (LOG) Log.v(TAG, "getGrilleHistorique() - Thread().run() - nbResultats = " + nbResultats.toString());
					
					// Affichage Résultats
		    		if (nbResultats != 0) {
		    			if (LOG) Log.v(TAG, "getGrilleHistorique() - Thread().run() - handler.sendMessage 3");
		    			handler.sendMessage(handler.obtainMessage(2,null));
		    			
		    			// Création de chacunes des Fiches
		    			if (LOG) Log.v(TAG, "getGrilleHistorique() - Thread().run() - listeFichesAff");
		    			listeFichesAff = rechercheDansLeCache.getFiches();
		    			
		    			if (LOG) Log.v(TAG, "getGrilleHistorique() - Thread().run() - listeFichesAff.size() : " + listeFichesAff.size());
			    		if (LOG) Log.v(TAG, "getGrilleHistorique() - Thread().run() - listeFichesAff : " + listeFichesAff.toString());
			            
		    		}
		    		handler.sendMessage(handler.obtainMessage(0,null));
		    		
		    	} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	};
    	thread.start();

        if (LOG) Log.d(TAG, "getGrilleHistorique() - Fin");
	}
	
	
	/* *********************************************************************
     * Vider la Grille de l'interface principale
     ********************************************************************** */	
	private void reset(){
		if (LOG) Log.d(TAG, "reset() - Début");
		
		mode = MODE_INIT;
		numPageSuite = 0;
    	listeFichesAff.clear();
		groupeAff = new Groupe();
		
		// On remet le Titre de la Fenêtre à DORIS
		setTitle(getString(R.string.app_name));

		GVResultat.setAdapter(new ImageAdapter(Doris.this));
		
		if (LOG) Log.d(TAG, "reset() - Fin");
	}

	
	/* *********************************************************************
     * A la fermeture de l 'application
     ********************************************************************** */	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if (LOG) Log.d(TAG, "onDestroy() - Début");
		//listeFichesAff.clear();
		//groupeAff = new Groupe();
		if (LOG) Log.d(TAG, "onDestroy() - mode : "+mode);
		if (LOG) Log.d(TAG, "onDestroy() - groupeAff : "+groupeAff.groupeListeEnfants.size());
		
		if (isFinishing()){
			mode = MODE_INIT;
		    pageSuite = false;
		    numPageSuite = 0;
		    listeFichesAff.clear();
			groupeAff = new Groupe();
		}
		
		preferences = appContext.getSharedPreferences(PREFS_NAME, 0);
        int type_cache = preferences.getInt("type_cache", 2);
        
        int nbFichiersEffaces = 0;
        switch(type_cache)
        {
            case 1:
            	nbFichiersEffaces = Outils.clearFolder(appContext.getCacheDir(), 1);
            break;
            case 2:
            	nbFichiersEffaces = Outils.clearFolder(appContext.getCacheDir(), 7);
            break;
            case 3:
            	nbFichiersEffaces = Outils.clearFolder(appContext.getCacheDir(), 30);
            break;
        }
        if (LOG) Log.d(TAG, "nbFichiersEffaces : " + nbFichiersEffaces);
        if (LOG) Log.d(TAG, "onDestroy() - Fin");
	}
	
	/* *********************************************************************
     * Capture des évènements sur le Clavier Physique de l'appareil
     ********************************************************************** */
	@Override
    public boolean onKeyDown(int inKeyCode, KeyEvent inEvent)
    {
		if (LOG) Log.d(TAG, "onKeyDown() - Début");     
		if (LOG) Log.d(TAG, "onKeyDown() - inKeyCode : " + inKeyCode);
		if (LOG) Log.d(TAG, "onKeyDown() - inEvent : " + inEvent);
		switch(inKeyCode){
		
		case KeyEvent.KEYCODE_BACK :
			if (LOG) Log.d(TAG, "onKeyDown() - mode : " + mode);
      	 
			switch(mode) {
			case MODE_INIT :
				if (LOG) Log.v(TAG, "onKeyDown() - Demande confirmation Quitter");
				new AlertDialog.Builder(this)
			    	.setTitle(getString(R.string.txt_QuitterTitre))
					.setMessage(getString(R.string.txt_QuitterDemandeConfirmation))
					.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener()
						{
						    public void onClick(DialogInterface dialog, int which)
						    {
						    	if (LOG) Log.v(TAG, "onKeyDown() - System.exit(0)");
						    	System.exit(0);
						    }
						})
					.setNegativeButton(android.R.string.cancel,
					        new DialogInterface.OnClickListener()
					{
					    public void onClick(DialogInterface dialog, int which)
					    {
					  	 // AlertDialog.cancel();
					    }
					})
					.create()
					.show();
				if (LOG) Log.d(TAG, "onKeyDown() - Fin");
				return true; 
				
			case MODE_RESULTATS :
			case MODE_RESULTATS_SUIVANT :
			case MODE_HISTORIQUE :
				reset();
				if (LOG) Log.d(TAG, "onKeyDown() - Fin");
				return true; 
				
			case MODE_RECHERCHE_GUIDEE :
				//Retour au groupe Parent
        		if (LOG) Log.v(TAG, "onKeyDown() - groupeAff.profondeur : " + groupeAff.profondeur);
        		if (groupeAff.profondeur == 0){
        			reset();
        		} else {
        			if (LOG) Log.v(TAG, "onKeyDown() - Retour au groupe Parent : " + groupeAff.parent);
        			groupeAff = groupeAff.parent;
        			GVResultat.setAdapter(new ImageAdapter(Doris.this, "Recherche_Guidee"));
        		}
        		if (LOG) Log.d(TAG, "onKeyDown() - Fin");
				return true; 
			}
		
		}
		if (LOG) Log.d(TAG, "onKeyDown() - Fin");
		return false;
    }
	
}