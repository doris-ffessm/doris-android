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


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import fr.ffessm.doris.android.R;
import fr.ffessm.doris.android.activities.Jeux_CustomViewActivity;

public class Jeux_CustomViewFragmentCallBack extends ListFragment
{

	private static final String LOG_TAG = Jeux_CustomViewFragmentCallBack.class.getCanonicalName();
    final static String ARG_ETAT_ENCOURS = "etatEncours";

    OnHeadlineSelectedListener mCallback;
    TypeCustomViewFragmentListe typeCustomViewFragment;

    public interface OnHeadlineSelectedListener {
        public void onCustomViewFragmentSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(LOG_TAG, "onAttach() - Début");
        super.onAttach(activity);

        try {
            mCallback = (OnHeadlineSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
        Log.d(LOG_TAG, "onAttach() - Fin");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate() - Début");
        super.onCreate(savedInstanceState);

        // Initialisation du Fragment
        //setListAdapter(new ArrayAdapter<String>(getActivity(), layout, Ipsum.Headlines));
        Log.d(LOG_TAG, "onCreate() - Fin");
     }

    @Override
    public void onStart() {
        Log.d(LOG_TAG, "onStart() - Début");
        super.onStart();

        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)
        if (getFragmentManager().findFragmentById(R.id.jeux_activity_fragment_principal) != null) {
            //Log.d(LOG_TAG, "onStart() - : "+.getInt(Jeux_CustomViewActivity.ARG_PETRI_ETAT));
            //getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        } else {
            Log.d(LOG_TAG, "onStart() - getFragmentManager() == null");
        }
        Log.d(LOG_TAG, "onStart() - Fin");
    }


    public enum TypeCustomViewFragmentListe {
        ACCUEIL, SCORES, QUESTION
    }


    public void updateTitre(String titre) {
        TextView titreTV = (TextView) getActivity().findViewById(R.id.jeux_customviewfragment_titre);
        titreTV.setText(titre);
    }
}
