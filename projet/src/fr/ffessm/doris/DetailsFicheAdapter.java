/* *********************************************************************
 * Licence CeCILL-B
 * *********************************************************************
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

import fr.ffessm.doris.Fiche.Detail;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailsFicheAdapter extends BaseAdapter {

	private static String TAG = "DetailsFicheAdapter";
	private static boolean LOG = false;
			
	private Context appContext;
	
	private Fiche mFiche;
	private List<Detail> mListDetails;

	private LayoutInflater mInflater;
	
	private TextView tv_Titre;
	private TextView tv_Contenu;
	
	private String typeLayout;
	
	private LinearLayout layoutItem;
	
    public DetailsFicheAdapter(Context inContext, Fiche inFiche) {
    	if (LOG) Log.d(TAG, "DetailsFicheAdapter() - Début");

    	appContext = inContext;
        mInflater = LayoutInflater.from(appContext);
       
        mFiche = inFiche;
    	mListDetails = mFiche.ficheListeDetails;
    	if (LOG) Log.d(TAG, "DetailsFicheAdapter() - inListDetails : " + mListDetails.size());
    	
        if (LOG) Log.d(TAG, "DetailsFicheAdapter() - Fin");
    }
	
    public int getCount() {
    	if (LOG) Log.d(TAG, "getCount() - Début");
    	if (LOG) Log.d(TAG, "getCount() - inListDetails : " + mListDetails.size());
    	
    	int nbDetails = mListDetails.size();
    	
    	if (LOG) Log.d(TAG, "getCount() - mFiche.ficheListeLiensTexte.size() : " + mFiche.ficheListeLiensTexte.size());
    	nbDetails = nbDetails + mFiche.ficheListeLiensTexte.size();
    	
    	if (LOG) Log.d(TAG, "getCount() - nbDetails() : " + nbDetails);
    	if (LOG) Log.d(TAG, "getCount() - Fin");
        return nbDetails;
    }

    public Object getItem(int position) {
    	if (LOG) Log.d(TAG, "getItem() - Début");
    	if (LOG) Log.d(TAG, "getItem() - position : " + position);
    	if (LOG) Log.d(TAG, "getItem() - Fin");
		return position;
    }

    public long getItemId(int position) {
    	if (LOG) Log.d(TAG, "getItemId() - Début");
    	if (LOG) Log.d(TAG, "getItemId() - position : " + position);
    	
    	if (position >= mListDetails.size()) {
    		if (LOG) Log.d(TAG, "getItemId() - url : " + mFiche.ficheListeLiensUrl.get(position - mListDetails.size()).toString());

    		afficheLien(appContext, position);
    	}
    	
    	if (LOG) Log.d(TAG, "getItemId() - Fin");
        return position;
    }

	public void afficheLien(Context lContext, int position) {
    	if (LOG) Log.d(TAG, "afficheLien() - Début");
    	if (LOG) Log.d(TAG, "afficheLien() - position : "+position);

        if (LOG) Log.d(TAG, "afficheLien() - Fin");
	}

	public View getView(int position, View convertView, ViewGroup parent) {
    	if (LOG) Log.d(TAG, "getView() - Début");
    	if (LOG) Log.d(TAG, "getView() - position : " + position);
    	
    	if (position < mListDetails.size()) {
    		typeLayout = "detail";
    	} else {
    		typeLayout = "listeFichesLiees";
    	}
    	if (LOG) Log.v(TAG, "getView() - typeLayout : "+typeLayout);

    	if (convertView == null) {
    		if (LOG) Log.v(TAG, "getView() - convertView == null");
    		layoutItem = (LinearLayout) mInflater.inflate(R.layout.detail_fiche, parent, false);

    	} else {
        	layoutItem = (LinearLayout) convertView;
		}

		tv_Titre = (TextView)layoutItem.findViewById(R.id.TV_Titre);
        tv_Contenu = (TextView)layoutItem.findViewById(R.id.TV_Contenu);
        
        if (typeLayout.equals("detail")) {
        	if (LOG) Log.v(TAG, "getView() - Titre : "+mListDetails.get(position).titre);
	        tv_Titre.setText(mListDetails.get(position).titre);
	        tv_Titre.setTag(position);
	        tv_Titre.setVisibility(View.VISIBLE);
	        
	        if (LOG) Log.v(TAG, "getView() - Contenu : "+mListDetails.get(position).contenu);
	        tv_Contenu.setText(mListDetails.get(position).contenu);
	        tv_Contenu.setTextSize(15);
	        
	        if (LOG) Log.v(TAG, "getView() - Visibility : "+mListDetails.get(position).affiche);
	        tv_Contenu.setVisibility(mListDetails.get(position).affiche ? View.VISIBLE : View.GONE);
	        
	        tv_Contenu.setTag(position);
        }
        
        if (typeLayout.equals("listeFichesLiees")) {
        	if (position == mListDetails.size()) {
        		tv_Titre.setText(appContext.getString(R.string.txt_ficheDetailFichesLiees));
        		tv_Titre.setVisibility(View.VISIBLE);
        	} else {
        		tv_Titre.setText("Devrait être caché");
        		tv_Titre.setVisibility(View.GONE);
        	}
        	tv_Titre.setTag(position);
        	
        	if (LOG) Log.v(TAG, "getView() - Contenu : "+mFiche.ficheListeLiensTexte.get(position - mListDetails.size()).toString());
        	tv_Contenu.setText(mFiche.ficheListeLiensTexte.get(position - mListDetails.size()).toString());
        	tv_Contenu.setTextSize(20);
        	
        	if (LOG) Log.v(TAG, "getView() - Visibility : "+mFiche.ficheListeLiensAffiche);
        	tv_Contenu.setVisibility(mFiche.ficheListeLiensAffiche ? View.VISIBLE : View.GONE);
        	tv_Contenu.setTag(position);
        }


        tv_Titre.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v_clic) {
				if (LOG) Log.d(TAG, "tv_Titre.onClick() - Début");
				
				Integer position_clic = (Integer)v_clic.getTag();
				if (LOG) Log.d(TAG, "tv_Titre.onClick() - position : "+position_clic);
				
		    	if (position_clic < mListDetails.size()) {
		    		typeLayout = "detail";
		    	} else {
		    		typeLayout = "listeFichesLiees";
		    	}
				if (LOG) Log.v(TAG, "tv_Titre.onClick() - typeLayout : "+typeLayout);
				
				if (typeLayout.equals("detail")) {
					mListDetails.get(position_clic).affiche = mListDetails.get(position_clic).affiche ? false : true;
				}
				if (typeLayout.equals("listeFichesLiees")) {
					mFiche.ficheListeLiensAffiche = mFiche.ficheListeLiensAffiche ? false : true;
				}
				notifyDataSetChanged();
				if (LOG) Log.d(TAG, "tv_Titre.onClick() - Fin");
			}
        	
        });

        tv_Contenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View inClic) {
				if (LOG) Log.d(TAG, "tv_Contenu.onClick() - Début");
				
				Integer position_clic = (Integer)inClic.getTag();
				if (LOG) Log.d(TAG, "tv_Contenu.onClick() - position : "+position_clic);
				
		    	if (position_clic < mListDetails.size()) {
		    		typeLayout = "detail";
		    	} else {
		    		typeLayout = "listeFichesLiees";
		    	}
				if (LOG) Log.v(TAG, "tv_Contenu.onClick() - typeLayout : "+typeLayout);
				
				if (typeLayout.equals("detail")) {
					
				}
				if (typeLayout.equals("listeFichesLiees")) {
					//Affichage Fiche du Lien Sélectionné
			        String refFicheAff = mFiche.ficheListeLiensUrl.get(position_clic - mListDetails.size()).toString();
			        if (LOG) Log.v(TAG, "tv_Contenu.onClick() - ref : "+refFicheAff);

			        //Si la fiche n'existe pas on commence par la créer
			        if (!Doris.listeFiches.containsKey(refFicheAff)){
			        	if (LOG) Log.v(TAG, "tv_Contenu.onClick() - Création Fiche : "+refFicheAff);
			        	Fiche ficheLien =  new Fiche(appContext, refFicheAff);
			        		
			        	Doris.listeFiches.put(refFicheAff, ficheLien);	
			        }
			        
			        //on affiche
			        if (LOG) Log.v(TAG, "tv_Contenu.onClick() - Affichage Fiche : "+refFicheAff);
			    	Intent explicit = new Intent();
			        explicit.setClass(appContext, AffFiche.class);
			        explicit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        explicit.putExtra("ref", refFicheAff);

			        appContext.startActivity(explicit);

				}
				notifyDataSetChanged();
				if (LOG) Log.d(TAG, "tv_Contenu.onClick() - Fin");
			}
        	
        });
        
        if (LOG) Log.d(TAG, "getView() - Fin");
        return layoutItem;
    }



    private ArrayList<FicheAdapterListener> mListListener = new ArrayList<FicheAdapterListener>();
    
    public void addListener(FicheAdapterListener inListener) {
    	if (LOG) Log.d(TAG, "addListener() - Début");
    	if (LOG) Log.d(TAG, "addListener() - lListener : " + inListener.toString());
    	mListListener.add(inListener);
    	if (LOG) Log.d(TAG, "addListener() - Fin");
    }
    
    private void sendListener(Detail inItem, int inPosition) {
    	if (LOG) Log.d(TAG, "sendListener() - Début");

    	if (LOG) Log.d(TAG, "sendListener() - Fin");
    }

	public interface FicheAdapterListener {

    }


}
