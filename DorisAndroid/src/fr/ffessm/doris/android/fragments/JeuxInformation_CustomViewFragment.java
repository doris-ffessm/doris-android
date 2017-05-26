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


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import fr.ffessm.doris.android.DorisApplicationContext;
import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.datamodel.Fiche;

public class JeuxInformation_CustomViewFragment extends Fragment
{

	private static final String LOG_TAG = JeuxInformation_CustomViewFragment.class.getCanonicalName();

    private ImageView jeu_icone;
    private TextView jeu_titre;
    private TextView jeu_soustitre;
    private TextView jeu_description;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView() - Début");

        View view = inflater.inflate(R.layout.jeux_customview_fragment, container, false);

        jeu_icone = (ImageView) view.findViewById(R.id.jeux_customviewfragment_icon);
        jeu_titre = (TextView) view.findViewById(R.id.jeux_customviewfragment_titre);
        jeu_soustitre = (TextView) view.findViewById(R.id.jeux_customviewfragment_soustitre);
        jeu_description = (TextView) view.findViewById(R.id.jeux_textefragment_description);

        if (savedInstanceState != null) {

        } else {
            Log.d(LOG_TAG, "onCreateView() - savedInstanceState == null");
        }

        Log.d(LOG_TAG, "onCreateView() - containerId : "+this.getId());
        Log.d(LOG_TAG, "onCreateView() - containerTag : "+this.getTag());
        Log.d(LOG_TAG, "onCreateView() - jeuEncours : "+DorisApplicationContext.getInstance().jeuStatut);

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
        Log.d(LOG_TAG, "onStart() - jeuEncours : "+DorisApplicationContext.getInstance().jeuStatut);

        updateTitre(DorisApplicationContext.getInstance().jeuStatut.toString());

        Log.d(LOG_TAG, "onStart() - Fin");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState() - Début");
        super.onSaveInstanceState(outState);

        //outState.putInt(ARG_PETRI_ETAT, 0);

        Log.d(LOG_TAG, "onSaveInstanceState() - Fin");
    }


    public void updateTitre(String titre) {
        TextView titreTV = (TextView) getActivity().findViewById(R.id.jeux_customviewfragment_titre);
        titreTV.setText(titre);
    }

    public void updateIcone(Fiche fiche) {
        ImageView ivIcone = (ImageView) getActivity().findViewById(R.id.jeux_customviewfragment_icon);
        //ivIcone.setImageResource();
    }
}
