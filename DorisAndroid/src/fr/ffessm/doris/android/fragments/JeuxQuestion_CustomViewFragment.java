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
package fr.ffessm.doris.android.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import fr.ffessm.doris.android.BuildConfig;
import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.DetailEntreeGlossaire_ElementViewActivity;
import fr.ffessm.doris.android.datamodel.DefinitionGlossaire;
import fr.ffessm.doris.android.sitedoris.Constants;
import fr.ffessm.doris.android.tools.Jeu;
import fr.ffessm.doris.android.tools.Photos_Outils;
import fr.ffessm.doris.android.tools.Photos_Outils.ImageType;
import fr.ffessm.doris.android.tools.Reseau_Outils;

public class JeuxQuestion_CustomViewFragment extends Fragment implements OnItemClickListener
{

	private static final String LOG_TAG = JeuxQuestion_CustomViewFragment.class.getCanonicalName();

    final static String ARG_JEUX = "jeux";

    private ImageView jeu_icone;
    private TextView jeu_titre;
    private TextView jeu_soustitre;
    private TextView jeu_description;

    Photos_Outils photosOutils;
    Reseau_Outils reseauOutils;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView() - Début");

        View view = inflater.inflate(R.layout.jeux_customview_fragment, container, false);

        jeu_icone = (ImageView) view.findViewById(R.id.jeux_customviewfragment_icon);
        jeu_titre = (TextView) view.findViewById(R.id.jeux_customviewfragment_titre);
        jeu_soustitre = (TextView) view.findViewById(R.id.jeux_customviewfragment_soustitre);
        jeu_description = (TextView) view.findViewById(R.id.jeux_textefragment_description);

        reseauOutils = new Reseau_Outils(getActivity());

        if (savedInstanceState != null) {

        } else {
            Log.d(LOG_TAG, "onCreateView() - savedInstanceState == null");
        }

        Log.d(LOG_TAG, "onCreateView() - containerId : "+this.getId());
        Log.d(LOG_TAG, "onCreateView() - containerTag : "+this.getTag());

        Log.d(LOG_TAG, "onCreateView() - Fin");
        return view;
    }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart() - Début");
        super.onStart();

        jeu_titre.setText("Test 010");
        Log.d(LOG_TAG, "onStart() - containerId : "+this.getId());
        Log.d(LOG_TAG, "onStart() - containerTag : "+this.getTag());

        setTitre(DorisApplicationContext.getInstance().jeuStatut.toString());

        Log.d(LOG_TAG, "onStart() - Fin");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState() - Début");
        super.onSaveInstanceState(outState);

        //outState.putInt(ARG_PETRI_ETAT, 0);

        Log.d(LOG_TAG, "onSaveInstanceState() - Fin");
    }


    public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
        Log.d(LOG_TAG, "onItemClick "+view);

    }



    public void setIcone(int idIcone) {
        jeu_icone.setImageResource(idIcone);
    }
    public void setIcone(String imageURL, ImageType imageType) {
        Log.d(LOG_TAG, "setIcone() - Début");
        Log.d(LOG_TAG, "setIcone() - imageURL : "+imageURL);
        Log.d(LOG_TAG, "setIcone() - imageType : "+imageType);

        if (getPhotosOutils().isAvailableInFolderPhoto(imageURL, imageType)) {

            String photoNom = imageURL.substring(imageURL.lastIndexOf('/') + 1);
            if (BuildConfig.DEBUG) Log.i(LOG_TAG, "isAvailableInFolderPhoto() - photoNom : "+ photoNom );

            try {
                Picasso.with(getActivity()).load(getPhotosOutils().getPhotoFile(photoNom, imageType))
                        .fit()
                        .centerInside()
                        .into(jeu_icone);
            } catch (IOException e) {
                Log.d(LOG_TAG, "setIcone() - IOException : "+e);
            }
        } else {
            // pas préchargée en local pour l'instant, cherche sur internet
            Log.d(LOG_TAG, "setIcone() -  pas préchargée en local pour l'instant, cherche sur internet");
            if (reseauOutils.isTelechargementsModeConnectePossible()) {
                String urlPhoto = Constants.IMAGE_BASE_URL + "/" + imageURL;
                Log.d(LOG_TAG, "setIcone() - urlPhoto : "+urlPhoto);
                Picasso.with(getActivity())
                        .load(urlPhoto.replace(" ", "%20"))
                        .placeholder(R.drawable.doris_logo_site_doris)  // utilisation de l'image par defaut pour commencer
                        .error(R.drawable.doris_icone_doris_large_pas_connecte)
                        .fit()
                        .centerInside()
                        .into(jeu_icone);
            } else {
                jeu_icone.setImageResource(R.drawable.doris_icone_doris_large_pas_connecte);
            }
        }
        Log.d(LOG_TAG, "setIcone() - Fin");
    }




    public void setTitre(String titre) {
        jeu_titre.setText(titre);
    }
    public String getTitre() {
        return (String) jeu_titre.getText();
    }
    public void setSousTitre(String soustitre) {
        jeu_soustitre.setText(soustitre);
    }
    public void setDescription(String titre) {
        jeu_description.setText(titre);
    }
    public String getDescription() {
        return (String) jeu_description.getText();
    }

    private Photos_Outils getPhotosOutils(){
        if(photosOutils == null) photosOutils = new Photos_Outils(getActivity());
        return photosOutils;
    }
}
