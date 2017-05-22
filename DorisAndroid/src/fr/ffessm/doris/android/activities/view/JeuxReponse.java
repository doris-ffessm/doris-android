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
package fr.ffessm.doris.android.activities.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.CloseableIterator;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.Accueil_CustomViewActivity;
import fr.ffessm.doris.android.activities.DetailsParticipant_ElementViewActivity;
import fr.ffessm.doris.android.activities.Preference_PreferenceViewActivity;
import fr.ffessm.doris.android.datamodel.DorisDB_metadata;
import fr.ffessm.doris.android.datamodel.OrmLiteDBHelper;
import fr.ffessm.doris.android.datamodel.Participant;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.App_Outils;
import fr.ffessm.doris.android.tools.Disque_Outils;
import fr.ffessm.doris.android.tools.Param_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.ScreenTools;

public class JeuxReponse {

	private Context context;
	private Activity activity;

	private static final String LOG_TAG = Accueil_CustomViewActivity.class.getCanonicalName();

	private final Param_Outils paramOutils;
	private final Disque_Outils disqueOutils;
	private final Photos_Outils photosOutils;

    private int id;
    private String valeur;
    private String libelle;
    private String icone;

	public JeuxReponse(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;

		paramOutils = new Param_Outils(context);
		disqueOutils = new Disque_Outils(context);
		photosOutils = new Photos_Outils(context);
	}

	public JeuxReponse(Context context) {
        this.context = context;

        paramOutils = new Param_Outils(context);
        disqueOutils = new Disque_Outils(context);
        photosOutils = new Photos_Outils(context);
	}

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
       return id;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }
    public String getValeur() {
        return valeur;
    }

     /* Création de la Réponse (Textes, Icônes et Boutons */
    /*
    protected View createNavigationZoneView(final ZoneGeographique zone){
        final Context context = this;

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewZone = inflater.inflate(R.layout.jeux_listviewrow, null);

        // Nom et Description de la Zone
        TextView tvLabel = (TextView) viewZone.findViewById(R.id.zonegeoselection_listviewrow_label);
        tvLabel.setText(zone.getNom());

        if(ScreenTools.getScreenWidth(context) > 500){ // TODO devra probablement être adapté lorsque l'on aura des fragments
            TextView tvLDetails = (TextView) viewZone.findViewById(R.id.zonegeoselection_listviewrow_details);
            tvLDetails.setVisibility(View.VISIBLE);
            tvLDetails.setText(zone.getDescription());
        } else {
            viewZone.findViewById(R.id.zonegeoselection_listviewrow_details).setVisibility(View.GONE);
        }

        // Icône illustrant la Zone
        int imageZone = getFichesOutils().getZoneIconeId(zone.getZoneGeoKind());

        ImageView ivIcone = (ImageView)viewZone.findViewById(R.id.zonegeoselection_listviewrow_icon);
        ivIcone.setImageResource(imageZone);
        int iconeZine = Integer.valueOf(getParamOutils().getParamString(R.string.pref_key_accueil_icon_size, "64"));
        ivIcone.setMaxHeight(iconeZine);
        ivIcone.setMaxWidth(iconeZine);


        // Quelle est l'action principale : par défaut ouverture de la liste des fiches de la Zone
        // sinon ouverture de l'arbre phylogénétique
        final String accueil_liste_ou_arbre_pardefaut = getParamOutils().getParamString(R.string.pref_key_accueil_liste_ou_arbre_pardefaut, "liste");
        //Log.d(LOG_TAG, "accueil_liste_ou_arbre_pardefaut : "+accueil_liste_ou_arbre_pardefaut);
*/

/*
        // Gestion Clic Principal sur la Zone (partout sauf 2 boutons "secondaires" (càd de droite)
        viewZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
                // positionne la recherche pour cette zone
                ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zone.getId());
                // réinitialise le filtre espèce
                ed.putInt(context.getString(R.string.pref_key_filtre_groupe), 1);
                ed.commit();

                if (accueil_liste_ou_arbre_pardefaut.equals("arbre")){
                    // Si choix de l'utilisateur, on accède à l'arbre en cliquant sur la zone

                    //Permet de revenir à l'accueil après recherche par le groupe, si false on irait dans la liste en quittant
                    Intent toGroupeSelectionView = new Intent(context, GroupeSelection_ClassListViewActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean("GroupeSelection_depuisAccueil", true);
                    toGroupeSelectionView.putExtras(b);

                    showToast(getString(R.string.accueil_recherche_guidee_label_text)+" ; "
                            +Constants.getTitreCourtZoneGeographique(zone.getZoneGeoKind()));
                    startActivity(toGroupeSelectionView);

                } else if(accueil_liste_ou_arbre_pardefaut.equals("photos")) {

                    showToast(Constants.getTitreCourtZoneGeographique(zone.getZoneGeoKind()));
                    startActivity(new Intent(context, ListeImageFicheAvecFiltre_ClassListViewActivity.class));


                } else {
                    // Par défaut, on ouvre la liste des fiches en cliquant sur la zone
                    showToast(Constants.getTitreCourtZoneGeographique(zone.getZoneGeoKind()));
                    startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
                }

            }
        });
*/


        // Gestion des boutons "secondaires" (de droite)
        // Si Liste par Défaut : H = Arbre ; B = Photos
        // Si Arbre par Défaut : H = Liste ; B = Photos
        // Si Photos par Défaut : H = Liste ; B = Arbre
/*
        // Image
        ImageButton imgBtnH = (ImageButton) viewZone.findViewById(R.id.zonegeoselection_selectBtn_h);
        ImageButton imgBtnB = (ImageButton) viewZone.findViewById(R.id.zonegeoselection_selectBtn_b);
        if (accueil_liste_ou_arbre_pardefaut.equals("arbre")) {
            imgBtnH.setImageResource(
                    ThemeUtil.attrToResId(((Accueil_CustomViewActivity) context), R.attr.ic_action_liste_fiches) );
            imgBtnB.setImageResource(
                    ThemeUtil.attrToResId(((Accueil_CustomViewActivity) context), R.attr.ic_action_liste_images) );
        } else if(accueil_liste_ou_arbre_pardefaut.equals("photos")) {
            imgBtnH.setImageResource(
                    ThemeUtil.attrToResId(((Accueil_CustomViewActivity) context), R.attr.ic_action_liste_fiches) );
            imgBtnB.setImageResource(
                    ThemeUtil.attrToResId(((Accueil_CustomViewActivity) context), R.attr.ic_action_arbre_phylogenetique) );
        } else {
            imgBtnH.setImageResource(
                    ThemeUtil.attrToResId(((Accueil_CustomViewActivity) context), R.attr.ic_action_arbre_phylogenetique) );
            imgBtnB.setImageResource(
                    ThemeUtil.attrToResId(((Accueil_CustomViewActivity) context), R.attr.ic_action_liste_images) );
        }
*/

/*
        // Clic sur Bouton du Haut (si Liste par défaut Alors Bouton Haut => Arbre, sinon Bouton Haut => Liste)
        imgBtnH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
                // positionne la recherche pour cette zone
                ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zone.getId());
                // réinitialise le filtre espèce
                ed.putInt(context.getString(R.string.pref_key_filtre_groupe), 1);
                ed.commit();

                if (accueil_liste_ou_arbre_pardefaut.equals("liste")) {
                    //Permet de revenir à l'accueil après recherche par le groupe, si false on irait dans la liste en quittant
                    Intent toGroupeSelectionView = new Intent(context, GroupeSelection_ClassListViewActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean("GroupeSelection_depuisAccueil", true);
                    toGroupeSelectionView.putExtras(b);

                    showToast(getString(R.string.accueil_recherche_guidee_label_text)+"; "+Constants.getTitreCourtZoneGeographique(zone.getZoneGeoKind()));
                    startActivity(toGroupeSelectionView);
                } else {
                    showToast(Constants.getTitreCourtZoneGeographique(zone.getZoneGeoKind()));
                    startActivity(new Intent(context, ListeFicheAvecFiltre_ClassListViewActivity.class));
                }

            }
        });
*/

/*
        // Clic sur Bouton du Bas (si Photos par défaut Alors Bouton Bas => Arbre, sinon Bouton Bas => Photos)
        imgBtnB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor ed = PreferenceManager.getDefaultSharedPreferences(context).edit();
                // positionne la recherche pour cette zone
                ed.putInt(context.getString(R.string.pref_key_filtre_zonegeo), zone.getId());
                // réinitialise le filtre espèce
                ed.putInt(context.getString(R.string.pref_key_filtre_groupe), 1);
                ed.commit();

                if (accueil_liste_ou_arbre_pardefaut.equals("photos")) {
                    //Permet de revenir à l'accueil après recherche par le groupe, si false on irait dans la liste en quittant
                    Intent toGroupeSelectionView = new Intent(context, GroupeSelection_ClassListViewActivity.class);
                    Bundle b = new Bundle();
                    b.putBoolean("GroupeSelection_depuisAccueil", true);
                    toGroupeSelectionView.putExtras(b);

                    showToast(getString(R.string.accueil_recherche_guidee_label_text)+"; "
                            +Constants.getTitreCourtZoneGeographique(zone.getZoneGeoKind()));
                    startActivity(toGroupeSelectionView);
                } else {
                    showToast(Constants.getTitreCourtZoneGeographique(zone.getZoneGeoKind()));
                    startActivity(new Intent(context, ListeImageFicheAvecFiltre_ClassListViewActivity.class));

                }
            }
        });


        return viewZone;
    }
*/
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
